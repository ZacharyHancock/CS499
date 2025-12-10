package com.example.cs360app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * DBHelper handles all database operations for the Weight Tracker app.
 * It manages three tables:
 *   - users: stores account credentials
 *   - weights: stores weight entries with dates
 *   - goals: stores the user's goal weight
 *
 * This class provides CRUD operations for users, weight entries,
 * and goal tracking while managing database creation and upgrades.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "weighttracker.db";
    private static final int DATABASE_VERSION = 2;

    // Users table
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    // Weights table
    private static final String TABLE_WEIGHTS = "weights";
    private static final String COL_WEIGHT_ID = "id";
    private static final String COL_DATE = "date";
    private static final String COL_WEIGHT = "weight";

    // Goals table
    private static final String TABLE_GOALS = "goals";
    private static final String COL_GOAL_ID = "id";
    private static final String COL_GOAL_VALUE = "value";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called only when the database is created for the first time.
     * Creates all required tables for the app.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT UNIQUE, " +
                COL_PASSWORD + " TEXT)");

        // Create weights table
        db.execSQL("CREATE TABLE " + TABLE_WEIGHTS + " (" +
                COL_WEIGHT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATE + " TEXT, " +
                COL_WEIGHT + " REAL)");

        // Creates goals table
        db.execSQL("CREATE TABLE " + TABLE_GOALS + " (" +
                COL_GOAL_ID + " INTEGER PRIMARY KEY , " +
                COL_GOAL_VALUE + " REAL)");
    }

    /**
     * Called when the database version number is increased.
     * Drops old tables and recreates them.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOALS);
        onCreate(db);
    }

    /**
     * Adds a new user to the database.
     *
     * @param username the username to create
     * @param password the password for the account
     * @return true if the insert succeeded, false otherwise
     */
    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USERNAME, username);
        cv.put(COL_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, cv);
        return result != -1;
    }

    /**
     * Validates login by checking if a username/password combo exists.
     *
     * @return true if a matching user is found
     */
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // -------------------------------------------------------------------------
    // Weight Tracking CRUD
    // -------------------------------------------------------------------------

    /**
     * Inserts a new weight entry.
     *
     * @param date   the date (string formatted)
     * @param weight the weight value
     * @return SQLite row ID of the new entry
     */
    public long addWeight(String date, double weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_DATE, date);
        cv.put(COL_WEIGHT, weight);
        return db.insert(TABLE_WEIGHTS, null, cv);
    }

    /**
     * Retrieves all weight entries sorted by most recent date first.
     *
     * @return list of Weight objects
     */
    public List<Weight> getAllWeights() {
        List<Weight> weightList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_WEIGHTS + " ORDER BY " + COL_DATE + " DESC", null);

        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_WEIGHT_ID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE));
                double weight = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_WEIGHT));
                weightList.add(new Weight(id,date, weight));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return weightList;

    }

    /**
     * Deletes a weight entry using its primary key ID.
     */
    public void deleteWeight(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WEIGHTS, COL_WEIGHT_ID + "=?", new String[]{String.valueOf(id)});
    }

    // -------------------------------------------------------------------------
    // Goal Tracking
    // -------------------------------------------------------------------------

    /**
     * Saves the user's goal weight.
     * Uses ID = 1 to ensure only one goal entry exists.
     */
    public void setGoalWeight(double goalWeight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_GOAL_ID, 1);
        cv.put(COL_GOAL_VALUE, goalWeight);

        // Insert or replace to enforce single-row table behavior
        db.insertWithOnConflict(TABLE_GOALS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Retrieves the user's goal weight.
     *
     * @return stored goal weight, or -1 if no goal is set
     */
    public double getGoalWeight() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_GOAL_VALUE +
                " FROM " + TABLE_GOALS +
                " WHERE " + COL_GOAL_ID + "=1", null);

        if (cursor.moveToFirst()) {
            double val = cursor.getDouble(0);
            cursor.close();
            return val;
        }
        cursor.close();
        return -1; // no goal set
    }
}