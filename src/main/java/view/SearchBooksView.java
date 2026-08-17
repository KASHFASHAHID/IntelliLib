package view;

import controller.BookController;
import controller.BorrowController;
import controller.ReservationController;
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
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Book;
import model.Role;
import model.User;
import util.MemberDashboardRouter;

public class SearchBooksView {

    private final BookController bookController;
    private final BorrowController borrowController;
    private final ReservationController reservationController;
    private final User loggedInUser;

    private Label resultCountLabel;

    public SearchBooksView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.bookController = new BookController();
        this.borrowController = new BorrowController();
        this.reservationController = new ReservationController();
    }

    public Scene createScene() {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color:#e8f1f2;"
        );

        VBox header = createHeader();

        TableView<Book> table = createBookTable();

        VBox content = createContent(table);

        root.setTop(header);
        root.setCenter(content);

        loadBooks(table, "");

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
                new Label("Library Catalogue");

        title.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        Label subtitle =
                new Label(
                        "Search the catalogue, borrow available books, or reserve unavailable titles."
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

        Label memberBadge =
                new Label(
                        loggedInUser.getRole() == Role.TEACHER
                                ? "● TEACHER"
                                : "● STUDENT"
                );

        memberBadge.setStyle(
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
                        memberBadge,
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
            TableView<Book> table
    ) {

        TextField searchField =
                createSearchField();

        Button searchButton =
                createPrimaryButton(
                        "Search"
                );

        searchButton.setOnAction(event ->
                loadBooks(
                        table,
                        searchField.getText()
                )
        );

        searchField.setOnAction(event ->
                searchButton.fire()
        );

        HBox.setHgrow(
                searchField,
                Priority.ALWAYS
        );

        HBox searchRow =
                new HBox(
                        12,
                        searchField,
                        searchButton
                );

        searchRow.setAlignment(
                Pos.CENTER_LEFT
        );

        resultCountLabel =
                new Label();

        resultCountLabel.setStyle(
                "-fx-background-color:#ccfbf1;" +
                "-fx-text-fill:#115e59;" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:8 14;" +
                "-fx-background-radius:18;"
        );

        Label instructionLabel =
                new Label(
                        "Select a book to view available actions."
                );

        instructionLabel.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:13px;"
        );

        Region countSpacer =
                new Region();

        HBox.setHgrow(
                countSpacer,
                Priority.ALWAYS
        );

        HBox informationRow =
                new HBox(
                        15,
                        instructionLabel,
                        countSpacer,
                        resultCountLabel
                );

        informationRow.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox searchCard =
                new VBox(
                        15,
                        searchRow,
                        informationRow
                );

        searchCard.setPadding(
                new Insets(20)
        );

        searchCard.setStyle(
                "-fx-background-color:#ffffff;" +
                "-fx-background-radius:16;" +
                "-fx-border-color:#cbd5e1;" +
                "-fx-border-radius:16;" +
                "-fx-border-width:1;"
        );

        HBox actionRow =
                createActionRow(
                        table,
                        searchField
                );

        VBox tableCard =
                new VBox(
                        15,
                        table,
                        actionRow
                );

        tableCard.setPadding(
                new Insets(18)
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
                        20,
                        searchCard,
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

    private HBox createActionRow(
            TableView<Book> table,
            TextField searchField
    ) {

        Button borrowButton =
                createSuccessButton(
                        loggedInUser.getRole() == Role.TEACHER
                                ? "Borrow Copies"
                                : "Borrow Book"
                );

        Button reserveButton =
                createOutlinedButton(
                        "Reserve Book"
                );

        borrowButton.setDisable(true);
        reserveButton.setDisable(true);

        Label quantityLabel =
                new Label("Copies");

        quantityLabel.setStyle(
                "-fx-text-fill:#334155;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;"
        );

        Button minusButton =
                createQuantityButton("-");

        Button plusButton =
                createQuantityButton("+");

        TextField quantityField =
                new TextField("1");

        quantityField.setEditable(false);
        quantityField.setAlignment(Pos.CENTER);
        quantityField.setPrefWidth(55);
        quantityField.setPrefHeight(42);

        quantityField.setStyle(
                "-fx-background-color:#f8fafc;" +
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-border-color:#cbd5e1;" +
                "-fx-border-radius:9;" +
                "-fx-background-radius:9;"
        );

        boolean teacher =
                loggedInUser.getRole() == Role.TEACHER;

        quantityLabel.setVisible(teacher);
        quantityLabel.setManaged(teacher);

        minusButton.setVisible(teacher);
        minusButton.setManaged(teacher);

        quantityField.setVisible(teacher);
        quantityField.setManaged(teacher);

        plusButton.setVisible(teacher);
        plusButton.setManaged(teacher);

        table.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (
                                observable,
                                previousBook,
                                selectedBook
                        ) -> {

                            quantityField.setText("1");

                            boolean hasSelection =
                                    selectedBook != null;

                            boolean available =
                                    hasSelection
                                            && selectedBook
                                            .getAvailableCopies() > 0;

                            borrowButton.setDisable(
                                    !available
                            );

                            reserveButton.setDisable(
                                    !hasSelection
                                            || selectedBook
                                            .getAvailableCopies() > 0
                            );
                        }
                );

        minusButton.setOnAction(event -> {

            int quantity =
                    Integer.parseInt(
                            quantityField.getText()
                    );

            if (quantity > 1) {

                quantityField.setText(
                        String.valueOf(
                                quantity - 1
                        )
                );
            }
        });

        plusButton.setOnAction(event -> {

            Book selectedBook =
                    table.getSelectionModel()
                            .getSelectedItem();

            if (selectedBook == null) {
                return;
            }

            int quantity =
                    Integer.parseInt(
                            quantityField.getText()
                    );

            if (quantity
                    < selectedBook.getAvailableCopies()) {

                quantityField.setText(
                        String.valueOf(
                                quantity + 1
                        )
                );
            }
        });

        borrowButton.setOnAction(event ->
                borrowSelectedBook(
                        table,
                        quantityField,
                        searchField
                )
        );

        reserveButton.setOnAction(event ->
                reserveSelectedBook(table)
        );

        Region spacer = new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox actionRow =
                new HBox(
                        12,
                        quantityLabel,
                        minusButton,
                        quantityField,
                        plusButton,
                        spacer,
                        reserveButton,
                        borrowButton
                );

        actionRow.setAlignment(
                Pos.CENTER_RIGHT
        );

        return actionRow;
    }

    private TableView<Book> createBookTable() {

        TableView<Book> table =
                new TableView<>();

        table.setPlaceholder(
                new Label(
                        "No books matched your search."
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
                            Book book,
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

        TableColumn<Book, String> isbnColumn =
                createColumn(
                        "ISBN",
                        "isbn"
                );

        TableColumn<Book, String> titleColumn =
                createColumn(
                        "Title",
                        "title"
                );

        TableColumn<Book, String> authorColumn =
                createColumn(
                        "Author",
                        "authors"
                );

        TableColumn<Book, String> categoryColumn =
                createColumn(
                        "Category",
                        "categoryName"
                );

        TableColumn<Book, Integer> availableColumn =
                createColumn(
                        "Available",
                        "availableCopies"
                );

        styleAvailabilityColumn(
                availableColumn
        );

        TableColumn<Book, Integer> totalColumn =
                createColumn(
                        "Total",
                        "totalCopies"
                );

        isbnColumn.setMinWidth(145);
        titleColumn.setMinWidth(230);
        authorColumn.setMinWidth(220);
        categoryColumn.setMinWidth(180);
        availableColumn.setMinWidth(100);
        totalColumn.setMinWidth(85);

        table.getColumns().addAll(
                isbnColumn,
                titleColumn,
                authorColumn,
                categoryColumn,
                availableColumn,
                totalColumn
        );

        return table;
    }

    private <T> TableColumn<Book, T> createColumn(
            String heading,
            String property
    ) {

        TableColumn<Book, T> column =
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

    private void styleAvailabilityColumn(
            TableColumn<Book, Integer> column
    ) {

        column.setCellFactory(tableColumn ->
                new TableCell<>() {

                    @Override
                    protected void updateItem(
                            Integer availableCopies,
                            boolean empty
                    ) {

                        super.updateItem(
                                availableCopies,
                                empty
                        );

                        if (empty || availableCopies == null) {

                            setText(null);
                            setStyle("");
                            return;
                        }

                        setText(
                                String.valueOf(
                                        availableCopies
                                )
                        );

                        setAlignment(Pos.CENTER);

                        if (availableCopies > 0) {

                            setStyle(
                                    "-fx-text-fill:#15803d;" +
                                    "-fx-font-weight:bold;"
                            );

                        } else {

                            setStyle(
                                    "-fx-text-fill:#dc2626;" +
                                    "-fx-font-weight:bold;"
                            );
                        }
                    }
                }
        );
    }

    private void borrowSelectedBook(
            TableView<Book> table,
            TextField quantityField,
            TextField searchField
    ) {

        Book selectedBook =
                table.getSelectionModel()
                        .getSelectedItem();

        if (selectedBook == null) {
            return;
        }

        int quantity =
                loggedInUser.getRole() == Role.TEACHER
                        ? Integer.parseInt(
                        quantityField.getText()
                )
                        : 1;

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Confirm Borrowing"
        );

        confirmation.setHeaderText(
                loggedInUser.getRole() == Role.TEACHER
                        ? "Borrow the selected copies?"
                        : "Borrow the selected book?"
        );

        confirmation.setContentText(
                "Title: "
                        + selectedBook.getTitle()
                        + "\nAuthor: "
                        + selectedBook.getAuthors()
                        + "\nCopies: "
                        + quantity
                        + "\nAvailable: "
                        + selectedBook.getAvailableCopies()
        );

        confirmation.showAndWait()
                .ifPresent(response -> {

                    if (response.getButtonData()
                            != ButtonBar.ButtonData.OK_DONE) {

                        return;
                    }

                    boolean success;

                    if (loggedInUser.getRole()
                            == Role.TEACHER) {

                        success =
                                borrowController.borrowBooks(
                                        loggedInUser,
                                        selectedBook.getIsbn(),
                                        quantity
                                );

                    } else {

                        success =
                                borrowController.borrowBook(
                                        loggedInUser,
                                        selectedBook.getIsbn()
                                );
                    }

                    if (success) {

                        showAlert(
                                Alert.AlertType.INFORMATION,
                                "Borrow Successful",
                                "Book borrowed successfully.",
                                "Please return the book on or before its due date."
                        );

                        loadBooks(
                                table,
                                searchField.getText()
                        );

                    } else {

                        showAlert(
                                Alert.AlertType.ERROR,
                                "Borrow Failed",
                                "The borrowing request could not be completed.",
                                "This may be caused by an overdue loan, account restriction, borrowing limit, or unavailable copies."
                        );
                    }
                });
    }

    private void reserveSelectedBook(
            TableView<Book> table
    ) {

        Book selectedBook =
                table.getSelectionModel()
                        .getSelectedItem();

        if (selectedBook == null) {
            return;
        }

        boolean reserved =
                reservationController.reserveBook(
                        loggedInUser.getUserId(),
                        selectedBook.getIsbn()
                );

        if (reserved) {

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Reservation Successful",
                    "The book was reserved successfully.",
                    "You will be notified when the book becomes available."
            );

        } else {

            TextArea messageArea =
                    new TextArea(
                            "Your reservation request was unsuccessful. "
                                    + "This may be caused by an overdue loan, "
                                    + "an account restriction, an existing reservation, "
                                    + "or the book already being borrowed by you."
                    );

            messageArea.setEditable(false);
            messageArea.setWrapText(true);
            messageArea.setPrefColumnCount(45);
            messageArea.setPrefRowCount(4);

            Alert alert =
                    new Alert(
                            Alert.AlertType.ERROR
                    );

            alert.setTitle(
                    "Reservation Failed"
            );

            alert.setHeaderText(
                    "The book could not be reserved."
            );

            alert.getDialogPane()
                    .setContent(messageArea);

            alert.getDialogPane()
                    .setPrefWidth(540);

            alert.showAndWait();
        }
    }

    private void loadBooks(
            TableView<Book> table,
            String keyword
    ) {

        table.setItems(
                FXCollections.observableArrayList(
                        bookController.searchBooks(
                                keyword == null
                                        ? ""
                                        : keyword.trim()
                        )
                )
        );

        updateResultCount(
                table.getItems().size()
        );

        table.getSelectionModel()
                .clearSelection();
    }

    private void updateResultCount(
            int count
    ) {

        if (resultCountLabel == null) {
            return;
        }

        resultCountLabel.setText(
                count
                        + (
                        count == 1
                                ? " book found"
                                : " books found"
                )
        );
    }

    private TextField createSearchField() {

        TextField field =
                new TextField();

        field.setPromptText(
                "Search by title, author, ISBN or category..."
        );

        field.setPrefHeight(46);
        field.setMaxWidth(
                Double.MAX_VALUE
        );

        String normalStyle =
                "-fx-background-color:#f8fafc;" +
                "-fx-text-fill:#0f172a;" +
                "-fx-prompt-text-fill:#94a3b8;" +
                "-fx-font-size:14px;" +
                "-fx-padding:0 15;" +
                "-fx-background-radius:10;" +
                "-fx-border-color:#cbd5e1;" +
                "-fx-border-radius:10;" +
                "-fx-border-width:1;";

        String focusedStyle =
                "-fx-background-color:#ffffff;" +
                "-fx-text-fill:#0f172a;" +
                "-fx-prompt-text-fill:#94a3b8;" +
                "-fx-font-size:14px;" +
                "-fx-padding:0 15;" +
                "-fx-background-radius:10;" +
                "-fx-border-color:#0f766e;" +
                "-fx-border-radius:10;" +
                "-fx-border-width:2;";

        field.setStyle(normalStyle);

        field.focusedProperty()
                .addListener(
                        (
                                observable,
                                previousValue,
                                focused
                        ) -> field.setStyle(
                                focused
                                        ? focusedStyle
                                        : normalStyle
                        )
                );

        return field;
    }

    private Button createPrimaryButton(
            String text
    ) {

        return createStyledButton(
                text,
                125,
                "-fx-background-color:#0f766e;",
                "-fx-background-color:#115e59;"
        );
    }

    private Button createSuccessButton(
            String text
    ) {

        return createStyledButton(
                text,
                180,
                "-fx-background-color:#15803d;",
                "-fx-background-color:#166534;"
        );
    }

    private Button createOutlinedButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefWidth(165);
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

    private Button createQuantityButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefSize(42, 42);
        button.setCursor(Cursor.HAND);

        String normalStyle =
                "-fx-background-color:#f1f5f9;" +
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:18px;" +
                "-fx-font-weight:bold;" +
                "-fx-border-color:#cbd5e1;" +
                "-fx-border-radius:9;" +
                "-fx-background-radius:9;";

        String hoverStyle =
                "-fx-background-color:#ccfbf1;" +
                "-fx-text-fill:#0f766e;" +
                "-fx-font-size:18px;" +
                "-fx-font-weight:bold;" +
                "-fx-border-color:#0f766e;" +
                "-fx-border-radius:9;" +
                "-fx-background-radius:9;";

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

        button.setStyle(normalStyle);

        button.setOnMouseEntered(event -> {

            if (!button.isDisabled()) {
                button.setStyle(hoverStyle);
            }
        });

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