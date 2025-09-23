package com.example.habitmaster.domain.usecases.alliances.missions;

import android.content.Context;

import com.example.habitmaster.data.repositories.AllianceMissionRepository;
import com.example.habitmaster.domain.models.AllianceMission;

public class GetAllianceMissionUseCase {
    private final AllianceMissionRepository localRepo;

    public GetAllianceMissionUseCase(Context context) {
        this.localRepo = new AllianceMissionRepository(context);
    }

    public AllianceMission getByAllianceId(String allianceId) {
        return localRepo.getByAllianceId(allianceId);
    }
}
