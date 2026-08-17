package service;

import model.Notification;
import repository.NotificationRepository;

import java.util.List;

public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService() {
        this.repository = new NotificationRepository();
    }

    public List<Notification> getNotificationsByUser(
            String userId
    ) {
        return repository.getNotificationsByUser(userId);
    }

    public boolean createNotification(
            String userId,
            String title,
            String message
    ) {
        return repository.createNotification(
                userId,
                title,
                message
        );
    }

    public boolean markAllAsRead(
            String userId
    ) {
        return repository.markAllAsRead(userId);
    }
}