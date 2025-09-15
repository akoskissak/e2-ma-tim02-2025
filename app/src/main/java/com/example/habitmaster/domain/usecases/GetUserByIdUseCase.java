package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.UserLocalRepository;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.services.ICallback;

public class GetUserByIdUseCase {
    private final UserLocalRepository repo;

    public GetUserByIdUseCase(Context ctx) {
        this.repo = new UserLocalRepository(ctx);
    }

    public void execute(String userId, ICallback<User> callback) {
        User user = repo.findById(userId);
        if(user == null) {
            callback.onError("User not found");
            return;
        }
        callback.onSuccess(user);
    }
}