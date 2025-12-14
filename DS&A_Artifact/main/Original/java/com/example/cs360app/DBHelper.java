package com.example.cs360app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "weighttracker.db";
    private static final int DATABASE_VERSION = 1;

    // User table
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    // Weight table
    private static final String TABLE_WEIGHTS = "weights";
    private static final String COL_WEIGHT_ID = "id";
    private static final String COL_DATE = "date";
    private static final String COL_WEIGHT = "weight";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHTS);
        onCreate(db);
    }

    // Add new user
    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USERNAME, username);
        cv.put(COL_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, cv);
        return result != -1;
    }

    // Validate login
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // CRUD for weights
    public long addWeight(String date, double weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_DATE, date);
        cv.put(COL_WEIGHT, weight);
        return db.insert(TABLE_WEIGHTS, null, cv);
    }

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

    public void deleteWeight(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WEIGHTS, COL_WEIGHT_ID + "=?", new String[]{String.valueOf(id)});
    }
}