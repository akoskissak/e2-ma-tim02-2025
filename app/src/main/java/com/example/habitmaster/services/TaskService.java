package com.example.habitmaster.services;

import android.content.Context;

import com.example.habitmaster.data.dtos.TaskInstanceDTO;
import com.example.habitmaster.data.firebases.FirebaseTaskInstanceRepository;
import com.example.habitmaster.data.firebases.FirebaseTaskRepository;
import com.example.habitmaster.data.repositories.TaskInstanceRepository;
import com.example.habitmaster.data.repositories.TaskRepository;
import com.example.habitmaster.data.repositories.UserLocalRepository;
import com.example.habitmaster.data.repositories.UserRepository;
import com.example.habitmaster.domain.models.TaskStatus;
import com.example.habitmaster.domain.usecases.AddUserXpUseCase;
import com.example.habitmaster.domain.usecases.CreateTaskUseCase;
import com.example.habitmaster.domain.usecases.DeleteTaskUseCase;
import com.example.habitmaster.domain.usecases.GetUserTasksUseCase;
import com.example.habitmaster.domain.usecases.UpdateTaskUseCase;

import java.time.LocalDate;
import java.util.List;

public class TaskService {
    private CreateTaskUseCase createTaskUseCase;
    private GetUserTasksUseCase getUserTasksUseCase;
    private UpdateTaskUseCase updateTaskUseCase;
    private DeleteTaskUseCase deleteTaskUseCase;
    private AddUserXpUseCase addUserXpUseCase;

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
        this.updateTaskUseCase = new UpdateTaskUseCase(localRepo, localTaskInstanceRepo);
        this.deleteTaskUseCase = new DeleteTaskUseCase(localTaskInstanceRepo);
        this.addUserXpUseCase = new AddUserXpUseCase(new UserLocalRepository(context));
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

    public List<TaskInstanceDTO> getRepeatingTasks(LocalDate fromDate) {
        return getUserTasksUseCase.getRepeatingTasks(fromDate);
    }

    public List<TaskInstanceDTO> getOneTimeTasks(LocalDate fromDate) {
        return getUserTasksUseCase.getOneTimeTasks(fromDate);
    }

    public TaskInstanceDTO getTaskById(String id) { return getUserTasksUseCase.findTaskInstanceById(id); }

    public TaskInstanceDTO updateTaskInfo(TaskInstanceDTO dto) {
        return updateTaskUseCase.updateTaskInfo(dto);
    }

    public void deleteTask(String taskId, Callback callback) {
        deleteTaskUseCase.execute(taskId, new DeleteTaskUseCase.Callback() {
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

    public void updateTaskStatus(String userId, String taskInstanceId, TaskStatus newStatus, Callback callback) {
        updateTaskUseCase.updateTaskInstanceStatus(taskInstanceId, newStatus, new UpdateTaskUseCase.Callback() {
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

    public void completeTask(String userId, TaskInstanceDTO dto, Callback callback) {
        updateTaskStatus(userId, dto.getId(), TaskStatus.COMPLETED, new Callback() {
            @Override
            public void onSuccess() {
                addUserXpUseCase.execute(userId, dto.getXpValue());
                callback.onSuccess();
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
}
