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
        this.removeFriendUC = new RemoveFriendUseCase();
        this.getFriendsUC = new GetFriendsUseCase();
        this.isAlreadyFriendUC = new IsAlreadyFriendUseCase();
        this.sendFollowRequestUC = new SendFollowRequestUseCase(ctx);
        this.respondFollowRequestUC = new RespondFollowRequestUseCase(ctx);
        this.getFollowRequestsUC = new GetFollowRequestsUseCase();
        this.isFollowRequestPendingUC = new IsFollowRequestPendingUserCase();
    }

    public void addFriend(Friend friend, String currentUserId, ICallback<Void> callback) {
        addFriendUC.execute(friend, currentUserId, callback);
    }

    public void sendFollowRequest(String fromUserId, String toUserId) {
        sendFollowRequestUC.execute(fromUserId, toUserId);
    }

    public void respondFollowRequest(FollowRequest request, boolean accept, String currentUserId, ICallback<Void> callback){
        respondFollowRequestUC.execute(request, accept, currentUserId, callback);
    }

    public void getFollowRequests(String currentUserId, ICallback<List<FollowRequestWithUsername>> callback) {
        getFollowRequestsUC.execute(currentUserId, callback);
    }

    public void removeFriend(String friendUserId, String currentUserId, ICallback<Void> callback) {
        removeFriendUC.execute(friendUserId, currentUserId, callback);
    }

    public void getFriends(String currentUserId, ICallback<List<Friend>> callback) {
        getFriendsUC.execute(currentUserId, callback);
    }

    public void isAlreadyFriend(String currentUserId, String viewedUserId, ICallback<Boolean> callback) {
        isAlreadyFriendUC.execute(currentUserId, viewedUserId, callback);
    }

    public void isFollowRequestPending(String fromUserId, String toUserId, ICallback<Boolean> callback) {
        isFollowRequestPendingUC.execute(fromUserId, toUserId, callback);
    }
}
