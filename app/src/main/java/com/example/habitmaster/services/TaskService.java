package com.example.habitmaster.services;

import android.content.Context;
import android.telecom.Call;

import com.example.habitmaster.data.firebases.FirebaseTaskRepository;
import com.example.habitmaster.data.repositories.TaskRepository;
import com.example.habitmaster.domain.models.Task;
import com.example.habitmaster.domain.usecases.CreateTaskUseCase;
import com.example.habitmaster.domain.usecases.GetAllTasksUseCase;

import java.util.List;

public class TaskService {
    private CreateTaskUseCase createTaskUseCase;
    private GetAllTasksUseCase getAllTasksUseCase;

    public interface Callback {
        void onSuccess();
        void onError(String errorMessage);
    }

    public TaskService(Context context) {
        TaskRepository localRepo = new TaskRepository(context);
        FirebaseTaskRepository remoteRepo = new FirebaseTaskRepository();
        this.createTaskUseCase = new CreateTaskUseCase(localRepo, remoteRepo);
        this.getAllTasksUseCase = new GetAllTasksUseCase(localRepo);
    }

    public void createTask(
            String name,
            String description,
            int categoryId,
            String frequency,
            int repeatInterval,
            String startDate,
            String endDate,
            String difficulty,
            String importance,
            Callback callback
    ) {
        createTaskUseCase.execute(name, description, categoryId, frequency, repeatInterval, startDate, endDate, difficulty, importance,
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

    public List<Task> getAllTasks() {
        return getAllTasksUseCase.execute();
    }
}
