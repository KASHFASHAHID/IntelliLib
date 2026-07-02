package view;

import controller.LoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import view.AdminDashboardView;

public class LoginView {

    private LoginController loginController;

    public LoginView() {
        loginController = new LoginController();
    }

    public Scene createScene() {

        StackPane root = new StackPane();

        VBox card = new VBox(22);
        card.getStyleClass().add("login-card");
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(45));
        card.setMaxWidth(520);
        card.setMinHeight(620);

        StackPane logoBox = new StackPane();
        logoBox.getStyleClass().add("logo-box");
        logoBox.setPrefSize(78, 78);
        logoBox.setMinSize(78, 78);
        logoBox.setMaxSize(78, 78);

        Text logo = new Text("▤");
        logo.setStyle("-fx-fill: white; -fx-font-size: 42px; -fx-font-weight: bold;");
        logoBox.getChildren().add(logo);

        Text title = new Text("Brainware Smart Library");
        title.getStyleClass().add("title");

        Text subtitle = new Text("Sign in to your account");
        subtitle.getStyleClass().add("subtitle");

        TextField userIdField = new TextField();
        userIdField.setPromptText("User ID");
        userIdField.getStyleClass().add("input");
        userIdField.setPrefHeight(58);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("input");
        passwordField.setPrefHeight(58);

        Text messageText = new Text();
        messageText.getStyleClass().add("small-text");

        CheckBox rememberMe = new CheckBox("Remember me");
        rememberMe.getStyleClass().add("small-text");
        rememberMe.setSelected(true);

        Hyperlink forgotPassword = new Hyperlink("Forgot password?");
        forgotPassword.getStyleClass().add("link");

        HBox options = new HBox();
        options.setAlignment(Pos.CENTER);
        options.setSpacing(170);
        options.getChildren().addAll(rememberMe, forgotPassword);

        Button loginButton = new Button("Login  →");
        loginButton.getStyleClass().add("login-button");
        loginButton.setPrefHeight(60);
        loginButton.setMaxWidth(Double.MAX_VALUE);

        loginButton.setOnAction(event -> {

    model.User user = loginController.handleLogin(
            userIdField.getText(),
            passwordField.getText()
    );

    if (user != null) {

        Stage stage = (Stage) loginButton.getScene().getWindow();

        AdminDashboardView dashboard = new AdminDashboardView(user);

        stage.setScene(dashboard.createScene());
        stage.setTitle("Brainware Smart Library - Admin Dashboard");

    } else {
        messageText.setText("Invalid User ID or Password.");
        messageText.setStyle("-fx-fill: #ef4444;");
    }
});

        Text demoText = new Text("NEW TO BRAINWARE SMART LIBRARY?");
        demoText.getStyleClass().add("footer");

        Text accessText = new Text("Contact library admin to create your account");
        accessText.getStyleClass().add("small-text");

        card.getChildren().addAll(
                logoBox,
                title,
                subtitle,
                userIdField,
                passwordField,
                options,
                loginButton,
                messageText,
                demoText,
                accessText
        );

        root.getChildren().add(card);

        Scene scene = new Scene(root, 1200, 760);
        scene.getStylesheets().add(
                getClass().getResource("/css/login.css").toExternalForm()
        );

        return scene;
    }
}