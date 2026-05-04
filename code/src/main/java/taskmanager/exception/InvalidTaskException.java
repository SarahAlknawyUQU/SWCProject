package taskmanager.exception;

/**
 * Exception thrown when a task is invalid or fails validation rules.
 */
public class InvalidTaskException extends RuntimeException {

    /**
     * Constructs an InvalidTaskException with an error message.
     *
     * @param message explanation of why the task is invalid
     */
    public InvalidTaskException(String message) {
        super(message);
    }
}