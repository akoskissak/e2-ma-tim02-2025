package com.example.habitmaster.domain.models;

import java.time.LocalDateTime;

public class AllianceMission {
    private String id;
    private String allianceId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime; // Duration 2 weeks
    private AllianceMissionStatus status;
    private int bossMaxHp;
    private int bossCurrentHp;

    public AllianceMission() {}

    public AllianceMission(String id, String allianceId, LocalDateTime startDateTime, int memberCount) {
        this.id = id;
        this.allianceId = allianceId;
        this.startDateTime = startDateTime;
        this.endDateTime = startDateTime.plusWeeks(2); // Duration 2 weeks
        this.status = AllianceMissionStatus.ONGOING;
        this.bossMaxHp = 100 * memberCount;
        this.bossCurrentHp = 100 * memberCount - 10 * memberCount; // OVDE SE I LEADER RACUNA KAO MEMBER
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

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public AllianceMissionStatus getStatus() {
        return status;
    }

    public void setStatus(AllianceMissionStatus status) {
        this.status = status;
    }

    public int getBossMaxHp() {
        return bossMaxHp;
    }

    public void setBossMaxHp(int bossMaxHp) {
        this.bossMaxHp = bossMaxHp;
    }

    public int getBossCurrentHp() {
        return bossCurrentHp;
    }

    public void setBossCurrentHp(int bossCurrentHp) {
        this.bossCurrentHp = bossCurrentHp;
    }

    public void attack(int damage) {
        bossCurrentHp -= damage;
        if (bossCurrentHp <= 0) {
            status = AllianceMissionStatus.COMPLETED;
            bossCurrentHp = 0;
        }
    }

    public void decreaseCurrentHp(int value) {
        this.bossCurrentHp -= value;
        if (bossCurrentHp <= 0 ) {
            bossCurrentHp = 0;
            status = AllianceMissionStatus.COMPLETED;
        }
    }

    public void increaseCurrentHp(int value) {
        if (this.bossCurrentHp + value <= this.bossMaxHp) {
            this.bossCurrentHp += value;
        } else {
            this.bossCurrentHp = this.bossMaxHp;
        }
    }

    public boolean finishMission() {
        if (bossCurrentHp <= 0) {
            status = AllianceMissionStatus.COMPLETED;
            return true;
        } else {
            status = AllianceMissionStatus.FAILED;
            return false;
        }
    }

    public boolean isCompleted() {
        return status == AllianceMissionStatus.COMPLETED;
    }
}
