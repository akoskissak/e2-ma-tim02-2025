package com.example.habitmaster.domain.usecases.bosses;

import android.content.Context;

import com.example.habitmaster.data.repositories.BossRepository;
import com.example.habitmaster.domain.models.Boss;

public class PeekBossUseCase {
    private final BossRepository localRepo;

    public PeekBossUseCase(Context context) {
        this.localRepo = new BossRepository(context);
    }

    public Boss execute(String userId) {
        return localRepo.findMaxBossByUserId(userId);
    }
}
