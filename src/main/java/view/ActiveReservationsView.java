package view;

import controller.ActiveReservationController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.ActiveReservation;
import model.Role;
import model.User;
import util.SceneRouter;

import java.time.LocalDate;
import java.util.List;

public class ActiveReservationsView {

    private final User loggedInUser;
    private final ActiveReservationController controller;

    public ActiveReservationsView(
            User loggedInUser
    ) {
        this.loggedInUser = loggedInUser;
        this.controller = new ActiveReservationController();
    }

    public Scene createScene() {

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color:#0f172a;"
        );

        TableView<ActiveReservation> table =
                createReservationsTable();

        VBox content =
                new VBox(
                        20,
                        createSummaryLabel(table),
                        table
                );

        content.setPadding(
                new Insets(
                        25,
                        30,
                        30,
                        30
                )
        );

        VBox.setVgrow(
                table,
                Priority.ALWAYS
        );

        root.setTop(
                createHeader()
        );

        root.setCenter(
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
                        "Active Reservations"
                );

        title.setStyle(
                "-fx-text-fill:white;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        Label subtitle =
                new Label(
                        "Waiting and ready-for-pickup reservations"
                );

        subtitle.setStyle(
                "-fx-text-fill:#94a3b8;" +
                "-fx-font-size:15px;"
        );

        VBox titleBox =
                new VBox(
                        6,
                        title,
                        subtitle
                );

        Button refreshButton =
                new Button(
                        "Refresh"
                );

        Button readyForPickupButton =
                new Button(
                        "Ready for Pickup"
                );

        Button backButton =
                new Button(
                        "Back to Dashboard"
                );

        stylePrimaryButton(
                refreshButton
        );

        stylePrimaryButton(
                readyForPickupButton
        );

        styleSecondaryButton(
                backButton
        );

        refreshButton.setOnAction(event -> {

            Stage stage =
                    getStage(
                            refreshButton
                    );

            SceneRouter.open(
                    stage,
                    new ActiveReservationsView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib- Active Reservations"
            );
        });

        readyForPickupButton.setOnAction(event -> {

            Stage stage =
                    getStage(
                            readyForPickupButton
                    );

            SceneRouter.open(
                    stage,
                    new ReadyForPickupView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Ready for Pickup"
            );
        });

        backButton.setOnAction(event -> {

            Stage stage =
                    getStage(
                            backButton
                    );

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
        });

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox headerRow =
                new HBox(
                        15,
                        titleBox,
                        spacer,
                        refreshButton,
                        readyForPickupButton,
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
                        30,
                        25,
                        30
                )
        );

        header.setStyle(
                "-fx-background-color:#111827;" +
                "-fx-border-color:transparent transparent #334155 transparent;" +
                "-fx-border-width:0 0 1 0;"
        );

        return header;
    }

    private TableView<ActiveReservation>
    createReservationsTable() {

        TableView<ActiveReservation> table =
                new TableView<>();

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        table.setPlaceholder(
                new Label(
                        "No active reservations found."
                )
        );

        table.setStyle(
                "-fx-background-color:#111827;" +
                "-fx-border-color:#334155;" +
                "-fx-border-radius:12;" +
                "-fx-background-radius:12;"
        );

        TableColumn<ActiveReservation, Integer>
                reservationIdColumn =
                new TableColumn<>(
                        "Reservation ID"
                );

        reservationIdColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "reservationId"
                )
        );

        TableColumn<ActiveReservation, String>
                userIdColumn =
                new TableColumn<>(
                        "User ID"
                );

        userIdColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "userId"
                )
        );

        TableColumn<ActiveReservation, String>
                memberNameColumn =
                new TableColumn<>(
                        "Member"
                );

        memberNameColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "memberName"
                )
        );

        TableColumn<ActiveReservation, String>
                isbnColumn =
                new TableColumn<>(
                        "ISBN"
                );

        isbnColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "isbn"
                )
        );

        TableColumn<ActiveReservation, String>
                bookTitleColumn =
                new TableColumn<>(
                        "Book Title"
                );

        bookTitleColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "bookTitle"
                )
        );

        TableColumn<ActiveReservation, String>
                statusColumn =
                new TableColumn<>(
                        "Status"
                );

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "status"
                )
        );

        TableColumn<ActiveReservation, LocalDate>
                reservationDateColumn =
                new TableColumn<>(
                        "Reserved On"
                );

        reservationDateColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "reservationDate"
                )
        );

        TableColumn<ActiveReservation, Integer>
                queuePositionColumn =
                new TableColumn<>(
                        "Queue Position"
                );

        queuePositionColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "queuePosition"
                )
        );

        TableColumn<ActiveReservation, LocalDate>
                pickupExpiryColumn =
                new TableColumn<>(
                        "Pickup Expiry"
                );

        pickupExpiryColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "pickupExpiryDate"
                )
        );

        TableColumn<ActiveReservation, String>
                notificationColumn =
                new TableColumn<>(
                        "Notification"
                );

        notificationColumn.setCellValueFactory(
                cellData -> {

                    boolean sent =
                            cellData.getValue()
                                    .isNotificationSent();

                    return new SimpleStringProperty(
                            sent
                                    ? "Sent"
                                    : "Not Sent"
                    );
                }
        );

        table.getColumns().addAll(
                reservationIdColumn,
                userIdColumn,
                memberNameColumn,
                isbnColumn,
                bookTitleColumn,
                statusColumn,
                reservationDateColumn,
                queuePositionColumn,
                pickupExpiryColumn,
                notificationColumn
        );

        List<ActiveReservation> reservations =
                controller
                        .getAllActiveReservations();

        ObservableList<ActiveReservation> data =
                FXCollections.observableArrayList(
                        reservations
                );

        table.setItems(
                data
        );

        return table;
    }

    private Label createSummaryLabel(
            TableView<ActiveReservation> table
    ) {

        long waitingCount =
                table.getItems()
                        .stream()
                        .filter(reservation ->
                                "WAITING".equalsIgnoreCase(
                                        reservation.getStatus()
                                )
                        )
                        .count();

        long readyCount =
                table.getItems()
                        .stream()
                        .filter(reservation ->
                                "READY_FOR_PICKUP"
                                        .equalsIgnoreCase(
                                                reservation
                                                        .getStatus()
                                        )
                        )
                        .count();

        Label summaryLabel =
                new Label(
                        "Total Active Reservations: "
                                + table.getItems().size()
                                + "   |   Waiting: "
                                + waitingCount
                                + "   |   Ready for Pickup: "
                                + readyCount
                );

        summaryLabel.setStyle(
                "-fx-text-fill:#38bdf8;" +
                "-fx-font-size:17px;" +
                "-fx-font-weight:bold;"
        );

        return summaryLabel;
    }

    private void stylePrimaryButton(
            Button button
    ) {

        button.setPrefHeight(42);
        button.setMinWidth(120);

        button.setStyle(
                "-fx-background-color:#2563eb;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:9;"
        );
    }

    private void styleSecondaryButton(
            Button button
    ) {

        button.setPrefWidth(170);
        button.setPrefHeight(42);

        button.setStyle(
                "-fx-background-color:#334155;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:9;"
        );
    }

    private Stage getStage(
            Button button
    ) {

        return (Stage) button
                .getScene()
                .getWindow();
    }
}