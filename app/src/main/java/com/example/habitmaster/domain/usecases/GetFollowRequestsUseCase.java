package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.dtos.FollowRequestWithUsername;
import com.example.habitmaster.data.repositories.FollowRequestRepository;
import com.example.habitmaster.services.ICallback;

import java.util.List;

public class GetFollowRequestsUseCase {
    private final FollowRequestRepository repo;

    public GetFollowRequestsUseCase(Context context) {
        this.repo = new FollowRequestRepository(context);
    }

    public void execute(String userId, ICallback<List<FollowRequestWithUsername>> callback) {
        List<FollowRequestWithUsername> requests = repo.getPendingRequests(userId);
        if(requests == null) {
            callback.onError("User has no pending requests");
            return;
        }
        callback.onSuccess(requests);
    }
}
