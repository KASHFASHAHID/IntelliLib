package service;

import model.ActivityLog;
import repository.ActivityLogRepository;

import java.util.Collections;
import java.util.List;

public class ActivityLogService {

    private final ActivityLogRepository repository;

    public ActivityLogService() {
        repository = new ActivityLogRepository();
    }

    public boolean logActivity(
            String userId,
            String action,
            String details
    ) {

        if (action == null || action.isBlank()) {
            return false;
        }

        String cleanUserId =
                userId == null || userId.isBlank()
                        ? null
                        : userId.trim();

        String cleanAction = action.trim();

        String cleanDetails =
                details == null || details.isBlank()
                        ? null
                        : details.trim();

        return repository.saveLog(
                cleanUserId,
                cleanAction,
                cleanDetails
        );
    }

    public List<ActivityLog> getAllLogs() {

        List<ActivityLog> logs =
                repository.findAllLogs();

        if (logs == null) {
            return Collections.emptyList();
        }

        return logs;
    }
}