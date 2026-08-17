package view;

import controller.AccountActivationController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.SceneRouter;

public class CreateAccountPasswordView {

    private final int resetId;
    private final AccountActivationController controller;

    public CreateAccountPasswordView(int resetId) {
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
        card.setMaxWidth(580);

        card.setStyle(
                "-fx-background-color:#111827;" +
                "-fx-background-radius:18;" +
                "-fx-border-color:#334155;" +
                "-fx-border-radius:18;" +
                "-fx-border-width:1;"
        );

        Label title = new Label("Create Your Password");

        title.setStyle(
                "-fx-text-fill:white;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        Label subtitle = new Label(
                "Create a secure password to activate your library account."
        );

        subtitle.setWrapText(true);
        subtitle.setAlignment(Pos.CENTER);

        subtitle.setStyle(
                "-fx-text-fill:#94a3b8;" +
                "-fx-font-size:15px;"
        );

        PasswordField passwordField =
                new PasswordField();

        passwordField.setPromptText(
                "Enter new password"
        );

        PasswordField confirmPasswordField =
                new PasswordField();

        confirmPasswordField.setPromptText(
                "Confirm new password"
        );

        stylePasswordField(passwordField);
        stylePasswordField(confirmPasswordField);

        Label passwordRules = new Label(
                "Password must contain at least 8 characters, "
                        + "one uppercase letter, one lowercase letter, "
                        + "one number and one special character."
        );

        passwordRules.setWrapText(true);
        passwordRules.setAlignment(Pos.CENTER);

        passwordRules.setStyle(
                "-fx-text-fill:#f59e0b;" +
                "-fx-font-size:13px;"
        );

        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.CENTER);

        Button activateButton =
                new Button("Activate Account");

        Button cancelButton =
                new Button("Cancel");

        stylePrimaryButton(activateButton);
        styleSecondaryButton(cancelButton);

        activateButton.setOnAction(event -> {

            messageLabel.setText("");

            String password =
                    passwordField.getText();

            String confirmPassword =
                    confirmPasswordField.getText();

            if (password == null
                    || password.isEmpty()
                    || confirmPassword == null
                    || confirmPassword.isEmpty()) {

                showError(
                        messageLabel,
                        "Please enter and confirm your password."
                );

                return;
            }

            if (!password.equals(confirmPassword)) {

                showError(
                        messageLabel,
                        "Password and confirmation do not match."
                );

                return;
            }

            if (!isStrongPassword(password)) {

                showError(
                        messageLabel,
                        "Password does not meet the required security rules."
                );

                return;
            }

            activateButton.setDisable(true);
            cancelButton.setDisable(true);
            activateButton.setText("Activating...");

            boolean activated =
                    controller.activateAccount(
                            resetId,
                            password,
                            confirmPassword
                    );

            activateButton.setDisable(false);
            cancelButton.setDisable(false);
            activateButton.setText("Activate Account");

            if (!activated) {

                showError(
                        messageLabel,
                        "Account could not be activated. "
                                + "The OTP may have expired or already been used."
                );

                return;
            }

            Alert successAlert =
                    new Alert(
                            Alert.AlertType.INFORMATION
                    );

            successAlert.setTitle(
                    "Account Activated"
            );

            successAlert.setHeaderText(
                    "Your library account is now active."
            );

            successAlert.setContentText(
                    "You can now log in using your User ID "
                            + "and the password you created."
            );

            successAlert.showAndWait();

            Stage stage =
                    (Stage) activateButton
                            .getScene()
                            .getWindow();

            SceneRouter.open(
        stage,
        new LoginView().createScene(),
        "IntelliLib - Login"
);
        });

        cancelButton.setOnAction(event -> {

            Stage stage =
                    (Stage) cancelButton
                            .getScene()
                            .getWindow();

            SceneRouter.open(
        stage,
        new LoginView().createScene(),
        "IntelliLib - Login"
);
        });

        HBox buttons = new HBox(
                15,
                cancelButton,
                activateButton
        );

        buttons.setAlignment(Pos.CENTER);

        card.getChildren().addAll(
                title,
                subtitle,
                passwordField,
                confirmPasswordField,
                passwordRules,
                messageLabel,
                buttons
        );

        root.getChildren().add(card);

        return new Scene(root, 1200, 760);
    }

    private boolean isStrongPassword(
            String password
    ) {

        if (password.length() < 8
                || password.length() > 72) {

            return false;
        }

        boolean hasUppercase =
                password.matches(".*[A-Z].*");

        boolean hasLowercase =
                password.matches(".*[a-z].*");

        boolean hasDigit =
                password.matches(".*\\d.*");

        boolean hasSpecialCharacter =
                password.matches(
                        ".*[@#$%&*!?_\\-].*"
                );

        return hasUppercase
                && hasLowercase
                && hasDigit
                && hasSpecialCharacter;
    }

    private void stylePasswordField(
            PasswordField field
    ) {

        field.setPrefWidth(350);
        field.setMaxWidth(350);
        field.setPrefHeight(48);

        field.setStyle(
                "-fx-background-color:#1e293b;" +
                "-fx-text-fill:white;" +
                "-fx-prompt-text-fill:#64748b;" +
                "-fx-border-color:#334155;" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;" +
                "-fx-padding:10;"
        );
    }

    private void stylePrimaryButton(
            Button button
    ) {

        button.setPrefWidth(190);
        button.setPrefHeight(48);

        button.setStyle(
                "-fx-background-color:#2563eb;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;"
        );
    }

    private void styleSecondaryButton(
            Button button
    ) {

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