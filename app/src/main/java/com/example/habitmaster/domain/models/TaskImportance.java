package com.example.habitmaster.domain.models;

public enum TaskImportance {
    NORMAL,
    IMPORTANT,
    EXTREMELY_IMPORTANT,
    SPECIAL;

    public int getXpValue(UserLevelProgress progress) {
        switch (this) {
            case NORMAL:
                return progress.getNormalXp();
            case IMPORTANT:
                return progress.getImportantXp();
            case EXTREMELY_IMPORTANT:
                return progress.getExtremelyImportantXp();
            case SPECIAL:
                return progress.getSpecialXp();
            default:
                throw new IllegalArgumentException("Unknown importance level");
        }
    }
}
