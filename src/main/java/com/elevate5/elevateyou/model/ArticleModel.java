package com.elevate5.elevateyou.model;

public class ArticleModel {

    private String articleUrl;
    private String author;
    private String title;
    private String description;
    private String content;
    private String articleImageUrl;

    public ArticleModel(String articleUrl, String author, String title, String description, String content, String articleImageUrl) {
        this.articleUrl = articleUrl;
        this.author = author;
        this.title = title;
        this.description = description;
        this.content = content;
        this.articleImageUrl = articleImageUrl;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getArticleImageUrl() {
        return articleImageUrl;
    }

    public void setArticleImageUrl(String articleImageUrl) {
        this.articleImageUrl = articleImageUrl;
    }
}
