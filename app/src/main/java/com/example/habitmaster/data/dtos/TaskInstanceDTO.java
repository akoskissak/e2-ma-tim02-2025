package com.example.habitmaster.data.dtos;

import com.example.habitmaster.domain.models.TaskDifficulty;
import com.example.habitmaster.domain.models.TaskFrequency;
import com.example.habitmaster.domain.models.TaskImportance;
import com.example.habitmaster.domain.models.TaskStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class TaskInstanceDTO {
    private String id;
    private String taskId;
    private String name;
    private String description;
    private int categoryId;
    private TaskFrequency frequency;
    private int repeatInterval;
    private LocalDate date;
    private LocalTime executionTime;
    private TaskDifficulty difficulty;
    private TaskImportance importance;
    private int xpValue;
    private TaskStatus status;
    private int categoryColor;

    public TaskInstanceDTO() {
    }

    public TaskInstanceDTO(String id, String taskId, String name, String description, int categoryId, TaskFrequency frequency, int repeatInterval, LocalDate date, LocalTime executionTime, TaskDifficulty difficulty, TaskImportance importance, int xpValue, TaskStatus status) {
        this.id = id;
        this.taskId = taskId;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.frequency = frequency;
        this.repeatInterval = repeatInterval;
        this.date = date;
        this.executionTime = executionTime;
        this.difficulty = difficulty;
        this.importance = importance;
        this.xpValue = xpValue;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public TaskFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(TaskFrequency frequency) {
        this.frequency = frequency;
    }

    public int getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(int repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public LocalTime getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(LocalTime executionTime) {
        this.executionTime = executionTime;
    }

    public TaskDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(TaskDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public TaskImportance getImportance() {
        return importance;
    }

    public void setImportance(TaskImportance importance) {
        this.importance = importance;
    }

    public int getXpValue() {
        return xpValue;
    }

    public void setXpValue(int xpValue) {
        this.xpValue = xpValue;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}

