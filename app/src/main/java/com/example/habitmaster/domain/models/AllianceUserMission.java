package com.example.habitmaster.domain.models;

import java.io.Serializable;

public class AllianceUserMission implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String userId;
    private String missionId;
    private int damageDealt;
    private int shopPurchases;              // max 5
    private int bossFightHits;              // max 10
    private int solvedTasks;                // max 10
    private int solvedOtherTasks;           // max 6
    private boolean noUnresolvedTasks;      // +10 HP ako je true
    private int messagesSentDays;           // broj dana kada je poslao bar 1 poruku
    private int totalDamage;

    public AllianceUserMission() {}

    public AllianceUserMission(String id, String userId, String missionId) {
        this.id = id;
        this.userId = userId;
        this.missionId = missionId;
        this.damageDealt = 0;
        this.shopPurchases = 0;
        this.bossFightHits = 0;
        this.solvedTasks = 0;
        this.solvedOtherTasks = 0;
        this.noUnresolvedTasks = false;
        this.messagesSentDays = 0;
        this.totalDamage = 0;
    }

    public int calculateTotalDamage() {
        int damage = 0;
        damage += Math.min(shopPurchases, 5) * 2;
        damage += Math.min(bossFightHits, 10) * 2;
        damage += Math.min(solvedTasks, 10);
        damage += Math.min(solvedOtherTasks, 6) * 4;
//        if (noUnresolvedTasks) damage += 10; // Proveriti ovo
        damage += messagesSentDays * 4;
        this.totalDamage = damage;
        return damage;
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

    public int getDamageDealt() {
        return damageDealt;
    }

    public void setDamageDealt(int damageDealt) {
        this.damageDealt = damageDealt;
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
}
