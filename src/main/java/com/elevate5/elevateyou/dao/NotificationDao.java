package com.elevate5.elevateyou.dao;

import com.elevate5.elevateyou.App;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.w3c.dom.ranges.DocumentRange;

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

    public List<QueryDocumentSnapshot> getMedications(String email)
            throws ExecutionException, InterruptedException {
        return db.collection("Medications")
                .document(email)
                .collection("UserMedications")
                .get().get().getDocuments();
    }

    public DocumentSnapshot getEvents(String uid)
            throws ExecutionException, InterruptedException {
        return db.collection("Events")
                .document(uid)
                .get().get();
    }

    public DocumentSnapshot getAppointment(String uid)
            throws ExecutionException, InterruptedException {
        return db.collection("Appointments")
                .document(uid)
                .get().get();
    }

    public DocumentSnapshot getWater(String uid)
            throws ExecutionException, InterruptedException{
        return db.collection("Water")
                .document(uid)
                .get().get();
    }

    public List<QueryDocumentSnapshot> getExercise(String uid)
            throws ExecutionException, InterruptedException {
        return db.collection("user")
                .document(uid)
                .collection("workoutGoals")
                .get().get().getDocuments();
    }
}