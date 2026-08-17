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

public class SuspendedMemberDashboardView {

    private final User loggedInUser;

    public SuspendedMemberDashboardView(
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
                    loggedInUser.isSuspended()
                            ? "Suspended Member Portal"
                            : "Limited Member Portal"
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

    Label statusLabel =
            new Label(
                    loggedInUser.isSuspended()
                            ? "● SUSPENDED"
                            : "● LIMITED ACCESS"
            );

    if (loggedInUser.isSuspended()) {

        statusLabel.setStyle(
                "-fx-background-color:#fee2e2;" +
                "-fx-text-fill:#b91c1c;" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:9 16;" +
                "-fx-background-radius:18;" +
                "-fx-border-color:#fca5a5;" +
                "-fx-border-radius:18;"
        );

    } else {

        statusLabel.setStyle(
                "-fx-background-color:#fef3c7;" +
                "-fx-text-fill:#92400e;" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:9 16;" +
                "-fx-background-radius:18;" +
                "-fx-border-color:#fcd34d;" +
                "-fx-border-radius:18;"
        );
    }

    HBox headerRow =
            new HBox(
                    20,
                    userInformation,
                    spacer,
                    statusLabel
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
                    "MEMBER MENU"
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

    Button borrowedBooksButton =
            createMenuButton(
                    "📚",
                    "My Borrowed Books"
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

    

    borrowedBooksButton.setOnAction(event ->
            openScene(
                    borrowedBooksButton,
                    new MyBorrowedBooksView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - My Borrowed Books"
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
            borrowedBooksButton,
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
                    "Limited Account Access"
            );

    dashboardTitle.setStyle(
            "-fx-fill:#0f172a;" +
            "-fx-font-size:28px;" +
            "-fx-font-weight:bold;"
    );

    Label dashboardSubtitle =
            new Label(
                    getExplanationText()
            );

    dashboardSubtitle.setWrapText(true);

    dashboardSubtitle.setStyle(
            "-fx-text-fill:#475569;" +
            "-fx-font-size:15px;"
    );

    VBox warningCard =
            createWarningCard(
                    "⚠ Account Restrictions",
                    "Borrowing, reserving and renewing books are currently disabled.\n\n"
                            + "You can still return books, review fines, read notifications "
                            + "and manage your profile."
            );

    VBox returnCard =
            createInformationCard(
                    "📚 Return Books",
                    "Return every overdue book as soon as possible to restore full access."
            );

    VBox fineCard =
            createInformationCard(
                    "₹ Views Fines",
                    "Review your pending fines and clear them to reactivate your account."
            );

    VBox notificationCard =
            createInformationCard(
                    "🔔 Notifications",
                    "Stay updated with reservation alerts, due dates and important announcements."
            );

    HBox cardsRow =
            new HBox(
                    20,
                    returnCard,
                    fineCard,
                    notificationCard
            );

            

    HBox.setHgrow(
            returnCard,
            Priority.ALWAYS
    );

    HBox.setHgrow(
            fineCard,
            Priority.ALWAYS
    );

    HBox.setHgrow(
            notificationCard,
            Priority.ALWAYS
    );

    returnCard.setOnMouseClicked(event -> {

    Stage stage =
            (Stage) returnCard.getScene().getWindow();

    SceneRouter.open(
            stage,
            new MyBorrowedBooksView(loggedInUser).createScene(),
            "IntelliLib - My Borrowed Books"
    );
});

fineCard.setOnMouseClicked(event -> {

    Stage stage =
            (Stage) fineCard.getScene().getWindow();

    SceneRouter.open(
            stage,
            new MyFinesView(loggedInUser).createScene(),
            "IntelliLib - My Fines"
    );
});

notificationCard.setOnMouseClicked(event -> {

    Stage stage =
            (Stage) notificationCard.getScene().getWindow();

    SceneRouter.open(
            stage,
            new NotificationsView(loggedInUser).createScene(),
            "IntelliLib - Notifications"
    );
});

    VBox content =
            new VBox(
                    22,
                    dashboardTitle,
                    dashboardSubtitle,
                    warningCard,
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

    content.setOpacity(0);
    content.setTranslateY(30);

    javafx.animation.FadeTransition fade =
            new javafx.animation.FadeTransition(
                    javafx.util.Duration.millis(500),
                    content
            );

    fade.setFromValue(0);
    fade.setToValue(1);

    javafx.animation.TranslateTransition slide =
            new javafx.animation.TranslateTransition(
                    javafx.util.Duration.millis(500),
                    content
            );

    slide.setFromY(30);
    slide.setToY(0);

    fade.play();
    slide.play();

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
                    12,
                    headingLabel,
                    descriptionLabel
            );

    card.setMaxWidth(
            Double.MAX_VALUE
    );

    card.setMinHeight(150);

    card.setPadding(
            new Insets(24)
    );

    card.setCursor(Cursor.HAND);

    card.setStyle(
            "-fx-background-color:#ffffff;" +
            "-fx-background-radius:16;" +
            "-fx-border-color:#b6d4d6;" +
            "-fx-border-radius:16;" +
            "-fx-border-width:1;" +
            "-fx-effect:dropshadow(gaussian, rgba(15,23,42,0.10), 14,0.15,0,4);"
    );

    card.setOnMouseEntered(e -> {

        card.setScaleX(1.03);
        card.setScaleY(1.03);

        card.setStyle(
                "-fx-background-color:white;" +
                "-fx-background-radius:16;" +
                "-fx-border-color:#0f766e;" +
                "-fx-border-radius:16;" +
                "-fx-border-width:1.5;" +
                "-fx-effect:dropshadow(gaussian, rgba(15,118,110,0.28),20,0.20,0,6);"
        );
    });

    card.setOnMouseExited(e -> {

        card.setScaleX(1);
        card.setScaleY(1);

        card.setStyle(
                "-fx-background-color:#ffffff;" +
                "-fx-background-radius:16;" +
                "-fx-border-color:#b6d4d6;" +
                "-fx-border-radius:16;" +
                "-fx-border-width:1;" +
                "-fx-effect:dropshadow(gaussian, rgba(15,23,42,0.10),14,0.15,0,4);"
        );
    });

    return card;
}
private VBox createWarningCard(
        String heading,
        String description
) {

    Label title =
            new Label(
                    heading
            );

    title.setStyle(
            "-fx-text-fill:#92400e;" +
            "-fx-font-size:19px;" +
            "-fx-font-weight:bold;"
    );

    Label body =
            new Label(
                    description
            );

    body.setWrapText(true);

    body.setStyle(
            "-fx-text-fill:#78350f;" +
            "-fx-font-size:14px;" +
            "-fx-line-spacing:4px;"
    );

    VBox card =
            new VBox(
                    14,
                    title,
                    body
            );

    card.setPadding(
            new Insets(24)
    );

    card.setStyle(
            "-fx-background-color:#fef3c7;" +
            "-fx-background-radius:18;" +
            "-fx-border-color:#fbbf24;" +
            "-fx-border-width:2;" +
            "-fx-border-radius:18;" +
            "-fx-effect:dropshadow(gaussian, rgba(251,191,36,0.25),20,0.20,0,5);"
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

    button.setOnMouseEntered(event -> {

        button.setStyle(
                hoverStyle
        );

        button.setScaleX(1.03);
        button.setScaleY(1.03);
    });

    button.setOnMouseExited(event -> {

        button.setStyle(
                normalStyle
        );

        button.setScaleX(1);
        button.setScaleY(1);
    });

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

    button.setOnMouseEntered(event -> {

        button.setStyle(
                hoverStyle
        );

        button.setScaleX(1.03);
        button.setScaleY(1.03);
    });

    button.setOnMouseExited(event -> {

        button.setStyle(
                normalStyle
        );

        button.setScaleX(1);
        button.setScaleY(1);
    });

    return button;
}

private String getExplanationText() {

    if (loggedInUser.isSuspended()) {

        return "Your library account has been suspended. "
                + "Return all overdue books and clear outstanding fines "
                + "before requesting account reactivation.";
    }

    return "Your account currently has limited access because one or more "
            + "borrowed books are overdue. Return the overdue books and "
            + "clear any related fines to restore your full borrowing privileges.";
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

    