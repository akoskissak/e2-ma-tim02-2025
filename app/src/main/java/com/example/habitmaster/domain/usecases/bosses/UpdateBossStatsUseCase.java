package com.example.habitmaster.domain.usecases.bosses;

import android.content.Context;

import com.example.habitmaster.data.firebases.FirebaseBossRepository;
import com.example.habitmaster.data.repositories.BossRepository;
import com.example.habitmaster.services.ICallback;

public class UpdateBossStatsUseCase {
    private final BossRepository localRepo;
    private final FirebaseBossRepository remoteRepo;

    public UpdateBossStatsUseCase(Context context) {
        localRepo = new BossRepository(context);
        remoteRepo = new FirebaseBossRepository();
    }

    public void execute(String bossId, int remainingAttacks, int rewardCoins) {
        localRepo.updateBossStats(bossId, remainingAttacks, rewardCoins);
        remoteRepo.updateBossStats(bossId, remainingAttacks, rewardCoins);
    }
}
