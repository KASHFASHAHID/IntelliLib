package repository;

import config.DatabaseConnection;
import model.ReservationExpiryResult;
import model.ReservationExpiryResult.ExpiredEmailData;
import model.ReservationExpiryResult.PickupEmailData;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReservationExpiryRepository {

    public ReservationExpiryResult processExpiredReservations() {

        String findExpiredSql = """
                SELECT
                    r.reservation_id,
                    r.user_id,
                    r.isbn,
                    u.name AS member_name,
                    u.email AS member_email,
                    b.title AS book_title
                FROM reservations r
                JOIN users u
                    ON r.user_id = u.user_id
                JOIN books b
                    ON r.isbn = b.isbn
                WHERE r.status = 'READY_FOR_PICKUP'
                  AND r.pickup_expiry_date < CURRENT_DATE
                ORDER BY
                    r.pickup_expiry_date,
                    r.reservation_id
                FOR UPDATE
                """;

        String expireReservationSql = """
                UPDATE reservations
                SET status = 'EXPIRED',
                    pickup_expiry_date = NULL
                WHERE reservation_id = ?
                  AND status = 'READY_FOR_PICKUP'
                """;

        String findNextWaitingSql = """
                SELECT
                    r.reservation_id,
                    r.user_id,
                    u.name,
                    u.email,
                    b.title
                FROM reservations r
                JOIN users u
                    ON r.user_id = u.user_id
                JOIN books b
                    ON r.isbn = b.isbn
                WHERE r.isbn = ?
                  AND r.status = 'WAITING'
                  AND u.account_status = 'ACTIVE'
                ORDER BY
                    r.queue_position,
                    r.reservation_date,
                    r.reservation_id
                LIMIT 1
                FOR UPDATE
                """;

        String activateNextSql = """
                UPDATE reservations
                SET status = 'READY_FOR_PICKUP',
                    pickup_expiry_date = ?,
                    notification_sent = TRUE
                WHERE reservation_id = ?
                  AND status = 'WAITING'
                """;

        String findReservedCopySql = """
                SELECT copy_number
                FROM book_copies
                WHERE isbn = ?
                  AND status = 'RESERVED'
                ORDER BY copy_number
                LIMIT 1
                FOR UPDATE
                """;

        String releaseReservedCopySql = """
                UPDATE book_copies
                SET status = 'AVAILABLE'
                WHERE copy_number = ?
                  AND status = 'RESERVED'
                """;

        String createNotificationSql = """
                INSERT INTO notifications(
                    user_id,
                    title,
                    message,
                    is_read
                )
                VALUES (?, ?, ?, FALSE)
                """;

        int processedCount = 0;

        List<PickupEmailData> pickupEmails =
                new ArrayList<>();

        List<ExpiredEmailData> expiredEmails =
                new ArrayList<>();

        try (
                Connection connection =
                        DatabaseConnection.getConnection()
        ) {

            connection.setAutoCommit(false);

            try (
                    PreparedStatement findExpiredStatement =
                            connection.prepareStatement(
                                    findExpiredSql
                            );

                    PreparedStatement expireStatement =
                            connection.prepareStatement(
                                    expireReservationSql
                            );

                    PreparedStatement findNextStatement =
                            connection.prepareStatement(
                                    findNextWaitingSql
                            );

                    PreparedStatement activateNextStatement =
                            connection.prepareStatement(
                                    activateNextSql
                            );

                    PreparedStatement findReservedCopyStatement =
                            connection.prepareStatement(
                                    findReservedCopySql
                            );

                    PreparedStatement releaseCopyStatement =
                            connection.prepareStatement(
                                    releaseReservedCopySql
                            );

                    PreparedStatement notificationStatement =
                            connection.prepareStatement(
                                    createNotificationSql
                            );

                    ResultSet expiredResult =
                            findExpiredStatement.executeQuery()
            ) {

                while (expiredResult.next()) {

                    int expiredReservationId =
                            expiredResult.getInt(
                                    "reservation_id"
                            );

                    String expiredUserId =
                            expiredResult.getString(
                                    "user_id"
                            );

                    String expiredMemberName =
                            expiredResult.getString(
                                    "member_name"
                            );

                    String expiredMemberEmail =
                            expiredResult.getString(
                                    "member_email"
                            );

                    String isbn =
                            expiredResult.getString(
                                    "isbn"
                            );

                    String expiredBookTitle =
                            expiredResult.getString(
                                    "book_title"
                            );

                    expireStatement.setInt(
                            1,
                            expiredReservationId
                    );

                    int expiredRows =
                            expireStatement.executeUpdate();

                    if (expiredRows != 1) {
                        continue;
                    }

                    createExpiredNotification(
                            notificationStatement,
                            expiredUserId,
                            expiredBookTitle
                    );

                    expiredEmails.add(
                            new ExpiredEmailData(
                                    expiredUserId,
                                    expiredMemberEmail,
                                    expiredMemberName,
                                    expiredBookTitle
                            )
                    );

                    findNextStatement.setString(
                            1,
                            isbn
                    );

                    boolean nextMemberPromoted =
                            false;

                    try (
                            ResultSet nextResult =
                                    findNextStatement.executeQuery()
                    ) {

                        if (nextResult.next()) {

                            int nextReservationId =
                                    nextResult.getInt(
                                            "reservation_id"
                                    );

                            String nextUserId =
                                    nextResult.getString(
                                            "user_id"
                                    );

                            String nextMemberName =
                                    nextResult.getString(
                                            "name"
                                    );

                            String nextMemberEmail =
                                    nextResult.getString(
                                            "email"
                                    );

                            String nextBookTitle =
                                    nextResult.getString(
                                            "title"
                                    );

                            LocalDate newExpiryDate =
                                    LocalDate.now()
                                            .plusDays(2);

                            activateNextStatement.setDate(
                                    1,
                                    Date.valueOf(
                                            newExpiryDate
                                    )
                            );

                            activateNextStatement.setInt(
                                    2,
                                    nextReservationId
                            );

                            int activatedRows =
                                    activateNextStatement
                                            .executeUpdate();

                            if (activatedRows == 1) {

                                createReadyNotification(
                                        notificationStatement,
                                        nextUserId,
                                        nextMemberName,
                                        nextBookTitle,
                                        newExpiryDate
                                );

                                pickupEmails.add(
                                        new PickupEmailData(
                                                nextUserId,
                                                nextMemberEmail,
                                                nextMemberName,
                                                nextBookTitle,
                                                newExpiryDate
                                        )
                                );

                                nextMemberPromoted =
                                        true;
                            }
                        }
                    }

                    if (!nextMemberPromoted) {

                        releaseReservedCopy(
                                findReservedCopyStatement,
                                releaseCopyStatement,
                                isbn
                        );
                    }

                    processedCount++;
                }

                connection.commit();

                return new ReservationExpiryResult(
                        processedCount,
                        pickupEmails,
                        expiredEmails
                );

            } catch (Exception exception) {

                connection.rollback();

                System.err.println(
                        "Expired reservations could not be processed: "
                                + exception.getMessage()
                );

                exception.printStackTrace();

                return emptyResult();
            }

        } catch (Exception exception) {

            System.err.println(
                    "Reservation expiry transaction failed: "
                            + exception.getMessage()
            );

            exception.printStackTrace();

            return emptyResult();
        }
    }

    private ReservationExpiryResult emptyResult() {

        return new ReservationExpiryResult(
                0,
                List.of(),
                List.of()
        );
    }

    private void releaseReservedCopy(
            PreparedStatement findReservedCopyStatement,
            PreparedStatement releaseCopyStatement,
            String isbn
    ) throws Exception {

        findReservedCopyStatement.setString(
                1,
                isbn
        );

        try (
                ResultSet copyResult =
                        findReservedCopyStatement.executeQuery()
        ) {

            if (!copyResult.next()) {
                return;
            }

            String copyNumber =
                    copyResult.getString(
                            "copy_number"
                    );

            releaseCopyStatement.setString(
                    1,
                    copyNumber
            );

            releaseCopyStatement.executeUpdate();
        }
    }

    private void createExpiredNotification(
            PreparedStatement notificationStatement,
            String userId,
            String bookTitle
    ) throws Exception {

        String title =
                "Reservation Expired";

        String message =
                "Your reservation for \""
                        + bookTitle
                        + "\" expired because the book was not collected "
                        + "within the pickup period.";

        notificationStatement.setString(
                1,
                userId
        );

        notificationStatement.setString(
                2,
                title
        );

        notificationStatement.setString(
                3,
                message
        );

        notificationStatement.executeUpdate();
    }

    private void createReadyNotification(
            PreparedStatement notificationStatement,
            String userId,
            String memberName,
            String bookTitle,
            LocalDate expiryDate
    ) throws Exception {

        String formattedExpiryDate =
                expiryDate.format(
                        DateTimeFormatter.ofPattern(
                                "dd MMMM yyyy"
                        )
                );

        String title =
                "Book Ready for Collection";

        String message =
                "Dear "
                        + memberName
                        + ",\n\n"
                        + "The book \""
                        + bookTitle
                        + "\" is now available for collection.\n\n"
                        + "Please collect it on or before "
                        + formattedExpiryDate
                        + ". If it is not collected within this period, "
                        + "the reservation will expire automatically.\n\n"
                        + "Regards,\n"
                        + "IntelliLib";

        notificationStatement.setString(
                1,
                userId
        );

        notificationStatement.setString(
                2,
                title
        );

        notificationStatement.setString(
                3,
                message
        );

        notificationStatement.executeUpdate();
    }
}