package com.elevate5.elevateyou.dao;

import com.elevate5.elevateyou.App;
import com.google.cloud.firestore.*;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class SleepArticleDao {
    private final Firestore db = App.fstore;

    private DocumentReference userDoc(String uid) {
        return db.collection("Sleep").document(uid);
    }

    private void ensureDoc(String uid) throws ExecutionException, InterruptedException {
        DocumentReference doc = userDoc(uid);
        if (!doc.get().get().exists()) {
            doc.set(new HashMap<String,Object>() {{ put("sleep", new HashMap<>()); put("article", new HashMap<>()); }}).get();
        }
    }

    public void upsertArticle(String uid, String dateKey, int score, String article)
            throws ExecutionException, InterruptedException {
        ensureDoc(uid);
        Map<String, Object> payload = new HashMap<>();
        payload.put("score", score);
        payload.put("article", article);
        userDoc(uid).update("article." + dateKey, payload).get();
    }

    public Map<String, Object> getArticle(String uid, String dateKey)
            throws ExecutionException, InterruptedException {
        DocumentSnapshot snap = userDoc(uid).get().get();
        if (!snap.exists()) return null;
        Object art = snap.get("article." + dateKey);
        return (art instanceof Map) ? (Map<String, Object>) art : null;
    }

    public void deleteArticle(String uid, String dateKey)
            throws ExecutionException, InterruptedException {
        DocumentReference doc = userDoc(uid);
        DocumentSnapshot snap = doc.get().get();
        if (!snap.exists()) return;
        Map<String, Object> data = new HashMap<>(snap.getData());
        Map<String, Object> article = (Map<String, Object>) data.get("article");
        if (article != null) {
            article.remove(dateKey);
            data.put("article", article);
            doc.set(data).get();
        }
    }
}