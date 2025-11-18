package com.elevate5.elevateyou.service;

import com.elevate5.elevateyou.dao.NotificationDao;
import com.elevate5.elevateyou.model.NotificationModel;
import com.elevate5.elevateyou.session.SessionManager;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import javafx.geometry.NodeOrientation;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Aggregates notifications from multiple sources and returns display models.
 * - No read/unread state
 * - No navigation/route
 * - Sorted by createdAt DESC
 */
public class NotificationService {

    private final NotificationDao dao;
    private final String uid;
    private final String email;

    public NotificationService(String uid, String email, NotificationDao dao) {
        this.dao = new NotificationDao();
        // Defensive fallback: if uid is null/blank, try from SessionManager; else dev fallback
        this.uid = normalizeUid(uid);
        this.email = (email != null && !email.isBlank()) ? email : null;
    }

    public NotificationService(String uid, NotificationDao dao) {
        this.dao = dao;
        this.uid = normalizeUid(uid);
        this.email = null;
    }
    public NotificationService(String uid, String email) {
        this(uid, email, new NotificationDao());
    }

    private String normalizeUid(String u) {
        if (u != null && !u.isBlank()) return u;
        if (SessionManager.getSession() != null && SessionManager.getSession().getUserID() != null) {
            return SessionManager.getSession().getUserID();
        }
        return "dev-demo-uid"; // DEV fallback for development
    }

    /** Merge all sources, sort, and limit for UI. */
    public List<NotificationModel> latest(int limit) {
        List<NotificationModel> out = new ArrayList<>();
        try {
            out.addAll(fromMedications());
            out.addAll(fromEvents());
            out.addAll(fromAppointments());
            out.addAll(fromWater());
            out.addAll(fromExercise());
        } catch (Exception e) {
            e.printStackTrace();
        }
        out.sort(Comparator.comparing((NotificationModel n) -> n.createdAt).reversed());
        return out.size() > limit ? out.subList(0, limit) : out;
    }

    // ---------- Sources ----------
    // Medications/{email}/UserMedications/*
    private List<NotificationModel> fromMedications()
            throws ExecutionException, InterruptedException {
        List<NotificationModel> list = new ArrayList<>();

        final boolean useEmail = (email != null && !email.isBlank());
        List<QueryDocumentSnapshot> docs = useEmail
                ? dao.getMedications(email)
                : dao.getMedications(uid);

        for (QueryDocumentSnapshot d : docs) {
            if (Boolean.TRUE.equals(d.getBoolean("completed"))
                || "completed".equalsIgnoreCase(String.valueOf(d.get ("status")))
                || "done".equalsIgnoreCase(String.valueOf(d.get ("status")))) { continue; }

            String medName = d.getString("medicationName");
            String dueDate = d.getString("endDate");
            String frequency = d.getString("frequency");

            Instant due = instantFromDate(dueDate); // Hide the overdue items.
            if (due.isBefore(Instant.now())) continue;

            String title = "Medication";
            String body  = "You have " + safe(medName) + " need to take " + safe(frequency) +" and due at " + safe(dueDate);

            String id = "Medications/" + uid + "/UserMedications/" + d.getId();

            list.add(new NotificationModel(id, title, body, instantFromDate(dueDate)));
        }
        return list;
    }

