package com.elevate5.elevateyou.notification;

/** output abstraction for delivering notifications. Replace with UI later. **/
public interface NotificationSink {
    void deliver(int userId, String title, String body);
}


