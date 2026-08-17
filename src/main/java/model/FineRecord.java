package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FineRecord {

    private int fineId;
    private String userId;
    private String memberName;
    private String role;
    private String bookTitle;
    private String copyNumber;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public FineRecord(
            int fineId,
            String userId,
            String memberName,
            String role,
            String bookTitle,
            String copyNumber,
            BigDecimal amount,
            String status,
            LocalDateTime createdAt,
            LocalDateTime paidAt) {

        this.fineId = fineId;
        this.userId = userId;
        this.memberName = memberName;
        this.role = role;
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

    public String getUserId() {
        return userId;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getRole() {
        return role;
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