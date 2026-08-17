package view;

import controller.MembershipRequestController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.MembershipRequest;
import model.User;
import util.SceneRouter;

public class MembershipRequestView {

    private final MembershipRequestController controller;
    private final User loggedInUser;

    public MembershipRequestView(
            User loggedInUser
    ) {
        this.loggedInUser = loggedInUser;
        this.controller =
                new MembershipRequestController();
    }

    public Scene createScene() {

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color:#0f172a;"
        );

        Label title =
                new Label(
                        "Membership Requests"
                );

        title.setStyle(
                "-fx-text-fill:white;" +
                "-fx-font-size:28px;" +
                "-fx-font-weight:bold;"
        );

        TableView<MembershipRequest> table =
                new TableView<>();

        TableColumn<MembershipRequest, Integer> idColumn =
                new TableColumn<>(
                        "ID"
                );

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "requestId"
                )
        );

        TableColumn<MembershipRequest, String> nameColumn =
                new TableColumn<>(
                        "Name"
                );

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "fullName"
                )
        );

        TableColumn<MembershipRequest, String> roleColumn =
                new TableColumn<>(
                        "Role"
                );

        roleColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "roleRequested"
                )
        );

        TableColumn<MembershipRequest, String> departmentColumn =
                new TableColumn<>(
                        "Department"
                );

        departmentColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "department"
                )
        );

        TableColumn<MembershipRequest, String> statusColumn =
                new TableColumn<>(
                        "Status"
                );

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "status"
                )
        );

        table.getColumns().addAll(
                idColumn,
                nameColumn,
                roleColumn,
                departmentColumn,
                statusColumn
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        table.setItems(
                FXCollections.observableArrayList(
                        controller.getPendingRequests()
                )
        );

        table.setPlaceholder(
                new Label(
                        "No pending membership requests found."
                )
        );

        table.setRowFactory(tableView -> {

            TableRow<MembershipRequest> row =
                    new TableRow<>();

            row.setOnMouseClicked(event -> {

                if (event.getClickCount() == 2
                        && !row.isEmpty()) {

                    openRequestDetails(
                            table,
                            row.getItem()
                    );
                }
            });

            return row;
        });

        Button viewButton =
                new Button(
                        "View Details"
                );

        viewButton.setPrefWidth(180);
        viewButton.setPrefHeight(45);

        viewButton.setOnAction(event -> {

            MembershipRequest selectedRequest =
                    table.getSelectionModel()
                            .getSelectedItem();

            if (selectedRequest == null) {

                Alert alert =
                        new Alert(
                                Alert.AlertType.WARNING
                        );

                alert.setTitle(
                        "No Request Selected"
                );

                alert.setHeaderText(
                        "Please select a membership request."
                );

                alert.showAndWait();

                return;
            }

            openRequestDetails(
                    table,
                    selectedRequest
            );
        });

        Button backButton =
                new Button(
                        "Back to Dashboard"
                );

        backButton.setPrefWidth(180);
        backButton.setPrefHeight(45);

        backButton.setOnAction(event -> {

            Stage stage =
                    (Stage) backButton
                            .getScene()
                            .getWindow();

            SceneRouter.open(
                    stage,
                    new AdminDashboardView(
                            loggedInUser
                    ).createScene(),
                    "IntelliLib - Admin Dashboard"
            );
        });

        VBox content =
                new VBox(
                        20,
                        title,
                        table,
                        viewButton,
                        backButton
                );

        content.setPadding(
                new Insets(30)
        );

        root.setCenter(
                content
        );

        return new Scene(
                root,
                1200,
                760
        );
    }

    private void openRequestDetails(
            TableView<MembershipRequest> table,
            MembershipRequest request
    ) {

        Stage stage =
                (Stage) table
                        .getScene()
                        .getWindow();

        SceneRouter.open(
                stage,
                new MembershipDetailsView(
                        request,
                        loggedInUser
                ).createScene(),
                "IntelliLib - Membership Details"
        );
    }
}