package com.example.habitmaster.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitmaster.R;
import com.example.habitmaster.data.dtos.TaskInstanceDTO;

import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {

    private final List<TaskInstanceDTO> tasks;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TaskInstanceDTO task);
    }

    public TasksAdapter(List<TaskInstanceDTO> tasks, OnItemClickListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskInstanceDTO task = tasks.get(position);
        holder.nameText.setText(task.getName());
        holder.descriptionText.setText(task.getDescription());
        holder.difficultyText.setText(task.getDifficulty().name());
        holder.importanceText.setText(task.getImportance().name());

        View itemColor = holder.itemView.findViewById(R.id.itemColor);
        int color = task.getCategoryColor();
        itemColor.setBackgroundColor(color);

        // klik listener
        holder.itemView.setOnClickListener(v -> listener.onItemClick(task));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, descriptionText, difficultyText, importanceText;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.textTaskName);
            descriptionText = itemView.findViewById(R.id.textTaskDescription);
            difficultyText = itemView.findViewById(R.id.textTaskDifficulty);
            importanceText = itemView.findViewById(R.id.textTaskImportance);
        }
    }
}
