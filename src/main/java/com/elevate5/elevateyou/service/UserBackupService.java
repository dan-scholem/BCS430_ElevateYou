package com.elevate5.elevateyou.service;

import com.elevate5.elevateyou.App;
import com.elevate5.elevateyou.session.SessionManager;
import com.google.cloud.firestore.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class UserBackupService {

    private final Firestore db = App.fstore;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public String exportUserData(String uid) throws ExecutionException, InterruptedException {
        Map<String, Object> root = new LinkedHashMap<>();
        String email = SessionManager.getSession().getUser().getEmail();


//        // 1) collection "user" / doc {uid}
//        DocumentSnapshot userDoc = db.collection("Users").document(uid).get().get();
//        if (userDoc.exists() && userDoc.getData() != null) {
//            root.put("user", userDoc.getData());
//        }

        // 2) Emergency Card
        DocumentSnapshot ecDoc = db.collection("EmergencyCards").document(uid).get().get();
        if (ecDoc.exists() && ecDoc.getData() != null) {
            root.put("emergencyCard", ecDoc.getData());
        }

        // 3) Medications: Medications/{email}/UserMedications/*
        QuerySnapshot medsSnap = db.collection("Medications")
                .document(email)
                .collection("UserMedications")
                .get().get();
        List<Map<String,Object>> meds = new ArrayList<>();
        for (QueryDocumentSnapshot d : medsSnap.getDocuments()) {
            Map<String,Object> m = new LinkedHashMap<>(d.getData());
            m.put("_id", d.getId());
            m.put("medicationName", d.getString("medicationName"));
            m.put("dosage",         d.getString("dosage"));
            m.put("frequency",      d.getString("frequency"));
            m.put("startDate",      d.getString("startDate"));
            m.put("endDate",        d.getString("endDate"));
            m.put("notes",          d.getString("notes"));
            meds.add(m);
        }
        root.put("medications", meds);

        // 4) Events: Events/{uid}
        DocumentSnapshot eventsDoc = db.collection("Events").document(uid).get().get();
        if (eventsDoc.exists() && eventsDoc.getData() != null) {
            root.put("events", eventsDoc.getData());
        }

        // 5) Appointments: Appointments/{uid}
        DocumentSnapshot apptDoc = db.collection("Appointments").document(uid).get().get();
        if (apptDoc.exists() && apptDoc.getData() != null) {
            root.put("appointments", apptDoc.getData());
        }

        // 6) Water: Water/{uid}
        DocumentSnapshot waterDoc = db.collection("Water").document(uid).get().get();
        if (waterDoc.exists() && waterDoc.getData() != null) {
            root.put("water", waterDoc.getData());
        }

        // 7) Workout Goals: user/{uid}/workoutGoals/*
        QuerySnapshot wgSnap = db.collection("users")
                .document(uid)
                .collection("workoutGoals")
                .get().get();
        List<Map<String,Object>> workoutGoals = new ArrayList<>();
        for (QueryDocumentSnapshot d : wgSnap.getDocuments()) {
            Map<String,Object> m = new LinkedHashMap<>(d.getData());
            m.put("_id", d.getId());
            m.put("completedsets", d.getLong("completedSets"));
            m.put("createdat", d.getString("createdAt"));
            m.put("email", d.getString("email"));
            m.put("goalTitle", d.getString("goalTitle"));
            m.put("notes", d.getString("notes"));
            m.put("progress", d.getLong("progress"));
            m.put("status", d.getString("status"));
            m.put("targetset", d.getLong("targetSet"));
            m.put("uid", d.getString("uid"));
            m.put("workout", d.getString("workout"));
            workoutGoals.add(m);
        }
        root.put("workoutGoals", workoutGoals);

        // 8) Journal: Journal Entries/{email}/UserJournals/*
        QuerySnapshot jnSnap = db.collection("Journal Entries")
                .document(email)
                .collection("UserJournals")
                .get().get();
        List<Map<String,Object>> journal = new ArrayList<>();
        for (QueryDocumentSnapshot d : jnSnap.getDocuments()) {
            Map<String,Object> m = new LinkedHashMap<>(d.getData());
            m.put("_id", d.getId());
            m.put("entry",      d.getString("entry"));
            m.put("entryDate",  d.getString("entryDate"));
            m.put("mood",       d.getString("mood"));
            m.put("title",      d.getString("title"));

            journal.add(m);
        }
        root.put("journal", journal);

        // 9) Calories: Calories/{uid}
        DocumentSnapshot CalDoc = db.collection("Calories").document(uid).get().get();
        if (CalDoc.exists() && CalDoc.getData() != null) {
            root.put("calories", CalDoc.getData());
        }

        // 10) LiveChat: LiveChat/{uid}
        DocumentSnapshot liveDoc = db.collection("LiveChat").document(uid).get().get();
        if (liveDoc.exists() && liveDoc.getData() != null) {
            root.put("livechat", liveDoc.getData());
        }

        // 11) Shares: Shares/{uid}/friend/*
        CollectionReference friendCol = db.collection("Shares")
                .document(uid)
                .collection("friends");

        QuerySnapshot friendSnap = friendCol.get().get();
        List<Map<String, Object>> friends = new ArrayList<>();

        for (QueryDocumentSnapshot fd : friendSnap.getDocuments()) {

            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("friendUid", fd.getId());

            Object val = fd.get("isDisplay");
            boolean isDisplay = false;

            if (val instanceof Boolean) {
                isDisplay = (Boolean) val;
            } else if (val != null) {
                isDisplay = Boolean.parseBoolean(val.toString());
            }

            entry.put("isDisplay", isDisplay);
            friends.add(entry);
        }

        root.put("shares", friends);

        // 12) Sleep: Sleep/{uid}
        DocumentSnapshot sleepDoc = db.collection("Sleep").document(uid).get().get();
        if (sleepDoc.exists() && sleepDoc.getData() != null) {
            root.put("sleep", sleepDoc.getData());
        }

        // Map -> JSON
        return gson.toJson(root);


    }

    @SuppressWarnings("unchecked")
    public void importUserData(String uid, String json) throws ExecutionException, InterruptedException {
        Map<String,Object> root = gson.fromJson(
                json,
                new TypeToken<Map<String,Object>>(){}.getType()
        );
        if (root == null) return;
        String email = SessionManager.getSession().getUser().getEmail();

//        // 1) user
//        if (root.containsKey("user")) {
//            Map<String,Object> userData = (Map<String,Object>) root.get("user");
//            db.collection("Users").document(uid).set(userData).get();
//        }

        // 2) Emergency Card
        if (root.containsKey("emergencyCard")) {
            Map<String,Object> ecData = (Map<String,Object>) root.get("emergencyCard");
            db.collection("EmergencyCards").document(uid).set(ecData).get();
        }

        // 3) Medications
        if (root.containsKey("medications")) {
            List<Map<String,Object>> meds =
                    (List<Map<String,Object>>) root.get("medications");

            CollectionReference col = db.collection("Medications")
                    .document(email)
                    .collection("UserMedications");
            for (Map<String, Object> m : meds) {
                Object idObj = m.get("_id");
                String id = (idObj == null)
                        ? UUID.randomUUID().toString()
                        : idObj.toString();
                m.remove("_id");
                col.document(id).set(m).get();
            }
        }

        // 4) Events
        if (root.containsKey("events")) {
            Map<String,Object> evData = (Map<String,Object>) root.get("events");
            db.collection("Events").document(uid).set(evData).get();
        }

        // 5) Appointments
        if (root.containsKey("appointments")) {
            Map<String,Object> apptData = (Map<String,Object>) root.get("appointments");
            db.collection("Appointments").document(uid).set(apptData).get();
        }

        // 6) Water
        if (root.containsKey("water")) {
            Map<String,Object> waterData = (Map<String,Object>) root.get("water");
            db.collection("Water").document(uid).set(waterData).get();
        }

        // 7) workoutGoals: users/{uid}/workoutGoals/*
        if (root.containsKey("workoutGoals")) {
            List<Map<String,Object>> workoutGoals =
                    (List<Map<String,Object>>) root.get("workoutGoals");

            CollectionReference wgCol = db.collection("users")
                    .document(uid)
                    .collection("workoutGoals");

            for (Map<String,Object> m : workoutGoals) {
                Object idObj = m.remove("_id");
                String id = (idObj == null)
                        ? UUID.randomUUID().toString()
                        : idObj.toString();

                wgCol.document(id).set(m, SetOptions.merge()).get();
            }
        }

        // 8) Journal: "Journal Entries"/{email}/UserJournals/*
        if (root.containsKey("journal")) {
            List<Map<String, Object>> journal =
                    (List<Map<String, Object>>) root.get("journal");

            CollectionReference jCol = db.collection("Journal Entries")
                    .document(email)
                    .collection("UserJournals");

            for (Map<String, Object> m : journal) {
                Object idObj = m.get("_id");
                String id = (idObj == null)
                        ? UUID.randomUUID().toString()
                        : idObj.toString();
                m.remove("_id");
                jCol.document(id).set(m).get();
            }
        }

        // 9) Calories: Calories/{uid}
        if (root.containsKey("calories")) {
            Map<String, Object> calData = (Map<String, Object>) root.get("calories");
            db.collection("Calories").document(uid).set(calData).get();
        }

        // 10) LiveChat: LiveChat/{uid}
        if (root.containsKey("livechat")) {
            Map<String, Object> liveData = (Map<String, Object>) root.get("livechat");
            db.collection("LiveChat").document(uid).set(liveData).get();
        }

        // 11) Shares: Shares/{uid}/friends/{friendUid}
        if (root.containsKey("shares")) {
            List<Map<String, Object>> shares =
                    (List<Map<String, Object>>) root.get("shares");

            CollectionReference friendCol = db.collection("Shares")
                    .document(uid)
                    .collection("friends");

            for (Map<String, Object> m : shares) {
                Object fuidObj = m.get("friendUid");
                if (fuidObj == null) continue;

                String friendUid = fuidObj.toString();
                Object val = m.get("isDisplay");
                boolean isDisplay = false;
                if (val instanceof Boolean) {
                    isDisplay = (Boolean) val;
                } else if (val != null) {
                    isDisplay = Boolean.parseBoolean(val.toString());
                }

                Map<String, Object> payload = new HashMap<>();
                payload.put("isDisplay", isDisplay);

                friendCol.document(friendUid).set(payload).get();
            }
        }

        // 12) Sleep: Sleep/{uid}
        if (root.containsKey("sleep")) {
            Map<String, Object> sleepData = (Map<String, Object>) root.get("sleep");
            db.collection("Sleep").document(uid).set(sleepData).get();
        }
    }
}
