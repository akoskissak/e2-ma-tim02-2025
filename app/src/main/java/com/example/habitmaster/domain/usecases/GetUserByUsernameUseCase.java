package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.UserLocalRepository;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.services.ICallback;

public class GetUserByUsernameUseCase {
    private final UserLocalRepository repo;

    public GetUserByUsernameUseCase(Context ctx) {
        this.repo = new UserLocalRepository(ctx);
    }

    public void execute(String username, ICallback<User> callback) {
        User user = repo.findUserByUsername(username);
        if(user != null) {
            callback.onSuccess(user);
            return;
        }
        callback.onError("There is no user with that username");
    }
}
