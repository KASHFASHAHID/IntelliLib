package controller;

import model.Book;
import service.BookService;
import service.InventoryService;

import java.math.BigDecimal;
import java.util.List;

public class InventoryController {

    private BookService bookService;
    private InventoryService inventoryService;

    public InventoryController() {
        bookService = new BookService();
        inventoryService = new InventoryService();
    }

    public List<Book> searchBooks(String keyword) {
        return bookService.searchBooks(keyword);
    }

    public boolean addBook(
            String isbn,
            String title,
            String categoryName,
            String publisher,
            String edition,
            String language,
            int publicationYear,
            String description,
            String authorNames,
            int numberOfCopies,
            String shelfLocation,
            BigDecimal price
    ) {

        return inventoryService.addBook(
                isbn,
                title,
                categoryName,
                publisher,
                edition,
                language,
                publicationYear,
                description,
                authorNames,
                numberOfCopies,
                shelfLocation,
                price
        );
    }

    public boolean updateBook(
        String isbn,
        String title,
        String categoryName,
        String publisher,
        String edition,
        String language,
        int publicationYear,
        String authorNames
) {
    return inventoryService.updateBook(
            isbn,
            title,
            categoryName,
            publisher,
            edition,
            language,
            publicationYear,
            authorNames
    );
}

public boolean deleteBook(String isbn) {
    return inventoryService.deleteBook(isbn);
}
}