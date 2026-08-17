package view;

import controller.ActivityLogController;
import controller.DashboardController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.DashboardStatistics;
import model.Role;
import model.User;
import util.SceneRouter;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AdminDashboardView {

    private final User loggedInUser;
    private final DashboardController dashboardController;

    public AdminDashboardView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.dashboardController = new DashboardController();
    }

    public Scene createScene() {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color:#e8f1f2;"
        );

        root.setTop(createHeader());
        root.setLeft(createSideMenu());
        root.setCenter(createDashboardContent());

        return new Scene(
                root,
                1200,
                760
        );
    }

    private VBox createHeader() {

        Text title =
                new Text("IntelliLib");

        title.setStyle(
                "-fx-fill:#0f172a;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        Label welcomeLabel =
                new Label(
                        "Welcome, "
                                + loggedInUser.getName()
                );

        welcomeLabel.setStyle(
                "-fx-text-fill:#0f766e;" +
                "-fx-font-size:17px;" +
                "-fx-font-weight:bold;"
        );

        Label portalLabel =
                new Label(
                        loggedInUser.getRole()
                                == Role.SUPER_ADMIN
                                ? "Library Administration Portal"
                                : "Administrator Portal"
                );

        portalLabel.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:14px;"
        );

        VBox userInformation =
                new VBox(
                        5,
                        welcomeLabel,
                        portalLabel
                );

        Label accountBadge =
                new Label(
                        loggedInUser.getRole()
                                == Role.SUPER_ADMIN
                                ? "● SUPER ADMIN"
                                : "● ADMIN"
                );

        accountBadge.setStyle(
                "-fx-background-color:#dcfce7;" +
                "-fx-text-fill:#166534;" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:9 16;" +
                "-fx-background-radius:18;" +
                "-fx-border-color:#86efac;" +
                "-fx-border-radius:18;"
        );

        Button refreshButton =
                new Button("Refresh Statistics");

        refreshButton.setPrefWidth(175);
        refreshButton.setPrefHeight(42);
        refreshButton.setCursor(Cursor.HAND);

        String refreshNormalStyle =
                "-fx-background-color:#0f766e;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;";

        String refreshHoverStyle =
                "-fx-background-color:#115e59;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;";

        refreshButton.setStyle(
                refreshNormalStyle
        );

        refreshButton.setOnMouseEntered(event ->
                refreshButton.setStyle(
                        refreshHoverStyle
                )
        );

        refreshButton.setOnMouseExited(event ->
                refreshButton.setStyle(
                        refreshNormalStyle
                )
        );

        refreshButton.setOnAction(event -> {

            Stage stage =
                    getStage(refreshButton);

            SceneRouter.open(
                    stage,
                    new AdminDashboardView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Admin Dashboard"
            );
        });

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox informationRow =
                new HBox(
                        20,
                        userInformation,
                        spacer,
                        accountBadge,
                        refreshButton
                );

        informationRow.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox header =
                new VBox(
                        12,
                        title,
                        informationRow
                );

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

    private VBox createSideMenu() {

        VBox sidebar =
                new VBox(15);

        sidebar.setPrefWidth(285);

        sidebar.setPadding(
                new Insets(
                        25,
                        22,
                        25,
                        22
                )
        );

        sidebar.setStyle(
                "-fx-background-color:#111827;" +
                "-fx-border-color:transparent #1e293b transparent transparent;" +
                "-fx-border-width:0 1 0 0;"
        );

        Label menuTitle =
                new Label("ADMIN MENU");

        menuTitle.setMaxWidth(
                Double.MAX_VALUE
        );

        menuTitle.setStyle(
                "-fx-text-fill:#94a3b8;" +
                "-fx-font-size:12px;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:0 0 5 5;"
        );

        Button membershipButton =
                createMenuButton(
                        "Membership Requests"
                );

        Button inventoryButton =
                createMenuButton(
                        "Manage Inventory"
                );

        Button issueButton =
                createMenuButton(
                        "Issue Book"
                );

        Button returnButton =
                createMenuButton(
                        "Return Book"
                );

        Button readyForPickupButton =
                createMenuButton(
                        "Ready for Pickup"
                );

        Button borrowRecordsButton =
                createMenuButton(
                        "Borrow Records"
                );

        Button finesButton =
                createMenuButton(
                        "Fine Management"
                );

        Button reportsButton =
                createMenuButton(
                        "Reports"
                );

        Button activityLogsButton =
                createMenuButton(
                        "Activity Logs"
                );

        Button settingsButton =
                createMenuButton(
                        "Library Settings"
                );

        Button backToSuperAdminButton =
                createMenuButton(
                        "Back to Super Admin"
                );

        boolean openedBySuperAdmin =
                loggedInUser.getRole()
                        == Role.SUPER_ADMIN;

        backToSuperAdminButton.setVisible(
                openedBySuperAdmin
        );

        backToSuperAdminButton.setManaged(
                openedBySuperAdmin
        );

        VBox navigationButtons =
                new VBox(
                        10,
                        membershipButton,
                        inventoryButton,
                        issueButton,
                        returnButton,
                        readyForPickupButton,
                        borrowRecordsButton,
                        finesButton,
                        reportsButton,
                        activityLogsButton,
                        settingsButton,
                        backToSuperAdminButton
                );

        ScrollPane navigationScrollPane =
                new ScrollPane(
                        navigationButtons
                );

        navigationScrollPane.setFitToWidth(true);

        navigationScrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        navigationScrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        navigationScrollPane.setStyle(
                "-fx-background-color:transparent;" +
                "-fx-background:transparent;" +
                "-fx-border-color:transparent;"
        );

        VBox.setVgrow(
                navigationScrollPane,
                Priority.ALWAYS
        );

        Button logoutButton =
                createLogoutButton("Logout");

        membershipButton.setOnAction(event ->
                openMembershipRequests(
                        getStage(
                                membershipButton
                        )
                )
        );

        inventoryButton.setOnAction(event -> {

            Stage stage =
                    getStage(inventoryButton);

            SceneRouter.open(
                    stage,
                    new InventoryView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Inventory Management"
            );
        });

        issueButton.setOnAction(event -> {

            Stage stage =
                    getStage(issueButton);

            SceneRouter.open(
                    stage,
                    new IssueBookView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Issue Book"
            );
        });

        returnButton.setOnAction(event -> {

            Stage stage =
                    getStage(returnButton);

            SceneRouter.open(
                    stage,
                    new ReturnBookView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Return Book"
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

        borrowRecordsButton.setOnAction(event ->
                openBorrowRecords(
                        getStage(
                                borrowRecordsButton
                        ),
                        "ALL"
                )
        );

        finesButton.setOnAction(event ->
                openFineManagement(
                        getStage(finesButton)
                )
        );

        reportsButton.setOnAction(event -> {

            Stage stage =
                    getStage(reportsButton);

            SceneRouter.open(
                    stage,
                    new ReportsView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Reports"
            );
        });

        activityLogsButton.setOnAction(event -> {

            Stage stage =
                    getStage(
                            activityLogsButton
                    );

            SceneRouter.open(
                    stage,
                    new ActivityLogView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Activity Logs"
            );
        });

        settingsButton.setOnAction(event -> {

            Stage stage =
                    getStage(settingsButton);

            SceneRouter.open(
                    stage,
                    new LibrarySettingsView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Library Settings"
            );
        });

        backToSuperAdminButton.setOnAction(event -> {

            Stage stage =
                    getStage(
                            backToSuperAdminButton
                    );

            SceneRouter.open(
                    stage,
                    new SuperAdminDashboardView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Super Admin Dashboard"
            );
        });

        logoutButton.setOnAction(event -> {

            ActivityLogController activityLogController =
                    new ActivityLogController();

            activityLogController.logActivity(
                    loggedInUser.getUserId(),
                    "LOGOUT",
                    loggedInUser.getRole()
                            + " logged out of the system."
            );

            Stage stage =
                    getStage(logoutButton);

            SceneRouter.open(
                    stage,
                    new LoginView().createScene(),
                    "IntelliLib - Login"
            );
        });

        sidebar.getChildren().addAll(
                menuTitle,
                navigationScrollPane,
                logoutButton
        );

        return sidebar;
    }

    private ScrollPane createDashboardContent() {

        DashboardStatistics statistics =
                dashboardController
                        .getStatistics();

        Text dashboardTitle =
                new Text("Dashboard Overview");

        dashboardTitle.setStyle(
                "-fx-fill:#0f172a;" +
                "-fx-font-size:28px;" +
                "-fx-font-weight:bold;"
        );

        Label dashboardSubtitle =
                new Label(
                        "Live information from the library database"
                );

        dashboardSubtitle.setStyle(
                "-fx-text-fill:#475569;" +
                "-fx-font-size:15px;"
        );

        VBox headingBox =
                new VBox(
                        7,
                        dashboardTitle,
                        dashboardSubtitle
                );

        GridPane statisticsGrid =
                new GridPane();

        statisticsGrid.setHgap(20);
        statisticsGrid.setVgap(20);
        statisticsGrid.setAlignment(
                Pos.TOP_LEFT
        );

        VBox totalTitlesCard =
                createStatisticCard(
                        "Total Book Titles",
                        String.valueOf(
                                statistics
                                        .getTotalBookTitles()
                        ),
                        "Unique books registered in the inventory."
                );

        VBox totalCopiesCard =
                createStatisticCard(
                        "Total Book Copies",
                        String.valueOf(
                                statistics
                                        .getTotalBookCopies()
                        ),
                        "All physical copies owned by the library."
                );

        VBox availableCopiesCard =
                createStatisticCard(
                        "Available Copies",
                        String.valueOf(
                                statistics
                                        .getAvailableCopies()
                        ),
                        "Copies currently ready to be issued."
                );

        VBox issuedCopiesCard =
                createStatisticCard(
                        "Issued Copies",
                        String.valueOf(
                                statistics
                                        .getIssuedCopies()
                        ),
                        "Open the list of currently borrowed books."
                );

        makeCardClickable(
                issuedCopiesCard,
                () -> openBorrowRecords(
                        getStage(
                                issuedCopiesCard
                        ),
                        "ISSUED"
                )
        );

        VBox overdueLoansCard =
                createStatisticCard(
                        "Overdue Loans",
                        String.valueOf(
                                statistics
                                        .getOverdueLoans()
                        ),
                        "Review books that have passed their due dates."
                );

        makeCardClickable(
                overdueLoansCard,
                () -> openBorrowRecords(
                        getStage(
                                overdueLoansCard
                        ),
                        "OVERDUE"
                )
        );

        VBox activeMembersCard =
                createStatisticCard(
                        "Active Members",
                        String.valueOf(
                                statistics
                                        .getActiveMembers()
                        ),
                        "View active Student and Teacher accounts."
                );

        makeCardClickable(
                activeMembersCard,
                () -> {

                    Stage stage =
                            getStage(
                                    activeMembersCard
                            );

                    SceneRouter.open(
                            stage,
                            new ActiveMembersView(
                                    loggedInUser
                            ).createScene(),
                            "IntelliLib - Active Members"
                    );
                }
        );

        VBox pendingRequestsCard =
                createStatisticCard(
                        "Pending Requests",
                        String.valueOf(
                                statistics
                                        .getPendingMembershipRequests()
                        ),
                        "Review membership requests awaiting approval."
                );

        makeCardClickable(
                pendingRequestsCard,
                () -> openMembershipRequests(
                        getStage(
                                pendingRequestsCard
                        )
                )
        );

        VBox reservationsCard =
                createStatisticCard(
                        "Active Reservations",
                        String.valueOf(
                                statistics
                                        .getActiveReservations()
                        ),
                        "View waiting and ready-for-pickup reservations."
                );

        makeCardClickable(
                reservationsCard,
                () -> {

                    Stage stage =
                            getStage(
                                    reservationsCard
                            );

                    SceneRouter.open(
                            stage,
                            new ActiveReservationsView(
                                    loggedInUser
                            ).createScene(),
                            "IntelliLib - Active Reservations"
                    );
                }
        );

        VBox fineAmountCard =
                createStatisticCard(
                        "Pending Fine Amount",
                        formatCurrency(
                                statistics
                                        .getPendingFineAmount()
                        ),
                        "Review and manage outstanding member fines."
                );

        makeCardClickable(
                fineAmountCard,
                () -> openFineManagement(
                        getStage(
                                fineAmountCard
                        )
                )
        );

        statisticsGrid.add(
                totalTitlesCard,
                0,
                0
        );

        statisticsGrid.add(
                totalCopiesCard,
                1,
                0
        );

        statisticsGrid.add(
                availableCopiesCard,
                2,
                0
        );

        statisticsGrid.add(
                issuedCopiesCard,
                0,
                1
        );

        statisticsGrid.add(
                overdueLoansCard,
                1,
                1
        );

        statisticsGrid.add(
                activeMembersCard,
                2,
                1
        );

        statisticsGrid.add(
                pendingRequestsCard,
                0,
                2
        );

        statisticsGrid.add(
                reservationsCard,
                1,
                2
        );

        statisticsGrid.add(
                fineAmountCard,
                2,
                2
        );

        VBox content =
                new VBox(
                        25,
                        headingBox,
                        statisticsGrid
                );

        content.setPadding(
                new Insets(
                        35,
                        40,
                        40,
                        40
                )
        );

        content.setStyle(
                "-fx-background-color:#e8f1f2;"
        );

        ScrollPane scrollPane =
                new ScrollPane(content);

        scrollPane.setFitToWidth(true);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
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

    private VBox createStatisticCard(
            String heading,
            String value,
            String description
    ) {

        Label headingLabel =
                new Label(heading);

        headingLabel.setWrapText(true);

        headingLabel.setStyle(
                "-fx-text-fill:#334155;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;"
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-text-fill:#0f766e;" +
                "-fx-font-size:32px;" +
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
                        13,
                        headingLabel,
                        valueLabel,
                        descriptionLabel
                );

        card.setPadding(
                new Insets(23)
        );

        card.setPrefWidth(250);
        card.setMinWidth(230);
        card.setPrefHeight(165);

        card.setStyle(
                getStatisticCardStyle()
        );

        return card;
    }

    private void makeCardClickable(
            VBox card,
            Runnable action
    ) {

        card.setCursor(Cursor.HAND);

        card.setOnMouseClicked(event ->
                action.run()
        );

        card.setOnMouseEntered(event ->
                card.setStyle(
                        getStatisticCardHoverStyle()
                )
        );

        card.setOnMouseExited(event ->
                card.setStyle(
                        getStatisticCardStyle()
                )
        );
    }

    private String getStatisticCardStyle() {

        return "-fx-background-color:#f8fafc;" +
                "-fx-background-radius:16;" +
                "-fx-border-color:#b6d4d6;" +
                "-fx-border-radius:16;" +
                "-fx-border-width:1;" +
                "-fx-effect:dropshadow(gaussian, rgba(15,23,42,0.10), 14, 0.15, 0, 4);";
    }

    private String getStatisticCardHoverStyle() {

        return "-fx-background-color:#ecfeff;" +
                "-fx-background-radius:16;" +
                "-fx-border-color:#0f766e;" +
                "-fx-border-radius:16;" +
                "-fx-border-width:2;" +
                "-fx-effect:dropshadow(gaussian, rgba(15,118,110,0.18), 18, 0.18, 0, 5);";
    }

    private Button createMenuButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setPrefHeight(46);

        button.setAlignment(
                Pos.CENTER_LEFT
        );

        button.setPadding(
                new Insets(
                        0,
                        18,
                        0,
                        18
                )
        );

        button.setCursor(
                Cursor.HAND
        );

        String normalStyle =
                "-fx-background-color:transparent;" +
                "-fx-text-fill:#e2e8f0;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;";

        String hoverStyle =
                "-fx-background-color:#0f766e;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
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

    private Button createLogoutButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setPrefHeight(48);

        button.setAlignment(
                Pos.CENTER_LEFT
        );

        button.setPadding(
                new Insets(
                        0,
                        18,
                        0,
                        18
                )
        );

        button.setCursor(
                Cursor.HAND
        );

        String normalStyle =
                "-fx-background-color:#7f1d1d;" +
                "-fx-text-fill:#fecaca;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:10;";

        String hoverStyle =
                "-fx-background-color:#dc2626;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;" +
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

    private void openMembershipRequests(
            Stage stage
    ) {

        SceneRouter.open(
                stage,
                new MembershipRequestView(
                        loggedInUser
                ).createScene(),
                "IntelliLib - Membership Requests"
        );
    }

    private void openBorrowRecords(
            Stage stage,
            String filterMode
    ) {

        String normalizedFilter =
                filterMode == null
                        ? "ALL"
                        : filterMode
                                .trim()
                                .toUpperCase();

        String pageTitle =
                switch (normalizedFilter) {

                    case "ISSUED" ->
                            "Currently Borrowed Books";

                    case "OVERDUE" ->
                            "Overdue Loans";

                    default ->
                            "Borrow Records";
                };

        SceneRouter.open(
                stage,
                new AdminBorrowRecordsView(
                        loggedInUser,
                        normalizedFilter
                ).createScene(),
                "IntelliLib - "
                        + pageTitle
        );
    }

    private void openFineManagement(
            Stage stage
    ) {

        SceneRouter.open(
                stage,
                new FineManagementView(
                        loggedInUser
                ).createScene(),
                "IntelliLib- Fine Management"
        );
    }

    private Stage getStage(
            javafx.scene.Node node
    ) {

        return (Stage) node
                .getScene()
                .getWindow();
    }

    private String formatCurrency(
            BigDecimal amount
    ) {

        if (amount == null) {
            return "₹0.00";
        }

        return "₹"
                + amount.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }
}