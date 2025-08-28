package com.example.habitmaster.domain.models;

public enum TaskImportance {
    NORMAL("Normal"),
    IMPORTANT("Important"),
    EXTREMELY_IMPORTANT("Extremely important"),
    SPECIAL("Special");

    private final String displayName;

    TaskImportance(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static TaskImportance fromDisplayName(String name) {
        for (TaskImportance importance : values()) {
            if (importance.displayName.equalsIgnoreCase(name)) {
                return importance;
            }
        }
        throw new IllegalArgumentException("Unknown importance: " + name);
    }

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
