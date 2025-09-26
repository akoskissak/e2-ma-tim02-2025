package com.example.habitmaster.domain.models;

public class Boss {
    private String id;
    private String userId;
    private int level;
    private double maxHp;
    private double currentHp;
    private int remainingAttacks;
    private int maxAttacks;
    private double rewardCoins;
    private double equipmentRewardChance;

    public Boss() {

    }

    public Boss(String id, String userId, int level) {
        this.id = id;
        this.userId = userId;
        this.level = level;
        this.maxHp = calculateHp(level);
        this.currentHp = this.maxHp;
        this.remainingAttacks = 5;
        this.maxAttacks = remainingAttacks;
        this.rewardCoins = calculateRewardCoins(level);
        this.equipmentRewardChance = 0.2;
    }

    public Boss(String id, int level, double maxHp) {
        this.id = id;
        this.level = level;
        this.maxHp = maxHp;
    }

    private double calculateHp(int level) {
        double hp = 200;
        for (int i = 1; i < level; i++) {
            hp = hp * 2 + hp / 2;
        }
        return hp;
    }

    private double calculateRewardCoins(int level) {
        double coins = 200;
        for (int i = 1; i < level; i++) {
            coins *= 1.2;
        }
        return coins;
    }

    public static int calculateHalfNextReward(int currentUserLevel) {
        double coins = 200;
        for (int i = 1; i < currentUserLevel + 1; i++) {
            coins *= 1.2;
        }
        return (int) (coins / 2);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLevel() { return level; }
    public double getMaxHp() { return maxHp; }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setMaxHp(double maxHp) {
        this.maxHp = maxHp;
    }

    public double getRewardCoins() {
        return rewardCoins;
    }

    public void setRewardCoins(double rewardCoins) {
        this.rewardCoins = rewardCoins;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(double currentHp) {
        this.currentHp = currentHp;
    }

    public int getRemainingAttacks() {
        return remainingAttacks;
    }

    public int getMaxAttacks() {
        return maxAttacks;
    }

    public void setMaxAttacks(int maxAttacks) {
        this.maxAttacks = maxAttacks;
    }

    public void setRemainingAttacks(int remainingAttacks) {
        this.remainingAttacks = remainingAttacks;
    }

    public double getEquipmentRewardChance() {
        return equipmentRewardChance;
    }

    public void setEquipmentRewardChance(double equipmentRewardChance) {
        this.equipmentRewardChance = equipmentRewardChance;
    }

    public boolean isDefeated() {
        return currentHp <= 0;
    }

    public void attack(int powerPoints) {
        if (currentHp >= powerPoints) {
            currentHp -= powerPoints;
        } else {
            currentHp = 0;
        }

        if (remainingAttacks > 0) {
            remainingAttacks--;
            if (currentHp > 0 && remainingAttacks == 0 && currentHp / maxHp < 0.5) {
                rewardCoins /= 2;
                equipmentRewardChance /= 2;
            }
        }
    }

    public boolean canAttack() {
        return currentHp > 0 && remainingAttacks > 0;
    }

    public boolean isHalfDefeated() {
        return (currentHp <= maxHp / 2) && remainingAttacks == 0;
    }
}

