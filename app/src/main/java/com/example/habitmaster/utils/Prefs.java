package com.example.habitmaster.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private static final String FILE = "habitmaster_prefs";
    private static final String KEY_UID = "uid";
    private static final String KEY_USERNAME_LOCK = "username_lock";
    private static final String KEY_AVATAR_LOCK = "avatar_lock";

    private final SharedPreferences sp;

    public Prefs(Context ctx) {
        sp = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public void setUid(String uid) {
        if(uid == null){
            sp.edit().remove(KEY_UID).apply();
        } else {
            sp.edit().putString(KEY_UID, uid).apply();
        }
    }

    public String getUid() {
        return sp.getString(KEY_UID, null);
    }

    public void lockUsername() {
        sp.edit().putBoolean(KEY_USERNAME_LOCK, true).apply();
    }

    public boolean isUsernameLocked() {
        return sp.getBoolean(KEY_USERNAME_LOCK, false);
    }

    public void lockAvatar() {
        sp.edit().putBoolean(KEY_AVATAR_LOCK, true).apply();
    }

    public boolean isAvatarLocked() {
        return sp.getBoolean(KEY_AVATAR_LOCK, false);
    }
}
