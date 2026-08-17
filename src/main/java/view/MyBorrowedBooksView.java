package view;

import controller.BorrowedBooksController;
import controller.ReturnController;
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
import javafx.scene.control.ButtonType;
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
import model.BorrowedBook;
import model.Role;
import model.User;
import util.MemberDashboardRouter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class MyBorrowedBooksView {

    private final User loggedInUser;
    private final BorrowedBooksController controller;
    private final ReturnController returnController;

    private Label recordCountLabel;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    public MyBorrowedBooksView(
            User loggedInUser
    ) {
        this.loggedInUser = loggedInUser;
        this.controller = new BorrowedBooksController();
        this.returnController = new ReturnController();
    }

    public Scene createScene() {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color:#e8f1f2;"
        );

        VBox header = createHeader();

        TableView<BorrowedBook> table =
                createBorrowedBooksTable();

        VBox content =
                createContent(table);

        root.setTop(header);
        root.setCenter(content);

        loadBorrowedBooks(table);

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
                new Label("My Borrowed Books");

        title.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        Label subtitle =
                new Label(
                        "Review your active loans, due dates, renewals and returns."
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
                returnToDashboard(backButton)
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
            TableView<BorrowedBook> table
    ) {

        Label tableTitle =
                new Label(
                        "Current Loans"
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

        Region tableSpacer =
                new Region();

        HBox.setHgrow(
                tableSpacer,
                Priority.ALWAYS
        );

        HBox tableHeader =
                new HBox(
                        15,
                        tableTitle,
                        tableSpacer,
                        recordCountLabel
                );

        tableHeader.setAlignment(
                Pos.CENTER_LEFT
        );

        Button renewButton =
                createPrimaryButton(
                        "Renew Selected Book"
                );

        Button returnButton =
                createSuccessButton(
                        "Return Selected Book"
                );

        renewButton.setDisable(true);
        returnButton.setDisable(true);

        table.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (
                                observable,
                                previousBook,
                                selectedBook
                        ) -> {

                            boolean nothingSelected =
                                    selectedBook == null;

                            renewButton.setDisable(
                                    loggedInUser.isSuspended()
                                            || nothingSelected
                            );

                            returnButton.setDisable(
                                    nothingSelected
                            );
                        }
                );

        renewButton.setOnAction(event ->
                handleRenewal(table)
        );

        returnButton.setOnAction(event ->
                handleReturn(table)
        );

        Region actionSpacer =
                new Region();

        HBox.setHgrow(
                actionSpacer,
                Priority.ALWAYS
        );

        HBox actionRow =
                new HBox(
                        12,
                        actionSpacer,
                        renewButton,
                        returnButton
                );

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

    private TableView<BorrowedBook>
    createBorrowedBooksTable() {

        TableView<BorrowedBook> table =
                new TableView<>();

        table.setPlaceholder(
                new Label(
                        "You currently have no borrowed books."
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
                            BorrowedBook book,
                            boolean empty
                    ) {

                        super.updateItem(
                                book,
                                empty
                        );

                        if (empty || book == null) {

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

        TableColumn<BorrowedBook, String> titleColumn =
                createColumn(
                        "Book Title",
                        "title"
                );

        TableColumn<BorrowedBook, String> authorsColumn =
                createColumn(
                        "Author",
                        "authors"
                );

        TableColumn<BorrowedBook, String> copyColumn =
                createColumn(
                        "Copy No.",
                        "copyNumber"
                );

        TableColumn<BorrowedBook, LocalDate> issueDateColumn =
                createDateColumn(
                        "Issue Date",
                        "issueDate"
                );

        TableColumn<BorrowedBook, LocalDate> dueDateColumn =
                createDateColumn(
                        "Due Date",
                        "dueDate"
                );

        TableColumn<BorrowedBook, String> statusColumn =
                createColumn(
                        "Status",
                        "status"
                );

        styleStatusColumn(
                statusColumn
        );

        TableColumn<BorrowedBook, Long> daysLeftColumn =
                createColumn(
                        "Days Left",
                        "daysLeft"
                );

        styleDaysLeftColumn(
                daysLeftColumn
        );

        TableColumn<BorrowedBook, Integer> renewalCountColumn =
                createColumn(
                        "Renewals",
                        "renewalCount"
                );

        titleColumn.setMinWidth(220);
        authorsColumn.setMinWidth(200);
        copyColumn.setMinWidth(95);
        issueDateColumn.setMinWidth(120);
        dueDateColumn.setMinWidth(120);
        statusColumn.setMinWidth(115);
        daysLeftColumn.setMinWidth(90);
        renewalCountColumn.setMinWidth(90);

        table.getColumns().addAll(
                titleColumn,
                authorsColumn,
                copyColumn,
                issueDateColumn,
                dueDateColumn,
                statusColumn,
                daysLeftColumn,
                renewalCountColumn
        );

        return table;
    }

    private <T> TableColumn<BorrowedBook, T> createColumn(
            String heading,
            String property
    ) {

        TableColumn<BorrowedBook, T> column =
                new TableColumn<>(heading);

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

    private TableColumn<BorrowedBook, LocalDate>
    createDateColumn(
            String heading,
            String property
    ) {

        TableColumn<BorrowedBook, LocalDate> column =
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
                            return;
                        }

                        setText(
                                date.format(
                                        DATE_FORMATTER
                                )
                        );
                    }
                }
        );

        return column;
    }

    private void styleStatusColumn(
            TableColumn<BorrowedBook, String> column
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

                        if (normalized.contains("OVERDUE")) {

                            setStyle(
                                    "-fx-text-fill:#dc2626;" +
                                    "-fx-font-weight:bold;"
                            );

                        } else if (normalized.contains("ACTIVE")
                                || normalized.contains("BORROWED")) {

                            setStyle(
                                    "-fx-text-fill:#15803d;" +
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

    private void styleDaysLeftColumn(
            TableColumn<BorrowedBook, Long> column
    ) {

        column.setCellFactory(tableColumn ->
                new TableCell<>() {

                    @Override
                    protected void updateItem(
                            Long daysLeft,
                            boolean empty
                    ) {

                        super.updateItem(
                                daysLeft,
                                empty
                        );

                        if (empty || daysLeft == null) {

                            setText(null);
                            setStyle("");
                            return;
                        }

                        setText(
                                String.valueOf(daysLeft)
                        );

                        setAlignment(
                                Pos.CENTER
                        );

                        if (daysLeft < 0) {

                            setStyle(
                                    "-fx-text-fill:#dc2626;" +
                                    "-fx-font-weight:bold;"
                            );

                        } else if (daysLeft <= 3) {

                            setStyle(
                                    "-fx-text-fill:#b45309;" +
                                    "-fx-font-weight:bold;"
                            );

                        } else {

                            setStyle(
                                    "-fx-text-fill:#15803d;" +
                                    "-fx-font-weight:bold;"
                            );
                        }
                    }
                }
        );
    }

    private void handleRenewal(
            TableView<BorrowedBook> table
    ) {

        if (loggedInUser.isSuspended()) {

            showWarning(
                    "Renewal Not Allowed",
                    "Suspended accounts cannot renew books. "
                            + "Please return overdue books and clear "
                            + "all outstanding fines."
            );

            return;
        }

        BorrowedBook selectedBook =
                table.getSelectionModel()
                        .getSelectedItem();

        if (selectedBook == null) {

            showWarning(
                    "No Book Selected",
                    "Please select a book before requesting renewal."
            );

            return;
        }

        String blockReason =
                controller.getRenewalBlockReason(
                        selectedBook.getLoanId(),
                        loggedInUser.getUserId()
                );

        if (blockReason != null) {

            showWarning(
                    "Renewal Not Allowed",
                    blockReason
            );

            return;
        }

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Confirm Book Renewal"
        );

        confirmation.setHeaderText(
                "Renew \""
                        + selectedBook.getTitle()
                        + "\"?"
        );

        confirmation.setContentText(
                "Current Due Date: "
                        + formatDate(
                                selectedBook.getDueDate()
                        )
                        + "\n\nThis loan may only be renewed once."
        );

        Optional<ButtonType> result =
                confirmation.showAndWait();

        if (result.isEmpty()
                || result.get() != ButtonType.OK) {

            return;
        }

        LocalDate newDueDate =
                controller.renewLoan(
                        selectedBook.getLoanId(),
                        loggedInUser.getUserId()
                );

        if (newDueDate == null) {

            showError(
                    "Renewal Failed",
                    "The loan could not be renewed. "
                            + "Its eligibility may have changed."
            );

            return;
        }

        showInformation(
                "Loan Renewed",
                "The book was renewed successfully.",
                "Book: "
                        + selectedBook.getTitle()
                        + "\nNew Due Date: "
                        + formatDate(newDueDate)
                        + "\nRenewals Used: 1 of 1"
        );

        loadBorrowedBooks(table);
    }

    private void handleReturn(
            TableView<BorrowedBook> table
    ) {

        BorrowedBook selectedBook =
                table.getSelectionModel()
                        .getSelectedItem();

        if (selectedBook == null) {

            showWarning(
                    "No Book Selected",
                    "Please select a book first."
            );

            return;
        }

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Confirm Book Return"
        );

        confirmation.setHeaderText(
                "Return \""
                        + selectedBook.getTitle()
                        + "\"?"
        );

        confirmation.setContentText(
                "Copy Number: "
                        + selectedBook.getCopyNumber()
                        + "\nDue Date: "
                        + formatDate(
                                selectedBook.getDueDate()
                        )
        );

        Optional<ButtonType> confirmationResult =
                confirmation.showAndWait();

        if (confirmationResult.isEmpty()
                || confirmationResult.get()
                != ButtonType.OK) {

            return;
        }

        boolean returned =
                returnController.returnBook(
                        selectedBook.getLoanId(),
                        selectedBook.getCopyNumber()
                );

        if (!returned) {

            showError(
                    "Return Failed",
                    "The book could not be returned."
            );

            return;
        }

        showInformation(
                "Book Returned",
                "The book was returned successfully.",
                "The loan record and copy availability were updated."
        );

        loadBorrowedBooks(table);
    }

    private void loadBorrowedBooks(
            TableView<BorrowedBook> table
    ) {

        table.setItems(
                FXCollections.observableArrayList(
                        controller.getBorrowedBooks(
                                loggedInUser.getUserId()
                        )
                )
        );

        table.getSelectionModel()
                .clearSelection();

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
                                ? " active loan"
                                : " active loans"
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

    private Button createSuccessButton(
            String text
    ) {

        return createStyledButton(
                text,
                190,
                "-fx-background-color:#15803d;",
                "-fx-background-color:#166534;"
        );
    }

    private Button createSecondaryButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefWidth(180);
        button.setPrefHeight(44);

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

        applyButtonBehaviour(
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

        applyButtonBehaviour(
                button,
                normalStyle,
                hoverStyle
        );

        return button;
    }

    private void applyButtonBehaviour(
            Button button,
            String normalStyle,
            String hoverStyle
    ) {

        String enabledStyle =
                normalStyle +
                "-fx-opacity:1;";

        String hoverEnabledStyle =
                hoverStyle +
                "-fx-opacity:1;";

        String disabledStyle =
                "-fx-background-color:#e2e8f0;" +
                "-fx-text-fill:#475569;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-border-color:#cbd5e1;" +
                "-fx-border-radius:10;" +
                "-fx-background-radius:10;" +
                "-fx-opacity:1;";

        button.setStyle(
                button.isDisabled()
                        ? disabledStyle
                        : enabledStyle
        );

        button.setCursor(
                button.isDisabled()
                        ? Cursor.DEFAULT
                        : Cursor.HAND
        );

        button.disabledProperty()
                .addListener(
                        (
                                observable,
                                previousValue,
                                disabled
                        ) -> {

                            button.setStyle(
                                    disabled
                                            ? disabledStyle
                                            : enabledStyle
                            );

                            button.setCursor(
                                    disabled
                                            ? Cursor.DEFAULT
                                            : Cursor.HAND
                            );
                        }
                );

        button.setOnMouseEntered(event -> {

            if (!button.isDisabled()) {

                button.setStyle(
                        hoverEnabledStyle
                );
            }
        });

        button.setOnMouseExited(event ->
                button.setStyle(
                        button.isDisabled()
                                ? disabledStyle
                                : enabledStyle
                )
        );
    }

    private String formatDate(
            LocalDate date
    ) {

        if (date == null) {
            return "-";
        }

        return date.format(
                DATE_FORMATTER
        );
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

    private void showInformation(
            String title,
            String header,
            String content
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

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