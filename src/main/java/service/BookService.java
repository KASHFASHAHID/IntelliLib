package service;

import model.Book;
import repository.BookRepository;
import repository.FineRepository;

import java.util.List;

public class BookService {

    private BookRepository repository;

    public BookService() {
        repository = new BookRepository();
    }

    public List<Book> searchBooks(String keyword) {
        return repository.searchBooks(keyword);
    }
}