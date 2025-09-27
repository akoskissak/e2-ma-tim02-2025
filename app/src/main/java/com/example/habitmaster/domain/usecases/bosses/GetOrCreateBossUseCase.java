package com.example.habitmaster.domain.usecases.bosses;

import android.content.Context;
import android.util.Log;

import com.example.habitmaster.data.firebases.FirebaseBossRepository;
import com.example.habitmaster.data.repositories.BossRepository;
import com.example.habitmaster.domain.models.BattleStatsBoost;
import com.example.habitmaster.domain.models.Boss;
import com.example.habitmaster.domain.models.BossStatus;
import com.example.habitmaster.domain.models.UserEquipment;
import com.example.habitmaster.services.EquipmentEffectService;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.services.UserEquipmentService;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GetOrCreateBossUseCase {
    private final BossRepository localRepo;
    private final FirebaseBossRepository remoteRepo;
    private final EquipmentEffectService equipmentEffectService;
    private final UserEquipmentService userEquipmentService;

    public GetOrCreateBossUseCase(Context context) {
        localRepo = new BossRepository(context);
        remoteRepo = new FirebaseBossRepository();
        equipmentEffectService = new EquipmentEffectService();
        userEquipmentService = new UserEquipmentService(context);
    }

    public void execute(String userId, int userLevel, ICallback<Boss> callback) {
        var boss = localRepo.findMaxBossByUserId(userId);

        if (boss == null) {
            // No boss -> Create first boss
            if (userLevel > 0) {
                insertNewBoss(userId, userLevel, new ICallback<Boss>() {
                    @Override
                    public void onSuccess(Boss newBoss) {
                        Log.d("GET BOSS", "new boss: ");
                        callback.onSuccess(newBoss);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.d("GET BOSS", "error creating first boss: " + errorMessage);
                        callback.onError(errorMessage);
                    }
                });
            } else {
                callback.onError("You are not ready for boss fight");
            }
        } else {
            if (boss.isDefeated()) {
                // Defeated -> create new if user is ready
                if (userLevel > boss.getLevel()) {
                    insertNewBoss(userId, userLevel, new ICallback<Boss>() {
                        @Override
                        public void onSuccess(Boss newBoss) {
                            Log.d("GET BOSS", "new boss (last defeated)");
                            callback.onSuccess(newBoss);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.d("GET BOSS", "last defeated -> new failed: " + errorMessage);
                            callback.onError(errorMessage);
                        }
                    });
                } else {
                    callback.onError("You are not ready for boss fight");
                }
            } else if (boss.getStatus() == BossStatus.FAILED) {
                // Failed -> reset boss and return it
                if (userLevel > boss.getLevel()) {
                    Log.d("GET BOSS", "reset boss");
                    boss.reset();
                    localRepo.update(boss);
                    remoteRepo.update(boss);

                    callback.onSuccess(boss);
                } else {
                    callback.onError("You are not ready for boss fight");
                }
            } else if (boss.getStatus() == BossStatus.ONGOING) {
                // Ongoing -> return existing
                Log.d("GET BOSS", "ongoing boss");
                callback.onSuccess(boss);
            }
        }
    }
    

    public void insertNewBoss(String userId, int level, ICallback<Boss> callback) {
        Log.d("BossService", "insertNewBoss START, userId=" + userId + ", level=" + level);

        Boss newBoss = new Boss(UUID.randomUUID().toString(), userId, level);

        userEquipmentService.getAllUserEquipment(userId, new ICallback<List<UserEquipment>>() {
            @Override
            public void onSuccess(List<UserEquipment> result) {
                Log.d("BossService", "User equipment loaded, size=" + result.size());

                List<UserEquipment> activatedList = result.stream()
                        .filter(UserEquipment::isActivated)
                        .collect(Collectors.toList());

                BattleStatsBoost currentBoost = equipmentEffectService.calculateEffects(activatedList);
                Log.d("BossService", "Calculated boosts: " + currentBoost);

                int extraAttack = calculateExtraAttack(currentBoost.extraAttackRolls);
                Log.d("BossService", "Extra attack rolls result=" + extraAttack);

                // Coins boost
                int boostedCoins = (int) (newBoss.getRewardCoins() * (1 + currentBoost.coinsIncrease));
                newBoss.setRewardCoins(boostedCoins);

                // Attacks boost
                int maxAttacks = newBoss.getMaxAttacks() + extraAttack;
                newBoss.setMaxAttacks(maxAttacks);
                newBoss.setRemainingAttacks(maxAttacks);

                Log.d("BossService", "Final newBoss before save: " + newBoss);


                localRepo.insert(newBoss);
                remoteRepo.insert(newBoss);

                callback.onSuccess(newBoss);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("BossService", "Error fetching user equipment: " + errorMessage);

                callback.onError(errorMessage);
            }
        });
    }

    private int calculateExtraAttack(int extraAttackRolls) {
        Random random = new Random();
        int extraAttack = 0;

        for (int i = 0; i < extraAttackRolls; i++) {
            int chance = random.nextInt(100);
            if (chance < 40) {
                extraAttack++;
            }
        }

        return extraAttack;
    }

}
