package com.example.habitmaster.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.example.habitmaster.domain.models.Alliance;
import com.example.habitmaster.domain.models.AllianceInvitation;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.services.AllianceService;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.services.InviteForegroundService;
import com.example.habitmaster.services.UserService;
import com.example.habitmaster.utils.NotificationHelper;

public class InviteActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String inviteId = intent.getStringExtra("inviteId");
        Intent stopServiceIntent = new Intent(context, InviteForegroundService.class);

        AllianceService allianceService = new AllianceService(context);
        allianceService.getAllianceInvitationById(inviteId, new ICallback<>() {
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
                            sendLeaderNotification(context, invitationResult.getFromUserId(), targetUserId);
                            NotificationManagerCompat.from(context).cancel(inviteId.hashCode());
                            context.stopService(stopServiceIntent);
                        } else if ("ACTION_DECLINE_INVITE".equals(intent.getAction())) {
                            allianceService.declineAllianceInvite(inviteId, invitationResult.getAllianceId());
                            NotificationManagerCompat.from(context).cancel(inviteId.hashCode());
                            context.stopService(stopServiceIntent);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // Nije jos u savezu
                        if ("ACTION_ACCEPT_INVITE".equals(intent.getAction())) {
                            allianceService.acceptAllianceInvite(inviteId, targetUserId, invitationResult.getAllianceId());
                            sendLeaderNotification(context, invitationResult.getFromUserId(), targetUserId);
                        } else if ("ACTION_DECLINE_INVITE".equals(intent.getAction())) {
                            allianceService.declineAllianceInvite(inviteId, invitationResult.getAllianceId());
                        }

                        NotificationManagerCompat.from(context).cancel(inviteId.hashCode());
                        context.stopService(stopServiceIntent);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendLeaderNotification(Context context, String leaderUserId, String newMemberUserId) {
        UserService userService = new UserService(context);
        userService.findUserById(newMemberUserId, new ICallback<>() {
            @Override
            public void onSuccess(User user) {
                String newMemberName = user.getUsername();

                NotificationHelper.notifyLeaderNewMember(context, leaderUserId, newMemberName);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
