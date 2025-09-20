package com.example.habitmaster.domain.usecases.tasks;

import com.example.habitmaster.data.firebases.FirebaseTaskInstanceRepository;
import com.example.habitmaster.data.repositories.TaskInstanceRepository;

public class DeleteTaskUseCase {
    private final TaskInstanceRepository localInstanceRepo;
    private final FirebaseTaskInstanceRepository remoteInstanceRepository;

    public interface Callback {
        void onSuccess();

        void onError(String errorMessage);
    }

    public DeleteTaskUseCase(TaskInstanceRepository localInstanceRepo,
                             FirebaseTaskInstanceRepository remoteInstanceRepository) {
        this.localInstanceRepo = localInstanceRepo;
        this.remoteInstanceRepository = remoteInstanceRepository;
    }

    public void execute(String taskId, Callback callback) {
        try {
            remoteInstanceRepository.deleteFutureTaskInstances(taskId);
            boolean deleted = localInstanceRepo.deleteFutureTaskInstances(taskId);
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
