package controller;

import model.Book;
import service.BookService;

import java.util.List;

public class BookController {

    private BookService service;

    public BookController() {
        service = new BookService();
    }

    public List<Book> searchBooks(String keyword) {

        if (keyword == null) {
            keyword = "";
        }

        return service.searchBooks(keyword.trim());
    }
}