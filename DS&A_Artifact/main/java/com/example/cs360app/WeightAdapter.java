package com.example.cs360app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * RecyclerView adapter responsible for displaying and managing a list of Weight entries.
 * Each item contains a date, weight, and a delete button that removes the entry from both
 * the database and the UI.
 *
 * This adapter supports notifying a listener when a weight entry is added or deleted,
 * allowing other UI components (such as charts or predictions) to update in real time.
 */
public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.WeightViewHolder> {

    /**
     * Callback interface used to notify the parent Activity/Fragment when the weight list changes.
     */
    public interface OnWeightChangedListener {
        void onWeightChanged();
    }

    private List<Weight> weightList;
    private DBHelper db;
    private OnWeightChangedListener weightChangedListener;

    /**
     * Constructs a new WeightAdapter.
     *
     * @param weightList the list of weight objects to display
     * @param dbHelper   reference to the database helper for deleting entries
     */
    public WeightAdapter(List<Weight> weightList, DBHelper dbHelper) {
        this.weightList = weightList;
        this.db = dbHelper;
    }



    /**
     * Inflates the CardView layout used for each weight entry.
     *
    ** @noinspection ClassEscapesDefinedScope*/
    @NonNull
    @Override
    public WeightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_entry, parent, false);
        return new WeightViewHolder(itemView);
    }

    /**
     * Binds data from a Weight object to the UI components inside the ViewHolder.
     * Also attaches a delete-button listener for removing the entry.
     */
    @Override
    public void onBindViewHolder(@NonNull WeightViewHolder holder, int position) {
        Weight weight = weightList.get(position);

        // Display date and weight
        holder.textDate.setText(weight.getDate());
        holder.textWeight.setText(weight.getWeight() + " lbs");

        //Delete button handler - deletes from DB and recyclerview
        holder.deleteButton.setOnClickListener(v -> {
        db.deleteWeight(weight.getId());
        weightList.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position, weightList.size());

        // Notify listener to update charts/predictions
        if(weightChangedListener != null){
            weightChangedListener.onWeightChanged();
        }
        });
    }

    /**
     * @return number of weight entries displayed
     */
    @Override
    public int getItemCount() {
        return weightList.size();
    }

    /**
     * Registers a listener that will be notified whenever weights are added or removed.
     */
    public void setOnWeightChangedListener(OnWeightChangedListener listener) {
        this.weightChangedListener = listener;
    }

    /**
     * Inserts a new weight entry at the top of the list (most recent first).
     *
     * @param weight the new Weight object to insert
     */
    public void addWeight(Weight weight) {
        weightList.add(0, weight); // add at top
        notifyItemInserted(0);
    }

    /**
     * Represents a single row in the RecyclerView.
     * Holds references to the date, weight text fields, and delete button.
     */
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
