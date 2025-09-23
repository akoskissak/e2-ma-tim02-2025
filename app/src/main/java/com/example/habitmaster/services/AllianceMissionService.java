package com.example.habitmaster.services;

import android.content.Context;

import com.example.habitmaster.domain.models.Alliance;
import com.example.habitmaster.domain.models.AllianceMission;
import com.example.habitmaster.domain.usecases.alliances.missions.CreateAllianceMissionUseCase;
import com.example.habitmaster.domain.usecases.alliances.missions.GetAllianceMissionUseCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AllianceMissionService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final AllianceService allianceService;
    private final CreateAllianceMissionUseCase createAllianceMissionUseCase;
    private final GetAllianceMissionUseCase getAllianceMissionUseCase;
    public AllianceMissionService(Context context) {
        this.allianceService = new AllianceService(context);
        this.createAllianceMissionUseCase = new CreateAllianceMissionUseCase(context);
        this.getAllianceMissionUseCase = new GetAllianceMissionUseCase(context);
    }

    public void startAllianceMission(String leaderId, ICallback<AllianceMission> callback) {
        executorService.execute(() -> {
            allianceService.getAllianceByUserId(leaderId, new ICallback<Alliance>() {
                @Override
                public void onSuccess(Alliance alliance) {
                    if (!alliance.getLeaderId().equals(leaderId)) {
                        callback.onError("User is not a leader of alliance");
                        return;
                    }

                    var allianceMission = createAllianceMissionUseCase.execute(leaderId, alliance.getId());
                    callback.onSuccess(allianceMission);
                }

                @Override
                public void onError(String errorMessage) {
                    callback.onError(errorMessage);
                }
            });
        });
    }

    public void getOngoingAllianceMissionByAllianceId(String allianceId, ICallback<AllianceMission> callback) {
        executorService.execute(() -> {
            var ongoingAllianceMission = getAllianceMissionUseCase.getByAllianceId(allianceId);
            if (ongoingAllianceMission != null) {
                callback.onSuccess(ongoingAllianceMission);
            } else {
                callback.onError("Alliance mission not found");
            }
        });
    }
}
