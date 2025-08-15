package com.example.habitmaster.domain.usecases;

import com.example.habitmaster.data.repositories.TaskRepository;
import com.example.habitmaster.data.repositories.UserRepository;
import com.example.habitmaster.domain.models.Task;

import java.util.List;

public class GetUserTasksUseCase {
    private final TaskRepository localRepo;
    private final UserRepository userRepository;

    public GetUserTasksUseCase(TaskRepository localRepo, UserRepository userRepo) {
        this.localRepo = localRepo;
        this.userRepository = userRepo;
    }

    public List<Task> getAllTasks() {
        return localRepo.getAllUserTasks(userRepository.currentUid());
    }

    public List<Task> getRepeatingTasks() {
        return localRepo.getRepeatingUserTasks(userRepository.currentUid());
    }

    public List<Task> getOneTimeTasks() {
        return localRepo.getOneTimeUserTasks(userRepository.currentUid());
    }
}
