package com.example.cs360app;

/**
 * Represents a single weight entry within the Weight Tracker app.
 * <p>
 * Each entry contains:
 * <ul>
 *     <li>A unique database ID</li>
 *     <li>A date string (formatted when inserted)</li>
 *     <li>A weight value in pounds</li>
 * </ul>
 *
 * This class acts as a simple data model used by the RecyclerView adapter
 * and database helper.
 */
public class Weight {
    private int id; // key ID for db
    private String date; // date weight was recorded
    private double weight; // recorded weight

    /**
     * Constructs a new Weight object.
     *
     * @param id     the unique database ID for the entry
     * @param date   the formatted date associated with the weight
     * @param weight the weight value recorded by the user
     */
    public Weight(int id, String date, double weight){
        this.id = id;
        this.date = date;
        this.weight = weight;
    }

    /**
     * Returns the date of this weight entry.
     *
     * @return date string (formatted)
     */
    public String getDate(){
        return date;
    }

    /**
     * Returns the weight value for this entry.
     *
     * @return weight in pounds
     */
    public double getWeight(){
        return weight;
    }

    /**
     * Returns the database ID of this entry.
     *
     * @return primary key ID
     */
    public int getId(){
        return id;
    }
}
