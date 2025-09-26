package com.example.habitmaster.domain.usecases.bosses;

import android.content.Context;
import android.util.Log;

import com.example.habitmaster.data.firebases.FirebaseBossRepository;
import com.example.habitmaster.data.repositories.BossRepository;
import com.example.habitmaster.domain.models.BattleStatsBoost;
import com.example.habitmaster.domain.models.Boss;
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

    public void getBossByUserId(String userId, int userLevel, ICallback<Boss> callback) {
        AtomicBoolean callbackCalled = new AtomicBoolean(false);
        AtomicBoolean bossCreationInProgress = new AtomicBoolean(false);

        try {
            Boss localBoss = localRepo.findByUserId(userId);
            if (localBoss != null && !localBoss.isDefeated()) {
                callbackCalled.set(true);
                callback.onSuccess(localBoss);
                return;
            }

            // Helper funkcija da insertuje novog bossa i pozove callback samo jednom
            Consumer<Integer> createNewBoss = (level) -> {
                if (bossCreationInProgress.compareAndSet(false, true)) {
                    insertNewBoss(userId, level, new ICallback<Boss>() {
                        @Override
                        public void onSuccess(Boss result) {
                            if (callbackCalled.compareAndSet(false, true)) {
                                callback.onSuccess(result);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            if (callbackCalled.compareAndSet(false, true)) {
                                callback.onError(errorMessage);
                            }
                        }
                    });
                }
            };

            if (localBoss != null && localBoss.isDefeated()) {
                if (userLevel >= localBoss.getLevel() + 1) {
                    createNewBoss.accept(localBoss.getLevel() + 1);
                    return;
                } else {
                    callback.onError("User not ready for next boss");
                    return;
                }
            }

            // Nema lokalnog, proveri Firebase
            remoteRepo.findByUserId(userId, new ICallback<Boss>() {
                @Override
                public void onSuccess(Boss bossFromFirebase) {
                    if (bossFromFirebase != null && !bossFromFirebase.isDefeated()) {
                        localRepo.insert(bossFromFirebase);
                        if (callbackCalled.compareAndSet(false, true)) {
                            callback.onSuccess(bossFromFirebase);
                        }
                    } else if (bossFromFirebase != null && bossFromFirebase.isDefeated()) {
                        if (userLevel >= bossFromFirebase.getLevel() + 1) {
                            createNewBoss.accept(bossFromFirebase.getLevel() + 1);
                        } else {
                            if (callbackCalled.compareAndSet(false, true)) {
                                callback.onError("User not ready for next boss");
                            }
                        }
                    } else {
                        createNewBoss.accept(1); // prvi boss
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    createNewBoss.accept(1); // prvi boss ako Firebase failuje
                }
            });

        } catch (Exception e) {
            if (callbackCalled.compareAndSet(false, true)) {
                callback.onError(e.getMessage());
            }
        }
    }

    

    public void insertNewBoss(String userId, int level, ICallback<Boss> callback) {
        Boss newBoss = new Boss(UUID.randomUUID().toString(), userId, level);

        userEquipmentService.getAllUserEquipment(userId, new ICallback<List<UserEquipment>>() {
            @Override
            public void onSuccess(List<UserEquipment> result) {
                List<UserEquipment> activatedList = result.stream()
                        .filter(UserEquipment::isActivated)
                        .collect(Collectors.toList());

                BattleStatsBoost currentBoost = equipmentEffectService.calculateEffects(activatedList);

                int extraAttack = calculateExtraAttack(currentBoost.extraAttackRolls);

                // Coins boost
                int boostedCoins = (int) (newBoss.getRewardCoins() * (1 + currentBoost.coinsIncrease));
                newBoss.setRewardCoins(boostedCoins);

                // Attacks boost
                int maxAttacks = newBoss.getMaxAttacks() + extraAttack;
                newBoss.setMaxAttacks(maxAttacks);
                newBoss.setRemainingAttacks(maxAttacks);

                localRepo.insert(newBoss);
                remoteRepo.insert(newBoss);
                Log.d("PRAV BOSS", "NAPRAVIO SAM BOSSA: " +  newBoss.getId());

                callback.onSuccess(newBoss);
            }

            @Override
            public void onError(String errorMessage) {
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
