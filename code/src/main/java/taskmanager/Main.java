package taskmanager;

import taskmanager.api.TaskManager;
import taskmanager.model.Task;
import taskmanager.ui.SmartTaskManagerFrame;
import taskmanager.impl.DefaultTaskManagerBuilder;

import java.time.LocalDateTime;
import javax.swing.*;

/**
 * Entry point of the Smart Task Manager application.
 * 
 * This class initializes the TaskManager, creates sample tasks,
 * and launches the graphical user interface.
 */
public class Main {

  /**
     * Main method that starts the application.
     *
     * @param args command-line arguments (not used)
     */
  public static void main(String[] args) {

        // Get API key from environment variable
        String apiKey = System.getenv("d3fa13bb2433c87bbed62e4d154e3fbc");

        // Fallback if environment variable is missing
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = "d3fa13bb2433c87bbed62e4d154e3fbc";
        }

         // Build the TaskManager (students will implement DefaultTaskManager)
        TaskManager tm = TaskManager.builder()
                .withWeatherApiKey(apiKey) 
                .build();

        // Add a couple of test tasks
        Task task1 = new Task(
                "task-001",
                "Morning run",
                LocalDateTime.now().plusHours(2),
                true
        );
        Task task2 = new Task(
                "task-002",
                "Coding session",
                LocalDateTime.now().plusHours(4),
                false
        );

        tm.addTask(task1);
        tm.addTask(task2);

        System.out.println("Tasks loaded: " + tm.getTasks().size());

        // Wire this to the Swing UI
        SmartTaskManagerFrame frame = new SmartTaskManagerFrame(tm);
        javax.swing.SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }
}