package com.example.habitmaster.domain.usecases.alliances.userMissions;

import android.content.Context;

import com.example.habitmaster.data.firebases.FirebaseAllianceMissionRepository;
import com.example.habitmaster.data.firebases.FirebaseAllianceUserMissionRepository;
import com.example.habitmaster.data.repositories.AllianceMissionRepository;
import com.example.habitmaster.data.repositories.AllianceRepository;
import com.example.habitmaster.data.repositories.AllianceUserMissionRepository;
import com.example.habitmaster.domain.models.AllianceUserMission;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckUnresolvedTasksUseCase {
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final FirebaseAllianceUserMissionRepository remoteUserMissionRepo;
    private final AllianceUserMissionRepository localUserMissionRepo;
    private final FirebaseAllianceMissionRepository remoteMissionRepo;
    private final AllianceMissionRepository localMissionRepo;
    private final AllianceRepository localAllianceRepo;

    public CheckUnresolvedTasksUseCase(Context context) {
        this.localUserMissionRepo = new AllianceUserMissionRepository(context);
        this.remoteUserMissionRepo = new FirebaseAllianceUserMissionRepository();
        this.localMissionRepo = new AllianceMissionRepository(context);
        this.remoteMissionRepo = new FirebaseAllianceMissionRepository();
        this.localAllianceRepo = new AllianceRepository(context);
    }

    public void execute(String userId) {
        executorService.execute(() -> {
            var alliance = localAllianceRepo.getAllianceByUserId(userId);
            if (alliance != null) {
                var ongoingMission = localMissionRepo.getOngoingByAllianceId(alliance.getId());
                if (ongoingMission != null) {
                    var userMission = localUserMissionRepo.getByUserIdAndMissionId(userId, ongoingMission.getId());
                    if (userMission != null) {
                        if (userMission.isNoUnresolvedTasks()) {
                            userMission.setNoUnresolvedTasks(true);
                            userMission.calculateTotalDamage();
                            localUserMissionRepo.update(userMission);
                            remoteUserMissionRepo.update(userMission);

                            ongoingMission.increaseCurrentHp(AllianceUserMission.DAMAGE_NO_UNRESOLVED_TASKS);
                            localMissionRepo.update(ongoingMission);
                            remoteMissionRepo.update(ongoingMission);
                        }
                    }
                }
            }
        });
    }
}
