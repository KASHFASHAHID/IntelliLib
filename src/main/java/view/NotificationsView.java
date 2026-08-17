package view;

import controller.NotificationController;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Notification;
import model.Role;
import model.User;
import util.MemberDashboardRouter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationsView {

    private final User loggedInUser;
    private final NotificationController controller;

    private Label recordCountLabel;
    private Label unreadCountLabel;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(
                    "dd MMM yyyy, h:mm a"
            );

    public NotificationsView(
            User loggedInUser
    ) {
        this.loggedInUser = loggedInUser;
        this.controller = new NotificationController();
    }

    public Scene createScene() {

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color:#e8f1f2;"
        );

        VBox header =
                createHeader();

        TableView<Notification> table =
                createNotificationTable();

        VBox content =
                createContent(table);

        root.setTop(header);
        root.setCenter(content);

        loadNotifications(table);

        /*
         * Notifications that were New when this page opened
         * remain displayed as New during this visit.
         * They appear as Viewed the next time the page opens.
         */
        controller.markAllAsRead(
                loggedInUser.getUserId()
        );

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
                        "Notifications"
                );

        title.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        Label subtitle =
                new Label(
                        "Review account updates, reservation alerts and library announcements."
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
                        loggedInUser.getRole()
                                == Role.TEACHER
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

        backButton.setOnAction(event ->
                returnToDashboard(
                        backButton
                )
        );

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
                new VBox(
                        headerRow
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

    private VBox createContent(
            TableView<Notification> table
    ) {

        Label tableTitle =
                new Label(
                        "Notification Centre"
                );

        tableTitle.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:18px;" +
                "-fx-font-weight:bold;"
        );

        Label instructionLabel =
                new Label(
                        "Double-click a notification to read its complete message."
                );

        instructionLabel.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:13px;"
        );

        VBox titleBox =
                new VBox(
                        5,
                        tableTitle,
                        instructionLabel
                );

        unreadCountLabel =
                new Label();

        unreadCountLabel.setStyle(
                "-fx-background-color:#dcfce7;" +
                "-fx-text-fill:#166534;" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:8 14;" +
                "-fx-background-radius:18;"
        );

        recordCountLabel =
                new Label();

        recordCountLabel.setStyle(
                "-fx-background-color:#e0f2fe;" +
                "-fx-text-fill:#075985;" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:8 14;" +
                "-fx-background-radius:18;"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox tableHeader =
                new HBox(
                        12,
                        titleBox,
                        spacer,
                        unreadCountLabel,
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
                new VBox(
                        tableCard
                );

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

    private TableView<Notification>
    createNotificationTable() {

        TableView<Notification> table =
                new TableView<>();

        table.setPlaceholder(
                new Label(
                        "You currently have no notifications."
                )
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        table.setStyle(
                "-fx-background-color:#ffffff;" +
                "-fx-border-color:transparent;" +
                "-fx-background-radius:12;" +
                "-fx-selection-bar:#ccfbf1;" +
                "-fx-selection-bar-non-focused:#ccfbf1;" +
                "-fx-selection-bar-text:#0f172a;"
        );

        table.setRowFactory(tableView ->
                new TableRow<>() {

                    @Override
                    protected void updateItem(
                            Notification notification,
                            boolean empty
                    ) {

                        super.updateItem(
                                notification,
                                empty
                        );

                        if (empty
                                || notification == null) {

                            setStyle("");
                            return;
                        }

                        if (isSelected()) {

                            setStyle(
                                    "-fx-background-color:#ccfbf1;" +
                                    "-fx-text-background-color:#0f172a;"
                            );

                        } else if (getIndex() % 2 == 0) {

                            setStyle(
                                    "-fx-background-color:#ffffff;" +
                                    "-fx-text-background-color:#0f172a;"
                            );

                        } else {

                            setStyle(
                                    "-fx-background-color:#f8fafc;" +
                                    "-fx-text-background-color:#0f172a;"
                            );
                        }
                    }
                }
        );

        TableColumn<Notification, String> titleColumn =
                createColumn(
                        "Title",
                        "title"
                );

        TableColumn<Notification, String> messageColumn =
                createColumn(
                        "Message",
                        "message"
                );

        styleMessageColumn(
                messageColumn
        );

        TableColumn<Notification, Boolean> statusColumn =
                createColumn(
                        "Status",
                        "read"
                );

        styleStatusColumn(
                statusColumn
        );

        TableColumn<Notification, LocalDateTime>
                receivedColumn =
                createDateTimeColumn(
                        "Received",
                        "createdAt"
                );

        titleColumn.setMinWidth(230);
        messageColumn.setMinWidth(500);
        statusColumn.setMinWidth(110);
        receivedColumn.setMinWidth(190);

        table.getColumns().addAll(
                titleColumn,
                messageColumn,
                statusColumn,
                receivedColumn
        );

        table.setOnMouseClicked(event -> {

            if (event.getClickCount() != 2) {
                return;
            }

            Notification selectedNotification =
                    table.getSelectionModel()
                            .getSelectedItem();

            if (selectedNotification == null) {
                return;
            }

            showNotificationDetails(
                    selectedNotification
            );
        });

        return table;
    }

    private <T> TableColumn<Notification, T>
    createColumn(
            String heading,
            String property
    ) {

        TableColumn<Notification, T> column =
                new TableColumn<>(
                        heading
                );

        column.setCellValueFactory(
                new PropertyValueFactory<>(
                        property
                )
        );

        column.setStyle(
                "-fx-alignment:CENTER-LEFT;"
        );

        return column;
    }

    private void styleMessageColumn(
            TableColumn<Notification, String> column
    ) {

        column.setCellFactory(tableColumn ->
                new TableCell<>() {

                    @Override
                    protected void updateItem(
                            String message,
                            boolean empty
                    ) {

                        super.updateItem(
                                message,
                                empty
                        );

                        if (empty || message == null) {

                            setText(null);
                            setStyle("");
                            return;
                        }

                        String displayMessage =
                                message.trim();

                        if (displayMessage.length() > 95) {

                            displayMessage =
                                    displayMessage.substring(
                                            0,
                                            95
                                    )
                                            + "...";
                        }

                        setText(displayMessage);

                        setStyle(
                                "-fx-text-fill:#475569;"
                        );
                    }
                }
        );
    }

    private void styleStatusColumn(
            TableColumn<Notification, Boolean> column
    ) {

        column.setCellFactory(tableColumn ->
                new TableCell<>() {

                    @Override
                    protected void updateItem(
                            Boolean isRead,
                            boolean empty
                    ) {

                        super.updateItem(
                                isRead,
                                empty
                        );

                        if (empty || isRead == null) {

                            setText(null);
                            setStyle("");
                            return;
                        }

                        setAlignment(
                                Pos.CENTER
                        );

                        if (isRead) {

                            setText("VIEWED");

                            setStyle(
                                    "-fx-text-fill:#64748b;" +
                                    "-fx-font-weight:bold;"
                            );

                        } else {

                            setText("NEW");

                            setStyle(
                                    "-fx-text-fill:#15803d;" +
                                    "-fx-font-weight:bold;"
                            );
                        }
                    }
                }
        );
    }

    private TableColumn<Notification, LocalDateTime>
    createDateTimeColumn(
            String heading,
            String property
    ) {

        TableColumn<Notification, LocalDateTime> column =
                createColumn(
                        heading,
                        property
                );

        column.setCellFactory(tableColumn ->
                new TableCell<>() {

                    @Override
                    protected void updateItem(
                            LocalDateTime dateTime,
                            boolean empty
                    ) {

                        super.updateItem(
                                dateTime,
                                empty
                        );

                        if (empty || dateTime == null) {

                            setText("-");
                            setStyle("");
                            return;
                        }

                        setText(
                                dateTime.format(
                                        DATE_FORMATTER
                                )
                        );

                        setStyle(
                                "-fx-text-fill:#475569;"
                        );
                    }
                }
        );

        return column;
    }

    private void loadNotifications(
            TableView<Notification> table
    ) {

        List<Notification> notifications =
                controller.getNotificationsByUser(
                        loggedInUser.getUserId()
                );

        table.setItems(
                FXCollections.observableArrayList(
                        notifications
                )
        );

        table.getSelectionModel()
                .clearSelection();

        updateRecordCount(
                notifications.size()
        );

        updateUnreadCount(
                notifications
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
                                ? " notification"
                                : " notifications"
                )
        );
    }

    private void updateUnreadCount(
            List<Notification> notifications
    ) {

        if (unreadCountLabel == null) {
            return;
        }

        long unreadCount =
                notifications.stream()
                        .filter(notification ->
                                notification != null
                                        && !notification.isRead()
                        )
                        .count();

        unreadCountLabel.setText(
                unreadCount
                        + (
                        unreadCount == 1
                                ? " new"
                                : " new"
                )
        );
    }

    private void showNotificationDetails(
            Notification notification
    ) {

        TextArea messageArea =
                new TextArea(
                        notification.getMessage() == null
                                ? ""
                                : notification.getMessage()
                );

        messageArea.setEditable(false);
        messageArea.setWrapText(true);
        messageArea.setPrefWidth(540);
        messageArea.setPrefHeight(270);

        messageArea.setStyle(
                "-fx-control-inner-background:#f8fafc;" +
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:14px;" +
                "-fx-border-color:#cbd5e1;" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;"
        );

        Alert detailsAlert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        detailsAlert.setTitle(
                "Notification Details"
        );

        detailsAlert.setHeaderText(
                notification.getTitle() == null
                        ? "Notification"
                        : notification.getTitle()
        );

        detailsAlert.getDialogPane()
                .setContent(messageArea);

        detailsAlert.getDialogPane()
                .setPrefWidth(610);

        detailsAlert.showAndWait();
    }

    private void returnToDashboard(
            Button backButton
    ) {

        Stage stage =
                (Stage) backButton
                        .getScene()
                        .getWindow();

        MemberDashboardRouter.openDashboard(
                stage,
                loggedInUser
        );
    }

    private Button createSecondaryButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefWidth(180);
        button.setPrefHeight(44);
        button.setCursor(
                Cursor.HAND
        );

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

        return button;
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