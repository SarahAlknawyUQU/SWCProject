package taskmanager.model;

/**
 * Represents a scheduling recommendation for a specific task.
 *
 * @param task the task being evaluated
 * @param recommendation the reccomendation for the task
 */
public record ScheduleRecommendation(Task task, String recommendation) {
}