package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.FollowRequestRepository;
import com.example.habitmaster.data.repositories.FriendRepository;
import com.example.habitmaster.data.repositories.UserLocalRepository;
import com.example.habitmaster.domain.models.FollowRequest;
import com.example.habitmaster.domain.models.FollowRequestStatus;
import com.example.habitmaster.domain.models.Friend;
import com.example.habitmaster.domain.models.User;

public class RespondFollowRequestUseCase {
    private final FollowRequestRepository repo;
    private final FriendRepository friendRepo;
    private final UserLocalRepository userRepo;

    public RespondFollowRequestUseCase(Context ctx) {
        this.repo = new FollowRequestRepository(ctx);
        this.friendRepo = new FriendRepository(ctx);
        this.userRepo = new UserLocalRepository(ctx);
    }

    public void execute(FollowRequest request, boolean accept, String currentUserId) {
        String newStatus = accept ? FollowRequestStatus.ACCEPTED.toString() : FollowRequestStatus.DECLINED.toString();
        repo.updateRequestStatus(request.getId(), newStatus);

        if(accept) {
            User toUser = userRepo.findById(currentUserId);
            Friend friend = new Friend(request.getFromUserId(), currentUserId, toUser.getUsername(), toUser.getAvatarName());
            friendRepo.addFriend(friend, request.getFromUserId());
        }
    }
}
