package com.example.habitmaster.domain.models;

public class Armor extends UserEquipment {
    public Armor(String name, double bonusValue, BonusType bonusType) {
        super();
        setName(name);
        setType(EquipmentType.ARMOR);
        setBonusValue(bonusValue);
        setBonusType(bonusType);
        setDuration(2);
    }

    public void stackBonus() {
        setBonusValue(getBonusValue() * 2);
    }
}
