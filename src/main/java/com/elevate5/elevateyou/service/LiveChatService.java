package com.elevate5.elevateyou.service;

import com.elevate5.elevateyou.LiveChatController;
import com.elevate5.elevateyou.dao.LiveChatDao;
import com.elevate5.elevateyou.model.LiveChatMessageModel;
import com.elevate5.elevateyou.model.LiveChatSessionModel;
import com.elevate5.elevateyou.model.LogInModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class LiveChatService {

    private final OpenAiChatModel model;
    private final LiveChatSessionModel session = new LiveChatSessionModel();
    private final LiveChatDao dao;
    private final String uid;

    public LiveChatService() {
        this(null, new LiveChatDao());
    }

    public LiveChatService(String uid, LiveChatDao dao) {
        this.uid = uid;
        this.dao = (dao != null ? dao : new LiveChatDao());
        this.model = OpenAiChatModel.builder()
                .baseUrl("https://api.openai.com/v1")
                .apiKey(LogInModel.OPEN_AI_KEY)
                .modelName("gpt-4o-mini")
                .build();

        if (uid != null && !uid.isBlank()) {
            try {
                for (LiveChatMessageModel m : this.dao.loadAll(uid)) {
                    session.add(m);
                }
            } catch (Exception e) {}
        }
    }

    public CompletableFuture<String> chat(String userText) {
        final long tUser = System.currentTimeMillis();
        final LiveChatMessageModel u = LiveChatMessageModel.user(userText, tUser);
        session.add(u);

        if (uid != null && !uid.isBlank()) {
            CompletableFuture.runAsync(() -> {
                try { dao.append(uid, u); } catch (Exception ignored) {}
            });
        }

        return CompletableFuture.supplyAsync(() -> {
            String reply = buildAiReply(userText);
            final long tAi = System.currentTimeMillis();
            LiveChatMessageModel a = LiveChatMessageModel.assistant(reply, tAi);
            session.add(a);

            if (uid != null && !uid.isBlank()) {
                try { dao.append(uid, a); } catch (Exception ignored) {}
            }
            return reply;
        });
    }

    public CompletableFuture<Void> deleteHistoryHardField() {
        session.clear();
        if (uid != null && !uid.isBlank()) {
            return CompletableFuture.runAsync(() -> {
                try { dao.deleteMessagesField(uid); } catch (Exception ignored) {}
            });
        }
        return CompletableFuture.completedFuture(null);
    }

    private String buildAiReply(String userText) {
        String history = session.getRecent(10).stream()
                .map(m -> (m.isUser() ? "User: " : "AI: ") + m.getContent())
                .collect(Collectors.joining("\n"));

        String prompt = """
            You are ElevateYou Help Assistant，please using friendly clean tone to answer.

            history（recent 10:
            %s

            User：%s
            Assistant：
            """.formatted(history, userText);

        return model.chat(prompt);
    }

    public LiveChatSessionModel getSession() {
        return session;
    }

    public void clear() {
        session.clear();
    }
}