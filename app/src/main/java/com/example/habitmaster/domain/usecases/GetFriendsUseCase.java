package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.FriendRepository;
import com.example.habitmaster.domain.models.Friend;
import com.example.habitmaster.services.ICallback;

import java.util.List;

public class GetFriendsUseCase {
    public final FriendRepository repo;

    public GetFriendsUseCase(Context ctx) {
        this.repo = new FriendRepository(ctx);
    }

    public void execute(String currentUserId, ICallback<List<Friend>> callback) {
        List<Friend> friends = repo.getAllFriends(currentUserId);
        if(!friends.isEmpty()) {
            callback.onSuccess(friends);
            return;
        }
        callback.onError("You have no friends yet");
    }
}
