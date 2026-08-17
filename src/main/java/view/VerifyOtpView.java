package view;

import controller.ForgotPasswordController;
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

public class VerifyOtpView {

    private final int resetId;
    private final ForgotPasswordController controller;

    public VerifyOtpView(int resetId) {
        this.resetId = resetId;
        this.controller = new ForgotPasswordController();
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
                "-fx-background-radius:18;"
        );

        Label title = new Label("Verify OTP");

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

        TextField otpField = new TextField();

        otpField.setPromptText("Enter 6-digit OTP");
        otpField.setPrefWidth(320);
        otpField.setPrefHeight(50);
        otpField.setMaxWidth(320);

        otpField.setStyle(
                "-fx-background-color:#1e293b;" +
                "-fx-text-fill:white;" +
                "-fx-prompt-text-fill:#64748b;" +
                "-fx-border-color:#334155;" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;" +
                "-fx-font-size:18px;" +
                "-fx-alignment:center;"
        );

        /*
         * Only allow numbers and maximum 6 digits.
         */
        otpField.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    if (!newValue.matches("\\d*")) {
                        otpField.setText(
                                newValue.replaceAll("\\D", "")
                        );
                    }

                    if (otpField.getText().length() > 6) {
                        otpField.setText(
                                otpField.getText().substring(0, 6)
                        );
                    }
                }
        );

        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.CENTER);

        Button verifyButton = new Button("Verify OTP");
        Button backButton = new Button("Back");

        verifyButton.setPrefWidth(170);
        verifyButton.setPrefHeight(45);

        verifyButton.setStyle(
                "-fx-background-color:#2563eb;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;"
        );

        backButton.setPrefWidth(120);
        backButton.setPrefHeight(45);

        backButton.setStyle(
                "-fx-background-color:#334155;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;"
        );

        verifyButton.setOnAction(event -> {

            messageLabel.setText("");

            String enteredOtp = otpField.getText();

            if (enteredOtp == null
                    || !enteredOtp.matches("\\d{6}")) {

                showError(
                        messageLabel,
                        "Please enter a valid 6-digit OTP."
                );

                return;
            }

            boolean verified =
                    controller.verifyOtp(
                            resetId,
                            enteredOtp
                    );

            if (!verified) {

                showError(
                        messageLabel,
                        "Invalid, expired, or already used OTP."
                );

                return;
            }

            Stage stage =
                    (Stage) verifyButton
                            .getScene()
                            .getWindow();

            SceneRouter.open(
        stage,
        new ResetPasswordView(
                resetId
        ).createScene(),
        "IntelliLib - Reset Password"
);
        });

        backButton.setOnAction(event -> {

            Stage stage =
                    (Stage) backButton
                            .getScene()
                            .getWindow();

            SceneRouter.open(
        stage,
        new ForgotPasswordView().createScene(),
        "IntelliLib - Forgot Password"
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
                otpField,
                messageLabel,
                buttons
        );

        root.getChildren().add(card);

        return new Scene(root, 1200, 760);
    }

    private void showError(
            Label messageLabel,
            String message
    ) {

        messageLabel.setStyle(
                "-fx-text-fill:#ef4444;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;"
        );

        messageLabel.setText(message);
    }
}