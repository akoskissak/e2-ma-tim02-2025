package com.example.habitmaster.domain.models;

public class Category {
    private String id;
    private String userId;
    private String name;
    private int color;

    public Category() {
    }

    public Category(String id, String userId, String name, int color) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.color = color;
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
