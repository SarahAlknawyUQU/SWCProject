package taskmanager.api;

import reactor.core.publisher.Mono;
import taskmanager.model.Task;
import taskmanager.model.WeatherForecast;
import taskmanager.impl.DefaultTaskManagerBuilder;

import java.util.List;

/**
 * Main facade for the Smart Task Manager.
 * Other developers will use this to interact with the system.
 */
public interface TaskManager {

    /**
     * Adds a new task to the system.
     *
     * @param task the task to be added
     */
    void addTask(Task task);

    /**
     * Removes a task from the system using its unique ID.
     *
     * @param taskId the ID of the task to remove
     */
    void removeTask(String taskId);

    /**
     * Retrieves all tasks currently stored in the system.
     *
     * @return a list of all tasks
     */
    List<Task> getTasks();

    /**
     * Fetches weather information for a given location asynchronously.
     *
     * This method does not block the calling thread and returns a Mono
     * that will emit the weather forecast once available.
     *
     * @param location the location to fetch weather for
     * @return a Mono containing the weather forecast
     */

    Mono<WeatherForecast> fetchWeather(String location); 

    /**
     * Returns the scheduling planner responsible for generating task recommendations.
     *
     * @return the SchedulePlanner instance
     */
    SchedulePlanner getPlanner();

    /**
     * Creates a new builder instance for constructing a TaskManager.
     *
     * @return a TaskManagerBuilder for configuring and building the system
     */
    static TaskManagerBuilder builder() {
        return new DefaultTaskManagerBuilder(); // Creates and returns the hidden implementation of the builder
    }

     /**
     * Builder interface for constructing a TaskManager instance step-by-step.
     *
     * This follows the Builder design pattern to allow flexible configuration.
     */
    interface TaskManagerBuilder {

        /**
         * Sets the API key used for weather service integration.
         *
         * @param apiKey the weather API key
         * @return the builder instance
         */
        TaskManagerBuilder withWeatherApiKey(String apiKey);

        /**
         * Sets the storage path for task persistence (optional).
         *
         * @param path file system path for storing data
         * @return the builder instance
         */
        TaskManagerBuilder withStoragePath(String path);

        /**
         * Builds and returns the final TaskManager instance.
         *
         * @return a fully configured TaskManager
         */
        TaskManager build();
    }
}