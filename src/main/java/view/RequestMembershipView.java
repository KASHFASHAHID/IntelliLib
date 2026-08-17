package view;

import controller.MembershipRequestController;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.MembershipRequest;
import util.SceneRouter;

public class RequestMembershipView {

    private final MembershipRequestController controller;

    public RequestMembershipView() {
        controller = new MembershipRequestController();
    }

    public Scene createScene() {

        StackPane root = new StackPane();

        root.setStyle(
                "-fx-background-color:linear-gradient(" +
                        "to bottom right," +
                        "#e8f1f2," +
                        "#dbeafe" +
                        ");"
        );

        VBox pageContent =
                new VBox(25);

        pageContent.setAlignment(
                Pos.TOP_CENTER
        );

        pageContent.setPadding(
                new Insets(
                        35,
                        40,
                        45,
                        40
                )
        );

        VBox headingBox =
                createHeading();

        VBox formCard =
                createFormCard();

        pageContent.getChildren().addAll(
                headingBox,
                formCard
        );

        ScrollPane scrollPane =
                new ScrollPane(pageContent);

        scrollPane.setFitToWidth(true);

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

        root.getChildren().add(
                scrollPane
        );

        playEntranceAnimation(
                headingBox,
                formCard
        );

        return new Scene(
                root,
                1200,
                760
        );
    }

    private VBox createHeading() {

        Label title =
                new Label(
                        "Membership Request"
                );

        title.setStyle(
                "-fx-text-fill:#0f172a;" +
                        "-fx-font-size:34px;" +
                        "-fx-font-weight:bold;"
        );

        Label subtitle =
                new Label(
                        "Apply for access to IntelliLib."
                );

        subtitle.setStyle(
                "-fx-text-fill:#475569;" +
                        "-fx-font-size:15px;"
        );

        Label note =
                new Label(
                        "Provide accurate academic and contact information. " +
                                "Your request will be reviewed by a library administrator."
                );

        note.setWrapText(true);

        note.setMaxWidth(
                760
        );

        note.setAlignment(
                Pos.CENTER
        );

        note.setStyle(
                "-fx-text-fill:#64748b;" +
                        "-fx-font-size:13px;"
        );

        VBox heading =
                new VBox(
                        7,
                        title,
                        subtitle,
                        note
                );

        heading.setAlignment(
                Pos.CENTER
        );

        return heading;
    }

