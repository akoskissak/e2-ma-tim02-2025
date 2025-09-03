package com.example.habitmaster.domain.models;

public abstract class UserEquipment {
    private String id;
    private String userId;
    private String equipmentId;
    private String name;
    private EquipmentType type;
    private boolean activated;
    private int duration;
    private double bonusValue;
    private BonusType bonusType;

    protected UserEquipment(String id, String userId, String equipmentId, String name, EquipmentType type, boolean activated, int duration, double bonusValue, BonusType bonusType) {
        this.id = id;
        this.userId = userId;
        this.equipmentId = equipmentId;
        this.name = name;
        this.type = type;
        this.activated = activated;
        this.duration = duration;
        this.bonusValue = bonusValue;
        this.bonusType = bonusType;
    }

    public UserEquipment() {}

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

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
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

    public void setType(EquipmentType type) {
        this.type = type;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getBonusValue() {
        return bonusValue;
    }

    public void setBonusValue(double bonusValue) {
        this.bonusValue = bonusValue;
    }

    public BonusType getBonusType() {
        return bonusType;
    }

    public void setBonusType(BonusType bonusType) {
        this.bonusType = bonusType;
    }

    public String formatDuration() {
        if(this.duration == -1) {
            return "Permanent";
        } else {
            return "Left " + duration;
        }
    }
}
