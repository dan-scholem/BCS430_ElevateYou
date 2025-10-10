package com.elevate5.elevateyou.notification;

import com.elevate5.elevateyou.util.DatabaseConnection;
//import com.elevate5.elevateyou.dao.NotificationDao;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

/**
 * Periodically scans `notification_queue` for due PENDING items,
 * delivers through sink, marks SENT, and writes `notification_log`.
 */
public class NotificationDispatcher {
    private final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
    private final NotificationSink sink;
    //private final NotificationDao dao =  new NotificationDao();
    private final int intervalSeconds;

    public NotificationDispatcher(NotificationSink sink, int intervalSeconds) {
        this.sink = sink;
        this.intervalSeconds = intervalSeconds;
    }

    /** Start periodic scanning. */
    public void start() {
        ses.scheduleAtFixedRate(this::tick, 0, intervalSeconds, TimeUnit.SECONDS);
    }

    /** Stop immediately. */
    public void stop() {
        ses.shutdownNow();
    }

    /** One scan cycle. */
    private void tick() {
        try {
            String nowIso = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            // 1) fetch due items
            try (Connection c = DatabaseConnection.connect();
                 PreparedStatement ps = c.prepareStatement(
                         "SELECT nq_id, user_id, title, body FROM notification_queue " +
                                 "WHERE status='PENDING' AND scheduled_at <= ? ORDER BY scheduled_at ASC")) {
                ps.setString(1, nowIso);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        long nqId   = rs.getLong("nq_id");
                        int userId  = rs.getInt("user_id");
                        String title= rs.getString("title");
                        String body = rs.getString("body");

                        // 2) deliver
                        String deliveredIso = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        sink.deliver(userId, title, body);
                        //dao.markSent(nqId, deliveredIso);
                        //dao.logSent(nqId, userId);
                        // 3) mark sent + log
                        markSent(nqId, LocalDateTime.now().toString());
                        logResult(nqId, userId, "SENT");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // replace with proper logger later
        }
    }

    public void markSent(long nqId, String deliveredIso) throws SQLException {
        try (Connection c = DatabaseConnection.connect();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE notification_queue SET status='SENT' WHERE nq_id=?")) {
            ps.setString(1, deliveredIso);
            ps.setLong(2, nqId);
            ps.executeUpdate();
        }
    }

    private void logResult(long nqId, int userId, String result) throws SQLException {
        try (Connection c = DatabaseConnection.connect();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO notification_log(nq_id,user_id,delivered_at,result) " +
                             "VALUES (?,?,datetime('now'),?)")) {
            ps.setLong(1, nqId);
            ps.setInt(2, userId);
            ps.setString(3, result);
            ps.executeUpdate();
        }
    }
}
