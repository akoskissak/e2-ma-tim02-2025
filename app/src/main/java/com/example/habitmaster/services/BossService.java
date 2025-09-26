package com.example.habitmaster.services;

import android.content.Context;

import com.example.habitmaster.data.dtos.BossFightResult;
import com.example.habitmaster.domain.models.AllianceMissionProgressType;
import com.example.habitmaster.domain.models.Boss;
import com.example.habitmaster.domain.models.UserEquipment;
import com.example.habitmaster.domain.usecases.bosses.AttackBossUseCase;
import com.example.habitmaster.domain.usecases.bosses.GetOrCreateBossUseCase;
import com.example.habitmaster.domain.usecases.bosses.UpdateBossStatsUseCase;
import com.example.habitmaster.ui.activities.BossFightActivity;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BossService {
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final GetOrCreateBossUseCase getOrCreateBossUseCase;
    private final AttackBossUseCase attackBossUseCase;
    private final TaskService taskService;
    private final AllianceService allianceService;
    private final UpdateBossStatsUseCase updateBossStatsUseCase;

    public BossService(Context context) {
        this.getOrCreateBossUseCase = new GetOrCreateBossUseCase(context);
        this.attackBossUseCase = new AttackBossUseCase(context);
        this.taskService = new TaskService(context);
        this.allianceService = new AllianceService(context);
        this.updateBossStatsUseCase = new UpdateBossStatsUseCase(context);
    }
    public void getBossByUserId(String userId, int userLevel, ICallback<Boss> callback) {
        getOrCreateBossUseCase.getBossByUserId(userId, userLevel, new ICallback<Boss>() {
            @Override
            public void onSuccess(Boss boss) {
                callback.onSuccess(boss);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    public void attackBoss(String userId, int powerPoints, double attackChanceIncrease, ICallback<BossFightResult> callback) {
        executorService.execute(() -> {
            double stageSuccessRate = taskService.getUserStageSuccessRate(userId);
            stageSuccessRate = stageSuccessRate + attackChanceIncrease;

            attackBossUseCase.execute(userId, powerPoints, stageSuccessRate, new ICallback<BossFightResult>() {
                @Override
                public void onSuccess(BossFightResult result) {
                    allianceService.tryUpdateAllianceProgress(userId, AllianceMissionProgressType.BOSS_FIGHT_HIT);
                    callback.onSuccess(result);
                }

                @Override
                public void onError(String errorMessage) {
                    callback.onError(errorMessage);
                }
            });
        });
    }

    public void getBossReward(String userId, Boss boss, ICallback<BossFightResult> callback) {
        executorService.execute(() -> {
            attackBossUseCase.getBossReward(userId, boss, new ICallback<UserEquipment>() {
                @Override
                public void onSuccess(UserEquipment rewardedEquipment) {
                    var bossFightResult = new BossFightResult(boss, rewardedEquipment, false);
                    callback.onSuccess(bossFightResult);
                }

                @Override
                public void onError(String errorMessage) {
                    callback.onError(errorMessage);
                }
            });
        });
    }

    public void updateBossStats(String bossId, int remainingAttacks, int rewardCoins) {
        updateBossStatsUseCase.execute(bossId, remainingAttacks, rewardCoins);
    }
}
