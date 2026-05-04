package taskmanager.impl;

import taskmanager.api.TaskManager;
import taskmanager.api.TaskService;
import taskmanager.api.SchedulePlanner;
import taskmanager.model.WeatherForecast;
import taskmanager.external.WeatherApiClient; //not sure about this
import taskmanager.model.Task;
import taskmanager.model.ScheduleRecommendation;

import reactor.core.publisher.Mono;

import java.util.List;


/**
 * Default implementation of the TaskManager interface.
 *
 * This class acts as a facade that coordinates between:
 * - TaskService for task management
 * - SchedulePlanner for scheduling logic
 * - WeatherApiClient for weather data
 *
 * It provides a simple interface while internally using reactive programming.
 */
public class DefaultTaskManager implements TaskManager {
 
    private final TaskService taskService;
    private final SchedulePlanner planner;
    private final WeatherApiClient weatherClient;
 
    /**
     * Constructs a TaskManager with required components.
     *
     * @param taskService service responsible for managing tasks
     * @param planner planner used to generate schedule recommendations
     * @param weatherClient client used to fetch weather data
     */
    public DefaultTaskManager(TaskService taskService,
                               SchedulePlanner planner,
                               WeatherApiClient weatherClient) {
        this.taskService = taskService;
        this.planner = planner;
        this.weatherClient = weatherClient;
    }

    /**
     * Adds a task to the system.
     *
     * This method blocks to adapt reactive code to a synchronous interface.
     *
     * @param task the task to add
     */
    @Override
    public void addTask(Task task) {
        // Block to stay compatible with the void (non-reactive) interface signature.
        // The underlying service is still reactive; we subscribe and block here at the facade boundary.
        taskService.addTask(task).block();
    }
 
    /**
     * Removes a task by its ID.
     *
     * @param taskId the ID of the task to remove
     */
    @Override
    public void removeTask(String taskId) {
        taskService.removeTask(taskId).block();
    }
 
    /**
     * Retrieves all tasks.
     *
     * @return a list of all tasks
     */
    @Override
    public List<Task> getTasks() {
        return taskService.findAllTasksAsList().block();
    }
 
    /**
     * Fetches weather data for a given location.
     *
     * @param location the location to fetch weather for
     * @return a Mono emitting the weather forecast
     */
    @Override
    public Mono<WeatherForecast> fetchWeather(String location) {
        return weatherClient.fetchWeather(location);
    }
 
    /**
     * Returns the schedule planner.
     *
     * @return the SchedulePlanner instance
     */
    @Override
    public SchedulePlanner getPlanner() {
        return planner;
    }
}