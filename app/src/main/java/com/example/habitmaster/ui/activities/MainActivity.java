package com.example.habitmaster.ui.activities;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.habitmaster.R;
import com.example.habitmaster.data.database.DatabaseHelper;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.services.AllianceChatListenerService;
import com.example.habitmaster.services.AllianceInviteListenerService;
import com.example.habitmaster.services.AllianceMemberListenerService;
import com.example.habitmaster.services.AllianceService;
import com.example.habitmaster.data.seed.DataSeeder;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.services.TaskService;
import com.example.habitmaster.services.UserEquipmentService;
import com.example.habitmaster.services.UserService;
import com.example.habitmaster.utils.Prefs;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    private Prefs prefs;
    private ImageView imgBossFight, imgShop;
    private UserService userService;
    private ImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = new Prefs(this);

        DataSeeder seeder = new DataSeeder(this);
        if (prefs.getUid() == null) {
            seeder.runSeedIfNeeded();

            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        if (prefs.getUsername().startsWith("demo")) {
            seeder.runTaskSeedIfNeeded();
        }

        TaskService taskService = new TaskService(this);
        taskService.checkMissedTasks(prefs.getUid());

        userService = new UserService(this);

        // da uvek bude mode day
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // zbog pregleda baze
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // provera permission-a za notifikacije
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_REQUEST_CODE
            );
        }
        createNotificationChannel();

        Intent serviceIntent = new Intent(this, AllianceInviteListenerService.class);
        serviceIntent.putExtra("extra_current_user_id", prefs.getUid());
        serviceIntent.putExtra("extra_last_logout", prefs.getLastLogout());
        ContextCompat.startForegroundService(this, serviceIntent);

        Intent MessageServiceIntent = new Intent(this, AllianceChatListenerService.class);
        MessageServiceIntent.putExtra("extra_current_user_id", prefs.getUid());
        MessageServiceIntent.putExtra("extra_last_logout", prefs.getLastLogout());
        ContextCompat.startForegroundService(this, MessageServiceIntent);

        Intent MemberServiceIntent = new Intent(this, AllianceMemberListenerService.class);
        MemberServiceIntent.putExtra("extra_current_user_id", prefs.getUid());
        MemberServiceIntent.putExtra("extra_last_logout", prefs.getLastLogout());
        ContextCompat.startForegroundService(this, MemberServiceIntent);

        ImageView imgLogout = findViewById(R.id.imgLogout);
        imgLogout.setOnClickListener(v -> {
            Intent stopIntent = new Intent(this, AllianceChatListenerService.class);
            stopService(stopIntent);

            Intent stopMemberIntent = new Intent(this, AllianceMemberListenerService.class);
            stopService(stopMemberIntent);

            Intent stopInviteIntent = new Intent(this, AllianceInviteListenerService.class);
            stopService(stopInviteIntent);

            userService.setLastLogout(prefs.getUid());
            prefs.setUid(null);
            prefs.setUsername(null);
            prefs.setLastLogout(System.currentTimeMillis());
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });
        getProfileImage();

        ImageView btnMyTasks = findViewById(R.id.btnMyTasks);
        btnMyTasks.setOnClickListener(view -> {
            startActivity(new Intent(this, MyTasksActivity.class));
        });

        ImageView imgStatistics = findViewById(R.id.imgStatistics);
        imgStatistics.setOnClickListener(v -> {
            startActivity(new Intent(this, UserStatisticsActivity.class));
        });

        ImageView imgLevelProgress = findViewById(R.id.imgLevelProgress);
        imgLevelProgress.setOnClickListener(v -> {
            startActivity(new Intent(this, LevelProgressActivity.class));
        });

        imgShop = findViewById(R.id.imgShop);
        imgShop.setOnClickListener(v -> {
            startActivity(new Intent(this, ShopActivity.class));
        });

        ImageView imgInventory = findViewById(R.id.imgInventory);
        imgInventory.setOnClickListener(v -> {
            startActivity(new Intent(this, InventoryActivity.class));
        });

        ImageView imgFriend = findViewById(R.id.imgFriend);
        imgFriend.setOnClickListener(v -> {
            startActivity(new Intent(this, FriendActivity.class));
        });

        ImageView imgAlliance = findViewById(R.id.imgAlliance);
        imgAlliance.setOnClickListener(v -> {
            startActivity(new Intent(this, AllianceActivity.class));
        });

        ImageView imgFollowRequests = findViewById(R.id.imgFollowRequests);
        imgFollowRequests.setOnClickListener(v -> {
            startActivity(new Intent(this, FollowRequestsActivity.class));
        });

        imgBossFight = findViewById(R.id.imgBossFight);
        imgBossFight.setOnClickListener(v -> {
            startActivity(new Intent(this, BossFightActivity.class));
        });
    }

    private void getProfileImage() {
        userService.getCurrentUser(new ICallback<User>() {
            @Override
            public void onSuccess(User currentUser) {
                int resId = getResources().getIdentifier(
                        currentUser.getAvatarName(), "drawable", getPackageName()
                );
                imgProfile.setImageResource(resId);
            }

            @Override
            public void onError(String errorMessage) {
                Log.d("GET PROFILE IMAGE", errorMessage);
                imgProfile.setImageResource(R.drawable.default_avatar);
            }
        });
    }

    private int getProfileImageResource(String avatarName) {
        Log.d("AAA", "getProfileImageResource: ");
        switch (avatarName) {
            case "avatar1":
                return R.drawable.avatar1;
            case "avatar2":
                return R.drawable.avatar2;
            case "avatar3":
                return R.drawable.avatar3;
            case "avatar4":
                return R.drawable.avatar4;
            case "avatar5":
                return R.drawable.avatar5;
            default:
                return R.drawable.default_avatar;
        }
    }

    private void checkIsAllianceMissionFinished() {
        AllianceService allianceService = new AllianceService(this);
        allianceService.checkIsMissionFinishedAndAddCoinsAndBadges(prefs.getUid(), new ICallback<List<String>>() {
            @Override
            public void onSuccess(List<String> allMembers) {
                var userEquipmentService = new UserEquipmentService(MainActivity.this);
                userEquipmentService.addMissionRewards(allMembers);
            }

            @Override
            public void onError(String errorMessage) {
                Log.d("ALLIANCE MISSION FINISHED", "msg: " + errorMessage);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        userService.getUserLevel(prefs.getUid(), new ICallback<Integer>() {
            @Override
            public void onSuccess(Integer userLevel) {
                Log.d("USER_LEVEL", "user level: " + userLevel);
                if (userLevel > 0) {
                    // TODO: Dodati da ako sledeci boss nije dostupan, ne moze da ode na boss activity
                    imgBossFight.setEnabled(true);
                    imgShop.setEnabled(true);
                } else {
                    imgBossFight.setEnabled(false);
                    imgShop.setEnabled(false);
                }
            }

            @Override
            public void onError(String errorMessage) {
                imgBossFight.setEnabled(false);
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        checkIsAllianceMissionFinished();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Dozvola za notifikacije odobrena", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Dozvola za notifikacije odbijena", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                "service_channel",
                "Background Service",
                NotificationManager.IMPORTANCE_MIN
        );
        serviceChannel.setDescription("Tihi kanal za servis");
        serviceChannel.setSound(null, null);
        serviceChannel.enableLights(false);
        serviceChannel.enableVibration(false);

        NotificationChannel allianceChannel = new NotificationChannel(
                "alliance_channel",
                "Alliance Notifications",
                NotificationManager.IMPORTANCE_HIGH
        );
        allianceChannel.setDescription("Pozivi i poruke u savezu");

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
        manager.createNotificationChannel(allianceChannel);
    }

}