package model;

import java.time.LocalDateTime;

public class Notification {

    private String title;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;

    public Notification(
            String title,
            String message,
            boolean read,
            LocalDateTime createdAt) {

        this.title = title;
        this.message = message;
        this.read = read;
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
