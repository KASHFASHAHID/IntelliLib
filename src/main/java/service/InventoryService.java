package service;

import repository.InventoryRepository;

import java.math.BigDecimal;

public class InventoryService {

    private InventoryRepository repository;

    public InventoryService() {
        repository = new InventoryRepository();
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

        if (isbn == null || isbn.isBlank()
                || title == null || title.isBlank()
                || categoryName == null || categoryName.isBlank()
                || authorNames == null || authorNames.isBlank()
                || numberOfCopies <= 0
                || shelfLocation == null || shelfLocation.isBlank()) {

            return false;
        }

        if (publicationYear < 0) {
            return false;
        }

        if (price != null && price.signum() < 0) {
            return false;
        }

        return repository.addBook(
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

    if (isbn == null || isbn.isBlank()
            || title == null || title.isBlank()
            || categoryName == null || categoryName.isBlank()
            || authorNames == null || authorNames.isBlank()) {

        return false;
    }

    return repository.updateBook(
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
    return repository.deleteBook(isbn);
}


}
