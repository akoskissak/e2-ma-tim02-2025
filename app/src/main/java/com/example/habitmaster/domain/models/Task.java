package com.example.habitmaster.domain.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Task {
    private String id;
    private String userId;
    private String name;
    private String description;
    private String categoryId;
    private TaskFrequency frequency;
    private int repeatInterval;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime executionTime;
    private TaskDifficulty difficulty;
    private TaskImportance importance;
    private int xpValue; // difficulty + importance

    public Task() {
    }

    public Task(String id, String userId, String name, String description, String categoryId, TaskFrequency frequency, int repeatInterval, LocalDate startDate, LocalDate endDate, LocalTime executionTime, TaskDifficulty difficulty, TaskImportance importance) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.frequency = frequency;
        this.repeatInterval = repeatInterval;
        this.startDate = startDate;
        this.endDate = endDate;
        this.executionTime = executionTime;
        this.difficulty = difficulty;
        this.importance = importance;
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void calculateXp(UserLevelProgress progress) {
        if (progress == null) {
            throw new IllegalArgumentException("User progress cannot be null");
        }
        this.xpValue = this.difficulty.getXpValue(progress) + this.importance.getXpValue(progress);
    }

    public LocalTime getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(LocalTime executionTime) {
        this.executionTime = executionTime;
    }
}
