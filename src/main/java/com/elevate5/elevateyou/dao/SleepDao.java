package com.elevate5.elevateyou.dao;

import com.elevate5.elevateyou.App;
import com.elevate5.elevateyou.model.SleepModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class SleepDao {

    private final Firestore db;

    public SleepDao() {
        this.db = App.fstore;
    }

    private DocumentReference userDoc(String uid) {
        return db.collection("Sleep").document(uid);
    }

    private static Map<String, Object> toMap(SleepModel m) {
        Map<String, Object> map = new HashMap<>();
        map.put("startIso",     m.getStartIso());          // Timestamp
        map.put("endIso",       m.getEndIso());            // Timestamp
        map.put("durationMin", m.getDurationMin());
        map.put("nap",         m.getNap());
        map.put("totalMin",    m.getTotalMin());
        map.put("factors",     m.getFactors() == null ? Collections.emptyMap() : m.getFactors());
        return map;
    }

    @SuppressWarnings("unchecked")
    private static SleepModel fromMap(Object obj) {
        if (!(obj instanceof Map)) return null;
        Map<String, Object> m = (Map<String, Object>) obj;

        SleepModel sm = new SleepModel();
        sm.setStartIso(   asString(m.get("startIso")));
        sm.setEndIso(     asString(m.get("endIso")));
        sm.setDurationMin(asInt(m.get("durationMin")));
        sm.setNap(        asInt(m.get("nap")));
        sm.setTotalMin(   asInt(m.get("totalMin")));

        Object f = m.get("factors");
        if (f instanceof Map<?, ?> fm) {
            sm.setFactors((Map<String, Object>) fm);
        } else {
            sm.setFactors(Collections.emptyMap());
        }
        return sm;
    }

    private static String asString(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private static Integer asInt(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.intValue();
        try { return Integer.parseInt(String.valueOf(o)); } catch (Exception e) { return null; }
    }

    public void appendSegment(String uid, String dateKey, SleepModel segment)
            throws ExecutionException, InterruptedException {
        DocumentReference doc = userDoc(uid);
        Map<String, Object> map = toMap(segment);
        trySetDocIfAbsent(uid);
        doc.update("sleep." + dateKey, FieldValue.arrayUnion(map)).get();
    }

    public void setDay(String uid, String dateKey, List<SleepModel> segments)
            throws ExecutionException, InterruptedException {
        DocumentReference doc = userDoc(uid);
        trySetDocIfAbsent(uid);

        List<Map<String, Object>> arr = new ArrayList<>();
        for (SleepModel s : segments) arr.add(toMap(s));

        Map<String, Object> update = new HashMap<>();
        update.put("sleep." + dateKey, arr);
        doc.update(update).get();
    }

    public void deleteDay(String uid, String dateKey)
            throws ExecutionException, InterruptedException {
        DocumentReference doc = userDoc(uid);
        trySetDocIfAbsent(uid);

        DocumentSnapshot snap = doc.get().get();
        Map<String, Object> root = snap.exists() ? new HashMap<>(snap.getData()) : new HashMap<>();
        Map<String, Object> sleep = (Map<String, Object>) root.get("sleep");
        if (sleep != null) {
            sleep.remove(dateKey);
            root.put("sleep", sleep);
            doc.set(root).get();
        }
    }

    public void deleteSegmentAt(String uid, String dateKey, int index)
            throws ExecutionException, InterruptedException {
        List<SleepModel> day = getDay(uid, dateKey);
        if (index < 0 || index >= day.size()) return;
        day.remove(index);
        setDay(uid, dateKey, day);
    }

    @SuppressWarnings("unchecked")
    public List<SleepModel> getDay(String uid, String dateKey)
            throws ExecutionException, InterruptedException {
        DocumentSnapshot snap = userDoc(uid).get().get();
        if (!snap.exists()) return Collections.emptyList();

        Object sleepObj = snap.get("sleep");
        if (!(sleepObj instanceof Map<?, ?> sleepMap)) return Collections.emptyList();

        Object arrObj = sleepMap.get(dateKey);
        if (!(arrObj instanceof List<?> arr)) return Collections.emptyList();

        List<SleepModel> out = new ArrayList<>();
        for (Object o : arr) {
            SleepModel sm = fromMap(o);
            if (sm != null) out.add(sm);
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    public Map<String, List<SleepModel>> getAllDays(String uid)
            throws ExecutionException, InterruptedException {
        DocumentSnapshot snap = userDoc(uid).get().get();
        Map<String, List<SleepModel>> result = new TreeMap<>();

        if (!snap.exists()) return result;

        Object sleepObj = snap.get("sleep");
        if (!(sleepObj instanceof Map<?, ?> sleepMap)) return result;

        for (Map.Entry<?, ?> e : ((Map<?, ?>) sleepMap).entrySet()) {
            String dateKey = String.valueOf(e.getKey());
            Object arrObj = e.getValue();
            List<SleepModel> dayList = new ArrayList<>();
            if (arrObj instanceof List<?> arr) {
                for (Object o : arr) {
                    SleepModel sm = fromMap(o);
                    if (sm != null) dayList.add(sm);
                }
            }
            result.put(dateKey, dayList);
        }
        return result;
    }

    private void trySetDocIfAbsent(String uid) throws ExecutionException, InterruptedException {
        DocumentReference doc = userDoc(uid);
        DocumentSnapshot snap = doc.get().get();
        if (!snap.exists()) {
            Map<String, Object> init = new HashMap<>();
            init.put("sleep", new HashMap<String, Object>());
            doc.set(init).get();
        }
    }
}