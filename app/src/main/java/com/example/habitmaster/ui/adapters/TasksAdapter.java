package com.example.habitmaster.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.Task;

import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {

    private final List<Task> tasks;

    public TasksAdapter(List<Task> tasks) {
        this.tasks = tasks;
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
        Task task = tasks.get(position);
        holder.nameText.setText(task.getName());
        holder.descriptionText.setText(task.getDescription());
        holder.difficultyText.setText(task.getDifficulty().name());
        holder.importanceText.setText(task.getImportance().name());

        View itemColor = holder.itemView.findViewById(R.id.itemColor);

        int color;
        switch (task.getCategoryId()) {
            case 1:
                color = Color.rgb(0, 0, 255);
                break;
            case 2:
                color = Color.rgb(255, 0, 0);
                break;
            case 3:
                color = Color.rgb(0, 255, 0);
                break;
            default:
                color = Color.rgb(200, 200, 200);
        }

        itemColor.setBackgroundColor(color);
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
