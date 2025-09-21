package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.UserLocalRepository;
import com.example.habitmaster.services.ICallback;

import java.time.LocalDate;

public class GetUserLevelStartDateUseCase {
    private final UserLocalRepository localRepo;

    public GetUserLevelStartDateUseCase(Context context) {
        localRepo = new UserLocalRepository(context);
    }

    public LocalDate execute(String userId) {
        return localRepo.getUserLevelStartDate(userId);
    }
}