    private VBox createFormCard() {

        TextField fullNameField =
                createTextField(
                        "Enter your full name"
                );

        TextField brainwareIdField =
                createTextField(
                        "Enter your Brainware ID"
                );

        TextField universityField =
                createTextField(
                        "Enter university name"
                );

        ComboBox<String> roleBox =
                new ComboBox<>();

        roleBox.getItems().addAll(
                "STUDENT",
                "TEACHER"
        );

        roleBox.setPromptText(
                "Select membership role"
        );

        roleBox.setPrefHeight(46);
        roleBox.setMaxWidth(
                Double.MAX_VALUE
        );

        roleBox.setStyle(
                "-fx-background-color:#f8fafc;" +
                        "-fx-border-color:#cbd5e1;" +
                        "-fx-border-radius:9;" +
                        "-fx-background-radius:9;" +
                        "-fx-font-size:14px;"
        );

        TextField courseField =
                createTextField(
                        "Course name or teacher designation"
                );

        TextField departmentField =
                createTextField(
                        "Enter department"
                );

        TextField semesterField =
                createTextField(
                        "Enter semester number if applicable"
                );

        TextField emailField =
                createTextField(
                        "Enter your email address"
                );

        TextField phoneField =
                createTextField(
                        "Example: +919876543210"
                );

        TextArea reasonArea =
                new TextArea();

        reasonArea.setPromptText(
                "Briefly explain why you need library membership"
        );

        reasonArea.setWrapText(true);
        reasonArea.setPrefRowCount(4);
        reasonArea.setPrefHeight(110);

        reasonArea.setStyle(
                "-fx-background-color:#f8fafc;" +
                        "-fx-text-fill:#0f172a;" +
                        "-fx-prompt-text-fill:#94a3b8;" +
                        "-fx-border-color:#cbd5e1;" +
                        "-fx-border-radius:9;" +
                        "-fx-background-radius:9;" +
                        "-fx-font-size:14px;" +
                        "-fx-padding:8;"
        );

        GridPane personalGrid =
                createFormGrid();

        addField(
                personalGrid,
                0,
                "Full Name",
                fullNameField
        );

        addField(
                personalGrid,
                1,
                "Brainware ID",
                brainwareIdField
        );

        addField(
                personalGrid,
                2,
                "University",
                universityField
        );

        addField(
                personalGrid,
                3,
                "Membership Role",
                roleBox
        );

        GridPane academicGrid =
                createFormGrid();

        addField(
                academicGrid,
                0,
                "Course / Designation",
                courseField
        );

        addField(
                academicGrid,
                1,
                "Department",
                departmentField
        );

        addField(
                academicGrid,
                2,
                "Semester",
                semesterField
        );

        GridPane contactGrid =
                createFormGrid();

        addField(
                contactGrid,
                0,
                "Email Address",
                emailField
        );

        addField(
                contactGrid,
                1,
                "Phone Number",
                phoneField
        );

        VBox reasonBox =
                new VBox(
                        8,
                        createFieldLabel(
                                "Reason for Membership"
                        ),
                        reasonArea
                );

        Button submitButton =
                new Button(
                        "Submit Request"
                );

        Button backButton =
                new Button(
                        "Back to Login"
                );

        stylePrimaryButton(
                submitButton
        );

        styleSecondaryButton(
                backButton
        );

        submitButton.setOnAction(event -> {

            try {

                String fullName =
                        safeText(
                                fullNameField
                        );

                String brainwareId =
                        safeText(
                                brainwareIdField
                        );

                String university =
                        safeText(
                                universityField
                        );

                String role =
                        roleBox.getValue();

                String course =
                        safeText(
                                courseField
                        );

                String department =
                        safeText(
                                departmentField
                        );

                String email =
                        safeText(
                                emailField
                        );

                String phone =
                        safeText(
                                phoneField
                        );

                String reason =
                        reasonArea.getText() == null
                                ? ""
                                : reasonArea
                                .getText()
                                .trim();

                if (fullName.isBlank()
                        || brainwareId.isBlank()
                        || university.isBlank()
                        || role == null
                        || course.isBlank()
                        || department.isBlank()
                        || email.isBlank()
                        || phone.isBlank()
                        || reason.isBlank()) {

                    showAlert(
                            Alert.AlertType.WARNING,
                            "Required Information Missing",
                            "Please complete all required fields.",
                            "Review the form and provide the requested personal, academic and contact information."
                    );

                    return;
                }

                Integer semester = null;

                String semesterText =
                        safeText(
                                semesterField
                        );

                if (!semesterText.isBlank()) {

                    semester =
                            Integer.parseInt(
                                    semesterText
                            );

                    if (semester < 1
                            || semester > 20) {

                        showAlert(
                                Alert.AlertType.WARNING,
                                "Invalid Semester",
                                "Please enter a valid semester number.",
                                "Semester must be between 1 and 20."
                        );

                        return;
                    }
                }

                if (!email.matches(
                        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
                )) {

                    showAlert(
                            Alert.AlertType.WARNING,
                            "Invalid Email Address",
                            "Please enter a valid email address.",
                            "Example: student@example.com"
                    );

                    return;
                }

                if (!phone.matches(
                        "^\\+91[6-9]\\d{9}$"
                )) {

                    showAlert(
                            Alert.AlertType.WARNING,
                            "Invalid Phone Number",
                            "Please enter a valid Indian mobile number.",
                            "Use +91 followed by exactly 10 digits.\n" +
                                    "Example: +919876543210"
                    );

                    return;
                }

                MembershipRequest request =
                        new MembershipRequest(
                                0,
                                fullName,
                                brainwareId,
                                role,
                                department,
                                email,
                                phone,
                                "PENDING",
                                university,
                                course,
                                semester,
                                reason
                        );

                submitButton.setDisable(true);
                submitButton.setText(
                        "Submitting..."
                );

                boolean success =
                        controller.submitRequest(
                                request
                        );

                submitButton.setDisable(false);
                submitButton.setText(
                        "Submit Request"
                );

                if (!success) {

                    showAlert(
                            Alert.AlertType.ERROR,
                            "Submission Failed",
                            "Your membership request could not be submitted.",
                            "The ID or email may already be registered, or a database error may have occurred."
                    );

                    return;
                }

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Request Submitted",
                        "Your membership request was submitted successfully.",
                        "The library administrator will review your application. You will receive further instructions after approval."
                );

                Stage stage =
                        (Stage) submitButton
                                .getScene()
                                .getWindow();

                SceneRouter.open(
                        stage,
                        new LoginView()
                                .createScene(),
                        "IntelliLib - Login"
                );

            } catch (NumberFormatException exception) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Invalid Semester",
                        "The semester value must be numeric.",
                        "Enter a valid semester number or leave the field empty if it does not apply."
                );

            } catch (Exception exception) {

                showAlert(
                        Alert.AlertType.ERROR,
                        "Submission Error",
                        "The request could not be processed.",
                        "Please review the form and try again."
                );

                submitButton.setDisable(false);
                submitButton.setText(
                        "Submit Request"
                );
            }
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

