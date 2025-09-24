package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.firebases.FirebaseFriendRepository;
import com.example.habitmaster.data.repositories.FriendRepository;
import com.example.habitmaster.domain.models.Friend;
import com.example.habitmaster.services.ICallback;

public class AddFriendUseCase {
    private final FriendRepository repo;
    private final FirebaseFriendRepository friendRepository;

    public AddFriendUseCase(Context ctx) {
        this.repo = new FriendRepository(ctx);
        this.friendRepository = new FirebaseFriendRepository();
    }

    public void execute(Friend friend, String currentUserId, ICallback<Void> callback) {
        friendRepository.addFriend(friend, currentUserId,
                unused -> callback.onSuccess(null),
                e -> callback.onError("Error adding friend: " + e.getMessage())
        );
        repo.addFriend(friend, currentUserId);
    }
}
