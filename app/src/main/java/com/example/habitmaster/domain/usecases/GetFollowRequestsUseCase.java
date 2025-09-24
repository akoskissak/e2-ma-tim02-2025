package com.example.habitmaster.domain.usecases;


import com.example.habitmaster.data.dtos.FollowRequestWithUsername;
import com.example.habitmaster.data.firebases.FirebaseFollowRequestRepository;
import com.example.habitmaster.services.ICallback;

import java.util.List;

public class GetFollowRequestsUseCase {
    private final FirebaseFollowRequestRepository repo;

    public GetFollowRequestsUseCase() {
        this.repo = new FirebaseFollowRequestRepository();
    }

    public void execute(String userId, ICallback<List<FollowRequestWithUsername>> callback) {
        repo.getPendingRequests(userId,
                requests -> {
                    callback.onSuccess(requests);
                });
    }
}
