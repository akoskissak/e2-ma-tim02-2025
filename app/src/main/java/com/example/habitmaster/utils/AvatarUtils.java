package com.example.habitmaster.utils;

import com.example.habitmaster.R;

import java.util.HashMap;
import java.util.Map;

public class AvatarUtils {
    private static final Map<String, Integer> avatarMap = new HashMap<>();

    static {
        avatarMap.put("avatar1", R.drawable.avatar1);
        avatarMap.put("avatar2", R.drawable.avatar2);
        avatarMap.put("avatar3", R.drawable.avatar3);
        avatarMap.put("avatar4", R.drawable.avatar4);
        avatarMap.put("avatar5", R.drawable.avatar5);
    }

    public static int getAvatarResId(String avatarName) {
        Integer resId = avatarMap.get(avatarName);
        return resId != null ? resId : R.drawable.default_avatar;
    }
}
