package com.example.habitmaster.domain.models;

public enum TaskDifficulty {
    VERY_EASY("Very easy"),
    EASY("Easy"),
    HARD("Hard"),
    EXTREMELY_HARD("Extremely hard");

    private final String displayName;

    TaskDifficulty(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static TaskDifficulty fromDisplayName(String name) {
        for (TaskDifficulty d : values()) {
            if (d.displayName.equalsIgnoreCase(name)) {
                return d;
            }
        }
        throw new IllegalArgumentException("Unknown difficulty: " + name);
    }

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
