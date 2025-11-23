package com.elevate5.elevateyou.model;

import com.elevate5.elevateyou.session.SessionManager;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteResult;

import java.util.ArrayList;

public class SavedArticlesManager {

    private ArrayList<ArticleModel> savedArticles;

    public SavedArticlesManager() {
        this.savedArticles = new ArrayList<>();
    }

    public void addArticle(ArticleModel article){
        this.savedArticles.add(article);
    }

    public void removeArticle(ArticleModel article){
        this.savedArticles.remove(article);
    }

    public void saveArticleToFirestore(ArticleModel article){
        SessionManager.getSession().getSavedArticlesManager().addArticle(article);
        DocumentReference savedArticlesDocRef = SessionManager.getSession().getSavedArticlesDocRef();
        ApiFuture<WriteResult> result = savedArticlesDocRef.set(SessionManager.getSession().getSavedArticlesManager());
    }

    public void removeArticleFromFirestore(ArticleModel article){
        SessionManager.getSession().getSavedArticlesManager().removeArticle(article);
        DocumentReference savedArticlesDocRef = SessionManager.getSession().getSavedArticlesDocRef();
        ApiFuture<WriteResult> result = savedArticlesDocRef.set(SessionManager.getSession().getSavedArticlesManager());
    }

    public ArrayList<ArticleModel> getSavedArticles() {
        return savedArticles;
    }

    public void setSavedArticles(ArrayList<ArticleModel> savedArticles) {
        this.savedArticles = savedArticles;
    }


}
