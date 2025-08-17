package com.example.habitmaster.domain.usecases;

import com.example.habitmaster.data.repositories.TaskInstanceRepository;

public class DeleteTaskUseCase {
    private final TaskInstanceRepository taskInstanceRepo;

    public interface Callback {
        void onSuccess();

        void onError(String errorMessage);
    }

    public DeleteTaskUseCase(TaskInstanceRepository taskInstanceRepo) {
        this.taskInstanceRepo = taskInstanceRepo;
    }

    public void execute(String taskId, Callback callback) {
        try {
            boolean deleted = taskInstanceRepo.deleteFutureTaskInstances(taskId);
            if (deleted) {
                callback.onSuccess();
            } else {
                callback.onError("Task not found or could not be deleted");
            }
        } catch (Exception e) {
            callback.onError(e.getMessage() != null ? e.getMessage() : "Unknown error occurred");
        }
    }
}
