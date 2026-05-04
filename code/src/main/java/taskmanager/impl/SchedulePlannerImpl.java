package taskmanager.impl;

import taskmanager.api.SchedulePlanner;
import taskmanager.exception.WeatherAPIException;
import taskmanager.external.WeatherApiClient;
import taskmanager.model.Task;
import taskmanager.model.WeatherForecast;
import taskmanager.model.ScheduleRecommendation;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

/**
 * Implementation of the SchedulePlanner interface.
 *
 * This class generates scheduling recommendations for tasks
 * based on weather conditions. It uses an external Weather API
 * to retrieve forecasts and determines whether tasks should be
 * performed or rescheduled.
 */
public class SchedulePlannerImpl implements SchedulePlanner {

    private final WeatherApiClient weatherClient;

    /**
     * Constructs a SchedulePlanner with a weather API client.
     *
     * @param weatherClient client used to fetch weather data
     */
    public SchedulePlannerImpl(WeatherApiClient weatherClient) {
        this.weatherClient = weatherClient;
    }

    /**
     * Suggests a schedule for tasks based on real-time weather data
     * for a given location.
     *
     * Weather-sensitive tasks are evaluated based on:
     * - Precipitation probability
     * - Temperature
     *
     * @param tasks list of tasks to evaluate
     * @param location location for which weather is fetched
     * @return a Mono emitting a list of schedule recommendations
     * @throws WeatherAPIException if the weather API fails
     */
    @Override
    public Mono<List<ScheduleRecommendation>> suggestScheduleForLocation(
            List<Task> tasks,
            String location) {

        return weatherClient.fetchWeather(location)

                .subscribeOn(Schedulers.boundedElastic())
                .map(forecast -> tasks.stream()
                        .map(task -> evaluateTask(task, forecast))
                        .toList()
                )

                .retry(2)
                .onErrorMap(e ->
                        new WeatherAPIException(
                                "Failed to fetch weather for location: " + location, e)
                );
    }

    /**
     * Suggests a schedule using a provided weather forecast.
     *
     * This method does not call the external API and instead uses
     * the given forecast to generate basic recommendations.
     *
     * @param tasks list of tasks to evaluate
     * @param forecast weather data used for planning
     * @return a Mono emitting a list of basic schedule recommendations
     */
    @Override
    public Mono<List<ScheduleRecommendation>> suggestSchedule(
            List<Task> tasks,
            WeatherForecast forecast) {

        return Mono.fromCallable(() ->
                tasks.stream()
                        .map(task -> evaluateTask(task, forecast))
                        .toList()
        );
    }

    /**
 * Evaluates a task and generates a schedule recommendation based on weather conditions.
 *
 * The method applies simple decision rules:
 * - If the task is not weather-sensitive, it is always recommended as normal.
 * - If precipitation probability is high, the task is avoided.
 * - If temperature is too high, the task is rescheduled.
 * - Otherwise, the task is considered suitable for execution.
 *
 * @param task the task to evaluate
 * @param forecast the weather forecast used for decision-making
 * @return a schedule recommendation describing whether the task should proceed or be adjusted
 */
    private ScheduleRecommendation evaluateTask(Task task, WeatherForecast forecast) {

        if (!task.isWeatherSensitive()) {
            return new ScheduleRecommendation(task, "Not weather dependent");
        }

        if (forecast.getPrecipitationProbability() > 0.7) {
            return new ScheduleRecommendation(
                    task,
                    " Avoid - heavy rain expected"
            );
        }

        if (forecast.getTemperatureCelsius() > 40) {
            return new ScheduleRecommendation(
                    task,
                    "Too hot - reschedule"
            );
        }

        return new ScheduleRecommendation(
                task,
                "Good weather for task"
        );
    }
}