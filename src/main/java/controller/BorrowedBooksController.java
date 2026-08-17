package controller;

import model.BorrowedBook;
import service.BorrowedBooksService;

import java.time.LocalDate;
import java.util.List;

public class BorrowedBooksController {

    private final BorrowedBooksService service;

    public BorrowedBooksController() {
        service = new BorrowedBooksService();
    }

    public List<BorrowedBook> getBorrowedBooks(
            String userId
    ) {

        return service.getBorrowedBooks(
                userId
        );
    }

    public String getRenewalBlockReason(
            int loanId,
            String userId
    ) {

        return service.getRenewalBlockReason(
                loanId,
                userId
        );
    }

    public LocalDate renewLoan(
            int loanId,
            String userId
    ) {

        return service.renewLoan(
                loanId,
                userId
        );
    }
}