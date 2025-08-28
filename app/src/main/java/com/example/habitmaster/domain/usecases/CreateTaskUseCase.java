package com.example.habitmaster.domain.usecases;

import com.example.habitmaster.data.firebases.FirebaseTaskInstanceRepository;
import com.example.habitmaster.data.firebases.FirebaseTaskRepository;
import com.example.habitmaster.data.repositories.TaskInstanceRepository;
import com.example.habitmaster.data.repositories.TaskRepository;
import com.example.habitmaster.data.repositories.UserLevelProgressRepository;
import com.example.habitmaster.data.repositories.UserRepository;
import com.example.habitmaster.domain.models.Task;
import com.example.habitmaster.domain.models.TaskDifficulty;
import com.example.habitmaster.domain.models.TaskFrequency;
import com.example.habitmaster.domain.models.TaskImportance;
import com.example.habitmaster.domain.models.TaskInstance;
import com.example.habitmaster.domain.models.TaskStatus;
import com.example.habitmaster.domain.models.UserLevelProgress;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateTaskUseCase {
    private final TaskRepository localRepo;
    private final TaskInstanceRepository localInstanceRepo;
    private final UserLevelProgressRepository userLevelProgressRepository;
    private final UserRepository userRepository;
    private final FirebaseTaskRepository remoteRepo;
    private final FirebaseTaskInstanceRepository remoteTaskInstanceRepository;

    public interface Callback {
        void onSuccess();
        void onError(String errorMessage);
    }

    public CreateTaskUseCase(TaskRepository localRepo, FirebaseTaskRepository remoteRepo,
                             UserRepository userRepository,
                             UserLevelProgressRepository userLevelProgressRepository,
                             TaskInstanceRepository localInstanceRepo,
                             FirebaseTaskInstanceRepository remoteTaskInstanceRepository) {
        this.localRepo = localRepo;
        this.remoteRepo = remoteRepo;
        this.userRepository = userRepository;
        this.localInstanceRepo = localInstanceRepo;
        this.remoteTaskInstanceRepository = remoteTaskInstanceRepository;
        this.userLevelProgressRepository = userLevelProgressRepository;
    }

    public void execute(
            String name,
            String description,
            String categoryId,
            String frequencyStr,
            int repeatInterval,
            String startDateStr,
            String endDateStr,
            String executionTimeStr,
            String difficultyStr,
            String importanceStr,
            Callback callback
    ) {
        TaskDifficulty difficulty;
        TaskImportance importance;
        TaskFrequency frequency;
        LocalDate startDate = null;
        LocalDate endDate = null;
        LocalTime executionTime = null;

        try {
            difficulty = TaskDifficulty.fromDisplayName(difficultyStr);
        } catch (IllegalArgumentException e) {
            difficulty = TaskDifficulty.EASY; // fallback default
        }

        try {
            importance = TaskImportance.fromDisplayName(importanceStr);
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

        try {
            executionTime = LocalTime.parse(executionTimeStr);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
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
                executionTime,
                difficulty,
                importance
        );
        UserLevelProgress progress = userLevelProgressRepository.getUserLevelProgress(userId);
        task.calculateXp(progress);

        try {
            localRepo.insert(task);
            remoteRepo.insert(task);

            List<LocalDate> dates = generateDatesForTask(task);
            for (LocalDate date : dates) {
                TaskInstance taskInstance = new TaskInstance(
                    UUID.randomUUID().toString(),
                    task.getId(),
                    date,
                    TaskStatus.ACTIVE
                );
                localInstanceRepo.insert(taskInstance);
                remoteTaskInstanceRepository.insert(taskInstance);
            }


            callback.onSuccess();
        } catch (Exception e) {
            callback.onError("Failed to create task: " + e.getMessage());
        }
    }

    private List<LocalDate> generateDatesForTask(Task task) {
        List<LocalDate> dates = new ArrayList<>();

        LocalDate start = task.getStartDate();
        LocalDate end = task.getEndDate() != null ? task.getEndDate() : start;

        if (start == null) {
            return dates;
        }

        LocalDate current = start;
        while (!current.isAfter(end)) {
            dates.add(current);

            switch (task.getFrequency()) {
                case DAILY:
                    current = current.plusDays(task.getRepeatInterval());
                    break;
                case WEEKLY:
                    current = current.plusWeeks(task.getRepeatInterval());
                    break;
                case MONTHLY:
                    current = current.plusMonths(task.getRepeatInterval());
                    break;
                case ONCE:
                default:
                    current = end.plusDays(1); // prekid
                    break;
            }
        }
        return dates;
    }

}
