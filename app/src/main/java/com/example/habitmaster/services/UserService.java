package com.example.habitmaster.services;

import android.content.Context;

import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.domain.models.UserLevelProgress;
import com.example.habitmaster.domain.models.UserStatistics;
import com.example.habitmaster.domain.usecases.ChangePasswordUseCase;
import com.example.habitmaster.domain.usecases.GetCurrentUserUseCase;
import com.example.habitmaster.domain.usecases.GetLevelProgressUseCase;
import com.example.habitmaster.domain.usecases.GetUserByIdUseCase;
import com.example.habitmaster.domain.usecases.GetUserByUsernameUseCase;
import com.example.habitmaster.domain.usecases.GetUserStatisticsUseCase;
import com.example.habitmaster.domain.usecases.GetUserUseCase;
import com.example.habitmaster.domain.usecases.LoginUserUseCase;
import com.example.habitmaster.domain.usecases.RegisterUserUseCase;

public class UserService {
    private final RegisterUserUseCase registerUC;
    private final LoginUserUseCase loginUC;
    private final GetUserUseCase getUserUC;
    private final ChangePasswordUseCase changePasswordUC;
    private final GetUserStatisticsUseCase getUserStatistiscUC;
    private final GetLevelProgressUseCase getLevelProgressUC;
    private final GetCurrentUserUseCase getCurrentUserUC;
    private final GetUserByUsernameUseCase getUserByUsernameUC;
    private final GetUserByIdUseCase getUserByIdUC;

    public UserService(Context ctx){
        this.registerUC = new RegisterUserUseCase(ctx);
        this.loginUC = new LoginUserUseCase(ctx);
        this.getUserUC = new GetUserUseCase(ctx);
        this.changePasswordUC = new ChangePasswordUseCase(ctx);
        this.getUserStatistiscUC = new GetUserStatisticsUseCase(ctx);
        this.getLevelProgressUC = new GetLevelProgressUseCase(ctx);
        this.getCurrentUserUC = new GetCurrentUserUseCase(ctx);
        this.getUserByUsernameUC = new GetUserByUsernameUseCase(ctx);
        this.getUserByIdUC = new GetUserByIdUseCase(ctx);
    }

    public void register(String email, String pass, String confirm, String username, String avatarName, ICallback<User> callback){
        registerUC.execute(email, pass, confirm, username, avatarName, callback);
    }

    public void login(String email, String pass, ICallback<User> callback){
        loginUC.execute(email, pass, callback);
    }

    public User getUser(String username){
        return getUserUC.execute(username);
    }

    public void changePassword(String oldPassword, String newPassword, ICallbackVoid callback) {
        changePasswordUC.execute(oldPassword, newPassword, callback);
    }

    public void getUserStatistics(ICallback<UserStatistics> callback) {
        getUserStatistiscUC.execute(callback);
    }

    public void getUserLevelProgress(ICallback<UserLevelProgress> callback) {
        getLevelProgressUC.execute(callback);
    }

    public void getCurrentUser(ICallback<User> callback) {
        getCurrentUserUC.execute(callback);
    }

    public void findUserByUsername(String username, ICallback<User> callback) {
        getUserByUsernameUC.execute(username, callback);
    }

    public void findUserById(String userId, ICallback<User> callback) {
        getUserByIdUC.execute(userId, callback);
    }
}
