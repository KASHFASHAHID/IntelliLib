package model;

import java.time.LocalDate;

public class BorrowRecord {

    private int loanId;
    private String userId;
    private String memberName;
    private String role;
    private String title;
    private String authors;
    private String copyNumber;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;

    public BorrowRecord(
            int loanId,
            String userId,
            String memberName,
            String role,
            String title,
            String authors,
            String copyNumber,
            LocalDate issueDate,
            LocalDate dueDate,
            LocalDate returnDate,
            String status
    ) {
        this.loanId = loanId;
        this.userId = userId;
        this.memberName = memberName;
        this.role = role;
        this.title = title;
        this.authors = authors;
        this.copyNumber = copyNumber;
        this.issueDate =
        issueDate;
        this.dueDate =
                dueDate;
        this.returnDate =
                returnDate;
        this.status =
                status;
    }

    public int getLoanId() {
        return loanId;
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

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public String getStatus() {
        return status;
    }
}