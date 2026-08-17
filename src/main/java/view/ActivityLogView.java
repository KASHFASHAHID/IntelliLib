package view;

import controller.ActivityLogController;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.ActivityLog;
import model.Role;
import model.User;
import util.SceneRouter;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ActivityLogView {

    private final User loggedInUser;
    private final ActivityLogController controller;

    private Label recordCountLabel;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(
                    "dd MMM yyyy, hh:mm a"
            );

    public ActivityLogView(
            User loggedInUser
    ) {
        this.loggedInUser = loggedInUser;
        this.controller = new ActivityLogController();
    }

    public Scene createScene() {

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color:#e8f1f2;"
        );

        VBox header =
                createHeader();

        TableView<ActivityLog> table =
                createActivityLogTable();

        VBox content =
                createContent(table);

        root.setTop(header);
        root.setCenter(content);

        loadLogs(table);

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
                new Label(
                        "Activity Logs"
                );

        title.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        Label subtitle =
                new Label(
                        "Review recent system, security and user activity."
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

        Button refreshButton =
                createPrimaryButton(
                        "Refresh"
                );

        Button backButton =
                createSecondaryButton(
                        "Back to Dashboard"
                );

        refreshButton.setOnAction(event -> {

            TableView<ActivityLog> table =
                    (TableView<ActivityLog>)
                            refreshButton
                                    .getProperties()
                                    .get("activityTable");

            if (table != null) {
                loadLogs(table);
            }
        });

        backButton.setOnAction(event ->
                returnToDashboard(
                        backButton
                )
        );

        HBox actions =
                new HBox(
                        12,
                        refreshButton,
                        backButton
                );

        actions.setAlignment(
                Pos.CENTER_RIGHT
        );

        HBox headerRow =
                new HBox(
                        20,
                        headingBox,
                        spacer,
                        actions
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

        header.getProperties().put(
                "refreshButton",
                refreshButton
        );

        return header;
    }

    private VBox createContent(
            TableView<ActivityLog> table
    ) {

        recordCountLabel =
                new Label();

        recordCountLabel.setStyle(
                "-fx-background-color:#ccfbf1;" +
                "-fx-text-fill:#115e59;" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:8 14;" +
                "-fx-background-radius:18;"
        );

        Label tableTitle =
                new Label(
                        "System Activity History"
                );

        tableTitle.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:18px;" +
                "-fx-font-weight:bold;"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox tableHeader =
                new HBox(
                        15,
                        tableTitle,
                        spacer,
                        recordCountLabel
                );

        tableHeader.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox tableCard =
                new VBox(
                        16,
                        tableHeader,
                        table
                );

        tableCard.setPadding(
                new Insets(22)
        );

        tableCard.setStyle(
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

        VBox.setVgrow(
                table,
                Priority.ALWAYS
        );

        VBox content =
                new VBox(tableCard);

        content.setPadding(
                new Insets(
                        28,
                        35,
                        35,
                        35
                )
        );

        VBox.setVgrow(
                tableCard,
                Priority.ALWAYS
        );

        return content;
    }

    private TableView<ActivityLog>
    createActivityLogTable() {

        TableView<ActivityLog> table =
                new TableView<>();

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );

        table.setPlaceholder(
                new Label(
                        "No activity logs found."
                )
        );

        table.setStyle(
                "-fx-background-color:#ffffff;" +
                "-fx-border-color:transparent;" +
                "-fx-background-radius:12;"
        );

        table.setRowFactory(tableView ->
                new TableRow<>() {

                    @Override
                    protected void updateItem(
                            ActivityLog item,
                            boolean empty
                    ) {

                        super.updateItem(
                                item,
                                empty
                        );

                        if (empty || item == null) {

                            setStyle("");
                            return;
                        }

                        if (getIndex() % 2 == 0) {

                            setStyle(
                                    "-fx-background-color:#ffffff;"
                            );

                        } else {

                            setStyle(
                                    "-fx-background-color:#f8fafc;"
                            );
                        }
                    }
                }
        );

        TableColumn<ActivityLog, String> idColumn =
                new TableColumn<>(
                        "Log ID"
                );

        idColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.valueOf(
                                data.getValue()
                                        .getLogId()
                        )
                )
        );

        TableColumn<ActivityLog, String> userColumn =
                new TableColumn<>(
                        "User ID"
                );

        userColumn.setCellValueFactory(data -> {

            String userId =
                    data.getValue()
                            .getUserId();

            return new SimpleStringProperty(
                    userId == null
                            || userId.isBlank()
                            ? "SYSTEM"
                            : userId
            );
        });

        TableColumn<ActivityLog, String> actionColumn =
                new TableColumn<>(
                        "Action"
                );

        actionColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        safeValue(
                                data.getValue()
                                        .getAction()
                        )
                )
        );

        styleActionColumn(
                actionColumn
        );

        TableColumn<ActivityLog, String> detailsColumn =
                new TableColumn<>(
                        "Details"
                );

        detailsColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        safeValue(
                                data.getValue()
                                        .getDetails()
                        )
                )
        );

        TableColumn<ActivityLog, String> timeColumn =
                new TableColumn<>(
                        "Date and Time"
                );

        timeColumn.setCellValueFactory(data -> {

            if (data.getValue()
                    .getCreatedAt() == null) {

                return new SimpleStringProperty("-");
            }

            return new SimpleStringProperty(
                    data.getValue()
                            .getCreatedAt()
                            .format(
                                    DATE_FORMATTER
                            )
            );
        });

        idColumn.setMinWidth(75);
        userColumn.setMinWidth(150);
        actionColumn.setMinWidth(170);
        detailsColumn.setMinWidth(390);
        timeColumn.setMinWidth(190);

        table.getColumns().addAll(
                idColumn,
                userColumn,
                actionColumn,
                detailsColumn,
                timeColumn
        );

        return table;
    }

    private void styleActionColumn(
            TableColumn<ActivityLog, String> column
    ) {

        column.setCellFactory(tableColumn ->
                new TableCell<>() {

                    @Override
                    protected void updateItem(
                            String action,
                            boolean empty
                    ) {

                        super.updateItem(
                                action,
                                empty
                        );

                        if (empty || action == null) {

                            setText(null);
                            setStyle("");
                            return;
                        }

                        String normalized =
                                action.trim()
                                        .toUpperCase();

                        setText(
                                normalized.replace(
                                        "_",
                                        " "
                                )
                        );

                        setAlignment(
                                Pos.CENTER
                        );

                        if (normalized.contains(
                                "LOGIN_SUCCESS"
                        )) {

                            setStyle(
                                    "-fx-text-fill:#15803d;" +
                                    "-fx-font-weight:bold;"
                            );

                        } else if (normalized.contains(
                                "LOGOUT"
                        )) {

                            setStyle(
                                    "-fx-text-fill:#0369a1;" +
                                    "-fx-font-weight:bold;"
                            );

                        } else if (normalized.contains(
                                "BLOCK"
                        )
                                || normalized.contains(
                                "SUSPEND"
                        )
                                || normalized.contains(
                                "FAILED"
                        )) {

                            setStyle(
                                    "-fx-text-fill:#dc2626;" +
                                    "-fx-font-weight:bold;"
                            );

                        } else if (normalized.contains(
                                "UPDATE"
                        )
                                || normalized.contains(
                                "CREATE"
                        )
                                || normalized.contains(
                                "APPROVE"
                        )) {

                            setStyle(
                                    "-fx-text-fill:#0f766e;" +
                                    "-fx-font-weight:bold;"
                            );

                        } else {

                            setStyle(
                                    "-fx-text-fill:#475569;" +
                                    "-fx-font-weight:bold;"
                            );
                        }
                    }
                }
        );
    }

    private void loadLogs(
            TableView<ActivityLog> table
    ) {

        List<ActivityLog> logs =
                controller.getAllLogs();

        table.setItems(
                FXCollections.observableArrayList(
                        logs
                )
        );

        updateRecordCount(
                table.getItems().size()
        );

        Object refreshObject =
                table.getScene() == null
                        ? null
                        : table.getScene()
                                .getRoot()
                                .lookup(".button");

        table.sceneProperty().addListener(
                (
                        observable,
                        oldScene,
                        newScene
                ) -> {

                    if (newScene == null) {
                        return;
                    }

                    BorderPane root =
                            (BorderPane) newScene
                                    .getRoot();

                    if (root.getTop()
                            instanceof VBox header) {

                        Object buttonObject =
                                header
                                        .getProperties()
                                        .get(
                                                "refreshButton"
                                        );

                        if (buttonObject
                                instanceof Button refreshButton) {

                            refreshButton
                                    .getProperties()
                                    .put(
                                            "activityTable",
                                            table
                                    );
                        }
                    }
                }
        );
    }

    private void updateRecordCount(
            int count
    ) {

        if (recordCountLabel == null) {
            return;
        }

        recordCountLabel.setText(
                count
                        + (
                        count == 1
                                ? " activity recorded"
                                : " activities recorded"
                )
        );
    }

    private void returnToDashboard(
        Button backButton
) {

    Stage stage =
            getStage(backButton);

    if (loggedInUser.getRole()
            == Role.SUPER_ADMIN) {

        SceneRouter.open(
                stage,
                new SuperAdminDashboardView(
                        loggedInUser
                ).createScene(),
                "IntelliLib - Super Admin Dashboard"
        );

    } else if (loggedInUser.getRole()
            == Role.LIBRARIAN) {

        SceneRouter.open(
                stage,
                new LibrarianDashboardView(
                        loggedInUser
                ).createScene(),
                "IntelliLib - Librarian Dashboard"
        );

    } else {

        SceneRouter.open(
                stage,
                new AdminDashboardView(
                        loggedInUser
                ).createScene(),
                "IntelliLib - Admin Dashboard"
        );
    }
}

    private Button createPrimaryButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefWidth(125);
        button.setPrefHeight(44);
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

    private String safeValue(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            return "-";
        }

        return value;
    }

    private Stage getStage(
            Button button
    ) {

        return (Stage) button
                .getScene()
                .getWindow();
    }
}