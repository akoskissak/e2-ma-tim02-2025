package com.example.habitmaster.domain.usecases.alliances.missions;

import android.content.Context;

import com.example.habitmaster.data.firebases.FirebaseAllianceMissionRepository;
import com.example.habitmaster.data.firebases.FirebaseAllianceUserMissionRepository;
import com.example.habitmaster.data.repositories.AllianceMissionRepository;
import com.example.habitmaster.data.repositories.AllianceRepository;
import com.example.habitmaster.data.repositories.AllianceUserMissionRepository;
import com.example.habitmaster.domain.models.Alliance;
import com.example.habitmaster.domain.models.AllianceMission;
import com.example.habitmaster.domain.models.AllianceUserMission;

import java.time.LocalDateTime;
import java.util.UUID;

public class CreateAllianceMissionUseCase {

    private final AllianceRepository localAllianceRepo;
    private final AllianceMissionRepository localMissionRepo;
    private final FirebaseAllianceMissionRepository remoteMissionRepo;
    private final AllianceUserMissionRepository localUserMissionRepo;
    private final FirebaseAllianceUserMissionRepository remoteUserMissionRepo;

    public CreateAllianceMissionUseCase(Context context) {
        this.localAllianceRepo = new AllianceRepository(context);
        this.localMissionRepo = new AllianceMissionRepository(context);
        this.localUserMissionRepo = new AllianceUserMissionRepository(context);
        this.remoteMissionRepo = new FirebaseAllianceMissionRepository();
        this.remoteUserMissionRepo = new FirebaseAllianceUserMissionRepository();
    }

    public AllianceMission execute(Alliance alliance) {
        var memberIds = localAllianceRepo.getMemberIdsByAllianceId(alliance.getId());

        AllianceMission newMission = new AllianceMission(UUID.randomUUID().toString(),
                alliance.getId(), LocalDateTime.now(), memberIds.size() + 1); // +1 FOR LEADER
        localMissionRepo.insert(newMission);
        remoteMissionRepo.insert(newMission);

        String missionId = newMission.getId();
        for (String id: memberIds) {
            AllianceUserMission newUserMission = new AllianceUserMission(UUID.randomUUID().toString(), id, missionId);
            localUserMissionRepo.insert(newUserMission);
            remoteUserMissionRepo.insert(newUserMission);
        }

        AllianceUserMission newLeaderMission = new AllianceUserMission(UUID.randomUUID().toString(), alliance.getLeaderId(), missionId);
        localUserMissionRepo.insert(newLeaderMission);
        remoteUserMissionRepo.insert(newLeaderMission);

        alliance.setMissionStarted(true);
        localAllianceRepo.updateMissionStarted(alliance.getId(), true);

        return newMission;
    }
}
