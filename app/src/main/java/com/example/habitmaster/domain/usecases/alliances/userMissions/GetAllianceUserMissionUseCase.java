package com.example.habitmaster.domain.usecases.alliances.userMissions;

import android.content.Context;

import com.example.habitmaster.data.repositories.AllianceUserMissionRepository;
import com.example.habitmaster.domain.models.AllianceUserMission;

import java.util.List;

public class GetAllianceUserMissionUseCase {

    private final AllianceUserMissionRepository localRepo;

    public GetAllianceUserMissionUseCase(Context context) {
        this.localRepo = new AllianceUserMissionRepository(context);
    }

    public List<AllianceUserMission> getAllUserMissionsByMissionId(String missionId) {
        return localRepo.getAllByMissionId(missionId);
    }
}
