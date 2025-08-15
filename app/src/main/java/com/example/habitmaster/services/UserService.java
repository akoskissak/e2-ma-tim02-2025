package com.example.habitmaster.services;

import android.content.Context;

import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.domain.usecases.ChangePasswordUseCase;
import com.example.habitmaster.domain.usecases.GetUserUseCase;
import com.example.habitmaster.domain.usecases.LoginUserUseCase;
import com.example.habitmaster.domain.usecases.RegisterUserUseCase;

public class UserService {
    private final RegisterUserUseCase registerUC;
    private final LoginUserUseCase loginUC;
    private final GetUserUseCase getUserUC;
    private final ChangePasswordUseCase changePasswordUC;

    public UserService(Context ctx){
        this.registerUC = new RegisterUserUseCase(ctx);
        this.loginUC = new LoginUserUseCase(ctx);
        this.getUserUC = new GetUserUseCase(ctx);
        this.changePasswordUC = new ChangePasswordUseCase(ctx);
    }

    public void register(String email, String pass, String confirm, String username, String avatarName, ICallback callback){
        registerUC.execute(email, pass, confirm, username, avatarName, callback);
    }

    public void login(String email, String pass, ICallback callback){
        loginUC.execute(email, pass, callback);
    }

    public User getUser(String email){
        return getUserUC.execute(email);
    }

    public void changePassword(String oldPassword, String newPassword, ICallbackVoid callback) {
        changePasswordUC.execute(oldPassword, newPassword, callback);
    }
}
