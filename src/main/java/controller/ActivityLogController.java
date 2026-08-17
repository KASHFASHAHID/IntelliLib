package controller;

import model.ActivityLog;
import service.ActivityLogService;

import java.util.List;

public class ActivityLogController {

    private final ActivityLogService service;

    public ActivityLogController() {
        service = new ActivityLogService();
    }

    public boolean logActivity(
            String userId,
            String action,
            String details
    ) {

        return service.logActivity(
                userId,
                action,
                details
        );
    }

    public List<ActivityLog> getAllLogs() {
        return service.getAllLogs();
    }
}