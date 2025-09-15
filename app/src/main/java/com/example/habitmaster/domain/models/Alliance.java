package com.example.habitmaster.domain.models;

import java.util.ArrayList;
import java.util.List;

public class Alliance {
    private String id;
    private String name;
    private String leaderId;
    private boolean missionStarted;

    public Alliance(String id, String name, String leaderId, boolean missionStarted) {
        this.id = id;
        this.name = name;
        this.leaderId = leaderId;
        this.missionStarted = missionStarted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }

    public boolean isMissionStarted() {
        return missionStarted;
    }

    public void setMissionStarted(boolean missionStarted) {
        this.missionStarted = missionStarted;
    }
}
