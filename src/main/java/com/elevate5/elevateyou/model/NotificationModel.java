package com.elevate5.elevateyou.model;

import java.time.Instant;

/**
 * View model for a notification row (display-only).
 * No read/unread, no navigation.
 */
public class NotificationModel {
    public final String id;          // unique key (for future use)
    public final String title;       // e.g., "Medication", "Appointment"
    public final String body;        // e.g., "You have X at 2025-10-23 09:00"
    public final Instant createdAt;  // for sorting DESC

    public NotificationModel(String id, String title, String body, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.createdAt = createdAt;
    }
}