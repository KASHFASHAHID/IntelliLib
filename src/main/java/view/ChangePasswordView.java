package view;

import controller.ProfileController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.User;
import util.SceneRouter;

public class ChangePasswordView {

    private final User loggedInUser;
    private final ProfileController controller;

    public ChangePasswordView(
            User loggedInUser
    ) {
        this.loggedInUser = loggedInUser;
        this.controller = new ProfileController();
    }

    public Scene createScene() {

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color:#0f172a;"
        );

        Text title =
                new Text(
                        "Change Password"
                );

        title.setStyle(
                "-fx-fill:white;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        Label subtitle =
                new Label(
                        "Update your account password securely"
                );

        subtitle.setStyle(
                "-fx-text-fill:#94a3b8;" +
                "-fx-font-size:15px;"
        );

        VBox topBox =
                new VBox(
                        8,
                        title,
                        subtitle
                );

        topBox.setPadding(
                new Insets(25)
        );

        PasswordField currentPasswordField =
                createPasswordField(
                        "Enter current password"
                );

        PasswordField newPasswordField =
                createPasswordField(
                        "Enter new password"
                );

        PasswordField confirmPasswordField =
                createPasswordField(
                        "Confirm new password"
                );

        Label currentLabel =
                createFieldLabel(
                        "Current Password"
                );

        Label newLabel =
                createFieldLabel(
                        "New Password"
                );

        Label confirmLabel =
                createFieldLabel(
                        "Confirm Password"
                );

        Label messageLabel =
                new Label();

        messageLabel.setWrapText(true);

        messageLabel.setStyle(
                "-fx-font-size:14px;"
        );

        GridPane form =
                new GridPane();

        form.setHgap(20);
        form.setVgap(18);

        form.setPadding(
                new Insets(30)
        );

        form.add(
                currentLabel,
                0,
                0
        );

        form.add(
                currentPasswordField,
                1,
                0
        );

        form.add(
                newLabel,
                0,
                1
        );

        form.add(
                newPasswordField,
                1,
                1
        );

        form.add(
                confirmLabel,
                0,
                2
        );

        form.add(
                confirmPasswordField,
                1,
                2
        );

        form.add(
                messageLabel,
                1,
                3
        );

        Button updateButton =
                new Button(
                        "Update Password"
                );

        stylePrimaryButton(
                updateButton
        );

        Button cancelButton =
                new Button(
                        "Cancel"
                );

        styleSecondaryButton(
                cancelButton
        );

        updateButton.setOnAction(event -> {

            String currentPassword =
                    currentPasswordField.getText();

            String newPassword =
                    newPasswordField.getText();

            String confirmPassword =
                    confirmPasswordField.getText();

            messageLabel.setText("");

            if (currentPassword.isEmpty()
                    || newPassword.isEmpty()
                    || confirmPassword.isEmpty()) {

                showError(
                        messageLabel,
                        "Please fill in all password fields."
                );

                return;
            }

            if (newPassword.length() < 8) {

                showError(
                        messageLabel,
                        "New password must contain at least 8 characters."
                );

                return;
            }

            if (newPassword.length() > 72) {

                showError(
                        messageLabel,
                        "New password must not exceed 72 characters."
                );

                return;
            }

            if (!newPassword.equals(
                    confirmPassword
            )) {

                showError(
                        messageLabel,
                        "New password and confirmation do not match."
                );

                return;
            }

            if (currentPassword.equals(
                    newPassword
            )) {

                showError(
                        messageLabel,
                        "New password must be different from the current password."
                );

                return;
            }

            boolean changed =
                    controller.changePassword(
                            loggedInUser.getUserId(),
                            currentPassword,
                            newPassword
                    );

            if (changed) {

                currentPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();

                messageLabel.setStyle(
                        "-fx-text-fill:#22c55e;" +
                        "-fx-font-size:14px;" +
                        "-fx-font-weight:bold;"
                );

                messageLabel.setText(
                        "Password changed successfully."
                );

            } else {

                showError(
                        messageLabel,
                        "Current password is incorrect or the password could not be updated."
                );
            }
        });

        cancelButton.setOnAction(event -> {

            Stage stage =
                    (Stage) cancelButton
                            .getScene()
                            .getWindow();

            SceneRouter.open(
                    stage,
                    new ProfileView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - My Profile"
            );
        });

        HBox buttons =
                new HBox(
                        15,
                        cancelButton,
                        updateButton
                );

        buttons.setAlignment(
                Pos.CENTER_RIGHT
        );

        buttons.setPadding(
                new Insets(
                        20,
                        30,
                        30,
                        30
                )
        );

        root.setTop(
                topBox
        );

        root.setCenter(
                form
        );

        root.setBottom(
                buttons
        );

        return new Scene(
                root,
                1200,
                760
        );
    }

    private PasswordField createPasswordField(
            String promptText
    ) {

        PasswordField passwordField =
                new PasswordField();

        passwordField.setPromptText(
                promptText
        );

        passwordField.setPrefWidth(340);
        passwordField.setPrefHeight(42);

        passwordField.setStyle(
                "-fx-background-color:#1e293b;" +
                "-fx-text-fill:white;" +
                "-fx-prompt-text-fill:#64748b;" +
                "-fx-border-color:#334155;" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;" +
                "-fx-padding:10;"
        );

        return passwordField;
    }

    private Label createFieldLabel(
            String text
    ) {

        Label label =
                new Label(
                        text + " :"
                );

        label.setStyle(
                "-fx-text-fill:#38bdf8;" +
                "-fx-font-size:16px;" +
                "-fx-font-weight:bold;"
        );

        return label;
    }

    private void stylePrimaryButton(
            Button button
    ) {

        button.setPrefWidth(180);
        button.setPrefHeight(45);

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
        button.setPrefHeight(45);

        button.setStyle(
                "-fx-background-color:#334155;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;"
        );
    }

    private void showError(
            Label messageLabel,
            String message
    ) {

        messageLabel.setStyle(
                "-fx-text-fill:#f87171;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;"
        );

        messageLabel.setText(
                message
        );
    }
}