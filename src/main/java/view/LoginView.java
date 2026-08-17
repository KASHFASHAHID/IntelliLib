package view;

import controller.LoginController;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Role;
import model.User;
import util.SceneRouter;
import util.RememberMeManager;

public class LoginView {

    private final LoginController loginController;

    public LoginView() {
        loginController = new LoginController();
    }

    public Scene createScene() {

        StackPane root = new StackPane();
        root.getStyleClass().add("login-root");

        HBox mainLayout = new HBox();
        mainLayout.setMaxWidth(1050);
        mainLayout.setMaxHeight(650);
        mainLayout.getStyleClass().add("login-container");

        VBox brandingPanel = createBrandingPanel();

        VBox formPanel = new VBox();
        formPanel.setAlignment(Pos.CENTER);
        formPanel.setPadding(new Insets(45, 55, 45, 55));
        formPanel.getStyleClass().add("form-panel");

        HBox.setHgrow(formPanel, Priority.ALWAYS);

        VBox formContent = new VBox(18);
        formContent.setMaxWidth(410);
        formContent.setAlignment(Pos.CENTER_LEFT);

        Label signInTitle = new Label("Welcome back");
        signInTitle.getStyleClass().add("form-title");

        Label signInSubtitle =
                new Label("Sign in to continue to your library account.");

        signInSubtitle.setWrapText(true);
        signInSubtitle.getStyleClass().add("form-subtitle");

        Label userIdLabel = new Label("User ID");
        userIdLabel.getStyleClass().add("field-label");

        TextField userIdField = new TextField();
        userIdField.setPromptText("Enter your User ID");
        userIdField.setPrefHeight(52);
        userIdField.getStyleClass().add("login-input");

        Label passwordLabel = new Label("Password");
        passwordLabel.getStyleClass().add("field-label");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefHeight(52);
        passwordField.getStyleClass().add("login-input");

        CheckBox rememberMe = new CheckBox("Remember me");
rememberMe.getStyleClass().add("remember-check");

if (RememberMeManager.isRemembered()) {

    userIdField.setText(
            RememberMeManager.getRememberedUserId()
    );

    rememberMe.setSelected(true);

} else {

    rememberMe.setSelected(false);
}

        Hyperlink accountAccess =
                new Hyperlink("Account access");

        accountAccess.getStyleClass().add("login-link");

        HBox accountOptions =
                new HBox(
                        15,
                        rememberMe,
                        createHorizontalSpacer(),
                        accountAccess
                );

        accountOptions.setAlignment(Pos.CENTER_LEFT);

        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setMinHeight(22);
        messageLabel.getStyleClass().add("message-label");

        Button loginButton = new Button("Sign in  →");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(54);
        loginButton.setCursor(Cursor.HAND);
        loginButton.getStyleClass().add("login-button");

        Label membershipQuestion =
                new Label("Not a library member yet?");

        membershipQuestion.getStyleClass().add("membership-text");

        Hyperlink requestMembership =
                new Hyperlink("Request membership");

        requestMembership.getStyleClass().add("login-link");

        HBox membershipBox =
                new HBox(
                        5,
                        membershipQuestion,
                        requestMembership
                );

        membershipBox.setAlignment(Pos.CENTER);

        formContent.getChildren().addAll(
                signInTitle,
                signInSubtitle,
                createVerticalGap(4),
                userIdLabel,
                userIdField,
                passwordLabel,
                passwordField,
                accountOptions,
                messageLabel,
                loginButton,
                createVerticalGap(5),
                membershipBox
        );

        formPanel.getChildren().add(formContent);

        mainLayout.getChildren().addAll(
                brandingPanel,
                formPanel
        );

        root.getChildren().add(mainLayout);

        accountAccess.setOnAction(event -> {

            Stage stage =
                    (Stage) accountAccess
                            .getScene()
                            .getWindow();

            SceneRouter.open(
                    stage,
                    new AccountAccessView().createScene(),
                    "IntelliLib - Account Access"
            );
        });

        requestMembership.setOnAction(event -> {

            Stage stage =
                    (Stage) requestMembership
                            .getScene()
                            .getWindow();

            SceneRouter.open(
                    stage,
                    new RequestMembershipView().createScene(),
                    "IntelliLib - Membership Request"
            );
        });

        loginButton.setOnAction(event ->
        handleLogin(
                userIdField,
                passwordField,
                rememberMe,
                messageLabel,
                loginButton,
                formContent
        )
);

        passwordField.setOnAction(event ->
                loginButton.fire()
        );

        addButtonHoverAnimation(loginButton);

        Scene scene =
                new Scene(
                        root,
                        1200,
                        760
                );

        var stylesheet =
                getClass().getResource(
                        "/css/login.css"
                );

        if (stylesheet != null) {

            scene.getStylesheets().add(
                    stylesheet.toExternalForm()
            );

        } else {

            System.err.println(
                    "Login stylesheet could not be found."
            );
        }

        playEntranceAnimation(
                brandingPanel,
                formPanel
        );

        return scene;
    }

