package com.example.habitmaster.utils;

import com.example.habitmaster.domain.models.Equipment;
import com.example.habitmaster.domain.models.EquipmentType;
import com.example.habitmaster.domain.models.Shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShopUtils {

    private static final Random random = new Random();

    public static Equipment[] getRandomPotionAndArmor(Shop shop) {
        if (shop == null) return new Equipment[]{null, null};

        List<Equipment> potions = new ArrayList<>();
        List<Equipment> armors = new ArrayList<>();

        for (Equipment e : shop.getItemsForSale()) {
            if (e.getType() == EquipmentType.POTION) {
                potions.add(e);
            } else if (e.getType() == EquipmentType.ARMOR) {
                armors.add(e);
            }
        }

        Equipment randomPotion = potions.isEmpty() ? null : potions.get(random.nextInt(potions.size()));
        Equipment randomArmor = armors.isEmpty() ? null : armors.get(random.nextInt(armors.size()));

        return new Equipment[]{randomPotion, randomArmor};
    }
}
