package com.elevate5.elevateyou.model;

import java.sql.Timestamp;

public class LiveChatMessageModel {

    private final long timestamp;
    private final Boolean user;
    private final String content;

    public LiveChatMessageModel(long timestamp, boolean user, String content) {
        this.timestamp = timestamp;
        this.user = user;
        this.content = content;
    }

    public long getTimestamp() { return timestamp; }
    public boolean isUser() { return user; }
    public String getContent() { return content; }


    public static LiveChatMessageModel user(String text) {
        return new LiveChatMessageModel(System.currentTimeMillis(), true, text);
    }

    public static LiveChatMessageModel assistant(String text) {
        return new LiveChatMessageModel(System.currentTimeMillis(), false, text);
    }

    public static LiveChatMessageModel user (String text, long ts) {
        return new LiveChatMessageModel(ts, true, text);
    }

    public static LiveChatMessageModel assistant(String text, long ts) {
        return new LiveChatMessageModel(ts, false, text);
    }
}