package service;

import model.Fine;
import repository.FineRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class FineService {

    private static final Set<String> ALLOWED_PAYMENT_METHODS =
            Set.of(
                    "CASH",
                    "UPI",
                    "CARD",
                    "BANK_TRANSFER"
            );

    private final FineRepository repository;
    private final NotificationService notificationService;
    private final ActivityLogService activityLogService;

    public FineService() {

        repository =
                new FineRepository();

        notificationService =
                new NotificationService();

        activityLogService =
                new ActivityLogService();
    }

    public List<Fine> getFinesByUser(
            String userId
    ) {

        if (userId == null || userId.isBlank()) {
            return List.of();
        }

        List<Fine> fines =
                repository.getFinesByUser(
                        userId.trim()
                );

        return fines == null
                ? List.of()
                : fines;
    }

    public BigDecimal getOutstandingFineAmount(
            String userId
    ) {

        if (userId == null || userId.isBlank()) {
            return BigDecimal.ZERO;
        }

        BigDecimal amount =
                repository.getOutstandingFineAmount(
                        userId.trim()
                );

        return amount == null
                ? BigDecimal.ZERO
                : amount;
    }

    public String validatePayment(
        int fineId,
        String memberUserId,
        String paymentMethod,
        String paidByUserId
) {

    if (fineId <= 0) {
        return "Please select a valid fine.";
    }

    if (memberUserId == null
            || memberUserId.isBlank()) {

        return "The member account could not be identified.";
    }

    if (paidByUserId == null
            || paidByUserId.isBlank()) {

        return "The staff account processing the payment "
                + "could not be identified.";
    }

    if (!repository.isFineFinalized(
            fineId,
            memberUserId.trim()
    )) {

        return "The book must be returned before this "
                + "running fine can be paid.";
    }

    if (paymentMethod == null
            || paymentMethod.isBlank()) {

        return "Please select a payment method.";
    }

    String normalizedMethod =
            paymentMethod
                    .trim()
                    .toUpperCase();

    if (!ALLOWED_PAYMENT_METHODS.contains(
            normalizedMethod
    )) {

        return "Invalid payment method. "
                + "Choose CASH, UPI, CARD, "
                + "or BANK_TRANSFER.";
    }

    return null;
}

    public BigDecimal payFine(
            int fineId,
            String memberUserId,
            String paymentMethod,
            String paymentReference,
            String paidByUserId
    ) {

        String validationMessage =
                validatePayment(
                        fineId,
                        memberUserId,
                        paymentMethod,
                        paidByUserId
                );

        if (validationMessage != null) {
            return null;
        }

        String normalizedMethod =
                paymentMethod
                        .trim()
                        .toUpperCase();

        boolean paymentRecorded =
                repository.markFineAsPaid(
                        fineId,
                        memberUserId.trim(),
                        normalizedMethod,
                        paymentReference,
                        paidByUserId.trim()
                );

        if (!paymentRecorded) {
            return null;
        }

        BigDecimal remainingBalance =
                repository.getOutstandingFineAmount(
                        memberUserId.trim()
                );

        if (remainingBalance == null) {
            remainingBalance =
                    BigDecimal.ZERO;
        }

        notificationService.createNotification(
                memberUserId.trim(),
                "Fine Payment Recorded",
                "Your fine payment has been recorded successfully."
                        + "\n\nFine ID: "
                        + fineId
                        + "\nPayment Method: "
                        + normalizedMethod
                        + "\nRemaining Outstanding Balance: ₹"
                        + remainingBalance
        );

        activityLogService.logActivity(
                paidByUserId.trim(),
                "FINE_PAYMENT_RECORDED",
                "Fine "
                        + fineId
                        + " for member "
                        + memberUserId.trim()
                        + " was marked as PAID using "
                        + normalizedMethod
                        + ". Remaining balance: ₹"
                        + remainingBalance
                        + "."
        );

        return remainingBalance;
    }

    public boolean isAllowedPaymentMethod(
            String paymentMethod
    ) {

        if (paymentMethod == null
                || paymentMethod.isBlank()) {

            return false;
        }

        return ALLOWED_PAYMENT_METHODS.contains(
                paymentMethod
                        .trim()
                        .toUpperCase()
        );
    }
}