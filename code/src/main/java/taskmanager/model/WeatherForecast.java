package taskmanager.model;

import java.time.LocalDateTime;

/**
 * Represents weather data for a specific location and time.
 *
 * This model is used to evaluate whether tasks should be
 * scheduled or adjusted based on environmental conditions.
 */
public class WeatherForecast {

    private final String location;
    private final LocalDateTime time;
    private final double temperatureCelsius;
    private final String condition;
    private final double precipitationProbability;

    /**
     * Constructs a WeatherForecast object.
     *
     * @param location weather location
     * @param time time of the forecast
     * @param temperatureCelsius 
     * @param condition weather condition description
     * @param precipitationProbability probability of rain (0.0–1.0)
     */
    public WeatherForecast(String location, LocalDateTime time,
                           double temperatureCelsius,
                           String condition,
                           double precipitationProbability) {
        this.location = location;
        this.time = time;
        this.temperatureCelsius = temperatureCelsius;
        this.condition = condition;
        this.precipitationProbability = precipitationProbability;
    }

    /** @return location of the forecast */
    public String getLocation() {
        return location;
    }

     /** @return time of the forecast */
    public LocalDateTime getTime() {
        return time;
    }

    /** @return temperature in Celsius */
    public double getTemperatureCelsius() {
        return temperatureCelsius;
    }

    /** @return weather condition description */
    public String getCondition() {
        return condition;
    }

    /** @return probability of precipitation (0.0–1.0) */
    public double getPrecipitationProbability() {
        return precipitationProbability;
    }
}