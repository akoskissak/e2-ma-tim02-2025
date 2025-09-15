package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.FollowRequestRepository;

public class IsFollowRequestPendingUserCase {
    private final FollowRequestRepository repo;

    public IsFollowRequestPendingUserCase(Context ctx) {
        this.repo = new FollowRequestRepository(ctx);
    }

    public boolean execute(String fromUserId, String toUserId) {
        return repo.isPending(fromUserId, toUserId);
    }
}
