package repository;

import config.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

public class InventoryRepository {

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

        String checkBookSql = """
                SELECT COUNT(*)
                FROM books
                WHERE isbn = ?
                """;

        String findCategorySql = """
                SELECT category_id
                FROM categories
                WHERE category_name = ?
                """;

        String insertCategorySql = """
                INSERT INTO categories (category_name)
                VALUES (?)
                """;

        String insertBookSql = """
                INSERT INTO books
                (
                    isbn,
                    title,
                    category_id,
                    publisher,
                    edition,
                    language,
                    publication_year,
                    description
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String findAuthorSql = """
                SELECT author_id
                FROM authors
                WHERE author_name = ?
                LIMIT 1
                """;

        String insertAuthorSql = """
                INSERT INTO authors (author_name)
                VALUES (?)
                """;

        String linkAuthorSql = """
                INSERT INTO book_authors (isbn, author_id)
                VALUES (?, ?)
                """;

        String insertCopySql = """
                INSERT INTO book_copies
                (
                    copy_number,
                    isbn,
                    barcode,
                    shelf_location,
                    status,
                    purchase_date,
                    price,
                    condition_note
                )
                VALUES (?, ?, ?, ?, 'AVAILABLE', ?, ?, 'Good condition')
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try (
                    PreparedStatement checkBookStatement =
                            connection.prepareStatement(checkBookSql);

                    PreparedStatement findCategoryStatement =
                            connection.prepareStatement(findCategorySql);

                    PreparedStatement insertCategoryStatement =
                            connection.prepareStatement(
                                    insertCategorySql,
                                    Statement.RETURN_GENERATED_KEYS
                            );

                    PreparedStatement insertBookStatement =
                            connection.prepareStatement(insertBookSql);

                    PreparedStatement findAuthorStatement =
                            connection.prepareStatement(findAuthorSql);

                    PreparedStatement insertAuthorStatement =
                            connection.prepareStatement(
                                    insertAuthorSql,
                                    Statement.RETURN_GENERATED_KEYS
                            );

                    PreparedStatement linkAuthorStatement =
                            connection.prepareStatement(linkAuthorSql);

                    PreparedStatement insertCopyStatement =
                            connection.prepareStatement(insertCopySql)
            ) {

                // Check whether the ISBN already exists.
                checkBookStatement.setString(1, isbn);

                try (ResultSet resultSet =
                             checkBookStatement.executeQuery()) {

                    if (resultSet.next()
                            && resultSet.getInt(1) > 0) {

                        connection.rollback();
                        return false;
                    }
                }

                // Find or create the category.
                int categoryId;

                findCategoryStatement.setString(
                        1,
                        categoryName.trim()
                );

                try (ResultSet resultSet =
                             findCategoryStatement.executeQuery()) {

                    if (resultSet.next()) {

                        categoryId =
                                resultSet.getInt("category_id");

                    } else {

                        insertCategoryStatement.setString(
                                1,
                                categoryName.trim()
                        );

                        insertCategoryStatement.executeUpdate();

                        try (ResultSet generatedKeys =
                                     insertCategoryStatement
                                             .getGeneratedKeys()) {

                            if (!generatedKeys.next()) {
                                connection.rollback();
                                return false;
                            }

                            categoryId = generatedKeys.getInt(1);
                        }
                    }
                }

                // Insert the book title.
                insertBookStatement.setString(1, isbn.trim());
                insertBookStatement.setString(2, title.trim());
                insertBookStatement.setInt(3, categoryId);
                insertBookStatement.setString(
                        4,
                        emptyToNull(publisher)
                );
                insertBookStatement.setString(
                        5,
                        emptyToNull(edition)
                );
                insertBookStatement.setString(
                        6,
                        emptyToNull(language)
                );

                if (publicationYear > 0) {
                    insertBookStatement.setInt(
                            7,
                            publicationYear
                    );
                } else {
                    insertBookStatement.setNull(
                            7,
                            java.sql.Types.INTEGER
                    );
                }

                insertBookStatement.setString(
                        8,
                        emptyToNull(description)
                );

                insertBookStatement.executeUpdate();

                // Supports multiple comma-separated authors.
                String[] authors = authorNames.split(",");

                for (String author : authors) {

                    String cleanAuthorName = author.trim();

                    if (cleanAuthorName.isBlank()) {
                        continue;
                    }

                    int authorId;

                    findAuthorStatement.setString(
                            1,
                            cleanAuthorName
                    );

                    try (ResultSet resultSet =
                                 findAuthorStatement
                                         .executeQuery()) {

                        if (resultSet.next()) {

                            authorId =
                                    resultSet.getInt("author_id");

                        } else {

                            insertAuthorStatement.setString(
                                    1,
                                    cleanAuthorName
                            );

                            insertAuthorStatement.executeUpdate();

                            try (ResultSet generatedKeys =
                                         insertAuthorStatement
                                                 .getGeneratedKeys()) {

                                if (!generatedKeys.next()) {
                                    connection.rollback();
                                    return false;
                                }

                                authorId =
                                        generatedKeys.getInt(1);
                            }
                        }
                    }

                    linkAuthorStatement.setString(
                            1,
                            isbn.trim()
                    );
                    linkAuthorStatement.setInt(
                            2,
                            authorId
                    );
                    linkAuthorStatement.executeUpdate();
                }

                // Create the physical copies.
                for (int copyIndex = 1;
                     copyIndex <= numberOfCopies;
                     copyIndex++) {

                    String sequence =
                            String.format("%02d", copyIndex);

                    String copyNumber =
                            "CP-" + isbn.trim() + "-" + sequence;

                    String barcode =
                            "BC-" + isbn.trim() + "-" + sequence;

                    insertCopyStatement.setString(
                            1,
                            copyNumber
                    );
                    insertCopyStatement.setString(
                            2,
                            isbn.trim()
                    );
                    insertCopyStatement.setString(
                            3,
                            barcode
                    );
                    insertCopyStatement.setString(
                            4,
                            shelfLocation.trim()
                    );
                    insertCopyStatement.setDate(
                            5,
                            Date.valueOf(LocalDate.now())
                    );

                    if (price != null) {
                        insertCopyStatement.setBigDecimal(
                                6,
                                price
                        );
                    } else {
                        insertCopyStatement.setNull(
                                6,
                                java.sql.Types.DECIMAL
                        );
                    }

                    insertCopyStatement.executeUpdate();
                }

                connection.commit();
                return true;

            } catch (Exception e) {

                connection.rollback();
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

    private String emptyToNull(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
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

    String findCategorySql = """
            SELECT category_id
            FROM categories
            WHERE category_name = ?
            """;

    String insertCategorySql = """
            INSERT INTO categories(category_name)
            VALUES (?)
            """;

    String updateBookSql = """
            UPDATE books
            SET
                title = ?,
                category_id = ?,
                publisher = ?,
                edition = ?,
                language = ?,
                publication_year = ?
            WHERE isbn = ?
            """;

    String deleteAuthorsSql = """
            DELETE FROM book_authors
            WHERE isbn = ?
            """;

    String findAuthorSql = """
            SELECT author_id
            FROM authors
            WHERE author_name = ?
            LIMIT 1
            """;

    String insertAuthorSql = """
            INSERT INTO authors(author_name)
            VALUES (?)
            """;

    String linkAuthorSql = """
            INSERT INTO book_authors(isbn, author_id)
            VALUES (?,?)
            """;

    try (Connection connection =
                 DatabaseConnection.getConnection()) {

        connection.setAutoCommit(false);

        try (

                PreparedStatement findCategoryStatement =
                        connection.prepareStatement(
                                findCategorySql
                        );

                PreparedStatement insertCategoryStatement =
                        connection.prepareStatement(
                                insertCategorySql,
                                Statement.RETURN_GENERATED_KEYS
                        );

                PreparedStatement updateBookStatement =
                        connection.prepareStatement(
                                updateBookSql
                        );

                PreparedStatement deleteAuthorsStatement =
                        connection.prepareStatement(
                                deleteAuthorsSql
                        );

                PreparedStatement findAuthorStatement =
                        connection.prepareStatement(
                                findAuthorSql
                        );

                PreparedStatement insertAuthorStatement =
                        connection.prepareStatement(
                                insertAuthorSql,
                                Statement.RETURN_GENERATED_KEYS
                        );

                PreparedStatement linkAuthorStatement =
        connection.prepareStatement(
                linkAuthorSql
        )
) {

    int categoryId;

            findCategoryStatement.setString(
                    1,
                    categoryName.trim()
            );

            try (ResultSet resultSet =
                         findCategoryStatement.executeQuery()) {

                if (resultSet.next()) {

                    categoryId =
                            resultSet.getInt("category_id");

                } else {

                    insertCategoryStatement.setString(
                            1,
                            categoryName.trim()
                    );

                    insertCategoryStatement.executeUpdate();

                    try (ResultSet generatedKeys =
                                 insertCategoryStatement.getGeneratedKeys()) {

                        if (!generatedKeys.next()) {
                            connection.rollback();
                            return false;
                        }

                        categoryId =
                                generatedKeys.getInt(1);
                    }
                }
            }

            updateBookStatement.setString(
                    1,
                    title.trim()
            );

            updateBookStatement.setInt(
                    2,
                    categoryId
            );

            updateBookStatement.setString(
                    3,
                    emptyToNull(publisher)
            );

            updateBookStatement.setString(
                    4,
                    emptyToNull(edition)
            );

            updateBookStatement.setString(
                    5,
                    emptyToNull(language)
            );

            if (publicationYear > 0) {

                updateBookStatement.setInt(
                        6,
                        publicationYear
                );

            } else {

                updateBookStatement.setNull(
                        6,
                        java.sql.Types.INTEGER
                );
            }

            updateBookStatement.setString(
                    7,
                    isbn.trim()
            );

            if (updateBookStatement.executeUpdate() != 1) {
                connection.rollback();
                return false;
            }

            deleteAuthorsStatement.setString(
                    1,
                    isbn.trim()
            );

            deleteAuthorsStatement.executeUpdate();

            String[] authors = authorNames.split(",");

            for (String author : authors) {

                String cleanAuthorName = author.trim();

                if (cleanAuthorName.isBlank()) {
                    continue;
                }

                int authorId;

                findAuthorStatement.setString(
                        1,
                        cleanAuthorName
                );

                try (ResultSet resultSet =
                             findAuthorStatement.executeQuery()) {

                    if (resultSet.next()) {

                        authorId =
                                resultSet.getInt("author_id");

                    } else {

                        insertAuthorStatement.setString(
                                1,
                                cleanAuthorName
                        );

                        insertAuthorStatement.executeUpdate();

                        try (ResultSet generatedKeys =
                                     insertAuthorStatement.getGeneratedKeys()) {

                            if (!generatedKeys.next()) {
                                connection.rollback();
                                return false;
                            }

                            authorId = generatedKeys.getInt(1);
                        }
                    }
                }

                linkAuthorStatement.setString(
                        1,
                        isbn.trim()
                );

                linkAuthorStatement.setInt(
                        2,
                        authorId
                );

                linkAuthorStatement.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (Exception e) {

            connection.rollback();
            e.printStackTrace();
            return false;
        }

    } catch (Exception e) {

        e.printStackTrace();
        return false;
    }
}

public boolean deleteBook(String isbn) {

    if (isbn == null || isbn.isBlank()) {
        return false;
    }

    String activeLoanSql = """
            SELECT COUNT(*)
            FROM book_loans bl
            JOIN book_copies bc
                ON bl.copy_number = bc.copy_number
            WHERE bc.isbn = ?
              AND bl.status = 'ISSUED'
            """;

    String activeReservationSql = """
            SELECT COUNT(*)
            FROM reservations
            WHERE isbn = ?
              AND status IN ('WAITING', 'READY_FOR_PICKUP')
            """;

    String deleteBookAuthorsSql = """
            DELETE FROM book_authors
            WHERE isbn = ?
            """;

    String deleteCopiesSql = """
            DELETE FROM book_copies
            WHERE isbn = ?
            """;

    String deleteBookSql = """
            DELETE FROM books
            WHERE isbn = ?
            """;

    try (Connection connection =
                 DatabaseConnection.getConnection()) {

        connection.setAutoCommit(false);

        try (
                PreparedStatement loanStatement =
                        connection.prepareStatement(activeLoanSql);

                PreparedStatement reservationStatement =
                        connection.prepareStatement(activeReservationSql);

                PreparedStatement deleteAuthorsStatement =
                        connection.prepareStatement(deleteBookAuthorsSql);

                PreparedStatement deleteCopiesStatement =
                        connection.prepareStatement(deleteCopiesSql);

                PreparedStatement deleteBookStatement =
                        connection.prepareStatement(deleteBookSql)
        ) {

            loanStatement.setString(1, isbn);

            try (ResultSet resultSet =
                         loanStatement.executeQuery()) {

                if (resultSet.next()
                        && resultSet.getInt(1) > 0) {

                    connection.rollback();
                    return false;
                }
            }

            reservationStatement.setString(1, isbn);

            try (ResultSet resultSet =
                         reservationStatement.executeQuery()) {

                if (resultSet.next()
                        && resultSet.getInt(1) > 0) {

                    connection.rollback();
                    return false;
                }
            }

            deleteAuthorsStatement.setString(1, isbn);
            deleteAuthorsStatement.executeUpdate();

            deleteCopiesStatement.setString(1, isbn);
            deleteCopiesStatement.executeUpdate();

            deleteBookStatement.setString(1, isbn);

            if (deleteBookStatement.executeUpdate() != 1) {
                connection.rollback();
                return false;
            }

            connection.commit();
            return true;

        } catch (Exception e) {

            connection.rollback();
            e.printStackTrace();
            return false;
        }

    } catch (Exception e) {

        e.printStackTrace();
        return false;
    }
}

}