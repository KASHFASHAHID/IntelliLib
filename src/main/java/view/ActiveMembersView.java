package view;

import controller.ActiveMemberController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.ActiveMember;
import model.User;
import util.SceneRouter;

import java.util.List;
import java.util.Optional;

public class ActiveMembersView {

    private final User loggedInUser;
    private final ActiveMemberController controller;

    private TableView<ActiveMember> table;
    private Label summaryLabel;
    private ChoiceBox<String> statusFilter;

    public ActiveMembersView(
            User loggedInUser
    ) {
        this.loggedInUser = loggedInUser;
        this.controller = new ActiveMemberController();
    }

    public Scene createScene() {

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color:#0f172a;"
        );

        table =
                createMembersTable();

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

        loadMembers();

        return new Scene(
                root,
                1200,
                760
        );
    }

    private VBox createHeader() {

        Label title =
                new Label(
                        "Member Account Management"
                );

        title.setStyle(
                "-fx-text-fill:white;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        Label subtitle =
                new Label(
                        "Manage student and teacher account access"
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

        Button refreshButton =
                new Button(
                        "Refresh"
                );

        Button backButton =
                new Button(
                        "Back to Dashboard"
                );

        stylePrimaryButton(
                refreshButton
        );

        styleSecondaryButton(
                backButton
        );

        refreshButton.setOnAction(event ->
                loadMembers()
        );

        backButton.setOnAction(event -> {

            Stage stage =
                    getStage(
                            backButton
                    );

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

        HBox headerRow =
                new HBox(
                        15,
                        titleBox,
                        spacer,
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

    private HBox createFilterBar() {

        Label filterLabel =
                new Label(
                        "Show:"
                );

        filterLabel.setStyle(
                "-fx-text-fill:#e2e8f0;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;"
        );

        statusFilter =
                new ChoiceBox<>();

        statusFilter.getItems().addAll(
                "ALL",
                "ACTIVE",
                "SUSPENDED",
                "BLOCKED"
        );

        statusFilter.setValue(
                "ALL"
        );

        statusFilter.setPrefWidth(
                170
        );

        statusFilter.setOnAction(event ->
                applyStatusFilter()
        );

        Label helpLabel =
                new Label(
                        "Select a member below to change account status."
                );

        helpLabel.setStyle(
                "-fx-text-fill:#94a3b8;" +
                "-fx-font-size:13px;"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox filterBar =
                new HBox(
                        12,
                        filterLabel,
                        statusFilter,
                        spacer,
                        helpLabel
                );

        filterBar.setAlignment(
                Pos.CENTER_LEFT
        );

        return filterBar;
    }

    private TableView<ActiveMember> createMembersTable() {

        TableView<ActiveMember> membersTable =
                new TableView<>();

        membersTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        membersTable.setPlaceholder(
                new Label(
                        "No members found for the selected status."
                )
        );

        membersTable.setStyle(
                "-fx-background-color:#111827;" +
                "-fx-border-color:#334155;" +
                "-fx-border-radius:12;" +
                "-fx-background-radius:12;"
        );

        TableColumn<ActiveMember, String> userIdColumn =
                new TableColumn<>(
                        "User ID"
                );

        userIdColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "userId"
                )
        );

        TableColumn<ActiveMember, String> nameColumn =
                new TableColumn<>(
                        "Name"
                );

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "name"
                )
        );

        TableColumn<ActiveMember, String> emailColumn =
                new TableColumn<>(
                        "Email"
                );

        emailColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "email"
                )
        );

        TableColumn<ActiveMember, String> phoneColumn =
                new TableColumn<>(
                        "Phone"
                );

        phoneColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "phone"
                )
        );

        TableColumn<ActiveMember, String> roleColumn =
                new TableColumn<>(
                        "Role"
                );

        roleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getRole() == null
                                ? ""
                                : cellData.getValue()
                                        .getRole()
                                        .toString()
                )
        );

        TableColumn<ActiveMember, String> departmentColumn =
                new TableColumn<>(
                        "Department"
                );

        departmentColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "department"
                )
        );

        TableColumn<ActiveMember, String> statusColumn =
                new TableColumn<>(
                        "Status"
                );

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "accountStatus"
                )
        );

        membersTable.getColumns().addAll(
                userIdColumn,
                nameColumn,
                emailColumn,
                phoneColumn,
                roleColumn,
                departmentColumn,
                statusColumn
        );

        return membersTable;
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

        suspendButton.setOnAction(event ->
                handleStatusChange(
                        "SUSPENDED"
                )
        );

        blockButton.setOnAction(event ->
                handleStatusChange(
                        "BLOCKED"
                )
        );

        reactivateButton.setOnAction(event ->
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

    private void loadMembers() {

        List<ActiveMember> members =
                controller.getAllManageableMembers();

        table.setItems(
                FXCollections.observableArrayList(
                        members
                )
        );

        applyStatusFilter();
    }

    private void applyStatusFilter() {

        if (table == null
                || statusFilter == null) {

            return;
        }

        String selectedStatus =
                statusFilter.getValue();

        List<ActiveMember> members =
                controller.getAllManageableMembers();

        if (selectedStatus == null
                || "ALL".equalsIgnoreCase(
                        selectedStatus
                )) {

            table.setItems(
                    FXCollections.observableArrayList(
                            members
                    )
            );

        } else {

            List<ActiveMember> filteredMembers =
                    members.stream()
                            .filter(member ->
                                    selectedStatus.equalsIgnoreCase(
                                            member.getAccountStatus()
                                    )
                            )
                            .toList();

            table.setItems(
                    FXCollections.observableArrayList(
                            filteredMembers
                    )
            );
        }

        updateSummary();
    }

    private void updateSummary() {

        long activeCount =
                table.getItems()
                        .stream()
                        .filter(member ->
                                "ACTIVE".equalsIgnoreCase(
                                        member.getAccountStatus()
                                )
                        )
                        .count();

        long suspendedCount =
                table.getItems()
                        .stream()
                        .filter(member ->
                                "SUSPENDED".equalsIgnoreCase(
                                        member.getAccountStatus()
                                )
                        )
                        .count();

        long blockedCount =
                table.getItems()
                        .stream()
                        .filter(member ->
                                "BLOCKED".equalsIgnoreCase(
                                        member.getAccountStatus()
                                )
                        )
                        .count();

        summaryLabel.setText(
                "Displayed: "
                        + table.getItems().size()
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

        ActiveMember selectedMember =
                table.getSelectionModel()
                        .getSelectedItem();

        if (selectedMember == null) {

            showWarning(
                    "Member Required",
                    "Please select a member from the table."
            );

            return;
        }

        String currentStatus =
                selectedMember.getAccountStatus() == null
                        ? ""
                        : selectedMember
                                .getAccountStatus()
                                .trim()
                                .toUpperCase();

        if (currentStatus.equals(
                newStatus
        )) {

            showWarning(
                    "No Status Change",
                    selectedMember.getName()
                            + " is already "
                            + newStatus
                            + "."
            );

            return;
        }

        if (!isAllowedUiTransition(
                currentStatus,
                newStatus
        )) {

            showWarning(
                    "Invalid Status Change",
                    getInvalidTransitionMessage(
                            currentStatus,
                            newStatus
                    )
            );

            return;
        }

        if ("ACTIVE".equals(
                newStatus
        )) {

            String blockReason =
                    controller.getReactivationBlockReason(
                            selectedMember.getUserId()
                    );

            if (blockReason != null) {

                showWarning(
                        "Reactivation Not Allowed",
                        blockReason
                );

                return;
            }
        }

        String actionName =
                getActionName(
                        newStatus
                );

        TextInputDialog reasonDialog =
                new TextInputDialog();

        reasonDialog.setTitle(
                actionName + " Member"
        );

        reasonDialog.setHeaderText(
                actionName
                        + " account for "
                        + selectedMember.getName()
                        + " ("
                        + selectedMember.getUserId()
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
                reasonResult.get()
                        .trim();

        if (reason.isBlank()) {

            showWarning(
                    "Reason Required",
                    "Please enter a valid reason for this account action."
            );

            return;
        }

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Confirm Account Action"
        );

        confirmation.setHeaderText(
                actionName
                        + " "
                        + selectedMember.getName()
                        + "'s account?"
        );

        confirmation.setContentText(
                "Current Status: "
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
                        selectedMember,
                        newStatus,
                        reason
                );

        if (!success) {

            showError(
                    "Account Update Failed",
                    "The member account status could not be changed."
            );

            return;
        }

        Alert successAlert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        successAlert.setTitle(
                "Account Updated"
        );

        successAlert.setHeaderText(
                "Member account updated successfully."
        );

        successAlert.setContentText(
                selectedMember.getName()
                        + "'s account is now "
                        + newStatus
                        + "."
        );

        successAlert.showAndWait();

        loadMembers();
    }

    private boolean performStatusChange(
            ActiveMember selectedMember,
            String newStatus,
            String reason
    ) {

        return switch (newStatus) {

            case "SUSPENDED" ->
                    controller.suspendMember(
                            selectedMember.getUserId(),
                            reason,
                            loggedInUser.getUserId()
                    );

            case "BLOCKED" ->
                    controller.blockMember(
                            selectedMember.getUserId(),
                            reason,
                            loggedInUser.getUserId()
                    );

            case "ACTIVE" ->
                    controller.reactivateMember(
                            selectedMember.getUserId(),
                            reason,
                            loggedInUser.getUserId()
                    );

            default ->
                    false;
        };
    }

    private boolean isAllowedUiTransition(
            String currentStatus,
            String newStatus
    ) {

        return switch (newStatus) {

            case "SUSPENDED" ->
                    "ACTIVE".equals(
                            currentStatus
                    );

            case "BLOCKED" ->
                    "ACTIVE".equals(
                            currentStatus
                    )
                            || "SUSPENDED".equals(
                            currentStatus
                    );

            case "ACTIVE" ->
                    "SUSPENDED".equals(
                            currentStatus
                    )
                            || "BLOCKED".equals(
                            currentStatus
                    );

            default ->
                    false;
        };
    }

    private String getInvalidTransitionMessage(
            String currentStatus,
            String newStatus
    ) {

        if ("SUSPENDED".equals(
                newStatus
        )) {

            return "Only an ACTIVE member can be suspended.";
        }

        if ("BLOCKED".equals(
                newStatus
        )) {

            return "Only an ACTIVE or SUSPENDED member can be blocked.";
        }

        if ("ACTIVE".equals(
                newStatus
        )) {

            return "Only a SUSPENDED or BLOCKED member can be reactivated.";
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

        button.setPrefWidth(120);
        button.setPrefHeight(42);

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

        button.setPrefWidth(170);
        button.setPrefHeight(42);

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

        button.setPrefWidth(170);
        button.setPrefHeight(44);

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

        button.setPrefWidth(160);
        button.setPrefHeight(44);

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

        button.setPrefWidth(180);
        button.setPrefHeight(44);

        button.setStyle(
                "-fx-background-color:#16a34a;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:9;"
        );
    }

    private Stage getStage(
            Button button
    ) {

        return (Stage) button
                .getScene()
                .getWindow();
    }
}