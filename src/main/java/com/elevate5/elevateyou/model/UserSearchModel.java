package com.elevate5.elevateyou.model;

import com.elevate5.elevateyou.App;
import com.elevate5.elevateyou.session.SessionManager;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class UserSearchModel {

    private ArrayList<String> friendUids;
    private ArrayList<String> receivedRequestUids;
    private ArrayList<String> sentRequestUids;
    private final DocumentReference currUserDocRef = App.fstore.collection("Users").document(SessionManager.getSession().getUserID());

    public UserSearchModel() {
        User currUser = SessionManager.getSession().getCurrUser();
        friendUids = currUser.getFriendsList();
        receivedRequestUids = currUser.getReceivedFriendRequestsList();
        sentRequestUids = currUser.getSentFriendRequestsList();
    }

    public void searchUsers(){
        //UserSearchModel  userSearchModel = new UserSearchModel();
    }

    public boolean containsFriend(String uid){
        return friendUids.contains(uid);
    }

    public boolean containsReceivedFriendRequest(String uid){
        return receivedRequestUids.contains(uid);
    }

    public boolean containsSentFriendRequest(String uid){
        return sentRequestUids.contains(uid);
    }

    public void sendFriendRequest(String uid){
        FriendRequestModel friendRequest = new FriendRequestModel(SessionManager.getSession().getUserID(), uid);

        sentRequestUids.add(friendRequest.getReceiverID());
        Map<String, Object> senderFriendsMap = new HashMap<>();
        senderFriendsMap.put("SentFriendRequests", sentRequestUids);
        SessionManager.getSession().getCurrUser().setSentFriendRequestsList(sentRequestUids);

        DocumentReference receiverDocRef = App.fstore.collection("Users").document(uid);
        ApiFuture<DocumentSnapshot> future = receiverDocRef.get();
        ArrayList<String> receiverFriendRequestsList;
        try {
            DocumentSnapshot receiverDoc = future.get();
            receiverFriendRequestsList = new ArrayList<>((List<String>) receiverDoc.get("ReceivedFriendRequests"));
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException");
            return;
        }
        receiverFriendRequestsList.add(friendRequest.getSenderID());
        Map<String, Object> receiverFriendsMap = new HashMap<>();
        receiverFriendsMap.put("ReceivedFriendRequests", receiverFriendRequestsList);

        this.currUserDocRef.update(senderFriendsMap);
        receiverDocRef.update(receiverFriendsMap);

    }

    public void acceptFriendRequest(String uid){
        FriendRequestModel friendRequest = new FriendRequestModel(uid, SessionManager.getSession().getUserID());

        receivedRequestUids.remove(friendRequest.getSenderID());
        Map<String, Object> receiverFriendsMap = new HashMap<>();
        receiverFriendsMap.put("ReceivedFriendRequests", receivedRequestUids);
        SessionManager.getSession().getCurrUser().setReceivedFriendRequestsList(receivedRequestUids);

        friendUids.add(friendRequest.getSenderID());
        receiverFriendsMap.put("Friends", friendUids);
        SessionManager.getSession().getCurrUser().setFriendsList(friendUids);

        DocumentReference senderDocRef = App.fstore.collection("Users").document(friendRequest.getSenderID());
        ApiFuture<DocumentSnapshot> future = senderDocRef.get();
        ArrayList<String> senderFriendRequestsList = new ArrayList<>();
        ArrayList<String> senderFriendsList = new ArrayList<>();
        try {
            DocumentSnapshot senderDoc = future.get();
            senderFriendRequestsList = new ArrayList<>((List<String>) senderDoc.get("SentFriendRequests"));
            senderFriendsList = new ArrayList<>((List<String>) senderDoc.get("Friends"));
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException");
        }
        senderFriendRequestsList.remove(friendRequest.getReceiverID());
        Map<String, Object> senderFriendsMap = new HashMap<>();
        senderFriendsMap.put("SentFriendRequests", senderFriendRequestsList);

        senderFriendsList.add(friendRequest.getReceiverID());
        senderFriendsMap.put("Friends", senderFriendsList);

        senderDocRef.update(senderFriendsMap);
        this.currUserDocRef.update(receiverFriendsMap);
    }

    public void denyFriendRequest(String uid){
        FriendRequestModel friendRequest = new FriendRequestModel(uid, SessionManager.getSession().getUserID());

        receivedRequestUids.remove(friendRequest.getSenderID());
        Map<String, Object> receiverFriendsMap = new HashMap<>();
        receiverFriendsMap.put("ReceivedFriendRequests", receivedRequestUids);
        SessionManager.getSession().getCurrUser().setReceivedFriendRequestsList(receivedRequestUids);

        DocumentReference senderDocRef = App.fstore.collection("Users").document(friendRequest.getSenderID());
        ApiFuture<DocumentSnapshot> future = senderDocRef.get();
        ArrayList<String> senderFriendRequestsList = new ArrayList<>();
        try {
            DocumentSnapshot senderDoc = future.get();
            senderFriendRequestsList = new ArrayList<>((List<String>) senderDoc.get("SentFriendRequests"));
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException");
        }
        senderFriendRequestsList.remove(friendRequest.getReceiverID());
        Map<String, Object> senderFriendsMap = new HashMap<>();
        senderFriendsMap.put("SentFriendRequests", senderFriendRequestsList);

        senderDocRef.update(senderFriendsMap);
        this.currUserDocRef.update(receiverFriendsMap);
    }

    public void removeFriend(String uid){
        friendUids.remove(uid);
        SessionManager.getSession().getCurrUser().setFriendsList(friendUids);
        Map<String, Object> userFriendsMap = new HashMap<>();
        userFriendsMap.put("Friends", friendUids);
        this.currUserDocRef.update(userFriendsMap);

        DocumentReference removedUserDocRef = App.fstore.collection("Users").document(uid);
        ApiFuture<DocumentSnapshot> future = removedUserDocRef.get();
        DocumentSnapshot removedUserDoc;
        try {
            removedUserDoc = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        ArrayList<String> removedUserFriendsList = new ArrayList<>((List<String>) removedUserDoc.get("Friends"));
        removedUserFriendsList.remove(SessionManager.getSession().getUserID());
        Map<String, Object> removedUserFriendsMap = new HashMap<>();
        removedUserFriendsMap.put("Friends", removedUserFriendsList);
        removedUserDocRef.update(removedUserFriendsMap);
    }


    public ArrayList<String> getFriendUids() {
        return friendUids;
    }

    public void setFriendUids(ArrayList<String> friendUids) {
        this.friendUids = friendUids;
    }

    public ArrayList<String> getReceivedRequestUids() {
        return receivedRequestUids;
    }

    public void setReceivedRequestUids(ArrayList<String> receivedRequestUids) {
        this.receivedRequestUids = receivedRequestUids;
    }

    public ArrayList<String> getSentRequestUids() {
        return sentRequestUids;
    }

    public void setSentRequestUids(ArrayList<String> sentRequestUids) {
        this.sentRequestUids = sentRequestUids;
    }
}
