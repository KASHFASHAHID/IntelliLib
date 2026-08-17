package view;

import controller.ReadyForPickupController;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.ReadyForPickup;
import model.Role;
import model.User;
import util.SceneRouter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReadyForPickupView {

    private final User loggedInUser;
    private final ReadyForPickupController controller;

    private Label recordCountLabel;

    public ReadyForPickupView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.controller = new ReadyForPickupController();
    }

    public Scene createScene() {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color:#e8f1f2;"
        );

        VBox header = createHeader();

        TableView<ReadyForPickup> table =
                createTable();

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
                new Label("Ready for Pickup");

        title.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        Label subtitle =
                new Label(
                        "Issue reserved books that are currently available for collection."
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

        Region spacer = new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button backButton =
                createSecondaryButton(
                        "Back to Dashboard"
                );

        backButton.setOnAction(event ->
                returnToDashboard(backButton)
        );

        HBox headerRow =
                new HBox(
                        20,
                        headingBox,
                        spacer,
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
            TableView<ReadyForPickup> table
    ) {

        Label tableTitle =
                new Label(
                        "Pickup Queue"
                );

        tableTitle.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:18px;" +
                "-fx-font-weight:bold;"
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

        Region spacer = new Region();

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

        Button issueButton =
                createPrimaryButton(
                        "Issue Selected Book"
                );

        issueButton.setDisable(true);

        table.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (
                                observable,
                                oldReservation,
                                selectedReservation
                        ) -> issueButton.setDisable(
                                selectedReservation == null
                        )
                );

        issueButton.setOnAction(event ->
                issueSelectedReservation(
                        table,
                        issueButton
                )
        );

        HBox actionRow =
                new HBox(issueButton);

        actionRow.setAlignment(
                Pos.CENTER_RIGHT
        );

        VBox tableCard =
                new VBox(
                        16,
                        tableHeader,
                        table,
                        actionRow
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

    private TableView<ReadyForPickup> createTable() {

        TableView<ReadyForPickup> table =
                new TableView<>();

        table.setPlaceholder(
                new Label(
                        "No reservations are currently ready for pickup."
                )
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
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
                            ReadyForPickup item,
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

                        if (isSelected()) {

                            setStyle(
                                    "-fx-background-color:#ccfbf1;"
                            );

                        } else if (getIndex() % 2 == 0) {

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

        TableColumn<ReadyForPickup, Integer>
                reservationIdColumn =
                createColumn(
                        "Reservation ID",
                        "reservationId"
                );

        TableColumn<ReadyForPickup, String>
                userIdColumn =
                createColumn(
                        "User ID",
                        "userId"
                );

        TableColumn<ReadyForPickup, String>
                memberNameColumn =
                createColumn(
                        "Member Name",
                        "memberName"
                );

        TableColumn<ReadyForPickup, String>
                isbnColumn =
                createColumn(
                        "ISBN",
                        "isbn"
                );

        TableColumn<ReadyForPickup, String>
                bookTitleColumn =
                createColumn(
                        "Book Title",
                        "bookTitle"
                );

        TableColumn<ReadyForPickup, Integer>
                queuePositionColumn =
                createColumn(
                        "Queue Position",
                        "queuePosition"
                );

        TableColumn<ReadyForPickup, LocalDate>
                pickupExpiryColumn =
                createDateColumn(
                        "Pickup Before",
                        "pickupExpiryDate"
                );

        reservationIdColumn.setMinWidth(120);
        userIdColumn.setMinWidth(145);
        memberNameColumn.setMinWidth(160);
        isbnColumn.setMinWidth(140);
        bookTitleColumn.setMinWidth(210);
        queuePositionColumn.setMinWidth(130);
        pickupExpiryColumn.setMinWidth(140);

        table.getColumns().addAll(
                reservationIdColumn,
                userIdColumn,
                memberNameColumn,
                isbnColumn,
                bookTitleColumn,
                queuePositionColumn,
                pickupExpiryColumn
        );

        return table;
    }

    private <T> TableColumn<ReadyForPickup, T>
    createColumn(
            String heading,
            String property
    ) {

        TableColumn<ReadyForPickup, T> column =
                new TableColumn<>(heading);

        column.setCellValueFactory(
                new PropertyValueFactory<>(property)
        );

        column.setStyle(
                "-fx-alignment:CENTER-LEFT;"
        );

        return column;
    }

    private TableColumn<ReadyForPickup, LocalDate>
    createDateColumn(
            String heading,
            String property
    ) {

        TableColumn<ReadyForPickup, LocalDate> column =
                createColumn(
                        heading,
                        property
                );

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd MMM yyyy"
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
                            return;
                        }

                        setText(
                                date.format(formatter)
                        );
                    }
                }
        );

        return column;
    }

    private void issueSelectedReservation(
            TableView<ReadyForPickup> table,
            Button issueButton
    ) {

        ReadyForPickup selected =
                table.getSelectionModel()
                        .getSelectedItem();

        if (selected == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Reservation Required",
                    "No reservation selected.",
                    "Select a reservation from the table before issuing the book."
            );

            return;
        }

        issueButton.setDisable(true);
        issueButton.setText(
                "Issuing..."
        );

        boolean success =
                controller.issueReservedBook(
                        selected.getReservationId(),
                        selected.getUserId(),
                        selected.getIsbn(),
                        7
                );

        issueButton.setText(
                "Issue Selected Book"
        );

        if (!success) {

            issueButton.setDisable(false);

            showAlert(
                    Alert.AlertType.ERROR,
                    "Issue Failed",
                    "The reserved book could not be issued.",
                    "The reservation may no longer be valid, the copy may be unavailable, or a database error may have occurred."
            );

            return;
        }

        showAlert(
                Alert.AlertType.INFORMATION,
                "Book Issued",
                "The reserved book was issued successfully.",
                "The reservation and loan records have been updated."
        );

        loadReservations(table);
        table.getSelectionModel().clearSelection();
    }

    private void loadReservations(
            TableView<ReadyForPickup> table
    ) {

        table.setItems(
                FXCollections.observableArrayList(
                        controller
                                .getReadyForPickupReservations()
                )
        );

        updateRecordCount(
                table.getItems().size()
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
                                ? " reservation ready"
                                : " reservations ready"
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

        if (loggedInUser.getRole()
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

        button.setPrefWidth(210);
        button.setPrefHeight(46);
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
                button.setStyle(hoverStyle)
        );

        button.setOnMouseExited(event ->
                button.setStyle(normalStyle)
        );
    }

    private void showAlert(
            Alert.AlertType type,
            String title,
            String header,
            String content
    ) {

        Alert alert = new Alert(type);

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