        HBox buttonRow =
                new HBox(
                        14,
                        backButton,
                        submitButton
                );

        buttonRow.setAlignment(
                Pos.CENTER_RIGHT
        );

        Region separatorOne =
                createSeparator();

        Region separatorTwo =
                createSeparator();

        Region separatorThree =
                createSeparator();

        VBox formCard =
                new VBox(
                        20,
                        createSectionTitle(
                                "Personal Information"
                        ),
                        personalGrid,
                        separatorOne,
                        createSectionTitle(
                                "Academic Information"
                        ),
                        academicGrid,
                        separatorTwo,
                        createSectionTitle(
                                "Contact Information"
                        ),
                        contactGrid,
                        separatorThree,
                        reasonBox,
                        buttonRow
                );

        formCard.setPadding(
                new Insets(
                        32,
                        38,
                        34,
                        38
                )
        );

        formCard.setMaxWidth(
                850
        );

        formCard.setStyle(
                "-fx-background-color:#ffffff;" +
                        "-fx-background-radius:20;" +
                        "-fx-border-color:#cbd5e1;" +
                        "-fx-border-radius:20;" +
                        "-fx-border-width:1;" +
                        "-fx-effect:dropshadow(" +
                        "gaussian," +
                        "rgba(15,23,42,0.16)," +
                        "28," +
                        "0.16," +
                        "0," +
                        "8" +
                        ");"
        );

