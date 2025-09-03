package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.UserLocalRepository;
import com.example.habitmaster.data.repositories.UserRepository;

public class UpdateUserCoinsUseCase {
    private final UserLocalRepository localRepo;
    private final UserRepository repo;

    public UpdateUserCoinsUseCase(Context ctx) {
        this.localRepo = new UserLocalRepository(ctx);
        this.repo = new UserRepository(ctx);
    }

    public void execute(String userId, int coins) {
        repo.updateUserCoins(userId, coins);
        localRepo.updateUserCoins(userId, coins);
    }
}
