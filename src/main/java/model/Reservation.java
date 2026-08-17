package model;

import java.time.LocalDate;

public class Reservation {

    private String title;
    private String authors;
    private String isbn;
    private int queuePosition;
    private String status;
    private LocalDate reservationDate;
    private LocalDate pickupExpiryDate;

    public Reservation(
            String title,
            String authors,
            String isbn,
            int queuePosition,
            String status,
            LocalDate reservationDate,
            LocalDate pickupExpiryDate) {

        this.title = title;
        this.authors = authors;
        this.isbn = isbn;
        this.queuePosition = queuePosition;
        this.status = status;
        this.reservationDate = reservationDate;
        this.pickupExpiryDate = pickupExpiryDate;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getQueuePosition() {
        return queuePosition;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public LocalDate getPickupExpiryDate() {
        return pickupExpiryDate;
    }
}