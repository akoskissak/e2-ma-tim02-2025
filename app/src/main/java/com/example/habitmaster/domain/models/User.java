package com.example.habitmaster.domain.models;

public class User {
    private String id;
    private String email;
    private String username;
    private String avatarName;
    private boolean activated;
    private long createdAt;

    public User() {}

    public User(String id, String email, String username, String avatarName, boolean activated, long createdAt) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.avatarName = avatarName;
        this.activated = activated;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
