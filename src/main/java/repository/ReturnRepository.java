package repository;

import config.DatabaseConnection;
import model.LibrarySettings;
import service.LibrarySettingsService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ReturnRepository {

    private final LibrarySettingsService settingsService;

    public ReturnRepository() {
        this.settingsService = new LibrarySettingsService();
    }

    public boolean returnBook(
            int loanId,
            String copyNumber
    ) {

        LibrarySettings settings =
                settingsService.getSettings();

        if (settings == null
                || copyNumber == null
                || copyNumber.isBlank()) {

            return false;
        }

        String findLoanSql = """
                SELECT user_id,
                       copy_number,
                       due_date
                FROM book_loans
                WHERE loan_id = ?
                  AND status IN ('ISSUED', 'OVERDUE')
                FOR UPDATE
                """;

        String finalizeFineSql = """
                INSERT INTO fines
                (
                    user_id,
                    loan_id,
                    amount,
                    status,
                    created_at,
                    finalized_at
                )
                VALUES
                (
                    ?,
                    ?,
                    ?,
                    'PENDING',
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP
                )
                ON DUPLICATE KEY UPDATE
                    amount = IF(
                        status = 'PENDING'
                        AND paid_at IS NULL,
                        VALUES(amount),
                        amount
                    ),
                    finalized_at = IF(
                        status = 'PENDING'
                        AND paid_at IS NULL,
                        CURRENT_TIMESTAMP,
                        finalized_at
                    )
                """;

        String updateLoanSql = """
                UPDATE book_loans
                SET status = 'RETURNED',
                    return_date = ?
                WHERE loan_id = ?
                  AND status IN ('ISSUED', 'OVERDUE')
                """;

        String findBookSql = """
                SELECT bc.isbn,
                       b.title
                FROM book_copies bc
                JOIN books b
                  ON bc.isbn = b.isbn
                WHERE bc.copy_number = ?
                FOR UPDATE
                """;

        String findFirstReservationSql = """
                SELECT r.reservation_id,
                       r.user_id,
                       u.name
                FROM reservations r
                JOIN users u
                  ON r.user_id = u.user_id
                WHERE r.isbn = ?
                  AND r.status = 'WAITING'
                ORDER BY r.queue_position,
                         r.reservation_date,
                         r.reservation_id
                LIMIT 1
                FOR UPDATE
                """;

        String updateCopyAvailableSql = """
                UPDATE book_copies
                SET status = 'AVAILABLE'
                WHERE copy_number = ?
                """;

        String updateCopyReservedSql = """
                UPDATE book_copies
                SET status = 'RESERVED'
                WHERE copy_number = ?
                """;

        String activateReservationSql = """
                UPDATE reservations
                SET status = 'READY_FOR_PICKUP',
                    pickup_expiry_date = ?,
                    notification_sent = TRUE
                WHERE reservation_id = ?
                """;

        String insertNotificationSql = """
                INSERT INTO notifications
                (
                    user_id,
                    title,
                    message,
                    is_read
                )
                VALUES (?, ?, ?, FALSE)
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection()
        ) {

            connection.setAutoCommit(false);

            try (
                    PreparedStatement loanInfoStatement =
                            connection.prepareStatement(
                                    findLoanSql
                            );

                    PreparedStatement fineStatement =
                            connection.prepareStatement(
                                    finalizeFineSql
                            );

                    PreparedStatement updateLoanStatement =
                            connection.prepareStatement(
                                    updateLoanSql
                            );

                    PreparedStatement findBookStatement =
                            connection.prepareStatement(
                                    findBookSql
                            );

                    PreparedStatement reservationStatement =
                            connection.prepareStatement(
                                    findFirstReservationSql
                            );

                    PreparedStatement availableStatement =
                            connection.prepareStatement(
                                    updateCopyAvailableSql
                            );

                    PreparedStatement reservedStatement =
                            connection.prepareStatement(
                                    updateCopyReservedSql
                            );

                    PreparedStatement activateStatement =
                            connection.prepareStatement(
                                    activateReservationSql
                            );

                    PreparedStatement notificationStatement =
                            connection.prepareStatement(
                                    insertNotificationSql
                            )
            ) {

                loanInfoStatement.setInt(
                        1,
                        loanId
                );

                String userId;
                String loanCopyNumber;
                LocalDate dueDate;

                try (
                        ResultSet loanResult =
                                loanInfoStatement.executeQuery()
                ) {

                    if (!loanResult.next()) {
                        connection.rollback();
                        return false;
                    }

                    userId =
                            loanResult.getString(
                                    "user_id"
                            );

                    loanCopyNumber =
                            loanResult.getString(
                                    "copy_number"
                            );

                    Date dueDateValue =
                            loanResult.getDate(
                                    "due_date"
                            );

                    if (dueDateValue == null) {
                        connection.rollback();
                        return false;
                    }

                    dueDate =
                            dueDateValue.toLocalDate();
                }

                /*
                 * Ensure the copy being returned belongs
                 * to the selected loan.
                 */
                if (!copyNumber.equals(loanCopyNumber)) {
                    connection.rollback();
                    return false;
                }

                LocalDate returnDate =
                        LocalDate.now();

                long overdueDays =
                        Math.max(
                                0,
                                ChronoUnit.DAYS.between(
                                        dueDate,
                                        returnDate
                                )
                        );

                /*
                 * Freeze the running fine at the exact
                 * amount due on the return date.
                 */
                if (overdueDays > 0) {

                    BigDecimal fineAmount =
                            settings.getFinePerDay()
                                    .multiply(
                                            BigDecimal.valueOf(
                                                    overdueDays
                                            )
                                    );

                    fineStatement.setString(
                            1,
                            userId
                    );

                    fineStatement.setInt(
                            2,
                            loanId
                    );

                    fineStatement.setBigDecimal(
                            3,
                            fineAmount
                    );

                    fineStatement.executeUpdate();
                }

                /*
                 * Mark the loan as returned.
                 */
                updateLoanStatement.setDate(
                        1,
                        Date.valueOf(returnDate)
                );

                updateLoanStatement.setInt(
                        2,
                        loanId
                );

                int updatedLoans =
                        updateLoanStatement.executeUpdate();

                if (updatedLoans != 1) {
                    connection.rollback();
                    return false;
                }

                /*
                 * Find the ISBN and title of the returned copy.
                 */
                findBookStatement.setString(
                        1,
                        copyNumber
                );

                String isbn;
                String bookTitle;

                try (
                        ResultSet bookResult =
                                findBookStatement.executeQuery()
                ) {

                    if (!bookResult.next()) {
                        connection.rollback();
                        return false;
                    }

                    isbn =
                            bookResult.getString(
                                    "isbn"
                            );

                    bookTitle =
                            bookResult.getString(
                                    "title"
                            );
                }

                /*
                 * Check whether another member is waiting
                 * for this title.
                 */
                reservationStatement.setString(
                        1,
                        isbn
                );

                try (
                        ResultSet reservationResult =
                                reservationStatement.executeQuery()
                ) {

                    if (reservationResult.next()) {

                        processWaitingReservation(
                                settings,
                                copyNumber,
                                bookTitle,
                                reservationResult,
                                reservedStatement,
                                activateStatement,
                                notificationStatement
                        );

                    } else {

                        availableStatement.setString(
                                1,
                                copyNumber
                        );

                        int updatedCopies =
                                availableStatement.executeUpdate();

                        if (updatedCopies != 1) {
                            connection.rollback();
                            return false;
                        }
                    }
                }

                connection.commit();
                return true;

            } catch (Exception exception) {

                connection.rollback();
                exception.printStackTrace();

                return false;
            }

        } catch (Exception exception) {

            exception.printStackTrace();
            return false;
        }
    }

    private void processWaitingReservation(
            LibrarySettings settings,
            String copyNumber,
            String bookTitle,
            ResultSet reservationResult,
            PreparedStatement reservedStatement,
            PreparedStatement activateStatement,
            PreparedStatement notificationStatement
    ) throws Exception {

        int reservationId =
                reservationResult.getInt(
                        "reservation_id"
                );

        String reservedUserId =
                reservationResult.getString(
                        "user_id"
                );

        String memberName =
                reservationResult.getString(
                        "name"
                );

        LocalDate pickupExpiryDate =
                LocalDate.now().plusDays(
                        settings.getReservationPickupDays()
                );

        reservedStatement.setString(
                1,
                copyNumber
        );

        int updatedCopies =
                reservedStatement.executeUpdate();

        if (updatedCopies != 1) {
            throw new IllegalStateException(
                    "The returned copy could not be reserved."
            );
        }

        activateStatement.setDate(
                1,
                Date.valueOf(pickupExpiryDate)
        );

        activateStatement.setInt(
                2,
                reservationId
        );

        int activatedReservations =
                activateStatement.executeUpdate();

        if (activatedReservations != 1) {
            throw new IllegalStateException(
                    "The reservation could not be activated."
            );
        }

        String formattedExpiryDate =
                pickupExpiryDate.format(
                        DateTimeFormatter.ofPattern(
                                "dd MMMM yyyy"
                        )
                );

        String notificationTitle =
                "Book Ready for Collection";

        String notificationMessage =
                "Dear "
                        + memberName
                        + ",\n\n"
                        + "The book \""
                        + bookTitle
                        + "\" that you reserved is now available "
                        + "for collection.\n\n"
                        + "Please collect it from the library on or "
                        + "before "
                        + formattedExpiryDate
                        + ". If it is not collected within this period, "
                        + "the reservation will expire and the next "
                        + "member in the queue will be notified.\n\n"
                        + "Regards,\n"
                        + "IntelliLib Team";

        notificationStatement.setString(
                1,
                reservedUserId
        );

        notificationStatement.setString(
                2,
                notificationTitle
        );

        notificationStatement.setString(
                3,
                notificationMessage
        );

        notificationStatement.executeUpdate();
    }
}