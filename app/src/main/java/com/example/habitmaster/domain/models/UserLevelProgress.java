package com.example.habitmaster.domain.models;

public class UserLevelProgress {
    private String userId;
    private int requiredXp = 200;

    private int veryEasyXp = 1;
    private int easyXp = 3;
    private int hardXp = 7;
    private int extremelyHardXp = 20;

    private int normalXp = 1;
    private int importantXp = 3;
    private int extremelyImportantXp = 10;
    private int specialXp = 100;

    public UserLevelProgress(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRequiredXp() {
        return requiredXp;
    }

    public void setRequiredXp(int requiredXp) {
        this.requiredXp = requiredXp;
    }

    public int getVeryEasyXp() {
        return veryEasyXp;
    }

    public void setVeryEasyXp(int veryEasyXp) {
        this.veryEasyXp = veryEasyXp;
    }

    public int getEasyXp() {
        return easyXp;
    }

    public void setEasyXp(int easyXp) {
        this.easyXp = easyXp;
    }

    public int getHardXp() {
        return hardXp;
    }

    public void setHardXp(int hardXp) {
        this.hardXp = hardXp;
    }

    public int getExtremelyHardXp() {
        return extremelyHardXp;
    }

    public void setExtremelyHardXp(int extremelyHardXp) {
        this.extremelyHardXp = extremelyHardXp;
    }

    public int getNormalXp() {
        return normalXp;
    }

    public void setNormalXp(int normalXp) {
        this.normalXp = normalXp;
    }

    public int getImportantXp() {
        return importantXp;
    }

    public void setImportantXp(int importantXp) {
        this.importantXp = importantXp;
    }

    public int getExtremelyImportantXp() {
        return extremelyImportantXp;
    }

    public void setExtremelyImportantXp(int extremelyImportantXp) {
        this.extremelyImportantXp = extremelyImportantXp;
    }

    public int getSpecialXp() {
        return specialXp;
    }

    public void setSpecialXp(int specialXp) {
        this.specialXp = specialXp;
    }

    public void updateXpValuesOnLevelUp() {
        // XP po tezini
        this.veryEasyXp = (int) Math.round(this.veryEasyXp + this.veryEasyXp / 2.0);
        this.easyXp = (int) Math.round(this.easyXp + this.easyXp / 2.0);
        this.hardXp = (int) Math.round(this.hardXp + this.hardXp / 2.0);
        this.extremelyHardXp = (int) Math.round(this.extremelyHardXp + this.extremelyHardXp / 2.0);

        // XP po bitnosti
        this.normalXp = (int) Math.round(this.normalXp + this.normalXp / 2.0);
        this.importantXp = (int) Math.round(this.importantXp + this.importantXp / 2.0);
        this.extremelyImportantXp = (int) Math.round(this.extremelyImportantXp + this.extremelyImportantXp / 2.0);
        this.specialXp = (int) Math.round(this.specialXp + this.specialXp / 2.0);
    }

    public void updateRequiredXp(int prevRequired) {
        double calculated = prevRequired * 2 + prevRequired / 2.0;
        this.requiredXp = (int) Math.ceil(calculated / 100.0) * 100;
    }
}
