package com.example.habitmaster.services;

import android.Manifest;
import android.app.Notification;
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

import com.example.habitmaster.data.firebases.FirebaseAllianceRepository;
import com.example.habitmaster.ui.activities.AllianceActivity;

public class AllianceChatListenerService extends Service {
    private String currentUserId;
    private static final String TAG = "AllianceChatService";
    private static final String CHANNEL_ID = "alliance_channel";
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

        Log.d(TAG, "Starting chat listener for userId: " + currentUserId);

        Notification notification = new NotificationCompat.Builder(this, "service_channel")
                .setContentTitle("Alliance Chat Listener")
                .setContentText("Servis radi u pozadini")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .build();

        startForeground(2, notification);

        firebaseRepo = new FirebaseAllianceRepository();
        firebaseRepo.startChatListener(currentUserId, this);

        return START_STICKY;
    }

    public void showMessageNotification(String messageId, String fromUsername, String messageText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Nemamo permission za notifikacije");
                return;
            }
        }

        Intent openChatIntent = new Intent(this, AllianceActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, messageId.hashCode(), openChatIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle("Poruka od " + fromUsername)
                .setContentText(messageText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat.from(this).notify(messageId.hashCode(), notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed, chat listener removed");
        if (firebaseRepo != null) {
            firebaseRepo.stopChatListener();
        }
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
