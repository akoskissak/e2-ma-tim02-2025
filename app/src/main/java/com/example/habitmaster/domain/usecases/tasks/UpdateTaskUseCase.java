package com.example.habitmaster.domain.usecases.tasks;

import android.content.Context;

import com.example.habitmaster.data.dtos.TaskInstanceDTO;
import com.example.habitmaster.data.firebases.FirebaseTaskInstanceRepository;
import com.example.habitmaster.data.firebases.FirebaseTaskRepository;
import com.example.habitmaster.data.repositories.TaskInstanceRepository;
import com.example.habitmaster.data.repositories.TaskRepository;
import com.example.habitmaster.data.repositories.UserLevelProgressRepository;
import com.example.habitmaster.domain.models.Task;
import com.example.habitmaster.domain.models.TaskFrequency;
import com.example.habitmaster.domain.models.TaskInstance;
import com.example.habitmaster.domain.models.TaskStatus;
import com.example.habitmaster.domain.models.UserLevelProgress;
import com.example.habitmaster.domain.usecases.alliances.userMissions.CheckUnresolvedTasksUseCase;
import com.example.habitmaster.services.ICallback;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class UpdateTaskUseCase {
    private final TaskRepository taskRepo;
    private final TaskInstanceRepository taskInstanceRepo;
    private final FirebaseTaskRepository remoteTaskRepo;
    private final FirebaseTaskInstanceRepository remoteInstanceRepository;
    private final UserLevelProgressRepository userLevelProgressRepository;

    public interface Callback {
        void onSuccess();

        void onError(String errorMessage);
    }

    public UpdateTaskUseCase(TaskRepository taskRepo, TaskInstanceRepository taskInstanceRepo, UserLevelProgressRepository userLevelProgressRepository,
                             FirebaseTaskRepository remoteRepo, FirebaseTaskInstanceRepository remoteInstanceRepo, Context context) {
        this.taskRepo = taskRepo;
        this.taskInstanceRepo = taskInstanceRepo;
        this.userLevelProgressRepository = userLevelProgressRepository;
        this.remoteTaskRepo = remoteRepo;
        this.remoteInstanceRepository = remoteInstanceRepo;
    }

    public void updateTaskInfo(TaskInstanceDTO dto, String userId, ICallback<TaskInstanceDTO> callback) {
        new Thread(() -> {
            try {
                // izracunavanje xp-a
                UserLevelProgress progress = userLevelProgressRepository.getUserLevelProgress(userId);
                int xpValue = dto.getDifficulty().getXpValue(progress) + dto.getImportance().getXpValue(progress);
                dto.setXpValue(xpValue);

                var task = taskRepo.findTaskById(dto.getTaskId());
                if (task == null) {
                    callback.onError("Task not found with id: " + dto.getTaskId());
                    return;
                }

                boolean isChanged =
                    !task.getName().equals(dto.getName()) ||
                    !task.getDescription().equals(dto.getDescription()) ||
                    !Objects.equals(task.getExecutionTime(), dto.getExecutionTime()) ||
                    task.getDifficulty() != dto.getDifficulty() ||
                    task.getImportance() != dto.getImportance() ||
                    !Objects.equals(task.getStartDate(), dto.getDate());

                if (!isChanged) {
                    callback.onSuccess(dto);
                    return;
                }

                if (task.getFrequency() == TaskFrequency.ONCE && !task.getStartDate().isBefore(LocalDate.now())) {
                    task.setName(dto.getName());
                    task.setDescription(dto.getDescription());
                    task.setExecutionTime(dto.getExecutionTime());
                    task.setDifficulty(dto.getDifficulty());
                    task.setImportance(dto.getImportance());
                    taskRepo.update(task);
                    remoteTaskRepo.update(task);
                } else if (!dto.getDate().isBefore(LocalDate.now())) {
                    var newTask = new Task(UUID.randomUUID().toString(), userId, dto.getName(), dto.getDescription(),
                            task.getCategoryId(), task.getFrequency(), task.getRepeatInterval(),
                            dto.getDate(), task.getEndDate(), dto.getExecutionTime(),
                            dto.getDifficulty(), dto.getImportance());
                    taskRepo.insert(newTask);
                    remoteTaskRepo.insert(newTask);

                    var instances = taskInstanceRepo.getByTaskIdFromDate(task.getId(), dto.getDate());
                    List<TaskInstance> instancesToUpdate = new ArrayList<>();
                    for (var instance : instances) {
                        if (instance.getStatus() == TaskStatus.ACTIVE || instance.getStatus() == TaskStatus.PAUSED) {
                            instance.setTaskId(newTask.getId());
                            instancesToUpdate.add(instance);
                        }
                    }
                    taskInstanceRepo.updateAll(instancesToUpdate);
                    remoteInstanceRepository.updateAll(instancesToUpdate);
                }

                callback.onSuccess(dto);
            } catch (Exception e) {
                callback.onError("Failed to update task: " + e.getMessage());
            }
        }).start();
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

        if (newStatus == TaskStatus.COMPLETED) {
            if (taskInstance.getStatus() == TaskStatus.PAUSED) {
                callback.onError("Task is paused");
                return;
            } else {
                var threeDaysAgo = LocalDate.now().minusDays(3);
                if (taskInstance.getDate().isBefore(threeDaysAgo) || taskInstance.getDate().isAfter(LocalDate.now())) {
                    callback.onError("Cannot complete future tasks");
                    return;
                }
            }
        }

        if (taskInstanceRepo.updateStatus(taskInstanceId, newStatus)){
            remoteInstanceRepository.updateStatus(taskInstanceId, newStatus);
            callback.onSuccess();
        }
        else
            callback.onError("Task was more than 3 days before");
    }

    public void markTaskInstanceAsMissed(TaskInstance instance) {
        if (taskInstanceRepo.updateStatus(instance.getId(), TaskStatus.MISSED)){
            remoteInstanceRepository.updateStatus(instance.getId(), TaskStatus.MISSED);
        }
    }
}
