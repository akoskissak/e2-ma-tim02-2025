package com.example.habitmaster.services;

import android.content.Context;

import com.example.habitmaster.data.dtos.BadgeDTO;
import com.example.habitmaster.domain.models.Badge;
import com.example.habitmaster.domain.usecases.badges.GetUserBadgesUseCase;

import java.util.List;

public class BadgeService {
    private final GetUserBadgesUseCase getUserBadgesUseCase;

    public BadgeService(Context context) {
        this.getUserBadgesUseCase = new GetUserBadgesUseCase(context);
    }

    public void getAllByUserId(String userId, ICallback<List<BadgeDTO>> callback) {
        var all = getUserBadgesUseCase.getAllByUserId(userId);
        if (!all.isEmpty()) {
            callback.onSuccess(all);
        } else {
            callback.onError("User has no badges");
        }
    }
}
