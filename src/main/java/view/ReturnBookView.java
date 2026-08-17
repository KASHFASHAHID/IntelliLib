package view;

import controller.BorrowedBooksController;
import controller.ReturnController;
import controller.UserController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.BorrowedBook;
import model.Role;
import model.User;
import util.SceneRouter;

import java.time.LocalDate;
import java.util.Optional;

public class ReturnBookView {

    private final User loggedInUser;
    private final UserController userController;
    private final BorrowedBooksController borrowedBooksController;
    private final ReturnController returnController;

    public ReturnBookView(
            User loggedInUser
    ) {

        this.loggedInUser = loggedInUser;
        this.userController = new UserController();
        this.borrowedBooksController =
                new BorrowedBooksController();
        this.returnController =
                new ReturnController();
    }

    public Scene createScene() {

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color:#0f172a;"
        );

        Text title =
                new Text("Return Book");

        title.setStyle(
                "-fx-fill:white;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        TextField memberIdField =
                new TextField();

        memberIdField.setPromptText(
                "Enter Student or Teacher User ID"
        );

        memberIdField.setPrefWidth(300);
        memberIdField.setPrefHeight(44);

        Button searchMemberButton =
                new Button("Search Member");

        searchMemberButton.setPrefWidth(160);
        searchMemberButton.setPrefHeight(44);

        searchMemberButton.setStyle(
                "-fx-background-color:#2563eb;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:9;"
        );

        Label memberLabel =
                new Label("No member selected");

        memberLabel.setStyle(
                "-fx-text-fill:#cbd5e1;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;"
        );

        HBox memberBox =
                new HBox(
                        14,
                        memberIdField,
                        searchMemberButton,
                        memberLabel
                );

        memberBox.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox topBox =
                new VBox(
                        16,
                        title,
                        memberBox
                );

        topBox.setPadding(
                new Insets(25)
        );

        TableView<BorrowedBook> table =
                createBorrowedBooksTable();

        final User[] selectedMember =
                {null};

        Button returnButton =
                new Button(
                        "Return Selected Book"
                );

        returnButton.setPrefWidth(220);
        returnButton.setPrefHeight(45);
        returnButton.setDisable(true);

        returnButton.setStyle(
                "-fx-background-color:#16a34a;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:9;"
        );

        Button backButton =
                new Button("Back");

        backButton.setPrefWidth(180);
        backButton.setPrefHeight(45);

        backButton.setStyle(
                "-fx-background-color:#334155;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:9;"
        );

        table.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (
                                observable,
                                oldBook,
                                selectedBook
                        ) -> {

                            returnButton.setDisable(
                                    selectedMember[0] == null
                                            || selectedBook == null
                            );
                        }
                );

