package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OverdueFineNotice(
        int fineId,
        int loanId,
        String userId,
        String memberName,
        String memberEmail,
        String bookTitle,
        LocalDate dueDate,
        long overdueDays,
        BigDecimal currentAmount
) {
}