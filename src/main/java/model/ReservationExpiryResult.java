package model;

import java.time.LocalDate;
import java.util.List;

public record ReservationExpiryResult(
        int processedCount,
        List<PickupEmailData> pickupEmails,
        List<ExpiredEmailData> expiredEmails
) {

    public ReservationExpiryResult {

        pickupEmails =
                pickupEmails == null
                        ? List.of()
                        : List.copyOf(pickupEmails);

        expiredEmails =
                expiredEmails == null
                        ? List.of()
                        : List.copyOf(expiredEmails);
    }

    public record PickupEmailData(
            String userId,
            String email,
            String memberName,
            String bookTitle,
            LocalDate pickupExpiryDate
    ) {
    }

    public record ExpiredEmailData(
            String userId,
            String email,
            String memberName,
            String bookTitle
    ) {
    }
}