package com.example.habitmaster.services;

import android.content.Context;
import android.os.Handler;

import com.example.habitmaster.data.dtos.TaskInstanceDTO;
import com.example.habitmaster.data.firebases.FirebaseTaskInstanceRepository;
import com.example.habitmaster.data.firebases.FirebaseTaskRepository;
import com.example.habitmaster.data.firebases.FirebaseUserRepository;
import com.example.habitmaster.data.repositories.CategoryRepository;
import com.example.habitmaster.data.repositories.TaskInstanceRepository;
import com.example.habitmaster.data.repositories.TaskRepository;
import com.example.habitmaster.data.repositories.UserLevelProgressRepository;
import com.example.habitmaster.data.repositories.UserLocalRepository;
import com.example.habitmaster.data.repositories.UserRepository;
import com.example.habitmaster.domain.models.AllianceMissionProgressType;
import com.example.habitmaster.domain.models.TaskDifficulty;
import com.example.habitmaster.domain.models.TaskImportance;
import com.example.habitmaster.domain.models.TaskStatus;
import com.example.habitmaster.domain.usecases.AddUserXpUseCase;
import com.example.habitmaster.domain.usecases.GetUserLevelStartDateUseCase;
import com.example.habitmaster.domain.usecases.tasks.CreateTaskUseCase;
import com.example.habitmaster.domain.usecases.tasks.DeleteTaskUseCase;
import com.example.habitmaster.domain.usecases.tasks.GetUserTasksUseCase;
import com.example.habitmaster.domain.usecases.tasks.UpdateTaskUseCase;
import com.example.habitmaster.utils.Prefs;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskService {
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private CreateTaskUseCase createTaskUseCase;
    private GetUserTasksUseCase getUserTasksUseCase;
    private UpdateTaskUseCase updateTaskUseCase;
    private DeleteTaskUseCase deleteTaskUseCase;
    private AddUserXpUseCase addUserXpUseCase;
    private final GetUserLevelStartDateUseCase getUserLevelStartDateUseCase;
    private final AllianceService allianceService;
    private final Context context;

    public interface Callback {
        void onSuccess();

        void onError(String errorMessage);
    }

    public TaskService(Context context) {
        this.context = context.getApplicationContext();
        TaskRepository localRepo = new TaskRepository(context);
        FirebaseTaskRepository remoteRepo = new FirebaseTaskRepository();
        UserRepository userRepo = new UserRepository(context);
        UserLevelProgressRepository userLevelProgressRepository = new UserLevelProgressRepository(context);
        UserLevelProgressRepository userLevelProgressRepo = new UserLevelProgressRepository(context);
        TaskInstanceRepository localTaskInstanceRepo = new TaskInstanceRepository(context);
        FirebaseTaskInstanceRepository remoteInstanceRepo = new FirebaseTaskInstanceRepository();
        CategoryRepository categoryRepo = new CategoryRepository(context);
        this.createTaskUseCase = new CreateTaskUseCase(localRepo, remoteRepo, userRepo, userLevelProgressRepo, localTaskInstanceRepo, remoteInstanceRepo);
        this.getUserTasksUseCase = new GetUserTasksUseCase(localRepo, localTaskInstanceRepo, userRepo, categoryRepo);
        this.updateTaskUseCase = new UpdateTaskUseCase(localRepo, localTaskInstanceRepo, userLevelProgressRepository, remoteRepo, remoteInstanceRepo);
        this.deleteTaskUseCase = new DeleteTaskUseCase(localTaskInstanceRepo, remoteInstanceRepo);
        this.addUserXpUseCase = new AddUserXpUseCase(new UserLocalRepository(context), new FirebaseUserRepository(context));
        this.getUserLevelStartDateUseCase = new GetUserLevelStartDateUseCase(context);
        this.allianceService = new AllianceService(context);
    }

    public void createTask(String name, String description, String categoryId, String frequency, int repeatInterval, String startDate, String endDate, String executionTime, String difficulty, String importance, Callback callback) {
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

    public void getRepeatingTasks(LocalDate fromDate, ICallback<List<TaskInstanceDTO>> callback) {
        executorService.execute(() -> {
            try {
                List<TaskInstanceDTO> tasks = getUserTasksUseCase.getRepeatingTasks(fromDate);
                new Handler(context.getMainLooper()).post(() -> callback.onSuccess(tasks));
            } catch (Exception e) {
                new Handler(context.getMainLooper()).post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public void getOneTimeTasks(LocalDate fromDate, ICallback<List<TaskInstanceDTO>> callback) {
        executorService.execute(() -> {
            try {
                List<TaskInstanceDTO> tasks = getUserTasksUseCase.getOneTimeTasks(fromDate);
                new Handler(context.getMainLooper()).post(() -> callback.onSuccess(tasks));
            } catch (Exception e) {
                new Handler(context.getMainLooper()).post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public TaskInstanceDTO getTaskById(String id) {
        return getUserTasksUseCase.findTaskInstanceById(id);
    }

    public void updateTaskInfo(TaskInstanceDTO dto, ICallback<TaskInstanceDTO> callback) {
        executorService.execute(() -> {
            try {
                Prefs prefs = new Prefs(context);
                updateTaskUseCase.updateTaskInfo(dto, prefs.getUid(), new ICallback<TaskInstanceDTO>() {
                    @Override
                    public void onSuccess(TaskInstanceDTO result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        callback.onError(errorMessage);
                    }
                });
            } catch (Exception e) {
                callback.onError("Error: " + e.getMessage());
            }
        });
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
                if (dto.getDifficulty() == TaskDifficulty.EASY && dto.getImportance() == TaskImportance.NORMAL) {
                    allianceService.tryUpdateAllianceProgress(userId, AllianceMissionProgressType.SOLVED_TASK2);
                } else if (dto.getDifficulty() == TaskDifficulty.VERY_EASY
                        || dto.getDifficulty() == TaskDifficulty.EASY
                        || dto.getImportance() == TaskImportance.NORMAL
                        || dto.getImportance() == TaskImportance.IMPORTANT) {
                    allianceService.tryUpdateAllianceProgress(userId, AllianceMissionProgressType.SOLVED_TASK1);
                } else {
                    allianceService.tryUpdateAllianceProgress(userId, AllianceMissionProgressType.SOLVED_OTHER_TASK);
                }

                addUserXpUseCase.execute(userId, dto.getXpValue());
                callback.onSuccess();
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    public boolean existsUserTaskByCategoryId(String userId, String categoryId) {
        return getUserTasksUseCase.existsUserTaskByCategoryId(userId, categoryId);
    }

    public double getUserStageSuccessRate(String userId) {
        var levelStartDate = getUserLevelStartDateUseCase.execute(userId);
        var valuableTaskInstances = getUserTasksUseCase.getValuableUserTaskInstances(userId, levelStartDate, LocalDate.now());

        if (valuableTaskInstances == null || valuableTaskInstances.isEmpty()) {
            return 0.0;
        }

        long excluded = valuableTaskInstances.stream()
                .filter(ti -> ti.getStatus() == TaskStatus.PAUSED || ti.getStatus() == TaskStatus.CANCELLED)
                .count();

        long total = valuableTaskInstances.size() - excluded;
        if (total == 0) {
            return 0.0;
        }

        long completed = valuableTaskInstances.stream()
                .filter(ti -> ti.getStatus() == TaskStatus.COMPLETED)
                .count();

        return (double) completed / total;
    }
}
