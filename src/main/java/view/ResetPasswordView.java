package view;

import controller.ForgotPasswordController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.SceneRouter;

public class ResetPasswordView {

    private final int resetId;
    private final ForgotPasswordController controller;

    public ResetPasswordView(int resetId) {
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

        Label title = new Label("Reset Password");

        title.setStyle(
                "-fx-text-fill:white;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        Label subtitle = new Label(
                "Create a new password for your account."
        );

        subtitle.setStyle(
                "-fx-text-fill:#94a3b8;" +
                "-fx-font-size:15px;"
        );

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        stylePasswordField(newPasswordField);
        stylePasswordField(confirmPasswordField);

        Label messageLabel = new Label();

        Button updateButton = new Button("Update Password");
        Button cancelButton = new Button("Cancel");

        stylePrimaryButton(updateButton);
        styleSecondaryButton(cancelButton);

        updateButton.setOnAction(event -> {

            messageLabel.setText("");

            boolean updated =
                    controller.resetPassword(
                            resetId,
                            newPasswordField.getText(),
                            confirmPasswordField.getText()
                    );

            if (!updated) {

                showError(
                        messageLabel,
                        "Password could not be updated."
                );

                return;
            }

            messageLabel.setStyle(
                    "-fx-text-fill:#22c55e;" +
                    "-fx-font-size:14px;" +
                    "-fx-font-weight:bold;"
            );

            messageLabel.setText(
                    "Password updated successfully."
            );

            Stage stage =
                    (Stage) updateButton
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

        HBox buttons =
                new HBox(
                        15,
                        cancelButton,
                        updateButton
                );

        buttons.setAlignment(Pos.CENTER);

        card.getChildren().addAll(
                title,
                subtitle,
                newPasswordField,
                confirmPasswordField,
                messageLabel,
                buttons
        );

        root.getChildren().add(card);

        return new Scene(root,1200,760);
    }

    private void stylePasswordField(
            PasswordField field
    ){

        field.setPrefWidth(340);
        field.setPrefHeight(45);
        field.setMaxWidth(340);

        field.setStyle(
                "-fx-background-color:#1e293b;" +
                "-fx-text-fill:white;" +
                "-fx-prompt-text-fill:#64748b;" +
                "-fx-border-color:#334155;" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;"
        );
    }

    private void stylePrimaryButton(Button button){

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

    private void styleSecondaryButton(Button button){

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
            Label label,
            String message
    ){

        label.setStyle(
                "-fx-text-fill:#ef4444;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;"
        );

        label.setText(message);
    }
}