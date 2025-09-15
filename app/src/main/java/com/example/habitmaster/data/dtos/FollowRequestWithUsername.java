package com.example.habitmaster.data.dtos;

import com.example.habitmaster.domain.models.FollowRequestStatus;

public class FollowRequestWithUsername {
    private String id;
    private String fromUserId;
    private String toUserId;
    private FollowRequestStatus status;
    private String fromUsername;

    public FollowRequestWithUsername(String id, String fromUserId, String toUserId,
                                 FollowRequestStatus status, String fromUsername) {
        this.id = id;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.status = status;
        this.fromUsername = fromUsername;
    }

    public String getId() {
        return id;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public FollowRequestStatus getStatus() {
        return status;
    }

    public String getFromUsername() {
        return fromUsername;
    }
}
