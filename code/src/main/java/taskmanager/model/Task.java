package taskmanager.model;

import java.time.LocalDateTime;


/**
 * Represents a task in the system that can be scheduled and managed.
 *
 * A task may optionally be weather-sensitive, meaning its execution
 * depends on environmental conditions such as rain or temperature.
 */
public class Task {

    private final String id;
    private String title;
    private String description;
    private LocalDateTime dueDateTime;
    private boolean weatherSensitive;

     /**
     * Constructs a new Task.
     *
     * @param id unique task identifier
     * @param title task title
     * @param dueDateTime deadline of the task
     * @param weatherSensitive whether the task depends on weather conditions
     */

    public Task(String id, String title, LocalDateTime dueDateTime, boolean weatherSensitive) {
        this.id = id;
        this.title = title;
        this.dueDateTime = dueDateTime;
        this.weatherSensitive = weatherSensitive;
    }

    /**
     * @return the unique task ID
     */
    public String getId() {
        return id;
    }

    /**
     * @return the task title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the task title.
     *
     * @param title new title of the task
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return task description
     */
    public String getDescription() {
        return description;
    }


    /**
     * Sets the task description.
     *
     * @param description description text
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return due date and time of the task
     */
    public LocalDateTime getDueDateTime() {
        return dueDateTime;
    }

     /**
     * Updates the task deadline.
     *
     * @param dueDateTime new due date and time
     */
    public void setDueDateTime(LocalDateTime dueDateTime) {
        this.dueDateTime = dueDateTime;
    }

     /**
     * @return true if the task depends on weather conditions
     */
    public boolean isWeatherSensitive() {
        return weatherSensitive;
    }

    /**
     * Sets whether the task depends on weather conditions.
     *
     * @param weatherSensitive true if weather affects the task
     */
    public void setWeatherSensitive(boolean weatherSensitive) {
        this.weatherSensitive = weatherSensitive;
    }
}
