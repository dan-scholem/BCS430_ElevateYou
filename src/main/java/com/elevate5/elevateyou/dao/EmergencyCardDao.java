package com.elevate5.elevateyou.dao;

import com.elevate5.elevateyou.App;
import com.elevate5.elevateyou.model.EmergencyCardModel;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class EmergencyCardDao {

    private final Firestore db = App.fstore;

    private DocumentReference doc(String uid) {
        return db.collection("EmergencyCards").document(uid);
    }

    public EmergencyCardModel load(String uid) throws ExecutionException, InterruptedException {
        DocumentSnapshot snap = doc(uid).get().get();
        if (!snap.exists() || snap.getData() == null) return null;
        Map<String, Object> data = snap.getData();
        return EmergencyCardModel.fromMap(data);
    }

    public void upsert(String uid, EmergencyCardModel model) throws ExecutionException, InterruptedException {
        doc(uid).set(model.toMap()).get();
    }

    public boolean exists(String uid) throws ExecutionException, InterruptedException {
        return doc(uid).get().get().exists();
    }
}