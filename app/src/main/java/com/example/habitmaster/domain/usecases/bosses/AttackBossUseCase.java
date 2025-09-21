package com.example.habitmaster.domain.usecases.bosses;

import android.content.Context;
import android.util.Log;

import com.example.habitmaster.data.dtos.BossFightResult;
import com.example.habitmaster.data.firebases.FirebaseBossRepository;
import com.example.habitmaster.data.repositories.BossRepository;
import com.example.habitmaster.domain.models.Boss;
import com.example.habitmaster.domain.models.Equipment;
import com.example.habitmaster.domain.models.Shop;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.domain.models.UserEquipment;
import com.example.habitmaster.domain.usecases.GetUserByIdUseCase;
import com.example.habitmaster.domain.usecases.UpdateUserCoinsUseCase;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.services.UserEquipmentService;

import java.util.Random;

public class AttackBossUseCase {
    private final BossRepository localRepo;
    private final FirebaseBossRepository remoteRepo;
    private final UpdateUserCoinsUseCase updateUserCoinsUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UserEquipmentService userEquipmentService;

    public AttackBossUseCase(Context context) {
        this.localRepo = new BossRepository(context);
        this.remoteRepo = new FirebaseBossRepository();
        this.getUserByIdUseCase = new GetUserByIdUseCase(context);
        this.updateUserCoinsUseCase = new UpdateUserCoinsUseCase(context);
        this.userEquipmentService = new UserEquipmentService(context);
    }

    public void execute(String userId, int powerPoints, double stageSuccessRate, ICallback<BossFightResult> callback) {
        try {
            Boss boss = localRepo.findByUserId(userId);

            if (boss != null && boss.canAttack()) {
                Random random = new Random();
                double roll = random.nextDouble(); // [0.0, 1.0)

                if (roll > stageSuccessRate) {
                    boss.setRemainingAttacks(boss.getRemainingAttacks() - 1);
                    localRepo.update(boss);
                    remoteRepo.update(boss);
                    callback.onSuccess(new BossFightResult(boss, null, false));
                    return;
                }

                boss.attack(powerPoints);
                localRepo.update(boss);
                remoteRepo.update(boss);
                Log.d("BOSS_FIGHT", "Boss attacked");

                if (boss.isDefeated()) {
                    Log.d("BOSS_FIGHT", "Boss defeated");
                    getBossReward(userId, boss, new ICallback<UserEquipment>() {
                        @Override
                        public void onSuccess(UserEquipment rewardedEquipment) {
                            Log.d("BOSS_FIGHT", "Boss currentHp: " + boss.getCurrentHp());
                            callback.onSuccess(new BossFightResult(boss, rewardedEquipment, true));
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.d("BOSS_FIGHT", "onError: " + errorMessage);
                            callback.onError(errorMessage);
                        }
                    });
                } else {
                    callback.onSuccess(new BossFightResult(boss, null, true));
                }

            } else {
                callback.onError("Attack failed: no more remaining attacks");
            }
        } catch (Exception e) {
            callback.onError("Attack failed: " + e.getMessage());
        }
    }

    private void getBossReward(String userId, Boss boss, ICallback<UserEquipment> callback) {
        getUserByIdUseCase.execute(userId, new ICallback<User>() {
            @Override
            public void onSuccess(User user) {
                user.setCoins((int) (user.getCoins() + boss.getRewardCoins()));
                updateUserCoinsUseCase.execute(userId, user.getCoins());

                Random random = new Random();
                double roll = random.nextDouble();

                if (roll <= boss.getEquipmentRewardChance()) {
                    roll = random.nextDouble();
                    if (roll <= 0.95) {
                        // Add armor
                        String armorId = random.nextDouble() <= 0.33 ? Shop.GLOVES_ID :
                                roll <= 0.67 ? Shop.SHIELD_ID : Shop.BOOTS_ID;

                        userEquipmentService.addRewardedArmor(userId, armorId, new ICallback<UserEquipment>() {
                            @Override
                            public void onSuccess(UserEquipment rewardedEquipment) {
                                callback.onSuccess(rewardedEquipment);
                            }

                            @Override
                            public void onError(String errorMessage) {
                                callback.onError(errorMessage);
                            }
                        });
                    } else {
                        // Add weapon
                        Equipment weapon = random.nextDouble() <= 0.5 ? Shop.Sword : Shop.BowAndArrow;

                        userEquipmentService.addRewardedWeapon(userId, weapon, new ICallback<UserEquipment>() {
                            @Override
                            public void onSuccess(UserEquipment rewardedWeapon) {
                                callback.onSuccess(rewardedWeapon);
                            }

                            @Override
                            public void onError(String errorMessage) {
                                callback.onError(errorMessage);
                            }
                        });
                    }
                }

                callback.onSuccess(null);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError("Error: " + errorMessage);
            }
        });
    }
}
