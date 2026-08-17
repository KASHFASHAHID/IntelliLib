package service;

import model.Role;
import model.StaffAccount;
import model.User;
import repository.StaffAccountRepository;
import repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class StaffAccountService {

    private final StaffAccountRepository repository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;
    private final EmailService emailService;

    public StaffAccountService() {

        repository =
                new StaffAccountRepository();

        userRepository =
                new UserRepository();

        activityLogService =
                new ActivityLogService();

        notificationService =
                new NotificationService();

        emailService =
                new EmailService();
    }

    public List<StaffAccount> getAllStaffAccounts(
            String performedBy
    ) {

        if (!isAuthorizedSuperAdmin(performedBy)) {
            return new ArrayList<>();
        }

        List<StaffAccount> staffAccounts =
                repository.findAllStaffAccounts();

        if (staffAccounts == null) {
            return new ArrayList<>();
        }

        return staffAccounts;
    }

    public String createStaffAccount(
            String name,
            String email,
            Role role,
            String performedBy
    ) {

        if (!isAuthorizedSuperAdmin(performedBy)) {
            return null;
        }

        if (name == null
                || name.isBlank()
                || email == null
                || email.isBlank()
                || role == null) {

            return null;
        }

        if (role != Role.ADMIN
                && role != Role.LIBRARIAN) {

            return null;
        }

        String cleanName =
                name.trim();

        String cleanEmail =
                email.trim().toLowerCase();

        if (cleanName.length() < 3
                || cleanName.length() > 100) {

            return null;
        }

        if (cleanEmail.length() > 100
                || !cleanEmail.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        )) {

            return null;
        }

        String userId =
                repository.createPendingStaffAccount(
                        cleanName,
                        cleanEmail,
                        role
                );

        if (userId == null) {
            return null;
        }

        activityLogService.logActivity(
                performedBy.trim(),
                "STAFF_ACCOUNT_CREATED",
                "Created pending "
                        + role
                        + " account "
                        + userId
                        + "."
        );

        notificationService.createNotification(
                userId,
                "Staff Account Created",
                "Your "
                        + role
                        + " account has been created."
                        + "\n\nUser ID: "
                        + userId
                        + "\nStatus: PENDING_ACTIVATION"
                        + "\n\nUse Account Access → Activate New Account "
                        + "to verify your email and create your password."
        );

        boolean emailSent =
                emailService.sendStaffAccountCreationEmail(
                        cleanEmail,
                        cleanName,
                        userId,
                        role
                );

        if (!emailSent) {

            System.err.println(
                    "Staff account was created, "
                            + "but the invitation email could not be sent to "
                            + cleanEmail
            );

            activityLogService.logActivity(
                    performedBy.trim(),
                    "STAFF_INVITATION_EMAIL_FAILED",
                    "Invitation email could not be sent for staff account "
                            + userId
                            + "."
            );
        }

        return userId;
    }

    public boolean suspendStaff(
            String staffUserId,
            String reason,
            String performedBy
    ) {

        return changeStaffStatus(
                staffUserId,
                "SUSPENDED",
                reason,
                performedBy
        );
    }

    public boolean blockStaff(
            String staffUserId,
            String reason,
            String performedBy
    ) {

        return changeStaffStatus(
                staffUserId,
                "BLOCKED",
                reason,
                performedBy
        );
    }

    public boolean reactivateStaff(
            String staffUserId,
            String reason,
            String performedBy
    ) {

        return changeStaffStatus(
                staffUserId,
                "ACTIVE",
                reason,
                performedBy
        );
    }

    private boolean changeStaffStatus(
            String staffUserId,
            String newStatus,
            String reason,
            String performedBy
    ) {

        if (!isAuthorizedSuperAdmin(performedBy)) {
            return false;
        }

        if (staffUserId == null
                || staffUserId.isBlank()
                || newStatus == null
                || newStatus.isBlank()
                || reason == null
                || reason.isBlank()) {

            return false;
        }

        String cleanStaffUserId =
                staffUserId.trim();

        String cleanReason =
                reason.trim();

        String normalizedNewStatus =
                newStatus.trim().toUpperCase();

        if (cleanReason.length() < 5
                || cleanReason.length() > 500) {

            return false;
        }

        if (cleanStaffUserId.equalsIgnoreCase(
                performedBy.trim()
        )) {

            return false;
        }

        StaffAccount staffAccount =
                repository.findStaffAccountById(
                        cleanStaffUserId
                );

        if (staffAccount == null) {
            return false;
        }

        if (staffAccount.getRole() != Role.ADMIN
                && staffAccount.getRole() != Role.LIBRARIAN) {

            return false;
        }

        String currentStatus =
                staffAccount.getAccountStatus() == null
                        ? ""
                        : staffAccount
                                .getAccountStatus()
                                .trim()
                                .toUpperCase();

        if (currentStatus.equals(normalizedNewStatus)) {
            return false;
        }

        if (!isValidTransition(
                currentStatus,
                normalizedNewStatus
        )) {

            return false;
        }

        boolean updated =
                repository.updateStaffAccountStatus(
                        cleanStaffUserId,
                        normalizedNewStatus
                );

        if (!updated) {
            return false;
        }

        String action =
                getActivityAction(
                        normalizedNewStatus
                );

        String details =
                "Changed "
                        + staffAccount.getRole()
                        + " account "
                        + staffAccount.getUserId()
                        + " from "
                        + currentStatus
                        + " to "
                        + normalizedNewStatus
                        + ". Reason: "
                        + cleanReason;

        activityLogService.logActivity(
                performedBy.trim(),
                action,
                details
        );

        sendStaffNotification(
                staffAccount,
                normalizedNewStatus,
                cleanReason
        );

        boolean emailSent =
        emailService.sendStaffAccountStatusEmail(
                staffAccount.getEmail(),
                staffAccount.getName(),
                staffAccount.getUserId(),
                staffAccount.getRole(),
                normalizedNewStatus,
                cleanReason
        );

        if (!emailSent) {

            System.err.println(
                    "Staff status was updated, "
                            + "but email delivery failed for "
                            + staffAccount.getUserId()
            );

            activityLogService.logActivity(
                    performedBy.trim(),
                    "STAFF_STATUS_EMAIL_FAILED",
                    "Status email could not be sent to "
                            + staffAccount.getUserId()
                            + " after account changed to "
                            + normalizedNewStatus
                            + "."
            );
        }

        return true;
    }

    private boolean isAuthorizedSuperAdmin(
            String performedBy
    ) {

        if (performedBy == null
                || performedBy.isBlank()) {

            return false;
        }

        User user =
                userRepository.findActiveUserById(
                        performedBy.trim()
                );

        return user != null
                && user.getRole() == Role.SUPER_ADMIN;
    }

    private boolean isValidTransition(
            String currentStatus,
            String newStatus
    ) {

        return switch (newStatus) {

            case "SUSPENDED" ->
                    "ACTIVE".equals(currentStatus);

            case "BLOCKED" ->
                    "ACTIVE".equals(currentStatus)
                            || "SUSPENDED".equals(currentStatus);

            case "ACTIVE" ->
                    "SUSPENDED".equals(currentStatus)
                            || "BLOCKED".equals(currentStatus);

            default -> false;
        };
    }

    private String getActivityAction(
            String newStatus
    ) {

        return switch (newStatus) {

            case "SUSPENDED" ->
                    "STAFF_ACCOUNT_SUSPENDED";

            case "BLOCKED" ->
                    "STAFF_ACCOUNT_BLOCKED";

            case "ACTIVE" ->
                    "STAFF_ACCOUNT_REACTIVATED";

            default ->
                    "STAFF_ACCOUNT_STATUS_CHANGED";
        };
    }

    private void sendStaffNotification(
            StaffAccount staffAccount,
            String newStatus,
            String reason
    ) {

        String title;
        String message;

        switch (newStatus) {

            case "SUSPENDED" -> {

                title =
                        "Staff Account Suspended";

                message =
                        "Your "
                                + staffAccount.getRole()
                                + " account has been temporarily suspended."
                                + "\n\nReason: "
                                + reason
                                + "\n\nPlease contact the Super Administrator "
                                + "for assistance.";
            }

            case "BLOCKED" -> {

                title =
                        "Staff Account Blocked";

                message =
                        "Your "
                                + staffAccount.getRole()
                                + " account has been blocked."
                                + "\n\nReason: "
                                + reason
                                + "\n\nLogin and administrative access "
                                + "are currently restricted.";
            }

            case "ACTIVE" -> {

                title =
                        "Staff Account Reactivated";

                message =
                        "Your "
                                + staffAccount.getRole()
                                + " account has been reactivated successfully."
                                + "\n\nRemarks: "
                                + reason
                                + "\n\nYou may now access your authorized "
                                + "library functions again.";
            }

            default -> {
                return;
            }
        }

        notificationService.createNotification(
                staffAccount.getUserId(),
                title,
                message
        );
    }
}