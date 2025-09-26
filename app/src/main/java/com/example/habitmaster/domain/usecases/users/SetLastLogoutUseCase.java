package com.example.habitmaster.domain.usecases.users;

import android.content.Context;

import com.example.habitmaster.data.repositories.UserRepository;

public class SetLastLogoutUseCase {
    private final UserRepository repo;

    public SetLastLogoutUseCase(Context ctx){
        this.repo = new UserRepository(ctx);
    }

    public void execute(String userId){
        repo.setLastLogout(userId);
    }
}
