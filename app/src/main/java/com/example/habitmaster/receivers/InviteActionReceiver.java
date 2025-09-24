package com.example.habitmaster.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.example.habitmaster.domain.models.Alliance;
import com.example.habitmaster.domain.models.AllianceInvitation;
import com.example.habitmaster.services.AllianceService;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.ui.activities.AllianceActivity;

public class InviteActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String inviteId = intent.getStringExtra("inviteId");
        String allianceId = intent.getStringExtra("allianceId");

        AllianceService allianceService = new AllianceService(context);
        allianceService.getAllianceInvitationById(inviteId, allianceId, new ICallback<>() {
            @Override
            public void onSuccess(AllianceInvitation invitationResult) {

                String targetUserId = invitationResult.getToUserId();

                allianceService.getAllianceByUserId(targetUserId, new ICallback<>() {
                    @Override
                    public void onSuccess(Alliance currentAlliance) {
                        if ("ACTION_ACCEPT_INVITE".equals(intent.getAction())) {
                            // Da li je trenutna misija pokrenuta
                            if (currentAlliance.isMissionStarted()) {
                                Toast.makeText(context, "Ne mozete napustiti trenutni savez dok traje misija", Toast.LENGTH_LONG).show();
                                return;
                            }

                            allianceService.acceptAllianceInvite(inviteId, targetUserId, invitationResult.getAllianceId());
                        } else if ("ACTION_DECLINE_INVITE".equals(intent.getAction())) {
                            allianceService.declineAllianceInvite(inviteId, invitationResult.getAllianceId());
                        }
                        NotificationManagerCompat.from(context).cancel(inviteId.hashCode());
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // Nije jos u savezu
                        if ("ACTION_ACCEPT_INVITE".equals(intent.getAction())) {
                            allianceService.acceptAllianceInvite(inviteId, targetUserId, invitationResult.getAllianceId());
                        } else if ("ACTION_DECLINE_INVITE".equals(intent.getAction())) {
                            allianceService.declineAllianceInvite(inviteId, invitationResult.getAllianceId());
                        }

                        NotificationManagerCompat.from(context).cancel(inviteId.hashCode());
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
