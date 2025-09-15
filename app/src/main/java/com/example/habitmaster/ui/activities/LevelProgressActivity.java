package com.example.habitmaster.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.domain.models.UserLevelProgress;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.services.UserService;
import com.example.habitmaster.utils.Prefs;

public class LevelProgressActivity extends AppCompatActivity {
    private TextView tvTitle, tvPP, tvCurrentXP, tvRequiredXP;
    private ProgressBar progressBar;
    private UserService userService;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_progress);

        tvTitle = findViewById(R.id.tv_title);
        tvPP = findViewById(R.id.tv_pp);
        tvCurrentXP = findViewById(R.id.tv_current_xp);
        tvRequiredXP = findViewById(R.id.tv_required_xp);
        progressBar = findViewById(R.id.progress_bar);

        userService = new UserService(this);
        prefs =  new Prefs(this);
        loadUserProgress();
    }

    private void loadUserProgress() {
        String uid = prefs.getUid();
        if(uid == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        userService.findUserByUsername(prefs.getUsername(), new ICallback<>() {
            @Override
            public void onSuccess(User user) {
                userService.getUserLevelProgress(new ICallback<>() {
                    @Override
                    public void onSuccess(UserLevelProgress result) {
                        tvTitle.setText(user.getTitle());
                        tvPP.setText(String.valueOf(user.getPowerPoints()));
                        tvCurrentXP.setText(String.valueOf(user.getXp()));
                        String requiredXp = String.valueOf(result.getRequiredXp()) + " XP";
                        tvRequiredXP.setText(requiredXp);

                        int progressPercent = (result.getRequiredXp() > 0) ? (int) ((user.getXp() * 100.0) / result.getRequiredXp()) : 0;
                        progressBar.setProgress(Math.min(progressPercent, 100));
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(LevelProgressActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(LevelProgressActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
