package view;

import controller.ActivityLogController;
import controller.NotificationController;
import controller.ProfileController;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Profile;
import model.Role;
import model.User;
import service.EmailService;
import util.MemberDashboardRouter;
import util.SceneRouter;

import java.util.Optional;
import javafx.scene.control.ScrollPane;

public class ProfileView {

    private final User loggedInUser;
    private final ProfileController controller;

    public ProfileView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.controller = new ProfileController();
    }

    public Scene createScene() {

        Profile profile =
                controller.getProfileByUserId(
                        loggedInUser.getUserId()
                );

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color:#e8f1f2;"
        );

        VBox header =
                createHeader();

        VBox content =
                createContent(profile);

        ScrollPane scrollPane =
        new ScrollPane(content);

scrollPane.setFitToWidth(true);
scrollPane.setPannable(true);
scrollPane.setHbarPolicy(
        ScrollPane.ScrollBarPolicy.NEVER
);
scrollPane.setVbarPolicy(
        ScrollPane.ScrollBarPolicy.AS_NEEDED
);

scrollPane.setStyle(
        "-fx-background-color:transparent;" +
        "-fx-background:transparent;" +
        "-fx-border-color:transparent;"
);

root.setTop(header);
root.setCenter(scrollPane);

        playEntranceAnimation(
                header,
                content
        );

        return new Scene(
                root,
                1200,
                760
        );
    }

    private VBox createHeader() {

        Label title =
                new Label("My Profile");

        title.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        Label subtitle =
                new Label(
                        "View your account information and manage your contact details."
                );

        subtitle.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:15px;"
        );

        VBox headingBox =
                new VBox(
                        6,
                        title,
                        subtitle
                );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label roleBadge =
                new Label(
                        loggedInUser.getRole() == Role.TEACHER
                                ? "● TEACHER"
                                : "● STUDENT"
                );

        roleBadge.setStyle(
                "-fx-background-color:#dcfce7;" +
                "-fx-text-fill:#166534;" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:9 16;" +
                "-fx-background-radius:18;" +
                "-fx-border-color:#86efac;" +
                "-fx-border-radius:18;"
        );

        Button backButton =
                createSecondaryButton(
                        "Back to Dashboard"
                );

        backButton.setOnAction(event -> {

            Stage stage =
                    (Stage) backButton
                            .getScene()
                            .getWindow();

            MemberDashboardRouter.openDashboard(
                    stage,
                    loggedInUser
            );
        });

        HBox headerRow =
                new HBox(
                        20,
                        headingBox,
                        spacer,
                        roleBadge,
                        backButton
                );

        headerRow.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox header =
                new VBox(headerRow);

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

    private VBox createContent(
            Profile profile
    ) {

        if (profile == null) {
            return createUnavailableContent();
        }

        VBox identityCard =
                createIdentityCard(profile);

        GridPane detailsGrid =
                createProfileDetailsGrid(profile);

        Button editProfileButton =
                createPrimaryButton(
                        "Edit Contact Details"
                );

        Button changePasswordButton =
                createOutlinedButton(
                        "Change Password"
                );

        editProfileButton.setOnAction(event ->
                openContactEditor(
                        profile,
                        editProfileButton
                )
        );

        changePasswordButton.setOnAction(event -> {

            Stage stage =
                    (Stage) changePasswordButton
                            .getScene()
                            .getWindow();

            SceneRouter.open(
                    stage,
                    new ChangePasswordView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Change Password"
            );
        });

        Region buttonSpacer =
                new Region();

        HBox.setHgrow(
                buttonSpacer,
                Priority.ALWAYS
        );

        HBox actionRow =
                new HBox(
                        12,
                        buttonSpacer,
                        changePasswordButton,
                        editProfileButton
                );

        actionRow.setAlignment(
                Pos.CENTER_RIGHT
        );

        VBox detailsCard =
                new VBox(
                        22,
                        createSectionHeader(
                                "Account Information",
                                "Your registered account and membership details."
                        ),
                        detailsGrid,
                        actionRow
                );

        detailsCard.setPadding(
                new Insets(26)
        );

        detailsCard.setStyle(
                "-fx-background-color:#ffffff;" +
                "-fx-background-radius:16;" +
                "-fx-border-color:#cbd5e1;" +
                "-fx-border-radius:16;" +
                "-fx-border-width:1;" +
                "-fx-effect:dropshadow(" +
                "gaussian," +
                "rgba(15,23,42,0.10)," +
                "14," +
                "0.15," +
                "0," +
                "4" +
                ");"
        );

        VBox content =
                new VBox(
                        20,
                        identityCard,
                        detailsCard
                );

        content.setPadding(
                new Insets(
                        28,
                        35,
                        35,
                        35
                )
        );

        return content;
    }

    private VBox createIdentityCard(
            Profile profile
    ) {

        String name =
                safeValue(
                        profile.getName()
                );

        String initial =
                name.equals("-")
                        ? "?"
                        : name.substring(0, 1)
                        .toUpperCase();

        Label avatar =
                new Label(initial);

        avatar.setAlignment(
                Pos.CENTER
        );

        avatar.setMinSize(
                72,
                72
        );

        avatar.setMaxSize(
                72,
                72
        );

        avatar.setStyle(
                "-fx-background-color:#0f766e;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:36;"
        );

        Label nameLabel =
                new Label(name);

        nameLabel.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:23px;" +
                "-fx-font-weight:bold;"
        );

        Label userIdLabel =
                new Label(
                        safeValue(
                                profile.getUserId()
                        )
                );

        userIdLabel.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:14px;"
        );

        Label statusBadge =
                new Label(
                        "● "
                                + safeValue(
                                profile.getAccountStatus()
                        ).toUpperCase()
                );

        statusBadge.setStyle(
                createStatusStyle(
                        profile.getAccountStatus()
                )
        );

        VBox identityText =
                new VBox(
                        6,
                        nameLabel,
                        userIdLabel,
                        statusBadge
                );

        HBox cardContent =
                new HBox(
                        18,
                        avatar,
                        identityText
                );

        cardContent.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox card =
                new VBox(cardContent);

        card.setPadding(
                new Insets(24)
        );

        card.setStyle(
                "-fx-background-color:#f8fafc;" +
                "-fx-background-radius:16;" +
                "-fx-border-color:#b6d4d6;" +
                "-fx-border-radius:16;" +
                "-fx-border-width:1;"
        );

        return card;
    }

    private GridPane createProfileDetailsGrid(
            Profile profile
    ) {

        GridPane grid =
                new GridPane();

        grid.setHgap(20);
        grid.setVgap(14);

        ColumnConstraints firstColumn =
                new ColumnConstraints();

        firstColumn.setPercentWidth(50);

        ColumnConstraints secondColumn =
                new ColumnConstraints();

        secondColumn.setPercentWidth(50);

        grid.getColumnConstraints().addAll(
                firstColumn,
                secondColumn
        );

        VBox userIdField =
                createDetailField(
                        "User ID",
                        profile.getUserId()
                );

        VBox roleField =
                createDetailField(
                        "Role",
                        profile.getRole()
                );

        VBox emailField =
                createDetailField(
                        "Email Address",
                        profile.getEmail()
                );

        VBox phoneField =
                createDetailField(
                        "Phone Number",
                        profile.getPhone()
                );

        VBox departmentField =
                createDetailField(
                        "Department",
                        profile.getDepartment()
                );

        VBox universityField =
                createDetailField(
                        "University",
                        profile.getUniversity()
                );

        VBox statusField =
                createDetailField(
                        "Account Status",
                        profile.getAccountStatus()
                );

        VBox nameField =
                createDetailField(
                        "Full Name",
                        profile.getName()
                );

        grid.add(userIdField, 0, 0);
        grid.add(nameField, 1, 0);

        grid.add(roleField, 0, 1);
        grid.add(statusField, 1, 1);

        grid.add(emailField, 0, 2);
        grid.add(phoneField, 1, 2);

        grid.add(departmentField, 0, 3);
        grid.add(universityField, 1, 3);

        return grid;
    }

    private VBox createDetailField(
            String heading,
            String value
    ) {

        Label headingLabel =
                new Label(heading);

        headingLabel.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:12px;" +
                "-fx-font-weight:bold;"
        );

        Label valueLabel =
                new Label(
                        safeValue(value)
                );

        valueLabel.setWrapText(true);

        valueLabel.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;"
        );

        VBox field =
                new VBox(
                        6,
                        headingLabel,
                        valueLabel
                );

        field.setMaxWidth(
                Double.MAX_VALUE
        );

        field.setMinHeight(72);

        field.setPadding(
                new Insets(14)
        );

        field.setStyle(
                "-fx-background-color:#f8fafc;" +
                "-fx-background-radius:10;" +
                "-fx-border-color:#e2e8f0;" +
                "-fx-border-radius:10;"
        );

        return field;
    }

    private VBox createSectionHeader(
            String title,
            String subtitle
    ) {

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:18px;" +
                "-fx-font-weight:bold;"
        );

        Label subtitleLabel =
                new Label(subtitle);

        subtitleLabel.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:13px;"
        );

        return new VBox(
                5,
                titleLabel,
                subtitleLabel
        );
    }

    private VBox createUnavailableContent() {

        Label title =
                new Label(
                        "Profile unavailable"
                );

        title.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:22px;" +
                "-fx-font-weight:bold;"
        );

        Label message =
                new Label(
                        "Your profile information could not be loaded. Please try again later."
                );

        message.setWrapText(true);

        message.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:15px;"
        );

        Button backButton =
                createPrimaryButton(
                        "Back to Dashboard"
                );

        backButton.setOnAction(event -> {

            Stage stage =
                    (Stage) backButton
                            .getScene()
                            .getWindow();

            MemberDashboardRouter.openDashboard(
                    stage,
                    loggedInUser
            );
        });

        VBox card =
                new VBox(
                        16,
                        title,
                        message,
                        backButton
                );

        card.setAlignment(
                Pos.CENTER
        );

        card.setMaxWidth(520);

        card.setPadding(
                new Insets(35)
        );

        card.setStyle(
                "-fx-background-color:#ffffff;" +
                "-fx-background-radius:16;" +
                "-fx-border-color:#fecaca;" +
                "-fx-border-radius:16;" +
                "-fx-border-width:1;"
        );

        VBox content =
                new VBox(card);

        content.setAlignment(
                Pos.CENTER
        );

        content.setPadding(
                new Insets(40)
        );

        return content;
    }

    private void openContactEditor(
            Profile profile,
            Button sourceButton
    ) {

        TextField emailField =
                createDialogInput(
                        profile.getEmail()
                );

        TextField phoneField =
                createDialogInput(
                        profile.getPhone()
                );

        phoneField.setPromptText(
                "+919876543210"
        );

        GridPane form =
                new GridPane();

        form.setHgap(18);
        form.setVgap(14);
        form.setPadding(
                new Insets(10)
        );

        Label emailLabel =
                createDialogLabel(
                        "Email Address"
                );

        Label phoneLabel =
                createDialogLabel(
                        "Phone Number"
                );

        Label phoneHelp =
                new Label(
                        "Use +91 followed by exactly 10 digits."
                );

        phoneHelp.setWrapText(true);

        phoneHelp.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:12px;"
        );

        form.add(
                emailLabel,
                0,
                0
        );

        form.add(
                emailField,
                1,
                0
        );

        form.add(
                phoneLabel,
                0,
                1
        );

        form.add(
                phoneField,
                1,
                1
        );

        form.add(
                phoneHelp,
                1,
                2
        );

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Edit Contact Details"
        );

        dialog.setHeaderText(
                "Update your registered email address and phone number."
        );

        ButtonType continueButtonType =
                new ButtonType(
                        "Continue",
                        ButtonBar.ButtonData.OK_DONE
                );

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        continueButtonType,
                        ButtonType.CANCEL
                );

        dialog.getDialogPane()
                .setContent(form);

        dialog.getDialogPane()
                .setPrefWidth(570);

        Optional<ButtonType> result =
                dialog.showAndWait();

        if (result.isEmpty()
                || result.get()
                != continueButtonType) {

            return;
        }

        String email =
                emailField.getText() == null
                        ? ""
                        : emailField.getText().trim();

        String phone =
                phoneField.getText() == null
                        ? ""
                        : phoneField.getText().trim();

        String validationError =
                controller.validateContactDetails(
                        loggedInUser.getUserId(),
                        email,
                        phone
                );

        if (validationError != null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Invalid Contact Details",
                    "The profile could not be updated.",
                    validationError
            );

            return;
        }

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Confirm Profile Update"
        );

        confirmation.setHeaderText(
                "Confirm the new contact information."
        );

        confirmation.setContentText(
                "Email: "
                        + email
                        + "\nPhone: "
                        + phone
                        + "\n\nDo you want to save these changes?"
        );

        Optional<ButtonType> confirmationResult =
                confirmation.showAndWait();

        if (confirmationResult.isEmpty()
                || confirmationResult.get()
                != ButtonType.OK) {

            return;
        }

        boolean updated =
                controller.updateContactDetails(
                        loggedInUser.getUserId(),
                        email,
                        phone
                );

        if (!updated) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Update Unsuccessful",
                    "Your profile could not be updated.",
                    "The email address may already be registered, or a database error may have occurred."
            );

            return;
        }

        processSuccessfulContactUpdate(
                profile,
                email,
                phone
        );

        showAlert(
                Alert.AlertType.INFORMATION,
                "Profile Updated",
                "Your contact details were updated successfully.",
                "The new email address and phone number are now associated with your account."
        );

        Stage stage =
                (Stage) sourceButton
                        .getScene()
                        .getWindow();

        SceneRouter.open(
                stage,
                new ProfileView(
                        loggedInUser
                ).createScene(),
                "IntelliLib - My Profile"
        );
    }

    private void processSuccessfulContactUpdate(
            Profile profile,
            String email,
            String phone
    ) {

        String oldEmail =
                profile.getEmail();

        boolean emailChanged =
                oldEmail != null
                        && !oldEmail.isBlank()
                        && !oldEmail.equalsIgnoreCase(email);

        EmailService emailService =
                new EmailService();

        emailService.sendProfileContactUpdateEmail(
                email,
                profile.getName(),
                loggedInUser.getUserId(),
                oldEmail,
                email,
                phone,
                false
        );

        if (emailChanged) {

            emailService.sendProfileContactUpdateEmail(
                    oldEmail,
                    profile.getName(),
                    loggedInUser.getUserId(),
                    oldEmail,
                    email,
                    phone,
                    true
            );
        }

        NotificationController notificationController =
                new NotificationController();

        notificationController.createNotification(
                loggedInUser.getUserId(),
                "Profile Contact Details Updated",
                "Your registered email address and phone number "
                        + "were updated successfully."
        );

        ActivityLogController activityLogController =
                new ActivityLogController();

        activityLogController.logActivity(
                loggedInUser.getUserId(),
                "PROFILE_UPDATE",
                "User updated registered email address and phone number."
        );
    }

    private TextField createDialogInput(
            String value
    ) {

        TextField field =
                new TextField(
                        value == null
                                ? ""
                                : value
                );

        field.setPrefWidth(330);
        field.setPrefHeight(42);

        field.setStyle(
                "-fx-background-color:#f8fafc;" +
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:14px;" +
                "-fx-padding:0 12;" +
                "-fx-background-radius:8;" +
                "-fx-border-color:#cbd5e1;" +
                "-fx-border-radius:8;"
        );

        return field;
    }

    private Label createDialogLabel(
            String text
    ) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-text-fill:#334155;" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;"
        );

        return label;
    }

    private Button createPrimaryButton(
            String text
    ) {

        return createStyledButton(
                text,
                190,
                "-fx-background-color:#0f766e;",
                "-fx-background-color:#115e59;"
        );
    }

    private Button createOutlinedButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefWidth(180);
        button.setPrefHeight(46);
        button.setCursor(Cursor.HAND);

        String normalStyle =
                "-fx-background-color:#ffffff;" +
                "-fx-text-fill:#0f766e;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-border-color:#0f766e;" +
                "-fx-border-radius:10;" +
                "-fx-background-radius:10;";

        String hoverStyle =
                "-fx-background-color:#ecfeff;" +
                "-fx-text-fill:#115e59;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-border-color:#115e59;" +
                "-fx-border-radius:10;" +
                "-fx-background-radius:10;";

        applyButtonHover(
                button,
                normalStyle,
                hoverStyle
        );

        return button;
    }

    private Button createSecondaryButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefWidth(180);
        button.setPrefHeight(44);
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

        applyButtonHover(
                button,
                normalStyle,
                hoverStyle
        );

        return button;
    }

    private Button createStyledButton(
            String text,
            double width,
            String normalBackground,
            String hoverBackground
    ) {

        Button button =
                new Button(text);

        button.setPrefWidth(width);
        button.setPrefHeight(46);
        button.setCursor(Cursor.HAND);

        String normalStyle =
                normalBackground +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;";

        String hoverStyle =
                hoverBackground +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;";

        applyButtonHover(
                button,
                normalStyle,
                hoverStyle
        );

        return button;
    }

    private void applyButtonHover(
            Button button,
            String normalStyle,
            String hoverStyle
    ) {

        button.setStyle(
                normalStyle
        );

        button.setOnMouseEntered(event -> {

            if (!button.isDisabled()) {

                button.setStyle(
                        hoverStyle
                );
            }
        });

        button.setOnMouseExited(event ->
                button.setStyle(
                        normalStyle
                )
        );
    }

    private String createStatusStyle(
            String status
    ) {

        String normalized =
                status == null
                        ? ""
                        : status.trim()
                        .toUpperCase();

        if (normalized.equals("ACTIVE")) {

            return "-fx-background-color:#dcfce7;"
                    + "-fx-text-fill:#166534;"
                    + "-fx-font-size:12px;"
                    + "-fx-font-weight:bold;"
                    + "-fx-padding:7 12;"
                    + "-fx-background-radius:16;";
        }

        if (normalized.contains("SUSPEND")
                || normalized.contains("BLOCK")) {

            return "-fx-background-color:#fee2e2;"
                    + "-fx-text-fill:#b91c1c;"
                    + "-fx-font-size:12px;"
                    + "-fx-font-weight:bold;"
                    + "-fx-padding:7 12;"
                    + "-fx-background-radius:16;";
        }

        return "-fx-background-color:#fef3c7;"
                + "-fx-text-fill:#92400e;"
                + "-fx-font-size:12px;"
                + "-fx-font-weight:bold;"
                + "-fx-padding:7 12;"
                + "-fx-background-radius:16;";
    }

    private String safeValue(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            return "-";
        }

        return value;
    }

    private void showAlert(
            Alert.AlertType type,
            String title,
            String header,
            String content
    ) {

        Alert alert =
                new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void playEntranceAnimation(
            VBox header,
            VBox content
    ) {

        header.setOpacity(0);
        header.setTranslateY(-15);

        content.setOpacity(0);
        content.setTranslateY(24);

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

        headerSlide.setFromY(-15);
        headerSlide.setToY(0);
        headerSlide.setInterpolator(
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
                Duration.millis(100)
        );

        TranslateTransition contentSlide =
                new TranslateTransition(
                        Duration.millis(600),
                        content
                );

        contentSlide.setFromY(24);
        contentSlide.setToY(0);
        contentSlide.setDelay(
                Duration.millis(100)
        );

        contentSlide.setInterpolator(
                Interpolator.EASE_OUT
        );

        headerFade.play();
        headerSlide.play();
        contentFade.play();
        contentSlide.play();
    }
}