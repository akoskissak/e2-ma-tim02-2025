package com.example.habitmaster.data.firebases;

import com.example.habitmaster.domain.models.AllianceMission;
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
}
