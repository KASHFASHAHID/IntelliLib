package controller;


import model.Notification;
import service.NotificationService;

import java.util.List;

public class NotificationController {

    private final NotificationService service;

    public NotificationController() {
        this.service = new NotificationService();
    }

    public List<Notification> getNotificationsByUser(
            String userId
    ) {
        return service.getNotificationsByUser(userId);
    }

    public boolean createNotification(
            String userId,
            String title,
            String message
    ) {
        return service.createNotification(
                userId,
                title,
                message
        );
    }

    public boolean markAllAsRead(
            String userId
    ) {
        return service.markAllAsRead(userId);
    }
}