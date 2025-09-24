package com.example.habitmaster.services;

import android.content.Context;

import com.example.habitmaster.domain.models.Alliance;
import com.example.habitmaster.domain.models.AllianceMission;
import com.example.habitmaster.domain.usecases.alliances.missions.CreateAllianceMissionUseCase;
import com.example.habitmaster.domain.usecases.alliances.missions.GetAllianceMissionUseCase;
import com.example.habitmaster.domain.usecases.alliances.missions.UpdateAllianceMissionUseCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AllianceMissionService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final CreateAllianceMissionUseCase createAllianceMissionUseCase;
    private final GetAllianceMissionUseCase getAllianceMissionUseCase;
    private final UpdateAllianceMissionUseCase updateAllianceMissionUseCase;
    public AllianceMissionService(Context context) {
        this.createAllianceMissionUseCase = new CreateAllianceMissionUseCase(context);
        this.getAllianceMissionUseCase = new GetAllianceMissionUseCase(context);
        this.updateAllianceMissionUseCase = new UpdateAllianceMissionUseCase(context);
    }

    public void startAllianceMission(Alliance alliance, ICallback<AllianceMission> callback) {
        if (alliance.isMissionStarted()) {
            callback.onError("Alliance mission already started");
            return;
        }

        executorService.execute(() -> {
            var allianceMission = createAllianceMissionUseCase.execute(alliance);
            callback.onSuccess(allianceMission);
        });
    }

    public void getOngoingAllianceMissionByAllianceId(String allianceId, ICallback<AllianceMission> callback) {
        executorService.execute(() -> {
            var ongoingAllianceMission = getAllianceMissionUseCase.getOngoingByAllianceId(allianceId);
            if (ongoingAllianceMission != null) {
                callback.onSuccess(ongoingAllianceMission);
            } else {
                callback.onError("Alliance mission not found");
            }
        });
    }

    public void update(AllianceMission allianceMission) {
        updateAllianceMissionUseCase.execute(allianceMission);
    }
}
