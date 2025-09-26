package com.example.habitmaster.services;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.habitmaster.data.firebases.FirebaseAllianceRepository;

public class AllianceMemberListenerService extends Service {
    private static final String TAG = "AllianceMemberService";
    private static final String CHANNEL_ID = "alliance_channel";
    private String currentUserId;
    private long lastLogout;
    private FirebaseAllianceRepository firebaseRepo;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        currentUserId = intent.getStringExtra("extra_current_user_id");
        lastLogout = intent.getLongExtra("extra_last_logout", 0);
        if (currentUserId == null) {
            Log.e(TAG, "currentUserId je null!");
            stopSelf();
            return START_NOT_STICKY;
        }

        Log.d(TAG, "Starting member listener for userId: " + currentUserId);

        Notification notification = new NotificationCompat.Builder(this, "service_channel")
                .setContentTitle("Alliance Member Listener")
                .setContentText("Servis radi u pozadini")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .build();

        startForeground(3, notification);

        firebaseRepo = new FirebaseAllianceRepository();
        firebaseRepo.startAllianceMembersListener(currentUserId, lastLogout, this);

        return START_STICKY;
    }

    public void showLeaderNewMemberNotification(String allianceId, String leaderUserId, String newMemberName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Nemamo permission za notifikacije");
                return;
            }
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Novi član u savezu")
                .setContentText(newMemberName + " je pridružen savezu")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        int notificationId = ("leader_notify_" + leaderUserId + "_" + allianceId).hashCode();
        NotificationManagerCompat.from(this).notify(notificationId, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed, member listener removed");
        if (firebaseRepo != null) {
            firebaseRepo.stopMemberListener();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

