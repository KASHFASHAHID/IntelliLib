package view;

import controller.ReservationController;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Reservation;
import model.Role;
import model.User;
import util.MemberDashboardRouter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyReservationsView {

    private final User loggedInUser;
    private final ReservationController controller;

    private Label recordCountLabel;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(
                    "dd MMM yyyy"
            );

    public MyReservationsView(
            User loggedInUser
    ) {
        this.loggedInUser = loggedInUser;
        this.controller = new ReservationController();
    }

    public Scene createScene() {

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color:#e8f1f2;"
        );

        VBox header =
                createHeader();

        TableView<Reservation> table =
                createReservationsTable();

        VBox content =
                createContent(table);

        root.setTop(header);
        root.setCenter(content);

        loadReservations(table);

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
                        "My Reservations"
                );

        title.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        Label subtitle =
                new Label(
                        "Track reserved books, queue positions and pickup availability."
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
            TableView<Reservation> table
    ) {

        Label tableTitle =
                new Label(
                        "Reservation History"
                );

        tableTitle.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:18px;" +
                "-fx-font-weight:bold;"
        );

        Label instructionLabel =
                new Label(
                        "Reservations marked READY FOR PICKUP should be collected before the expiry date."
                );

        instructionLabel.setWrapText(true);

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

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox tableHeader =
                new HBox(
                        15,
                        titleBox,
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

    private TableView<Reservation>
    createReservationsTable() {

        TableView<Reservation> table =
                new TableView<>();

        table.setPlaceholder(
                new Label(
                        "You currently have no reservations."
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
                            Reservation reservation,
                            boolean empty
                    ) {

                        super.updateItem(
                                reservation,
                                empty
                        );

                        if (empty || reservation == null) {

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

        TableColumn<Reservation, String> bookColumn =
                createColumn(
                        "Book",
                        "title"
                );

        TableColumn<Reservation, String> authorColumn =
                createColumn(
                        "Author",
                        "authors"
                );

        TableColumn<Reservation, Integer> queueColumn =
                createColumn(
                        "Queue Position",
                        "queuePosition"
                );

        styleQueueColumn(
                queueColumn
        );

        TableColumn<Reservation, String> statusColumn =
                createColumn(
                        "Status",
                        "status"
                );

        styleStatusColumn(
                statusColumn
        );

        TableColumn<Reservation, LocalDate> reservationDateColumn =
                createDateColumn(
                        "Reserved On",
                        "reservationDate"
                );

        TableColumn<Reservation, LocalDate> pickupExpiryColumn =
                createDateColumn(
                        "Pickup Before",
                        "pickupExpiryDate"
                );

        bookColumn.setMinWidth(260);
        authorColumn.setMinWidth(230);
        queueColumn.setMinWidth(125);
        statusColumn.setMinWidth(155);
        reservationDateColumn.setMinWidth(140);
        pickupExpiryColumn.setMinWidth(145);

        table.getColumns().addAll(
                bookColumn,
                authorColumn,
                queueColumn,
                statusColumn,
                reservationDateColumn,
                pickupExpiryColumn
        );

        return table;
    }

    private <T> TableColumn<Reservation, T>
    createColumn(
            String heading,
            String property
    ) {

        TableColumn<Reservation, T> column =
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

    private TableColumn<Reservation, LocalDate>
    createDateColumn(
            String heading,
            String property
    ) {

        TableColumn<Reservation, LocalDate> column =
                createColumn(
                        heading,
                        property
                );

        column.setCellFactory(tableColumn ->
                new TableCell<>() {

                    @Override
                    protected void updateItem(
                            LocalDate date,
                            boolean empty
                    ) {

                        super.updateItem(
                                date,
                                empty
                        );

                        if (empty || date == null) {

                            setText("-");
                            setStyle("");
                            return;
                        }

                        setText(
                                date.format(
                                        DATE_FORMATTER
                                )
                        );

                        setStyle(
                                "-fx-text-fill:#334155;"
                        );
                    }
                }
        );

        return column;
    }

    private void styleQueueColumn(
            TableColumn<Reservation, Integer> column
    ) {

        column.setCellFactory(tableColumn ->
                new TableCell<>() {

                    @Override
                    protected void updateItem(
                            Integer queuePosition,
                            boolean empty
                    ) {

                        super.updateItem(
                                queuePosition,
                                empty
                        );

                        if (empty || queuePosition == null) {

                            setText("-");
                            setStyle("");
                            return;
                        }

                        setText(
                                String.valueOf(
                                        queuePosition
                                )
                        );

                        setAlignment(
                                Pos.CENTER
                        );

                        if (queuePosition == 1) {

                            setStyle(
                                    "-fx-text-fill:#15803d;" +
                                    "-fx-font-weight:bold;"
                            );

                        } else {

                            setStyle(
                                    "-fx-text-fill:#0369a1;" +
                                    "-fx-font-weight:bold;"
                            );
                        }
                    }
                }
        );
    }

    private void styleStatusColumn(
            TableColumn<Reservation, String> column
    ) {

        column.setCellFactory(tableColumn ->
                new TableCell<>() {

                    @Override
                    protected void updateItem(
                            String status,
                            boolean empty
                    ) {

                        super.updateItem(
                                status,
                                empty
                        );

                        if (empty || status == null) {

                            setText(null);
                            setStyle("");
                            return;
                        }

                        String normalized =
                                status.trim()
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
                                "READY"
                        )) {

                            setStyle(
                                    "-fx-text-fill:#15803d;" +
                                    "-fx-font-weight:bold;"
                            );

                        } else if (normalized.contains(
                                "WAIT"
                        )
                                || normalized.contains(
                                "PENDING"
                        )) {

                            setStyle(
                                    "-fx-text-fill:#b45309;" +
                                    "-fx-font-weight:bold;"
                            );

                        } else if (normalized.contains(
                                "EXPIRED"
                        )
                                || normalized.contains(
                                "CANCEL"
                        )) {

                            setStyle(
                                    "-fx-text-fill:#dc2626;" +
                                    "-fx-font-weight:bold;"
                            );

                        } else if (normalized.contains(
                                "FULFILLED"
                        )
                                || normalized.contains(
                                "COLLECTED"
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

    private void loadReservations(
            TableView<Reservation> table
    ) {

        List<Reservation> reservations =
                controller.getReservationsByUser(
                        loggedInUser.getUserId()
                );

        table.setItems(
                FXCollections.observableArrayList(
                        reservations
                )
        );

        table.getSelectionModel()
                .clearSelection();

        updateRecordCount(
                reservations.size()
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
                                ? " reservation"
                                : " reservations"
                )
        );
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