        searchMemberButton.setOnAction(event -> {

            String enteredUserId =
                    memberIdField
                            .getText()
                            .trim();

            if (enteredUserId.isBlank()) {

                selectedMember[0] = null;
                table.getItems().clear();
                returnButton.setDisable(true);

                memberLabel.setText(
                        "No member selected"
                );

                showAlert(
                        Alert.AlertType.WARNING,
                        "Member ID Required",
                        "Please enter a member User ID.",
                        "Enter the exact Student or Teacher User ID before searching."
                );

                return;
            }

            User member =
                    userController.findActiveUserById(
                            enteredUserId
                    );

            if (member != null
                    && member.getRole() != Role.STUDENT
                    && member.getRole() != Role.TEACHER) {

                member = null;
            }

            if (member == null) {

                selectedMember[0] = null;
                table.getItems().clear();
                returnButton.setDisable(true);

                memberLabel.setText(
                        "Member not found"
                );

                showAlert(
                        Alert.AlertType.WARNING,
                        "Member Not Found",
                        "No active Student or Teacher was found.",
                        "Please verify the User ID and try again."
                );

                return;
            }

            selectedMember[0] = member;

            memberLabel.setText(
                    member.getName()
                            + " ("
                            + member.getRole()
                            + ")"
            );

            loadBorrowedBooks(
                    table,
                    member.getUserId()
            );

            returnButton.setDisable(true);

            if (table.getItems().isEmpty()) {

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "No Active Loans",
                        "This member has no books available for return.",
                        "There are currently no issued or overdue loans associated with this account."
                );
            }
        });

        returnButton.setOnAction(event -> {

            User member =
                    selectedMember[0];

            BorrowedBook selectedBook =
                    table.getSelectionModel()
                            .getSelectedItem();

            if (member == null) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Member Required",
                        "Search and select a valid member first.",
                        "A Student or Teacher account must be loaded before processing a return."
                );

                return;
            }

            if (selectedBook == null) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Book Required",
                        "Select a borrowed book from the table.",
                        "Choose the exact physical copy that is being returned."
                );

                return;
            }

            Alert confirmation =
                    new Alert(
                            Alert.AlertType.CONFIRMATION
                    );

            confirmation.setTitle(
                    "Confirm Book Return"
            );

            confirmation.setHeaderText(
                    "Confirm the return of this library book."
            );

            confirmation.setContentText(
                    "Book: "
                            + selectedBook.getTitle()
                            + "\nCopy Number: "
                            + selectedBook.getCopyNumber()
                            + "\nMember: "
                            + member.getName()
                            + "\nUser ID: "
                            + member.getUserId()
                            + "\n\nDo you want to continue?"
            );

            Optional<ButtonType> confirmationResult =
                    confirmation.showAndWait();

            /*
             * Closing the confirmation with X or selecting
             * Cancel does not process the return.
             */
            if (confirmationResult.isEmpty()
                    || confirmationResult.get()
                    != ButtonType.OK) {

                return;
            }

            boolean returned =
                    returnController.returnBook(
                            selectedBook.getLoanId(),
                            selectedBook.getCopyNumber()
                    );

            if (!returned) {

                showAlert(
                        Alert.AlertType.ERROR,
                        "Return Unsuccessful",
                        "The book return could not be completed.",
                        "The loan may already be closed, the selected copy may not match the loan, or a database error may have occurred."
                );

                return;
            }

            Alert successAlert =
                    new Alert(
                            Alert.AlertType.INFORMATION
                    );

            successAlert.setTitle(
                    "Book Return Completed"
            );

            successAlert.setHeaderText(
                    "The book has been returned successfully."
            );

            successAlert.setContentText(
                    "Book: "
                            + selectedBook.getTitle()
                            + "\nCopy Number: "
                            + selectedBook.getCopyNumber()
                            + "\nMember: "
                            + member.getName()
                            + "\nUser ID: "
                            + member.getUserId()
                            + "\n\nThe loan record and copy status have been updated."
            );

            successAlert.showAndWait();

            loadBorrowedBooks(
                    table,
                    member.getUserId()
            );

            returnButton.setDisable(true);
        });

        backButton.setOnAction(event -> {

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

        HBox bottomBox =
                new HBox(
                        15,
                        returnButton,
                        backButton
                );

        bottomBox.setAlignment(
                Pos.CENTER_RIGHT
        );

        bottomBox.setPadding(
                new Insets(
                        15,
                        25,
                        25,
                        25
                )
        );

        BorderPane.setMargin(
                table,
                new Insets(
                        0,
                        25,
                        10,
                        25
                )
        );

        root.setTop(topBox);
        root.setCenter(table);
        root.setBottom(bottomBox);

        return new Scene(
                root,
                1200,
                760
        );
    }

    private TableView<BorrowedBook>
    createBorrowedBooksTable() {

        TableView<BorrowedBook> table =
                new TableView<>();

        TableColumn<BorrowedBook, String>
                titleColumn =
                new TableColumn<>("Book Title");

        titleColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "title"
                )
        );

        TableColumn<BorrowedBook, String>
                authorColumn =
                new TableColumn<>("Author");

        authorColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "authors"
                )
        );

        TableColumn<BorrowedBook, String>
                copyColumn =
                new TableColumn<>("Copy No.");

        copyColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "copyNumber"
                )
        );

        TableColumn<BorrowedBook, LocalDate>
                issueDateColumn =
                new TableColumn<>("Issue Date");

        issueDateColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "issueDate"
                )
        );

        TableColumn<BorrowedBook, LocalDate>
                dueDateColumn =
                new TableColumn<>("Due Date");

        dueDateColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "dueDate"
                )
        );

        TableColumn<BorrowedBook, String>
                statusColumn =
                new TableColumn<>("Status");

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "status"
                )
        );

        TableColumn<BorrowedBook, Long>
                daysLeftColumn =
                new TableColumn<>("Days Left");

        daysLeftColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "daysLeft"
                )
        );

        table.getColumns().addAll(
                titleColumn,
                authorColumn,
                copyColumn,
                issueDateColumn,
                dueDateColumn,
                statusColumn,
                daysLeftColumn
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        table.setPlaceholder(
                new Label(
                        "Search for a member to view active loans."
                )
        );

        return table;
    }

    private void loadBorrowedBooks(
            TableView<BorrowedBook> table,
            String userId
    ) {

        table.setItems(
                FXCollections.observableArrayList(
                        borrowedBooksController
                                .getBorrowedBooks(
                                        userId
                                )
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
}