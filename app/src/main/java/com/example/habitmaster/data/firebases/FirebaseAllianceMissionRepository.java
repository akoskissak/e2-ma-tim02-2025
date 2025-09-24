package com.example.habitmaster.data.firebases;

import com.example.habitmaster.domain.models.AllianceMission;
import com.example.habitmaster.domain.models.AllianceUserMission;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseAllianceMissionRepository {
    private final FirebaseFirestore db;

    public FirebaseAllianceMissionRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void insert(AllianceMission mission) {
        Map<String, Object> missionData = new HashMap<>();
        missionData.put("id", mission.getId());
        missionData.put("allianceId", mission.getAllianceId());
        missionData.put("startDateTime", mission.getStartDateTime().toString()); // LocalDateTime kao string
        missionData.put("endDateTime", mission.getEndDateTime().toString());
        missionData.put("status", mission.getStatus().name());
        missionData.put("bossMaxHp", mission.getBossMaxHp());
        missionData.put("bossCurrentHp", mission.getBossCurrentHp());

        db.collection("alliances_missions")
                .document(mission.getId())
                .set(missionData);
    }

    public void update(AllianceMission allianceMission) {
        Map<String, Object> missionData = new HashMap<>();
        missionData.put("allianceId", allianceMission.getAllianceId());
        missionData.put("startDateTime", allianceMission.getStartDateTime().toString());
        missionData.put("endDateTime", allianceMission.getEndDateTime().toString());
        missionData.put("status", allianceMission.getStatus().name());
        missionData.put("bossMaxHp", allianceMission.getBossMaxHp());
        missionData.put("bossCurrentHp", allianceMission.getBossCurrentHp());

        db.collection("alliances_missions")
                .document(allianceMission.getId())
                .update(missionData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("Alliance mission updated successfully in Firebase");
                    } else {
                        System.err.println("Failed to update alliance mission: " + task.getException());
                    }
                });
    }

}
