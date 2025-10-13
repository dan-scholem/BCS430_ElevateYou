package com.elevate5.elevateyou.service;

//import com.elevate5.elevateyou.dao.NotificationDao;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Application-facing service for creating notifications.
 * UI/controllers can call these methods directly.
 * This class writes into `notification_queue`; delivery is handled by the dispatcher.
 */
public class NotificationService {

//    private final NotificationDao dao;
//    public NotificationService() {
//        this.dao = new NotificationDao();
//    }
//
//    // read / show relase
//    public List<NotificationModel> fetchInbox(String userId, int limit) throws Exception {
//        try {
//            return dao.getLatest(userId, limit);
//        } catch (Exception e) {
//            return List.of();
//        }
//    }
//
//    public int getUnreadCount(String userId) throws Exception {
//        try {
//            return dao.getUnreadCount(userId);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }
//
//    public void markAllRead(String userId) throws Exception {
//        try {
//            dao.markAllRead(userId);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void markOneRead(String docId) throws Exception {
//        try {
//            dao.markOneRead(docId);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public String publish(String userId, String title, String body) throws Exception {
//        try {
//            return dao.insert(userId, title, body, null, null, null);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public String publishWithLink(String userId, String title, String body, String linkType, String linkRefId, Instant dueAt) throws Exception {
//        try {
//            return dao.insert(userId, title, body, linkType, linkRefId, dueAt);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public static class Item {
        public final String userId;
        public final String title;
        public final String body;
        private final Instant createdAt;
        public boolean read;

        public Item(String userId, String title, String body, Instant createdAt, boolean read) {
            this.userId = userId;
            this.title = title;
            this.body = body;
            this.createdAt = createdAt;
            this.read = read;
        }
    }

    private final List<Item> store = new ArrayList<>();
    private final AtomicInteger seq = new AtomicInteger(1);
    public void add(String title, String body, boolean read) {
        String userId = String.valueOf(seq.getAndIncrement());
        store.add(0, new Item(userId, title, body, Instant.now(), read));
    }

    public NotificationService() {
        add("Appointments", "You have a doctor visit tomorrow at 9:00 AM", false);
        add("Doctor", "Doctor A messaged you something, check it out", false);
        add("Workout", "You completed your workout yesterday. Great job!", false);
        add("Nutrition", "Reminder: Log your lunch from 2 days ago", false);
        add("System", "Your account settings were updated 3 days ago", false);
        add("Calendar", "Weekly summary is ready for review", false);
    }

    public List<Item> latest(int limit) {
        int end = Math.min(limit, store.size());
        List<Item> list = new ArrayList<>(store.subList(0, end));
        return list;
    }

    public int unreadCount () {
        int count = 0;
        for (Item item : store) if (item.read) count++;
        return count;
    }

    public void markAllRead() {
        for (Item item : store)  item.read = true;
    }

    public void markOneRead(String id) {
        for (Item item : store) {
            if (item.userId.equals(id)) {
                item.read = true;
                break;
            }
        };
    }
}
