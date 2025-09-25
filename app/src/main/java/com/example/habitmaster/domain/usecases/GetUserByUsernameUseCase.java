package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.firebases.FirebaseUserRepository;
import com.example.habitmaster.data.repositories.UserLocalRepository;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.services.ICallback;

public class GetUserByUsernameUseCase {
    private final FirebaseUserRepository repo;

    public GetUserByUsernameUseCase(Context ctx) {
        this.repo = new FirebaseUserRepository(ctx);
    }

    public void execute(String username, ICallback<User> callback) {
        repo.findUserByUsername(username,
                user -> {
                    if (user != null) {
                        callback.onSuccess(user);
                    } else {
                        callback.onError("There is no user with that username");
                    }
                },
                e -> {
                    callback.onError("Error fetching user: " + e.getMessage());
                }
        );
    }
}
