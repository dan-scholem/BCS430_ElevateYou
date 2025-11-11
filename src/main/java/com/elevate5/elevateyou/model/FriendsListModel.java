package com.elevate5.elevateyou.model;

import com.elevate5.elevateyou.session.SessionManager;
import com.google.cloud.firestore.DocumentReference;

import java.util.ArrayList;

public class FriendsListModel {

    private ArrayList<String> friendUids;
    private ArrayList<String> blockedUids;
    private final DocumentReference currUserDocRef;

    public FriendsListModel(DocumentReference userDocRef) {
        User currUser = SessionManager.getSession().getCurrUser();
        friendUids = currUser.getFriendsList();
        blockedUids = currUser.getBlockList();
        this.currUserDocRef = userDocRef;
    }

    public ArrayList<String> getFriendUids() {
        return friendUids;
    }

    public void setFriendUids(ArrayList<String> friendUids) {
        this.friendUids = friendUids;
    }

    public ArrayList<String> getBlockedUids() {
        return blockedUids;
    }

    public void setBlockedUids(ArrayList<String> blockedUids) {
        this.blockedUids = blockedUids;
    }

    public DocumentReference getCurrUserDocRef() {
        return currUserDocRef;
    }
}
