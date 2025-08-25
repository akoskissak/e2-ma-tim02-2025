package com.example.habitmaster.domain.models;

import java.util.List;
import java.util.Map;

public class UserStatistics {
    private int activeDays;
    private int totalCreated;
    private int totalCompleted;
    private int totalMissed;
    private int totalCancelled;
    private int longestStreak;
    private Map<String, Integer> completedTasksByCategory;
    private Map<TaskDifficulty, Float> difficultyPercent;
    private List<Integer> xpLast7Days;
    private int specialMissionsStarted;
    private int specialMissionsCompleted;

    public UserStatistics(){};

    public int getActiveDays() {
        return activeDays;
    }

    public void setActiveDays(int activeDays) {
        this.activeDays = activeDays;
    }

    public int getTotalCreated() {
        return totalCreated;
    }

    public void setTotalCreated(int totalCreated) {
        this.totalCreated = totalCreated;
    }

    public int getTotalCompleted() {
        return totalCompleted;
    }

    public void setTotalCompleted(int totalCompleted) {
        this.totalCompleted = totalCompleted;
    }

    public int getTotalMissed() {
        return totalMissed;
    }

    public void setTotalMissed(int totalMissed) {
        this.totalMissed = totalMissed;
    }

    public int getTotalCancelled() {
        return totalCancelled;
    }

    public void setTotalCancelled(int totalCancelled) {
        this.totalCancelled = totalCancelled;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public Map<String, Integer> getCompletedTasksByCategory() {
        return completedTasksByCategory;
    }

    public void setCompletedTasksByCategory(Map<String, Integer> completedTasksByCategory) {
        this.completedTasksByCategory = completedTasksByCategory;
    }

    public Map<TaskDifficulty, Float> getDifficultyPercent() {
        return difficultyPercent;
    }

    public void setDifficultyPercent(Map<TaskDifficulty, Float> difficultyPercent) {
        this.difficultyPercent = difficultyPercent;
    }

    public List<Integer> getXpLast7Days() {
        return xpLast7Days;
    }

    public void setXpLast7Days(List<Integer> xpLast7Days) {
        this.xpLast7Days = xpLast7Days;
    }

    public int getSpecialMissionsStarted() {
        return specialMissionsStarted;
    }

    public void setSpecialMissionsStarted(int specialMissionsStarted) {
        this.specialMissionsStarted = specialMissionsStarted;
    }

    public int getSpecialMissionsCompleted() {
        return specialMissionsCompleted;
    }

    public void setSpecialMissionsCompleted(int specialMissionsCompleted) {
        this.specialMissionsCompleted = specialMissionsCompleted;
    }
}
