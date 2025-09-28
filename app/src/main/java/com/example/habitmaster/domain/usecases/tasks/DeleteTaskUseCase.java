package com.example.habitmaster.domain.usecases.tasks;

import android.content.Context;

import com.example.habitmaster.data.firebases.FirebaseTaskInstanceRepository;
import com.example.habitmaster.data.firebases.FirebaseTaskRepository;
import com.example.habitmaster.data.repositories.TaskInstanceRepository;
import com.example.habitmaster.data.repositories.TaskRepository;
import com.example.habitmaster.domain.models.TaskFrequency;

import java.time.LocalDate;

public class DeleteTaskUseCase {
    private final TaskInstanceRepository localInstanceRepo;
    private final FirebaseTaskInstanceRepository remoteInstanceRepository;
    private final TaskRepository localTaskRepo;
    private final FirebaseTaskRepository remoteTaskRepo;

    public interface Callback {
        void onSuccess();

        void onError(String errorMessage);
    }

    public DeleteTaskUseCase(Context context) {
        this.localInstanceRepo = new TaskInstanceRepository(context);
        this.remoteInstanceRepository = new FirebaseTaskInstanceRepository();
        this.localTaskRepo = new TaskRepository(context);
        this.remoteTaskRepo = new FirebaseTaskRepository();
    }

    public void execute(String taskInstanceId, String taskId, LocalDate date, Callback callback) {
        try {
            var task = localTaskRepo.findTaskById(taskId);
            if (task == null) {
                callback.onError("Task not found or could not be deleted");
                return;
            }

            remoteInstanceRepository.deleteFutureTaskInstances(task.getId(), date);
            boolean deleted = localInstanceRepo.deleteFutureTaskInstances(task.getId(), date);
            if (deleted) {
                if (task.getFrequency() == TaskFrequency.ONCE) {
                    localTaskRepo.deleteTask(taskId);
                    remoteTaskRepo.deleteTask(taskId);
                }

                callback.onSuccess();
            } else {
                callback.onError("Task not found or could not be deleted");
            }
        } catch (Exception e) {
            callback.onError(e.getMessage() != null ? e.getMessage() : "Unknown error occurred");
        }
    }
}
