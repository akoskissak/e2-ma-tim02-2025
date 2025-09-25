package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.firebases.FirebaseFriendRepository;
import com.example.habitmaster.data.repositories.FriendRepository;
import com.example.habitmaster.domain.models.Friend;
import com.example.habitmaster.services.ICallback;

import java.util.List;

public class GetFriendsUseCase {
    public final FirebaseFriendRepository repo;

    public GetFriendsUseCase() {
        this.repo = new FirebaseFriendRepository();
    }

    public void execute(String currentUserId, ICallback<List<Friend>> callback) {
        repo.getAllFriends(currentUserId,
                friends -> {
                    if (!friends.isEmpty()) {
                        callback.onSuccess(friends);
                    } else {
                        callback.onError("You have no friends yet");
                    }
                },
                e -> {
                    callback.onError("Error fetching friends: " + e.getMessage());
                });
    }
}
