package service;

import model.MembershipRequest;
import repository.MembershipRequestRepository;

import java.util.List;

public class MembershipRequestService {

    private final MembershipRequestRepository repository;
    private final EmailService emailService;
    private final ActivityLogService activityLogService;

    public MembershipRequestService() {
        repository = new MembershipRequestRepository();
        emailService = new EmailService();
        activityLogService = new ActivityLogService();
    }

    public List<MembershipRequest> getPendingRequests() {
        return repository.getAllPendingRequests();
    }

    public String[] approveRequest(
            MembershipRequest request,
            String reviewedBy
    ) {

        if (request == null
                || reviewedBy == null
                || reviewedBy.isBlank()) {

            return null;
        }

        String[] result = repository.approveRequest(
                request,
                reviewedBy
        );

        if (result == null) {
            return null;
        }

        String newUserId = result[0];
        String cardNumber = result[1];

        boolean emailSent =
                emailService.sendMembershipApprovalEmail(
                        request.getEmail(),
                        request.getFullName(),
                        newUserId,
                        cardNumber
                );

        activityLogService.logActivity(
                reviewedBy,
                "MEMBERSHIP_APPROVED",
                "Approved membership request "
                        + request.getRequestId()
                        + " and created account "
                        + newUserId
                        + "."
        );

        return new String[]{
                newUserId,
                cardNumber,
                String.valueOf(emailSent)
        };
    }

    public boolean rejectRequest(
            MembershipRequest request,
            String reviewedBy,
            String rejectionReason
    ) {

        boolean rejected = repository.rejectRequest(
                request,
                reviewedBy,
                rejectionReason
        );

        if (rejected) {

            activityLogService.logActivity(
                    reviewedBy,
                    "MEMBERSHIP_REJECTED",
                    "Rejected membership request "
                            + request.getRequestId()
                            + " for "
                            + request.getFullName()
                            + "."
            );
        }

        return rejected;
    }

    public boolean submitRequest(
            MembershipRequest request
    ) {

        return repository.submitRequest(request);
    }
}