package com.elevate5.elevateyou.dao;

import com.elevate5.elevateyou.App;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * DAO for Firestore. No business logic here.
 * Current schema assumptions:
 * - Medications/{uid}/UserMedications/{medId} with fields:
 *   medicationName (string), startDate (YYYY-MM-DD)  // adjust if you have a real "due" field
 * - Events/{uid} document with field "events" (Map<YYYY-MM-DD, List<Map>>)
 *   event item map includes: eventName, time (HH:mm)
 */
public class NotificationDao {

    private final Firestore db = App.fstore;

    public List<QueryDocumentSnapshot> getMedications(String uid)
            throws ExecutionException, InterruptedException {
        return db.collection("Medications")
                .document(uid)
                .collection("UserMedications")
                .get().get().getDocuments();
    }

    public DocumentSnapshot getEventsDoc(String uid)
            throws ExecutionException, InterruptedException {
        return db.collection("Events")
                .document(uid)
                .get().get();
    }
}
