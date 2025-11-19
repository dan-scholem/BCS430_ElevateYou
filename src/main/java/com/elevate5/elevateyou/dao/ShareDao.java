package com.elevate5.elevateyou.dao;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import java.util.HashMap;
import java.util.Map;

public class ShareDao {

    private final Firestore db = FirestoreClient.getFirestore();

    public boolean isSharing(String ownerUid, String friendUid) throws Exception {
        DocumentReference docRef = db.collection("Shares")
                .document(ownerUid)
                .collection("friends")
                .document(friendUid);

        DocumentSnapshot snapshot = docRef.get().get();

        if (!snapshot.exists()) return false;

        Boolean isDisplay = snapshot.getBoolean("isDisplay");

        return isDisplay != null && isDisplay;
    }


    public void setSharing(String ownerUid, String friendUid, boolean enabled) throws Exception {
        DocumentReference docRef = db.collection("Shares")
                .document(ownerUid)
                .collection("friends")
                .document(friendUid);

        Map<String, Object> data = new HashMap<>();
        data.put("isDisplay", enabled);

        docRef.set(data, SetOptions.merge()).get();
    }


    public Map<String, Boolean> getAllShares(String ownerUid) throws Exception {
        CollectionReference friendsCol = db.collection("Shares")
                .document(ownerUid)
                .collection("friends");

        QuerySnapshot querySnapshot = friendsCol.get().get();

        Map<String, Boolean> result = new HashMap<>();

        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
            Boolean val = doc.getBoolean("isDisplay");
            result.put(doc.getId(), val != null && val);
        }

        return result;
    }
}