package service;

import repository.ReportRepository;

import java.math.BigDecimal;

public class ReportService {

    private final ReportRepository repository;

    public ReportService() {
        repository = new ReportRepository();
    }

    public int getTotalMembers() {
        return repository.getTotalMembers();
    }

    public int getTotalBooks() {
        return repository.getTotalBooks();
    }

    public int getTotalCopies() {
        return repository.getTotalCopies();
    }

    public int getAvailableCopies() {
        return repository.getAvailableCopies();
    }

    public int getIssuedBooks() {
        return repository.getIssuedBooks();
    }

    public int getReturnedBooks() {
        return repository.getReturnedBooks();
    }

    public int getOverdueBooks() {
        return repository.getOverdueBooks();
    }

    public int getActiveReservations() {
        return repository.getActiveReservations();
    }

    public int getPendingFinesCount() {
        return repository.getPendingFinesCount();
    }

    public BigDecimal getPendingFineAmount() {
        return repository.getPendingFineAmount();
    }

    public BigDecimal getCollectedFineAmount() {
        return repository.getCollectedFineAmount();
    }
}