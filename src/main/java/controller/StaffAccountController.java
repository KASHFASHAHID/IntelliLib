package controller;

import model.StaffAccount;
import service.StaffAccountService;

import java.util.List;
import model.Role;

public class StaffAccountController {

    private final StaffAccountService service;

    public StaffAccountController() {
        service = new StaffAccountService();
    }

    public List<StaffAccount> getAllStaffAccounts(
            String performedBy
    ) {

        return service.getAllStaffAccounts(
                performedBy
        );
    }

    public String createStaffAccount(
        String name,
        String email,
        Role role,
        String performedBy
) {

    return service.createStaffAccount(
            name,
            email,
            role,
            performedBy
    );
}

    public boolean suspendStaff(
            String staffUserId,
            String reason,
            String performedBy
    ) {

        return service.suspendStaff(
                staffUserId,
                reason,
                performedBy
        );
    }

    public boolean blockStaff(
            String staffUserId,
            String reason,
            String performedBy
    ) {

        return service.blockStaff(
                staffUserId,
                reason,
                performedBy
        );
    }

    public boolean reactivateStaff(
            String staffUserId,
            String reason,
            String performedBy
    ) {

        return service.reactivateStaff(
                staffUserId,
                reason,
                performedBy
        );
    }
}