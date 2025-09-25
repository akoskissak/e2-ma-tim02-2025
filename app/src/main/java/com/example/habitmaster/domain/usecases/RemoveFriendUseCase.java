package com.example.habitmaster.domain.usecases;

import com.example.habitmaster.data.firebases.FirebaseFriendRepository;
import com.example.habitmaster.services.ICallback;

public class RemoveFriendUseCase {
    private final FirebaseFriendRepository repo;

    public RemoveFriendUseCase() {
        this.repo = new FirebaseFriendRepository();
    }

    public void execute(String friendUserId, String currentUserId, ICallback<Void> callback) {
        repo.removeFriend(friendUserId, currentUserId,
                unused -> callback.onSuccess(null),
                e -> callback.onError("Error removing friend: " + e.getMessage())
        );
    }
}
