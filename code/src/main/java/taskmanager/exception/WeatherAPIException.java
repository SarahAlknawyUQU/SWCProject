package taskmanager.exception;

/**
 * Exception thrown when an error occurs while communicating with the weather API.
 */
public class WeatherAPIException extends RuntimeException {

    /**
     * Constructs a new WeatherAPIException with a message and root cause.
     *
     * @param message an explanation of the error
     * @param cause the underlying exception that triggered this error
     */
    public WeatherAPIException(String message, Throwable cause) {
        super(message, cause);
    }
}