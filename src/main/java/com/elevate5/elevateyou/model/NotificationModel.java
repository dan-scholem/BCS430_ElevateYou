package com.elevate5.elevateyou.model;

import java.time.Instant;

public class NotificationModel {
    private String id;
    private String userId;
    private String title;
    private String body;
    private String linkType;
    private String linkRefId;
    private Instant dueAt;
    private Instant createdAt;
    private boolean read;

    public NotificationModel() {
        // default constructor
    }
    public NotificationModel(String id, String userId, String title, String body, String linkType,  String linkRefId, Instant dueAt, Instant createdAt, boolean read) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.linkType = linkType;
        this.linkRefId = linkRefId;
        this.dueAt = dueAt;
        this.createdAt = createdAt;
        this.read = read;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public String getLinkType() {
        return linkType;
    }
    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }
    public String getLinkRefId() {
        return linkRefId;
    }
    public void setLinkRefId(String linkRefId) {
        this.linkRefId = linkRefId;
    }
    public Instant getDueAt() {
        return dueAt;
    }
    public void setDueAt(Instant dueAt) {
        this.dueAt = dueAt;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    public boolean isRead() {
        return read;
    }
    public void setRead(boolean read) {
        this.read = read;
    }
}
