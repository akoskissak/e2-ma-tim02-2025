package com.example.habitmaster.domain.usecases.alliances.missions;

import android.content.Context;

import com.example.habitmaster.data.repositories.AllianceMissionRepository;
import com.example.habitmaster.domain.models.AllianceMission;

public class GetAllianceMissionUseCase {
    private final AllianceMissionRepository localRepo;

    public GetAllianceMissionUseCase(Context context) {
        this.localRepo = new AllianceMissionRepository(context);
    }

    public AllianceMission getOngoingByAllianceId(String allianceId) {
        return localRepo.getOngoingByAllianceId(allianceId);
    }

    public AllianceMission getById(String id) {
        return localRepo.getById(id);
    }
}
