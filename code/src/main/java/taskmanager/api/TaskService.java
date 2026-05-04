package taskmanager.api;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import taskmanager.model.Task;

import java.util.List;

/**
 * Service interface for managing tasks.
 *
 * This interface defines asynchronous (reactive) operations
 * for creating, deleting, and retrieving tasks.
 *
 * All methods return reactive types (Mono or Flux),
 * meaning results are produced asynchronously.
 */
public interface TaskService {

     /**
     * Adds a new task.
     *
     * @param task the task to add
     * @return a Mono that completes when the task is added
     */
    Mono<Void> addTask(Task task);

    /**
     * Removes a task by its ID.
     *
     * @param taskId the ID of the task to remove
     * @return a Mono that completes when the task is removed
     */
    Mono<Void> removeTask(String taskId);

    /**
     * Finds a task by its ID.
     *
     * @param taskId the ID of the task
     * @return a Mono emitting the found task
     */
    Mono<Task> findTaskById(String taskId);

    /**
     * Retrieves all tasks as a stream.
     *
     * @return a Flux emitting all tasks
     */
    Flux<Task> findAllTasks();

    /**
     * Retrieves all tasks as a list.
     *
     * @return a Mono emitting a list of all tasks
     */
    Mono<List<Task>> findAllTasksAsList();
}