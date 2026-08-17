package view;

import controller.InventoryController;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.Book;

public class UpdateBookDialog {

    private final InventoryController controller;
    private final Book selectedBook;

    public UpdateBookDialog(
            InventoryController controller,
            Book selectedBook
    ) {
        this.controller = controller;
        this.selectedBook = selectedBook;
    }

    public boolean show() {

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle("Update Book");
        dialog.setHeaderText(
                "Update details for: " + selectedBook.getTitle()
        );

        ButtonType updateButtonType = new ButtonType(
                "Update Book",
                ButtonBar.ButtonData.OK_DONE
        );

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        updateButtonType,
                        ButtonType.CANCEL
                );

        TextField isbnField =
                new TextField(selectedBook.getIsbn());

        TextField titleField =
                new TextField(selectedBook.getTitle());

        TextField categoryField =
                new TextField(selectedBook.getCategoryName());

        TextField authorsField =
                new TextField(selectedBook.getAuthors());

        TextField publisherField =
                new TextField(selectedBook.getPublisher());

        TextField editionField =
                new TextField(selectedBook.getEdition());

        TextField languageField =
                new TextField(selectedBook.getLanguage());

        TextField yearField =
                new TextField(
                        String.valueOf(
                                selectedBook.getPublicationYear()
                        )
                );

        isbnField.setEditable(false);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(15);
        grid.setVgap(12);

        grid.add(new Label("ISBN"), 0, 0);
        grid.add(isbnField, 1, 0);

        grid.add(new Label("Title *"), 0, 1);
        grid.add(titleField, 1, 1);

        grid.add(new Label("Category *"), 0, 2);
        grid.add(categoryField, 1, 2);

        grid.add(new Label("Author(s) *"), 0, 3);
        grid.add(authorsField, 1, 3);

        grid.add(new Label("Publisher"), 0, 4);
        grid.add(publisherField, 1, 4);

        grid.add(new Label("Edition"), 0, 5);
        grid.add(editionField, 1, 5);

        grid.add(new Label("Language"), 0, 6);
        grid.add(languageField, 1, 6);

        grid.add(new Label("Publication Year"), 0, 7);
        grid.add(yearField, 1, 7);

        dialog.getDialogPane().setContent(grid);
        Button updateButton =
                (Button) dialog.getDialogPane()
                        .lookupButton(updateButtonType);

        final boolean[] updated = {false};

        updateButton.addEventFilter(
                javafx.event.ActionEvent.ACTION,
                event -> {

                    try {

                        int publicationYear =
                                yearField.getText().isBlank()
                                        ? 0
                                        : Integer.parseInt(
                                                yearField.getText().trim()
                                        );

                        boolean success =
                                controller.updateBook(
                                        isbnField.getText(),
                                        titleField.getText(),
                                        categoryField.getText(),
                                        publisherField.getText(),
                                        editionField.getText(),
                                        languageField.getText(),
                                        publicationYear,
                                        authorsField.getText()
                                );

                        if (!success) {

                            Alert alert =
                                    new Alert(Alert.AlertType.ERROR);

                            alert.setTitle("Update Failed");
                            alert.setHeaderText(
                                    "The book could not be updated."
                            );
                            alert.setContentText(
                                    "Please check the required fields."
                            );
                            alert.showAndWait();

                            event.consume();
                            return;
                        }

                        updated[0] = true;

                        Alert alert =
                                new Alert(Alert.AlertType.INFORMATION);

                        alert.setTitle("Update Successful");
                        alert.setHeaderText(
                                "Book updated successfully."
                        );
                        alert.setContentText(
                                "The inventory details were updated."
                        );
                        alert.showAndWait();

                    } catch (NumberFormatException exception) {

                        Alert alert =
                                new Alert(Alert.AlertType.ERROR);

                        alert.setTitle("Invalid Input");
                        alert.setHeaderText(
                                "Publication year is invalid."
                        );
                        alert.setContentText(
                                "Please enter a valid numeric year."
                        );
                        alert.showAndWait();

                        event.consume();
                    }
                }
        );

        dialog.showAndWait();

        return updated[0];
    }
}


