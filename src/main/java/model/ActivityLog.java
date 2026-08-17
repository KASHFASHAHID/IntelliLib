package model;

import java.time.LocalDateTime;

public class ActivityLog {

    private int logId;
    private String userId;
    private String action;
    private String details;
    private LocalDateTime createdAt;

    public ActivityLog(
            int logId,
            String userId,
            String action,
            String details,
            LocalDateTime createdAt
    ) {
        this.logId = logId;
        this.userId = userId;
        this.action = action;
        this.details = details;
        this.createdAt = createdAt;
    }

    public int getLogId() {
        return logId;
    }

    public String getUserId() {
        return userId;
    }

    public String getAction() {
        return action;
    }

    public String getDetails() {
        return details;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}