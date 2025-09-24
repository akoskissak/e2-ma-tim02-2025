package com.example.habitmaster.domain.models;

public class AllianceInvitation {
    private String id;
    private String allianceId;
    private String fromUserId;
    private String toUserId;
    private AllianceInviteStatus status;

    public AllianceInvitation() {}
    public AllianceInvitation(String id, String allianceId, String fromUserId, String toUserId, AllianceInviteStatus status) {
        this.id = id;
        this.allianceId = allianceId;
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

    public String getAllianceId() {
        return allianceId;
    }

    public void setAllianceId(String allianceId) {
        this.allianceId = allianceId;
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

    public AllianceInviteStatus getStatus() {
        return status;
    }

    public void setStatus(AllianceInviteStatus status) {
        this.status = status;
    }
}
