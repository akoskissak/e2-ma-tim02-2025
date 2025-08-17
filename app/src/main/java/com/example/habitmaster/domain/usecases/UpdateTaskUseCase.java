package com.example.habitmaster.domain.usecases;

import com.example.habitmaster.data.dtos.TaskInstanceDTO;
import com.example.habitmaster.data.repositories.TaskInstanceRepository;
import com.example.habitmaster.data.repositories.TaskRepository;

public class UpdateTaskUseCase {
    private final TaskRepository taskRepo;
    public UpdateTaskUseCase(TaskRepository taskRepo, TaskInstanceRepository taskInstanceRepo) {
        this.taskRepo = taskRepo;
    }

    // TODO: Update only future tasks
    // Currently all task instances (previous and future) are updated
    public TaskInstanceDTO execute(TaskInstanceDTO dto) {
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
}
