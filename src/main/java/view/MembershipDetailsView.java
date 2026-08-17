package view;

import controller.MembershipRequestController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.MembershipRequest;
import model.User;
import util.SceneRouter;

import java.util.Optional;
import javafx.scene.control.ButtonType;

public class MembershipDetailsView {

    private final MembershipRequest request;
    private final User loggedInUser;
    private final MembershipRequestController controller;

    public MembershipDetailsView(
            MembershipRequest request,
            User loggedInUser
    ) {

        this.request = request;
        this.loggedInUser = loggedInUser;
        this.controller = new MembershipRequestController();
    }

    public Scene createScene() {

        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color:#0f172a;");

        VBox card = new VBox(22);
        card.setPadding(new Insets(35));
        card.setMaxWidth(760);

        card.setStyle(
                "-fx-background-color:#111827;" +
                "-fx-background-radius:22;" +
                "-fx-border-color:#334155;" +
                "-fx-border-radius:22;" +
                "-fx-border-width:1;"
        );

        Label title = new Label("Membership Details");

        title.setStyle(
                "-fx-text-fill:white;" +
                "-fx-font-size:34px;" +
                "-fx-font-weight:bold;"
        );

        GridPane grid = new GridPane();
        grid.setHgap(35);
        grid.setVgap(16);

        addRow(grid, 0, "Full Name", request.getFullName());
        addRow(grid, 1, "Brainware ID", request.getBrainwareId());
        addRow(grid, 2, "University", request.getUniversity());
        addRow(grid, 3, "Role", request.getRoleRequested());
        addRow(
                grid,
                4,
                "Course / Designation",
                request.getCourseOrDesignation()
        );
        addRow(grid, 5, "Department", request.getDepartment());

        String semester =
                request.getSemester() == null
                        ? "-"
                        : String.valueOf(request.getSemester());

        addRow(grid, 6, "Semester", semester);
        addRow(grid, 7, "Email", request.getEmail());
        addRow(grid, 8, "Phone", request.getPhone());
        addRow(grid, 9, "Reason", request.getReason());

        Button approveButton = new Button("Approve");
        Button rejectButton = new Button("Reject");
        Button backButton = new Button("Back");

        styleButton(approveButton, "#16a34a");
        styleButton(rejectButton, "#dc2626");
        styleButton(backButton, "#475569");

        approveButton.setOnAction(event -> {

            boolean confirmed = showApprovalConfirmation();

            if (!confirmed) {
                return;
            }

            approveButton.setDisable(true);
            rejectButton.setDisable(true);
            approveButton.setText("Approving...");

            String[] result =
                    controller.approveRequest(
                            request,
                            loggedInUser.getUserId()
                    );

            approveButton.setDisable(false);
            rejectButton.setDisable(false);
            approveButton.setText("Approve");

            if (result == null) {

                showError(
                        "Approval Failed",
                        "The membership request could not be approved.",
                        "Check for a duplicate User ID, email, or library card."
                );

                return;
            }

            boolean emailSent =
                    result.length >= 3
                            && Boolean.parseBoolean(result[2]);

            Alert alert =
                    new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Membership Approved");
            alert.setHeaderText(
                    "Pending account created successfully"
            );

            String emailMessage =
                    emailSent
                            ? "An activation email was sent successfully."
                            : "The account was created, but the activation "
                              + "email could not be sent. Verify the email "
                              + "configuration before asking the member "
                              + "to activate the account.";

            alert.setContentText(
                    "User ID: " + result[0]
                            + "\nLibrary Card: " + result[1]
                            + "\nAccount Status: PENDING_ACTIVATION"
                            + "\n\n" + emailMessage
                            + "\n\nThe member must create their own "
                            + "password through Account Access."
            );

            alert.showAndWait();

            openMembershipRequests(
                    approveButton
            );
        });

        rejectButton.setOnAction(event -> {

            TextInputDialog reasonDialog =
                    new TextInputDialog();

            reasonDialog.setTitle("Reject Membership");
            reasonDialog.setHeaderText(
                    "Enter the reason for rejecting this request."
            );
            reasonDialog.setContentText("Reason:");

            Optional<String> result =
                    reasonDialog.showAndWait();

            if (result.isEmpty()
                    || result.get().isBlank()) {

                return;
            }

            boolean rejected =
                    controller.rejectRequest(
                            request,
                            loggedInUser.getUserId(),
                            result.get().trim()
                    );

            if (rejected) {

                Alert alert =
                        new Alert(Alert.AlertType.INFORMATION);

                alert.setTitle("Membership Rejected");
                alert.setHeaderText(
                        "Request rejected successfully"
                );
                alert.setContentText(
                        "The decision and reviewing Admin "
                                + "have been recorded."
                );
                alert.showAndWait();

                openMembershipRequests(
                        rejectButton
                );

            } else {

                showError(
                        "Rejection Failed",
                        "The request could not be rejected.",
                        "The request may already have been reviewed."
                );
            }
        });

        backButton.setOnAction(event ->
                openMembershipRequests(backButton)
        );

        HBox buttons = new HBox(
                18,
                approveButton,
                rejectButton,
                backButton
        );

        buttons.setAlignment(Pos.CENTER);

        card.getChildren().addAll(
                title,
                grid,
                buttons
        );

        root.getChildren().add(card);

        return new Scene(root, 1200, 760);
    }

    private boolean showApprovalConfirmation() {

        Alert confirmation =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmation.setTitle("Confirm Approval");
        confirmation.setHeaderText(
                "Approve membership for "
                        + request.getFullName()
                        + "?"
        );

        confirmation.setContentText(
                "A pending account and library card will be created. "
                        + "The member will choose their own password "
                        + "during account activation."
        );

        return confirmation.showAndWait()
                .filter(
                        buttonType ->
                                buttonType == ButtonType.OK
                )
                .isPresent();
    }

    private void openMembershipRequests(
        Button sourceButton
) {

    Stage stage =
            (Stage) sourceButton
                    .getScene()
                    .getWindow();

    SceneRouter.open(
            stage,
            new MembershipRequestView(
                    loggedInUser
            ).createScene(),
            "IntelliLib - Membership Requests"
    );
}

    private void showError(
            String title,
            String header,
            String content
    ) {

        Alert alert =
                new Alert(Alert.AlertType.ERROR);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void addRow(
            GridPane grid,
            int row,
            String labelText,
            String valueText
    ) {

        Label label =
                new Label(labelText + " :");

        label.setStyle(
                "-fx-text-fill:white;" +
                "-fx-font-size:18px;" +
                "-fx-font-weight:bold;"
        );

        Label value =
                new Label(
                        valueText == null
                                ? "-"
                                : valueText
                );

        value.setWrapText(true);

        value.setStyle(
                "-fx-text-fill:white;" +
                "-fx-font-size:18px;"
        );

        grid.add(label, 0, row);
        grid.add(value, 1, row);
    }

    private void styleButton(
            Button button,
            String color
    ) {

        button.setPrefWidth(170);
        button.setPrefHeight(50);

        button.setStyle(
                "-fx-background-color:" + color + ";" +
                "-fx-text-fill:white;" +
                "-fx-font-size:16px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:12;" +
                "-fx-cursor:hand;"
        );
    }
}