package com.elevate5.elevateyou.model;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ArticleSearchModel {

    private ArrayList<ArticleModel> articles;
    private String searchString;

    public ArticleSearchModel() {
    }

    public ArrayList<ArticleModel> searchArticles(String searchString) throws MalformedURLException {

        articles = new ArrayList<>();

        URL url = new URL(buildAPIUrl(searchString));

        try {

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                ArrayNode results = getJsonNodes(conn);
                for(JsonNode node: results){
                    //System.out.println(node.toString());
                    String pattern = "^\"|\"$";
                    String articleUrl = node.get("url").toString().replaceAll(pattern, "");
                    //System.out.println(articleUrl);
                    String articleTitle = node.get("title").toString().replaceAll(pattern, "");
                    String articleDescription = node.get("description").toString().replaceAll(pattern, "");
                    String articleImage = node.get("urlToImage").toString().replaceAll(pattern, "");
                    String articleAuthor = node.get("author").toString().replaceAll(pattern, "");
                    ArticleModel article = new ArticleModel(articleUrl, articleAuthor, articleTitle, articleDescription, articleImage);
                    //System.out.println(article);
                    articles.add(article);
                }
            }else{
                System.out.println(responseCode + " Bad Connection");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return articles;
    }



    public void addArticle(ArticleModel article) {
        articles.add(article);
    }

    public ArrayList<ArticleModel> getArticles() {
        return articles;
    }

    public void setArticles(ArrayList<ArticleModel> articles) {
        this.articles = articles;
    }

    public String getNewsAPIKey() {
        return "3e7518386e88489fad37f636b99fbd5e";
    }

    public String buildAPIUrl(String searchString) {

        return "https://newsapi.org/v2/everything?q=" + searchString + "&sortBy=popularity&apiKey="+getNewsAPIKey();
    }

    private static ArrayNode getJsonNodes(HttpURLConnection conn) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder sb = new StringBuilder();

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();

        String jsonString = sb.toString();

        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(jsonString);

        return (ArrayNode) root.path("articles");
    }

}
