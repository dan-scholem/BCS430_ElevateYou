package com.elevate5.elevateyou.view;

import com.elevate5.elevateyou.*;
import com.elevate5.elevateyou.model.ArticleModel;
import com.elevate5.elevateyou.service.NotificationService;
import com.elevate5.elevateyou.session.SessionManager;
import com.elevate5.elevateyou.viewmodel.ArticleViewModel;
import com.elevate5.elevateyou.viewmodel.FriendsListViewModel;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ArticleView extends Application {

    @FXML
    private Button appointmentsButton;
    @FXML
    private Button calendarButton;
    @FXML
    private Button dashButton;
    @FXML
    private Button exerciseButton;
    @FXML
    private Button foodButton;
    @FXML
    private Button journalButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button medButton;
    @FXML
    private Button sleepButton;
    @FXML
    private Button friendsButton;
    @FXML
    private Button quotesaffirmationBtn;
    @FXML
    private Button dailyArticlesButton;
    @FXML
    private Label activityLabel;
    @FXML
    private ComboBox<String> categoryBox;
    @FXML
    private TextField searchField;
    @FXML
    private ScrollPane articleScrollPane;
    @FXML
    private BorderPane articleBorderPane;
    @FXML
    private TilePane resultsPane;

    private WebView webView;

    private Popup favPopup;

    private ArticleViewModel articleViewModel;

    @FXML
    public void initialize() throws IOException {

        webView = new WebView();
        webView.getEngine().setJavaScriptEnabled(false);

        articleViewModel = new ArticleViewModel();

        displayDefaultArticles();

        categoryBox.valueProperty().bindBidirectional(articleViewModel.categoryProperty());
        searchField.textProperty().bindBidirectional(articleViewModel.searchStringProperty());

    }

    private void displayDefaultArticles() throws IOException {
        resultsPane.getChildren().clear();
        activityLabel.setText("Daily Articles");
        ArrayList<ArticleModel> articles = articleViewModel.getDefaultArticles();
        generateArticles(articles);
        dailyArticlesButton.setVisible(false);
    }

    @FXML
    private void searchButtonClick(ActionEvent event) throws MalformedURLException {
        resultsPane.getChildren().clear();
        activityLabel.setText(categoryBox.getValue() + " Articles");
        ArrayList<ArticleModel> articles = articleViewModel.searchArticles();
        generateArticles(articles);
        dailyArticlesButton.setVisible(true);
    }

    @FXML
    private void dailyArticlesButtonClick(ActionEvent event) throws IOException {
        displayDefaultArticles();
    }

    private void generateArticles(ArrayList<ArticleModel> articles) {
        for (ArticleModel article : articles) {

            Image articleImage;
            try{
                articleImage = new Image(article.getArticleImageUrl(), 150,100,true,false, true);
                //System.out.println(articleImage.getUrl());
            } catch (IllegalArgumentException e) {
                articleImage = new Image("https://icons.iconarchive.com/icons/iconarchive/childrens-book-animals/48/Duck-icon.png", 150,100,false,false, true);
            }

            ImageView articleImageView = new ImageView(articleImage);
            articleImageView.setFitHeight(100);
            articleImageView.setFitWidth(150);
            articleImageView.setPreserveRatio(true);

            Label title = new Label(article.getTitle());
            title.setWrapText(true);
            title.setPrefWidth(200);
            title.setAlignment(Pos.CENTER);

            VBox resultBox = new VBox(articleImageView, title);
            resultBox.setAlignment(Pos.CENTER);
            resultBox.setSpacing(15);

            Button resultButton = new Button();
            resultButton.setGraphic(resultBox);
            resultButton.setContentDisplay(ContentDisplay.TOP);
            resultButton.setGraphicTextGap(5);
            resultButton.setCursor(Cursor.HAND);
            resultButton.wrapTextProperty().setValue(true);
            resultButton.setStyle("-fx-background-color: white;");
            resultButton.setOnAction(e->{
                webView.getEngine().load(article.getArticleUrl());
                Button backButton = getBackButton();
                Button favoriteButton = getFavoriteButton();
                StackPane root = new StackPane(webView, backButton, favoriteButton);
                StackPane.setAlignment(backButton, Pos.TOP_LEFT);
                StackPane.setAlignment(favoriteButton, Pos.TOP_RIGHT);
                StackPane.setAlignment(webView, Pos.TOP_CENTER);
                StackPane.setMargin(backButton, new Insets(20,0,0,20));
                StackPane.setMargin(favoriteButton, new Insets(20,20,0,0));
                articleBorderPane.setCenter(root);
            });
            resultsPane.getChildren().add(resultButton);
        }
    }

    private @NotNull Button getBackButton() {
        Button backButton = new Button("Back");
        backButton.setPrefSize(70,70);
        backButton.setCursor(Cursor.HAND);
        backButton.setStyle("-fx-background-color: white;" +
                "-fx-background-radius: 50%;" +
                "-fx-border-color: black;" +
                "-fx-border-radius: 50%;" +
                "-fx-border-width: 2");
        backButton.setOnAction(e2->{
            articleBorderPane.setCenter(articleScrollPane);
        });
        return backButton;
    }

    private @NotNull Button getFavoriteButton() {
        Button favoriteButton = new Button();
        favoriteButton.setPrefSize(40,40);
        favoriteButton.setStyle("-fx-shape: \"M50,15 L61,35 L84,39 L67,55 L71,78 L50,67 L29,78 L33,55 L16,39 L39,35 Z\";" +
                "-fx-background-color: white;-fx-border-color: black");
        favoriteButton.setUserData("false");
        favoriteButton.setCursor(Cursor.HAND);
        favoriteButton.setOnAction(e3->{
            if(favoriteButton.getUserData().equals("false")){
                favoriteButton.setUserData("true");
                favoriteButton.setStyle("-fx-shape: \"M50,15 L61,35 L84,39 L67,55 L71,78 L50,67 L29,78 L33,55 L16,39 L39,35 Z\";" +
                        "-fx-background-color: gold;-fx-border-color: black");
                toggleFavoritePopup((String) favoriteButton.getUserData());
            }else{
                favoriteButton.setUserData("false");
                favoriteButton.setStyle("-fx-shape: \"M50,15 L61,35 L84,39 L67,55 L71,78 L50,67 L29,78 L33,55 L16,39 L39,35 Z\";" +
                        "-fx-background-color: white;-fx-border-color: black");
                toggleFavoritePopup((String) favoriteButton.getUserData());
            }
        });
        return favoriteButton;
    }

    private void toggleFavoritePopup(String text){
        favPopup = new Popup();
        StackPane contentPane = new StackPane();
        contentPane.setStyle("-fx-background-color: gray");
        Label popupLabel = new Label();
        popupLabel.setStyle("-fx-background-color: gray;-fx-font-size: 24");
        if(text.equals("true")){
            popupLabel.setText("Added to Favorites");
        }else{
            popupLabel.setText("Removed from Favorites");
        }
        contentPane.getChildren().add(popupLabel);
        favPopup.getContent().add(contentPane);
        favPopup.show(articleBorderPane.getScene().getWindow());
        contentPane.setOpacity(1);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), contentPane);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setCycleCount(1);
        fadeTransition.setAutoReverse(false);
        fadeTransition.play();
        fadeTransition.setOnFinished(e -> favPopup.hide());
    }


    // Logout
    @FXML
    private void logoutUser(ActionEvent event) throws IOException {
        Stage stage;
        Alert logoutalert = new Alert(Alert.AlertType.CONFIRMATION);
        logoutalert.setTitle("Logout");
        logoutalert.setHeaderText("You are about to logout!");
        logoutalert.setContentText("Are you sure you want to logout?");

        if (logoutalert.showAndWait().get() == ButtonType.OK) {
            stage = (Stage) logoutButton.getScene().getWindow();
            System.out.println("User logged out successfully");
            SessionManager.closeSession();
            stage.close();
            UserLogin.loadUserLoginScene(stage);
        }
    }

    @FXML
    protected void dashboardButtonClick() {
        try {
            Stage stage = (Stage) dashButton.getScene().getWindow();
            Dashboard.loadDashboardScene(stage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void medicationButtonClick() throws IOException {
        try {
            Stage stage = (Stage) medButton.getScene().getWindow();
            Medication.loadMedTrackerScene(stage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void calendarButtonClick() throws IOException {
        try {
            Stage stage = (Stage) calendarButton.getScene().getWindow();
            CalendarView.loadCalendarScene(stage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void foodButtonClick() {
        try {
            Stage stage = (Stage) foodButton.getScene().getWindow();
            CaloriesWaterIntake.loadCaloriesWaterIntakeScene(stage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void journalButtonClick() throws IOException {
        try {
            Stage stage = (Stage) journalButton.getScene().getWindow();
            JournalEntry.loadJournalScene(stage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
/*
    @FXML
    protected void settingsButtonClick() throws IOException {

        try {
            Stage stage = (Stage) profileButton.getScene().getWindow();

            UserProfile.loadSettingsScene(stage);
        } catch (IOException e) {

            throw new RuntimeException(e);
        }

    }

    @FXML
    public void setNotificationsButton() {
        try {
            String uid = (SessionManager.getSession() != null && SessionManager.getSession().getUserID() != null)
                    ? SessionManager.getSession().getUserID()
                    : "";
            String email = (SessionManager.getSession() != null && SessionManager.getSession().getUser() != null)
                    ? SessionManager.getSession().getUser().getEmail()
                    : "";

            NotificationService svc = new NotificationService(uid, email);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/elevate5/elevateyou/Notification.fxml"));
            Node bell = loader.load();

            NotificationController ctrl = loader.getController();
            ctrl.setService(svc);

            topRightBar.getChildren().add(bell);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 */

    // Exercise navigation
    @FXML
    private void exerciseButtonClick(ActionEvent event) {
        System.out.println("[NAV] Exercise clicked"); // visibility in console
        FXMLLoader loader;
        try {
            loader = new FXMLLoader(
                    getClass().getResource("/com/elevate5/elevateyou/exercise.fxml")
            );
            Node view = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene((javafx.scene.Parent) view, 900, 600));
            stage.setTitle("Exercise Tracker");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open Exercise:\n" + e.getMessage()).showAndWait();
        }
    }
    @FXML
    public void appointmentButtonClick() throws IOException {
        try {
            Stage stage = (Stage) appointmentsButton.getScene().getWindow();
            AppointmentView.loadAppointmentScene(stage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void sleepButtonClick() {
        try {
            Stage s = (Stage) sleepButton.getScene().getWindow();
            Sleep.loadSleepScene(s);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @FXML
    protected void friendsButtonClick() {
        try {
            Stage stage = (Stage) friendsButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/elevate5/elevateyou/FriendsListView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            FriendsListView controller = fxmlLoader.getController();
            controller.setViewModel(new FriendsListViewModel());
            stage.setTitle("Friends");
            stage.setScene(scene);
            stage.show();
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void quoteaffirmationButtonClick() throws IOException {

        try {
            Stage stage = (Stage) quotesaffirmationBtn.getScene().getWindow();

            QuotesAffirmations.loadQuotesAffirmationsScene(stage);
        }

        catch (IOException e) {

            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("ArticleView.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1218, 738);
        stage.setTitle("Articles");
        stage.setScene(scene);
        stage.show();
    }


}
