package view;

import controller.ActivityLogController;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
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
import javafx.stage.Stage;
import javafx.util.Duration;
import model.User;
import util.SceneRouter;

public class LibrarianDashboardView {

    private final User loggedInUser;

    public LibrarianDashboardView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public Scene createScene() {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color:#e8f1f2;"
        );

        VBox header = createHeader();
        VBox sidebar = createSideMenu();
        VBox content = createDashboardContent();

        root.setTop(header);
        root.setLeft(sidebar);
        root.setCenter(content);

        playEntranceAnimation(
                header,
                sidebar,
                content
        );

        return new Scene(
                root,
                1200,
                760
        );
    }

    private VBox createHeader() {

        Label applicationTitle =
                new Label("IntelliLib");

        applicationTitle.setStyle(
                "-fx-text-fill:#0f172a;" +
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

        Label portalLabel =
                new Label("Librarian Operations Portal");

        portalLabel.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:14px;"
        );

        VBox userInformation =
                new VBox(
                        5,
                        welcomeLabel,
                        portalLabel
                );

        Region spacer = new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label roleBadge =
                new Label("● LIBRARIAN");

        roleBadge.setStyle(
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
                        roleBadge
                );

        headerRow.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox header =
                new VBox(
                        12,
                        applicationTitle,
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

        VBox sidebar = new VBox(12);

        sidebar.setPrefWidth(285);

        sidebar.setPadding(
                new Insets(
                        25,
                        22,
                        25,
                        22
                )
        );

        sidebar.setStyle(
                "-fx-background-color:#111827;" +
                "-fx-border-color:transparent #1e293b transparent transparent;" +
                "-fx-border-width:0 1 0 0;"
        );

        Label menuTitle =
                new Label("LIBRARIAN MENU");

        menuTitle.setMaxWidth(
                Double.MAX_VALUE
        );

        menuTitle.setStyle(
                "-fx-text-fill:#94a3b8;" +
                "-fx-font-size:12px;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:0 0 8 5;"
        );

        Button inventoryButton =
                createMenuButton(
                        "Manage Inventory"
                );

        Button issueBookButton =
                createMenuButton(
                        "Issue Book"
                );

        Button returnBookButton =
                createMenuButton(
                        "Return Book"
                );

        Button readyForPickupButton =
                createMenuButton(
                        "Ready for Pickup"
                );

        Button borrowRecordsButton =
                createMenuButton(
                        "Borrow Records"
                );

        Button finesButton =
                createMenuButton(
                        "Fine Management"
                );

        Button activityLogsButton =
                createMenuButton(
                        "Activity Logs"
                );

        Region spacer = new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        Button logoutButton =
                createLogoutButton("Logout");

        inventoryButton.setOnAction(event -> {

            Stage stage =
                    getStage(inventoryButton);

            SceneRouter.open(
                    stage,
                    new InventoryView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Inventory Management"
            );
        });

        issueBookButton.setOnAction(event -> {

            Stage stage =
                    getStage(issueBookButton);

            SceneRouter.open(
                    stage,
                    new IssueBookView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Issue Book"
            );
        });

        returnBookButton.setOnAction(event -> {

            Stage stage =
                    getStage(returnBookButton);

            SceneRouter.open(
                    stage,
                    new ReturnBookView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Return Book"
            );
        });

        readyForPickupButton.setOnAction(event -> {

            Stage stage =
                    getStage(readyForPickupButton);

            SceneRouter.open(
                    stage,
                    new ReadyForPickupView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Ready for Pickup"
            );
        });

        borrowRecordsButton.setOnAction(event -> {

            Stage stage =
                    getStage(borrowRecordsButton);

            SceneRouter.open(
                    stage,
                    new AdminBorrowRecordsView(
                            loggedInUser,
                            "ALL"
                    ).createScene(),
                    "IntelliLib - Borrow Records"
            );
        });

        finesButton.setOnAction(event -> {

            Stage stage =
                    getStage(finesButton);

            SceneRouter.open(
                    stage,
                    new FineManagementView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Fine Management"
            );
        });

        activityLogsButton.setOnAction(event -> {

            Stage stage =
                    getStage(activityLogsButton);

            SceneRouter.open(
                    stage,
                    new ActivityLogView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Activity Logs"
            );
        });

        logoutButton.setOnAction(event -> {

            ActivityLogController activityLogController =
                    new ActivityLogController();

            activityLogController.logActivity(
                    loggedInUser.getUserId(),
                    "LOGOUT",
                    "LIBRARIAN logged out of the system."
            );

            Stage stage =
                    getStage(logoutButton);

            SceneRouter.open(
                    stage,
                    new LoginView().createScene(),
                    "IntelliLib - Login"
            );
        });

        sidebar.getChildren().addAll(
                menuTitle,
                inventoryButton,
                issueBookButton,
                returnBookButton,
                readyForPickupButton,
                borrowRecordsButton,
                finesButton,
                activityLogsButton,
                spacer,
                logoutButton
        );

        return sidebar;
    }

    private VBox createDashboardContent() {

        Label dashboardTitle =
                new Label("Librarian Dashboard");

        dashboardTitle.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:28px;" +
                "-fx-font-weight:bold;"
        );

        Label dashboardSubtitle =
                new Label(
                        "Manage daily circulation, reservations, inventory and member transactions."
                );

        dashboardSubtitle.setWrapText(true);

        dashboardSubtitle.setStyle(
                "-fx-text-fill:#475569;" +
                "-fx-font-size:15px;"
        );

        VBox circulationCard =
                createInformationCard(
                        "Circulation Management",
                        "Issue books to active members, process returns, "
                                + "and review current borrowing records."
                );

        VBox reservationCard =
                createInformationCard(
                        "Reservation Pickup",
                        "Review reservations that are ready for collection "
                                + "and issue the reserved copy to the correct member."
                );

        VBox inventoryCard =
                createInformationCard(
                        "Inventory and Fines",
                        "Maintain book records, monitor physical copies, "
                                + "and review outstanding member fines."
                );

        HBox lowerCards =
                new HBox(
                        20,
                        reservationCard,
                        inventoryCard
                );

        HBox.setHgrow(
                reservationCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                inventoryCard,
                Priority.ALWAYS
        );

        VBox content =
                new VBox(
                        22,
                        dashboardTitle,
                        dashboardSubtitle,
                        circulationCard,
                        lowerCards
                );

        content.setPadding(
                new Insets(
                        35,
                        40,
                        40,
                        40
                )
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
                new Label(heading);

        headingLabel.setWrapText(true);

        headingLabel.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:18px;" +
                "-fx-font-weight:bold;"
        );

        Label descriptionLabel =
                new Label(description);

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

        card.setMinHeight(140);

        card.setPadding(
                new Insets(24)
        );

        card.setStyle(
                "-fx-background-color:#f8fafc;" +
                "-fx-background-radius:16;" +
                "-fx-border-color:#b6d4d6;" +
                "-fx-border-radius:16;" +
                "-fx-border-width:1;" +
                "-fx-effect:dropshadow(" +
                "gaussian," +
                "rgba(15,23,42,0.10)," +
                "14," +
                "0.15," +
                "0," +
                "4" +
                ");"
        );

        return card;
    }

    private Button createMenuButton(
            String text
    ) {

        Button button =
                new Button(text);

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

        button.setCursor(Cursor.HAND);

        String normalStyle =
                "-fx-background-color:transparent;" +
                "-fx-text-fill:#e2e8f0;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;";

        String hoverStyle =
                "-fx-background-color:#0f766e;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;";

        button.setStyle(normalStyle);

        button.setOnMouseEntered(event ->
                button.setStyle(hoverStyle)
        );

        button.setOnMouseExited(event ->
                button.setStyle(normalStyle)
        );

        return button;
    }

    private Button createLogoutButton(
            String text
    ) {

        Button button =
                new Button(text);

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

        button.setCursor(Cursor.HAND);

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

        button.setStyle(normalStyle);

        button.setOnMouseEntered(event ->
                button.setStyle(hoverStyle)
        );

        button.setOnMouseExited(event ->
                button.setStyle(normalStyle)
        );

        return button;
    }

    private void playEntranceAnimation(
            VBox header,
            VBox sidebar,
            VBox content
    ) {

        header.setOpacity(0);
        header.setTranslateY(-15);

        sidebar.setOpacity(0);
        sidebar.setTranslateX(-25);

        content.setOpacity(0);
        content.setTranslateY(24);

        FadeTransition headerFade =
                new FadeTransition(
                        Duration.millis(450),
                        header
                );

        headerFade.setFromValue(0);
        headerFade.setToValue(1);

        TranslateTransition headerSlide =
                new TranslateTransition(
                        Duration.millis(450),
                        header
                );

        headerSlide.setFromY(-15);
        headerSlide.setToY(0);
        headerSlide.setInterpolator(
                Interpolator.EASE_OUT
        );

        FadeTransition sidebarFade =
                new FadeTransition(
                        Duration.millis(550),
                        sidebar
                );

        sidebarFade.setFromValue(0);
        sidebarFade.setToValue(1);
        sidebarFade.setDelay(
                Duration.millis(70)
        );

        TranslateTransition sidebarSlide =
                new TranslateTransition(
                        Duration.millis(550),
                        sidebar
                );

        sidebarSlide.setFromX(-25);
        sidebarSlide.setToX(0);
        sidebarSlide.setDelay(
                Duration.millis(70)
        );

        sidebarSlide.setInterpolator(
                Interpolator.EASE_OUT
        );

        FadeTransition contentFade =
                new FadeTransition(
                        Duration.millis(600),
                        content
                );

        contentFade.setFromValue(0);
        contentFade.setToValue(1);
        contentFade.setDelay(
                Duration.millis(120)
        );

        TranslateTransition contentSlide =
                new TranslateTransition(
                        Duration.millis(600),
                        content
                );

        contentSlide.setFromY(24);
        contentSlide.setToY(0);
        contentSlide.setDelay(
                Duration.millis(120)
        );

        contentSlide.setInterpolator(
                Interpolator.EASE_OUT
        );

        headerFade.play();
        headerSlide.play();
        sidebarFade.play();
        sidebarSlide.play();
        contentFade.play();
        contentSlide.play();
    }

    private Stage getStage(
            javafx.scene.Node node
    ) {

        return (Stage) node
                .getScene()
                .getWindow();
    }
}