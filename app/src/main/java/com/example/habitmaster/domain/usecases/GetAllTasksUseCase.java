package com.example.habitmaster.domain.usecases;

import com.example.habitmaster.data.repositories.TaskRepository;
import com.example.habitmaster.domain.models.Task;

import java.util.List;

public class GetAllTasksUseCase {
    private final TaskRepository localRepo;

    public GetAllTasksUseCase(TaskRepository localRepo) {
        this.localRepo = localRepo;
    }

    public List<Task> execute() {
        return localRepo.getAllTasks();
    }
}
