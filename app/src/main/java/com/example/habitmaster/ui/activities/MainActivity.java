package com.example.habitmaster.ui.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.habitmaster.R;
import com.example.habitmaster.data.database.DatabaseHelper;
import com.example.habitmaster.utils.Prefs;

public class MainActivity extends AppCompatActivity {
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

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            prefs.setUid(null);
            prefs.setEmail(null);
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
    }
}