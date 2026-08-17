package view;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.User;
import util.SceneRouter;

public class TeacherDashboardView {

    private final User loggedInUser;

    public TeacherDashboardView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public Scene createScene() {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color:#e8f1f2;"
        );

        VBox header = createHeader();
        ScrollPane sidebar = createSidebar();
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
                new Label(
                        "IntelliLib"
                );

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
                new Label(
                        "Teacher Library Portal"
                );

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
                new Label(
                        "● TEACHER"
                );

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

    private ScrollPane createSidebar() {

        VBox menu = new VBox(11);

        menu.setPrefWidth(280);

        menu.setPadding(
                new Insets(
                        25,
                        22,
                        25,
                        22
                )
        );

        menu.setStyle(
                "-fx-background-color:#111827;"
        );

        Label menuTitle =
                new Label(
                        "TEACHER MENU"
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
                        "Search Books"
                );

        Button borrowedBooksButton =
                createMenuButton(
                        "My Borrowed Books"
                );

        Button reservationsButton =
                createMenuButton(
                        "My Reservations"
                );

        Button finesButton =
                createMenuButton(
                        "My Fines"
                );

        Button notificationsButton =
                createMenuButton(
                        "Notifications"
                );

        Button profileButton =
                createMenuButton(
                        "My Profile"
                );

        Region spacer = new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        Button logoutButton =
                createLogoutButton(
                        "Logout"
                );

        searchBooksButton.setOnAction(event -> {

            Stage stage =
                    getStage(searchBooksButton);

            SceneRouter.open(
                    stage,
                    new SearchBooksView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Search Books"
            );
        });

        borrowedBooksButton.setOnAction(event -> {

            Stage stage =
                    getStage(borrowedBooksButton);

            SceneRouter.open(
                    stage,
                    new MyBorrowedBooksView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - My Borrowed Books"
            );
        });

        reservationsButton.setOnAction(event -> {

            Stage stage =
                    getStage(reservationsButton);

            SceneRouter.open(
                    stage,
                    new MyReservationsView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - My Reservations"
            );
        });

        finesButton.setOnAction(event -> {

            Stage stage =
                    getStage(finesButton);

            SceneRouter.open(
                    stage,
                    new MyFinesView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - My Fines"
            );
        });

        notificationsButton.setOnAction(event -> {

            Stage stage =
                    getStage(notificationsButton);

            SceneRouter.open(
                    stage,
                    new NotificationsView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Notifications"
            );
        });

        profileButton.setOnAction(event -> {

            Stage stage =
                    getStage(profileButton);

            SceneRouter.open(
                    stage,
                    new ProfileView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - My Profile"
            );
        });

        logoutButton.setOnAction(event -> {

            Stage stage =
                    getStage(logoutButton);

            SceneRouter.open(
                    stage,
                    new LoginView().createScene(),
                    "IntelliLib - Login"
            );
        });

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

        ScrollPane scrollPane =
                new ScrollPane(menu);

        scrollPane.setPrefWidth(280);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setStyle(
                "-fx-background:#111827;" +
                "-fx-background-color:#111827;" +
                "-fx-border-color:transparent #1e293b transparent transparent;" +
                "-fx-border-width:0 1 0 0;"
        );

        return scrollPane;
    }

    private VBox createDashboardContent() {

        Label dashboardTitle =
                new Label(
                        "Teacher Dashboard"
                );

        dashboardTitle.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:28px;" +
                "-fx-font-weight:bold;"
        );

        Label dashboardSubtitle =
                new Label(
                        "Search the library catalogue, borrow multiple copies and manage your account."
                );

        dashboardSubtitle.setWrapText(true);

        dashboardSubtitle.setStyle(
                "-fx-text-fill:#475569;" +
                "-fx-font-size:15px;"
        );

        VBox searchCard =
                createActionCard(
                        "Search Library Catalogue",
                        "Browse books by title, author, ISBN or category and borrow available copies.",
                        "Open Catalogue",
                        button -> {

                            Stage stage =
                                    getStage(button);

                            SceneRouter.open(
                                    stage,
                                    new SearchBooksView(
                                            loggedInUser
                                    ).createScene(),
                                    "IntelliLib - Search Books"
                            );
                        }
                );

        VBox borrowedCard =
                createActionCard(
                        "My Borrowed Books",
                        "Review your current loans, due dates and renewal availability.",
                        "View Borrowed Books",
                        button -> {

                            Stage stage =
                                    getStage(button);

                            SceneRouter.open(
                                    stage,
                                    new MyBorrowedBooksView(
                                            loggedInUser
                                    ).createScene(),
                                    "IntelliLib - My Borrowed Books"
                            );
                        }
                );

        VBox reservationsCard =
                createActionCard(
                        "Reservations",
                        "Track reserved books, queue positions and pickup availability.",
                        "View Reservations",
                        button -> {

                            Stage stage =
                                    getStage(button);

                            SceneRouter.open(
                                    stage,
                                    new MyReservationsView(
                                            loggedInUser
                                    ).createScene(),
                                    "IntelliLib - My Reservations"
                            );
                        }
                );

        VBox accountCard =
                createActionCard(
                        "Account and Profile",
                        "Review fines, notifications and your registered account information.",
                        "Open Profile",
                        button -> {

                            Stage stage =
                                    getStage(button);

                            SceneRouter.open(
                                    stage,
                                    new ProfileView(
                                            loggedInUser
                                    ).createScene(),
                                    "IntelliLib - My Profile"
                            );
                        }
                );

        FlowPane cards =
                new FlowPane();

        cards.setHgap(20);
        cards.setVgap(20);
        cards.setPrefWrapLength(760);

        cards.getChildren().addAll(
                searchCard,
                borrowedCard,
                reservationsCard,
                accountCard
        );

        VBox content =
                new VBox(
                        22,
                        dashboardTitle,
                        dashboardSubtitle,
                        cards
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

    private VBox createActionCard(
            String heading,
            String description,
            String buttonText,
            java.util.function.Consumer<Button> action
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

        Region spacer = new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        Button actionButton =
                createCardButton(
                        buttonText
                );

        actionButton.setOnAction(event ->
                action.accept(actionButton)
        );

        VBox card =
                new VBox(
                        12,
                        headingLabel,
                        descriptionLabel,
                        spacer,
                        actionButton
                );

        card.setPrefWidth(350);
        card.setMinHeight(210);
        card.setPadding(
                new Insets(24)
        );

        card.setStyle(
                "-fx-background-color:#ffffff;" +
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

        button.setCursor(
                Cursor.HAND
        );

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

        applyButtonHover(
                button,
                normalStyle,
                hoverStyle
        );

        return button;
    }

    private Button createCardButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefWidth(180);
        button.setPrefHeight(44);
        button.setCursor(
                Cursor.HAND
        );

        String normalStyle =
                "-fx-background-color:#0f766e;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;";

        String hoverStyle =
                "-fx-background-color:#115e59;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;";

        applyButtonHover(
                button,
                normalStyle,
                hoverStyle
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

        applyButtonHover(
                button,
                normalStyle,
                hoverStyle
        );

        return button;
    }

    private void applyButtonHover(
            Button button,
            String normalStyle,
            String hoverStyle
    ) {

        button.setStyle(normalStyle);

        button.setOnMouseEntered(event ->
                button.setStyle(hoverStyle)
        );

        button.setOnMouseExited(event ->
                button.setStyle(normalStyle)
        );
    }

    private void playEntranceAnimation(
            VBox header,
            ScrollPane sidebar,
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