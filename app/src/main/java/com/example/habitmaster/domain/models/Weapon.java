package com.example.habitmaster.domain.models;

public class Weapon extends UserEquipment {
    private int upgradeLevel = 0;
    public Weapon(String name, double bonusValue, BonusType bonusType) {
        super();
        setName(name);
        setType(EquipmentType.WEAPON);
        setBonusType(bonusType);
        setBonusValue(bonusValue);
        setDuration(-1);
    }

    public void upgrade() {
        upgradeLevel++;
        setBonusValue(getBonusValue() + 0.0001);    // 0.01% = 0.0001
    }

    public int getUpgradeLevel() {
        return this.upgradeLevel;
    }

    public void setUpgradeLevel(int upgradeLevel) {
        this.upgradeLevel = upgradeLevel;
    }

    public int calculateUpgradeCost(User user) {
        int reward = user.getPreviousLevelReward();
        int upgradeCostPercent = 60;
        return (int) (reward * (upgradeCostPercent / 100.0));
    }
}
