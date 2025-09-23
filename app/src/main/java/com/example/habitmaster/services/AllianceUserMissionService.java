package com.example.habitmaster.services;

import android.content.Context;

import com.example.habitmaster.domain.models.AllianceUserMission;
import com.example.habitmaster.domain.usecases.alliances.userMissions.GetAllianceUserMissionUseCase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AllianceUserMissionService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final GetAllianceUserMissionUseCase getAllianceUserMissionUC;
    public AllianceUserMissionService(Context context) {
        this.getAllianceUserMissionUC = new GetAllianceUserMissionUseCase(context);
    }

    public void getAllUserAllianceUserMissionsByMissionId(String missionId, ICallback<List<AllianceUserMission>> callback) {
        executorService.execute(() -> {
            var all = getAllianceUserMissionUC.getAllUserMissionsByMissionId(missionId);
            if (all.isEmpty()) {
                callback.onError("Empty list");
            } else {
                callback.onSuccess(all);
            }
        });
    }
}
