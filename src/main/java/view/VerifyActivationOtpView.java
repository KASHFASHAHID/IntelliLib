package view;

import controller.AccountActivationController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.SceneRouter;

public class VerifyActivationOtpView {

    private final int resetId;
    private final AccountActivationController controller;

    public VerifyActivationOtpView(int resetId) {
        this.resetId = resetId;
        this.controller = new AccountActivationController();
    }

    public Scene createScene() {

        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color:#0f172a;");

        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.setMaxWidth(560);

        card.setStyle(
                "-fx-background-color:#111827;" +
                "-fx-background-radius:18;" +
                "-fx-border-color:#334155;" +
                "-fx-border-radius:18;" +
                "-fx-border-width:1;"
        );

        Label title = new Label("Verify Activation OTP");

        title.setStyle(
                "-fx-text-fill:white;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        Label subtitle = new Label(
                "Enter the 6-digit OTP sent to your registered email."
        );

        subtitle.setWrapText(true);
        subtitle.setAlignment(Pos.CENTER);

        subtitle.setStyle(
                "-fx-text-fill:#94a3b8;" +
                "-fx-font-size:15px;"
        );

        Label expiryLabel = new Label(
                "The OTP is valid for 5 minutes and allows a maximum of 3 attempts."
        );

        expiryLabel.setWrapText(true);
        expiryLabel.setAlignment(Pos.CENTER);

        expiryLabel.setStyle(
                "-fx-text-fill:#f59e0b;" +
                "-fx-font-size:13px;"
        );

        TextField otpField = new TextField();

        otpField.setPromptText("Enter 6-digit OTP");
        otpField.setPrefWidth(320);
        otpField.setMaxWidth(320);
        otpField.setPrefHeight(50);

        otpField.setStyle(
                "-fx-background-color:#1e293b;" +
                "-fx-text-fill:white;" +
                "-fx-prompt-text-fill:#64748b;" +
                "-fx-border-color:#334155;" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;" +
                "-fx-font-size:18px;" +
                "-fx-alignment:center;" +
                "-fx-padding:10;"
        );

        /*
         * Allow only numbers and a maximum of six digits.
         */
        otpField.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    String numericValue =
                            newValue.replaceAll("\\D", "");

                    if (numericValue.length() > 6) {
                        numericValue =
                                numericValue.substring(0, 6);
                    }

                    if (!numericValue.equals(newValue)) {
                        otpField.setText(numericValue);
                    }
                }
        );

        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.CENTER);

        Button verifyButton =
                new Button("Verify OTP");

        Button backButton =
                new Button("Back");

        stylePrimaryButton(verifyButton);
        styleSecondaryButton(backButton);

        verifyButton.setOnAction(event -> {

            messageLabel.setText("");

            String enteredOtp =
                    otpField.getText();

            if (enteredOtp == null
                    || !enteredOtp.matches("\\d{6}")) {

                showError(
                        messageLabel,
                        "Please enter a valid 6-digit OTP."
                );

                return;
            }

            verifyButton.setDisable(true);
            verifyButton.setText("Verifying...");

            boolean verified =
                    controller.verifyActivationOtp(
                            resetId,
                            enteredOtp
                    );

            verifyButton.setDisable(false);
            verifyButton.setText("Verify OTP");

            if (!verified) {

                showError(
                        messageLabel,
                        "Invalid, expired, already verified, or blocked OTP."
                );

                return;
            }

            Stage stage =
                    (Stage) verifyButton
                            .getScene()
                            .getWindow();

            SceneRouter.open(
        stage,
        new CreateAccountPasswordView(
                resetId
        ).createScene(),
        "IntelliLib - Create Password"
);
        });

        backButton.setOnAction(event -> {

            Stage stage =
                    (Stage) backButton
                            .getScene()
                            .getWindow();

            SceneRouter.open(
        stage,
        new ActivateAccountView().createScene(),
        "IntelliLib - Activate Account"
);
        });

        HBox buttons = new HBox(
                15,
                backButton,
                verifyButton
        );

        buttons.setAlignment(Pos.CENTER);

        card.getChildren().addAll(
                title,
                subtitle,
                expiryLabel,
                otpField,
                messageLabel,
                buttons
        );

        root.getChildren().add(card);

        return new Scene(root, 1200, 760);
    }

    private void stylePrimaryButton(Button button) {

        button.setPrefWidth(170);
        button.setPrefHeight(48);

        button.setStyle(
                "-fx-background-color:#2563eb;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;"
        );
    }

    private void styleSecondaryButton(Button button) {

        button.setPrefWidth(120);
        button.setPrefHeight(48);

        button.setStyle(
                "-fx-background-color:#334155;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;"
        );
    }

    private void showError(
            Label label,
            String message
    ) {

        label.setStyle(
                "-fx-text-fill:#ef4444;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;"
        );

        label.setText(message);
    }
}