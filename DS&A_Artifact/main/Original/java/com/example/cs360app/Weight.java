package com.example.cs360app;

// Simple weight class that will be used to properly store the weight data, and be easily accessible
public class Weight {
    private int id;
    private String date;
    private double weight;

    public Weight(int id, String date, double weight){
        this.id = id;
        this.date = date;
        this.weight = weight;
    }

    public String getDate(){
        return date;
    }

    public double getWeight(){
        return weight;
    }

    public int getId(){
        return id;
    }
}
