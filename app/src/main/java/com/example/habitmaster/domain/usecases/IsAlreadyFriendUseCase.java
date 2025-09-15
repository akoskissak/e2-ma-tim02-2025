package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.FriendRepository;

public class IsAlreadyFriendUseCase {
    private final FriendRepository repo;

    public IsAlreadyFriendUseCase(Context ctx) {
        this.repo = new FriendRepository(ctx);
    }

    public boolean execute(String currentUserId, String viewUserId) {
        return repo.isAlreadyFriend(currentUserId, viewUserId);
    }
}
