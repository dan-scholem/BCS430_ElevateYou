package com.elevate5.elevateyou.model;

public class SleepArticleModel {
    private String dateKey;
    private Integer score;        // 0-100
    private String article;       // generated short text
    public SleepArticleModel() {}

    public SleepArticleModel(String dateKey, Integer score, String article) {
        this.dateKey = dateKey;
        this.score = score;
        this.article = article;
    }
    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getDateKey() {
        return dateKey;
    }

    public void setDateKey(String dateKey) {
        this.dateKey = dateKey;
    }
}