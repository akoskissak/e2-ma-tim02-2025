package com.example.habitmaster.domain.models;

import java.io.Serializable;

public class AllianceUserMission implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int DAMAGE_SHOP_PURCHASE = 2;
    public static final int DAMAGE_BOSS_FIGHT_HIT = 2;
    public static final int DAMAGE_SOLVED_TASK = 1;
    public static final int DAMAGE_SOLVED_OTHER_TASK = 4;
    public static final int DAMAGE_NO_UNRESOLVED_TASKS = 10;
    public static final int DAMAGE_MESSAGE_SENT = 4;

    private String id;
    private String userId;
    private String missionId;
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
        this.shopPurchases = 0;
        this.bossFightHits = 0;
        this.solvedTasks = 0;
        this.solvedOtherTasks = 0;
        this.noUnresolvedTasks = true;
        this.messagesSentDays = 0;
        this.totalDamage = 0;
    }

    public int calculateTotalDamage() {
        int damage = 0;
        damage += Math.min(shopPurchases, 5) * DAMAGE_SHOP_PURCHASE;
        damage += Math.min(bossFightHits, 10) * DAMAGE_BOSS_FIGHT_HIT;
        damage += Math.min(solvedTasks, 10) * DAMAGE_SOLVED_TASK;
        damage += Math.min(solvedOtherTasks, 6) * DAMAGE_SOLVED_OTHER_TASK;
        if (noUnresolvedTasks) damage += DAMAGE_NO_UNRESOLVED_TASKS; // Proveriti ovo
        damage += messagesSentDays * DAMAGE_MESSAGE_SENT;
        this.totalDamage = damage;
        return damage;
    }

    public boolean tryIncreaseShopPurchases() {
        if (shopPurchases < 5) {
            shopPurchases++;
            calculateTotalDamage();
            return true;
        }

        return false;
    }

    public boolean tryIncreaseBossFightHits() {
        if (bossFightHits < 10) {
            bossFightHits++;
            calculateTotalDamage();
            return true;
        }

        return false;
    }

    public boolean tryIncreaseSolvedTasks1() {
        if (solvedTasks < 10) {
            solvedTasks++;
            calculateTotalDamage();
            return true;
        }

        return false;
    }

    public boolean tryIncreaseSolvedTasks2() {
        if ((solvedTasks + 2) <= 10) {
            solvedTasks += 2;
            calculateTotalDamage();
            return true;
        } else if ((solvedTasks + 1) <= 10) {
            solvedTasks += 1;
            calculateTotalDamage();
            return true;
        }

        return false;
    }

    public boolean tryIncreaseSolvedOtherTasks() {
        if (solvedOtherTasks < 6) {
            solvedOtherTasks++;
            calculateTotalDamage();
            return true;
        }

        return false;
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
}
