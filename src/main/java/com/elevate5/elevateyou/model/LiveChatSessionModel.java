package com.elevate5.elevateyou.model;

import java.util.ArrayList;
import java.util.List;

public class LiveChatSessionModel {

    private final List<LiveChatMessageModel> messages = new ArrayList<>();

    public void add(LiveChatMessageModel m) {
        messages.add(m);
    }

    public List<LiveChatMessageModel> getMessages() {
        return messages;
    }

    public List<LiveChatMessageModel> getRecent(int n) {
        int start = Math.max(0, messages.size() - n);
        return messages.subList(start, messages.size());
    }

    public void clear() {
        messages.clear();
    }
}