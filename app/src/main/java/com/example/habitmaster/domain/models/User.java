package com.example.habitmaster.domain.models;

public class User {
    private String id;
    private String email;
    private String username;
    private String avatarName;
    private boolean activated;
    private long createdAt;
    private int level;
    private String title;
    private int powerPoints;
    private int xp;
    private int coins;
    private int badgesCount;
    private String badges;

    public User() {}

    public User(String id, String email, String username, String avatarName, boolean activated, long createdAt) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.avatarName = avatarName;
        this.activated = activated;
        this.createdAt = createdAt;
        this.level = 0;
        this.title = "Rookie";
        this.powerPoints = 0;
        this.xp = 0;
        this.coins = 0;
        this.badgesCount = 0;
        this.badges = "";
    }

    public int getPreviousLevelReward() {
        if(level <= 1) return 200;
        int reward = 200;
        for(int i = 2; i <= level; i++) {
            reward = (int) (reward * 1.2);
        }
        return reward;
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPowerPoints() {
        return powerPoints;
    }

    public void setPowerPoints(int powerPoints) {
        this.powerPoints = powerPoints;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getBadgesCount() {
        return badgesCount;
    }

    public void setBadgesCount(int badgesCount) {
        this.badgesCount = badgesCount;
    }

    public String getBadges() {
        return badges;
    }

    public void setBadges(String badges) {
        this.badges = badges;
    }
}
