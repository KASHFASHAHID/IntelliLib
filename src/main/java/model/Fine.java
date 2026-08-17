package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Fine {

    private int fineId;
    private int loanId;
    private String bookTitle;
    private String copyNumber;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public Fine(
            int fineId,
            int loanId,
            String bookTitle,
            String copyNumber,
            BigDecimal amount,
            String status,
            LocalDateTime createdAt,
            LocalDateTime paidAt) {

        this.fineId = fineId;
        this.loanId = loanId;
        this.bookTitle = bookTitle;
        this.copyNumber = copyNumber;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
    }

    public int getFineId() {
        return fineId;
    }

    public int getLoanId() {
        return loanId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getCopyNumber() {
        return copyNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }
}