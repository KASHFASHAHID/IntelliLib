package model;

import java.time.LocalDate;

public class ReadyForPickup {

    private int reservationId;
    private String userId;
    private String memberName;
    private String isbn;
    private String bookTitle;
    private int queuePosition;
    private LocalDate pickupExpiryDate;

    public ReadyForPickup(
            int reservationId,
            String userId,
            String memberName,
            String isbn,
            String bookTitle,
            int queuePosition,
            LocalDate pickupExpiryDate
    ) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.memberName = memberName;
        this.isbn = isbn;
        this.bookTitle = bookTitle;
        this.queuePosition = queuePosition;
        this.pickupExpiryDate = pickupExpiryDate;
    }

    public int getReservationId() {
        return reservationId;
    }

    public String getUserId() {
        return userId;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public int getQueuePosition() {
        return queuePosition;
    }

    public LocalDate getPickupExpiryDate() {
        return pickupExpiryDate;
    }
}