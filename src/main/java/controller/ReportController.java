package controller;

import service.ReportService;

import java.math.BigDecimal;

public class ReportController {

    private final ReportService service;

    public ReportController() {
        service = new ReportService();
    }

    public int getTotalMembers() {
        return service.getTotalMembers();
    }

    public int getTotalBooks() {
        return service.getTotalBooks();
    }

    public int getTotalCopies() {
        return service.getTotalCopies();
    }

    public int getAvailableCopies() {
        return service.getAvailableCopies();
    }

    public int getIssuedBooks() {
        return service.getIssuedBooks();
    }

    public int getReturnedBooks() {
        return service.getReturnedBooks();
    }

    public int getOverdueBooks() {
        return service.getOverdueBooks();
    }

    public int getActiveReservations() {
        return service.getActiveReservations();
    }

    public int getPendingFinesCount() {
        return service.getPendingFinesCount();
    }

    public BigDecimal getPendingFineAmount() {
        return service.getPendingFineAmount();
    }

    public BigDecimal getCollectedFineAmount() {
        return service.getCollectedFineAmount();
    }
}