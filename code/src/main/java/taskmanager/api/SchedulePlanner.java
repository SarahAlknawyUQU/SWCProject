package taskmanager.api;


import reactor.core.publisher.Mono;
import taskmanager.model.Task;
import taskmanager.model.WeatherForecast;

import java.util.List;

/**
 * Defines scheduling logic for recommending task execution order.
 *
 * The planner considers task data and external conditions such as weather
 * to generate smart scheduling recommendations.
 *
 * Implementations of this interface may use different strategies such as:
 * priority-based scheduling, weather-aware planning, or AI-based optimization.
 */
public interface SchedulePlanner {

        /**
     * Generates scheduling recommendations based on a list of tasks and a weather forecast.
     *
     * The result is asynchronous and returned as a {@link Mono}.
     *
     * @param tasks list of tasks to be scheduled
     * @param forecast weather forecast used to influence scheduling decisions
     * @return a Mono containing a list of schedule recommendations
     */
    Mono<List<taskmanager.model.ScheduleRecommendation>> suggestSchedule(
            List<Task> tasks,
            WeatherForecast forecast);


        /**
     * Generates scheduling recommendations based on tasks and a specific location.
     *
     * The implementation may fetch weather data internally based on the location
     * before producing recommendations.
     *
     * @param tasks list of tasks to be scheduled
     * @param location the geographic location used for weather-based planning
     * @return a Mono containing a list of schedule recommendations
     */
    Mono<List<taskmanager.model.ScheduleRecommendation>> suggestScheduleForLocation(
            List<Task> tasks,
            String location);
}

record ScheduleRecommendation(Task task, String recommendation) {}


