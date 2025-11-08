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
            String medName = d.getString("medicationName");
            String dueDate = d.getString("endDate");
            String frequency = d.getString("frequency");
            Instant due = instantFromDate(dueDate); // Hide the overdue items.

            if (due.isBefore(Instant.now())) continue;

            String title = "Medication";
            String body  = "You have " + safe(medName) + " need to take " + safe(frequency) +" and due at " + safe(dueDate);

            String id = "Medications/" + uid + "/UserMedications/" + d.getId();

            list.add(new NotificationModel(
                    id, title, body, instantFromDate(dueDate)
            ));
        }
        return list;
    }

    // Events/{uid} with events map: { "YYYY-MM-DD": [ {eventName, time, ...}, ... ] }
    private List<NotificationModel> fromEvents()
            throws ExecutionException, InterruptedException {
        List<NotificationModel> list = new ArrayList<>();
        DocumentSnapshot evDoc = dao.getEventsDoc(uid);

        if (evDoc.exists() && evDoc.contains("events")) {
            Object evField = evDoc.get("events");
            if (evField instanceof Map<?, ?> evMap) {
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) evField).entrySet()) {
                    String date = String.valueOf(entry.getKey()); // YYYY-MM-DD
                    Object arr = entry.getValue();
                    if (arr instanceof List<?> items) {
                        for (Object raw : items) {
                            if (raw instanceof Map<?, ?> m) {
                                String name = asString(m.get("eventName"));
                                String time = asString(m.get("time"));

                                Instant due = instantFromDate(time);
                                if (due.isBefore(Instant.now())) continue;

                                String title = "Events";
                                String body  = "You have " + safe(name) + " at " + safe(date + (time != null ? " " + time : ""));

                                // compose a unique id for this item
                                String id = "Events/" + uid + "#" + date + "#" + safe(name);

                                list.add(new NotificationModel(
                                        id, title, body, instantFromDateTime(date, time)
                                ));
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
    private List<NotificationModel> fromWater() throws ExecutionException, InterruptedException {
        List<NotificationModel> list = new ArrayList<>();

        // 业务规则（可以后做成用户设置）：
        final int goalOz = 64;             // 每日总目标（oz），默认 64
        final int slots = 8;               // 16 小时 / 每 2 小时 = 8 槽
        final int perSlot = Math.max(1, (int) Math.round(goalOz / (double) slots));
        final LocalTime startAt = LocalTime.of(8, 0); // 每天从 08:00 开始计 16 小时
        final ZoneId zone = ZoneId.systemDefault();
        final LocalDate today = LocalDate.now(zone);

        // 读取今天已喝（从 Water/{uid}）
        int consumedToday = 0;
        DocumentSnapshot waterDoc = dao.getWater(uid);
        if (waterDoc.exists()) {
            Object raw = waterDoc.getData() != null ? waterDoc.getData().get(today.toString()) : null;
            if (raw instanceof List<?> arr) {
                for (Object o : arr) {
                    if (o instanceof Map<?, ?> m) {
                        Object oz = m.get("ounces");
                        if (oz instanceof Number n) consumedToday += n.intValue();
                        else {
                            try { consumedToday += Integer.parseInt(String.valueOf(oz)); } catch (Exception ignore) {}
                        }
                    }
                }
            }
        }

        List<ZonedDateTime> slotTimes = new ArrayList<>(slots);
        ZonedDateTime base = today.atTime(startAt).atZone(zone);
        for (int i = 0; i < slots; i++) slotTimes.add(base.plusHours(2L * i));

        ZonedDateTime now = ZonedDateTime.now(zone);
        int nextIdx = -1;
        for (int i = 0; i < slotTimes.size(); i++) {
            if (now.isBefore(slotTimes.get(i))) { nextIdx = i; break; }
        }
        if (nextIdx == -1) {
            return list;
        }

        int expectedByNext = perSlot * (nextIdx + 1);
        int behind = Math.max(0, expectedByNext - consumedToday);

        String id = "Water/" + uid + "#" + today + "#slot" + (nextIdx + 1);
        String title = "Hydration";
        String body  = "Slot " + (nextIdx + 1) + "/8 at " +
                slotTimes.get(nextIdx).toLocalTime() +
                " — Drink ~" + perSlot + " oz. " +
                "(Today: " + consumedToday + "/" + goalOz + " oz" +
                (behind > 0 ? ", behind ~" + behind + " oz" : "") + ")";

        list.add(new NotificationModel(
                id,
                title,
                body,
                slotTimes.get(nextIdx).toInstant()
        ));
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
}