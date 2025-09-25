package com.example.habitmaster.domain.usecases.tasks;

import android.util.Log;

import com.example.habitmaster.data.dtos.TaskInstanceDTO;
import com.example.habitmaster.data.repositories.CategoryRepository;
import com.example.habitmaster.data.repositories.TaskInstanceRepository;
import com.example.habitmaster.data.repositories.TaskRepository;
import com.example.habitmaster.data.repositories.UserRepository;
import com.example.habitmaster.domain.models.Task;
import com.example.habitmaster.domain.models.TaskFrequency;
import com.example.habitmaster.domain.models.TaskInstance;
import com.example.habitmaster.domain.models.TaskStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetUserTasksUseCase {
    private final TaskRepository taskRepo;
    private final TaskInstanceRepository taskInstanceRepo;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepo;

    public GetUserTasksUseCase(TaskRepository taskRepo, TaskInstanceRepository taskInstanceRepo, UserRepository userRepository, CategoryRepository categoryRepo) {
        this.taskRepo = taskRepo;
        this.taskInstanceRepo = taskInstanceRepo;
        this.userRepository = userRepository;
        this.categoryRepo = categoryRepo;
    }

    public List<TaskInstanceDTO> getAllTasksInstances() {
        String userId = userRepository.currentUid();

        // 1. Get all tasks
        List<Task> tasks = taskRepo.getAllUserTasks(userId);

        if (tasks.isEmpty()) return new ArrayList<>();

        // 2. Separate repeating and one-time tasks
        List<Task> repeatingTasks = new ArrayList<>();
        List<Task> oneTimeTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.getFrequency() == TaskFrequency.ONCE) {
                oneTimeTasks.add(task);
            } else {
                repeatingTasks.add(task);
            }
        }

        // 3. Get instances for repeating tasks
        List<TaskInstanceDTO> dtos = new ArrayList<>();
        if (!repeatingTasks.isEmpty()) {
            List<String> repeatingTaskIds = repeatingTasks.stream()
                    .map(Task::getId)
                    .collect(Collectors.toList());

            List<TaskInstance> instances = taskInstanceRepo.getByTaskIds(repeatingTaskIds);

            Map<String, Task> taskMap = repeatingTasks.stream()
                    .collect(Collectors.toMap(Task::getId, t -> t));

            for (TaskInstance instance : instances) {
                Task task = taskMap.get(instance.getTaskId());
                if (task == null) continue;

                dtos.add(new TaskInstanceDTO(
                        instance.getId(),
                        task.getId(),
                        task.getName(),
                        task.getDescription(),
                        categoryRepo.getCategoryById(task.getCategoryId()),
                        task.getFrequency(),
                        task.getRepeatInterval(),
                        instance.getDate(),
                        task.getExecutionTime(),
                        task.getDifficulty(),
                        task.getImportance(),
                        task.getXpValue(),
                        instance.getStatus()
                ));
            }
        }

        List<String> oneTimeTaskIds = oneTimeTasks.stream()
                .map(Task::getId)
                .collect(Collectors.toList());
        List<TaskInstance> oneTimeInstances = taskInstanceRepo.getByTaskIds(oneTimeTaskIds);
        Map<String, String> oneTimeInstanceMap = oneTimeInstances.stream()
                .collect(Collectors.toMap(TaskInstance::getTaskId, TaskInstance::getId));
        for (Task task : oneTimeTasks) {
            dtos.add(new TaskInstanceDTO(
                    oneTimeInstanceMap.get(task.getId()), // Gets instanceId from map
                    task.getId(),
                    task.getName(),
                    task.getDescription(),
                    categoryRepo.getCategoryById(task.getCategoryId()),
                    task.getFrequency(),
                    task.getRepeatInterval(),
                    task.getStartDate(),
                    task.getExecutionTime(),
                    task.getDifficulty(),
                    task.getImportance(),
                    task.getXpValue(),
                    TaskStatus.ACTIVE
            ));
        }

        return dtos;
    }

    public List<TaskInstanceDTO> getRepeatingTasks(LocalDate fromDate) {
        String userId = userRepository.currentUid();

        List<Task> tasks = taskRepo.getRepeatingUserTasks(userId, fromDate);

        if (tasks.isEmpty()) return new ArrayList<>();

        List<String> taskIds = tasks.stream()
            .map(Task::getId)
            .collect(Collectors.toList());

        List<TaskInstance> instances = taskInstanceRepo.getByTaskIdsFromDate(taskIds, fromDate);

        Map<String, Task> taskMap = tasks.stream()
                .collect(Collectors.toMap(Task::getId, t -> t));

        List<TaskInstanceDTO> dtos = new ArrayList<>();
        for (TaskInstance instance : instances) {
            Task task = taskMap.get(instance.getTaskId());
            if (task == null) continue;

            TaskInstanceDTO dto = new TaskInstanceDTO(instance.getId(), task.getId(), task.getName(), task.getDescription(),
                    categoryRepo.getCategoryById(task.getCategoryId()), task.getFrequency(), task.getRepeatInterval(),
                    instance.getDate(), task.getExecutionTime(), task.getDifficulty(), task.getImportance(),
                    task.getXpValue(), instance.getStatus());

            dtos.add(dto);
        }

        return dtos;
    }

    public List<TaskInstanceDTO> getOneTimeTasks(LocalDate fromDate) {
        String userId = userRepository.currentUid();

        // 1. Get one-time tasks
        List<Task> tasks = taskRepo.getOneTimeUserTasks(userId, fromDate);
        if (tasks.isEmpty()) return new ArrayList<>();

        // 2. Collect task IDs
        List<String> taskIds = tasks.stream()
                .map(Task::getId)
                .collect(Collectors.toList());

        // 3. Get TaskInstances
        List<TaskInstance> instances = taskInstanceRepo.getByTaskIds(taskIds);

        // 4. Map tasks by ID
        Map<String, Task> taskMap = tasks.stream()
                .collect(Collectors.toMap(Task::getId, t -> t));

        // 5. Combine into DTOs
        List<TaskInstanceDTO> dtos = new ArrayList<>();
        for (TaskInstance instance : instances) {
            Task task = taskMap.get(instance.getTaskId());
            if (task == null) continue;

            TaskInstanceDTO dto = new TaskInstanceDTO(
                    instance.getId(),
                    task.getId(),
                    task.getName(),
                    task.getDescription(),
                    categoryRepo.getCategoryById(task.getCategoryId()),
                    task.getFrequency(),
                    task.getRepeatInterval(),
                    instance.getDate(),
                    task.getExecutionTime(),
                    task.getDifficulty(),
                    task.getImportance(),
                    task.getXpValue(),
                    instance.getStatus()
            );
            dtos.add(dto);
        }

        return dtos;
    }

    public TaskInstanceDTO findTaskInstanceById(String taskId) {
        String userId = userRepository.currentUid();

        Task task = taskRepo.findUserTaskById(userId, taskId);
        if (task == null) return null;

        List<TaskInstance> instances = taskInstanceRepo.getByTaskIds(List.of(taskId));
        if (instances.isEmpty()) return null; // TODO: Handle null return

        // 3. For one-time tasks, take the first instance
        TaskInstance instance = instances.get(0);

        return new TaskInstanceDTO(
                instance.getId(),
                task.getId(),
                task.getName(),
                task.getDescription(),
                categoryRepo.getCategoryById(task.getCategoryId()),
                task.getFrequency(),
                task.getRepeatInterval(),
                instance.getDate(),
                task.getExecutionTime(),
                task.getDifficulty(),
                task.getImportance(),
                task.getXpValue(),
                instance.getStatus()
        );
    }

    public boolean existsUserTaskByCategoryId(String userId, String categoryId) {
        return taskRepo.existsUserTaskByCategoryId(userId, categoryId);
    }

    public List<TaskInstance> getValuableUserTaskInstances(String userId, LocalDate from, LocalDate to) {
        return taskInstanceRepo.getValuableUserTaskInstances(userId, from, to);
    }

    public List<TaskInstance> detectMissedUserTaskInstances(String userId) {
        return taskInstanceRepo.detectMissedByUserId(userId);
    }

    public List<Task> getAllUserTasks(String userId) {
        return taskRepo.getAllUserTasks(userId);
    }
}
