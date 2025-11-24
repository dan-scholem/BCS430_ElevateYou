package com.elevate5.elevateyou.session;

public class SessionManager {
    private static Session session;

    public static Session getSession() {
        return session;
    }

    public static void setSession(Session session) {
        SessionManager.session = session;
    }

    public static void closeSession() {
        session.getWebView().getEngine().load(null);
        session.setWebView(null);
        session.saveCalorieGoalToFirestore();
        session = null;


    }


}
