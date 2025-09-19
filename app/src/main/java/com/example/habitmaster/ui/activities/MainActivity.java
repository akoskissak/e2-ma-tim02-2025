package com.example.habitmaster.ui.activities;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
import com.example.habitmaster.utils.Prefs;

public class MainActivity extends AppCompatActivity {
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = new Prefs(this);

        if(prefs.getUid() == null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

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

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            prefs.setUid(null);
            prefs.setUsername(null);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
        Button btnProfile = findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        Button btnMyTasks = findViewById(R.id.btnMyTasks);
        btnMyTasks.setOnClickListener(view -> {
            startActivity(new Intent(this, MyTasksActivity.class));
        });

        Button btnCategories = findViewById(R.id.btnCategories);
        btnCategories.setOnClickListener(v -> {
            startActivity(new Intent(this, CategoriesActivity.class));
        });

        Button btnStatistics = findViewById(R.id.btnStatistics);
        btnStatistics.setOnClickListener(v -> {
            startActivity(new Intent(this, UserStatisticsActivity.class));
        });

        Button btnLevelProgress = findViewById(R.id.btnLevelProgress);
        btnLevelProgress.setOnClickListener(v -> {
            startActivity(new Intent(this, LevelProgressActivity.class));
        });

        Button btnShop = findViewById(R.id.btnShop);
        btnShop.setOnClickListener(v -> {
            startActivity(new Intent(this, ShopActivity.class));
        });

        Button btnInventory = findViewById(R.id.btnInventory);
        btnInventory.setOnClickListener(v -> {
            startActivity(new Intent(this, InventoryActivity.class));
        });

        Button btnFriend = findViewById(R.id.btnFriend);
        btnFriend.setOnClickListener(v -> {
            startActivity(new Intent(this, FriendActivity.class));
        });

        Button btnAlliance = findViewById(R.id.btnAlliance);
        btnAlliance.setOnClickListener(v -> {
            startActivity(new Intent(this, AllianceActivity.class));
        });

        Button btnFollowRequests = findViewById(R.id.btnFollowRequests);
        btnFollowRequests.setOnClickListener(v -> {
            startActivity(new Intent(this, FollowRequestsActivity.class));
        });
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
        NotificationChannel channel = new NotificationChannel(
                "alliance_channel",
                "Alliance Notifications",
                NotificationManager.IMPORTANCE_HIGH
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }
}