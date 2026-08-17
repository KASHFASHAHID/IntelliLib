package controller;

import model.User;
import service.BorrowService;

public class BorrowController {

    private BorrowService service;

    public BorrowController() {
        service = new BorrowService();
    }

    public boolean borrowBook(User user, String isbn) {
        return service.borrowBook(user, isbn);
    }

    public boolean borrowBooks(
            User user,
            String isbn,
            int quantity
    ) {
        return service.borrowBooks(
                user,
                isbn,
                quantity
        );
    }
}