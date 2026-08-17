// package view;

// import controller.InventoryController;
// import javafx.geometry.Insets;
// import javafx.scene.control.*;
// import javafx.scene.layout.GridPane;

// import java.math.BigDecimal;

// public class AddBookDialog {

//     private final InventoryController controller;

//     public AddBookDialog(InventoryController controller) {
//         this.controller = controller;
//     }

//     public boolean show() {

//         Dialog<ButtonType> dialog = new Dialog<>();

//         dialog.setTitle("Add New Book");
//         dialog.setHeaderText("Enter Book Details");

//         ButtonType saveButton =
//                 new ButtonType(
//                         "Save Book",
//                         ButtonBar.ButtonData.OK_DONE
//                 );

//         dialog.getDialogPane()
//                 .getButtonTypes()
//                 .addAll(
//                         saveButton,
//                         ButtonType.CANCEL
//                 );

//         TextField isbnField = new TextField();
//         TextField titleField = new TextField();
//         TextField categoryField = new TextField();
//         TextField authorsField = new TextField();
//         TextField publisherField = new TextField();
//         TextField editionField = new TextField();
//         TextField languageField = new TextField();
//         TextField yearField = new TextField();
//         TextArea descriptionArea = new TextArea();
//         TextField copiesField = new TextField("1");
//         TextField shelfField = new TextField();
//         TextField priceField = new TextField();

//         descriptionArea.setPrefRowCount(3);

//         GridPane grid = new GridPane();

//         grid.setPadding(new Insets(20));
//         grid.setHgap(15);
//         grid.setVgap(12);

//         grid.add(new Label("ISBN *"),0,0);
//         grid.add(isbnField,1,0);

//         grid.add(new Label("Title *"),0,1);
//         grid.add(titleField,1,1);

//         grid.add(new Label("Category *"),0,2);
//         grid.add(categoryField,1,2);

//         grid.add(new Label("Author(s) *"),0,3);
//         grid.add(authorsField,1,3);

//         grid.add(new Label("Publisher"),0,4);
//         grid.add(publisherField,1,4);

//         grid.add(new Label("Edition"),0,5);
//         grid.add(editionField,1,5);

//         grid.add(new Label("Language"),0,6);
//         grid.add(languageField,1,6);

//         grid.add(new Label("Publication Year"),0,7);
//         grid.add(yearField,1,7);

//         grid.add(new Label("Description"),0,8);
//         grid.add(descriptionArea,1,8);

//         grid.add(new Label("Copies *"),0,9);
//         grid.add(copiesField,1,9);

//         grid.add(new Label("Shelf"),0,10);
//         grid.add(shelfField,1,10);

//         grid.add(new Label("Price"),0,11);
//         grid.add(priceField,1,11);

//         dialog.getDialogPane().setContent(grid);
