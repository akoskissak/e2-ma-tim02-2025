package com.example.habitmaster.domain.usecases.alliances;

import android.content.Context;

import com.example.habitmaster.data.firebases.FirebaseAllianceRepository;
import com.example.habitmaster.data.repositories.AllianceRepository;

public class UpdateAllianceUseCase {
    private final AllianceRepository localRepo;
    private final FirebaseAllianceRepository remoteRepo;

    public UpdateAllianceUseCase(Context context) {
        this.localRepo = new AllianceRepository(context);
        this.remoteRepo = new FirebaseAllianceRepository();
    }

    public void updateAllianceMissionStarted(String allianceId, boolean started) {
        localRepo.updateMissionStarted(allianceId, started);
        remoteRepo.updateMissionStarted(allianceId, started);
    }
}
