package com.example.habitmaster.domain.models;

public class Equipment {
    private String id;
    private String name;
    private EquipmentType type;
    private BonusType bonusType;
    private double bonusValue;  // za 10% -> 0.10 (decimalni)
    private int duration;
    private int costPercent;    // procenti
    private int iconResId;

    public Equipment(String id, String name, EquipmentType type, BonusType bonusType, double bonusValue, int duration, int cost, int iconResId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.bonusType = bonusType;
        this.bonusValue = bonusValue;
        this.duration = duration;
        this.costPercent = cost;
        this.iconResId = iconResId;
    }

    public Equipment(Equipment other) {
        this.setId(other.getId());
        this.setName(other.getName());
        this.setType(other.getType());
        this.setBonusType(other.getBonusType());
        this.setBonusValue(other.getBonusValue());
        this.setDuration(other.getDuration());
        this.setCostPercent(other.getCostPercent());
        this.setIconResId(other.iconResId);
    }

    public int calculateCost(User user) {
        int reward = user.getPreviousLevelReward();
        return (int) (reward * (this.costPercent / 100.0));
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

    public EquipmentType getType() {
        return type;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public void setType(EquipmentType type) {
        this.type = type;
    }

    public BonusType getBonusType() {
        return bonusType;
    }

    public void setBonusType(BonusType bonusType) {
        this.bonusType = bonusType;
    }

    public double getBonusValue() {
        return bonusValue;
    }

    public void setBonusValue(double bonusValue) {
        this.bonusValue = bonusValue;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCostPercent() {
        return costPercent;
    }

    public void setCostPercent(int costPercent) {
        this.costPercent = costPercent;
    }
}
