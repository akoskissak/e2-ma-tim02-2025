package com.example.habitmaster.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.habitmaster.R;
import com.example.habitmaster.data.firebases.FirebaseAllianceRepository;
import com.example.habitmaster.receivers.InviteActionReceiver;

public class AllianceInviteListenerService extends Service {
    private static final String TAG = "AllianceService";
    private static final String CHANNEL_ID = "alliance_channel";
    private String currentUserId;
    private FirebaseAllianceRepository firebaseRepo;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        currentUserId = intent.getStringExtra("extra_current_user_id");
        if (currentUserId == null) {
            Log.e(TAG, "currentUserId je null!");
            stopSelf();
            return START_NOT_STICKY;
        }

        Log.d(TAG, "Starting listener for userId: " + currentUserId);

        Notification notification = new NotificationCompat.Builder(this, "service_channel")
                .setContentTitle("Alliance Invite Listener")
                .setContentText("Servis radi u pozadini")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setOngoing(true)
                .build();

        startForeground(1, notification);

        firebaseRepo = new FirebaseAllianceRepository();
        firebaseRepo.startInviteListener(currentUserId, this);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void showInviteNotification(String inviteId, String allianceId, String fromUsername) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.w("AllianceService", "Nemamo permission za notifikacije");
                return;
            }
        }
        Intent acceptIntent = new Intent(this, InviteActionReceiver.class);
        acceptIntent.setAction("ACTION_ACCEPT_INVITE");
        acceptIntent.putExtra("inviteId", inviteId);
        acceptIntent.putExtra("allianceId", allianceId);

        PendingIntent acceptPending = PendingIntent.getBroadcast(
                this, inviteId.hashCode(), acceptIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent declineIntent = new Intent(this, InviteActionReceiver.class);
        declineIntent.setAction("ACTION_DECLINE_INVITE");
        declineIntent.putExtra("inviteId", inviteId);
        declineIntent.putExtra("allianceId", allianceId);

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

        NotificationManagerCompat.from(this).notify(inviteId.hashCode(), notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (firebaseRepo != null) {
            firebaseRepo.stopInviteListener();
        }
        Log.d(TAG, "Service destroyed, listener removed");
    }
}

