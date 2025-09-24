package com.example.habitmaster.domain.usecases.alliances.missions;

import android.content.Context;

import com.example.habitmaster.data.firebases.FirebaseAllianceMissionRepository;
import com.example.habitmaster.data.repositories.AllianceMissionRepository;
import com.example.habitmaster.domain.models.AllianceMission;

public class UpdateAllianceMissionUseCase {
    private final AllianceMissionRepository localRepo;
    private final FirebaseAllianceMissionRepository remoteRepo;

    public UpdateAllianceMissionUseCase(Context context) {
        this.localRepo = new AllianceMissionRepository(context);
        this.remoteRepo = new FirebaseAllianceMissionRepository();
    }

    public void execute(AllianceMission allianceMission) {
        localRepo.update(allianceMission);
        remoteRepo.update(allianceMission);
    }
}
