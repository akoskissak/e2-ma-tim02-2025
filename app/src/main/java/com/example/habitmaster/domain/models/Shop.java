package com.example.habitmaster.domain.models;

import com.example.habitmaster.R;

import java.util.ArrayList;
import java.util.List;

public class Shop {
    private final List<Equipment> itemsForSale;
    public static Equipment Sword = new Equipment("sword", "Mač +5% PP", EquipmentType.WEAPON, BonusType.PERM_PP_INCREASE, 0.05, -1, -1, R.drawable.sword);
    public static Equipment BowAndArrow = new Equipment("bow_and_arrow", "Luk i strela +5% dobijenog coins", EquipmentType.WEAPON, BonusType.PERM_COINS_INCREASE, 0.05, -1, -1, R.drawable.bow_and_arrow);
    public static String GLOVES_ID = "gloves";
    public static String SHIELD_ID = "shield";
    public static String BOOTS_ID = "boots";

    public Shop() {
        itemsForSale = new ArrayList<>();

        // Napici
        itemsForSale.add(new Equipment(
                "potion20", "Napitak +20% PP", EquipmentType.POTION,
                BonusType.TEMP_PP_INCREASE, 0.20, 1, 50, R.drawable.potion20));

        itemsForSale.add(new Equipment(
                "potion40", "Napitak +40% PP", EquipmentType.POTION,
                BonusType.TEMP_PP_INCREASE, 0.40, 1, 70, R.drawable.potion40));

        itemsForSale.add(new Equipment(
                "potionPerm5", "Napitak +5% PP trajno", EquipmentType.POTION,
                BonusType.PERM_PP_INCREASE, 0.05, -1, 200, R.drawable.potion5));

        itemsForSale.add(new Equipment(
                "potionPerm10", "Napitak +10% PP trajno", EquipmentType.POTION,
                BonusType.PERM_PP_INCREASE, 0.10, -1, 1000, R.drawable.potion10));

        // Odeca
        itemsForSale.add(new Equipment(
                GLOVES_ID, "Rukavice +10% PP", EquipmentType.ARMOR,
                BonusType.TEMP_PP_INCREASE, 0.10, 2, 60, R.drawable.gloves));

        itemsForSale.add(new Equipment(
                SHIELD_ID, "Stit +10% sanse uspeha", EquipmentType.ARMOR,
                BonusType.ATTACK_CHANCE_INCREASE, 0.10, 2, 60, R.drawable.shield));

        itemsForSale.add(new Equipment(
                BOOTS_ID, "Cizme +40% sansa dodatnog napada", EquipmentType.ARMOR,
                BonusType.EXTRA_ATTACK_CHANCE, 0.40, 2, 80, R.drawable.boots));
        itemsForSale.add(Sword);
        itemsForSale.add(BowAndArrow);
    }

    public List<Equipment> getItemsForSale() {
        return itemsForSale;
    }

    public Equipment getItemById(String equipmentId) {
        for (Equipment e : itemsForSale) {
            if (equipmentId.equals(e.getId())) {
                return e;
            }
        }
        return null;
    }
}
