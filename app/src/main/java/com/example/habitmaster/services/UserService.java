package com.example.habitmaster.services;

import android.content.Context;

import com.example.habitmaster.domain.usecases.RegisterUserUseCase;

public class UserService {
    private final RegisterUserUseCase registerUC;

    public UserService(Context ctx){
        this.registerUC = new RegisterUserUseCase(ctx);
    }

    public void register(String email, String pass, String confirm, String username, String avatarName, RegisterUserUseCase.Callback callback){
        registerUC.execute(email, pass, confirm, username, avatarName, callback);
    }
}
