package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.UserLevelProgressRepository;
import com.example.habitmaster.domain.models.UserLevelProgress;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.utils.Prefs;

public class GetLevelProgressUseCase {
    private final UserLevelProgressRepository repo;

    private final Context context;

    public GetLevelProgressUseCase(Context ctx) {
        this.context = ctx.getApplicationContext();
        this.repo = new UserLevelProgressRepository(ctx);
    }

    public void execute(ICallback<UserLevelProgress> callback) {
        try {
            String userId =getCurrentUserId();
            if(userId == null) {
                callback.onError("No user logged in");
                return;
            }

            UserLevelProgress progress = repo.getUserLevelProgress(userId);
            if(progress == null) {
                // kreiranje defaultnog progressa
                progress = new UserLevelProgress(userId);
                repo.createUserLevelProgress(progress);
            }
            callback.onSuccess(progress);
        } catch (Exception e) {
            callback.onError("Failed to load progress: " + e.getMessage());
        }
    }

    private String getCurrentUserId() {
        Prefs prefs = new Prefs(context);
        return prefs.getUid();
    }
}