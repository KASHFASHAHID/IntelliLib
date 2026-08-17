package view;

import controller.AccountActivationController;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import util.SceneRouter;

public class ActivateAccountView {

    private final AccountActivationController controller;

    public ActivateAccountView() {
        controller = new AccountActivationController();
    }

    public Scene createScene() {

        StackPane root = new StackPane();

        root.setPadding(new Insets(40));

        root.setStyle(
                "-fx-background-color:linear-gradient(" +
                        "to bottom right," +
                        "#e8f1f2," +
                        "#dbeafe" +
                        ");"
        );

        VBox card = new VBox(18);

        card.setAlignment(Pos.CENTER);

        card.setPadding(
                new Insets(
                        45,
                        55,
                        45,
                        55
                )
        );

        card.setMaxWidth(550);
        card.setMinWidth(470);

        card.setStyle(
                "-fx-background-color:#ffffff;" +
                        "-fx-background-radius:22;" +
                        "-fx-border-color:#cbd5e1;" +
                        "-fx-border-radius:22;" +
                        "-fx-border-width:1;" +
                        "-fx-effect:dropshadow(" +
                        "gaussian," +
                        "rgba(15,23,42,0.18)," +
                        "30," +
                        "0.16," +
                        "0," +
                        "10" +
                        ");"
        );

        StackPane iconBox = new StackPane();

        iconBox.setPrefSize(76, 76);
        iconBox.setMinSize(76, 76);
        iconBox.setMaxSize(76, 76);

        iconBox.setStyle(
                "-fx-background-color:linear-gradient(" +
                        "to bottom right," +
                        "#0f766e," +
                        "#0891b2" +
                        ");" +
                        "-fx-background-radius:22;" +
                        "-fx-effect:dropshadow(" +
                        "gaussian," +
                        "rgba(15,118,110,0.30)," +
                        "18," +
                        "0.20," +
                        "0," +
                        "5" +
                        ");"
        );

        Label icon = new Label("✓");

        icon.setStyle(
                "-fx-text-fill:white;" +
                        "-fx-font-size:34px;" +
                        "-fx-font-weight:bold;"
        );

        iconBox.getChildren().add(icon);

        Label title =
                new Label("Activate New Account");

        title.setStyle(
                "-fx-text-fill:#0f172a;" +
                        "-fx-font-size:31px;" +
                        "-fx-font-weight:bold;"
        );

        Label subtitle =
                new Label(
                        "Enter the User ID and registered email address " +
                                "provided in your approval message."
                );

        subtitle.setWrapText(true);
        subtitle.setMaxWidth(400);
        subtitle.setAlignment(Pos.CENTER);

        subtitle.setStyle(
                "-fx-text-fill:#64748b;" +
                        "-fx-font-size:14px;" +
                        "-fx-line-spacing:2px;"
        );

        VBox userIdBox =
                createFieldBox(
                        "User ID",
                        "Enter your approved User ID"
                );

        TextField userIdField =
                (TextField) userIdBox
                        .getChildren()
                        .get(1);

        VBox emailBox =
                createFieldBox(
                        "Registered Email",
                        "Enter your registered email address"
                );

        TextField emailField =
                (TextField) emailBox
                        .getChildren()
                        .get(1);

        Label messageLabel = new Label();

        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(400);
        messageLabel.setMinHeight(22);
        messageLabel.setAlignment(Pos.CENTER);

        Button backButton =
                new Button("Back");

        Button sendOtpButton =
                new Button("Send Activation OTP");

        styleSecondaryButton(backButton);
        stylePrimaryButton(sendOtpButton);

        sendOtpButton.setOnAction(event -> {

            messageLabel.setText("");

            String userId =
                    userIdField.getText() == null
                            ? ""
                            : userIdField
                            .getText()
                            .trim();

            String email =
                    emailField.getText() == null
                            ? ""
                            : emailField
                            .getText()
                            .trim();

            if (userId.isBlank()
                    || email.isBlank()) {

                showError(
                        messageLabel,
                        "Please enter both your User ID and registered email."
                );

                playShakeAnimation(card);
                return;
            }

            if (!email.matches(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
            )) {

                showError(
                        messageLabel,
                        "Please enter a valid email address."
                );

                playShakeAnimation(card);
                return;
            }

            sendOtpButton.setDisable(true);
            sendOtpButton.setText(
                    "Sending OTP..."
            );

            int resetId =
                    controller.sendActivationOtp(
                            userId,
                            email
                    );

            sendOtpButton.setDisable(false);
            sendOtpButton.setText(
                    "Send Activation OTP"
            );

            if (resetId == -1) {

                showError(
                        messageLabel,
                        "The account was not found, is already active, " +
                                "or the activation email could not be sent."
                );

                playShakeAnimation(card);
                return;
            }

            Stage stage =
                    (Stage) sendOtpButton
                            .getScene()
                            .getWindow();

            SceneRouter.open(
                    stage,
                    new VerifyActivationOtpView(
                            resetId
                    ).createScene(),
                    "IntelliLib - Verify Activation OTP"
            );
        });

        emailField.setOnAction(event ->
                sendOtpButton.fire()
        );

        backButton.setOnAction(event -> {

            Stage stage =
                    (Stage) backButton
                            .getScene()
                            .getWindow();

            SceneRouter.open(
                    stage,
                    new AccountAccessView()
                            .createScene(),
                    "IntelliLib - Account Access"
            );
        });

        HBox buttonRow =
                new HBox(
                        14,
                        backButton,
                        sendOtpButton
                );

        buttonRow.setAlignment(Pos.CENTER);

        card.getChildren().addAll(
                iconBox,
                title,
                subtitle,
                createVerticalGap(5),
                userIdBox,
                emailBox,
                messageLabel,
                buttonRow
        );

        root.getChildren().add(card);

        playEntranceAnimation(card);
        playFloatingAnimation(iconBox);

        return new Scene(
                root,
                1200,
                760
        );
    }

