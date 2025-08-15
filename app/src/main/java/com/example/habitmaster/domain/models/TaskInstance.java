package com.example.habitmaster.domain.models;

import java.time.LocalDate;

public class TaskInstance {
    private String id;
    private String taskId;
    private LocalDate date;
    private TaskStatus status;

    public TaskInstance() {
    }

    public TaskInstance(String id, String taskId, LocalDate date, TaskStatus status) {
        this.id = id;
        this.taskId = taskId;
        this.date = date;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
