package com.example.habitmaster.domain.usecases;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.habitmaster.data.firebases.FirebaseAllianceRepository;
import com.example.habitmaster.data.repositories.AllianceRepository;
import com.example.habitmaster.domain.models.Alliance;
import com.example.habitmaster.domain.models.AllianceInvitation;
import com.example.habitmaster.domain.models.AllianceInviteStatus;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.utils.NotificationHelper;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CreateAllianceUseCase {
    private final AllianceRepository repo;
    private final FirebaseAllianceRepository firebaseRepo;
    private Context ctx;

    public CreateAllianceUseCase(Context ctx) {
        this.ctx = ctx;
        this.repo = new AllianceRepository(ctx);
        this.firebaseRepo = new FirebaseAllianceRepository();
    }

    public void execute(String allianceName, String leaderUserId, String leaderUsername, Set<String> memberIds, ICallback<String> callback) {
        Alliance existingAllianceAsLeader = repo.getAllianceByLeaderId(leaderUserId);
        if (existingAllianceAsLeader != null) {
            callback.onError("Vec vodis savez i ne mozes kreirati novi.");
            return;
        }

        Alliance existingAlliance = repo.getAllianceByUserId(leaderUserId);
        if(existingAlliance != null) {
            callback.onError("Vec pripadas savezu i ne mozes kreirati novi.");
            return;
        }

        String allianceId = UUID.randomUUID().toString();
        Alliance newAlliance = new Alliance(allianceId, allianceName, leaderUserId, false);

        repo.createAlliance(newAlliance);



        if(memberIds != null) {

            for(String memberId : memberIds){
                String inviteId = UUID.randomUUID().toString();
                AllianceInvitation invitation = new AllianceInvitation(inviteId, allianceId, leaderUserId, memberId, AllianceInviteStatus.PENDING);
                repo.addInvitation(invitation);

                NotificationHelper.showInviteNotification(ctx, invitation.getId(), leaderUsername);
            }
        }

        // Firebase
        firebaseRepo.createAlliance(newAlliance, task -> {
            if (task.isSuccessful()) {
                if (memberIds != null) {
                    for (String memberId : memberIds) {
                        String inviteId = UUID.randomUUID().toString();
                        AllianceInvitation invitation = new AllianceInvitation(inviteId, allianceId, leaderUserId, memberId, AllianceInviteStatus.PENDING);
                        firebaseRepo.addInvitation(invitation, inivteTask -> {});
                    }
                }
                callback.onSuccess(allianceId);
            } else {
                callback.onError("Greska Firebase: " + task.getException().getMessage());
            }
        });
    }
}