    private VBox createFieldBox(
            String labelText,
            String promptText
    ) {

        Label label =
                new Label(labelText);

        label.setMaxWidth(
                Double.MAX_VALUE
        );

        label.setStyle(
                "-fx-text-fill:#334155;" +
                        "-fx-font-size:13px;" +
                        "-fx-font-weight:bold;"
        );

        TextField field =
                new TextField();

        field.setPromptText(promptText);
        field.setPrefWidth(400);
        field.setMaxWidth(Double.MAX_VALUE);
        field.setPrefHeight(50);

        String normalStyle =
                "-fx-background-color:#f8fafc;" +
                        "-fx-text-fill:#0f172a;" +
                        "-fx-prompt-text-fill:#94a3b8;" +
                        "-fx-font-size:14px;" +
                        "-fx-padding:0 15;" +
                        "-fx-background-radius:10;" +
                        "-fx-border-color:#cbd5e1;" +
                        "-fx-border-radius:10;" +
                        "-fx-border-width:1;";

        String focusedStyle =
                "-fx-background-color:#ffffff;" +
                        "-fx-text-fill:#0f172a;" +
                        "-fx-prompt-text-fill:#94a3b8;" +
                        "-fx-font-size:14px;" +
                        "-fx-padding:0 15;" +
                        "-fx-background-radius:10;" +
                        "-fx-border-color:#0f766e;" +
                        "-fx-border-radius:10;" +
                        "-fx-border-width:2;" +
                        "-fx-effect:dropshadow(" +
                        "gaussian," +
                        "rgba(15,118,110,0.14)," +
                        "9," +
                        "0.15," +
                        "0," +
                        "0" +
                        ");";

        field.setStyle(normalStyle);

        field.focusedProperty().addListener(
                (observable, oldValue, focused) ->
                        field.setStyle(
                                focused
                                        ? focusedStyle
                                        : normalStyle
                        )
        );

        VBox box =
                new VBox(
                        7,
                        label,
                        field
                );

        box.setMaxWidth(400);

        return box;
    }

