package com.example.habitmaster.data.dtos;

import com.example.habitmaster.domain.models.Boss;

public class BossDTO {

    private String id;
    private int level;
    private double maxHp;

    private double currentHp;
    private boolean defeated;
    private int remainingAttacks;

    public BossDTO(Boss boss, double currentHp, int remainingAttacks, boolean defeated) {
        this.id = boss.getId();
        this.level = boss.getLevel();
        this.maxHp = boss.getMaxHp();

        this.currentHp = currentHp;
        this.remainingAttacks = remainingAttacks;
        this.defeated = defeated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(double maxHp) {
        this.maxHp = maxHp;
    }

    public double getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(double currentHp) {
        this.currentHp = currentHp;
    }

    public boolean isDefeated() {
        return defeated;
    }

    public void setDefeated(boolean defeated) {
        this.defeated = defeated;
    }

    public int getRemainingAttacks() {
        return remainingAttacks;
    }

    public void setRemainingAttacks(int remainingAttacks) {
        this.remainingAttacks = remainingAttacks;
    }
}
