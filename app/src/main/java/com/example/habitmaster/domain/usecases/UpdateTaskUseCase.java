package com.example.habitmaster.domain.usecases;

import com.example.habitmaster.data.dtos.TaskInstanceDTO;
import com.example.habitmaster.data.repositories.TaskInstanceRepository;
import com.example.habitmaster.data.repositories.TaskRepository;
import com.example.habitmaster.data.repositories.UserLevelProgressRepository;
import com.example.habitmaster.domain.models.Task;
import com.example.habitmaster.domain.models.TaskStatus;
import com.example.habitmaster.domain.models.UserLevelProgress;
import com.example.habitmaster.utils.Prefs;

public class UpdateTaskUseCase {
    private final TaskRepository taskRepo;
    private final TaskInstanceRepository taskInstanceRepo;
    private final UserLevelProgressRepository userLevelProgressRepository;

    public interface Callback {
        void onSuccess();

        void onError(String errorMessage);
    }

    public UpdateTaskUseCase(TaskRepository taskRepo, TaskInstanceRepository taskInstanceRepo, UserLevelProgressRepository userLevelProgressRepository) {
        this.taskRepo = taskRepo;
        this.taskInstanceRepo = taskInstanceRepo;
        this.userLevelProgressRepository = userLevelProgressRepository;
    }

    // TODO: Update only future tasks
    // Currently all task instances (previous and future) are updated
    public TaskInstanceDTO updateTaskInfo(TaskInstanceDTO dto, String userId) {
        // izracunavanje xp-a
        UserLevelProgress progress = userLevelProgressRepository.getUserLevelProgress(userId);
        int xpValue = dto.getDifficulty().getXpValue(progress) + dto.getImportance().getXpValue(progress);
        dto.setXpValue(xpValue);

        var task = taskRepo.findTaskById(dto.getTaskId());

        if (task == null) {
            throw new IllegalArgumentException("Task not found with id: " + dto.getTaskId());
        }

        task.setName(dto.getName());
        task.setDescription(dto.getDescription());
        task.setExecutionTime(dto.getExecutionTime());
        task.setDifficulty(dto.getDifficulty());
        task.setImportance(dto.getImportance());

        taskRepo.update(task);

        return dto;
    }

    public void updateTaskInstanceStatus(String taskInstanceId, TaskStatus newStatus, Callback callback) {
        var taskInstance = taskInstanceRepo.findById(taskInstanceId);

        if (taskInstance == null) {
            callback.onError("Task not found with id: " + taskInstanceId);
            return;
        }

        if (taskInstance.getStatus() == TaskStatus.MISSED
                || taskInstance.getStatus() == TaskStatus.CANCELLED
                || taskInstance.getStatus() == newStatus) {
            callback.onError("Task status cannot be changed");
            return;
        }

        if (taskInstance.getStatus() == TaskStatus.PAUSED
                && newStatus == TaskStatus.COMPLETED) {
            callback.onError("Task is paused");
            return;
        }

        if (taskInstanceRepo.updateStatus(taskInstanceId, newStatus))
            callback.onSuccess();
        else
            callback.onError("Task was more than 3 days before");
    }
}
