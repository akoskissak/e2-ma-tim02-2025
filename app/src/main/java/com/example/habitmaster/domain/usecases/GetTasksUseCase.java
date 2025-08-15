package com.example.habitmaster.domain.usecases;

import com.example.habitmaster.data.repositories.TaskRepository;
import com.example.habitmaster.domain.models.Task;

import java.util.List;

public class GetTasksUseCase {
    private final TaskRepository localRepo;

    public GetTasksUseCase(TaskRepository localRepo) {
        this.localRepo = localRepo;
    }

    public List<Task> getAllTasks() {
        return localRepo.getAllTasks();
    }

    public List<Task> getRepeatingTasks() {
        return localRepo.getRepeatingTasks();
    }

    public List<Task> getOneTimeTasks() {
        return localRepo.getOneTimeTasks();
    }
}
