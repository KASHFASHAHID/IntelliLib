package view;

import controller.LibrarySettingsController;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
import model.LibrarySettings;
import model.User;
import util.SceneRouter;

import java.math.BigDecimal;

public class LibrarySettingsView {

    private final User loggedInUser;
    private final LibrarySettingsController controller;

    public LibrarySettingsView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.controller = new LibrarySettingsController();
    }

    public Scene createScene() {

        LibrarySettings settings =
                controller.getSettings();

        if (settings == null) {
            return createSettingsUnavailableScene();
        }

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color:#e8f1f2;"
        );

        VBox header =
                createHeader();

        VBox settingsCard =
                createSettingsCard(settings);

        VBox pageContent =
                new VBox(settingsCard);

        pageContent.setAlignment(
                Pos.TOP_CENTER
        );

        pageContent.setPadding(
                new Insets(
                        30,
                        40,
                        45,
                        40
                )
        );

        pageContent.setStyle(
                "-fx-background-color:#e8f1f2;"
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
                "-fx-background-color:#e8f1f2;" +
                "-fx-background:#e8f1f2;" +
                "-fx-border-color:transparent;"
        );

        root.setTop(header);
        root.setCenter(scrollPane);

        playEntranceAnimation(
                header,
                settingsCard
        );

        return new Scene(
                root,
                1200,
                760
        );
    }

    private VBox createHeader() {

        Label title =
                new Label("Library Settings");

        title.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        Label subtitle =
                new Label(
                        "Manage borrowing policies and operational rules."
                );

        subtitle.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:15px;"
        );

        VBox header =
                new VBox(
                        7,
                        title,
                        subtitle
                );

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

    private VBox createSettingsCard(
            LibrarySettings settings
    ) {

        Label sectionTitle =
                new Label(
                        "Loan and Borrowing Rules"
                );

        sectionTitle.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:22px;" +
                "-fx-font-weight:bold;"
        );

        Label sectionDescription =
                new Label(
                        "Configure loan periods, borrowing limits, reservation pickup time and overdue charges."
                );

        sectionDescription.setWrapText(true);

        sectionDescription.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:14px;"
        );

        VBox sectionHeading =
                new VBox(
                        6,
                        sectionTitle,
                        sectionDescription
                );

        TextField studentNormalDaysField =
                createNumberField(
                        settings.getStudentNormalDays()
                );

        TextField studentExamDaysField =
                createNumberField(
                        settings.getStudentExamDays()
                );

        TextField teacherDaysField =
                createNumberField(
                        settings.getTeacherDays()
                );

        TextField studentMaxBooksField =
                createNumberField(
                        settings.getStudentMaxBooks()
                );

        TextField teacherMaxBooksField =
                createNumberField(
                        settings.getTeacherMaxBooks()
                );

        TextField reservationDaysField =
                createNumberField(
                        settings.getReservationPickupDays()
                );

        TextField finePerDayField =
                createDecimalField(
                        settings.getFinePerDay()
                );

        GridPane settingsGrid =
                createSettingsGrid();

        addSettingRow(
                settingsGrid,
                0,
                "Student normal loan period",
                "Number of days allowed during normal mode.",
                studentNormalDaysField
        );

        addSettingRow(
                settingsGrid,
                1,
                "Student exam loan period",
                "Number of days allowed while exam mode is enabled.",
                studentExamDaysField
        );

        addSettingRow(
                settingsGrid,
                2,
                "Teacher loan period",
                "Number of days allowed for teacher loans.",
                teacherDaysField
        );

        addSettingRow(
                settingsGrid,
                3,
                "Student borrowing limit",
                "Maximum active books allowed per student.",
                studentMaxBooksField
        );

        addSettingRow(
                settingsGrid,
                4,
                "Teacher borrowing limit",
                "Maximum active books allowed per teacher.",
                teacherMaxBooksField
        );

        addSettingRow(
                settingsGrid,
                5,
                "Reservation pickup period",
                "Number of days a reserved book remains available.",
                reservationDaysField
        );

        addSettingRow(
                settingsGrid,
                6,
                "Fine per overdue day",
                "Daily charge applied after the due date.",
                finePerDayField
        );

        CheckBox examModeBox =
                new CheckBox(
                        "Enable exam mode"
                );

        examModeBox.setSelected(
                settings.isExamMode()
        );

        examModeBox.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;"
        );

        Label examModeDescription =
                new Label(
                        "When enabled, student loans use the shorter exam-period duration."
                );

        examModeDescription.setWrapText(true);

        examModeDescription.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:13px;"
        );

        VBox examModeContainer =
                new VBox(
                        7,
                        examModeBox,
                        examModeDescription
                );

        examModeContainer.setPadding(
                new Insets(18)
        );

        examModeContainer.setStyle(
                "-fx-background-color:#f1f5f9;" +
                "-fx-background-radius:12;" +
                "-fx-border-color:#cbd5e1;" +
                "-fx-border-radius:12;" +
                "-fx-border-width:1;"
        );

        Button backButton =
                createSecondaryButton(
                        "Back to Dashboard"
                );

        Button saveButton =
                createPrimaryButton(
                        "Save Settings"
                );

        saveButton.setOnAction(event ->
                saveSettings(
                        examModeBox,
                        studentNormalDaysField,
                        studentExamDaysField,
                        teacherDaysField,
                        studentMaxBooksField,
                        teacherMaxBooksField,
                        reservationDaysField,
                        finePerDayField
                )
        );

        backButton.setOnAction(event -> {

            Stage stage =
                    getStage(backButton);

            SceneRouter.open(
                    stage,
                    new AdminDashboardView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Admin Dashboard"
            );
        });

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox buttonRow =
                new HBox(
                        14,
                        spacer,
                        backButton,
                        saveButton
                );

        buttonRow.setAlignment(
                Pos.CENTER_RIGHT
        );

        VBox card =
                new VBox(
                        24,
                        sectionHeading,
                        createSeparator(),
                        settingsGrid,
                        examModeContainer,
                        buttonRow
                );

        card.setMaxWidth(820);

        card.setPadding(
                new Insets(
                        32,
                        38,
                        34,
                        38
                )
        );

        card.setStyle(
                "-fx-background-color:#ffffff;" +
                "-fx-background-radius:20;" +
                "-fx-border-color:#cbd5e1;" +
                "-fx-border-radius:20;" +
                "-fx-border-width:1;" +
                "-fx-effect:dropshadow(" +
                "gaussian," +
                "rgba(15,23,42,0.14)," +
                "24," +
                "0.15," +
                "0," +
                "8" +
                ");"
        );

        return card;
    }

    private GridPane createSettingsGrid() {

        GridPane grid =
                new GridPane();

        grid.setHgap(35);
        grid.setVgap(18);

        ColumnConstraints labelColumn =
                new ColumnConstraints();

        labelColumn.setMinWidth(400);
        labelColumn.setPrefWidth(480);
        labelColumn.setHgrow(
                Priority.ALWAYS
        );

        ColumnConstraints inputColumn =
                new ColumnConstraints();

        inputColumn.setMinWidth(180);
        inputColumn.setPrefWidth(190);

        grid.getColumnConstraints().addAll(
                labelColumn,
                inputColumn
        );

        return grid;
    }

    private void addSettingRow(
            GridPane grid,
            int row,
            String heading,
            String description,
            TextField field
    ) {

        VBox labelBox =
                createFieldLabel(
                        heading,
                        description
                );

        GridPane.setHgrow(
                labelBox,
                Priority.ALWAYS
        );

        grid.add(
                labelBox,
                0,
                row
        );

        grid.add(
                field,
                1,
                row
        );
    }

    private VBox createFieldLabel(
            String heading,
            String description
    ) {

        Label headingLabel =
                new Label(heading);

        headingLabel.setStyle(
                "-fx-text-fill:#334155;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;"
        );

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(440);

        descriptionLabel.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:12px;"
        );

        return new VBox(
                3,
                headingLabel,
                descriptionLabel
        );
    }

    private TextField createNumberField(
            int value
    ) {

        TextField field =
                new TextField(
                        String.valueOf(value)
                );

        configureInputField(field);

        field.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    if (!newValue.matches("\\d*")) {
                        field.setText(
                                newValue.replaceAll(
                                        "\\D",
                                        ""
                                )
                        );
                    }
                }
        );

        return field;
    }

    private TextField createDecimalField(
            BigDecimal value
    ) {

        TextField field =
                new TextField(
                        value == null
                                ? "0"
                                : value.toPlainString()
                );

        configureInputField(field);

        field.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    if (!newValue.matches(
                            "\\d*(\\.\\d*)?"
                    )) {

                        field.setText(oldValue);
                    }
                }
        );

        return field;
    }

    private void configureInputField(
            TextField field
    ) {

        field.setPrefWidth(190);
        field.setPrefHeight(46);

        String normalStyle =
                "-fx-background-color:#f8fafc;" +
                "-fx-text-fill:#0f172a;" +
                "-fx-prompt-text-fill:#94a3b8;" +
                "-fx-font-size:15px;" +
                "-fx-padding:0 14;" +
                "-fx-background-radius:9;" +
                "-fx-border-color:#cbd5e1;" +
                "-fx-border-radius:9;" +
                "-fx-border-width:1;";

        String focusedStyle =
                "-fx-background-color:#ffffff;" +
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:15px;" +
                "-fx-padding:0 14;" +
                "-fx-background-radius:9;" +
                "-fx-border-color:#0f766e;" +
                "-fx-border-radius:9;" +
                "-fx-border-width:2;" +
                "-fx-effect:dropshadow(" +
                "gaussian," +
                "rgba(15,118,110,0.13)," +
                "8," +
                "0.12," +
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
    }

    private Button createPrimaryButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefWidth(175);
        button.setPrefHeight(48);
        button.setCursor(Cursor.HAND);

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
        button.setPrefHeight(48);
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

    private void applyButtonHover(
            Button button,
            String normalStyle,
            String hoverStyle
    ) {

        button.setStyle(normalStyle);

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

    private Region createSeparator() {

        Region separator =
                new Region();

        separator.setPrefHeight(1);

        separator.setStyle(
                "-fx-background-color:#e2e8f0;"
        );

        return separator;
    }

    private void saveSettings(
            CheckBox examModeBox,
            TextField studentNormalDaysField,
            TextField studentExamDaysField,
            TextField teacherDaysField,
            TextField studentMaxBooksField,
            TextField teacherMaxBooksField,
            TextField reservationDaysField,
            TextField finePerDayField
    ) {

        try {

            int studentNormalDays =
                    parsePositiveInteger(
                            studentNormalDaysField,
                            "Student normal loan period"
                    );

            int studentExamDays =
                    parsePositiveInteger(
                            studentExamDaysField,
                            "Student exam loan period"
                    );

            int teacherDays =
                    parsePositiveInteger(
                            teacherDaysField,
                            "Teacher loan period"
                    );

            int studentMaxBooks =
                    parsePositiveInteger(
                            studentMaxBooksField,
                            "Student borrowing limit"
                    );

            int teacherMaxBooks =
                    parsePositiveInteger(
                            teacherMaxBooksField,
                            "Teacher borrowing limit"
                    );

            int reservationDays =
                    parsePositiveInteger(
                            reservationDaysField,
                            "Reservation pickup period"
                    );

            String fineText =
                    finePerDayField
                            .getText()
                            .trim();

            if (fineText.isBlank()) {

                throw new IllegalArgumentException(
                        "Fine per overdue day is required."
                );
            }

            BigDecimal finePerDay =
                    new BigDecimal(fineText);

            if (finePerDay.compareTo(
                    BigDecimal.ZERO
            ) < 0) {

                throw new IllegalArgumentException(
                        "Fine per overdue day cannot be negative."
                );
            }

            LibrarySettings updatedSettings =
                    new LibrarySettings(
                            examModeBox.isSelected(),
                            studentNormalDays,
                            studentExamDays,
                            teacherDays,
                            studentMaxBooks,
                            teacherMaxBooks,
                            reservationDays,
                            finePerDay
                    );

            boolean saved =
                    controller.saveSettings(
                            updatedSettings
                    );

            if (saved) {

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Settings Saved",
                        "Library settings updated successfully.",
                        "The new borrowing rules will apply to future transactions."
                );

            } else {

                showAlert(
                        Alert.AlertType.ERROR,
                        "Save Failed",
                        "Library settings could not be updated.",
                        "Please verify the database connection and try again."
                );
            }

        } catch (NumberFormatException exception) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Invalid Input",
                    "Please enter valid numeric values.",
                    "Loan periods, borrowing limits, pickup days and the fine amount must contain numbers only."
            );

        } catch (IllegalArgumentException exception) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Invalid Settings",
                    exception.getMessage(),
                    "All periods and borrowing limits must be greater than zero."
            );
        }
    }

    private int parsePositiveInteger(
            TextField field,
            String fieldName
    ) {

        String text =
                field.getText() == null
                        ? ""
                        : field.getText().trim();

        if (text.isBlank()) {

            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        int value =
                Integer.parseInt(text);

        if (value <= 0) {

            throw new IllegalArgumentException(
                    fieldName
                            + " must be greater than zero."
            );
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

    private Scene createSettingsUnavailableScene() {

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color:#e8f1f2;"
        );

        Label title =
                new Label(
                        "Settings Unavailable"
                );

        title.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:28px;" +
                "-fx-font-weight:bold;"
        );

        Label message =
                new Label(
                        "Library settings could not be loaded. Please check the database connection and try again."
                );

        message.setWrapText(true);
        message.setMaxWidth(470);
        message.setAlignment(Pos.CENTER);

        message.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:14px;"
        );

        Button backButton =
                createSecondaryButton(
                        "Back to Dashboard"
                );

        backButton.setOnAction(event -> {

            Stage stage =
                    getStage(backButton);

            SceneRouter.open(
                    stage,
                    new AdminDashboardView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Admin Dashboard"
            );
        });

        VBox card =
                new VBox(
                        18,
                        title,
                        message,
                        backButton
                );

        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(45));
        card.setMaxWidth(560);

        card.setStyle(
                "-fx-background-color:#ffffff;" +
                "-fx-background-radius:20;" +
                "-fx-border-color:#cbd5e1;" +
                "-fx-border-radius:20;" +
                "-fx-border-width:1;" +
                "-fx-effect:dropshadow(" +
                "gaussian," +
                "rgba(15,23,42,0.14)," +
                "24," +
                "0.15," +
                "0," +
                "8" +
                ");"
        );

        root.setCenter(card);

        BorderPane.setAlignment(
                card,
                Pos.CENTER
        );

        BorderPane.setMargin(
                card,
                new Insets(40)
        );

        return new Scene(
                root,
                1200,
                760
        );
    }

    private void playEntranceAnimation(
            VBox header,
            VBox card
    ) {

        header.setOpacity(0);
        header.setTranslateY(-15);

        card.setOpacity(0);
        card.setTranslateY(25);

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

        FadeTransition cardFade =
                new FadeTransition(
                        Duration.millis(600),
                        card
                );

        cardFade.setFromValue(0);
        cardFade.setToValue(1);
        cardFade.setDelay(
                Duration.millis(100)
        );

        TranslateTransition cardSlide =
                new TranslateTransition(
                        Duration.millis(600),
                        card
                );

        cardSlide.setFromY(25);
        cardSlide.setToY(0);
        cardSlide.setDelay(
                Duration.millis(100)
        );

        cardSlide.setInterpolator(
                Interpolator.EASE_OUT
        );

        headerFade.play();
        headerSlide.play();
        cardFade.play();
        cardSlide.play();
    }

    private Stage getStage(
            Button button
    ) {

        return (Stage) button
                .getScene()
                .getWindow();
    }
}