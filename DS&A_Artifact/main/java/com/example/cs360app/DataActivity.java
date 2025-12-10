package com.example.cs360app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;


/**
 * DataActivity handles all weight-tracking functionality including:
 * - Adding new weight entries
 * - Displaying historical data in a RecyclerView
 * - Plotting weight trends in an MPAndroidChart LineChart
 * - Predicting the user's goal date using linear regression
 *
 * This activity acts as the main visualization and analytics screen for
 * the weight-tracking portion of the app.
 */
public class DataActivity extends AppCompatActivity {

        private DBHelper db;
        private RecyclerView recyclerView;
        private EditText weightInput;
        private EditText editGoal;
        private TextView predictionText;
        private Button addGoal;

        private LineChart lineChart;
        private List<Double> weightData = new ArrayList<>();
        private List<String> dateLabels = new ArrayList<>();

        private SimpleDateFormat dateFormat;
        private WeightAdapter adapter;

    /**
     * Initializes UI components, RecyclerView adapter, Chart, and database.
     * Sets listeners for adding weights, setting a goal, and navigating to settings.
     *
     * @param savedInstanceState previous activity state bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        // Initialize the DB
        db = new DBHelper(this);

        // Setup weight list and line chart
        recyclerView = findViewById(R.id.dataRecyclerView);
        lineChart = findViewById(R.id.lineChart);

        // setup text and buttons
        Button addWeightButton = findViewById(R.id.addEntryButton);
        Button settingsButton = findViewById(R.id.settingsButton);
        EditText editGoal = findViewById(R.id.editGoal);
        predictionText = findViewById(R.id.textGoalPrediction);
        addGoal = findViewById(R.id.addGoalButton);
        weightInput = findViewById(R.id.weightEntry);

        // call functions to setup chart and load data for chart
        setupChart();
        loadDataFromDatabase();

        // listener for goal weight entry, sets goal weight in db and then refreshes chart
        addGoal.setOnClickListener((v) -> {
            db.setGoalWeight(Double.parseDouble(String.valueOf(editGoal.getText())));
            List<Weight> updated = db.getAllWeights();
            predictGoalDate(updated, db.getGoalWeight());
            refreshChart();
            });

        //Listener for adding weight
        addWeightButton.setOnClickListener(v -> addWeight());

        //Listener for settings button
        settingsButton.setOnClickListener(v -> startActivity(new Intent(this, SMSActivity.class)));

        //sets format for getting the current date for weight entry
        dateFormat = new SimpleDateFormat("dd-MM-yyy", Locale.getDefault());

        //load weights
        List<Weight> weightList = db.getAllWeights();
        adapter = new WeightAdapter(weightList, db);

        //recyclerView initialize
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);

        // Listener to update graph if weight deleted
        adapter.setOnWeightChangedListener(() ->{
            loadDataFromDatabase();
            List<Weight> updated = db.getAllWeights();
            predictGoalDate(updated, db.getGoalWeight());
        });
    }

    /**
     * Predicts the date the user will reach their goal weight.
     * Uses simple linear regression across all previous weight entries.
     *
     * Process:
     * 1. Convert weight-entry dates into X values (days since first entry)
     * 2. Use weights as Y values
     * 3. Compute slope/intercept of best-fit line
     * 4. Solve for target day when goalWeight is reached
     *
     * Displays result inside predictionText TextView.
     *
     * @param weights list of Weight objects from the database
     * @param goalWeight the user's desired target weight
     */
    public void predictGoalDate(List<Weight> weights, double goalWeight){

        //checks if past goal first
        if(weights.get(0).getWeight() <= goalWeight){
            predictionText.setText("GOAL REACHED!!");
            return;
        }

        // Needs at lest 2 data points to calculate
        if (weights.size() < 2) {
            predictionText.setText("Not enough data to predict goal date.");
            return;
        }

        // Convert dates to numeric (days)
        List<Long> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();


        // Tries to get oldest entry
        Date startDate;
        try {
            startDate = dateFormat.parse(weights.get(weights.size() - 1).getDate()); // oldest entry
        } catch (Exception e) {
            predictionText.setText("Date format error.");
            return;
        }

        // Converts all dates to days since start date
        // Provides x-axis values for linear regression
        for (Weight w : weights) {
            try {
                Date d = dateFormat.parse(w.getDate());
                long days = (d.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
                x.add(days);
                y.add(w.getWeight());
            } catch (Exception e) {
                Log.e("Failed parsing date date: " + w.getDate(), String.valueOf(e));
            }
        }

        // Linear regression: computes slope (m) and intercept (b)
        int n = x.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        //sums up x's and y's
        for (int i = 0; i < n; i++) {
            sumX += x.get(i);
            sumY += y.get(i);
            sumXY += x.get(i) * y.get(i);
            sumX2 += x.get(i) * x.get(i);
        }
        double m = 0;
        double b = 0;
        try {
            m = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);  // slope
            b = (sumY - m * sumX) / n;                                  // intercept

        }catch (Exception e){
            Log.e("Division error: ", String.valueOf(e));
        }

        // not losing weight since m >= 0
        if (m >= 0) { // not losing weight
            predictionText.setText("Goal cannot be predicted yet.");
            return;
        }

        // if goal days is less than 0 then the goal has been reached
        long goalDays = (long) ((goalWeight - b) / m);

        // Convert to calendar date
        Date goalDate = new Date(startDate.getTime() + goalDays * 24L * 60 * 60 * 1000);
        String formatted = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(goalDate);

        predictionText.setText("Predicted Goal Date: " + formatted);
    }

