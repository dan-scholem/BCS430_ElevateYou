package com.elevate5.elevateyou.viewmodel;

import com.elevate5.elevateyou.App;
import com.elevate5.elevateyou.model.FriendsListModel;
import com.elevate5.elevateyou.model.UserSearchModel;
import com.elevate5.elevateyou.session.SessionManager;
import com.google.cloud.firestore.DocumentReference;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class FriendsListViewModel {

    private final ObjectProperty<ArrayList<String>> friendUids = new SimpleObjectProperty<>();
    private final ObjectProperty<ArrayList<String>> requestUids = new SimpleObjectProperty<>();
    private final StringProperty searchString = new SimpleStringProperty();
    private UserSearchModel userSearchModel;
    private FriendsListModel  friendsListModel;

    public FriendsListViewModel() {
        DocumentReference currUserDocRef = App.fstore.collection("Users").document(SessionManager.getSession().getUserID());
        this.userSearchModel = new UserSearchModel(currUserDocRef);
        this.friendsListModel = new FriendsListModel(currUserDocRef);
    }

    public boolean containsFriendUid(String uid){
        return userSearchModel.containsFriend(uid);
    }

    public boolean containsReceivedFriendRequestUid(String uid){
        return userSearchModel.containsReceivedFriendRequest(uid);
    }

    public boolean containsSentFriendRequestUid(String uid){
        return userSearchModel.containsSentFriendRequest(uid);
    }

    public void sendFriendRequest(String uid){
        userSearchModel.sendFriendRequest(uid);
    }

    public void acceptFriendRequest(String uid){
        userSearchModel.acceptFriendRequest(uid);
    }

    public void denyFriendRequest(String uid){
        userSearchModel.denyFriendRequest(uid);
    }

    public void removeFriend(String uid){
        userSearchModel.removeFriend(uid);
    }


    public ArrayList<String> getFriendUids() {
        return friendsListModel.getFriendUids();
    }

    public ObjectProperty<ArrayList<String>> friendUidsProperty() {
        return friendUids;
    }

    public ArrayList<String> getRequestUids() {
        return userSearchModel.getReceivedRequestUids();
    }

    public ObjectProperty<ArrayList<String>> requestUidsProperty() {
        return requestUids;
    }

    public String getSearchString() {
        return searchString.get();
    }

    public StringProperty searchStringProperty() {
        return searchString;
    }

    public UserSearchModel getUserSearchModel() {
        return userSearchModel;
    }

    public void setUserSearchModel(UserSearchModel userSearchModel) {
        this.userSearchModel = userSearchModel;
    }

    public FriendsListModel getFriendsListModel() {
        return friendsListModel;
    }

    public void setFriendsListModel(FriendsListModel friendsListModel) {
        this.friendsListModel = friendsListModel;
    }
}
