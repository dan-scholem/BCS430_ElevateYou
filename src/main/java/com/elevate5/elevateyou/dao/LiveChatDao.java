package com.elevate5.elevateyou.dao;

import com.elevate5.elevateyou.App;
import com.elevate5.elevateyou.LiveChatController;
import com.elevate5.elevateyou.model.LiveChatMessageModel;
import com.elevate5.elevateyou.model.LiveChatSessionModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class LiveChatDao {
    private static final String COL = "LiveChat";
    private DocumentReference doc(String uid) {
        return App.fstore.collection(COL).document(uid);
    }
    public List<LiveChatMessageModel> loadAll (String uid)
            throws ExecutionException, InterruptedException {
    DocumentSnapshot snap = doc(uid).get().get();
    List<LiveChatMessageModel> out = new ArrayList<>();
    if (snap.exists() && snap.contains("messages")) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> arr = (List<Map<String, Object>>) snap.get("messages");
        if (arr != null) {
            for (Map<String, Object> m : arr) {
                long ts = ((Number) m.getOrDefault("ts", System.currentTimeMillis())).longValue();
                String role = String.valueOf(m.getOrDefault("role", "user"));
                String content = String.valueOf(m.getOrDefault("content", ""));
                out.add(new LiveChatMessageModel(ts,"user".equalsIgnoreCase(role), content));
            }
        }
    }
    out.sort(Comparator.comparing(LiveChatMessageModel::getTimestamp));
    return out;
    }

    public void append(String uid, LiveChatMessageModel msg)
            throws ExecutionException, InterruptedException{
        Map<String, Object> item = new HashMap<>();
        item.put("ts", msg.getTimestamp());
        item.put("role", msg.isUser() ? "user" : "ai");
        item.put("content", msg.getContent());

        DocumentReference ref = doc(uid);
        DocumentSnapshot snap = ref.get().get();
        if (!snap.exists()) {
            Map<String, Object> init = new HashMap<>();
            init.put("messages", new ArrayList<>(List.of(item)));
            ref.set(init).get();
        } else if (!snap.contains("messages") || snap.get("messages") == null) {
            ref.update("messages", new ArrayList<>(List.of(item))).get();
        } else {
            ref.update("messages", FieldValue.arrayUnion(item)).get();
        }
    }

    public void deleteMessagesField(String uid)
        throws ExecutionException, InterruptedException {
        doc(uid).update("messages", FieldValue.delete()).get();
    }
}
