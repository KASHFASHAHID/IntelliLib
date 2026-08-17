package controller;

import model.MembershipRequest;
import service.MembershipRequestService;

import java.util.List;

public class MembershipRequestController {

    private final MembershipRequestService service;

    public MembershipRequestController() {
        service = new MembershipRequestService();
    }

    public List<MembershipRequest> getPendingRequests() {
        return service.getPendingRequests();
    }

    public String[] approveRequest(
            MembershipRequest request,
            String reviewedBy
    ) {

        return service.approveRequest(
                request,
                reviewedBy
        );
    }

    public boolean rejectRequest(
            MembershipRequest request,
            String reviewedBy,
            String rejectionReason
    ) {

        return service.rejectRequest(
                request,
                reviewedBy,
                rejectionReason
        );
    }

    public boolean submitRequest(
            MembershipRequest request
    ) {

        return service.submitRequest(request);
    }
}