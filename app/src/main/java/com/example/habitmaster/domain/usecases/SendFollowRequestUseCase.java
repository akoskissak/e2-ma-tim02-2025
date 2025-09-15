package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.FollowRequestRepository;
import com.example.habitmaster.domain.models.FollowRequest;
import com.example.habitmaster.domain.models.FollowRequestStatus;

import java.util.UUID;

public class SendFollowRequestUseCase {
    private final FollowRequestRepository repo;

    public SendFollowRequestUseCase(Context ctx) {
        this.repo = new FollowRequestRepository(ctx);
    }

    public void execute(String fromUserId, String toUserId) {
        String requestId = UUID.randomUUID().toString();
        FollowRequest request = new FollowRequest(requestId, fromUserId, toUserId, FollowRequestStatus.PENDING);
        repo.createFollowRequest(request);

    }
}
