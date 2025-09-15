package com.example.habitmaster.domain.models;

public class FollowRequest {
    private String id;
    private String fromUserId;
    private String toUserId;
    private FollowRequestStatus status;

    public FollowRequest(String id, String fromUserId, String toUserId, FollowRequestStatus status) {
        this.id = id;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public FollowRequestStatus getStatus() {
        return status;
    }

    public void setStatus(FollowRequestStatus status) {
        this.status = status;
    }
}

