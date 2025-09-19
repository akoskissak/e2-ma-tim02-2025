package com.example.habitmaster.utils;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.habitmaster.services.InviteForegroundService;

public class NotificationHelper {
    private static final String CHANNEL_ID = "alliance_channel";
    public static void showInviteNotification(Context context, String inviteId, String fromUsername) {
        Intent serviceIntent = new Intent(context, InviteForegroundService.class);
        serviceIntent.putExtra(InviteForegroundService.EXTRA_INVITE_ID, inviteId);
        serviceIntent.putExtra(InviteForegroundService.EXTRA_FROM_USERNAME, fromUsername);
        ContextCompat.startForegroundService(context, serviceIntent);
    }

    public static void notifyLeaderNewMember(Context context, String leaderUserId, String newMemberName) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        createNotificationChannel(context);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Novi clan u savezu")
                .setContentText(newMemberName + " je prihvatio poziv u savez")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        int notificationId = ("leader_notify_" + leaderUserId).hashCode();
        manager.notify(notificationId, notification);
    }

    public static void notifyNewMessage(Context context, String fromUser, String messageText) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        createNotificationChannel(context);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle("Nova poruka od " + fromUser)
                .setContentText(messageText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        int notificationId = ("msg_" + fromUser + System.currentTimeMillis()).hashCode();
        manager.notify(notificationId, notification);
    }


    private static void createNotificationChannel(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Alliance Notifications",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Notifications for alliance");
        channel.enableVibration(true);
        manager.createNotificationChannel(channel);
    }
}
