package com.example.habitmaster.domain.models;

public class Badge {
    private String id;
    private String userId;
    private String missionId;
    private String imageName;
    private int shopPurchases;              // max 5
    private int bossFightHits;              // max 10
    private int solvedTasks;                // max 10
    private int solvedOtherTasks;           // max 6
    private boolean noUnresolvedTasks;      // +10 HP ako je true
    private int messagesSentDays;           // broj dana kada je poslao bar 1 poruku
    private int totalDamage;

    public Badge() {}

    public Badge(String id, String userId, String missionId, String imageName, int shopPurchases, int bossFightHits, int solvedTasks, int solvedOtherTasks, boolean noUnresolvedTasks, int messagesSentDays, int totalDamage) {
        this.id = id;
        this.userId = userId;
        this.missionId = missionId;
        this.imageName = imageName;
        this.shopPurchases = shopPurchases;
        this.bossFightHits = bossFightHits;
        this.solvedTasks = solvedTasks;
        this.solvedOtherTasks = solvedOtherTasks;
        this.noUnresolvedTasks = noUnresolvedTasks;
        this.messagesSentDays = messagesSentDays;
        this.totalDamage = totalDamage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public int getShopPurchases() {
        return shopPurchases;
    }

    public void setShopPurchases(int shopPurchases) {
        this.shopPurchases = shopPurchases;
    }

    public int getBossFightHits() {
        return bossFightHits;
    }

    public void setBossFightHits(int bossFightHits) {
        this.bossFightHits = bossFightHits;
    }

    public int getSolvedTasks() {
        return solvedTasks;
    }

    public void setSolvedTasks(int solvedTasks) {
        this.solvedTasks = solvedTasks;
    }

    public int getSolvedOtherTasks() {
        return solvedOtherTasks;
    }

    public void setSolvedOtherTasks(int solvedOtherTasks) {
        this.solvedOtherTasks = solvedOtherTasks;
    }

    public boolean isNoUnresolvedTasks() {
        return noUnresolvedTasks;
    }

    public void setNoUnresolvedTasks(boolean noUnresolvedTasks) {
        this.noUnresolvedTasks = noUnresolvedTasks;
    }

    public int getMessagesSentDays() {
        return messagesSentDays;
    }

    public void setMessagesSentDays(int messagesSentDays) {
        this.messagesSentDays = messagesSentDays;
    }

    public int getTotalDamage() {
        return totalDamage;
    }

    public void setTotalDamage(int totalDamage) {
        this.totalDamage = totalDamage;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
