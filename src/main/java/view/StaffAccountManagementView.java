package view;

import controller.StaffAccountController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Role;
import model.StaffAccount;
import model.User;
import util.SceneRouter;

import java.util.List;
import java.util.Optional;

public class StaffAccountManagementView {

    private final User loggedInUser;
    private final StaffAccountController controller;

    private TableView<StaffAccount> table;
    private ChoiceBox<String> statusFilter;
    private Label summaryLabel;

    public StaffAccountManagementView(
            User loggedInUser
    ) {

        this.loggedInUser = loggedInUser;
        this.controller = new StaffAccountController();
    }

    public Scene createScene() {

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color:#0f172a;"
        );

        table =
                createStaffTable();

        statusFilter =
                createStatusFilter();

        summaryLabel =
                createSummaryLabel();

        VBox content =
                new VBox(
                        18,
                        createFilterBar(),
                        summaryLabel,
                        table,
                        createActionBar()
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

        loadStaffAccounts();

        return new Scene(
                root,
                1200,
                760
        );
    }

    private VBox createHeader() {

        Label title =
                new Label(
                        "Staff Account Management"
                );

        title.setStyle(
                "-fx-text-fill:white;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        Label subtitle =
                new Label(
                        "Manage Admin and Librarian account access"
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

        Button createStaffButton =
                new Button(
                        "Create Staff Account"
                );

        Button refreshButton =
                new Button(
                        "Refresh"
                );

        Button backButton =
                new Button(
                        "Back to Dashboard"
                );

        styleSuccessButton(
                createStaffButton
        );

        stylePrimaryButton(
                refreshButton
        );

        styleSecondaryButton(
                backButton
        );

        createStaffButton.setPrefWidth(
                190
        );

        createStaffButton.setOnAction(
                event ->
                        handleCreateStaffAccount()
        );

        refreshButton.setOnAction(
                event ->
                        loadStaffAccounts()
        );

        backButton.setOnAction(event -> {

            Stage stage =
                    getStage(
                            backButton
                    );

            SceneRouter.open(
        stage,
        new SuperAdminDashboardView(
                loggedInUser
        ).createScene(),
        "IntelliLib - Super Admin Dashboard"
);
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
                        createStaffButton,
                        refreshButton,
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

    private void handleCreateStaffAccount() {

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Create Staff Account"
        );

        dialog.setHeaderText(
                "Create a pending Admin or Librarian account"
        );

        TextField nameField =
                new TextField();

        nameField.setPromptText(
                "Full name"
        );

        TextField emailField =
                new TextField();

        emailField.setPromptText(
                "Valid email address"
        );

        ComboBox<Role> roleBox =
                new ComboBox<>();

        roleBox.getItems().addAll(
                Role.ADMIN,
                Role.LIBRARIAN
        );

        roleBox.setPromptText(
                "Select role"
        );

        roleBox.setMaxWidth(
                Double.MAX_VALUE
        );

        GridPane form =
                new GridPane();

        form.setHgap(12);
        form.setVgap(15);

        form.setPadding(
                new Insets(10)
        );

        form.add(
                new Label("Name:"),
                0,
                0
        );

        form.add(
                nameField,
                1,
                0
        );

        form.add(
                new Label("Email:"),
                0,
                1
        );

        form.add(
                emailField,
                1,
                1
        );

        form.add(
                new Label("Role:"),
                0,
                2
        );

        form.add(
                roleBox,
                1,
                2
        );

        dialog.getDialogPane()
                .setContent(form);

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        ButtonType.OK,
                        ButtonType.CANCEL
                );

        Optional<ButtonType> result =
                dialog.showAndWait();

        if (result.isEmpty()
                || result.get()
                != ButtonType.OK) {

            return;
        }

        String name =
                nameField.getText() == null
                        ? ""
                        : nameField
                                .getText()
                                .trim();

        String email =
                emailField.getText() == null
                        ? ""
                        : emailField
                                .getText()
                                .trim();

        Role selectedRole =
                roleBox.getValue();

        if (name.length() < 3) {

            showWarning(
                    "Invalid Name",
                    "Please enter the staff member's full name."
            );

            return;
        }

        if (name.length() > 100) {

            showWarning(
                    "Name Too Long",
                    "The name cannot exceed 100 characters."
            );

            return;
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        )) {

            showWarning(
                    "Invalid Email",
                    "Please enter a valid email address."
            );

            return;
        }

        if (email.length() > 100) {

            showWarning(
                    "Email Too Long",
                    "The email cannot exceed 100 characters."
            );

            return;
        }

        if (selectedRole == null) {

            showWarning(
                    "Role Required",
                    "Please select ADMIN or LIBRARIAN."
            );

            return;
        }

        String newUserId =
                controller.createStaffAccount(
                        name,
                        email,
                        selectedRole,
                        loggedInUser.getUserId()
                );

        if (newUserId == null) {

            showError(
                    "Account Creation Failed",
                    "The staff account could not be created. "
                            + "The email may already be registered."
            );

            return;
        }

        Alert successAlert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        successAlert.setTitle(
                "Staff Account Created"
        );

        successAlert.setHeaderText(
                "Pending staff account created successfully."
        );

        successAlert.setContentText(
                "User ID: "
                        + newUserId
                        + "\nRole: "
                        + selectedRole
                        + "\nStatus: PENDING_ACTIVATION"
                        + "\n\nThe staff member must open Account Access, "
                        + "select Activate New Account, verify the OTP, "
                        + "and create their own password."
        );

        successAlert.showAndWait();

        statusFilter.setValue(
                "ALL"
        );

        loadStaffAccounts();
    }

    private HBox createFilterBar() {

        Label filterLabel =
                new Label(
                        "Status:"
                );

        filterLabel.setStyle(
                "-fx-text-fill:#e2e8f0;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label instruction =
                new Label(
                        "Select a staff account before choosing an action."
                );

        instruction.setStyle(
                "-fx-text-fill:#94a3b8;" +
                "-fx-font-size:13px;"
        );

        HBox filterBar =
                new HBox(
                        12,
                        filterLabel,
                        statusFilter,
                        spacer,
                        instruction
                );

        filterBar.setAlignment(
                Pos.CENTER_LEFT
        );

        return filterBar;
    }

    private ChoiceBox<String> createStatusFilter() {

        ChoiceBox<String> filter =
                new ChoiceBox<>();

        filter.getItems().addAll(
                "ALL",
                "PENDING_ACTIVATION",
                "ACTIVE",
                "SUSPENDED",
                "BLOCKED"
        );

        filter.setValue(
                "ALL"
        );

        filter.setPrefWidth(
                190
        );

        filter.setOnAction(
                event ->
                        applyStatusFilter()
        );

        return filter;
    }

    private TableView<StaffAccount> createStaffTable() {

        TableView<StaffAccount> staffTable =
                new TableView<>();

        staffTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        staffTable.setPlaceholder(
                new Label(
                        "No staff accounts found."
                )
        );

        staffTable.setStyle(
                "-fx-background-color:#111827;" +
                "-fx-border-color:#334155;" +
                "-fx-border-radius:12;" +
                "-fx-background-radius:12;"
        );

        TableColumn<StaffAccount, String>
                userIdColumn =
                new TableColumn<>(
                        "User ID"
                );

        userIdColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "userId"
                )
        );

        TableColumn<StaffAccount, String>
                nameColumn =
                new TableColumn<>(
                        "Name"
                );

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "name"
                )
        );