    private VBox createBrandingPanel() {

        VBox panel = new VBox(22);

        panel.setAlignment(Pos.CENTER_LEFT);
        panel.setPadding(new Insets(55));
        panel.setPrefWidth(470);
        panel.getStyleClass().add("branding-panel");

        StackPane logoBox = new StackPane();
        logoBox.setPrefSize(82, 82);
        logoBox.setMinSize(82, 82);
        logoBox.setMaxSize(82, 82);
        logoBox.getStyleClass().add("brand-logo");

        Text logoText = new Text("▤");
        logoText.getStyleClass().add("brand-logo-text");

        logoBox.getChildren().add(logoText);

        Label applicationName =
                new Label("IntelliLib");

        applicationName.setWrapText(true);
        applicationName.getStyleClass().add("brand-title");

        Label tagline =
                new Label(
                        "A smarter way to discover, borrow and manage library resources."
                );

        tagline.setWrapText(true);
        tagline.getStyleClass().add("brand-subtitle");

        VBox featureOne =
                createFeature(
                        "✓",
                        "Search and borrow books easily"
                );

        VBox featureTwo =
                createFeature(
                        "✓",
                        "Track reservations and due dates"
                );

        VBox featureThree =
                createFeature(
                        "✓",
                        "Manage your library account securely"
                );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label secureLabel =
                new Label(
                        "Secure access • Real-time library information"
                );

        secureLabel.getStyleClass().add("security-text");

        panel.getChildren().addAll(
                logoBox,
                applicationName,
                tagline,
                createVerticalGap(8),
                featureOne,
                featureTwo,
                featureThree,
                spacer,
                secureLabel
        );

        playLogoFloatingAnimation(logoBox);

        return panel;
    }

    private VBox createFeature(
            String icon,
            String text
    ) {

        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("feature-icon");

        Label textLabel = new Label(text);
        textLabel.setWrapText(true);
        textLabel.getStyleClass().add("feature-text");

        HBox row =
                new HBox(
                        12,
                        iconLabel,
                        textLabel
                );

        row.setAlignment(Pos.CENTER_LEFT);

        return new VBox(row);
    }

    private void handleLogin(
        TextField userIdField,
        PasswordField passwordField,
        CheckBox rememberMe,
        Label messageLabel,
        Button loginButton,
        VBox formContent
) {

        messageLabel.setText("");

        String userId =
                userIdField.getText();

        String password =
                passwordField.getText();

        if (userId == null
                || userId.isBlank()
                || password == null
                || password.isBlank()) {

            showError(
                    messageLabel,
                    "Please enter your User ID and password."
            );

            playShakeAnimation(formContent);
            return;
        }

        loginButton.setDisable(true);
        loginButton.setText("Signing in...");

        User user;

try {

    user =
            loginController.handleLogin(
                    userId.trim(),
                    password
            );

} catch (Exception exception) {

    loginButton.setDisable(false);
    loginButton.setText("Sign in  →");

    showError(
            messageLabel,
            "Login could not be completed. Please try again."
    );

    playShakeAnimation(formContent);

    System.err.println(
            "Login failed: "
                    + exception.getMessage()
    );

    return;
}

loginButton.setDisable(false);
loginButton.setText("Sign in  →");

if (user == null) {

    passwordField.clear();

    showError(
            messageLabel,
            "Incorrect User ID or password."
    );

    playShakeAnimation(formContent);
    passwordField.requestFocus();

    return;
}

if (rememberMe.isSelected()) {

    RememberMeManager.save(
            userId.trim()
    );

} else {

    RememberMeManager.clear();
}

        Stage stage =
                (Stage) loginButton
                        .getScene()
                        .getWindow();

        boolean isMember =
                user.getRole() == Role.STUDENT
                        || user.getRole() == Role.TEACHER;

        boolean needsLimitedAccess =
                user.isSuspended()
                        || (
                        isMember
                                && loginController
                                .hasOverdueActiveLoans(
                                        user.getUserId()
                                )
                );

        if (isMember && needsLimitedAccess) {

            SceneRouter.open(
                    stage,
                    new SuspendedMemberDashboardView(
                            user
                    ).createScene(),
                    "IntelliLib - Limited Account Access"
            );

            return;
        }

        if (user.getRole() == Role.SUPER_ADMIN) {

            SceneRouter.open(
                    stage,
                    new SuperAdminDashboardView(
                            user
                    ).createScene(),
                    "IntelliLib - Super Admin Dashboard"
            );

        } else if (user.getRole() == Role.ADMIN) {

            SceneRouter.open(
                    stage,
                    new AdminDashboardView(
                            user
                    ).createScene(),
                    "IntelliLib - Admin Dashboard"
            );

        } else if (user.getRole() == Role.LIBRARIAN) {

            SceneRouter.open(
                    stage,
                    new LibrarianDashboardView(
                            user
                    ).createScene(),
                    "IntelliLib - Librarian Dashboard"
            );

        } else if (user.getRole() == Role.STUDENT) {

            SceneRouter.open(
                    stage,
                    new StudentDashboardView(
                            user
                    ).createScene(),
                    "IntelliLib - Student Dashboard"
            );

        } else if (user.getRole() == Role.TEACHER) {

            SceneRouter.open(
                    stage,
                    new TeacherDashboardView(
                            user
                    ).createScene(),
                    "IntelliLib - Teacher Dashboard"
            );

        } else {

            showError(
                    messageLabel,
                    "This account role is not supported."
            );

            playShakeAnimation(formContent);
        }
    }