    // Events/{uid} with events map: { "YYYY-MM-DD": [ {eventName, time, ...}, ... ] }
    private List<NotificationModel> fromEvents()
            throws ExecutionException, InterruptedException {
        List<NotificationModel> list = new ArrayList<>();
        DocumentSnapshot evDoc = dao.getEvents(uid);

        if (evDoc.exists() && evDoc.contains("events")) {
            Object evField = evDoc.get("events");
            if (evField instanceof Map<?, ?> evMap) {
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) evField).entrySet()) {
                    String date = String.valueOf(entry.getKey()); // YYYY-MM-DD
                    Object arr = entry.getValue();
                    if (arr instanceof List<?> items) {
                        for (Object raw : items) {
                            if (raw instanceof Map<?, ?> m) {

                                if (isCompleted(m)) continue;

                                String name = asString(m.get("eventName"));
                                String time = asString(m.get("time"));
                                Instant due = instantFromDateTime(date, time);

                                if (due.isBefore(Instant.now())) continue;

                                String title = "Events";
                                String body  = "You have " + safe(name) + " at " + safe(date + (time != null ? " " + time : ""));

                                String id = "Events/" + uid + "#" + date + "#" + safe(name);

                                list.add(new NotificationModel(id, title, body, due));
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    // Appointments/{uid} with the map
    private List<NotificationModel> fromAppointments()
            throws ExecutionException, InterruptedException {

        List<NotificationModel> list = new ArrayList<>();

        DocumentSnapshot doc = dao.getAppointment(uid);
        if (!doc.exists()) return list;

        Object arr = doc.get("appointments");
        if (!(arr instanceof List<?> items)) return list;

        for (Object raw : items) {
            if (!(raw instanceof Map<?, ?> m)) continue;

            if (isCompleted(m)) continue;

            String date = asString(m.get("date"));     // "MM/dd/yyyy"
            String time = asString(m.get("time"));     // "HH:mm"
            String doctor = asString(m.get("docName"));
            String type = asString(m.get("docType"));

            Instant due = instantFromUsDate(date, time);
            if (due.isBefore(Instant.now())) continue;

            String title = "Appointment";
            String body = "You have a " + safe(type)
                    + " appointment with Dr. " + safe(doctor)
                    + " at " + safe(date + " " + time);

            String id = "Appointments/" + uid + "#" + safe(date) + "#" + safe(time);

            list.add(new NotificationModel(id, title, body, due));
        }
        return list;
    }
        // Water/{uid} with the map
    private List<NotificationModel> fromWater() throws ExecutionException, InterruptedException {
        List<NotificationModel> list = new ArrayList<>();

        final int goalOz = 64;
        final ZoneId zone = ZoneId.systemDefault();
        final LocalDate today = LocalDate.now(zone);

        int consumedToday = 0;
        DocumentSnapshot waterDoc = dao.getWater(uid);
        if (waterDoc.exists()) {
            Object raw = waterDoc.getData() != null ? waterDoc.getData().get(today.toString()) : null;
            if (raw instanceof List<?> arr) {
                for (Object o : arr) {
                    if (o instanceof Map<?, ?> m) {
                        Object oz = m.get("ounces");
                        if (oz instanceof Number n) {
                            consumedToday += n.intValue();
                        } else {
                            try {
                                consumedToday += Integer.parseInt(String.valueOf(oz));
                            } catch (Exception ignore) {
                            }
                        }
                    }
                }
            }
        }

        if (consumedToday >= goalOz) {
            return list;
        }

        String id = "Water/" + uid + "#" + today;
        String title = "Hydration";
        String body = "Today you have drunk " + consumedToday + "/" + goalOz
                + " oz of water. Don't forget to drink more!";

        list.add(new NotificationModel(
                id,
                title,
                body,
                Instant.now()
        ));

        return list;
    }
    // Water{uid} with the map
    private List<NotificationModel> fromExercise() throws ExecutionException, InterruptedException {
        List<NotificationModel> list = new ArrayList<>();

        List<com.google.cloud.firestore.QueryDocumentSnapshot> docs =
                dao.getExercise(uid);

        for (var d : docs) {
            String status     = Optional.ofNullable(d.getString("status")).orElse("").toLowerCase(); // e.g. "in_progress" / "done"
            long   progress   = asLong(d.get("progress"), 0);
            long   target     = asLong(d.get("targetSets"), 0);
            String workout    = Optional.ofNullable(d.getString("workout")).orElse("-");
            String goalTitle  = Optional.ofNullable(d.getString("goalTitle")).orElse("-");
            String notes      = Optional.ofNullable(d.getString("notes")).orElse("");

            if ("done".equals(status) || (target > 0 && progress >= target)) {
                continue;
            }

            String title = "Support Goal";
            String body  = String.format(
                    "%s — %d/%d sets • %s%s",
                    safe(workout),
                    progress, target,
                    (goalTitle.isBlank() ? "Goal" : goalTitle),
                    (notes.isBlank() ? "" : (" • " + notes))
            );

            Instant created = parseIsoInstant(d.getString("updatedAt"));
            if (created == null) created = Instant.now();

            String id = "WorkoutGoals/" + uid + "/" + d.getId();

            list.add(new NotificationModel(id, title, body, created));
        }

        return list;
    }



    // ---------- utils ----------
    private static String asString(Object o) { return o == null ? null : String.valueOf(o); }
    private static String safe(String s) { return (s == null || s.isBlank()) ? "-" : s; }

    private static Instant instantFromDate(String ymd) {
        try { return LocalDate.parse(ymd).atStartOfDay(ZoneId.systemDefault()).toInstant(); }
        catch (Exception e) { return Instant.now(); }
    }

    private static Instant instantFromDateTime(String ymd, String hm) {
        try {
            LocalDate d = LocalDate.parse(ymd);
            if (hm != null && !hm.isBlank()) {
                LocalTime t = LocalTime.parse(hm); // "HH:mm"
                return d.atTime(t).atZone(ZoneId.systemDefault()).toInstant();
            }
            return d.atStartOfDay(ZoneId.systemDefault()).toInstant();
        } catch (Exception e) {
            return Instant.now();
        }
    }
    private static Instant instantFromUsDate(String mmddyyyy, String hm) {
        try {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate d = LocalDate.parse(mmddyyyy, df);

            LocalTime t = LocalTime.parse(hm); // "HH:mm"
            return d.atTime(t).atZone(ZoneId.systemDefault()).toInstant();
        } catch (Exception e) {
            return Instant.now();
        }
    }

    @SuppressWarnings("unchecked")
    private static boolean isCompleted(Object src) {
        try {
            if (src instanceof Map<?,?> m) {
                Object v;

                // completed / done / isDone
                v = m.get("completed");
                if (v instanceof Boolean b && b) return true;

                v = m.get("done");
                if (v instanceof Boolean b2 && b2) return true;

                v = m.get("isDone");
                if (v instanceof Boolean b3 && b3) return true;

                // status: completed|done
                v = m.get("status");
                if (v != null) {
                    String s = String.valueOf(v).toLowerCase(Locale.ROOT);
                    if (s.contains("completed") || s.contains("done")) return true;
                }
            }
        } catch (Exception ignore) {}
        return false;
    }

    private static long asLong(Object o, long def) {
        if (o instanceof Number n) return n.longValue();
        try { return Long.parseLong(String.valueOf(o)); } catch (Exception ignore) { return def; }
    }
    private static Instant parseIsoInstant(String s) {
        if (s == null || s.isBlank()) return null;
        try { return Instant.parse(s); } catch (Exception e) { return null; }
    }

}