        TableColumn<StaffAccount, String>
                emailColumn =
                new TableColumn<>(
                        "Email"
                );

        emailColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "email"
                )
        );

        TableColumn<StaffAccount, String>
                roleColumn =
                new TableColumn<>(
                        "Role"
                );

        roleColumn.setCellValueFactory(
                cellData ->
                        new javafx.beans.property
                                .SimpleStringProperty(
                                cellData
                                        .getValue()
                                        .getRole() == null
                                        ? ""
                                        : cellData
                                                .getValue()
                                                .getRole()
                                                .toString()
                        )
        );

        TableColumn<StaffAccount, String>
                statusColumn =
                new TableColumn<>(
                        "Status"
                );

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "accountStatus"
                )
        );

        staffTable.getColumns().addAll(
                userIdColumn,
                nameColumn,
                emailColumn,
                roleColumn,
                statusColumn
        );

        return staffTable;
    }

    private Label createSummaryLabel() {

        Label label =
                new Label();

        label.setStyle(
                "-fx-text-fill:#38bdf8;" +
                "-fx-font-size:17px;" +
                "-fx-font-weight:bold;"
        );

        return label;
    }

    private HBox createActionBar() {

        Button suspendButton =
                new Button(
                        "Suspend Account"
                );

        Button blockButton =
                new Button(
                        "Block Account"
                );

        Button reactivateButton =
                new Button(
                        "Reactivate Account"
                );

        styleWarningButton(
                suspendButton
        );

        styleDangerButton(
                blockButton
        );

        styleSuccessButton(
                reactivateButton
        );

        suspendButton.setOnAction(
                event ->
                        handleStatusChange(
                                "SUSPENDED"
                        )
        );

        blockButton.setOnAction(
                event ->
                        handleStatusChange(
                                "BLOCKED"
                        )
        );

        reactivateButton.setOnAction(
                event ->
                        handleStatusChange(
                                "ACTIVE"
                        )
        );

        HBox actionBar =
                new HBox(
                        15,
                        suspendButton,
                        blockButton,
                        reactivateButton
                );

        actionBar.setAlignment(
                Pos.CENTER_RIGHT
        );

        return actionBar;
    }

    private void loadStaffAccounts() {

        List<StaffAccount> staffAccounts =
                controller.getAllStaffAccounts(
                        loggedInUser.getUserId()
                );

        table.setItems(
                FXCollections.observableArrayList(
                        staffAccounts
                )
        );

        applyStatusFilter();
    }

    private void applyStatusFilter() {

        if (table == null
                || statusFilter == null
                || loggedInUser == null) {

            return;
        }

        List<StaffAccount> allAccounts =
                controller.getAllStaffAccounts(
                        loggedInUser.getUserId()
                );

        String selectedStatus =
                statusFilter.getValue();

        if (selectedStatus == null
                || "ALL".equalsIgnoreCase(
                        selectedStatus
                )) {

            table.setItems(
                    FXCollections.observableArrayList(
                            allAccounts
                    )
            );

        } else {

            List<StaffAccount> filteredAccounts =
                    allAccounts.stream()
                            .filter(account ->
                                    selectedStatus.equalsIgnoreCase(
                                            account.getAccountStatus()
                                    )
                            )
                            .toList();

            table.setItems(
                    FXCollections.observableArrayList(
                            filteredAccounts
                    )
            );
        }

        updateSummary();
    }

    private void updateSummary() {

        long pendingCount =
                table.getItems()
                        .stream()
                        .filter(account ->
                                "PENDING_ACTIVATION"
                                        .equalsIgnoreCase(
                                                account.getAccountStatus()
                                        )
                        )
                        .count();

        long activeCount =
                table.getItems()
                        .stream()
                        .filter(account ->
                                "ACTIVE"
                                        .equalsIgnoreCase(
                                                account.getAccountStatus()
                                        )
                        )
                        .count();

        long suspendedCount =
                table.getItems()
                        .stream()
                        .filter(account ->
                                "SUSPENDED"
                                        .equalsIgnoreCase(
                                                account.getAccountStatus()
                                        )
                        )
                        .count();

        long blockedCount =
                table.getItems()
                        .stream()
                        .filter(account ->
                                "BLOCKED"
                                        .equalsIgnoreCase(
                                                account.getAccountStatus()
                                        )
                        )
                        .count();

        summaryLabel.setText(
                "Displayed: "
                        + table.getItems().size()
                        + "   |   Pending: "
                        + pendingCount
                        + "   |   Active: "
                        + activeCount
                        + "   |   Suspended: "
                        + suspendedCount
                        + "   |   Blocked: "
                        + blockedCount
        );
    }

    private void handleStatusChange(
            String newStatus
    ) {

        StaffAccount selectedAccount =
                table.getSelectionModel()
                        .getSelectedItem();

        if (selectedAccount == null) {

            showWarning(
                    "Staff Account Required",
                    "Please select an Admin or Librarian account."
            );

            return;
        }

        String currentStatus =
                selectedAccount
                        .getAccountStatus() == null
                        ? ""
                        : selectedAccount
                                .getAccountStatus()
                                .trim()
                                .toUpperCase();

        if ("PENDING_ACTIVATION"
                .equals(currentStatus)) {

            showWarning(
                    "Account Not Activated",
                    "This account is still waiting for activation. "
                            + "The staff member must activate it first."
            );

            return;
        }

        if (currentStatus.equals(
                newStatus
        )) {

            showWarning(
                    "No Status Change",
                    selectedAccount.getName()
                            + " is already "
                            + newStatus
                            + "."
            );

            return;
        }

        if (!isValidTransition(
                currentStatus,
                newStatus
        )) {

            showWarning(
                    "Invalid Status Change",
                    getInvalidTransitionMessage(
                            newStatus
                    )
            );

            return;
        }

        TextInputDialog reasonDialog =
                new TextInputDialog();

        reasonDialog.setTitle(
                getActionName(newStatus)
                        + " Staff Account"
        );

        reasonDialog.setHeaderText(
                getActionName(newStatus)
                        + " "
                        + selectedAccount.getName()
                        + " ("
                        + selectedAccount.getUserId()
                        + ")"
        );

        reasonDialog.setContentText(
                "Reason:"
        );

        Optional<String> reasonResult =
                reasonDialog.showAndWait();

        if (reasonResult.isEmpty()) {
            return;
        }

        String reason =
                reasonResult
                        .get()
                        .trim();

        if (reason.length() < 5) {

            showWarning(
                    "Invalid Reason",
                    "The reason must contain at least 5 characters."
            );

            return;
        }

        if (reason.length() > 500) {

            showWarning(
                    "Reason Too Long",
                    "The reason cannot exceed 500 characters."
            );

            return;
        }

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Confirm Staff Account Action"
        );

        confirmation.setHeaderText(
                getActionName(newStatus)
                        + " "
                        + selectedAccount.getName()
                        + "'s account?"
        );

        confirmation.setContentText(
                "Role: "
                        + selectedAccount.getRole()
                        + "\nCurrent Status: "
                        + currentStatus
                        + "\nNew Status: "
                        + newStatus
                        + "\nReason: "
                        + reason
        );

        Optional<ButtonType> confirmationResult =
                confirmation.showAndWait();

        if (confirmationResult.isEmpty()
                || confirmationResult.get()
                != ButtonType.OK) {

            return;
        }

        boolean success =
                performStatusChange(
                        selectedAccount,
                        newStatus,
                        reason
                );

        if (!success) {

            showError(
                    "Account Update Failed",
                    "The staff account could not be updated. "
                            + "Confirm that you are logged in as an active Super Admin."
            );

            return;
        }

        Alert successAlert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        successAlert.setTitle(
                "Staff Account Updated"
        );

        successAlert.setHeaderText(
                "The account status was updated successfully."
        );

        successAlert.setContentText(
                selectedAccount.getName()
                        + "'s account is now "
                        + newStatus
                        + "."
        );

        successAlert.showAndWait();

        loadStaffAccounts();
    }

    private boolean performStatusChange(
            StaffAccount selectedAccount,
            String newStatus,
            String reason
    ) {

        return switch (newStatus) {

            case "SUSPENDED" ->
                    controller.suspendStaff(
                            selectedAccount.getUserId(),
                            reason,
                            loggedInUser.getUserId()
                    );

            case "BLOCKED" ->
                    controller.blockStaff(
                            selectedAccount.getUserId(),
                            reason,
                            loggedInUser.getUserId()
                    );

            case "ACTIVE" ->
                    controller.reactivateStaff(
                            selectedAccount.getUserId(),
                            reason,
                            loggedInUser.getUserId()
                    );

            default -> false;
        };
    }

    private boolean isValidTransition(
            String currentStatus,
            String newStatus
    ) {

        return switch (newStatus) {

            case "SUSPENDED" ->
                    "ACTIVE"
                            .equals(currentStatus);

            case "BLOCKED" ->
                    "ACTIVE"
                            .equals(currentStatus)
                            || "SUSPENDED"
                            .equals(currentStatus);

            case "ACTIVE" ->
                    "SUSPENDED"
                            .equals(currentStatus)
                            || "BLOCKED"
                            .equals(currentStatus);

            default -> false;
        };
    }

    private String getInvalidTransitionMessage(
            String newStatus
    ) {

        if ("SUSPENDED".equals(
                newStatus
        )) {

            return "Only an ACTIVE staff account can be suspended.";
        }

        if ("BLOCKED".equals(
                newStatus
        )) {

            return "Only an ACTIVE or SUSPENDED staff account can be blocked.";
        }

        if ("ACTIVE".equals(
                newStatus
        )) {

            return "Only a SUSPENDED or BLOCKED staff account can be reactivated.";
        }

        return "This account status change is not allowed.";
    }

    private String getActionName(
            String newStatus
    ) {

        return switch (newStatus) {

            case "SUSPENDED" ->
                    "Suspend";

            case "BLOCKED" ->
                    "Block";

            case "ACTIVE" ->
                    "Reactivate";

            default ->
                    "Update";
        };
    }

    private void showWarning(
            String title,
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.WARNING
                );

        alert.setTitle(
                title
        );

        alert.setHeaderText(
                message
        );

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

        alert.setTitle(
                title
        );

        alert.setHeaderText(
                message
        );

        alert.showAndWait();
    }

    private void stylePrimaryButton(
            Button button
    ) {

        button.setPrefWidth(
                120
        );

        button.setPrefHeight(
                42
        );

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

        button.setPrefWidth(
                170
        );

        button.setPrefHeight(
                42
        );

        button.setStyle(
                "-fx-background-color:#334155;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:9;"
        );
    }

    private void styleWarningButton(
            Button button
    ) {

        button.setPrefWidth(
                170
        );

        button.setPrefHeight(
                44
        );

        button.setStyle(
                "-fx-background-color:#d97706;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:9;"
        );
    }

    private void styleDangerButton(
            Button button
    ) {

        button.setPrefWidth(
                160
        );

        button.setPrefHeight(
                44
        );

        button.setStyle(
                "-fx-background-color:#dc2626;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:9;"
        );
    }

    private void styleSuccessButton(
            Button button
    ) {

        button.setPrefWidth(
                180
        );

        button.setPrefHeight(
                44
        );

        button.setStyle(
                "-fx-background-color:#16a34a;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:9;"
        );
    }

    private Stage getStage(
            javafx.scene.Node node
    ) {

        return (Stage) node
                .getScene()
                .getWindow();
    }
}