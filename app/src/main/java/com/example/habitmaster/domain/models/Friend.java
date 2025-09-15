package com.example.habitmaster.domain.models;

public class Friend {
    private int id;
    private String userId;
    private String friendUserId;
    private String friendUsername;
    private String FriendAvatarName;

    public Friend(String userId, String friendUserId, String friendUsername, String friendAvatarName) {
        this.userId = userId;
        this.friendUserId = friendUserId;
        this.friendUsername = friendUsername;
        FriendAvatarName = friendAvatarName;
    }

    public Friend() {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFriendUserId() {
        return friendUserId;
    }

    public void setFriendUserId(String friendUserId) {
        this.friendUserId = friendUserId;
    }

    public String getFriendUsername() {
        return friendUsername;
    }

    public void setFriendUsername(String friendUsername) {
        this.friendUsername = friendUsername;
    }

    public String getFriendAvatarName() {
        return FriendAvatarName;
    }

    public void setFriendAvatarName(String friendAvatarName) {
        FriendAvatarName = friendAvatarName;
    }
}
