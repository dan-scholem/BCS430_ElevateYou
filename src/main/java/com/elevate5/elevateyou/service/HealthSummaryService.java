package com.elevate5.elevateyou.service;

import com.elevate5.elevateyou.App;
import com.elevate5.elevateyou.dao.SleepArticleDao;
import com.google.cloud.firestore.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;


public class HealthSummaryService {

    private final Firestore db = App.fstore;
    private final ZoneId zone = ZoneId.systemDefault();
    private final SleepArticleDao sleepArticleDao = new SleepArticleDao();

    public CompletableFuture<String> getTodayNutritionSummary(String uid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                LocalDate today = LocalDate.now(zone);

                DocumentSnapshot snap = db.collection("Calories")
                        .document(uid)
                        .get().get();

                if (!snap.exists()) return "-";

                Object raw = snap.get(today.toString());
                int totalCal = 0;

                if (raw instanceof List<?> arr) {
                    for (Object o : arr) {
                        if (o instanceof Map<?, ?> m) {
                            Object cal = m.get("calories");
                            if (cal instanceof Number n) {
                                totalCal += n.intValue();
                            } else if (cal != null) {
                                try { totalCal += Integer.parseInt(String.valueOf(cal)); }
                                catch (Exception ignore) {}
                            }
                        }
                    }
                }

                if (totalCal <= 0) return "-";
                return totalCal + " Cal";
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<String> getTodayWaterSummary(String uid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                LocalDate today = LocalDate.now(zone);

                DocumentSnapshot snap = db.collection("Water")
                        .document(uid)
                        .get().get();

                if (!snap.exists()) return "-";

                Object raw = snap.get(today.toString());
                int totalOz = 0;

                if (raw instanceof List<?> arr) {
                    for (Object o : arr) {
                        if (o instanceof Map<?, ?> m) {
                            Object oz = m.get("ounces");
                            if (oz instanceof Number n) {
                                totalOz += n.intValue();
                            } else if (oz != null) {
                                try { totalOz += Integer.parseInt(String.valueOf(oz)); }
                                catch (Exception ignore) {}
                            }
                        }
                    }
                }

                if (totalOz <= 0) return "-";
                return totalOz + " oz";
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<String> getLastNightSleepSummary(String uid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String dateKey = LocalDate.now().minusDays(1).toString();

                Map<String, Object> article = sleepArticleDao.getArticle(uid, dateKey);

                if (article == null || article.isEmpty()) {
                    return "N/A";
                }

                Object scoreObj = article.get("score");
                if (scoreObj == null) {
                    return "N/A";
                }

                return String.valueOf(scoreObj);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<String> getSleepScoreByDate(String uid, String dateKey) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> article = sleepArticleDao.getArticle(uid, dateKey);
                if (article == null || article.isEmpty()) {
                    return "N/A";
                }
                Object scoreObj = article.get("score");
                return scoreObj == null ? "N/A" : String.valueOf(scoreObj);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }
}