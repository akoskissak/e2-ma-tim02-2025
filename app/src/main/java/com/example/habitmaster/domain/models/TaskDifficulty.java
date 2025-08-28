package com.example.habitmaster.domain.models;

public enum TaskDifficulty {
    VERY_EASY,
    EASY,
    HARD,
    EXTREMELY_HARD;

    public int getXpValue(UserLevelProgress progress) {
        switch (this) {
            case VERY_EASY:
                return progress.getVeryEasyXp();
            case EASY:
                return progress.getEasyXp();
            case HARD:
                return progress.getHardXp();
            case EXTREMELY_HARD:
                return progress.getExtremelyHardXp();
            default:
                throw new IllegalArgumentException("Unknown difficulty level");
        }
    }
}
