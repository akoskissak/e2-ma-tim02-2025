package com.example.habitmaster.domain.usecases;

import android.content.Context;
import android.util.Log;

import com.example.habitmaster.data.repositories.UserStatisticsRepository;
import com.example.habitmaster.domain.models.UserStatistics;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.utils.Prefs;

public class GetUserStatisticsUseCase {
    private final UserStatisticsRepository repo;

    private final Context context;
    public GetUserStatisticsUseCase(Context ctx){
        this.context = ctx.getApplicationContext();
        this.repo = new UserStatisticsRepository(ctx);
    }

    public void execute(ICallback<UserStatistics> callback){
        try {
            Prefs prefs = new Prefs(context);
            String uid = prefs.getUid();
            if(uid == null){
                callback.onError("No user logged in");
                return;
            }

            UserStatistics stats = repo.getUserStatistics(uid);
            callback.onSuccess(stats);
        } catch (Exception e) {
            callback.onError("Failed to load statistics: " + e.getMessage());
        }
    }
}