    /**
     * Configures MPAndroidChart behavior and X-axis styling.
     * Called once during initialization.
     */
    private void setupChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
    }


    /**
     * Loads weight entries from SQLite, extracts weight values and date labels,
     * then refreshes the chart with updated data.
     */
    public void loadDataFromDatabase() {
        List<Weight> weights = db.getAllWeights();

        weightData.clear();
        dateLabels.clear();

        for (Weight w : weights){
            weightData.add(w.getWeight());
            dateLabels.add(w.getDate());
        }

        refreshChart();
    }


    /**
     * Rebuilds the chart data set using the current weight list.
     * Adds the user's goal-weight as a horizontal LimitLine if it exists.
     */
    private void refreshChart(){

        //Pulls weights into entries table
        List<Entry> entries = new ArrayList<>();
        for(int i = 0; i < weightData.size(); i++){
            entries.add(new Entry(i, weightData.get(i).floatValue()));
        }
        // Formats line for weights
        LineDataSet dataSet = new LineDataSet(entries, "Weight Progress");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(true);
        dataSet.setDrawValues(true);

        // sets data into linechart
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        double goalWeight = db.getGoalWeight();

        // Adds horizontal goal line if user sets one, must be greater than 0
        if (goalWeight > 0) {
            LimitLine goalLine = new LimitLine((float) goalWeight, "Goal: " + goalWeight + " lbs");
            goalLine.setLineWidth(2f);
            goalLine.enableDashedLine(10f, 10f, 0f);
            goalLine.setTextSize(12f);
            goalLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);

            YAxis leftAxis = lineChart.getAxisLeft();
            leftAxis.removeAllLimitLines();   // clear old goal line
            leftAxis.addLimitLine(goalLine);  // add new goal line
        }

        // Sets x axis label and invalidate chart to draw new chart
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dateLabels));
        lineChart.invalidate();
    }



    /**
     * Attempts to add a new weight record to the database.
     *
     * Steps:
     * 1. Validate input
     * 2. Parse numeric weight
     * 3. Insert into SQLite
     * 4. Update RecyclerView + chart
     */
    private void addWeight(){
        String weight = weightInput.getText().toString().trim();

        if(weight.isEmpty()){
            Toast.makeText(this, "Please enter a weight", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tries to format string to double to use the adapter to insert the weight into DB
        try{
            double weightDouble = Double.parseDouble(weight);
            String currentDateString = dateFormat.format(new Date());
            try {
                long id = db.addWeight(currentDateString, weightDouble);
                if (id != -1){
                    Weight newWeight = new Weight((int) id, currentDateString, weightDouble);
                    adapter.addWeight(newWeight);
                    recyclerView.scrollToPosition(0);
                    loadDataFromDatabase();
                    weightInput.setText("");
                }
            } catch (Exception e) {
                Log.e("DB_ERROR", "Failed to insert weight", e);
                Toast.makeText(this, "Error saving weight", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e){
            Toast.makeText(this, "Invalid Format", Toast.LENGTH_SHORT).show();
        }

    }
}
