package com.elevate5.elevateyou.service;

import com.elevate5.elevateyou.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Application-facing service for creating notifications.
 * UI/controllers can call these methods directly.
 * This class writes into `notification_queue`; delivery is handled by the dispatcher.
 */
public class NotificationService {

    /** Enqueue a generic notification at a specific time. */
    public void enqueue(int userId, String title, String body, LocalDateTime when) throws SQLException {
        final String sql = "INSERT INTO notification_queue(user_id, title, body, scheduled_at) VALUES (?,?,?,?)";
        try (Connection c = DatabaseConnection.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, title);
            ps.setString(3, body);
            ps.setString(4, when.toString()); // ISO_LOCAL_DATE_TIME
            ps.executeUpdate();
        }
    }

    /** Task reminder: enqueue a notification at the task due time. */
    public void createTaskReminder(int userId, String taskTitle, String note, LocalDateTime dueAt) throws SQLException {
        String title = (taskTitle == null || taskTitle.isBlank()) ? "Task due" : taskTitle;
        String body  = (note == null) ? "" : note;
        enqueue(userId, title, body, dueAt);
    }

    /** Doctor visit: schedule a reminder at the appointment time. */
    public void remindDoctorVisit(int userId, String doctorName, LocalDateTime appointmentAt) throws SQLException {
        String title = "Doctor visit";
        String who   = (doctorName == null || doctorName.isBlank()) ? "appointment" : ("visit with " + doctorName);
        String body  = "Upcoming " + who + " at " + appointmentAt;
        enqueue(userId, title, body, appointmentAt);
    }

    /** Workout: schedule a single workout reminder. */
    public void remindWorkout(int userId, LocalDateTime workoutAt) throws SQLException {
        enqueue(userId, "Workout", "Workout at " + workoutAt, workoutAt);
    }

    /** Water: schedule a one-time hydration reminder. */
    public void remindWaterOnce(int userId, LocalDateTime when) throws SQLException {
        enqueue(userId, "Hydration time", "Drink some water", when);
    }

    /** Supportive notification: send now (enqueue at current time). */
    public void supportiveNow(int userId, String message) throws SQLException {
        String body = (message == null) ? "Keep going!" : message;
        enqueue(userId, "Keep going!", body, LocalDateTime.now());
    }
}
