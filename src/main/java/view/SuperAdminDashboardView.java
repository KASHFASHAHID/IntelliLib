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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.User;
import util.SceneRouter;

public class SuperAdminDashboardView {

    private final User loggedInUser;

    public SuperAdminDashboardView(
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

        VBox header =
                createHeader();

        VBox sidebar =
                createSideMenu();

        VBox content =
                createContent();

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

        Label portalLabel =
                new Label(
                        "Super Administrator Portal"
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

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label roleBadge =
                new Label(
                        "● SUPER ADMIN"
                );

        roleBadge.setStyle(
                "-fx-background-color:#fef3c7;" +
                "-fx-text-fill:#92400e;" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:9 16;" +
                "-fx-background-radius:18;" +
                "-fx-border-color:#fcd34d;" +
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

        VBox sidebar =
                new VBox(12);

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
                new Label(
                        "SUPER ADMIN MENU"
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

        Button staffManagementButton =
                createMenuButton(
                        "Manage Staff Accounts"
                );

        Button administrationButton =
                createMenuButton(
                        "Library Administration"
                );

        Button activityLogsButton =
                createMenuButton(
                        "Activity Logs"
                );

        Region spacer =
                new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        Button logoutButton =
                createLogoutButton(
                        "Logout"
                );

        staffManagementButton.setOnAction(event -> {

            Stage stage =
                    getStage(
                            staffManagementButton
                    );

            SceneRouter.open(
                    stage,
                    new StaffAccountManagementView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Staff Account Management"
            );
        });

        administrationButton.setOnAction(event -> {

            Stage stage =
                    getStage(
                            administrationButton
                    );

            SceneRouter.open(
                    stage,
                    new AdminDashboardView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Library Administration"
            );
        });

        activityLogsButton.setOnAction(event -> {

            Stage stage =
                    getStage(
                            activityLogsButton
                    );

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
                    "SUPER_ADMIN logged out of the system."
            );

            Stage stage =
                    getStage(
                            logoutButton
                    );

            SceneRouter.open(
                    stage,
                    new LoginView()
                            .createScene(),
                    "IntelliLib - Login"
            );
        });

        sidebar.getChildren().addAll(
                menuTitle,
                staffManagementButton,
                administrationButton,
                activityLogsButton,
                spacer,
                logoutButton
        );

        return sidebar;
    }

    private VBox createContent() {

        Text heading =
                new Text(
                        "Super Admin Control Center"
                );

        heading.setStyle(
                "-fx-fill:#0f172a;" +
                "-fx-font-size:28px;" +
                "-fx-font-weight:bold;"
        );

        Label description =
                new Label(
                        "Manage administrative accounts, access library operations, "
                                + "and review important system activity."
                );

        description.setWrapText(true);

        description.setStyle(
                "-fx-text-fill:#475569;" +
                "-fx-font-size:15px;"
        );

        VBox staffCard =
                createActionCard(
                        "Staff Account Management",
                        "Create Admin and Librarian accounts, review account status, "
                                + "and suspend, block or reactivate staff access.",
                        "Open Staff Management"
                );

        VBox administrationCard =
                createActionCard(
                        "Library Administration",
                        "Access inventory, circulation, reservations, fines, reports, "
                                + "settings and membership management.",
                        "Open Administration"
                );

        VBox auditCard =
                createActionCard(
                        "System Activity Logs",
                        "Review sign-ins, account changes and important actions performed "
                                + "by administrators and librarians.",
                        "Open Activity Logs"
                );

        staffCard.setOnMouseClicked(event -> {

            Stage stage =
                    getStage(
                            staffCard
                    );

            SceneRouter.open(
                    stage,
                    new StaffAccountManagementView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Staff Account Management"
            );
        });

        administrationCard.setOnMouseClicked(event -> {

            Stage stage =
                    getStage(
                            administrationCard
                    );

            SceneRouter.open(
                    stage,
                    new AdminDashboardView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Library Administration"
            );
        });

        auditCard.setOnMouseClicked(event -> {

            Stage stage =
                    getStage(
                            auditCard
                    );

            SceneRouter.open(
                    stage,
                    new ActivityLogView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Activity Logs"
            );
        });

        VBox content =
                new VBox(
                        22,
                        heading,
                        description,
                        staffCard,
                        administrationCard,
                        auditCard
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
            String actionText
    ) {

        Label headingLabel =
                new Label(
                        heading
                );

        headingLabel.setWrapText(true);

        headingLabel.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:19px;" +
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

        Label actionLabel =
                new Label(
                        actionText + "  →"
                );

        actionLabel.setStyle(
                "-fx-text-fill:#0f766e;" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;"
        );

        VBox card =
                new VBox(
                        10,
                        headingLabel,
                        descriptionLabel,
                        actionLabel
                );

        card.setPadding(
                new Insets(24)
        );

        card.setMaxWidth(
                820
        );

        card.setMinHeight(
                135
        );

        card.setCursor(
                Cursor.HAND
        );

        String normalStyle =
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
                ");";

        String hoverStyle =
                "-fx-background-color:#ecfeff;" +
                "-fx-background-radius:16;" +
                "-fx-border-color:#0f766e;" +
                "-fx-border-radius:16;" +
                "-fx-border-width:2;" +
                "-fx-effect:dropshadow(" +
                "gaussian," +
                "rgba(15,118,110,0.18)," +
                "18," +
                "0.18," +
                "0," +
                "5" +
                ");";

        card.setStyle(
                normalStyle
        );

        card.setOnMouseEntered(event ->
                card.setStyle(
                        hoverStyle
                )
        );

        card.setOnMouseExited(event ->
                card.setStyle(
                        normalStyle
                )
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

    private void playEntranceAnimation(
            VBox header,
            VBox sidebar,
            VBox content
    ) {

        header.setOpacity(0);
        header.setTranslateY(-18);

        sidebar.setOpacity(0);
        sidebar.setTranslateX(-25);

        content.setOpacity(0);
        content.setTranslateY(25);

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

        headerSlide.setFromY(-18);
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
                Duration.millis(80)
        );

        TranslateTransition sidebarSlide =
                new TranslateTransition(
                        Duration.millis(550),
                        sidebar
                );

        sidebarSlide.setFromX(-25);
        sidebarSlide.setToX(0);
        sidebarSlide.setDelay(
                Duration.millis(80)
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
                Duration.millis(140)
        );

        TranslateTransition contentSlide =
                new TranslateTransition(
                        Duration.millis(600),
                        content
                );

        contentSlide.setFromY(25);
        contentSlide.setToY(0);
        contentSlide.setDelay(
                Duration.millis(140)
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