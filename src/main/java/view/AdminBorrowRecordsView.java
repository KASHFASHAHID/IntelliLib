package view;

import controller.BorrowRecordController;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.BorrowRecord;
import model.User;
import util.SceneRouter;
import model.Role;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AdminBorrowRecordsView {

    private final User loggedInUser;
    private final BorrowRecordController controller;
    private final String filterMode;

    private Label totalLabel;

    public AdminBorrowRecordsView(
            User loggedInUser
    ) {
        this(
                loggedInUser,
                "ALL"
        );
    }

    public AdminBorrowRecordsView(
            User loggedInUser,
            String filterMode
    ) {
        this.loggedInUser = loggedInUser;
        this.controller = new BorrowRecordController();
        this.filterMode = normalizeFilter(filterMode);
    }

    public Scene createScene() {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color:#e8f1f2;"
        );

        VBox header = createHeader();

        TableView<BorrowRecord> table =
                createTable();

        VBox content =
                createContent(table);

        root.setTop(header);
        root.setCenter(content);

        refreshTable(
                table,
                ""
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

        Label pageTitle =
                new Label(
                        getPageTitle()
                );

        pageTitle.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        Label subtitle =
                new Label(
                        getPageSubtitle()
                );

        subtitle.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:15px;"
        );

        VBox headingBox =
                new VBox(
                        6,
                        pageTitle,
                        subtitle
                );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button backButton =
                createSecondaryButton(
                        "Back to Dashboard"
                );

        backButton.setOnAction(event -> {

    Stage stage =
            getStage(backButton);

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
});

        HBox titleRow =
                new HBox(
                        20,
                        headingBox,
                        spacer,
                        backButton
                );

        titleRow.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox header =
                new VBox(titleRow);

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
            TableView<BorrowRecord> table
    ) {

        TextField searchField =
                createSearchField();

        Button searchButton =
                createPrimaryButton(
                        "Search"
                );

        searchButton.setOnAction(event ->
                refreshTable(
                        table,
                        searchField.getText()
                )
        );

        searchField.setOnAction(event ->
                searchButton.fire()
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

        HBox.setHgrow(
                searchField,
                Priority.ALWAYS
        );

        Button allButton =
                createFilterButton(
                        "All Records",
                        "ALL"
                );

        Button issuedButton =
                createFilterButton(
                        "Currently Borrowed",
                        "ISSUED"
                );

        Button overdueButton =
                createFilterButton(
                        "Overdue",
                        "OVERDUE"
                );

        allButton.setOnAction(event ->
                openFilteredView(
                        allButton,
                        "ALL"
                )
        );

        issuedButton.setOnAction(event ->
                openFilteredView(
                        issuedButton,
                        "ISSUED"
                )
        );

        overdueButton.setOnAction(event ->
                openFilteredView(
                        overdueButton,
                        "OVERDUE"
                )
        );

        HBox filterRow =
                new HBox(
                        10,
                        allButton,
                        issuedButton,
                        overdueButton
                );

        filterRow.setAlignment(
                Pos.CENTER_LEFT
        );

        totalLabel =
                new Label();

        totalLabel.setStyle(
                "-fx-background-color:#ccfbf1;" +
                "-fx-text-fill:#115e59;" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:8 14;" +
                "-fx-background-radius:18;"
        );

        Region countSpacer =
                new Region();

        HBox.setHgrow(
                countSpacer,
                Priority.ALWAYS
        );

        HBox filterAndCountRow =
                new HBox(
                        15,
                        filterRow,
                        countSpacer,
                        totalLabel
                );

        filterAndCountRow.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox toolbar =
                new VBox(
                        16,
                        searchRow,
                        filterAndCountRow
                );

        toolbar.setPadding(
                new Insets(22)
        );

        toolbar.setStyle(
                "-fx-background-color:#ffffff;" +
                "-fx-background-radius:16;" +
                "-fx-border-color:#cbd5e1;" +
                "-fx-border-radius:16;" +
                "-fx-border-width:1;"
        );

        VBox tableCard =
                new VBox(table);

        tableCard.setPadding(
                new Insets(8)
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
                        toolbar,
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

    private TableView<BorrowRecord> createTable() {

        TableView<BorrowRecord> table =
                new TableView<>();

        table.setPlaceholder(
                new Label(
                        "No matching borrow records found."
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
                            BorrowRecord item,
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

        TableColumn<BorrowRecord, Integer> loanIdColumn =
                createColumn(
                        "Loan ID",
                        "loanId"
                );

        TableColumn<BorrowRecord, String> userIdColumn =
                createColumn(
                        "User ID",
                        "userId"
                );

        TableColumn<BorrowRecord, String> memberColumn =
                createColumn(
                        "Member",
                        "memberName"
                );

        TableColumn<BorrowRecord, String> roleColumn =
                createColumn(
                        "Role",
                        "role"
                );

        TableColumn<BorrowRecord, String> titleColumn =
                createColumn(
                        "Book",
                        "title"
                );

        TableColumn<BorrowRecord, String> authorsColumn =
                createColumn(
                        "Author",
                        "authors"
                );

        TableColumn<BorrowRecord, String> copyColumn =
                createColumn(
                        "Copy Number",
                        "copyNumber"
                );

        TableColumn<BorrowRecord, LocalDate> issueDateColumn =
                createDateColumn(
                        "Issue Date",
                        "issueDate"
                );

        TableColumn<BorrowRecord, LocalDate> dueDateColumn =
                createDateColumn(
                        "Due Date",
                        "dueDate"
                );

        TableColumn<BorrowRecord, LocalDate> returnDateColumn =
                createDateColumn(
                        "Return Date",
                        "returnDate"
                );

        TableColumn<BorrowRecord, String> statusColumn =
                createColumn(
                        "Status",
                        "status"
                );

        styleStatusColumn(
                statusColumn
        );

        loanIdColumn.setMinWidth(70);
        userIdColumn.setMinWidth(115);
        memberColumn.setMinWidth(125);
        roleColumn.setMinWidth(85);
        titleColumn.setMinWidth(170);
        authorsColumn.setMinWidth(180);
        copyColumn.setMinWidth(145);
        issueDateColumn.setMinWidth(105);
        dueDateColumn.setMinWidth(105);
        returnDateColumn.setMinWidth(105);
        statusColumn.setMinWidth(100);

        table.getColumns().addAll(
                loanIdColumn,
                userIdColumn,
                memberColumn,
                roleColumn,
                titleColumn,
                authorsColumn,
                copyColumn,
                issueDateColumn,
                dueDateColumn,
                returnDateColumn,
                statusColumn
        );

        return table;
    }

    private <T> TableColumn<BorrowRecord, T>
    createColumn(
            String heading,
            String property
    ) {

        TableColumn<BorrowRecord, T> column =
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

    private TableColumn<BorrowRecord, LocalDate>
    createDateColumn(
            String heading,
            String property
    ) {

        TableColumn<BorrowRecord, LocalDate> column =
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
                        } else {
                            setText(
                                    date.format(
                                            formatter
                                    )
                            );
                        }
                    }
                }
        );

        return column;
    }

    private void styleStatusColumn(
            TableColumn<BorrowRecord, String> column
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

                        String normalizedStatus =
                                status.trim()
                                        .toUpperCase();

                        setText(normalizedStatus);
                        setAlignment(Pos.CENTER);

                        switch (normalizedStatus) {

                            case "ISSUED" ->
                                    setStyle(
                                            "-fx-text-fill:#0369a1;" +
                                            "-fx-font-weight:bold;"
                                    );

                            case "OVERDUE" ->
                                    setStyle(
                                            "-fx-text-fill:#dc2626;" +
                                            "-fx-font-weight:bold;"
                                    );

                            case "RETURNED" ->
                                    setStyle(
                                            "-fx-text-fill:#15803d;" +
                                            "-fx-font-weight:bold;"
                                    );

                            default ->
                                    setStyle(
                                            "-fx-text-fill:#475569;" +
                                            "-fx-font-weight:bold;"
                                    );
                        }
                    }
                }
        );
    }

    private TextField createSearchField() {

        TextField searchField =
                new TextField();

        searchField.setPromptText(
                "Search by User ID, member, book, author, copy number or status..."
        );

        searchField.setPrefHeight(46);
        searchField.setMaxWidth(
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

        searchField.setStyle(
                normalStyle
        );

        searchField.focusedProperty()
                .addListener(
                        (
                                observable,
                                oldValue,
                                focused
                        ) -> searchField.setStyle(
                                focused
                                        ? focusedStyle
                                        : normalStyle
                        )
                );

        return searchField;
    }

    private Button createFilterButton(
            String text,
            String buttonFilter
    ) {

        Button button =
                new Button(text);

        button.setPrefHeight(42);
        button.setMinWidth(135);
        button.setCursor(Cursor.HAND);

        boolean selected =
                filterMode.equals(
                        buttonFilter
                );

        String normalStyle =
                selected
                        ? getSelectedFilterStyle()
                        : getUnselectedFilterStyle();

        String hoverStyle =
                selected
                        ? getSelectedFilterHoverStyle()
                        : getUnselectedFilterHoverStyle();

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

        return button;
    }

    private Button createPrimaryButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefWidth(125);
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

        return button;
    }

    private Button createSecondaryButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefWidth(175);
        button.setPrefHeight(42);
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

        return button;
    }

    private void refreshTable(
            TableView<BorrowRecord> table,
            String keyword
    ) {

        table.setItems(
                FXCollections.observableArrayList(
                        controller.getBorrowRecords(
                                keyword == null
                                        ? ""
                                        : keyword.trim(),
                                filterMode
                        )
                )
        );

        updateTotalLabel(table);
    }

    private void openFilteredView(
            Button sourceButton,
            String selectedFilter
    ) {

        Stage stage =
                getStage(sourceButton);

        SceneRouter.open(
                stage,
                new AdminBorrowRecordsView(
                        loggedInUser,
                        selectedFilter
                ).createScene(),
                "IntelliLib - "
                        + getPageTitle(
                                selectedFilter
                        )
        );
    }

    private void updateTotalLabel(
            TableView<BorrowRecord> table
    ) {

        if (totalLabel == null) {
            return;
        }

        int total =
                table.getItems().size();

        totalLabel.setText(
                total
                        + (
                        total == 1
                                ? " record displayed"
                                : " records displayed"
                )
        );
    }

    private String normalizeFilter(
            String value
    ) {

        if (value == null) {
            return "ALL";
        }

        String normalized =
                value.trim()
                        .toUpperCase();

        if ("ISSUED".equals(normalized)
                || "OVERDUE".equals(normalized)) {

            return normalized;
        }

        return "ALL";
    }

    private String getPageTitle() {

        return getPageTitle(
                filterMode
        );
    }

    private String getPageTitle(
            String selectedFilter
    ) {

        return switch (
                normalizeFilter(
                        selectedFilter
                )
        ) {

            case "ISSUED" ->
                    "Currently Borrowed Books";

            case "OVERDUE" ->
                    "Overdue Loans";

            default ->
                    "Borrow Records";
        };
    }

    private String getPageSubtitle() {

        return switch (filterMode) {

            case "ISSUED" ->
                    "Books currently held by members, including overdue loans.";

            case "OVERDUE" ->
                    "Active loans that have passed their scheduled due date.";

            default ->
                    "Complete borrowing history for all library members.";
        };
    }

    private String getSelectedFilterStyle() {

        return "-fx-background-color:#0f766e;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:9;";
    }

    private String getSelectedFilterHoverStyle() {

        return "-fx-background-color:#115e59;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:9;";
    }

    private String getUnselectedFilterStyle() {

        return "-fx-background-color:#ffffff;" +
                "-fx-text-fill:#475569;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-border-color:#cbd5e1;" +
                "-fx-border-radius:9;" +
                "-fx-background-radius:9;";
    }

    private String getUnselectedFilterHoverStyle() {

        return "-fx-background-color:#ecfeff;" +
                "-fx-text-fill:#0f766e;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-border-color:#0f766e;" +
                "-fx-border-radius:9;" +
                "-fx-background-radius:9;";
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

    private Stage getStage(
            Button button
    ) {

        return (Stage) button
                .getScene()
                .getWindow();
    }
}