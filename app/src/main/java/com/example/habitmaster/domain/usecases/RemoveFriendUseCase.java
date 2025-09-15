package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.FriendRepository;

public class RemoveFriendUseCase {
    private final FriendRepository repo;

    public RemoveFriendUseCase(Context ctx) {
        this.repo = new FriendRepository(ctx);
    }

    public void execute(String friendUserId, String currentUserId) {
        repo.removeFriend(friendUserId, currentUserId);
    }
}
