package controller;

import service.AccountActivationService;

public class AccountActivationController {

    private final AccountActivationService service;

    public AccountActivationController() {
        service = new AccountActivationService();
    }

    public int sendActivationOtp(
            String userId,
            String email
    ) {

        return service.sendActivationOtp(
                userId,
                email
        );
    }

    public boolean verifyActivationOtp(
            int resetId,
            String enteredOtp
    ) {

        return service.verifyActivationOtp(
                resetId,
                enteredOtp
        );
    }

    public boolean activateAccount(
            int resetId,
            String newPassword,
            String confirmPassword
    ) {

        return service.activateAccount(
                resetId,
                newPassword,
                confirmPassword
        );
    }
}