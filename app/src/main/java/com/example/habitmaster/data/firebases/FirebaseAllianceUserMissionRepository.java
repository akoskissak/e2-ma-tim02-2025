package com.example.habitmaster.data.firebases;

import com.example.habitmaster.domain.models.AllianceUserMission;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseAllianceUserMissionRepository {

    private final FirebaseFirestore db;

    public FirebaseAllianceUserMissionRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void insert(AllianceUserMission mission) {
        Map<String, Object> missionData = new HashMap<>();
        missionData.put("id", mission.getId());
        missionData.put("userId", mission.getUserId());
        missionData.put("missionId", mission.getMissionId());
        missionData.put("shopPurchases", mission.getShopPurchases());
        missionData.put("bossFightHits", mission.getBossFightHits());
        missionData.put("solvedTasks", mission.getSolvedTasks());
        missionData.put("solvedOtherTasks", mission.getSolvedOtherTasks());
        missionData.put("noUnresolvedTasks", mission.isNoUnresolvedTasks());
        missionData.put("messagesSentDays", mission.getMessagesSentDays());
        missionData.put("totalDamage", mission.getTotalDamage());

        db.collection("alliances_user_missions")
                .document(mission.getId())
                .set(missionData);
    }

    public void update(AllianceUserMission userMission) {
        Map<String, Object> missionData = new HashMap<>();
        missionData.put("shopPurchases", userMission.getShopPurchases());
        missionData.put("bossFightHits", userMission.getBossFightHits());
        missionData.put("solvedTasks", userMission.getSolvedTasks());
        missionData.put("solvedOtherTasks", userMission.getSolvedOtherTasks());
        missionData.put("noUnresolvedTasks", userMission.isNoUnresolvedTasks());
        missionData.put("messagesSentDays", userMission.getMessagesSentDays());
        missionData.put("totalDamage", userMission.getTotalDamage());

        db.collection("alliances_user_missions")
                .document(userMission.getId())
                .update(missionData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("User mission updated successfully in Firebase");
                    } else {
                        System.err.println("Failed to update user mission: " + task.getException());
                    }
                });
    }
}
