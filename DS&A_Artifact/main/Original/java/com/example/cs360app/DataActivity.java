package com.example.cs360app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class DataActivity extends AppCompatActivity {

        private DBHelper db;
        private RecyclerView recyclerView;
        private EditText weightInput;

    private SimpleDateFormat dateFormat;
    private WeightAdapter adapter;

    // onCreate the data activity screen listens for button presses and initializes the adapter for the recycler view and the recycler itself
    @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_data);

            // Initialize the database helper
            db = new DBHelper(this);

            recyclerView = findViewById(R.id.dataRecyclerView);

        Button addWeightButton = findViewById(R.id.addEntryButton);
        Button settingsButton = findViewById(R.id.settingsButton);

            weightInput = findViewById(R.id.weightEntry);

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

        }



        // Handles adding weight into the table, by getting the weight entered in the editText and checking if its not empty, then trying to properly add it to the db and updating the recycler
        private void addWeight(){
            String weight = weightInput.getText().toString().trim();

            if(weight.isEmpty()){
                Toast.makeText(this, "Please enter a weight", Toast.LENGTH_SHORT).show();
                return;
            }

            try{
                double weightDouble = Double.parseDouble(weight);
                String currentDateString = dateFormat.format(new Date());
                try {
                    long id = db.addWeight(currentDateString, weightDouble);
                    if (id != -1){
                        Weight newWeight = new Weight((int) id, currentDateString, weightDouble);
                        adapter.addWeight(newWeight);
                        recyclerView.scrollToPosition(0);
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
