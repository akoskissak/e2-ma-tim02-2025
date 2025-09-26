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

        Log.d("BossService", "getBossByUserId START, userId=" + userId + ", userLevel=" + userLevel);

        try {
            Boss localBoss = localRepo.findByUserId(userId);
            Log.d("BossService", "Local boss = " + (localBoss != null ? localBoss.toString() : "null"));

            if (localBoss != null && !localBoss.isDefeated()) {
                Log.d("BossService", "Found undefeated local boss, returning it");

                callbackCalled.set(true);
                callback.onSuccess(localBoss);
                return;
            }

            // Helper funkcija da insertuje novog bossa i pozove callback samo jednom
            Consumer<Integer> createNewBoss = (level) -> {
                Log.d("BossService", "createNewBoss called with level=" + level);

                if (bossCreationInProgress.compareAndSet(false, true)) {
                    Log.d("BossService", "Creating new boss (first time, allowed)");

                    insertNewBoss(userId, level, new ICallback<Boss>() {
                        @Override
                        public void onSuccess(Boss result) {
                            Log.d("BossService", "insertNewBoss SUCCESS, boss=" + result.toString());

                            if (callbackCalled.compareAndSet(false, true)) {
                                callback.onSuccess(result);
                            } else {
                                Log.w("BossService", "insertNewBoss success ignored (callback already called)");

                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e("BossService", "insertNewBoss ERROR: " + errorMessage);

                            if (callbackCalled.compareAndSet(false, true)) {
                                callback.onError(errorMessage);
                            }
                        }
                    });
                } else {
                    Log.w("BossService", "createNewBoss ignored (already in progress)");
                }
            };

            if (localBoss != null && localBoss.isDefeated()) {
                Log.d("BossService", "Local boss is defeated, checking level requirement");

                if (userLevel >= localBoss.getLevel() + 1) {
                    Log.d("BossService", "User ready for next boss, creating new");

                    createNewBoss.accept(localBoss.getLevel() + 1);
                    return;
                } else {
                    Log.d("BossService", "User NOT ready for next boss");

                    callback.onError("User not ready for next boss");
                    return;
                }
            }

            // Nema lokalnog, proveri Firebase
            Log.d("BossService", "Fetching boss from Firebase...");
            remoteRepo.findByUserId(userId, new ICallback<Boss>() {
                @Override
                public void onSuccess(Boss bossFromFirebase) {
                    Log.d("BossService", "Firebase boss = " + (bossFromFirebase != null ? bossFromFirebase.toString() : "null"));

                    if (bossFromFirebase != null && !bossFromFirebase.isDefeated()) {
                        Log.d("BossService", "Found undefeated Firebase boss, saving locally and returning");

                        localRepo.insert(bossFromFirebase);
                        if (callbackCalled.compareAndSet(false, true)) {
                            callback.onSuccess(bossFromFirebase);
                        }
                    } else if (bossFromFirebase != null && bossFromFirebase.isDefeated()) {
                        Log.d("BossService", "Firebase boss defeated, checking user level");

                        if (userLevel >= bossFromFirebase.getLevel() + 1) {
                            Log.d("BossService", "User ready for new boss after defeated Firebase boss");

                            createNewBoss.accept(bossFromFirebase.getLevel() + 1);
                        } else {
                            Log.d("BossService", "User NOT ready for next boss (Firebase defeated)");

                            if (callbackCalled.compareAndSet(false, true)) {
                                callback.onError("User not ready for next boss");
                            }
                        }
                    } else {
                        Log.d("BossService", "No boss in Firebase, creating level 1 boss");

                        createNewBoss.accept(1); // prvi boss
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e("BossService", "Firebase lookup ERROR: " + errorMessage + " → creating level 1 boss");

                    createNewBoss.accept(1); // prvi boss ako Firebase failuje
                }
            });

        } catch (Exception e) {
            Log.e("BossService", "Exception in getBossByUserId", e);
            if (callbackCalled.compareAndSet(false, true)) {
                callback.onError(e.getMessage());
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
