package service;

import model.ReservationExpiryResult;
import model.ReservationExpiryResult.ExpiredEmailData;
import model.ReservationExpiryResult.PickupEmailData;
import repository.ReservationExpiryRepository;

public class ReservationExpiryService {

    private final ReservationExpiryRepository repository;
    private final EmailService emailService;
    private final ActivityLogService activityLogService;

    public ReservationExpiryService() {

        this.repository =
                new ReservationExpiryRepository();

        this.emailService =
                new EmailService();

        this.activityLogService =
                new ActivityLogService();
    }

    public int processExpiredReservations() {

        ReservationExpiryResult result =
                repository.processExpiredReservations();

        if (result == null) {
            return 0;
        }

        sendPickupEmails(
                result
        );

        sendExpiredReservationEmails(
                result
        );

        return result.processedCount();
    }

    private void sendPickupEmails(
            ReservationExpiryResult result
    ) {

        for (PickupEmailData emailData
                : result.pickupEmails()) {

            if (emailData == null
                    || emailData.email() == null
                    || emailData.email().isBlank()) {

                logPickupEmailFailure(
                        emailData,
                        "The member email address was missing."
                );

                continue;
            }

            boolean emailSent =
                    emailService
                            .sendBookReadyForPickupEmail(
                                    emailData.email(),
                                    emailData.memberName(),
                                    emailData.bookTitle(),
                                    emailData.pickupExpiryDate()
                            );

            if (!emailSent) {

                logPickupEmailFailure(
                        emailData,
                        "The email service could not deliver the message."
                );
            }
        }
    }

    private void sendExpiredReservationEmails(
            ReservationExpiryResult result
    ) {

        for (ExpiredEmailData emailData
                : result.expiredEmails()) {

            if (emailData == null
                    || emailData.email() == null
                    || emailData.email().isBlank()) {

                logExpiredEmailFailure(
                        emailData,
                        "The member email address was missing."
                );

                continue;
            }

            boolean emailSent =
                    emailService
                            .sendReservationExpiredEmail(
                                    emailData.email(),
                                    emailData.memberName(),
                                    emailData.bookTitle()
                            );

            if (!emailSent) {

                logExpiredEmailFailure(
                        emailData,
                        "The email service could not deliver the message."
                );
            }
        }
    }

    private void logPickupEmailFailure(
            PickupEmailData emailData,
            String reason
    ) {

        String userId =
                emailData == null
                        ? null
                        : emailData.userId();

        String bookTitle =
                emailData == null
                        ? "Unknown book"
                        : safeValue(
                                emailData.bookTitle()
                        );

        System.err.println(
                "Ready-for-pickup email could not be sent to "
                        + safeValue(userId)
                        + ". "
                        + reason
        );

        if (userId == null
                || userId.isBlank()) {

            return;
        }

        activityLogService.logActivity(
                userId,
                "READY_FOR_PICKUP_EMAIL_FAILED",
                "The ready-for-pickup email could not be sent "
                        + "for the reserved book \""
                        + bookTitle
                        + "\". "
                        + reason
        );
    }

    private void logExpiredEmailFailure(
            ExpiredEmailData emailData,
            String reason
    ) {

        String userId =
                emailData == null
                        ? null
                        : emailData.userId();

        String bookTitle =
                emailData == null
                        ? "Unknown book"
                        : safeValue(
                                emailData.bookTitle()
                        );

        System.err.println(
                "Reservation-expired email could not be sent to "
                        + safeValue(userId)
                        + ". "
                        + reason
        );

        if (userId == null
                || userId.isBlank()) {

            return;
        }

        activityLogService.logActivity(
                userId,
                "RESERVATION_EXPIRED_EMAIL_FAILED",
                "The reservation-expired email could not be sent "
                        + "for the book \""
                        + bookTitle
                        + "\". "
                        + reason
        );
    }

    private String safeValue(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            return "Unknown";
        }

        return value;
    }
}