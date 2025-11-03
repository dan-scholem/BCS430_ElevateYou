package com.elevate5.elevateyou.service;

import com.elevate5.elevateyou.dao.SleepDao;
import com.elevate5.elevateyou.model.SleepModel;
import com.elevate5.elevateyou.session.SessionManager;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class SleepService {

    private final SleepDao dao;
    private final String uid;

    public SleepService() {
        this.dao = new SleepDao();
        if (SessionManager.getSession() != null && SessionManager.getSession().getUserID() != null) {
            this.uid = SessionManager.getSession().getUserID();
        } else {
            this.uid = "dev-demo-uid";
        }
    }

    public void addSegment(String dateKey, SleepModel segment) throws ExecutionException, InterruptedException {
        if (dateKey == null || dateKey.isBlank()) {
            throw new IllegalArgumentException("dateKey cannot be null or blank");
        }
        if (segment == null || !segment.isComplete()) {
            throw new IllegalArgumentException("segment is incomplete");
        }
        dao.appendSegment(uid, dateKey, segment);
    }


    public void overwriteDay(String dateKey, List<SleepModel> segments) throws ExecutionException, InterruptedException {
        if (dateKey == null || dateKey.isBlank()) {
            throw new IllegalArgumentException("dateKey cannot be null or blank");
        }
        dao.setDay(uid, dateKey, segments != null ? segments : Collections.emptyList());
    }

    public void deleteDay(String dateKey) throws ExecutionException, InterruptedException {
        if (dateKey == null || dateKey.isBlank()) {
            return;
        }
        dao.deleteDay(uid, dateKey);
    }

    public void deleteSegment(String dateKey, int index) throws ExecutionException, InterruptedException {
        if (dateKey == null || dateKey.isBlank()) {
            return;
        }
        dao.deleteSegmentAt(uid, dateKey, index);
    }

    public List<SleepModel> getSegmentsForDay(String dateKey) throws ExecutionException, InterruptedException {
        if (dateKey == null || dateKey.isBlank()) {
            return Collections.emptyList();
        }
        return dao.getDay(uid, dateKey);
    }

    public Map<String, List<SleepModel>> getAllSegments() throws ExecutionException, InterruptedException {
        return dao.getAllDays(uid);
    }

    public void addSegmentAndRefreshArticle(String dateKey, SleepModel segment)
            throws ExecutionException, InterruptedException {
        addSegment(dateKey, segment);
        new SleepArticleService().generateAndSave(dateKey);
    }

}