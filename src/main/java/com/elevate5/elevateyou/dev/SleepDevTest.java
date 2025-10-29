package com.elevate5.elevateyou.dev;

import com.elevate5.elevateyou.App;
import com.elevate5.elevateyou.model.FirestoreContext;
import com.elevate5.elevateyou.model.SleepModel;
import com.elevate5.elevateyou.service.SleepService;
import com.elevate5.elevateyou.service.SleepArticleService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SleepDevTest {

    public static void main(String[] args) {
        if (App.fstore == null) {
            App.fstore = new FirestoreContext().firebase();
        }

        SleepService sleepSvc = new SleepService();
        SleepArticleService articleSvc = new SleepArticleService();


        try {
//            sleepSvc.addSegment("2025-10-27", makeSegment("2025-10-27T22:00:00-04:00", "2025-10-28T06:00:00-04:00", 480, 0, false, false));
//
//            sleepSvc.addSegment("2025-10-26", makeSegment("2025-10-26T00:00:00-04:00", "2025-10-26T05:00:00-04:00", 300, 0, false, false));
//
//            sleepSvc.addSegment("2025-10-25", makeSegment("2025-10-25T21:00:00-04:00", "2025-10-26T07:00:00-04:00", 600, 0, false, false));
//
//            sleepSvc.addSegment("2025-10-24", makeSegment("2025-10-24T22:00:00-04:00", "2025-10-25T02:00:00-04:00", 240, 0, true, false));
//            sleepSvc.addSegment("2025-10-24", makeSegment("2025-10-25T03:00:00-04:00", "2025-10-25T07:00:00-04:00", 240, 0, true, false));
//
//            sleepSvc.addSegment("2025-10-23", makeSegment("2025-10-23T22:30:00-04:00", "2025-10-24T06:30:00-04:00", 480, 0, false, true));
//
//            sleepSvc.addSegment("2025-10-22", makeSegment("2025-10-22T23:00:00-04:00", "2025-10-23T04:00:00-04:00", 300, 0, false, false));
//            sleepSvc.addSegment("2025-10-22", makeSegment("2025-10-23T13:00:00-04:00", "2025-10-23T14:30:00-04:00", 90, 90, true, false));

            sleepSvc.addSegment("2025-10-21", makeSegment("2025-10-21T02:00:00-04:00", "2025-10-21T05:00:00-04:00", 210, 30, true, true));

            // article
            String[] testDates = {
//                    "2025-10-27", "2025-10-26", "2025-10-25",
//                    "2025-10-24", "2025-10-23", "2025-10-22",
                    "2025-10-21"
            };

            for (String d : testDates) {
                articleSvc.generateAndSave(d);
                Map<String, Object> art = articleSvc.getArticle(d);
                System.out.println("📅 " + d + " => " + art);
            }

            System.out.println("all set up");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SleepModel makeSegment(String start, String end, int totalMin, int nap, boolean fragmented, boolean exerciseLate) {
        SleepModel model = new SleepModel();
        model.setStartIso(start);
        model.setEndIso(end);
        model.setDurationMin(totalMin);
        model.setTotalMin(totalMin);
        model.setNap(nap);

        Map<String, Object> factors = new HashMap<>();
        factors.put("fragmented", fragmented);
        factors.put("exerciseLate", exerciseLate);
        model.setFactors(factors);

        return model;
    }
}