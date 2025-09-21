package com.example.habitmaster.domain.usecases.users;

import android.content.Context;

import com.example.habitmaster.data.repositories.UserLocalRepository;
import com.example.habitmaster.services.ICallback;

public class GetUserLevelUseCase {
    private final UserLocalRepository localRepo;

    public GetUserLevelUseCase(Context context) {
        localRepo = new UserLocalRepository(context);
    }

    public int getUserLevel(String userId) {
        return localRepo.getUserLevel(userId);
    }
}
