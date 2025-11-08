package com.elevate5.elevateyou.model;

public class FriendRequestModel {

    private String senderID;
    private String receiverID;
    private boolean requestAccepted;

    public FriendRequestModel(String senderID, String receiverID) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.requestAccepted = false;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public boolean isRequestAccepted() {
        return requestAccepted;
    }

    public void setRequestAccepted(boolean requestAccepted) {
        this.requestAccepted = requestAccepted;
    }
}
