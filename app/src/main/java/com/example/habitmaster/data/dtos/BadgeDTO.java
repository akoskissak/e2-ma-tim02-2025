package com.example.habitmaster.data.dtos;

public class BadgeDTO {
    public String id;
    public String userId;
    public String missionStartDate;
    public String missionEndDate;
    public String imageName;
    public int shopPurchases;              // max 5
    public int bossFightHits;              // max 10
    public int solvedTasks;                // max 10
    public int solvedOtherTasks;           // max 6
    public boolean noUnresolvedTasks;      // +10 HP ako je true
    public int messagesSentDays;           // broj dana kada je poslao bar 1 poruku
    public int totalDamage;

    public BadgeDTO() {}
}