        return formCard;
    }

    private GridPane createFormGrid() {

        GridPane grid =
                new GridPane();

        grid.setHgap(22);
        grid.setVgap(15);

        grid.getColumnConstraints()
                .addAll(
                        createLabelColumn(),
                        createInputColumn()
                );

        return grid;
    }

    private javafx.scene.layout.ColumnConstraints
    createLabelColumn() {

        javafx.scene.layout.ColumnConstraints column =
                new javafx.scene.layout.ColumnConstraints();

        column.setMinWidth(175);
        column.setPrefWidth(175);

        return column;
    }

    private javafx.scene.layout.ColumnConstraints
    createInputColumn() {

        javafx.scene.layout.ColumnConstraints column =
                new javafx.scene.layout.ColumnConstraints();

        column.setHgrow(
                Priority.ALWAYS
        );

        column.setFillWidth(true);

        return column;
    }

    private void addField(
            GridPane grid,
            int row,
            String labelText,
            javafx.scene.Node field
    ) {

        Label label =
                createFieldLabel(
                        labelText
                );

        GridPane.setHgrow(
                field,
                Priority.ALWAYS
        );

        grid.add(
                label,
                0,
                row
        );

        grid.add(
                field,
                1,
                row
        );
    }

    private TextField createTextField(
            String promptText
    ) {

        TextField field =
                new TextField();

        field.setPromptText(
                promptText
        );

        field.setPrefHeight(46);
        field.setMaxWidth(
                Double.MAX_VALUE
        );

        field.setStyle(
                "-fx-background-color:#f8fafc;" +
                        "-fx-text-fill:#0f172a;" +
                        "-fx-prompt-text-fill:#94a3b8;" +
                        "-fx-border-color:#cbd5e1;" +
                        "-fx-border-radius:9;" +
                        "-fx-background-radius:9;" +
                        "-fx-font-size:14px;" +
                        "-fx-padding:0 14;"
        );

        return field;
    }

    private Label createFieldLabel(
            String text
    ) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-text-fill:#334155;" +
                        "-fx-font-size:14px;" +
                        "-fx-font-weight:bold;"
        );

        return label;
    }

    private Label createSectionTitle(
            String text
    ) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-text-fill:#0f766e;" +
                        "-fx-font-size:18px;" +
                        "-fx-font-weight:bold;"
        );

        return label;
    }

    private Region createSeparator() {

        Region separator =
                new Region();

        separator.setPrefHeight(1);

        separator.setStyle(
                "-fx-background-color:#e2e8f0;"
        );

        return separator;
    }

    private void stylePrimaryButton(
            Button button
    ) {

        button.setPrefWidth(180);
        button.setPrefHeight(48);
        button.setCursor(
                Cursor.HAND
        );

        String normalStyle =
                "-fx-background-color:#0f766e;" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:14px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-background-radius:10;";

        String hoverStyle =
                "-fx-background-color:#115e59;" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:14px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-background-radius:10;";

        button.setStyle(
                normalStyle
        );

        button.setOnMouseEntered(event ->
                button.setStyle(
                        hoverStyle
                )
        );

        button.setOnMouseExited(event ->
                button.setStyle(
                        normalStyle
                )
        );
    }

    private void styleSecondaryButton(
            Button button
    ) {

        button.setPrefWidth(160);
        button.setPrefHeight(48);
        button.setCursor(
                Cursor.HAND
        );

        String normalStyle =
                "-fx-background-color:#ffffff;" +
                        "-fx-text-fill:#334155;" +
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

        button.setStyle(
                normalStyle
        );

        button.setOnMouseEntered(event ->
                button.setStyle(
                        hoverStyle
                )
        );

        button.setOnMouseExited(event ->
                button.setStyle(
                        normalStyle
                )
        );
    }

    private void playEntranceAnimation(
            VBox heading,
            VBox formCard
    ) {

        heading.setOpacity(0);
        heading.setTranslateY(-15);

        formCard.setOpacity(0);
        formCard.setTranslateY(30);

        FadeTransition headingFade =
                new FadeTransition(
                        Duration.millis(500),
                        heading
                );

        headingFade.setFromValue(0);
        headingFade.setToValue(1);

        TranslateTransition headingSlide =
                new TranslateTransition(
                        Duration.millis(500),
                        heading
                );

        headingSlide.setFromY(-15);
        headingSlide.setToY(0);
        headingSlide.setInterpolator(
                Interpolator.EASE_OUT
        );

        FadeTransition cardFade =
                new FadeTransition(
                        Duration.millis(650),
                        formCard
                );

        cardFade.setFromValue(0);
        cardFade.setToValue(1);
        cardFade.setDelay(
                Duration.millis(120)
        );

        TranslateTransition cardSlide =
                new TranslateTransition(
                        Duration.millis(650),
                        formCard
                );

        cardSlide.setFromY(30);
        cardSlide.setToY(0);
        cardSlide.setDelay(
                Duration.millis(120)
        );

        cardSlide.setInterpolator(
                Interpolator.EASE_OUT
        );

        headingFade.play();
        headingSlide.play();
        cardFade.play();
        cardSlide.play();
    }

    private String safeText(
            TextField field
    ) {

        if (field.getText() == null) {
            return "";
        }

        return field
                .getText()
                .trim();
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
}