package com.elevate5.elevateyou.viewmodel;

import com.elevate5.elevateyou.App;
import com.elevate5.elevateyou.model.FriendsListModel;
import com.elevate5.elevateyou.model.UserSearchModel;
import com.elevate5.elevateyou.session.SessionManager;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FriendsListViewModel {

    private final ObjectProperty<ArrayList<String>> friendUids = new SimpleObjectProperty<>();
    private final ObjectProperty<ArrayList<String>> requestUids = new SimpleObjectProperty<>();
    private final StringProperty searchString = new SimpleStringProperty();
    private UserSearchModel userSearchModel;
    private FriendsListModel  friendsListModel;

    public FriendsListViewModel() {
        DocumentReference currUserDocRef = App.fstore.collection("Users").document(SessionManager.getSession().getUserID());
        this.userSearchModel = new UserSearchModel();
        this.friendsListModel = new FriendsListModel(currUserDocRef);
    }

    public ArrayList<DocumentSnapshot> searchUsers(){
        return userSearchModel.searchUsers(searchString.get());
    }

    public ArrayList<DocumentSnapshot> getUserDocs(ArrayList<String> uids) throws ExecutionException, InterruptedException {
        return friendsListModel.getUserDocs(uids);
    }

    public boolean containsFriendUid(String uid){
        return friendsListModel.containsFriend(uid);
    }

    public boolean containsReceivedFriendRequestUid(String uid){
        return friendsListModel.containsReceivedFriendRequest(uid);
    }

    public boolean containsSentFriendRequestUid(String uid){
        return friendsListModel.containsSentFriendRequest(uid);
    }

    public void sendFriendRequest(String uid){
        friendsListModel.sendFriendRequest(uid);
    }

    public void acceptFriendRequest(String uid){
        friendsListModel.acceptFriendRequest(uid);
    }

    public void denyFriendRequest(String uid){
        friendsListModel.denyFriendRequest(uid);
    }

    public void removeFriend(String uid){
        friendsListModel.removeFriend(uid);
    }

    public ArrayList<String> getFriendUids() {
        return friendsListModel.getFriendUids();
    }

    public ObjectProperty<ArrayList<String>> friendUidsProperty() {
        return friendUids;
    }

    public ArrayList<String> getRequestUids() {
        return friendsListModel.getReceivedRequestUids();
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
