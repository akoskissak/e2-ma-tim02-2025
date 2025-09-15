package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.FriendRepository;
import com.example.habitmaster.domain.models.Friend;

public class AddFriendUseCase {
    private final FriendRepository repo;

    public AddFriendUseCase(Context ctx) {
        this.repo = new FriendRepository(ctx);
    }

    public void execute(Friend friend, String currentUserId) {
        repo.addFriend(friend, currentUserId);
    }
}
