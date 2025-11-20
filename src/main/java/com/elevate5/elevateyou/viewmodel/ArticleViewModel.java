package com.elevate5.elevateyou.viewmodel;

import com.elevate5.elevateyou.model.ArticleModel;
import com.elevate5.elevateyou.model.ArticleSearchModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.net.MalformedURLException;
import java.util.ArrayList;

public class ArticleViewModel {

    private final StringProperty searchString =  new SimpleStringProperty();
    private final StringProperty category =  new SimpleStringProperty();
    private ArticleSearchModel articleSearchModel =  new ArticleSearchModel();

    public ArticleViewModel() {
        category.setValue("General");
    }



    public ArrayList<ArticleModel> searchArticles() throws MalformedURLException {
        return articleSearchModel.searchArticles((categoryProperty().get().toLowerCase() + "+\"" + searchStringProperty().get() + "\"").replaceAll(" ", "+"));
    }

    public ArrayList<ArticleModel> getDefaultArticles() throws MalformedURLException {
        return articleSearchModel.searchArticles("");
    }

    public String getSearchString() {
        return searchString.get();
    }

    public StringProperty searchStringProperty() {
        return searchString;
    }

    public String getCategory() {
        return category.get();
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public ArticleSearchModel getArticleSearchModel() {
        return articleSearchModel;
    }

    public void setArticleSearchModel(ArticleSearchModel articleSearchModel) {
        this.articleSearchModel = articleSearchModel;
    }
}
