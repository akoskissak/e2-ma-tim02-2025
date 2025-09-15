package com.example.habitmaster.services;

import android.content.Context;

import com.example.habitmaster.data.dtos.FollowRequestWithUsername;
import com.example.habitmaster.domain.models.FollowRequest;
import com.example.habitmaster.domain.models.Friend;
import com.example.habitmaster.domain.usecases.AddFriendUseCase;
import com.example.habitmaster.domain.usecases.GetFollowRequestsUseCase;
import com.example.habitmaster.domain.usecases.GetFriendsUseCase;
import com.example.habitmaster.domain.usecases.IsAlreadyFriendUseCase;
import com.example.habitmaster.domain.usecases.IsFollowRequestPendingUserCase;
import com.example.habitmaster.domain.usecases.RemoveFriendUseCase;
import com.example.habitmaster.domain.usecases.RespondFollowRequestUseCase;
import com.example.habitmaster.domain.usecases.SendFollowRequestUseCase;

import java.util.List;

public class FriendService {
    private final AddFriendUseCase addFriendUC;
    private final RemoveFriendUseCase removeFriendUC;
    private final GetFriendsUseCase getFriendsUC;
    private final IsAlreadyFriendUseCase isAlreadyFriendUC;
    private final SendFollowRequestUseCase sendFollowRequestUC;
    private final RespondFollowRequestUseCase respondFollowRequestUC;
    private final GetFollowRequestsUseCase getFollowRequestsUC;
    private final IsFollowRequestPendingUserCase isFollowRequestPendingUC;

    public FriendService(Context ctx) {
        this.addFriendUC = new AddFriendUseCase(ctx);
        this.removeFriendUC = new RemoveFriendUseCase(ctx);
        this.getFriendsUC = new GetFriendsUseCase(ctx);
        this.isAlreadyFriendUC = new IsAlreadyFriendUseCase(ctx);
        this.sendFollowRequestUC = new SendFollowRequestUseCase(ctx);
        this.respondFollowRequestUC = new RespondFollowRequestUseCase(ctx);
        this.getFollowRequestsUC = new GetFollowRequestsUseCase(ctx);
        this.isFollowRequestPendingUC = new IsFollowRequestPendingUserCase(ctx);
    }

    public void addFriend(Friend friend, String currentUserId) {
        addFriendUC.execute(friend, currentUserId);
    }

    public void sendFollowRequest(String fromUserId, String toUserId) {
        sendFollowRequestUC.execute(fromUserId, toUserId);
    }

    public void respondFollowRequest(FollowRequest request, boolean accept, String currentUserId){
        respondFollowRequestUC.execute(request, accept, currentUserId);
    }

    public void getFollowRequests(String currentUserId, ICallback<List<FollowRequestWithUsername>> callback) {
        getFollowRequestsUC.execute(currentUserId, callback);
    }

    public void removeFriend(String friendUserId, String currentUserId) {
        removeFriendUC.execute(friendUserId, currentUserId);
    }

    public void getFriends(String currentUserId, ICallback<List<Friend>> callback) {
        getFriendsUC.execute(currentUserId, callback);
    }

    public boolean isAlreadyFriend(String currentUserId, String viewedUserId) {
        return isAlreadyFriendUC.execute(currentUserId, viewedUserId);
    }

    public boolean isFollowRequestPending(String fromUserId, String toUserId) {
        return isFollowRequestPendingUC.execute(fromUserId, toUserId);
    }
}
