package com.example.habitmaster.domain.usecases;

import com.example.habitmaster.data.firebases.FirebaseTaskRepository;
import com.example.habitmaster.data.repositories.TaskRepository;
import com.example.habitmaster.data.repositories.UserRepository;
import com.example.habitmaster.domain.models.Task;
import com.example.habitmaster.domain.models.TaskDifficulty;
import com.example.habitmaster.domain.models.TaskFrequency;
import com.example.habitmaster.domain.models.TaskImportance;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.UUID;

import javax.security.auth.callback.Callback;

public class CreateTaskUseCase {
    private final TaskRepository localRepo;
    private final UserRepository userRepository;
    private final FirebaseTaskRepository remoteRepo;

    public interface Callback {
        void onSuccess();
        void onError(String errorMessage);
    }

    public CreateTaskUseCase(TaskRepository localRepo, FirebaseTaskRepository remoteRepo,
                             UserRepository userRepository) {
        this.localRepo = localRepo;
        this.remoteRepo = remoteRepo;
        this.userRepository = userRepository;
    }

    public void execute(
            String name,
            String description,
            int categoryId,
            String frequencyStr,
            int repeatInterval,
            String startDateStr,
            String endDateStr,
            String difficultyStr,
            String importanceStr,
            Callback callback
    ) {
        TaskDifficulty difficulty;
        TaskImportance importance;
        TaskFrequency frequency;
        LocalDate startDate = null;
        LocalDate endDate = null;

        try {
            difficulty = TaskDifficulty.valueOf(difficultyStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            difficulty = TaskDifficulty.EASY; // fallback default
        }

        try {
            importance = TaskImportance.valueOf(importanceStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            importance = TaskImportance.NORMAL; // fallback default
        }

        try {
            frequency = TaskFrequency.valueOf(frequencyStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            frequency = TaskFrequency.DAILY; // fallback default
        }

        if (frequency != TaskFrequency.ONCE) {
            try {
                startDate = LocalDate.parse(startDateStr);
                endDate = LocalDate.parse(endDateStr);
            } catch (DateTimeParseException e) {
                e.printStackTrace();
            }
        } else {
            try {
                startDate = LocalDate.parse(startDateStr);
                endDate = startDate;
            } catch (DateTimeParseException e) {
                e.printStackTrace();
            }
        }

        int createdCount = localRepo.getTasksCountByDifficultyAndImportance(difficulty, importance);
        int xpValue;

        if (difficulty == TaskDifficulty.VERY_EASY
                && importance == TaskImportance.NORMAL
                && createdCount >= 5) {
            xpValue = 0;
        } else if (difficulty == TaskDifficulty.EASY
                && importance == TaskImportance.IMPORTANT
                && createdCount >= 5) {
            xpValue = 0;
        } else if (difficulty == TaskDifficulty.HARD
                && importance == TaskImportance.EXTREMELY_IMPORTANT
                && createdCount >= 2) {
            xpValue = 0;
        } else if (difficulty == TaskDifficulty.EXTREMELY_HARD
                && createdCount >= 1) {
            xpValue = 0;
        } else if (importance == TaskImportance.SPECIAL
                && createdCount > 1) {
            xpValue = 0;
        } else {
            xpValue = 0;
        }

        String userId = userRepository.currentUid();
        String id = UUID.randomUUID().toString();
        Task task = new Task(
                id,
                userId,
                name,
                description,
                categoryId,
                frequency,
                repeatInterval,
                startDate,
                endDate,
                difficulty,
                importance
        );

        task.calculateXp();

        try {
            localRepo.insert(task);
            remoteRepo.insert(task);

            callback.onSuccess();
        } catch (Exception e) {
            callback.onError("Failed to create task: " + e.getMessage());
        }
    }
}
