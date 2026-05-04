package taskmanager.external;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import taskmanager.model.WeatherForecast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import org.json.JSONObject;

/**
 * Client responsible for retrieving weather data from an external API (OpenWeatherMap).
 *
 * <p>This class supports:</p>
 * <ul>
 *     <li>Fetching real-time weather data via HTTP requests</li>
 *     <li>Caching results to reduce repeated API calls</li>
 *     <li>Asynchronous execution using Project Reactor (Mono)</li>
 * </ul>
 *
 * <p>The precipitation probability is estimated using rain volume data.</p>
 */
public class WeatherApiClient {

    private final String API_KEY;

    // Cache to store previously fetched weather results
    private final Map<String, WeatherForecast> cache = new ConcurrentHashMap<>();

    /**
     * Constructs a WeatherApiClient with the given API key.
     *
     * @param API_KEY the API key used to access the weather service
     *
     * @throws IllegalStateException if API key is null or empty
     */
    public WeatherApiClient(String API_KEY) {
        if (API_KEY == null || API_KEY.isBlank()) {
            throw new IllegalStateException("API key is missing");
        }
        this.API_KEY = API_KEY;
    }

    /**
     * Fetches weather data for a given location.
     *
     * <p>If the data exists in the cache, it is returned immediately.</p>
     * <p>Otherwise, an HTTP request is sent to the OpenWeatherMap API.</p>
     *
     * <p>The operation is executed asynchronously on a bounded elastic thread pool
     * to avoid blocking the main or UI thread.</p>
     *
     * @param location the city or location name (e.g., "Jeddah")
     * @return a {@link Mono} emitting the weather forecast for the location
     *
     * @throws RuntimeException if the API request fails or returns a non-200 response
     */
    public Mono<WeatherForecast> fetchWeather(String location) {

        // Validate input
        if (location == null || location.isBlank()) {
            return Mono.error(new IllegalArgumentException("Location cannot be empty"));
        }

        // Return cached result if available
        if (cache.containsKey(location)) {
            return Mono.just(cache.get(location));
        }

        return Mono.fromCallable(() -> {

            // Build API request URL
            String urlString =
                    "https://api.openweathermap.org/data/2.5/weather?q="
                            + location + "&appid=" + API_KEY + "&units=metric";

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Check HTTP response status
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new RuntimeException("API request failed with status: " + status);
            }

            // Read API response
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            conn.disconnect(); // Important to release resources

            // Parse JSON response
            JSONObject json = new JSONObject(response.toString());

            double temp = json.getJSONObject("main").getDouble("temp");
            String condition = json
                    .getJSONArray("weather")
                    .getJSONObject(0)
                    .getString("main");

            // Estimate precipitation probability from rain volume
            double rainMm = 0.0;
            if (json.has("rain") && json.getJSONObject("rain").has("1h")) {
                rainMm = json.getJSONObject("rain").getDouble("1h");
            }

            double precipProb = Math.min(rainMm / 10.0, 1.0);

            // Create forecast object
            WeatherForecast forecast =
                    new WeatherForecast(location, LocalDateTime.now(), temp, condition, precipProb);

            // Cache result
            cache.put(location, forecast);

            return forecast;

        })
        .subscribeOn(Schedulers.boundedElastic()) // run in background thread
        .onErrorMap(e -> new RuntimeException("Failed to fetch weather data", e));
    }
}