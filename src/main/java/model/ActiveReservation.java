package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ActiveReservation {

    private int reservationId;
    private String userId;
    private String memberName;
    private String isbn;
    private String bookTitle;
    private String status;
    private LocalDate reservationDate;
    private int queuePosition;
    private LocalDate pickupExpiryDate;
    private boolean notificationSent;
    private LocalDateTime createdAt;

    public ActiveReservation() {
    }

    public ActiveReservation(
            int reservationId,
            String userId,
            String memberName,
            String isbn,
            String bookTitle,
            String status,
            LocalDate reservationDate,
            int queuePosition,
            LocalDate pickupExpiryDate,
            boolean notificationSent,
            LocalDateTime createdAt
    ) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.memberName = memberName;
        this.isbn = isbn;
        this.bookTitle = bookTitle;
        this.status = status;
        this.reservationDate = reservationDate;
        this.queuePosition = queuePosition;
        this.pickupExpiryDate = pickupExpiryDate;
        this.notificationSent = notificationSent;
        this.createdAt = createdAt;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public int getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(int queuePosition) {
        this.queuePosition = queuePosition;
    }

    public LocalDate getPickupExpiryDate() {
        return pickupExpiryDate;
    }

    public void setPickupExpiryDate(LocalDate pickupExpiryDate) {
        this.pickupExpiryDate = pickupExpiryDate;
    }

    public boolean isNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(boolean notificationSent) {
        this.notificationSent = notificationSent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}