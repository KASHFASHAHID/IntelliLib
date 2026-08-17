package service;

import model.ActiveMember;
import repository.ActiveMemberRepository;
import repository.BorrowRepository;
import repository.FineRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ActiveMemberService {

    private final ActiveMemberRepository repository;
    private final FineRepository fineRepository;
    private final BorrowRepository borrowRepository;

    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;
    private final EmailService emailService;

    public ActiveMemberService() {

        repository =
                new ActiveMemberRepository();

        fineRepository =
                new FineRepository();

        borrowRepository =
                new BorrowRepository();

        activityLogService =
                new ActivityLogService();

        notificationService =
                new NotificationService();

        emailService =
                new EmailService();
    }

    public List<ActiveMember> getAllActiveMembers() {

        List<ActiveMember> members =
                repository.findAllActiveMembers();

        if (members == null) {
            return new ArrayList<>();
        }

        return members;
    }

    public List<ActiveMember> getAllManageableMembers() {

        List<ActiveMember> members =
                repository.findAllManageableMembers();

        if (members == null) {
            return new ArrayList<>();
        }

        return members;
    }

    public String getReactivationBlockReason(
            String memberUserId
    ) {

        if (memberUserId == null
                || memberUserId.isBlank()) {

            return "Invalid member account.";
        }

        String cleanUserId =
                memberUserId.trim();

        BigDecimal outstandingFine =
                fineRepository.getOutstandingFineAmount(
                        cleanUserId
                );

        if (outstandingFine == null) {
            outstandingFine = BigDecimal.ZERO;
        }

        if (outstandingFine.compareTo(
                BigDecimal.ZERO
        ) > 0) {

            return "This member has an unpaid fine of ₹"
                    + outstandingFine
                    + ". Clear the fine before reactivation.";
        }

        boolean hasOverdueLoans =
                borrowRepository.hasOverdueActiveLoans(
                        cleanUserId
                );

        if (hasOverdueLoans) {

            return "This member still has one or more overdue books. "
                    + "Return or resolve them before reactivation.";
        }

        return null;
    }

    public boolean suspendMember(
            String memberUserId,
            String reason,
            String performedBy
    ) {

        return changeMemberStatus(
                memberUserId,
                "SUSPENDED",
                reason,
                performedBy
        );
    }

    public boolean blockMember(
            String memberUserId,
            String reason,
            String performedBy
    ) {

        return changeMemberStatus(
                memberUserId,
                "BLOCKED",
                reason,
                performedBy
        );
    }

    public boolean reactivateMember(
            String memberUserId,
            String reason,
            String performedBy
    ) {

        String blockReason =
                getReactivationBlockReason(
                        memberUserId
                );

        if (blockReason != null) {
            return false;
        }

        return changeMemberStatus(
                memberUserId,
                "ACTIVE",
                reason,
                performedBy
        );
    }

    private boolean changeMemberStatus(
            String memberUserId,
            String newStatus,
            String reason,
            String performedBy
    ) {

        if (memberUserId == null
                || memberUserId.isBlank()
                || newStatus == null
                || newStatus.isBlank()
                || performedBy == null
                || performedBy.isBlank()) {

            return false;
        }

        String safeReason =
                reason == null
                        ? ""
                        : reason.trim();

        if (safeReason.isBlank()) {
            return false;
        }

        if (safeReason.length() > 500) {
            return false;
        }

        ActiveMember member =
                repository.findManageableMemberById(
                        memberUserId.trim()
                );

        if (member == null) {
            return false;
        }

        String currentStatus =
                member.getAccountStatus() == null
                        ? ""
                        : member.getAccountStatus()
                                .trim()
                                .toUpperCase();

        String normalizedNewStatus =
                newStatus
                        .trim()
                        .toUpperCase();

        if (currentStatus.equals(
                normalizedNewStatus
        )) {
            return false;
        }

        if (!isValidTransition(
                currentStatus,
                normalizedNewStatus
        )) {
            return false;
        }

        boolean updated =
                repository.updateAccountStatus(
                        member.getUserId(),
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
                "Changed member account "
                        + member.getUserId()
                        + " from "
                        + currentStatus
                        + " to "
                        + normalizedNewStatus
                        + ". Reason: "
                        + safeReason;

        activityLogService.logActivity(
                performedBy.trim(),
                action,
                details
        );

        sendMemberNotification(
                member,
                normalizedNewStatus,
                safeReason
        );

        boolean emailSent =
                emailService.sendAccountStatusEmail(
                        member.getEmail(),
                        member.getName(),
                        member.getUserId(),
                        normalizedNewStatus,
                        safeReason
                );

        if (!emailSent) {

            System.err.println(
                    "Account status was updated, "
                            + "but email delivery failed for "
                            + member.getUserId()
            );

            activityLogService.logActivity(
                    performedBy.trim(),
                    "ACCOUNT_STATUS_EMAIL_FAILED",
                    "Account status email could not be sent to "
                            + member.getUserId()
                            + " after status changed to "
                            + normalizedNewStatus
                            + "."
            );
        }

        return true;
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
                            || "SUSPENDED".equals(
                            currentStatus
                    );

            case "ACTIVE" ->
                    "SUSPENDED".equals(currentStatus)
                            || "BLOCKED".equals(
                            currentStatus
                    );

            default -> false;
        };
    }

    private String getActivityAction(
            String newStatus
    ) {

        return switch (newStatus) {

            case "SUSPENDED" ->
                    "MEMBER_SUSPENDED";

            case "BLOCKED" ->
                    "MEMBER_BLOCKED";

            case "ACTIVE" ->
                    "MEMBER_REACTIVATED";

            default ->
                    "MEMBER_STATUS_CHANGED";
        };
    }

    private void sendMemberNotification(
            ActiveMember member,
            String newStatus,
            String reason
    ) {

        String title;
        String message;

        switch (newStatus) {

            case "SUSPENDED" -> {

                title =
                        "Library Account Suspended";

                message =
                        "Dear "
                                + member.getName()
                                + ",\n\n"
                                + "Your library account has been "
                                + "temporarily suspended.\n\n"
                                + "Reason: "
                                + reason
                                + "\n\n"
                                + "You cannot borrow or reserve new books "
                                + "until your account is reactivated.\n\n"
                                + "Please contact the library administration "
                                + "for assistance.";
            }

            case "BLOCKED" -> {

                title =
                        "Library Account Blocked";

                message =
                        "Dear "
                                + member.getName()
                                + ",\n\n"
                                + "Your library account has been blocked.\n\n"
                                + "Reason: "
                                + reason
                                + "\n\n"
                                + "Login, borrowing and reservation access "
                                + "are currently restricted.\n\n"
                                + "Please contact the library administration "
                                + "for further information.";
            }

            case "ACTIVE" -> {

                title =
                        "Library Account Reactivated";

                message =
                        "Dear "
                                + member.getName()
                                + ",\n\n"
                                + "Your library account has been "
                                + "reactivated successfully.\n\n"
                                + "Reason/Remarks: "
                                + reason
                                + "\n\n"
                                + "You can now use your available library "
                                + "services again.";
            }

            default -> {
                return;
            }
        }

        notificationService.createNotification(
                member.getUserId(),
                title,
                message
        );
    }
}