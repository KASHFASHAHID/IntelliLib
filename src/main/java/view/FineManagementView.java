package view;

import controller.FineRecordController;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.FineRecord;
import model.Role;
import model.User;
import util.SceneRouter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class FineManagementView {

    private final User loggedInUser;
    private final FineRecordController controller;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    public FineManagementView(
            User loggedInUser
    ) {

        this.loggedInUser = loggedInUser;
        this.controller = new FineRecordController();
    }

    public Scene createScene() {

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color:#e8f1f2;"
        );

        root.setTop(
                createHeader()
        );

        VBox centerContent =
                createMainContent();

        root.setCenter(
                centerContent
        );

        playEntranceAnimation(
                centerContent
        );

        return new Scene(
                root,
                1200,
                760
        );
    }

    private VBox createHeader() {

        Text title =
                new Text(
                        "IntelliLib"
                );

        title.setStyle(
                "-fx-fill:#0f172a;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        Label pageLabel =
                new Label(
                        "Fine Management"
                );

        pageLabel.setStyle(
                "-fx-text-fill:#0f766e;" +
                "-fx-font-size:17px;" +
                "-fx-font-weight:bold;"
        );

        Label description =
                new Label(
                        "Review outstanding fines and record member payments."
                );

        description.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:14px;"
        );

        VBox information =
                new VBox(
                        5,
                        pageLabel,
                        description
                );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label roleLabel =
                new Label(
                        loggedInUser.getRole() == Role.SUPER_ADMIN
                                ? "SUPER ADMIN"
                                : loggedInUser.getRole() == Role.ADMIN
                                ? "ADMIN"
                                : "LIBRARIAN"
                );

        roleLabel.setStyle(
                "-fx-background-color:#dcfce7;" +
                "-fx-text-fill:#166534;" +
                "-fx-font-size:12px;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:9 16;" +
                "-fx-background-radius:18;" +
                "-fx-border-color:#86efac;" +
                "-fx-border-radius:18;"
        );

        HBox headerRow =
                new HBox(
                        20,
                        information,
                        spacer,
                        roleLabel
                );

        headerRow.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox header =
                new VBox(
                        8,
                        title,
                        headerRow
                );

        header.setPadding(
                new Insets(
                        22,
                        35,
                        22,
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

    private VBox createMainContent() {

        Text heading =
                new Text(
                        "Fine Management"
                );

        heading.setStyle(
                "-fx-fill:#0f172a;" +
                "-fx-font-size:28px;" +
                "-fx-font-weight:bold;"
        );

        Label subtitle =
                new Label(
                        "Search, review and manage library fine records."
                );

        subtitle.setStyle(
                "-fx-text-fill:#475569;" +
                "-fx-font-size:15px;"
        );

        TextField searchField =
                new TextField();

        searchField.setPromptText(
                "Search by User ID, Member, Book or Status..."
        );

        searchField.setPrefHeight(42);

        searchField.setMaxWidth(
                Double.MAX_VALUE
        );

        searchField.setStyle(
                "-fx-background-color:white;" +
                "-fx-text-fill:#0f172a;" +
                "-fx-prompt-text-fill:#94a3b8;" +
                "-fx-font-size:14px;" +
                "-fx-background-radius:10;" +
                "-fx-border-color:#cbd5e1;" +
                "-fx-border-radius:10;" +
                "-fx-border-width:1;"
        );

        Button searchButton =
                createPrimaryButton(
                        "🔍  Search"
                );

        searchButton.setPrefWidth(125);

        HBox searchBox =
                new HBox(
                        12,
                        searchField,
                        searchButton
                );

        HBox.setHgrow(
                searchField,
                Priority.ALWAYS
        );

        searchBox.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox searchCard =
                new VBox(
                        12,
                        new Label("Search Fine Records"),
                        searchBox
                );

        Label searchTitle =
                (Label) searchCard.getChildren().get(0);

        searchTitle.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;"
        );

        searchCard.setPadding(
                new Insets(18)
        );

        searchCard.setStyle(
                "-fx-background-color:#f8fafc;" +
                "-fx-background-radius:16;" +
                "-fx-border-color:#b6d4d6;" +
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

        TableView<FineRecord> table =
                createFineTable();

        refreshTable(
                table,
                ""
        );

        searchButton.setOnAction(event ->
                refreshTable(
                        table,
                        searchField.getText()
                )
        );

        searchField.setOnAction(event ->
                refreshTable(
                        table,
                        searchField.getText()
                )
        );

        VBox tableCard =
                new VBox(
                        table
                );

        VBox.setVgrow(
                table,
                Priority.ALWAYS
        );

        tableCard.setPadding(
                new Insets(12)
        );

        tableCard.setStyle(
                "-fx-background-color:#f8fafc;" +
                "-fx-background-radius:16;" +
                "-fx-border-color:#b6d4d6;" +
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

        Button paymentButton =
                createPaymentButton(
                        "₹  Record Payment"
                );

        paymentButton.setDisable(true);

        Button backButton =
                createSecondaryButton(
                        "←  Back to Dashboard"
                );

        table.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (
                                observable,
                                oldFine,
                                selectedFine
                        ) -> {

                            boolean disable =
                                    selectedFine == null
                                            || "PAID".equalsIgnoreCase(
                                            selectedFine.getStatus()
                                    );

                            paymentButton.setDisable(
                                    disable
                            );
                        }
                );

        paymentButton.setOnAction(event ->
                handlePayment(
                        table,
                        searchField
                )
        );

        backButton.setOnAction(event ->
                returnToDashboard(
                        backButton
                )
        );

        HBox bottomButtons =
                new HBox(
                        12,
                        paymentButton,
                        backButton
                );

        bottomButtons.setAlignment(
                Pos.CENTER_RIGHT
        );

        VBox content =
                new VBox(
                        12,
                        heading,
                        subtitle,
                        searchCard,
                        tableCard,
                        bottomButtons
                );

        VBox.setVgrow(
                tableCard,
                Priority.ALWAYS
        );

        content.setPadding(
                new Insets(
                        28,
                        35,
                        25,
                        35
                )
        );

        content.setStyle(
                "-fx-background-color:#e8f1f2;"
        );

        return content;
    }

    private TableView<FineRecord> createFineTable() {

        TableView<FineRecord> table =
                new TableView<>();

        table.setStyle(
                "-fx-background-color:#f8fafc;" +
                "-fx-border-color:transparent;" +
                "-fx-font-size:13px;"
        );

        TableColumn<FineRecord, Integer>
                fineIdColumn =
                new TableColumn<>("Fine ID");

        fineIdColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "fineId"
                )
        );

        TableColumn<FineRecord, String>
                userIdColumn =
                new TableColumn<>("User ID");

        userIdColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "userId"
                )
        );

        TableColumn<FineRecord, String>
                memberColumn =
                new TableColumn<>("Member");

        memberColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "memberName"
                )
        );

        TableColumn<FineRecord, String>
                roleColumn =
                new TableColumn<>("Role");

        roleColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "role"
                )
        );

        TableColumn<FineRecord, String>
                bookColumn =
                new TableColumn<>("Book");

        bookColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "bookTitle"
                )
        );

        TableColumn<FineRecord, String>
                copyColumn =
                new TableColumn<>("Copy Number");

        copyColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "copyNumber"
                )
        );

        TableColumn<FineRecord, BigDecimal>
                amountColumn =
                new TableColumn<>("Amount");

        amountColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "amount"

                )
                );

        TableColumn<FineRecord, String>
                statusColumn =
                new TableColumn<>("Status");

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "status"
                )
        );

        TableColumn<FineRecord, LocalDateTime>
                createdColumn =
                new TableColumn<>("Generated On");

        createdColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "createdAt"
                )
        );

        createdColumn.setCellFactory(
                column ->
                        new javafx.scene.control.TableCell<>() {

                            @Override
                            protected void updateItem(
                                    LocalDateTime item,
                                    boolean empty
                            ) {

                                super.updateItem(
                                        item,
                                        empty
                                );

                                setText(
                                        empty || item == null
                                                ? ""
                                                : item.format(
                                                DATE_TIME_FORMATTER
                                        )
                                );
                            }
                        }
        );

        TableColumn<FineRecord, LocalDateTime>
                paidColumn =
                new TableColumn<>("Paid On");

        paidColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "paidAt"
                )
        );

        paidColumn.setCellFactory(
                column ->
                        new javafx.scene.control.TableCell<>() {

                            @Override
                            protected void updateItem(
                                    LocalDateTime item,
                                    boolean empty
                            ) {

                                super.updateItem(
                                        item,
                                        empty
                                );

                                setText(
                                        empty || item == null
                                                ? "-"
                                                : item.format(
                                                DATE_TIME_FORMATTER
                                        )
                                );
                            }
                        }
        );

        table.getColumns().addAll(
                fineIdColumn,
                userIdColumn,
                memberColumn,
                roleColumn,
                bookColumn,
                copyColumn,
                amountColumn,
                statusColumn,
                createdColumn,
                paidColumn
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        table.setPlaceholder(
                new Label(
                        "No fine records found."
                )
        );

        return table;
    }

    private Button createPrimaryButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefHeight(42);
        button.setCursor(
                Cursor.HAND
        );

        String normal =
                "-fx-background-color:linear-gradient(" +
                "to right,#0f766e,#0891b2);" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;";

        String hover =
                "-fx-background-color:linear-gradient(" +
                "to right,#115e59,#0e7490);" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;";

        applyHoverEffect(
                button,
                normal,
                hover
        );

        return button;
    }

    private Button createPaymentButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefHeight(46);
        button.setPrefWidth(190);
        button.setCursor(
                Cursor.HAND
        );

        String normal =
                "-fx-background-color:#16a34a;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;" +
                "-fx-effect:dropshadow(" +
                "gaussian," +
                "rgba(22,163,74,0.20)," +
                "12," +
                "0.15," +
                "0," +
                "4" +
                ");";

        String hover =
                "-fx-background-color:#15803d;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;" +
                "-fx-effect:dropshadow(" +
                "gaussian," +
                "rgba(22,163,74,0.30)," +
                "16," +
                "0.18," +
                "0," +
                "5" +
                ");";

        applyHoverEffect(
                button,
                normal,
                hover
        );

        return button;
    }

    private Button createSecondaryButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefHeight(46);
        button.setPrefWidth(190);
        button.setCursor(
                Cursor.HAND
        );

        String normal =
                "-fx-background-color:#f1f5f9;" +
                "-fx-text-fill:#475569;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-border-color:#cbd5e1;" +
                "-fx-border-radius:10;" +
                "-fx-background-radius:10;";

        String hover =
                "-fx-background-color:#e2e8f0;" +
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-border-color:#94a3b8;" +
                "-fx-border-radius:10;" +
                "-fx-background-radius:10;";

        applyHoverEffect(
                button,
                normal,
                hover
        );

        return button;
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

    private void playEntranceAnimation(
            VBox content
    ) {

        content.setOpacity(0);
        content.setTranslateY(22);

        FadeTransition fade =
                new FadeTransition(
                        Duration.millis(550),
                        content
                );

        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide =
                new TranslateTransition(
                        Duration.millis(550),
                        content
                );

        slide.setFromY(22);
        slide.setToY(0);

        slide.setInterpolator(
                Interpolator.EASE_OUT
        );

        fade.play();
        slide.play();
    }

    private void handlePayment(
            TableView<FineRecord> table,
            TextField searchField
    ) {

        FineRecord selectedFine =
                table.getSelectionModel()
                        .getSelectedItem();

        if (selectedFine == null) {

            showWarning(
                    "No Fine Selected",
                    "Please select an unpaid fine first."
            );

            return;
        }

        if ("PAID".equalsIgnoreCase(
                selectedFine.getStatus()
        )) {

            showWarning(
                    "Fine Already Paid",
                    "The selected fine has already been paid."
            );

            return;
        }

        ComboBox<String> paymentMethodBox =
                new ComboBox<>();

        paymentMethodBox.setItems(
                FXCollections.observableArrayList(
                        "CASH",
                        "UPI",
                        "CARD",
                        "BANK_TRANSFER"
                )
        );

        paymentMethodBox.setPromptText(
                "Select payment method"
        );

        paymentMethodBox.setPrefWidth(230);

        TextField referenceField =
                new TextField();

        referenceField.setPromptText(
                "Optional for cash payments"
        );

        referenceField.setPrefWidth(230);

        GridPane paymentForm =
                new GridPane();

        paymentForm.setHgap(12);
        paymentForm.setVgap(12);

        paymentForm.add(
                new Label("Member:"),
                0,
                0
        );

        paymentForm.add(
                new Label(
                        selectedFine.getMemberName()
                                + " ("
                                + selectedFine.getUserId()
                                + ")"
                ),
                1,
                0
        );

        paymentForm.add(
                new Label("Book:"),
                0,
                1
        );

        paymentForm.add(
                new Label(
                        selectedFine.getBookTitle()
                ),
                1,
                1
        );

        paymentForm.add(
                new Label("Fine Amount:"),
                0,
                2
        );

        paymentForm.add(
                new Label(
                        "₹" + selectedFine.getAmount()
                ),
                1,
                2
        );

        paymentForm.add(
                new Label("Payment Method:"),
                0,
                3
        );

        paymentForm.add(
                paymentMethodBox,
                1,
                3
        );

        paymentForm.add(
                new Label("Reference:"),
                0,
                4
        );

        paymentForm.add(
                referenceField,
                1,
                4
        );

        Alert paymentDialog =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        paymentDialog.setTitle(
                "Record Fine Payment"
        );

        paymentDialog.setHeaderText(
                "Confirm payment for Fine ID "
                        + selectedFine.getFineId()
        );

        paymentDialog.getDialogPane()
                .setContent(paymentForm);

        Optional<ButtonType> result =
                paymentDialog.showAndWait();

        if (result.isEmpty()
                || result.get() != ButtonType.OK) {

            return;
        }

        String paymentMethod =
                paymentMethodBox.getValue();

        String validationMessage =
                controller.validatePayment(
                        selectedFine.getFineId(),
                        selectedFine.getUserId(),
                        paymentMethod,
                        loggedInUser.getUserId()
                );

        if (validationMessage != null) {

            showWarning(
                    "Invalid Payment",
                    validationMessage
            );

            return;
        }

        String paymentReference =
                referenceField.getText();

        BigDecimal remainingBalance =
                controller.payFine(
                        selectedFine.getFineId(),
                        selectedFine.getUserId(),
                        paymentMethod,
                        paymentReference,
                        loggedInUser.getUserId()
                );

        if (remainingBalance == null) {

            showError(
                    "Payment Failed",
                    "The fine payment could not be recorded. "
                            + "The fine may already be paid."
            );

            return;
        }

        Alert successAlert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        successAlert.setTitle(
                "Payment Recorded"
        );

        successAlert.setHeaderText(
                "The fine was marked as PAID."
        );

        successAlert.setContentText(
                "Fine ID: "
                        + selectedFine.getFineId()
                        + "\nAmount Paid: ₹"
                        + selectedFine.getAmount()
                        + "\nPayment Method: "
                        + paymentMethod
                        + "\nRemaining Member Balance: ₹"
                        + remainingBalance
        );

        successAlert.showAndWait();

        refreshTable(
                table,
                searchField.getText()
        );
    }

    private void refreshTable(
            TableView<FineRecord> table,
            String keyword
    ) {

        table.setItems(
                FXCollections.observableArrayList(
                        controller.getAllFineRecords(
                                keyword == null
                                        ? ""
                                        : keyword.trim()
                        )
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
                == Role.SUPER_ADMIN) {

            SceneRouter.open(
                    stage,
                    new SuperAdminDashboardView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Super Admin Dashboard"
            );

        } else if (loggedInUser.getRole()
                == Role.ADMIN) {

            SceneRouter.open(
                    stage,
                    new AdminDashboardView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Admin Dashboard"
            );

        } else {

            SceneRouter.open(
                    stage,
                    new LibrarianDashboardView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Librarian Dashboard"
            );
        }
    }

    private void showWarning(
            String title,
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.WARNING
                );

        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private void showError(
            String title,
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}