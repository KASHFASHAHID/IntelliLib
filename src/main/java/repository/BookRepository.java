package repository;

import config.DatabaseConnection;
import model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {

    public List<Book> searchBooks(String keyword) {

        List<Book> books = new ArrayList<>();

        String sql = """
        SELECT b.isbn,
               b.title,
               c.category_name,
               b.publisher,
               b.edition,
               b.language,
               b.publication_year,
               author_data.authors,
               copy_data.available_copies,
               copy_data.total_copies
        FROM books b
        LEFT JOIN categories c ON b.category_id = c.category_id

        LEFT JOIN (
            SELECT ba.isbn,
                   GROUP_CONCAT(a.author_name SEPARATOR ', ') AS authors
            FROM book_authors ba
            JOIN authors a ON ba.author_id = a.author_id
            GROUP BY ba.isbn
        ) author_data ON b.isbn = author_data.isbn

        LEFT JOIN (
            SELECT isbn,
                   SUM(CASE WHEN status = 'AVAILABLE' THEN 1 ELSE 0 END) AS available_copies,
                   COUNT(copy_number) AS total_copies
            FROM book_copies
            GROUP BY isbn
        ) copy_data ON b.isbn = copy_data.isbn

        WHERE b.title LIKE ?
           OR b.isbn LIKE ?
           OR c.category_name LIKE ?
           OR author_data.authors LIKE ?

        ORDER BY b.title
        """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            String searchText = "%" + keyword + "%";

            statement.setString(1, searchText);
            statement.setString(2, searchText);
            statement.setString(3, searchText);
            statement.setString(4, searchText);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                books.add(new Book(
                        resultSet.getString("isbn"),
                        resultSet.getString("title"),
                        resultSet.getString("category_name"),
                        resultSet.getString("publisher"),
                        resultSet.getString("edition"),
                        resultSet.getString("language"),
                        resultSet.getInt("publication_year"),
                        resultSet.getString("authors"),
                        resultSet.getInt("available_copies"),
                        resultSet.getInt("total_copies")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return books;
    }
}