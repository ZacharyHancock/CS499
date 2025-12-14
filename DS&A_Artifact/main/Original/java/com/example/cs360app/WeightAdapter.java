package com.example.cs360app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Adaptes the CardView to be properly used in the Recycler
public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.WeightViewHolder> {

    private List<Weight> weightList;
    private DBHelper db;

    public WeightAdapter(List<Weight> weightList, DBHelper dbHelper) {
        this.weightList = weightList;
        this.db = dbHelper;
    }

    /** @noinspection ClassEscapesDefinedScope*/
    // returns each table entry for BindViewHolder to wokr with the RecyclerView
    @NonNull
    @Override
    public WeightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_entry, parent, false);
        return new WeightViewHolder(itemView);
    }

    // handles the display of the data in the Recycler properly to be able to use the delete button for each entry
    @Override
    public void onBindViewHolder(@NonNull WeightViewHolder holder, int position) {
        Weight weight = weightList.get(position);
        holder.textDate.setText(weight.getDate());
        holder.textWeight.setText(weight.getWeight() + " lbs");

        //Delete button handler
        holder.deleteButton.setOnClickListener(v -> {
        db.deleteWeight(weight.getId());

        weightList.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position, weightList.size());
        });
    }

    // Gets the number of weights in the list
    @Override
    public int getItemCount() {
        return weightList.size();
    }

    // adds the entered weight to the top of the list
    public void addWeight(Weight weight) {
        weightList.add(0, weight); // add at top
        notifyItemInserted(0);
    }

    //Handles the View for the cardView
    static class WeightViewHolder extends RecyclerView.ViewHolder {
        TextView textDate, textWeight;
        Button deleteButton;

        public WeightViewHolder(@NonNull View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.textDate);
            textWeight = itemView.findViewById(R.id.textWeight);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
