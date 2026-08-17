package view;

import controller.ReportController;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.User;
import util.SceneRouter;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ReportsView {

    private final User loggedInUser;
    private final ReportController controller;

    public ReportsView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.controller = new ReportController();
    }

    public Scene createScene() {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color:#e8f1f2;"
        );

        VBox header = createHeader();
        ScrollPane content = createContent();

        root.setTop(header);
        root.setCenter(content);

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
                new Label("Library Reports");

        title.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        Label subtitle =
                new Label(
                        "Overview of library activity, resources and financial records."
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

        Button refreshButton =
                createPrimaryButton(
                        "Refresh Reports"
                );

        Button backButton =
                createSecondaryButton(
                        "Back to Dashboard"
                );

        refreshButton.setOnAction(event -> {

            Stage stage =
                    getStage(refreshButton);

            SceneRouter.open(
                    stage,
                    new ReportsView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Reports"
            );
        });

        backButton.setOnAction(event -> {

            Stage stage =
                    getStage(backButton);

            SceneRouter.open(
                    stage,
                    new AdminDashboardView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Admin Dashboard"
            );
        });

        HBox actions =
                new HBox(
                        12,
                        refreshButton,
                        backButton
                );

        actions.setAlignment(
                Pos.CENTER_RIGHT
        );

        HBox headerRow =
                new HBox(
                        20,
                        headingBox,
                        spacer,
                        actions
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

    private ScrollPane createContent() {

        Label sectionTitle =
                new Label(
                        "Current Library Summary"
                );

        sectionTitle.setStyle(
                "-fx-text-fill:#0f172a;" +
                "-fx-font-size:22px;" +
                "-fx-font-weight:bold;"
        );

        Label sectionSubtitle =
                new Label(
                        "Live figures retrieved from the library database."
                );

        sectionSubtitle.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:14px;"
        );

        VBox sectionHeading =
                new VBox(
                        5,
                        sectionTitle,
                        sectionSubtitle
                );

        GridPane cardsGrid =
                new GridPane();

        cardsGrid.setHgap(20);
        cardsGrid.setVgap(20);

        ColumnConstraints firstColumn =
                createFlexibleColumn();

        ColumnConstraints secondColumn =
                createFlexibleColumn();

        ColumnConstraints thirdColumn =
                createFlexibleColumn();

        cardsGrid.getColumnConstraints().addAll(
                firstColumn,
                secondColumn,
                thirdColumn
        );

        VBox totalMembersCard =
                createCard(
                        "Total Members",
                        String.valueOf(
                                controller.getTotalMembers()
                        ),
                        "Registered Student and Teacher accounts."
                );

        VBox totalBooksCard =
                createCard(
                        "Book Titles",
                        String.valueOf(
                                controller.getTotalBooks()
                        ),
                        "Unique titles available in the catalogue."
                );

        VBox totalCopiesCard =
                createCard(
                        "Total Copies",
                        String.valueOf(
                                controller.getTotalCopies()
                        ),
                        "All physical book copies owned by the library."
                );

        VBox availableCopiesCard =
                createCard(
                        "Available Copies",
                        String.valueOf(
                                controller.getAvailableCopies()
                        ),
                        "Copies currently ready to be issued."
                );

        VBox issuedBooksCard =
                createCard(
                        "Issued Books",
                        String.valueOf(
                                controller.getIssuedBooks()
                        ),
                        "Copies currently held by library members."
                );

        VBox returnedBooksCard =
                createCard(
                        "Returned Books",
                        String.valueOf(
                                controller.getReturnedBooks()
                        ),
                        "Completed borrowing transactions."
                );

        VBox overdueBooksCard =
                createCard(
                        "Overdue Books",
                        String.valueOf(
                                controller.getOverdueBooks()
                        ),
                        "Active loans that passed their due dates."
                );

        VBox reservationsCard =
                createCard(
                        "Active Reservations",
                        String.valueOf(
                                controller.getActiveReservations()
                        ),
                        "Reservations currently waiting or ready for pickup."
                );

        VBox pendingFinesCard =
                createCard(
                        "Pending Fines",
                        String.valueOf(
                                controller.getPendingFinesCount()
                        ),
                        "Outstanding fine records requiring payment."
                );

        VBox pendingAmountCard =
                createCard(
                        "Pending Fine Amount",
                        formatCurrency(
                                controller.getPendingFineAmount()
                        ),
                        "Total value of unpaid member fines."
                );

        VBox collectedAmountCard =
                createCard(
                        "Fine Collected",
                        formatCurrency(
                                controller.getCollectedFineAmount()
                        ),
                        "Total fine payments recorded by the library."
                );

        cardsGrid.add(
                totalMembersCard,
                0,
                0
        );

        cardsGrid.add(
                totalBooksCard,
                1,
                0
        );

        cardsGrid.add(
                totalCopiesCard,
                2,
                0
        );

        cardsGrid.add(
                availableCopiesCard,
                0,
                1
        );

        cardsGrid.add(
                issuedBooksCard,
                1,
                1
        );

        cardsGrid.add(
                returnedBooksCard,
                2,
                1
        );

        cardsGrid.add(
                overdueBooksCard,
                0,
                2
        );

        cardsGrid.add(
                reservationsCard,
                1,
                2
        );

        cardsGrid.add(
                pendingFinesCard,
                2,
                2
        );

        cardsGrid.add(
                pendingAmountCard,
                0,
                3
        );

        cardsGrid.add(
                collectedAmountCard,
                1,
                3
        );

        VBox content =
                new VBox(
                        24,
                        sectionHeading,
                        cardsGrid
                );

        content.setPadding(
                new Insets(
                        30,
                        35,
                        40,
                        35
                )
        );

        content.setStyle(
                "-fx-background-color:#e8f1f2;"
        );

        ScrollPane scrollPane =
                new ScrollPane(content);

        scrollPane.setFitToWidth(true);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setStyle(
                "-fx-background-color:#e8f1f2;" +
                "-fx-background:#e8f1f2;" +
                "-fx-border-color:transparent;"
        );

        return scrollPane;
    }

    private ColumnConstraints createFlexibleColumn() {

        ColumnConstraints column =
                new ColumnConstraints();

        column.setPercentWidth(
                33.33
        );

        column.setHgrow(
                Priority.ALWAYS
        );

        column.setFillWidth(true);

        return column;
    }

    private VBox createCard(
            String cardTitle,
            String cardValue,
            String description
    ) {

        Label titleLabel =
                new Label(cardTitle);

        titleLabel.setWrapText(true);

        titleLabel.setStyle(
                "-fx-text-fill:#334155;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;"
        );

        Label valueLabel =
                new Label(cardValue);

        valueLabel.setWrapText(true);

        valueLabel.setStyle(
                "-fx-text-fill:#0f766e;" +
                "-fx-font-size:29px;" +
                "-fx-font-weight:bold;"
        );

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setWrapText(true);

        descriptionLabel.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:13px;" +
                "-fx-line-spacing:2px;"
        );

        VBox card =
                new VBox(
                        11,
                        titleLabel,
                        valueLabel,
                        descriptionLabel
                );

        card.setAlignment(
                Pos.CENTER_LEFT
        );

        card.setPadding(
                new Insets(22)
        );

        card.setMaxWidth(
                Double.MAX_VALUE
        );

        card.setMinHeight(150);

        String normalStyle =
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
                ");";

        String hoverStyle =
                "-fx-background-color:#ecfeff;" +
                "-fx-background-radius:16;" +
                "-fx-border-color:#0f766e;" +
                "-fx-border-radius:16;" +
                "-fx-border-width:2;" +
                "-fx-effect:dropshadow(" +
                "gaussian," +
                "rgba(15,118,110,0.17)," +
                "18," +
                "0.18," +
                "0," +
                "5" +
                ");";

        card.setStyle(normalStyle);
        card.setCursor(Cursor.DEFAULT);

        card.setOnMouseEntered(event ->
                card.setStyle(
                        hoverStyle
                )
        );

        card.setOnMouseExited(event ->
                card.setStyle(
                        normalStyle
                )
        );

        return card;
    }

    private Button createPrimaryButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefWidth(160);
        button.setPrefHeight(44);
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

    private void playEntranceAnimation(
            VBox header,
            ScrollPane content
    ) {

        header.setOpacity(0);
        header.setTranslateY(-16);

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

        headerSlide.setFromY(-16);
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

    private String formatCurrency(
            BigDecimal amount
    ) {

        if (amount == null) {
            return "Rs. 0.00";
        }

        return "Rs. "
                + amount.setScale(
                        2,
                        RoundingMode.HALF_UP
                ).toPlainString();
    }

    private Stage getStage(
            Button button
    ) {

        return (Stage) button
                .getScene()
                .getWindow();
    }
}