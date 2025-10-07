package com.example.habitmaster.domain.usecases.tasks;

import android.content.Context;

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

    public CreateTaskUseCase(Context context) {
        this.localRepo = new TaskRepository(context);
        this.remoteRepo = new FirebaseTaskRepository();
        this.userRepository = new UserRepository(context);
        this.localInstanceRepo = new TaskInstanceRepository(context);
        this.remoteTaskInstanceRepository = new FirebaseTaskInstanceRepository();
        this.userLevelProgressRepository = new UserLevelProgressRepository(context);
    }

    public void execute(String name, String description, String categoryId, String frequencyStr, int repeatInterval, String startDateStr, String endDateStr,
                        String executionTimeStr, String difficultyStr, String importanceStr, Callback callback) {

        if (!validateTaskData(name, categoryId, frequencyStr, startDateStr, endDateStr, executionTimeStr, callback)) {
            return;
        }

        TaskDifficulty difficulty;
        TaskImportance importance;
        TaskFrequency frequency;
        LocalDate startDate = null;
        LocalDate endDate = null;
        LocalTime executionTime = null;

        try {
            difficulty = TaskDifficulty.fromDisplayName(difficultyStr);
        } catch (IllegalArgumentException e) {
            difficulty = TaskDifficulty.EASY;
        }

        try {
            importance = TaskImportance.fromDisplayName(importanceStr);
        } catch (IllegalArgumentException e) {
            importance = TaskImportance.NORMAL;
        }

        try {
            frequency = TaskFrequency.valueOf(frequencyStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            frequency = TaskFrequency.DAILY;
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

        int createdCount;

        String userId = userRepository.currentUid();
        String id = UUID.randomUUID().toString();
        Task task = new Task(id, userId, name, description, categoryId, frequency, repeatInterval, startDate, endDate, executionTime, difficulty, importance);
        UserLevelProgress progress = userLevelProgressRepository.getUserLevelProgress(userId);
        task.calculateXp(progress);

        if (difficulty == TaskDifficulty.VERY_EASY
                && importance == TaskImportance.NORMAL) {
            createdCount = localInstanceRepo.countTasksByDifficultyImportanceAndPeriod(difficulty, importance, LocalDate.now(), LocalDate.now());
            if (createdCount >= 5) {
                task.setXpValue(0);
            }
        } else if (difficulty == TaskDifficulty.EASY
                && importance == TaskImportance.IMPORTANT) {
            createdCount = localInstanceRepo.countTasksByDifficultyImportanceAndPeriod(difficulty, importance, LocalDate.now(), LocalDate.now());
            if (createdCount >= 5) {
                task.setXpValue(0);
            }
        } else if (difficulty == TaskDifficulty.HARD
                && importance == TaskImportance.EXTREMELY_IMPORTANT) {
            createdCount = localInstanceRepo.countTasksByDifficultyImportanceAndPeriod(difficulty, importance, LocalDate.now(), LocalDate.now());
            if (createdCount >= 2) {
                task.setXpValue(0);
            }
        } else if (difficulty == TaskDifficulty.EXTREMELY_HARD) {
            createdCount = localInstanceRepo.countTasksByDifficultyImportanceAndPeriod(difficulty, importance, LocalDate.now().minusWeeks(1), LocalDate.now());
            if (createdCount >= 1) {
                task.setXpValue(0);
            }
        } else if (importance == TaskImportance.SPECIAL) {
            createdCount = localInstanceRepo.countTasksByDifficultyImportanceAndPeriod(difficulty, importance, LocalDate.now().minusMonths(1), LocalDate.now());
            if (createdCount >= 1) {
                task.setXpValue(0);
            }
        }

        try {
            localRepo.insert(task);
            remoteRepo.insert(task);

            List<LocalDate> dates = generateDatesForTask(task);
            for (LocalDate date : dates) {
                TaskInstance taskInstance = new TaskInstance(
                        UUID.randomUUID().toString(),
                        task.getId(),
                        date,
                        LocalDate.now(),
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

    private boolean validateTaskData(String name, String categoryId, String frequencyStr, String startDateStr, String endDateStr, String executionTimeStr, Callback callback) {
        if (name == null || name.trim().isEmpty()) {
            callback.onError("Task name cannot be empty");
            return false;
        }
        if (categoryId == null || categoryId.trim().isEmpty()) {
            callback.onError("Category must be selected");
            return false;
        }
        if (frequencyStr == null || frequencyStr.trim().isEmpty()) {
            callback.onError("Frequency must be selected");
            return false;
        }
        if (executionTimeStr == null || executionTimeStr.trim().isEmpty()) {
            callback.onError("Execution time must be selected");
            return false;
        }
        return true;
    }

    private List<LocalDate> generateDatesForTask(Task task) {
        List<LocalDate> dates = new ArrayList<>();

        LocalDate start = task.getStartDate();
        LocalDate end = task.getEndDate() != null ? task.getEndDate() : start;

        if (start == null) {
            return dates;
        }

        if (task.getFrequency() == TaskFrequency.ONCE) {
            dates.add(start);
            return dates;
        }

        int interval = task.getRepeatInterval();
        int multiplier = 0;
        LocalDate current;

        while (true) {
            switch (task.getFrequency()) {
                case DAILY:
                    current = start.plusDays(interval * multiplier);
                    break;
                case WEEKLY:
                    current = start.plusWeeks(interval * multiplier);
                    break;
                case MONTHLY:
                    current = start.plusMonths(interval * multiplier);
                    break;
                case YEARLY:
                    current = start.plusYears(interval * multiplier);
                    break;
                default:
                    return dates;
            }

            if (current.isAfter(end)) {
                break;
            }

            dates.add(current);
            multiplier++;
        }

        return dates;
    }

}
