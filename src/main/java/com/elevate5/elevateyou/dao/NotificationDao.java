//package com.elevate5.elevateyou.dao;
//
//import com.elevate5.elevateyou.model.NotificationModel;
//
//import com.google.api.core.ApiFunction;
//import com.google.api.core.ApiFuture;
//import com.google.cloud.Timestamp;
//import com.google.cloud.firestore.*;
//import com.google.firebase.cloud.FirestoreClient;
//
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ExecutionException;
//
//public class NotificationDao {
//    /**
//     * get lastest notification for a user, ordered by creation time descending
//     * @param userId
//     * @param limit maximum num of item
//     * @return list of NotificationModel
//     * @throws Exception on storage error
//    */
//    public List<NotificationModel> getLatest(String userId, int limit) throws Exception{
//        Firestore db = FirestoreClient.getFirestore();
//        CollectionReference clr = db.collection("Notification");
//
//        Query query = clr.whereEqualTo("userId", userId)
//                         .orderBy("createdAt", Query.Direction.DESCENDING)
//                         .limit(limit);
//
//        ApiFuture<QuerySnapshot> future = query.get();
//        QuerySnapshot querySnapshot = future.get();
//
//        List<NotificationModel> list = new ArrayList<>();
//        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
//            NotificationModel nModel = new NotificationModel();
//            nModel.setId(doc.getId());
//            nModel.setUserId(userId);
//            nModel.setTitle(doc.getString("title"));
//            nModel.setBody(doc.getString("body"));
//
//            if (doc.contains("linkType")) {
//                nModel.setLinkType(doc.getString("linkType"));
//            }
//            if (doc.contains("linkRefId")) {
//                nModel.setLinkRefId(doc.getString("linkRefId"));
//            }
//            if (doc.contains("dueAt")) {
//                Timestamp ts = doc.getTimestamp("dueAt");
//                if (ts != null) {
//                    nModel.setDueAt(ts.toInstant());
//                }
//            }
//
//            Timestamp cts = doc.getTimestamp("createdAt");
//            if (cts != null) {
//                nModel.setCreatedAt(cts.toInstant());
//            }
//
//            Boolean isRead = doc.getBoolean("isRead");
//            nModel.setRead(isRead != null && isRead);
//
//            list.add(nModel);
//        }
//        return null;
//    }
//
//    /**
//     * Count num of unread notification for a user
//     * @param userId
//     * @return count of unread
//     * @throws Exception on error
//     */
//    public int getUnreadCount(String userId) throws Exception{
//        Firestore db = FirestoreClient.getFirestore();
//        CollectionReference clr = db.collection("Notification");
//
//        Query query = clr.whereEqualTo("userId", userId)
//                         .whereEqualTo("isRead", false);
//
//        ApiFuture<QuerySnapshot> future = query.get();
//        QuerySnapshot qsp = future.get();
//
//        return qsp.getDocuments().size();
//    }
//
//    /**
//     * Mark all notification for a user as read
//     * @param userId
//     * @throws Exception on error
//     */
//    public void markAllRead(String userId) throws Exception{
//        Firestore db = FirestoreClient.getFirestore();
//        CollectionReference clr = db.collection("Notification");
//        Query query = clr.whereEqualTo("userId", userId)
//                         .whereEqualTo("isRead", false);
//
//        ApiFuture<QuerySnapshot> future = query.get();
//        QuerySnapshot qsp = future.get();
//
//        WriteBatch batch = db.batch();
//        for (DocumentSnapshot doc : qsp.getDocuments()) {
//            DocumentReference docR = doc.getReference();
//            batch.update(docR, "isRead", true, "readAt", FieldValue.serverTimestamp());
//        }
//        ApiFuture<List<WriteResult>> commitFuture = batch.commit();
//        commitFuture.get();
//    }
//
//    /**
//     * Mark one notification for a user as read
//     * @param docId
//     * @throws Exception on error
//     */
//    public void markOneRead(String docId) throws Exception{
//        Firestore db = FirestoreClient.getFirestore();
//        DocumentReference docR = db.collection("Notification").document(docId);
//
//        ApiFuture<WriteResult> future = docR.update("isRead", true, "readAt", FieldValue.serverTimestamp());
//        future.get();
//    }
//
//    /**
//     * Insert a new notification
//     * @param userId
//     * @param title
//     * @param body the context
//     * @param linkType
//     * @param linkRefId
//     * @param dueAt
//     * @return the doc ID or record ID of inserted notification
//     * @throws Exception on error
//     */
//    public String insert(String userId, String title, String body, String linkType, String linkRefId, Instant dueAt) throws Exception{
//        Firestore db = FirestoreClient.getFirestore();
//        CollectionReference clr = db.collection("Notification");
//
//        var data = new java.util.HashMap<String, Object>();
//        data.put("userId", userId);
//        data.put("title", title);
//        data.put("body", body);
//        if (linkType != null) {
//            data.put("linkType", linkType);
//        }
//        if (linkRefId != null) {
//            data.put("linkRefId", linkRefId);
//        }
//        if (dueAt != null) {
//            data.put("dueAt", com.google.cloud.Timestamp.ofTimeSecondsAndNanos(
//                    dueAt.getEpochSecond(), dueAt.getNano()));
//        }
//        data.put("isRead", false);
//        data.put("createdAt", FieldValue.serverTimestamp());
//
//        ApiFuture<DocumentReference> future = clr.add(data);
//        DocumentReference docR = future.get();
//        return docR.getId();
//    }
//}