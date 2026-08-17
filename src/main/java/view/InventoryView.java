package view;

import controller.InventoryController;
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
import util.SceneRouter;

public class InventoryView {

    private final InventoryController controller;
    private final User loggedInUser;

    private Label recordCountLabel;

    public InventoryView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.controller = new InventoryController();
    }

    public Scene createScene() {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color:#e8f1f2;"
        );

        VBox header = createHeader();

        TableView<Book> table =
                createInventoryTable();

        VBox content =
                createContent(table);

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
                new Label(
                        "Inventory Management"
                );

        title.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        Label subtitle =
                new Label(
                        "Search, add, update and manage books in the library catalogue."
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

        Label roleBadge =
                new Label(
                        loggedInUser.getRole() == Role.LIBRARIAN
                                ? "● LIBRARIAN"
                                : "● ADMIN"
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
            TableView<Book> table
    ) {

        TextField searchField =
                createSearchField();

        Button searchButton =
                createPrimaryButton(
                        "Search"
                );

        Button addBookButton =
                createPrimaryButton(
                        "Add New Book"
                );

        Button updateBookButton =
                createOutlinedButton(
                        "Update Book"
                );

        Button deleteBookButton =
                createDeleteButton(
                        "Delete Book"
                );

        updateBookButton.setDisable(true);
        deleteBookButton.setDisable(true);

        HBox.setHgrow(
                searchField,
                Priority.ALWAYS
        );

        HBox searchRow =
                new HBox(
                        12,
                        searchField,
                        searchButton,
                        addBookButton,
                        updateBookButton,
                        deleteBookButton
                );

        searchRow.setAlignment(
                Pos.CENTER_LEFT
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

        addBookButton.setOnAction(event -> {

            AddBookDialog dialog =
                    new AddBookDialog(controller);

            boolean added =
                    dialog.show();

            if (added) {

                loadBooks(
                        table,
                        searchField.getText()
                );
            }
        });

        updateBookButton.setOnAction(event -> {

            Book selectedBook =
                    table.getSelectionModel()
                            .getSelectedItem();

            if (selectedBook == null) {
                return;
            }

            UpdateBookDialog dialog =
                    new UpdateBookDialog(
                            controller,
                            selectedBook
                    );

            boolean updated =
                    dialog.show();

            if (updated) {

                loadBooks(
                        table,
                        searchField.getText()
                );
            }
        });

        deleteBookButton.setOnAction(event ->
                deleteSelectedBook(
                        table,
                        searchField
                )
        );

        table.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (
                                observable,
                                previousBook,
                                selectedBook
                        ) -> {

                            boolean noSelection =
                                    selectedBook == null;

                            updateBookButton.setDisable(
                                    noSelection
                            );

                            deleteBookButton.setDisable(
                                    noSelection
                            );
                        }
                );

        VBox searchCard =
                new VBox(searchRow);

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

        Label tableTitle =
                new Label(
                        "Book Inventory"
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

    private TableView<Book> createInventoryTable() {

        TableView<Book> table =
                new TableView<>();

        table.setPlaceholder(
                new Label(
                        "No books found in the inventory."
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

        TableColumn<Book, Integer> totalColumn =
                createColumn(
                        "Total",
                        "totalCopies"
                );

        TableColumn<Book, Integer> availableColumn =
                createColumn(
                        "Available",
                        "availableCopies"
                );

        styleAvailabilityColumn(
                availableColumn
        );

        isbnColumn.setMinWidth(150);
        titleColumn.setMinWidth(300);
        authorColumn.setMinWidth(280);
        totalColumn.setMinWidth(100);
        availableColumn.setMinWidth(110);

        table.getColumns().addAll(
                isbnColumn,
                titleColumn,
                authorColumn,
                totalColumn,
                availableColumn
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

                        setAlignment(
                                Pos.CENTER
                        );

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

    private void deleteSelectedBook(
            TableView<Book> table,
            TextField searchField
    ) {

        Book selectedBook =
                table.getSelectionModel()
                        .getSelectedItem();

        if (selectedBook == null) {
            return;
        }

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Delete Book"
        );

        confirmation.setHeaderText(
                "Delete \""
                        + selectedBook.getTitle()
                        + "\"?"
        );

        confirmation.setContentText(
                "This action permanently removes the book and all available copies.\n\n"
                        + "A book with an active loan or reservation cannot be deleted."
        );

        confirmation.showAndWait()
                .ifPresent(response -> {

                    if (response != ButtonType.OK) {
                        return;
                    }

                    boolean deleted =
                            controller.deleteBook(
                                    selectedBook.getIsbn()
                            );

                    if (deleted) {

                        showAlert(
                                Alert.AlertType.INFORMATION,
                                "Book Deleted",
                                "Book deleted successfully.",
                                "The selected book was removed from the inventory."
                        );

                        loadBooks(
                                table,
                                searchField.getText()
                        );

                    } else {

                        showAlert(
                                Alert.AlertType.ERROR,
                                "Delete Failed",
                                "The book could not be deleted.",
                                "The book may have an active loan or reservation."
                        );
                    }
                });
    }

    private void loadBooks(
            TableView<Book> table,
            String keyword
    ) {

        String searchText =
                keyword == null
                        ? ""
                        : keyword.trim();

        table.setItems(
                FXCollections.observableArrayList(
                        controller.searchBooks(
                                searchText
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
                                ? " book record"
                                : " book records"
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

        } else if (loggedInUser.getRole()
                == Role.SUPER_ADMIN) {

            SceneRouter.open(
                    stage,
                    new SuperAdminDashboardView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Super Admin Dashboard"
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
                                oldValue,
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
                150,
                "-fx-background-color:#0f766e;",
                "-fx-background-color:#115e59;"
        );
    }

    private Button createOutlinedButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefWidth(145);
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

    private Button createDeleteButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefWidth(145);
        button.setPrefHeight(46);
        button.setCursor(Cursor.HAND);

        String normalStyle =
                "-fx-background-color:#fee2e2;" +
                "-fx-text-fill:#b91c1c;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-border-color:#fca5a5;" +
                "-fx-border-radius:10;" +
                "-fx-background-radius:10;";

        String hoverStyle =
                "-fx-background-color:#dc2626;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-border-color:#dc2626;" +
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

    String visibleNormalStyle =
            normalStyle +
            "-fx-opacity:1;";

    String visibleHoverStyle =
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
                    : visibleNormalStyle
    );

    button.disabledProperty().addListener(
            (
                    observable,
                    oldValue,
                    disabled
            ) -> {

                if (disabled) {

                    button.setStyle(
                            disabledStyle
                    );

                    button.setCursor(
                            Cursor.DEFAULT
                    );

                } else {

                    button.setStyle(
                            visibleNormalStyle
                    );

                    button.setCursor(
                            Cursor.HAND
                    );
                }
            }
    );

    button.setOnMouseEntered(event -> {

        if (!button.isDisabled()) {

            button.setStyle(
                    visibleHoverStyle
            );
        }
    });

    button.setOnMouseExited(event -> {

        button.setStyle(
                button.isDisabled()
                        ? disabledStyle
                        : visibleNormalStyle
        );
    });
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