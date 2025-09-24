package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.firebases.FirebaseUserRepository;
import com.example.habitmaster.data.repositories.UserLocalRepository;
import com.example.habitmaster.data.repositories.UserRepository;

public class AddUserXpUseCase {
    private final UserLocalRepository localRepo;
    private final FirebaseUserRepository remoteRepo;

    public interface Callback {
        void onSuccess();

        void onError(String errorMessage);
    }

    public AddUserXpUseCase(UserLocalRepository localRepo, FirebaseUserRepository remoteRepo) {
        this.localRepo = localRepo;
        this.remoteRepo = remoteRepo;

    }

    public void execute(String userId, int xp) {
        localRepo.addXp(userId, xp);
        remoteRepo.addXp(userId, xp);
    }
}
