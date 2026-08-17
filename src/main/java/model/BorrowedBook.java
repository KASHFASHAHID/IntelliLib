package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BorrowedBook {

    private int loanId;
    private String title;
    private String authors;
    private String copyNumber;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private String status;
    private long daysLeft;
    private int renewalCount;
    private LocalDateTime lastRenewedAt;

    public BorrowedBook(
            int loanId,
            String title,
            String authors,
            String copyNumber,
            LocalDate issueDate,
            LocalDate dueDate,
            String status,
            long daysLeft,
            int renewalCount,
            LocalDateTime lastRenewedAt
    ) {

        this.loanId = loanId;
        this.title = title;
        this.authors = authors;
        this.copyNumber = copyNumber;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = status;
        this.daysLeft = daysLeft;
        this.renewalCount = renewalCount;
        this.lastRenewedAt = lastRenewedAt;
    }

    public int getLoanId() {
        return loanId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getCopyNumber() {
        return copyNumber;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }

    public long getDaysLeft() {
        return daysLeft;
    }

    public int getRenewalCount() {
        return renewalCount;
    }

    public LocalDateTime getLastRenewedAt() {
        return lastRenewedAt;
    }

    public boolean isRenewed() {
        return renewalCount > 0;
    }
}