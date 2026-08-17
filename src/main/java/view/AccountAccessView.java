package view;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import util.SceneRouter;

public class AccountAccessView {

    public Scene createScene() {

        StackPane root =
                new StackPane();

        root.setPadding(
                new Insets(40)
        );

        root.setStyle(
                "-fx-background-color:linear-gradient(" +
                        "to bottom right," +
                        "#e8f1f2," +
                        "#dbeafe" +
                        ");"
        );

        VBox card =
                new VBox(20);

        card.setAlignment(
                Pos.CENTER
        );

        card.setPadding(
                new Insets(
                        45,
                        55,
                        45,
                        55
                )
        );

        card.setMaxWidth(520);
        card.setMinWidth(460);

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

        StackPane iconBox =
                new StackPane();

        iconBox.setPrefSize(
                76,
                76
        );

        iconBox.setMinSize(
                76,
                76
        );

        iconBox.setMaxSize(
                76,
                76
        );

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

        Label icon =
                new Label("🔐");

        icon.setStyle(
                "-fx-font-size:31px;"
        );

        iconBox.getChildren().add(
                icon
        );

        Label title =
                new Label(
                        "Account Access"
                );

        title.setStyle(
                "-fx-text-fill:#0f172a;" +
                        "-fx-font-size:31px;" +
                        "-fx-font-weight:bold;"
        );

        Label subtitle =
                new Label(
                        "Choose the option that matches your account."
                );

        subtitle.setWrapText(true);

        subtitle.setAlignment(
                Pos.CENTER
        );

        subtitle.setStyle(
                "-fx-text-fill:#64748b;" +
                        "-fx-font-size:15px;"
        );

        Label activationDescription =
                createDescription(
                        "Activate an approved Student, Teacher, Admin, or Librarian account."
                );

        Button activateButton =
                new Button(
                        "Activate New Account"
                );

        stylePrimaryButton(
                activateButton
        );

        Label forgotDescription =
                createDescription(
                        "Recover access if you cannot remember your current password."
                );

        Button forgotPasswordButton =
                new Button(
                        "Forgot Password"
                );

        styleOutlinedButton(
                forgotPasswordButton
        );

        Button backButton =
                new Button(
                        "Back to Login"
                );

        styleSecondaryButton(
                backButton
        );

        activateButton.setOnAction(event -> {

            Stage stage =
                    (Stage) activateButton
                            .getScene()
                            .getWindow();

            SceneRouter.open(
                    stage,
                    new ActivateAccountView()
                            .createScene(),
                    "IntelliLib - Activate Account"
            );
        });

        forgotPasswordButton.setOnAction(event -> {

            Stage stage =
                    (Stage) forgotPasswordButton
                            .getScene()
                            .getWindow();

            SceneRouter.open(
                    stage,
                    new ForgotPasswordView()
                            .createScene(),
                    "IntelliLib - Forgot Password"
            );
        });

        backButton.setOnAction(event -> {

            Stage stage =
                    (Stage) backButton
                            .getScene()
                            .getWindow();

            SceneRouter.open(
                    stage,
                    new LoginView()
                            .createScene(),
                    "IntelliLib - Login"
            );
        });

        card.getChildren().addAll(
                iconBox,
                title,
                subtitle,
                createVerticalGap(5),
                activationDescription,
                activateButton,
                createSeparator(),
                forgotDescription,
                forgotPasswordButton,
                createVerticalGap(4),
                backButton
        );

        root.getChildren().add(
                card
        );

        playEntranceAnimation(
                card
        );

        playFloatingAnimation(
                iconBox
        );

        return new Scene(
                root,
                1200,
                760
        );
    }

    private Label createDescription(
            String text
    ) {

        Label label =
                new Label(text);

        label.setWrapText(true);

        label.setMaxWidth(
                390
        );

        label.setAlignment(
                Pos.CENTER
        );

        label.setStyle(
                "-fx-text-fill:#64748b;" +
                        "-fx-font-size:13px;" +
                        "-fx-line-spacing:2px;"
        );

        return label;
    }

    private void stylePrimaryButton(
            Button button
    ) {

        button.setPrefWidth(340);
        button.setPrefHeight(50);
        button.setCursor(
                Cursor.HAND
        );

        String normalStyle =
                "-fx-background-color:linear-gradient(" +
                        "to right," +
                        "#0f766e," +
                        "#0891b2" +
                        ");" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:15px;" +
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
                        "-fx-font-size:15px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-background-radius:10;" +
                        "-fx-effect:dropshadow(" +
                        "gaussian," +
                        "rgba(15,118,110,0.32)," +
                        "18," +
                        "0.18," +
                        "0," +
                        "5" +
                        ");";

        applyHoverEffect(
                button,
                normalStyle,
                hoverStyle
        );
    }

    private void styleOutlinedButton(
            Button button
    ) {

        button.setPrefWidth(340);
        button.setPrefHeight(50);
        button.setCursor(
                Cursor.HAND
        );

        String normalStyle =
                "-fx-background-color:#ffffff;" +
                        "-fx-text-fill:#0f766e;" +
                        "-fx-font-size:15px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-border-color:#0f766e;" +
                        "-fx-border-width:1.5;" +
                        "-fx-border-radius:10;" +
                        "-fx-background-radius:10;";

        String hoverStyle =
                "-fx-background-color:#ecfeff;" +
                        "-fx-text-fill:#115e59;" +
                        "-fx-font-size:15px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-border-color:#115e59;" +
                        "-fx-border-width:1.5;" +
                        "-fx-border-radius:10;" +
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

        button.setPrefWidth(340);
        button.setPrefHeight(48);
        button.setCursor(
                Cursor.HAND
        );

        String normalStyle =
                "-fx-background-color:#f1f5f9;" +
                        "-fx-text-fill:#475569;" +
                        "-fx-font-size:14px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-border-color:#cbd5e1;" +
                        "-fx-border-radius:10;" +
                        "-fx-background-radius:10;";

        String hoverStyle =
                "-fx-background-color:#e2e8f0;" +
                        "-fx-text-fill:#0f172a;" +
                        "-fx-font-size:14px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-border-color:#94a3b8;" +
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

        button.setStyle(
                normalStyle
        );

        button.setOnMouseEntered(event -> {

            button.setStyle(
                    hoverStyle
            );

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

            button.setStyle(
                    normalStyle
            );

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

    private javafx.scene.layout.Region createSeparator() {

        javafx.scene.layout.Region separator =
                new javafx.scene.layout.Region();

        separator.setPrefWidth(360);
        separator.setPrefHeight(1);

        separator.setStyle(
                "-fx-background-color:#e2e8f0;"
        );

        return separator;
    }

    private javafx.scene.layout.Region createVerticalGap(
            double height
    ) {

        javafx.scene.layout.Region gap =
                new javafx.scene.layout.Region();

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
}