package com.elevate5.elevateyou.service;

import com.elevate5.elevateyou.dao.SleepArticleDao;
import com.elevate5.elevateyou.dao.SleepDao;
import com.elevate5.elevateyou.model.SleepModel;
import com.elevate5.elevateyou.session.SessionManager;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class SleepArticleService {

    private final SleepDao sleepDao = new SleepDao();
    private final SleepArticleDao articleDao = new SleepArticleDao();
    private final String uid;

    public SleepArticleService() {
        this.uid = (SessionManager.getSession()!=null && SessionManager.getSession().getUserID()!=null)
                ? SessionManager.getSession().getUserID() : "dev-demo-uid";
    }

    public void generateAndSave(String dateKey) throws ExecutionException, InterruptedException {
        List<SleepModel> segments = sleepDao.getDay(uid, dateKey);
        if (segments == null || segments.isEmpty()) return;

        String timeRange = buildTimeRange(segments);
        int total = segments.stream().mapToInt(s -> s.getTotalMin() == null ? 0 : s.getTotalMin()).sum();
        int napTotal = segments.stream().mapToInt(s -> s.getNap() == null ? 0 : s.getNap()).sum();

        boolean fragmented = (segments.size() > 1) ||
                segments.stream().anyMatch(s -> boolFactor(s.getFactors(), "fragmented"));

        boolean exerciseLate = segments.stream().anyMatch(s -> boolFactor(s.getFactors(), "exerciseLate"));

        int base = durationCoreScore(total);

        int penalty = 0;
        if (fragmented)    penalty -= 8;
        if (exerciseLate)  penalty -= 6;

        int napBonus = (total < 420)
                ? Math.min(4, napTotal / 30)
                : 0;

        int score = clamp(base + penalty + napBonus, 0, 100);

        String article = buildArticle(dateKey, total, napTotal, fragmented, exerciseLate, score, timeRange);
        SessionManager.getSession().setLatestSleepArticle(article);
        articleDao.upsertArticle(uid, dateKey, score, article);
    }

    private boolean boolFactor(Map<String, Object> f, String key) {
        if (f == null) return false;
        Object v = f.get(key);
        return (v instanceof Boolean b && b) ||
                ("true".equalsIgnoreCase(String.valueOf(v)));
    }

    private int durationCoreScore(int totalMin) {
        double diff = (totalMin - 480.0) / 120.0;
        double score = 90.0 * Math.exp(-(diff * diff)) + 10.0;

        if (totalMin < 180 || totalMin > 720) {
            return (int) Math.round(clamp((int) score, 0, 90));
        }
        return (int) Math.round(Math.max(20, Math.min(score, 100)));
    }

    private int clamp(int v, int lo, int hi) { return Math.max(lo, Math.min(hi, v)); }

    private String buildArticle(String dateKey, int totalMin, int napMin, boolean fragmented, boolean exerciseLate, int score, String timeRange) {
        StringBuilder sb = new StringBuilder();

        sb.append("Your sleep score is ").append(score).append(". ");

        if (score >= 85) {
            sb.append("Great job! ");
        } else if (score >= 70) {
            sb.append("Nice work—you're close. ");
        } else if (score >= 55) {
            sb.append("You're getting there. ");
        } else {
            sb.append("Let’s improve your sleep. ");
        }

        sb.append("On ")
                .append(dateKey);
                if (timeRange != null && !timeRange.isBlank()) {
                    sb.append(" (").append(timeRange).append(") ");
                }
                sb.append(", your total sleep was ")
                .append(String.format("%.1f", totalMin / 60.0)).append("h");
        if (napMin > 0) sb.append(" (nap ").append(napMin).append(" min)");
        sb.append(". ");

        if (fragmented)   sb.append("Sleep was fragmented; try consolidating bedtime. ");
        if (exerciseLate) sb.append("Late exercise detected; aim to finish workouts earlier. ");

        if (score >= 85) {
            sb.append("Keep a consistent wind-down routine to maintain this.");
        } else if (score >= 70) {
            sb.append("A steady bedtime and fewer interruptions can lift you further.");
        } else if (score >= 55) {
            sb.append("Set a fixed lights-out time and simplify your evening routine.");
        } else {
            sb.append("Start with a fixed bedtime and limit late-night stimulation.");
        }

        return sb.toString();
    }

    public Map<String,Object> getArticle(String dateKey) throws ExecutionException, InterruptedException {
        return articleDao.getArticle(uid, dateKey);
    }

    public void deleteArticle(String dateKey) throws ExecutionException, InterruptedException {
        articleDao.deleteArticle(uid, dateKey);
    }


    private static class DaySummary {
        int totalMin;
        int napMin;
        boolean fragmented;
        boolean exerciseLate;
        int segments;
    }

    private static int nz(Integer v) { return v == null ? 0 : v; }

    private static boolean isTrue(Object o) {
        if (o == null) return false;
        if (o instanceof Boolean b) return b;
        return "true".equalsIgnoreCase(String.valueOf(o));
    }

    private String buildTimeRange(List<SleepModel> segments) {
        if (segments == null || segments.isEmpty()) return "";

        String earliestIso = segments.stream()
                .map(SleepModel::getStartIso)
                .filter(s -> s != null && !s.isBlank())
                .min(Comparator.naturalOrder())
                .orElse(null);

        String latestIso = segments.stream()
                .map(SleepModel::getEndIso)
                .filter(s -> s != null && !s.isBlank())
                .max(Comparator.naturalOrder())
                .orElse(null);

        if (earliestIso == null || latestIso == null) return "";

        try {
            OffsetDateTime start = OffsetDateTime.parse(earliestIso);
            OffsetDateTime end = OffsetDateTime.parse(latestIso);
            String startStr = start.toLocalTime().toString().substring(0, 5);
            String endStr = end.toLocalTime().toString().substring(0, 5);
            return startStr + " → " + endStr;
        } catch (Exception e) {
            return "";
        }
    }
}