package view;

import controller.BookController;
import controller.BorrowController;
import controller.UserController;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleObjectProperty;
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

public class IssueBookView {

    private final User loggedInUser;
    private final UserController userController;
    private final BookController bookController;
    private final BorrowController borrowController;

    private final SimpleObjectProperty<User> selectedMember =
            new SimpleObjectProperty<>();

    private Label resultCountLabel;

    public IssueBookView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.userController = new UserController();
        this.bookController = new BookController();
        this.borrowController = new BorrowController();
    }

    public Scene createScene() {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color:#e8f1f2;"
        );

        VBox header = createHeader();

        TableView<Book> table =
                createBookTable();

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
                new Label("Issue Book");

        title.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        Label subtitle =
                new Label(
                        "Search for an active member and issue an available library book."
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
                        loggedInUser.getRole() == Role.LIBRARIAN
                                ? "● LIBRARIAN"
                                : loggedInUser.getRole() == Role.SUPER_ADMIN
                                ? "● SUPER ADMIN"
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

        TextField memberIdField =
                createInputField(
                        "Enter Student or Teacher User ID"
                );

        memberIdField.setPrefWidth(340);
        memberIdField.setMaxWidth(340);

        Button searchMemberButton =
                createPrimaryButton(
                        "Search Member",
                        155
                );

        Label memberStatusLabel =
                new Label(
                        "No member selected"
                );

        memberStatusLabel.setWrapText(true);

        setMemberStatusStyle(
                memberStatusLabel,
                false
        );

        searchMemberButton.setOnAction(event ->
                searchMember(
                        memberIdField,
                        memberStatusLabel
                )
        );

        memberIdField.setOnAction(event ->
                searchMemberButton.fire()
        );

        HBox memberSearchRow =
                new HBox(
                        12,
                        memberIdField,
                        searchMemberButton,
                        memberStatusLabel
                );

        memberSearchRow.setAlignment(
                Pos.CENTER_LEFT
        );

        HBox.setHgrow(
                memberStatusLabel,
                Priority.ALWAYS
        );

        Label memberSectionTitle =
                new Label(
                        "Member Selection"
                );

        memberSectionTitle.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:17px;" +
                "-fx-font-weight:bold;"
        );

        VBox memberCard =
                new VBox(
                        14,
                        memberSectionTitle,
                        memberSearchRow
                );

        memberCard.setPadding(
                new Insets(20)
        );

        memberCard.setStyle(
                "-fx-background-color:#ffffff;" +
                "-fx-background-radius:16;" +
                "-fx-border-color:#cbd5e1;" +
                "-fx-border-radius:16;" +
                "-fx-border-width:1;"
        );

        TextField bookSearchField =
                createInputField(
                        "Search by title, author, ISBN or category..."
                );

        Button searchBookButton =
                createPrimaryButton(
                        "Search Book",
                        145
                );

        searchBookButton.setOnAction(event ->
                loadBooks(
                        table,
                        bookSearchField.getText()
                )
        );

        bookSearchField.setOnAction(event ->
                searchBookButton.fire()
        );

        HBox.setHgrow(
                bookSearchField,
                Priority.ALWAYS
        );

        HBox bookSearchRow =
                new HBox(
                        12,
                        bookSearchField,
                        searchBookButton
                );

        bookSearchRow.setAlignment(
                Pos.CENTER_LEFT
        );

        Label tableTitle =
                new Label(
                        "Available Catalogue"
                );

        tableTitle.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:18px;" +
                "-fx-font-weight:bold;"
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
                        resultCountLabel
                );

        tableHeader.setAlignment(
                Pos.CENTER_LEFT
        );

        Button issueButton =
                createSuccessButton(
                        "Issue Selected Book"
                );

        issueButton.setDisable(true);

        table.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (
                                observable,
                                previousBook,
                                selectedBook
                        ) -> updateIssueButtonState(
                                issueButton,
                                selectedBook
                        )
                );

        selectedMember.addListener(
                (
                        observable,
                        previousMember,
                        currentMember
                ) -> updateIssueButtonState(
                        issueButton,
                        table.getSelectionModel()
                                .getSelectedItem()
                )
        );

        issueButton.setOnAction(event ->
                issueSelectedBook(
                        table,
                        bookSearchField,
                        memberStatusLabel
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
                        bookSearchRow,
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
                new VBox(
                        20,
                        memberCard,
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
                "-fx-background-radius:12;" +
                "-fx-selection-bar:#ccfbf1;" +
                "-fx-selection-bar-non-focused:#ccfbf1;" +
                "-fx-selection-bar-text:#0f172a;"
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

        isbnColumn.setMinWidth(145);
        titleColumn.setMinWidth(270);
        authorColumn.setMinWidth(240);
        categoryColumn.setMinWidth(180);
        availableColumn.setMinWidth(110);

        table.getColumns().addAll(
                isbnColumn,
                titleColumn,
                authorColumn,
                categoryColumn,
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

    private void searchMember(
            TextField memberIdField,
            Label memberStatusLabel
    ) {

        String userId =
                memberIdField.getText() == null
                        ? ""
                        : memberIdField.getText().trim();

        if (userId.isBlank()) {

            selectedMember.set(null);

            memberStatusLabel.setText(
                    "Enter a member User ID."
            );

            setMemberStatusStyle(
                    memberStatusLabel,
                    false
            );

            showAlert(
                    Alert.AlertType.WARNING,
                    "User ID Required",
                    "Enter a member User ID.",
                    "Search using the Student or Teacher account ID."
            );

            return;
        }

        User member =
                userController.findActiveUserById(
                        userId
                );

        if (member != null
                && member.getRole() != Role.STUDENT
                && member.getRole() != Role.TEACHER) {

            member = null;
        }

        if (member == null) {

            selectedMember.set(null);

            memberStatusLabel.setText(
                    "No active Student or Teacher found."
            );

            setMemberStatusStyle(
                    memberStatusLabel,
                    false
            );

            showAlert(
                    Alert.AlertType.WARNING,
                    "Member Not Found",
                    "No active Student or Teacher was found.",
                    "Check the User ID and try again."
            );

            return;
        }

        selectedMember.set(member);

        memberStatusLabel.setText(
                "Selected: "
                        + member.getName()
                        + "  •  "
                        + member.getUserId()
                        + "  •  "
                        + member.getRole()
        );

        setMemberStatusStyle(
                memberStatusLabel,
                true
        );
    }

    private void issueSelectedBook(
            TableView<Book> table,
            TextField searchBookField,
            Label memberStatusLabel
    ) {

        User member =
                selectedMember.get();

        Book selectedBook =
                table.getSelectionModel()
                        .getSelectedItem();

        if (member == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Member Required",
                    "No valid member is selected.",
                    "Search and select an active Student or Teacher first."
            );

            return;
        }

        if (selectedBook == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Book Required",
                    "No book is selected.",
                    "Select an available book from the table."
            );

            return;
        }

        if (selectedBook.getAvailableCopies() <= 0) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Book Unavailable",
                    "No available copy was found.",
                    "Select another book or wait until a copy is returned."
            );

            return;
        }

        boolean success =
                borrowController.borrowBook(
                        member,
                        selectedBook.getIsbn()
                );

        if (!success) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Issue Failed",
                    "The book could not be issued.",
                    "The member may have reached the borrowing limit, "
                            + "already borrowed this book, have an overdue loan, "
                            + "or have an account restriction."
            );

            return;
        }

        showAlert(
                Alert.AlertType.INFORMATION,
                "Book Issued",
                "The book was issued successfully.",
                "Book: "
                        + selectedBook.getTitle()
                        + "\nIssued to: "
                        + member.getName()
                        + "\nUser ID: "
                        + member.getUserId()
        );

        loadBooks(
                table,
                searchBookField.getText()
        );

        table.getSelectionModel()
                .clearSelection();

        memberStatusLabel.setText(
                "Selected: "
                        + member.getName()
                        + "  •  "
                        + member.getUserId()
                        + "  •  "
                        + member.getRole()
        );
    }

    private void updateIssueButtonState(
            Button issueButton,
            Book selectedBook
    ) {

        boolean memberMissing =
                selectedMember.get() == null;

        boolean bookUnavailable =
                selectedBook == null
                        || selectedBook.getAvailableCopies() <= 0;

        issueButton.setDisable(
                memberMissing || bookUnavailable
        );
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
                        bookController.searchBooks(
                                searchText
                        )
                )
        );

        table.getSelectionModel()
                .clearSelection();

        updateResultCount(
                table.getItems().size()
        );
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

    private void setMemberStatusStyle(
            Label label,
            boolean valid
    ) {

        if (valid) {

            label.setStyle(
                    "-fx-background-color:#dcfce7;" +
                    "-fx-text-fill:#166534;" +
                    "-fx-font-size:13px;" +
                    "-fx-font-weight:bold;" +
                    "-fx-padding:10 14;" +
                    "-fx-background-radius:10;" +
                    "-fx-border-color:#86efac;" +
                    "-fx-border-radius:10;"
            );

        } else {

            label.setStyle(
                    "-fx-background-color:#f1f5f9;" +
                    "-fx-text-fill:#64748b;" +
                    "-fx-font-size:13px;" +
                    "-fx-font-weight:bold;" +
                    "-fx-padding:10 14;" +
                    "-fx-background-radius:10;" +
                    "-fx-border-color:#cbd5e1;" +
                    "-fx-border-radius:10;"
            );
        }
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

    private TextField createInputField(
            String prompt
    ) {

        TextField field =
                new TextField();

        field.setPromptText(prompt);
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
            String text,
            double width
    ) {

        return createStyledButton(
                text,
                width,
                "-fx-background-color:#0f766e;",
                "-fx-background-color:#115e59;"
        );
    }

    private Button createSuccessButton(
            String text
    ) {

        return createStyledButton(
                text,
                205,
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

        String enabledHoverStyle =
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
                        enabledHoverStyle
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