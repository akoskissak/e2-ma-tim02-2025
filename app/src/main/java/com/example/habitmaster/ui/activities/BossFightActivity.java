package com.example.habitmaster.ui.activities;

import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitmaster.R;
import com.example.habitmaster.data.dtos.BossFightResult;
import com.example.habitmaster.domain.models.BattleStatsBoost;
import com.example.habitmaster.domain.models.Boss;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.domain.models.UserEquipment;
import com.example.habitmaster.services.BossService;
import com.example.habitmaster.services.EquipmentEffectService;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.services.TaskService;
import com.example.habitmaster.services.UserEquipmentService;
import com.example.habitmaster.services.UserService;
import com.example.habitmaster.ui.adapters.ActiveEquipmentAdapter;
import com.example.habitmaster.utils.EquipmentDrawableMapper;
import com.example.habitmaster.utils.ShakeDetector;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class BossFightActivity extends AppCompatActivity {

    private User currentUser;
    private Boss currentBoss;

    private ProgressBar bossHpBar;
    private ProgressBar userPpBar;
    private ImageView activeEquipment;
    private Button attackButton;
    private TextView remainingAttacksText, attackChanceText, bossHpText, userPpText, rewardCoinsText;
    private BossService bossService;
    private BattleStatsBoost currentBoost;
    private EquipmentEffectService effectService;

    private double stageSuccessRate;

    private ImageView chestAnimationView, rewardEquipmentIcon;
    private boolean chestAnimationFinished = false;
    private View darkBackground;
    private View rewardLayout;
    private TextView tvEquipmentTitle;

    private RecyclerView activeEquipmentRecyclerView;
    private ActiveEquipmentAdapter activeEquipmentAdapter;
    private List<UserEquipment> activeEquipmentList = new ArrayList<>();

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ShakeDetector shakeDetector;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boss_fight);

        bossHpBar = findViewById(R.id.bossHpBar);
        userPpBar = findViewById(R.id.userPpBar);
        attackButton = findViewById(R.id.attackButton);
        remainingAttacksText = findViewById(R.id.remainingAttacks);
        attackChanceText = findViewById(R.id.attackChance);
        bossHpText = findViewById(R.id.bossHpText);
        userPpText = findViewById(R.id.userPpText);

        activeEquipmentRecyclerView = findViewById(R.id.activeEquipmentRecyclerView);
        activeEquipmentRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        activeEquipmentAdapter = new ActiveEquipmentAdapter(activeEquipmentList, new UserEquipmentService(this), null);
        activeEquipmentRecyclerView.setAdapter(activeEquipmentAdapter);
        tvEquipmentTitle = findViewById(R.id.tvEquipmentTitle);

        rewardEquipmentIcon = findViewById(R.id.rewardEquipmentIcon);
        rewardCoinsText = findViewById(R.id.rewardCoinsText);
        rewardLayout = findViewById(R.id.rewardLayout);

        bossService = new BossService(this);
        effectService = new EquipmentEffectService();
        currentBoost = new BattleStatsBoost();

        setAttackChance();

        attackButton.setOnClickListener(v -> performAttack());


        chestAnimationView = findViewById(R.id.chestAnimationView);
        chestAnimationView.setBackgroundResource(R.drawable.chest_animation);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeDetector = new ShakeDetector();

        shakeDetector.setOnShakeListener(this::performAttack);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAttackChance();
        darkBackground = findViewById(R.id.darkBackground);

        sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(shakeDetector);
    }

    private void setAttackChance() {
        UserService userService = new UserService(this);
        userService.getCurrentUser(new ICallback<User>() {
            @Override
            public void onSuccess(User result) {
                currentUser = result;
                loadBoss(result.getLevel());

                TaskService taskService = new TaskService(BossFightActivity.this);
                stageSuccessRate = taskService.getUserStageSuccessRate(currentUser.getId());
                stageSuccessRate = stageSuccessRate + currentBoost.attackChanceIncrease;
                attackChanceText.setText(String.format(Locale.US, "%.2f%%", stageSuccessRate * 100));
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(BossFightActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBoss(int userLevel) {
        bossService.getBossByUserId(currentUser.getId(), userLevel, new ICallback<Boss>() {
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

            bossHpText.setText(String.format("%s/%s HP", (int) currentHp, (int) maxHp));

            UserEquipmentService equipmentService = new UserEquipmentService(this);
            equipmentService.getAllUserEquipment(currentUser.getId(), new ICallback<List<UserEquipment>>() {
                @Override
                public void onSuccess(List<UserEquipment> result) {
                    List<UserEquipment> activatedList = result.stream()
                            .filter(UserEquipment::isActivated)
                            .collect(Collectors.toList());

                    activeEquipmentList.clear();
                    activeEquipmentList.addAll(activatedList);
                    activeEquipmentAdapter.notifyDataSetChanged();

                    currentBoost = effectService.calculateEffects(activatedList);

                    int boostedPP = (int) currentBoost.calculateFinalPP(currentUser.getPowerPoints());
                    userPpBar.setMax(boostedPP);
                    userPpBar.setProgress(boostedPP);
                    userPpText.setText(String.format("%s PP", boostedPP));

                    if (activatedList.isEmpty()) {
                        tvEquipmentTitle.setText("No active equipment");
                        activeEquipmentRecyclerView.setVisibility(View.GONE);
                    } else {
                        tvEquipmentTitle.setText("Active equipments");
                        activeEquipmentRecyclerView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(BossFightActivity.this, "Error loading active equipment", Toast.LENGTH_SHORT).show();
                    tvEquipmentTitle.setText("No active equipment");
                }
            });

            remainingAttacksText.setText(
                    String.format("%d/%d", currentBoss.getMaxAttacks(), currentBoss.getRemainingAttacks())
            );
        }
    }

    private void performAttack() {
        if(!currentBoss.canAttack()) {
            Toast.makeText(this, "No attacks left!", Toast.LENGTH_SHORT).show();
            return;
        }

        bossService.attackBoss(currentUser.getId(), currentUser.getPowerPoints(), currentBoost.attackChanceIncrease, new ICallback<BossFightResult>() {
            @Override
            public void onSuccess(BossFightResult result) {
                runOnUiThread(() -> {
                    currentBoss = result.getBoss();
                    updateUI();

                    if (result.isSuccess()) {
                        playBossAnimation(true);
                        if (currentBoss.getCurrentHp() == 0) {
                            updateUIOnBossFightEnd(result, "Boss defeated! Tap the chest or shake!");
                        } else if (currentBoss.isHalfDefeated()) {
                            getHalfBossReward(currentUser.getId(), currentBoss);
                        }
                    } else {
                        if (currentBoss.isHalfDefeated()) {
                            getHalfBossReward(currentUser.getId(), currentBoss);
                        }
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

    private void getHalfBossReward(String userId, Boss boss) {
        bossService.getBossReward(currentUser.getId(), currentBoss, new ICallback<BossFightResult>() {
            @Override
            public void onSuccess(BossFightResult result) {
                runOnUiThread(() -> updateUIOnBossFightEnd(result, "Fight ended! Tap the chest or shake!"));
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> Toast.makeText(BossFightActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateUIOnBossFightEnd(BossFightResult result, String toastText) {
        var rewardedEquipment = result.getRewardedEquipment();
        var baseCoins = (int) result.getBoss().getRewardCoins();

        int boostedCoins = (int) (baseCoins * (1 + currentBoost.coinsIncrease));

        if (rewardedEquipment != null) {
            Integer equipmentIcon = EquipmentDrawableMapper.getAvatarResId(rewardedEquipment.getEquipmentId());
            setOnClickListeners(boostedCoins, equipmentIcon);
        } else {
            setOnClickListeners(boostedCoins, null);
        }

        darkBackground.setVisibility(View.VISIBLE);
        chestAnimationView.setVisibility(View.VISIBLE);

        Toast.makeText(
                BossFightActivity.this,
                toastText,
                Toast.LENGTH_SHORT
        ).show();
        attackButton.setEnabled(false);
    }

    private void setOnClickListeners(int rewardCoins, @Nullable Integer equipmentIcon) {
        chestAnimationView.setOnClickListener(v -> showChestAnimationAndReward(rewardCoins, equipmentIcon));
        darkBackground.setOnClickListener(v -> showChestAnimationAndReward(rewardCoins, equipmentIcon));
        shakeDetector.setOnShakeListener(() -> { showChestAnimationAndReward(rewardCoins, equipmentIcon); });
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