package com.example.habitmaster.domain.usecases.alliances.userMissions;

import android.content.Context;

import com.example.habitmaster.data.firebases.FirebaseAllianceMissionRepository;
import com.example.habitmaster.data.firebases.FirebaseAllianceUserMissionRepository;
import com.example.habitmaster.data.repositories.AllianceUserMissionRepository;
import com.example.habitmaster.domain.models.AllianceUserMission;

public class UpdateAllianceUserMissionUseCase {
    private final AllianceUserMissionRepository localRepo;
    private final FirebaseAllianceUserMissionRepository remoteRepo;

    public UpdateAllianceUserMissionUseCase(Context context) {
        this.localRepo = new AllianceUserMissionRepository(context);
        this.remoteRepo = new FirebaseAllianceUserMissionRepository();
    }

    public void update(AllianceUserMission userMission) {
        localRepo.update(userMission);
        remoteRepo.update(userMission);
    }
}
