# Smart Task Manager
This project implements a Java Swing-based task manager that supports weather-aware scheduling using Project Reactor to enhance task planning.

## How to Run the App

follow these steps:

### 1. Open terminal and navigate to the project folder

```bash
cd code
```

### 2. Build the project

```bash
mvn clean compile
```

### 3. Run the application

```bash
mvn exec:java "-Dexec.mainClass=taskmanager.Main"
```

---

## Where to Put the API Key

This project uses the OpenWeather API. You must set the API key before running the app.

## Using .env File

You can store the API key in a .env file inside the project:

- Create a file named .env in the root directory of the project
- Add the following line:
WEATHER_API_KEY=your_api_key_here
- Make sure your application reads environment variables properly

## Important Notes
- Do not upload the .env file to GitHub (add it to .gitignore)
- This method is safer and keeps your API key outside the source code

---

## Code Example (Using TaskManager)

```java
TaskManager manager = new DefaultTaskManagerBuilder()
        .taskService(new DefaultTaskService())
        .planner(new SchedulePlannerImpl(new WeatherApiClient(apiKey)))
        .withWeatherApiKey(apiKey)
        .build();

// Example: adding a task
manager.addTask("Study Java", LocalDateTime.now().plusHours(2));
```

---

## Notes

- The project is built using Java and Maven
- Weather data is fetched using OpenWeather API
- Reactive programming is used for API calls

---

## Author

Razan
Ghala
Sarah