package com.example.habitmaster.services;

import android.content.Context;

import com.example.habitmaster.data.dtos.BossFightResult;
import com.example.habitmaster.domain.models.Boss;
import com.example.habitmaster.domain.models.UserEquipment;
import com.example.habitmaster.domain.usecases.bosses.AttackBossUseCase;
import com.example.habitmaster.domain.usecases.bosses.GetOrCreateBossUseCase;
import com.example.habitmaster.ui.activities.BossFightActivity;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BossService {
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final GetOrCreateBossUseCase getOrCreateBossUseCase;
    private final AttackBossUseCase attackBossUseCase;
    private final TaskService taskService;

    public BossService(Context context) {
        getOrCreateBossUseCase = new GetOrCreateBossUseCase(context);
        attackBossUseCase = new AttackBossUseCase(context);
        taskService = new TaskService(context);
    }
    public void getBossByUserId(String userId, ICallback<Boss> callback) {
        getOrCreateBossUseCase.getBossByUserId(userId, new ICallback<Boss>() {
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

    public void attackBoss(String userId, int powerPoints, ICallback<BossFightResult> callback) {
        executorService.execute(() -> {
            double stageSuccessRate = taskService.getUserStageSuccessRate(userId);

            attackBossUseCase.execute(userId, powerPoints, stageSuccessRate, new ICallback<BossFightResult>() {
                @Override
                public void onSuccess(BossFightResult result) {
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
}