    private void playEntranceAnimation(
            VBox brandingPanel,
            VBox formPanel
    ) {

        brandingPanel.setOpacity(0);
        brandingPanel.setTranslateX(-35);

        formPanel.setOpacity(0);
        formPanel.setTranslateY(30);

        FadeTransition brandFade =
                new FadeTransition(
                        Duration.millis(650),
                        brandingPanel
                );

        brandFade.setFromValue(0);
        brandFade.setToValue(1);

        TranslateTransition brandSlide =
                new TranslateTransition(
                        Duration.millis(650),
                        brandingPanel
                );

        brandSlide.setFromX(-35);
        brandSlide.setToX(0);
        brandSlide.setInterpolator(
                Interpolator.EASE_OUT
        );

        FadeTransition formFade =
                new FadeTransition(
                        Duration.millis(650),
                        formPanel
                );

        formFade.setFromValue(0);
        formFade.setToValue(1);
        formFade.setDelay(
                Duration.millis(120)
        );

        TranslateTransition formSlide =
                new TranslateTransition(
                        Duration.millis(650),
                        formPanel
                );

        formSlide.setFromY(30);
        formSlide.setToY(0);
        formSlide.setDelay(
                Duration.millis(120)
        );

        formSlide.setInterpolator(
                Interpolator.EASE_OUT
        );

        brandFade.play();
        brandSlide.play();
        formFade.play();
        formSlide.play();
    }

    private void playLogoFloatingAnimation(
            StackPane logoBox
    ) {

        TranslateTransition floatAnimation =
                new TranslateTransition(
                        Duration.seconds(2.4),
                        logoBox
                );

        floatAnimation.setFromY(0);
        floatAnimation.setToY(-8);
        floatAnimation.setAutoReverse(true);
        floatAnimation.setCycleCount(
                TranslateTransition.INDEFINITE
        );

        floatAnimation.setInterpolator(
                Interpolator.EASE_BOTH
        );

        floatAnimation.play();
    }

    private void addButtonHoverAnimation(
            Button button
    ) {

        button.setOnMouseEntered(event -> {

            ScaleTransition scale =
                    new ScaleTransition(
                            Duration.millis(140),
                            button
                    );

            scale.setToX(1.02);
            scale.setToY(1.02);
            scale.play();
        });

        button.setOnMouseExited(event -> {

            ScaleTransition scale =
                    new ScaleTransition(
                            Duration.millis(140),
                            button
                    );

            scale.setToX(1);
            scale.setToY(1);
            scale.play();
        });
    }

    private void playShakeAnimation(
            VBox formContent
    ) {

        Timeline shake =
                new Timeline(
                        new KeyFrame(
                                Duration.ZERO,
                                new KeyValue(
                                        formContent.translateXProperty(),
                                        0
                                )
                        ),
                        new KeyFrame(
                                Duration.millis(60),
                                new KeyValue(
                                        formContent.translateXProperty(),
                                        -9
                                )
                        ),
                        new KeyFrame(
                                Duration.millis(120),
                                new KeyValue(
                                        formContent.translateXProperty(),
                                        9
                                )
                        ),
                        new KeyFrame(
                                Duration.millis(180),
                                new KeyValue(
                                        formContent.translateXProperty(),
                                        -7
                                )
                        ),
                        new KeyFrame(
                                Duration.millis(240),
                                new KeyValue(
                                        formContent.translateXProperty(),
                                        7
                                )
                        ),
                        new KeyFrame(
                                Duration.millis(300),
                                new KeyValue(
                                        formContent.translateXProperty(),
                                        0
                                )
                        )
                );

        shake.play();
    }

    private Region createHorizontalSpacer() {

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        return spacer;
    }

    private Region createVerticalGap(
            double height
    ) {

        Region gap = new Region();
        gap.setMinHeight(height);

        return gap;
    }

    private void showError(
            Label messageLabel,
            String message
    ) {

        messageLabel.setText(message);
        messageLabel.getStyleClass().remove(
                "success-message"
        );

        if (!messageLabel
                .getStyleClass()
                .contains("error-message")) {

            messageLabel
                    .getStyleClass()
                    .add("error-message");
        }
    }
}