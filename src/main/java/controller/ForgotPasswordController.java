package controller;

import service.PasswordResetService;

public class ForgotPasswordController {

    private final PasswordResetService service;

    public ForgotPasswordController() {
        service = new PasswordResetService();
    }

    public int sendOtp(
            String userId,
            String email
    ) {

        return service.sendOtp(
                userId,
                email
        );
    }

    public boolean verifyOtp(
            int resetId,
            String otp
    ) {

        return service.verifyOtp(
                resetId,
                otp
        );
    }

    public boolean resetPassword(
            int resetId,
            String newPassword,
            String confirmPassword
    ) {

        return service.resetPassword(
                resetId,
                newPassword,
                confirmPassword
        );
    }
}