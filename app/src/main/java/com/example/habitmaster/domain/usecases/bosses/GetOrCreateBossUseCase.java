package com.example.habitmaster.domain.usecases.bosses;

import android.content.Context;

import com.example.habitmaster.data.firebases.FirebaseBossRepository;
import com.example.habitmaster.data.repositories.BossRepository;
import com.example.habitmaster.domain.models.Boss;
import com.example.habitmaster.services.ICallback;

import java.util.UUID;

public class GetOrCreateBossUseCase {
    private final BossRepository localRepo;
    private final FirebaseBossRepository remoteRepo;

    public GetOrCreateBossUseCase(Context context) {
        localRepo = new BossRepository(context);
        remoteRepo = new FirebaseBossRepository();
    }

    public void getBossByUserId(String userId, ICallback<Boss> callback) {
        try {
            // 1. Proveri lokalno
            Boss localBoss = localRepo.findByUserId(userId);
            if (localBoss != null) {
                if (!localBoss.isDefeated()) {
                    callback.onSuccess(localBoss);
                    return;
                } else {
                    // Kreiraj novog ako je prethodni poražen
                    Boss newBoss = insertNewBoss(userId, localBoss.getLevel() + 1);
                    callback.onSuccess(newBoss);
                    return;
                }
            }

            // 2. Nema lokalno → proveri Firebase
            remoteRepo.findByUserId(userId, new ICallback<Boss>() {
                @Override
                public void onSuccess(Boss bossFromFirebase) {
                    if (bossFromFirebase != null) {
                        if (!bossFromFirebase.isDefeated()) {
                            localRepo.insert(bossFromFirebase);
                            callback.onSuccess(bossFromFirebase);
                        } else {
                            // Kreiraj novog ako je prethodni poražen
                            Boss newBoss = insertNewBoss(userId, bossFromFirebase.getLevel() + 1);
                            callback.onSuccess(newBoss);
                            return;
                        }
                    } else {
                        // Kreiraj prvog
                        Boss newBoss = new Boss(UUID.randomUUID().toString(), userId, 1);
                        localRepo.insert(newBoss);
                        remoteRepo.insert(newBoss);
                        callback.onSuccess(newBoss);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    // Ako Firebase failuje → kreiraj prvog boss-a
                    Boss newBoss = insertNewBoss(userId, 1);
                    callback.onSuccess(newBoss);
                }
            });
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    private Boss insertNewBoss(String userId, int level) {
        Boss newBoss = new Boss(UUID.randomUUID().toString(), userId, level);
        localRepo.insert(newBoss);
        remoteRepo.insert(newBoss);
        return newBoss;
    }
}

