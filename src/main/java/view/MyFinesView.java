package view;

import controller.FineController;
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
import model.Fine;
import model.Role;
import model.User;
import util.MemberDashboardRouter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyFinesView {

    private final User loggedInUser;
    private final FineController controller;

    private Label recordCountLabel;
    private Label pendingAmountLabel;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(
                    "dd MMM yyyy, hh:mm a"
            );

    public MyFinesView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.controller = new FineController();
    }

    public Scene createScene() {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color:#e8f1f2;"
        );

        VBox header = createHeader();

        TableView<Fine> table =
                createFinesTable();

        VBox content =
                createContent(table);

        root.setTop(header);
        root.setCenter(content);

        loadFines(table);

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
                new Label("My Fines");

        title.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        Label subtitle =
                new Label(
                        "Review pending and paid fines associated with your library account."
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
            TableView<Fine> table
    ) {

        Label pendingHeading =
                new Label(
                        "Pending Fine Amount"
                );

        pendingHeading.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;"
        );

        pendingAmountLabel =
                new Label("₹0.00");

        pendingAmountLabel.setStyle(
                "-fx-text-fill:#b91c1c;" +
                "-fx-font-size:27px;" +
                "-fx-font-weight:bold;"
        );

        Label pendingHelp =
                new Label(
                        "Outstanding fines may restrict borrowing and renewal services."
                );

        pendingHelp.setWrapText(true);

        pendingHelp.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:13px;"
        );

        VBox pendingCard =
                new VBox(
                        8,
                        pendingHeading,
                        pendingAmountLabel,
                        pendingHelp
                );

        pendingCard.setPadding(
                new Insets(20)
        );

        pendingCard.setStyle(
                "-fx-background-color:#fff7ed;" +
                "-fx-background-radius:14;" +
                "-fx-border-color:#fed7aa;" +
                "-fx-border-radius:14;" +
                "-fx-border-width:1;"
        );

        Label tableTitle =
                new Label(
                        "Fine History"
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
                        pendingCard,
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

    private TableView<Fine> createFinesTable() {

        TableView<Fine> table =
                new TableView<>();

        table.setPlaceholder(
                new Label(
                        "No fines were found for your account."
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
                            Fine fine,
                            boolean empty
                    ) {

                        super.updateItem(
                                fine,
                                empty
                        );

                        if (empty || fine == null) {

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

        TableColumn<Fine, Integer> fineIdColumn =
                createColumn(
                        "Fine ID",
                        "fineId"
                );

        TableColumn<Fine, String> bookColumn =
                createColumn(
                        "Book",
                        "bookTitle"
                );

        TableColumn<Fine, String> copyColumn =
                createColumn(
                        "Copy No.",
                        "copyNumber"
                );

        TableColumn<Fine, BigDecimal> amountColumn =
                createColumn(
                        "Amount",
                        "amount"
                );

        styleAmountColumn(
                amountColumn
        );

        TableColumn<Fine, String> statusColumn =
                createColumn(
                        "Status",
                        "status"
                );

        styleStatusColumn(
                statusColumn
        );

        TableColumn<Fine, LocalDateTime> createdColumn =
                createDateTimeColumn(
                        "Generated On",
                        "createdAt"
                );

        TableColumn<Fine, LocalDateTime> paidColumn =
                createDateTimeColumn(
                        "Paid On",
                        "paidAt"
                );

        fineIdColumn.setMinWidth(80);
        bookColumn.setMinWidth(250);
        copyColumn.setMinWidth(110);
        amountColumn.setMinWidth(110);
        statusColumn.setMinWidth(110);
        createdColumn.setMinWidth(185);
        paidColumn.setMinWidth(185);

        table.getColumns().addAll(
                fineIdColumn,
                bookColumn,
                copyColumn,
                amountColumn,
                statusColumn,
                createdColumn,
                paidColumn
        );

        return table;
    }

    private <T> TableColumn<Fine, T> createColumn(
            String heading,
            String property
    ) {

        TableColumn<Fine, T> column =
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

    private TableColumn<Fine, LocalDateTime>
    createDateTimeColumn(
            String heading,
            String property
    ) {

        TableColumn<Fine, LocalDateTime> column =
                createColumn(
                        heading,
                        property
                );

        column.setCellFactory(tableColumn ->
                new TableCell<>() {

                    @Override
                    protected void updateItem(
                            LocalDateTime value,
                            boolean empty
                    ) {

                        super.updateItem(
                                value,
                                empty
                        );

                        if (empty || value == null) {

                            setText("-");
                            return;
                        }

                        setText(
                                value.format(
                                        DATE_FORMATTER
                                )
                        );
                    }
                }
        );

        return column;
    }

    private void styleAmountColumn(
            TableColumn<Fine, BigDecimal> column
    ) {

        column.setCellFactory(tableColumn ->
                new TableCell<>() {

                    @Override
                    protected void updateItem(
                            BigDecimal amount,
                            boolean empty
                    ) {

                        super.updateItem(
                                amount,
                                empty
                        );

                        if (empty || amount == null) {

                            setText("-");
                            setStyle("");
                            return;
                        }

                        setText(
                                "₹"
                                        + amount.setScale(
                                        2,
                                        java.math.RoundingMode.HALF_UP
                                )
                        );

                        setAlignment(
                                Pos.CENTER
                        );

                        setStyle(
                                "-fx-text-fill:#b91c1c;" +
                                "-fx-font-weight:bold;"
                        );
                    }
                }
        );
    }

    private void styleStatusColumn(
            TableColumn<Fine, String> column
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

                        if (normalized.contains("PAID")) {

                            setStyle(
                                    "-fx-text-fill:#15803d;" +
                                    "-fx-font-weight:bold;"
                            );

                        } else if (normalized.contains("PENDING")
                                || normalized.contains("UNPAID")) {

                            setStyle(
                                    "-fx-text-fill:#dc2626;" +
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

    private void loadFines(
            TableView<Fine> table
    ) {

        List<Fine> fines =
                controller.getFinesByUser(
                        loggedInUser.getUserId()
                );

        table.setItems(
                FXCollections.observableArrayList(
                        fines
                )
        );

        updateRecordCount(
                fines.size()
        );

        updatePendingAmount(
                fines
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
                                ? " fine record"
                                : " fine records"
                )
        );
    }

    private void updatePendingAmount(
            List<Fine> fines
    ) {

        if (pendingAmountLabel == null) {
            return;
        }

        BigDecimal total =
                BigDecimal.ZERO;

        for (Fine fine : fines) {

            if (fine == null
                    || fine.getAmount() == null) {

                continue;
            }

            String status =
                    fine.getStatus() == null
                            ? ""
                            : fine.getStatus()
                            .trim()
                            .toUpperCase();

            if (!status.contains("PAID")) {

                total =
                        total.add(
                                fine.getAmount()
                        );
            }
        }

        pendingAmountLabel.setText(
                "₹"
                        + total.setScale(
                        2,
                        java.math.RoundingMode.HALF_UP
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