package com.example.habitmaster.domain.usecases;


import android.content.Context;

import com.example.habitmaster.data.repositories.UserLocalRepository;
import com.example.habitmaster.data.repositories.UserRepository;
import com.example.habitmaster.domain.models.User;

public class GetUserUseCase {
    private final UserLocalRepository userRepository;

    public GetUserUseCase(Context ctx) {
        this.userRepository = new UserLocalRepository(ctx);
    }

    public User execute(String email) {
        return userRepository.findByEmail(email);
    }
}
