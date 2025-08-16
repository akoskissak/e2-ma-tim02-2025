package com.example.habitmaster.services;

import android.content.Context;

import com.example.habitmaster.data.dtos.TaskInstanceDTO;
import com.example.habitmaster.data.firebases.FirebaseTaskInstanceRepository;
import com.example.habitmaster.data.firebases.FirebaseTaskRepository;
import com.example.habitmaster.data.repositories.TaskInstanceRepository;
import com.example.habitmaster.data.repositories.TaskRepository;
import com.example.habitmaster.data.repositories.UserRepository;
import com.example.habitmaster.domain.models.Task;
import com.example.habitmaster.domain.usecases.CreateTaskUseCase;
import com.example.habitmaster.domain.usecases.GetUserTasksUseCase;

import java.util.List;

public class TaskService {
    private CreateTaskUseCase createTaskUseCase;
    private GetUserTasksUseCase getUserTasksUseCase;

    public interface Callback {
        void onSuccess();
        void onError(String errorMessage);
    }

    public TaskService(Context context) {
        TaskRepository localRepo = new TaskRepository(context);
        FirebaseTaskRepository remoteRepo = new FirebaseTaskRepository();
        UserRepository userRepo = new UserRepository(context);
        TaskInstanceRepository localTaskInstanceRepo = new TaskInstanceRepository(context);
        FirebaseTaskInstanceRepository remoteInstanceRepo = new FirebaseTaskInstanceRepository();
        this.createTaskUseCase = new CreateTaskUseCase(localRepo, remoteRepo, userRepo, localTaskInstanceRepo, remoteInstanceRepo);
        this.getUserTasksUseCase = new GetUserTasksUseCase(localRepo, localTaskInstanceRepo, userRepo);
    }

    public void createTask(
            String name,
            String description,
            int categoryId,
            String frequency,
            int repeatInterval,
            String startDate,
            String endDate,
            String executionTime,
            String difficulty,
            String importance,
            Callback callback
    ) {
        createTaskUseCase.execute(name, description, categoryId, frequency, repeatInterval, startDate, endDate, executionTime, difficulty, importance,
            new CreateTaskUseCase.Callback() {
                @Override
                public void onSuccess() {
                    callback.onSuccess();
                }

                @Override
                public void onError(String errorMessage) {
                    callback.onError(errorMessage);
                }
            });
    }

    public List<TaskInstanceDTO> getAllTasks() {
        return getUserTasksUseCase.getAllTasks();
    }

    public List<TaskInstanceDTO> getRepeatingTasks() {
        return getUserTasksUseCase.getRepeatingTasks();
    }

    public List<TaskInstanceDTO> getOneTimeTasks() {
        return getUserTasksUseCase.getOneTimeTasks();
    }

    public TaskInstanceDTO getTaskById(String id) { return getUserTasksUseCase.getTaskById(id); }
}
