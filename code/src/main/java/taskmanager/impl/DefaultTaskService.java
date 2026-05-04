package taskmanager.impl;

import taskmanager.api.TaskService;
import taskmanager.model.Task;
import taskmanager.exception.InvalidTaskException;
import taskmanager.exception.TaskNotFoundException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Default implementation of the TaskService interface.
 *
 * This class manages tasks in memory using a thread-safe data structure.
 * It provides asynchronous operations for adding, removing, and retrieving tasks
 * using reactive programming (Mono and Flux).
 */
 public class DefaultTaskService implements TaskService {

    private final Map<String, Task> tasks = new ConcurrentHashMap<>();


    /**
     * Adds a new task to the system.
     *
     * The task is validated before being stored.
     *
     * @param task the task to add
     * @return a Mono that completes when the task is successfully added
     * @throws InvalidTaskException if the task is null or contains invalid data
     */
    @Override
    public Mono<Void> addTask(Task task) {
        return Mono.fromRunnable(() -> {
            validateTask(task);
            tasks.put(task.getId(), task);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

     /**
     * Removes a task by its ID.
     *
     * @param taskId the ID of the task to remove
     * @return a Mono that completes when the task is removed
     * @throws TaskNotFoundException if the task does not exist
     */
    @Override
    public Mono<Void> removeTask(String taskId) {
        return Mono.fromRunnable(() -> {
            if (!tasks.containsKey(taskId)) {
                throw new TaskNotFoundException(taskId);
            }
            tasks.remove(taskId);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    /**
     * Finds a task by its ID.
     *
     * @param taskId the ID of the task
     * @return a Mono emitting the found task
     * @throws TaskNotFoundException if the task does not exist
     */
    @Override
    public Mono<Task> findTaskById(String taskId) {
        return Mono.fromCallable(() -> {
            Task task = tasks.get(taskId);
            if (task == null) {
                throw new TaskNotFoundException(taskId);
            }
            return task;
        }).subscribeOn(Schedulers.boundedElastic());
    }

     /**
     * Retrieves all tasks.
     *
     * @return a Flux emitting all stored tasks
     */
    @Override
    public Flux<Task> findAllTasks() {
        return Flux.fromIterable(tasks.values())
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Retrieves all tasks as a list.
     *
     * @return a Mono emitting a list of all tasks
     */
    @Override
    public Mono<java.util.List<Task>> findAllTasksAsList() {
        return findAllTasks().collectList();
    }

    /**
     * Validates a task before adding it.
     *
     * @param task the task to validate
     * @throws InvalidTaskException if the task is null or missing required fields
     */
    private void validateTask(Task task) {
        if (task == null || task.getId() == null || task.getTitle() == null) {
            throw new InvalidTaskException("Invalid task");
        }
    }
 }
