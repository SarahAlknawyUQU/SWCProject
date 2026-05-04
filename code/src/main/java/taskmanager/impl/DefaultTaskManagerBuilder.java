package taskmanager.impl;

import taskmanager.api.TaskManager;
import taskmanager.api.TaskService;
import taskmanager.external.WeatherApiClient;
import taskmanager.impl.DefaultTaskManager;
import taskmanager.api.SchedulePlanner;

/**
 * Default implementation of the TaskManagerBuilder interface.
 *
 * This builder is responsible for constructing a TaskManager instance
 * by configuring its required components such as:
 * - TaskService
 * - SchedulePlanner
 * - Weather API client
 *
 * If components are not provided, default implementations are used.
 */

public class DefaultTaskManagerBuilder implements TaskManager.TaskManagerBuilder {

    private TaskService taskService;
    private SchedulePlanner planner;
    private String weatherApiKey = "";
    private String storagePath;

    /**
     * Sets a custom TaskService implementation.
     *
     * @param taskService the task service to use
     * @return the builder instance for chaining
     */
    public DefaultTaskManagerBuilder taskService(TaskService taskService) {
        this.taskService = taskService;
        return this;
    }


    /**
     * Sets a custom SchedulePlanner implementation.
     *
     * @param planner the schedule planner to use
     * @return the builder instance for chaining
     */
    public DefaultTaskManagerBuilder planner(SchedulePlanner planner) {
        this.planner = planner;
        return this;
    }

    /**
     * Sets the API key used for accessing the weather service.
     *
     * @param apiKey the weather API key
     * @return the builder instance for chaining
     */
      @Override
    public TaskManager.TaskManagerBuilder withWeatherApiKey(String apiKey) {
        this.weatherApiKey = apiKey;
        return this;
    }

    /**
     * Sets the storage path for saving tasks.
     *
     * @param path the file system path for storage
     * @return the builder instance for chaining
     */
    @Override
    public TaskManager.TaskManagerBuilder withStoragePath(String path) {
        this.storagePath = path;
        return this;
    }

    /**
     * Builds and returns a fully configured TaskManager instance.
     *
     * If no custom components are provided, default implementations are used:
     * - DefaultTaskService for task management
     * - SchedulePlannerImpl for scheduling
     *
     * @return a configured TaskManager instance
     */
    @Override
    public TaskManager build() {

        WeatherApiClient weatherClient = new WeatherApiClient(weatherApiKey);

        if (taskService == null) {
            taskService = new DefaultTaskService();
        }

        if (planner == null) {
            planner = new SchedulePlannerImpl(weatherClient);
        }

        return new DefaultTaskManager(taskService, planner, weatherClient);
    }
}


