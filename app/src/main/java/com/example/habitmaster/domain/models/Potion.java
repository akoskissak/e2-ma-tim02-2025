package com.example.habitmaster.domain.models;

public class Potion extends UserEquipment {
    public Potion(String name, double bonusValue, boolean isPermanent) {
        super();
        setName(name);
        setType(EquipmentType.POTION);
        setBonusValue(bonusValue);
        setBonusType(isPermanent ? BonusType.PERM_PP_INCREASE : BonusType.TEMP_PP_INCREASE);
        setDuration(isPermanent ? -1 : 1);
    }
}
