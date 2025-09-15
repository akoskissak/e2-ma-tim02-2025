package com.example.habitmaster.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.habitmaster.R;
import com.example.habitmaster.receivers.InviteActionReceiver;

public class InviteForegroundService extends Service {
    public static final String EXTRA_INVITE_ID = "extra_invite_id";
    public static final String EXTRA_FROM_USERNAME = "extra_from_username";

    private static final String CHANNEL_ID = "alliance_channel";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String inviteId = intent.getStringExtra(EXTRA_INVITE_ID);
        String fromUsername = intent.getStringExtra(EXTRA_FROM_USERNAME);

        createNotificationChannel();

        Intent acceptIntent = new Intent(this, InviteActionReceiver.class);
        acceptIntent.setAction("ACTION_ACCEPT_INVITE");
        acceptIntent.putExtra("inviteId", inviteId);
        PendingIntent acceptPending = PendingIntent.getBroadcast(
                this, inviteId.hashCode(), acceptIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent declineIntent = new Intent(this, InviteActionReceiver.class);
        declineIntent.setAction("ACTION_DECLINE_INVITE");
        declineIntent.putExtra("inviteId", inviteId);
        PendingIntent declinePending = PendingIntent.getBroadcast(
                this, inviteId.hashCode() + 1, declineIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Poziv u savez")
                .setContentText(fromUsername + " vas je pozvao u savez")
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                .addAction(R.drawable.ic_accept, "Prihvati", acceptPending)
                .addAction(R.drawable.ic_decline, "Odbij", declinePending)
                .build();

        startForeground(inviteId.hashCode(), notification);

        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Alliance Notifications",
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Notifications for alliance invitations");
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}