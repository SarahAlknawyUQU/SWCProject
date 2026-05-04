package taskmanager.ui;

import taskmanager.api.TaskManager;
import taskmanager.exception.TaskNotFoundException;
import taskmanager.model.Task;
import taskmanager.model.WeatherForecast;
import taskmanager.model.ScheduleRecommendation;
import taskmanager.api.TaskService;
import taskmanager.api.SchedulePlanner;
import taskmanager.impl.DefaultTaskService; // added: needed to instantiate TaskService

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Graphical user interface for the Smart Task Manager system.
 *
 * This Swing-based frame allows users to view tasks, select them,
 * and update their status based on real-time weather conditions.
 *
 * The class integrates:
 * - Task management (TaskManager)
 * - Weather forecasting (reactive Mono)
 * - Scheduling logic (SchedulePlanner)
 */
public class SmartTaskManagerFrame extends JFrame {

    private final TaskManager taskManager;
    private final TaskService taskService;  // to be initialized from taskManager.impl
    private final SchedulePlanner schedulePlanner;

    private final JTable taskTable;
    private final DefaultTableModel tableModel;
    private final JButton addTaskButton;
    private final JButton editTaskButton;
    private final JButton deleteTaskButton;
    private final JButton updateWeatherButton;
    private final JButton suggestScheduleButton;

    private final String[] columnNames = {"ID", "Title", "Due Time", "Weather Sensitive", "Status"};

