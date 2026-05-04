package taskmanager.exception;

/**
 * Exception thrown when a requested task cannot be found in the system.
 */
public class TaskNotFoundException extends RuntimeException {
    
     /**
     * Constructs a TaskNotFoundException for the given task ID.
     *
     * @param taskId the ID of the task that was not found
     */
    public TaskNotFoundException(String taskId) {
        super("Task not found: " + taskId);
    }
}

