package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.User;
import util.SceneRouter;

public class StudentDashboardView {

    private final User loggedInUser;

    public StudentDashboardView(
            User loggedInUser
    ) {
        this.loggedInUser = loggedInUser;
    }

    public Scene createScene() {

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color:#e8f1f2;"
        );

        root.setTop(
                createHeader()
        );

        root.setLeft(
                createSideMenu()
        );

        root.setCenter(
                createDashboardContent()
        );

        return new Scene(
                root,
                1200,
                760
        );
    }

    private VBox createHeader() {

        Text title =
                new Text(
                        "IntelliLib"
                );

        title.setStyle(
                "-fx-fill:#0f172a;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        Label welcomeLabel =
                new Label(
                        "Welcome, "
                                + loggedInUser.getName()
                );

        welcomeLabel.setStyle(
                "-fx-text-fill:#0f766e;" +
                "-fx-font-size:17px;" +
                "-fx-font-weight:bold;"
        );

        Label roleLabel =
                new Label(
                        "Student Member Portal"
                );

        roleLabel.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:14px;"
        );

        VBox userInformation =
                new VBox(
                        5,
                        welcomeLabel,
                        roleLabel
                );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label accountLabel =
                new Label(
                        "● ACTIVE"
                );

        accountLabel.setStyle(
                "-fx-background-color:#dcfce7;" +
                "-fx-text-fill:#166534;" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:9 16;" +
                "-fx-background-radius:18;" +
                "-fx-border-color:#86efac;" +
                "-fx-border-radius:18;"
        );

        HBox headerRow =
                new HBox(
                        20,
                        userInformation,
                        spacer,
                        accountLabel
                );

        headerRow.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox header =
                new VBox(
                        12,
                        title,
                        headerRow
                );

        header.setPadding(
                new Insets(
                        25,
                        35,
                        25,
                        35
                )
        );

        header.setStyle(
                "-fx-background-color:#f8fafc;" +
                "-fx-border-color:transparent transparent #cbd5e1 transparent;" +
                "-fx-border-width:0 0 1 0;"
        );

        return header;
    }

    private VBox createSideMenu() {

        VBox menu =
                new VBox(12);

        menu.setPrefWidth(285);

        menu.setPadding(
                new Insets(
                        25,
                        22,
                        25,
                        22
                )
        );

        menu.setAlignment(
                Pos.TOP_CENTER
        );

        menu.setStyle(
                "-fx-background-color:#111827;" +
                "-fx-border-color:transparent #1e293b transparent transparent;" +
                "-fx-border-width:0 1 0 0;"
        );

        Label menuTitle =
                new Label(
                        "STUDENT MENU"
                );

        menuTitle.setMaxWidth(
                Double.MAX_VALUE
        );

        menuTitle.setStyle(
                "-fx-text-fill:#94a3b8;" +
                "-fx-font-size:12px;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:0 0 8 5;"
        );

        Button searchBooksButton =
                createMenuButton(
                        "🔍",
                        "Search Books"
                );

        Button borrowedBooksButton =
                createMenuButton(
                        "📚",
                        "My Borrowed Books"
                );

        Button reservationsButton =
                createMenuButton(
                        "🔖",
                        "My Reservations"
                );

        Button finesButton =
                createMenuButton(
                        "₹",
                        "My Fines"
                );

        Button notificationsButton =
                createMenuButton(
                        "🔔",
                        "Notifications"
                );

        Button profileButton =
                createMenuButton(
                        "👤",
                        "My Profile"
                );

        Region spacer =
                new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        Button logoutButton =
                createLogoutButton(
                        "↪",
                        "Logout"
                );

        searchBooksButton.setOnAction(event ->
                openScene(
                        searchBooksButton,
                        new SearchBooksView(
                                loggedInUser
                        ).createScene(),
                        "IntelliLib - Search Books"
                )
        );

        borrowedBooksButton.setOnAction(event ->
                openScene(
                        borrowedBooksButton,
                        new MyBorrowedBooksView(
                                loggedInUser
                        ).createScene(),
                        "IntelliLib - My Borrowed Books"
                )
        );

        reservationsButton.setOnAction(event ->
                openScene(
                        reservationsButton,
                        new MyReservationsView(
                                loggedInUser
                        ).createScene(),
                        "IntelliLib - My Reservations"
                )
        );

        finesButton.setOnAction(event ->
                openScene(
                        finesButton,
                        new MyFinesView(
                                loggedInUser
                        ).createScene(),
                        "IntelliLib - My Fines"
                )
        );

        notificationsButton.setOnAction(event ->
                openScene(
                        notificationsButton,
                        new NotificationsView(
                                loggedInUser
                        ).createScene(),
                        "IntelliLib - Notifications"
                )
        );

        profileButton.setOnAction(event ->
                openScene(
                        profileButton,
                        new ProfileView(
                                loggedInUser
                        ).createScene(),
                        "IntelliLib - My Profile"
                )
        );

        logoutButton.setOnAction(event ->
                openScene(
                        logoutButton,
                        new LoginView()
                                .createScene(),
                        "IntelliLib - Login"
                )
        );

        menu.getChildren().addAll(
                menuTitle,
                searchBooksButton,
                borrowedBooksButton,
                reservationsButton,
                finesButton,
                notificationsButton,
                profileButton,
                spacer,
                logoutButton
        );

        return menu;
    }

    private VBox createDashboardContent() {

        Text dashboardTitle =
                new Text(
                        "Student Dashboard"
                );

        dashboardTitle.setStyle(
                "-fx-fill:#0f172a;" +
                "-fx-font-size:28px;" +
                "-fx-font-weight:bold;"
        );

        Label dashboardSubtitle =
                new Label(
                        "Access your books, reservations, fines and account information."
                );

        dashboardSubtitle.setStyle(
                "-fx-text-fill:#475569;" +
                "-fx-font-size:15px;"
        );

        VBox welcomeCard =
                createInformationCard(
                        "Welcome to your library portal",
                        "Search the catalogue, review your active loans, "
                                + "manage reservations and keep track of important notifications."
                );

        VBox borrowingCard =
                createInformationCard(
                        "Borrowing reminder",
                        "Please return books on or before their due dates. "
                                + "Overdue loans may temporarily restrict borrowing and reservation access."
                );

        VBox notificationCard =
                createInformationCard(
                        "Stay informed",
                        "Review the Notifications section regularly for due dates, "
                                + "reservation updates, fines and account-security messages."
                );

        HBox cardsRow =
                new HBox(
                        20,
                        borrowingCard,
                        notificationCard
                );

        HBox.setHgrow(
                borrowingCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                notificationCard,
                Priority.ALWAYS
        );

        VBox content =
                new VBox(
                        22,
                        dashboardTitle,
                        dashboardSubtitle,
                        welcomeCard,
                        cardsRow
                );

        content.setPadding(
                new Insets(
                        35,
                        40,
                        40,
                        40
                )
        );

        content.setAlignment(
                Pos.TOP_LEFT
        );

        content.setStyle(
                "-fx-background-color:#e8f1f2;"
        );

        return content;
    }

    private VBox createInformationCard(
            String heading,
            String description
    ) {

        Label headingLabel =
                new Label(
                        heading
                );

        headingLabel.setWrapText(true);

        headingLabel.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:18px;" +
                "-fx-font-weight:bold;"
        );

        Label descriptionLabel =
                new Label(
                        description
                );

        descriptionLabel.setWrapText(true);

        descriptionLabel.setStyle(
                "-fx-text-fill:#475569;" +
                "-fx-font-size:14px;" +
                "-fx-line-spacing:3px;"
        );

        VBox card =
                new VBox(
                        10,
                        headingLabel,
                        descriptionLabel
                );

        card.setMaxWidth(
                Double.MAX_VALUE
        );

        card.setMinHeight(135);

        card.setPadding(
                new Insets(24)
        );

        card.setStyle(
                "-fx-background-color:#f8fafc;" +
                "-fx-background-radius:16;" +
                "-fx-border-color:#b6d4d6;" +
                "-fx-border-radius:16;" +
                "-fx-border-width:1;" +
                "-fx-effect:dropshadow(gaussian, rgba(15,23,42,0.10), 14, 0.15, 0, 4);"
        );

        return card;
    }

    private Button createMenuButton(
            String icon,
            String text
    ) {

        Button button =
                new Button(
                        icon + "   " + text
                );

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setPrefHeight(48);

        button.setAlignment(
                Pos.CENTER_LEFT
        );

        button.setPadding(
                new Insets(
                        0,
                        18,
                        0,
                        18
                )
        );

        button.setCursor(
                Cursor.HAND
        );

        String normalStyle =
                "-fx-background-color:transparent;" +
                "-fx-text-fill:#e2e8f0;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;";

        String hoverStyle =
                "-fx-background-color:#0f766e;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;";

        button.setStyle(
                normalStyle
        );

        button.setOnMouseEntered(event ->
                button.setStyle(
                        hoverStyle
                )
        );

        button.setOnMouseExited(event ->
                button.setStyle(
                        normalStyle
                )
        );

        return button;
    }

    private Button createLogoutButton(
            String icon,
            String text
    ) {

        Button button =
                new Button(
                        icon + "   " + text
                );

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setPrefHeight(48);

        button.setAlignment(
                Pos.CENTER_LEFT
        );

        button.setPadding(
                new Insets(
                        0,
                        18,
                        0,
                        18
                )
        );

        button.setCursor(
                Cursor.HAND
        );

        String normalStyle =
                "-fx-background-color:#7f1d1d;" +
                "-fx-text-fill:#fecaca;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;";

        String hoverStyle =
                "-fx-background-color:#dc2626;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;";

        button.setStyle(
                normalStyle
        );

        button.setOnMouseEntered(event ->
                button.setStyle(
                        hoverStyle
                )
        );

        button.setOnMouseExited(event ->
                button.setStyle(
                        normalStyle
                )
        );

        return button;
    }

    private void openScene(
            Button sourceButton,
            Scene scene,
            String title
    ) {

        Stage stage =
                (Stage) sourceButton
                        .getScene()
                        .getWindow();

        SceneRouter.open(
        stage,
        scene,
        title
);
    }
}