    private void stylePrimaryButton(
            Button button
    ) {

        button.setPrefWidth(210);
        button.setPrefHeight(50);
        button.setCursor(Cursor.HAND);

        String normalStyle =
                "-fx-background-color:linear-gradient(" +
                        "to right," +
                        "#0f766e," +
                        "#0891b2" +
                        ");" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:14px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-background-radius:10;" +
                        "-fx-effect:dropshadow(" +
                        "gaussian," +
                        "rgba(15,118,110,0.24)," +
                        "14," +
                        "0.16," +
                        "0," +
                        "4" +
                        ");";

        String hoverStyle =
                "-fx-background-color:linear-gradient(" +
                        "to right," +
                        "#115e59," +
                        "#0e7490" +
                        ");" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:14px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-background-radius:10;";

        applyHoverEffect(
                button,
                normalStyle,
                hoverStyle
        );
    }

    private void styleSecondaryButton(
            Button button
    ) {

        button.setPrefWidth(140);
        button.setPrefHeight(50);
        button.setCursor(Cursor.HAND);

        String normalStyle =
                "-fx-background-color:#ffffff;" +
                        "-fx-text-fill:#475569;" +
                        "-fx-font-size:14px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-border-color:#94a3b8;" +
                        "-fx-border-radius:10;" +
                        "-fx-background-radius:10;";

        String hoverStyle =
                "-fx-background-color:#f1f5f9;" +
                        "-fx-text-fill:#0f172a;" +
                        "-fx-font-size:14px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-border-color:#64748b;" +
                        "-fx-border-radius:10;" +
                        "-fx-background-radius:10;";

        applyHoverEffect(
                button,
                normalStyle,
                hoverStyle
        );
    }

    private void applyHoverEffect(
            Button button,
            String normalStyle,
            String hoverStyle
    ) {

        button.setStyle(normalStyle);

        button.setOnMouseEntered(event -> {

            button.setStyle(hoverStyle);

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

            button.setStyle(normalStyle);

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

    private void showError(
            Label label,
            String message
    ) {

        label.setText(message);

        label.setStyle(
                "-fx-text-fill:#dc2626;" +
                        "-fx-font-size:13px;" +
                        "-fx-font-weight:bold;"
        );
    }

    private Region createVerticalGap(
            double height
    ) {

        Region gap = new Region();
        gap.setMinHeight(height);

        return gap;
    }

    private void playEntranceAnimation(
            VBox card
    ) {

        card.setOpacity(0);
        card.setTranslateY(28);

        FadeTransition fade =
                new FadeTransition(
                        Duration.millis(600),
                        card
                );

        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide =
                new TranslateTransition(
                        Duration.millis(600),
                        card
                );

        slide.setFromY(28);
        slide.setToY(0);
        slide.setInterpolator(
                Interpolator.EASE_OUT
        );

        fade.play();
        slide.play();
    }

    private void playFloatingAnimation(
            StackPane iconBox
    ) {

        TranslateTransition floating =
                new TranslateTransition(
                        Duration.seconds(2.5),
                        iconBox
                );

        floating.setFromY(0);
        floating.setToY(-6);
        floating.setAutoReverse(true);
        floating.setCycleCount(
                TranslateTransition.INDEFINITE
        );

        floating.setInterpolator(
                Interpolator.EASE_BOTH
        );

        floating.play();
    }

    private void playShakeAnimation(
            VBox card
    ) {

        TranslateTransition first =
                new TranslateTransition(
                        Duration.millis(65),
                        card
                );

        first.setToX(-8);

        TranslateTransition second =
                new TranslateTransition(
                        Duration.millis(65),
                        card
                );

        second.setToX(8);

        TranslateTransition third =
                new TranslateTransition(
                        Duration.millis(65),
                        card
                );

        third.setToX(-6);

        TranslateTransition fourth =
                new TranslateTransition(
                        Duration.millis(65),
                        card
                );

        fourth.setToX(0);

        SequentialTransition shake =
                new SequentialTransition(
                        first,
                        second,
                        third,
                        fourth
                );

        shake.play();
    }
}