    /**
     * Constructs the Smart Task Manager UI and initializes all components.
     *
     * Sets up the table, buttons, layout, and event listeners,
     * and loads the initial task list.
     *
     * @param taskManager the main system responsible for tasks and their weather data
     */
    public SmartTaskManagerFrame(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.taskService = new DefaultTaskService();
        this.schedulePlanner = taskManager.getPlanner();

        setTitle("Smart Task Manager (Swing)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 450);

        tableModel = new DefaultTableModel(columnNames, 0);
        taskTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(taskTable);

        addTaskButton = new JButton("Add Task");
        editTaskButton = new JButton("Edit Task");
        deleteTaskButton = new JButton("Delete Task");
        updateWeatherButton = new JButton("Update Weather");
        suggestScheduleButton = new JButton("Suggest Schedule");

        editTaskButton.setEnabled(false);
        deleteTaskButton.setEnabled(false);
        updateWeatherButton.setEnabled(false);
        suggestScheduleButton.setEnabled(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        buttonPanel.add(addTaskButton);
        buttonPanel.add(editTaskButton);
        buttonPanel.add(deleteTaskButton);
        buttonPanel.add(updateWeatherButton);
        buttonPanel.add(suggestScheduleButton);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Initialization: load tasks
        loadTasks();

        // Wiring: select row → enable weather button
        taskTable.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = taskTable.getSelectedRow() >= 0;
            editTaskButton.setEnabled(selected);
            deleteTaskButton.setEnabled(selected);
            updateWeatherButton.setEnabled(selected);
            suggestScheduleButton.setEnabled(selected);
        });

        // "Add Task" clicked
        addTaskButton.addActionListener(e -> {
            JTextField idField    = new JTextField(java.util.UUID.randomUUID().toString().substring(0, 8));
            JTextField titleField = new JTextField();
            JTextField dueField   = new JTextField(LocalDateTime.now().plusHours(2)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            JCheckBox  weatherBox = new JCheckBox("Weather Sensitive");

            JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
            panel.add(new JLabel("ID:"));                       panel.add(idField);
            panel.add(new JLabel("Title:"));                    panel.add(titleField);
            panel.add(new JLabel("Due (yyyy-MM-dd HH:mm):"));   panel.add(dueField);
            panel.add(new JLabel(""));                          panel.add(weatherBox);

            int result = JOptionPane.showConfirmDialog(this, panel, "Add Task",
                    JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    LocalDateTime due = LocalDateTime.parse(dueField.getText(),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    taskManager.addTask(new Task(idField.getText(), titleField.getText(),
                            due, weatherBox.isSelected()));
                    loadTasks();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // "Edit Task" clicked
        editTaskButton.addActionListener(e -> {
            int row = taskTable.getSelectedRow();
            if (row < 0) return;
            String taskId = (String) tableModel.getValueAt(row, 0);

            JTextField titleField = new JTextField((String) tableModel.getValueAt(row, 1));
            JTextField dueField = new JTextField(
    ((LocalDateTime) tableModel.getValueAt(row, 2))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            JCheckBox  weatherBox = new JCheckBox("Weather Sensitive",
                    Boolean.parseBoolean(tableModel.getValueAt(row, 3).toString()));

            JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
            panel.add(new JLabel("Title:"));                    panel.add(titleField);
            panel.add(new JLabel("Due (yyyy-MM-dd HH:mm):"));   panel.add(dueField);
            panel.add(new JLabel(""));                          panel.add(weatherBox);

            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Task",
                    JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    LocalDateTime due = LocalDateTime.parse(dueField.getText(),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    Task updated = new Task(taskId, titleField.getText(),
                            due, weatherBox.isSelected());
                    taskManager.removeTask(taskId);
                    taskManager.addTask(updated);
                    loadTasks();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // "Delete Task" clicked
        deleteTaskButton.addActionListener(e -> {
            int row = taskTable.getSelectedRow();
            if (row < 0) return;
            String taskId = (String) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete task " + taskId + "?", "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                taskManager.removeTask(taskId);
                loadTasks();
            }
        });

        // "Update Weather" clicked
        updateWeatherButton.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow < 0) return;

            String taskId = (String) tableModel.getValueAt(selectedRow, 0);
            updateWeatherForTask(taskId);
        });

        // "Suggest Schedule" clicked
        suggestScheduleButton.addActionListener(e -> {
            schedulePlanner.suggestScheduleForLocation(taskManager.getTasks(), "Jeddah")
                    .subscribeOn(Schedulers.boundedElastic())
                    .doOnNext(recommendations -> SwingUtilities.invokeLater(() -> {
                        StringBuilder sb = new StringBuilder();
                        for (ScheduleRecommendation r : recommendations) {
                            sb.append(r.task().getTitle())
                              .append(": ")
                              .append(r.recommendation())
                              .append("\n");
                        }
                        JOptionPane.showMessageDialog(this, sb.toString(),
                                "Suggested Schedule", JOptionPane.INFORMATION_MESSAGE);
                    }))
                    .doOnError(err -> SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this,
                                    "Could not fetch schedule: " + err.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE)))
                    .subscribe();
        });
    }

    /**
     * Loads all tasks asynchronously and displays them in the table.
     *
     * The operation runs on a background thread using Reactor
     * to avoid blocking the UI.
     */
    private void loadTasks() {
        Mono.just(taskManager.getTasks())
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(tasks -> SwingUtilities.invokeLater(() -> populateTable(tasks)))
                .subscribe();
    }

    /**
     * Populates the table with a list of tasks.
     *
     * @param tasks list of tasks to display
     */
    private void populateTable(List<Task> tasks) {
        tableModel.setRowCount(0);
        for (Task t : tasks) {
            tableModel.addRow(new Object[]{t.getId(), t.getTitle(), t.getDueDateTime(), t.isWeatherSensitive(), "N/A"});
        }
    }

    /**
     * Fetches weather data and updates the selected task status accordingly.
     *
     * If precipitation probability is above 0.6, the task is marked
     * as risky; otherwise, it is marked as safe.
     *
     * @param taskId the ID of the task to update
     */
    private void updateWeatherForTask(String taskId) {
        Mono<WeatherForecast> forecastMono = taskManager.fetchWeather("Jeddah");  // fixed city

        forecastMono
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(forecast -> SwingUtilities.invokeLater(() -> {
                    // Simple weather‑aware status logic
                    String status = forecast.getPrecipitationProbability() > 0.6
                            ? "RISKY (rain)"
                            : "SAFE";

                    updateTaskStatusInTable(taskId, status);
                }))
                .doOnError(error -> SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this,
                                "Weather fetch failed: " + error.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE)))
                .subscribe();
    }

    /**
     * Updates the status of a specific task in the table.
     *
     * @param taskId the ID of the task to update
     * @param status the new status value to display
     */
    private void updateTaskStatusInTable(String taskId, String status) {
        int rowCount = tableModel.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            String idInTable = (String) tableModel.getValueAt(i, 0);
            if (idInTable.equals(taskId)) {
                tableModel.setValueAt(status, i, 4);
                break;
            }
        }
    }
}