package com.example.habitmaster.domain.usecases;

import com.example.habitmaster.data.firebases.FirebaseFollowRequestRepository;
import com.example.habitmaster.services.ICallback;

public class IsFollowRequestPendingUserCase {
    private final FirebaseFollowRequestRepository repo;

    public IsFollowRequestPendingUserCase() {
        this.repo = new FirebaseFollowRequestRepository();
    }

    public void execute(String fromUserId, String toUserId, ICallback<Boolean> callback) {
        repo.isPending(fromUserId, toUserId,
                exists -> {
                    callback.onSuccess(exists);
                },
                e -> {
                    callback.onError("Error checking pending request: " + e.getMessage());
                });
    }
}
