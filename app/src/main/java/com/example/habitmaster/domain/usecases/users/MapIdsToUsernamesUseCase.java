package com.example.habitmaster.domain.usecases.users;

import android.content.Context;

import com.example.habitmaster.data.repositories.UserLocalRepository;

import java.util.List;
import java.util.Map;

public class MapIdsToUsernamesUseCase {
    private final UserLocalRepository localRepo;

    public MapIdsToUsernamesUseCase(Context context) {
        this.localRepo = new UserLocalRepository(context);
    }

    public Map<String, String> execute(List<String> userIds) {
        return localRepo.mapIdsToUsernames(userIds);
    }
}
