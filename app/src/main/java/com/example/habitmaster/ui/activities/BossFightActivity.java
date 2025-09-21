package com.example.habitmaster.ui.activities;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.habitmaster.R;
import com.example.habitmaster.data.dtos.BossFightResult;
import com.example.habitmaster.domain.models.Boss;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.services.BossService;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.services.TaskService;
import com.example.habitmaster.services.UserService;
import com.example.habitmaster.utils.EquipmentDrawableMapper;

import java.text.BreakIterator;
import java.util.Locale;

public class BossFightActivity extends AppCompatActivity {

    private User currentUser;
    private Boss currentBoss;

    private ProgressBar bossHpBar;
    private ProgressBar userPpBar;
    private ImageView activeEquipment;
    private Button attackButton;
    private TextView remainingAttacksText, attackChanceText, bossHpText, userPpText, rewardCoinsText;
    private BossService bossService;
    private double stageSuccessRate;

    private ImageView chestAnimationView, rewardEquipmentIcon;
    private AnimationDrawable chestAnimation;
    private boolean chestAnimationFinished = false;
    private View darkBackground;
    private View rewardLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boss_fight);

        bossHpBar = findViewById(R.id.bossHpBar);
        userPpBar = findViewById(R.id.userPpBar);
        activeEquipment = findViewById(R.id.activeEquipment);
        attackButton = findViewById(R.id.attackButton);
        remainingAttacksText = findViewById(R.id.remainingAttacks);
        attackChanceText = findViewById(R.id.attackChance);
        bossHpText = findViewById(R.id.bossHpText);
        userPpText = findViewById(R.id.userPpText);

        rewardEquipmentIcon = findViewById(R.id.rewardEquipmentIcon);
        rewardCoinsText = findViewById(R.id.rewardCoinsText);
        rewardLayout = findViewById(R.id.rewardLayout);

        bossService = new BossService(this);

        setAttackChance();

        attackButton.setOnClickListener(v -> performAttack());

        chestAnimationView = findViewById(R.id.chestAnimationView);
        chestAnimationView.setBackgroundResource(R.drawable.chest_animation);
        chestAnimation = (AnimationDrawable) chestAnimationView.getBackground();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setAttackChance();
        darkBackground = findViewById(R.id.darkBackground);
    }

    private void setAttackChance() {
        UserService userService = new UserService(this);
        userService.getCurrentUser(new ICallback<User>() {
            @Override
            public void onSuccess(User result) {
                currentUser = result;
                TaskService taskService = new TaskService(BossFightActivity.this);
                stageSuccessRate = taskService.getUserStageSuccessRate(currentUser.getId());
                attackChanceText.setText(String.format(Locale.US, "%.2f%%", stageSuccessRate * 100));
                loadBoss();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(BossFightActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBoss() {
        bossService.getBossByUserId(currentUser.getId(), new ICallback<Boss>() {
            @Override
            public void onSuccess(Boss result) {
                currentBoss = result;
                updateUI();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(BossFightActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if(currentBoss != null) {
            var maxHp = currentBoss.getMaxHp();
            var currentHp = currentBoss.getCurrentHp();

            bossHpBar.setMax((int) maxHp);
            bossHpBar.setProgress((int) currentHp);

            userPpBar.setMax(currentUser.getPowerPoints());
            userPpBar.setProgress(currentUser.getPowerPoints());

            bossHpText.setText(String.format("%s/%s HP", (int) currentHp, (int) maxHp));
            userPpText.setText(String.format("%s PP", currentUser.getPowerPoints()));

            // TODO: postavi sliku aktivne opreme ako postoji
            // activeEquipment.setImageResource(...);

            remainingAttacksText.setText(String.format("%d/5", currentBoss.getRemainingAttacks()));
        }
    }

    private void performAttack() {
        if(!currentBoss.canAttack()) {
            Toast.makeText(this, "No attacks left!", Toast.LENGTH_SHORT).show();
            playBossAnimation(false);
            return;
        }

        bossService.attackBoss(currentUser.getId(), currentUser.getPowerPoints(), new ICallback<BossFightResult>() {
            @Override
            public void onSuccess(BossFightResult result) {
                runOnUiThread(() -> {
                    currentBoss = result.getBoss();
                    updateUI();

                    if (result.isSuccess()) {
                        playBossAnimation(true);
                        var rewardedEquipment = result.getRewardedEquipment();
                        var rewardCoins = result.getBoss().getRewardCoins();
                        if (currentBoss.getCurrentHp() == 0) {
                            Integer equipmentIcon;
                            if (rewardedEquipment != null) {
                                equipmentIcon = EquipmentDrawableMapper.getAvatarResId(rewardedEquipment.getEquipmentId());
                                chestAnimationView.setOnClickListener(v -> showChestAnimationAndReward((int) rewardCoins, equipmentIcon));
                                darkBackground.setOnClickListener(v -> showChestAnimationAndReward((int) rewardCoins, equipmentIcon));
                            } else {
                                equipmentIcon = null;
                                darkBackground.setOnClickListener(v -> showChestAnimationAndReward((int) rewardCoins, equipmentIcon));
                                darkBackground.setOnClickListener(v -> showChestAnimationAndReward((int) rewardCoins, equipmentIcon));
                            }

                            darkBackground.setVisibility(View.VISIBLE);
                            chestAnimationView.setVisibility(View.VISIBLE);

                            Toast.makeText(
                                    BossFightActivity.this,
                                    "Boss defeated! Tap the chest!",
                                    Toast.LENGTH_SHORT
                            ).show();
                            attackButton.setEnabled(false);
                        }
                    } else {
                        playBossAnimation(false);
                        Toast.makeText(BossFightActivity.this, "Attack missed", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> Toast.makeText(BossFightActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void playBossAnimation(boolean hit) {
        ImageView bossImage = findViewById(R.id.bossImage);

        if(hit) {
            bossImage.setImageResource(R.drawable.boss_hit_animation);
        } else {
            bossImage.setImageResource(R.drawable.boss_miss_animation);
        }

        AnimationDrawable animationDrawable = (AnimationDrawable) bossImage.getDrawable();
        animationDrawable.start();
    }

    private void showChestAnimationAndReward(int coins, @Nullable Integer equipmentIconResId) {
        AnimationDrawable chestAnimation = (AnimationDrawable) chestAnimationView.getBackground();

        if (!chestAnimation.isRunning() && !chestAnimationFinished) {
            chestAnimationView.setVisibility(View.VISIBLE);
            darkBackground.setVisibility(View.VISIBLE);
            rewardLayout.setVisibility(View.GONE);

            final int totalFrames = chestAnimation.getNumberOfFrames();
            final Handler handler = new Handler(Looper.getMainLooper());

            Runnable frameRunnable = new Runnable() {
                int currentFrame = 0;

                @Override
                public void run() {
                    chestAnimation.selectDrawable(currentFrame);

                    if (currentFrame == 3) {
                        rewardLayout.setVisibility(View.VISIBLE);
                        rewardCoinsText.setText(coins + " coins");
                        rewardCoinsText.setVisibility(View.VISIBLE);

                        if (equipmentIconResId != null) {
                            rewardEquipmentIcon.setImageResource(equipmentIconResId);
                            rewardEquipmentIcon.setVisibility(View.VISIBLE);
                        } else {
                            rewardEquipmentIcon.setVisibility(View.GONE);
                        }
                    }

                    currentFrame++;
                    if (currentFrame < totalFrames) {
                        handler.postDelayed(this, chestAnimation.getDuration(currentFrame - 1));
                    } else {
                        chestAnimationFinished = true;
                    }
                }
            };

            handler.post(frameRunnable);
        } else if (chestAnimationFinished) {
            chestAnimationView.setVisibility(View.GONE);
            darkBackground.setVisibility(View.GONE);
            rewardCoinsText.setVisibility(View.GONE);
            rewardLayout.setVisibility(View.GONE);
            rewardEquipmentIcon.setVisibility(View.GONE);
            chestAnimationFinished = false;
        }
    }

}