package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.User;

public class AdminDashboardView {

    private User loggedInUser;

    public AdminDashboardView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public Scene createScene() {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f172a;");

        Text title = new Text("Brainware Smart Library");
        title.setStyle("-fx-fill: white; -fx-font-size: 30px; -fx-font-weight: bold;");

        Text welcome = new Text("Welcome, " + loggedInUser.getName());
        welcome.setStyle("-fx-fill: #38bdf8; -fx-font-size: 18px;");

        VBox topBox = new VBox(10, title, welcome);
        topBox.setPadding(new Insets(30));
        topBox.setAlignment(Pos.CENTER_LEFT);

        VBox menu = new VBox(18);
        menu.setPadding(new Insets(30));
        menu.setAlignment(Pos.TOP_LEFT);
        menu.setStyle("-fx-background-color: #111827;");

        Button membershipBtn = createMenuButton("Membership Requests");
        Button booksBtn = createMenuButton("Manage Books");
        Button issueBtn = createMenuButton("Issue Book");
        Button returnBtn = createMenuButton("Return Book");
        Button finesBtn = createMenuButton("Fines");
        Button reportsBtn = createMenuButton("Reports");
        Button logoutBtn = createMenuButton("Logout");

        menu.getChildren().addAll(
                membershipBtn,
                booksBtn,
                issueBtn,
                returnBtn,
                finesBtn,
                reportsBtn,
                logoutBtn
        );

        Text content = new Text("Admin Dashboard");
        content.setStyle("-fx-fill: white; -fx-font-size: 26px;");

        VBox centerBox = new VBox(content);
        centerBox.setAlignment(Pos.CENTER);

        root.setTop(topBox);
        root.setLeft(menu);
        root.setCenter(centerBox);

        return new Scene(root, 1200, 760);
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(220);
        button.setPrefHeight(45);
        button.setStyle(
                "-fx-background-color: #2563eb;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-background-radius: 10;"
        );
        return button;
    }
}