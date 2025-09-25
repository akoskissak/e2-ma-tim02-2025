package com.example.habitmaster.domain.usecases;


import com.example.habitmaster.data.firebases.FirebaseFriendRepository;
import com.example.habitmaster.services.ICallback;

public class IsAlreadyFriendUseCase {
    private final FirebaseFriendRepository repo;

    public IsAlreadyFriendUseCase() {
        this.repo = new FirebaseFriendRepository();
    }

    public void execute(String currentUserId, String viewedUserId, ICallback<Boolean> callback) {
        repo.isAlreadyFriend(currentUserId, viewedUserId,
                exists -> {
                    callback.onSuccess(exists);
                },
                e -> {
                    callback.onError("Error checking friendship: " + e.getMessage());
                });
    }
}
