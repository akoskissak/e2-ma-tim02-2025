package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.UserRepository;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.services.ICallback;

public class GetUserByIdUseCase {
    private final UserRepository repo;

    public GetUserByIdUseCase(Context ctx) {
        this.repo = new UserRepository(ctx);
    }

    public void execute(String userId, ICallback<User> callback) {
        repo.getUserFromFirestore(userId, new ICallback<>() {
            @Override
            public void onSuccess(User result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError("User not found");
            }
        });
    }
}