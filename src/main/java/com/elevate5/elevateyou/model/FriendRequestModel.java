package com.elevate5.elevateyou.model;

public class FriendRequestModel {

    private String senderID;
    private String receiverID;

    public FriendRequestModel(String senderID, String receiverID) {
        this.senderID = senderID;
        this.receiverID = receiverID;
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

}
