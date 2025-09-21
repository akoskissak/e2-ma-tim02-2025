package com.example.habitmaster.utils;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.Shop;

import java.util.HashMap;
import java.util.Map;

public class EquipmentDrawableMapper {
    private static final Map<String, Integer> equipmentMap = new HashMap<>();

    static {
        equipmentMap.put("sword", R.drawable.sword);
        equipmentMap.put("bowAndArrow", R.drawable.bow_and_arrow);
        equipmentMap.put(Shop.GLOVES_ID, R.drawable.gloves);
        equipmentMap.put(Shop.SHIELD_ID, R.drawable.shield);
        equipmentMap.put(Shop.BOOTS_ID, R.drawable.boots);
    }

    public static int getAvatarResId(String equipmentId) {
        Integer resId = equipmentMap.get(equipmentId);
        return resId != null ? resId : R.drawable.default_avatar;
    }
}
