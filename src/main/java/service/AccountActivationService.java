package service;

import model.PasswordResetOtp;
import model.User;
import org.mindrot.jbcrypt.BCrypt;
import repository.AccountActivationRepository;
import util.OtpGenerator;

import java.time.LocalDateTime;

public class AccountActivationService {

    private static final int OTP_VALIDITY_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 3;

    private final AccountActivationRepository repository;
    private final EmailService emailService;
    private final ActivityLogService activityLogService;

    public AccountActivationService() {

        repository = new AccountActivationRepository();
        emailService = new EmailService();
        activityLogService = new ActivityLogService();
    }

    public int sendActivationOtp(
            String userId,
            String email
    ) {

        if (userId == null || userId.isBlank()
                || email == null || email.isBlank()) {

            return -1;
        }

        User user = repository.findPendingUser(
                userId.trim(),
                email.trim()
        );

        if (user == null) {
            return -1;
        }

        String otp = OtpGenerator.generateOtp();

        String otpHash = BCrypt.hashpw(
                otp,
                BCrypt.gensalt(10)
        );

        LocalDateTime expiresAt =
                LocalDateTime.now()
                        .plusMinutes(OTP_VALIDITY_MINUTES);

        repository.invalidateExistingOtps(
                user.getUserId()
        );

        int resetId = repository.saveOtp(
                user.getUserId(),
                otpHash,
                expiresAt
        );

        if (resetId == -1) {
            return -1;
        }

        boolean emailSent =
                emailService.sendAccountActivationOtp(
                        user.getEmail(),
                        user.getName(),
                        otp
                );

        if (!emailSent) {

            repository.deleteOtp(resetId);
            return -1;
        }

        activityLogService.logActivity(
                user.getUserId(),
                "ACTIVATION_OTP_SENT",
                "Account activation OTP was sent."
        );

        return resetId;
    }

    public boolean verifyActivationOtp(
            int resetId,
            String enteredOtp
    ) {

        if (resetId <= 0
                || enteredOtp == null
                || !enteredOtp.matches("\\d{6}")) {

            return false;
        }

        PasswordResetOtp activationOtp =
                repository.findOtpById(resetId);

        if (activationOtp == null
                || activationOtp.isUsed()
                || activationOtp.isVerified()) {

            return false;
        }

        if (activationOtp.getAttemptCount()
                >= MAX_ATTEMPTS) {

            repository.markOtpUsed(resetId);
            return false;
        }

        if (activationOtp.getExpiresAt() == null
                || LocalDateTime.now()
                        .isAfter(
                                activationOtp.getExpiresAt()
                        )) {

            repository.markOtpUsed(resetId);
            return false;
        }

        boolean matches;

        try {

            matches = BCrypt.checkpw(
                    enteredOtp,
                    activationOtp.getOtpHash()
            );

        } catch (IllegalArgumentException exception) {

            repository.markOtpUsed(resetId);
            return false;
        }

        if (!matches) {

            repository.incrementAttemptCount(resetId);

            if (activationOtp.getAttemptCount() + 1
                    >= MAX_ATTEMPTS) {

                repository.markOtpUsed(resetId);
            }

            return false;
        }

        boolean verified =
                repository.markOtpVerified(resetId);

        if (verified) {

            activityLogService.logActivity(
                    activationOtp.getUserId(),
                    "ACTIVATION_OTP_VERIFIED",
                    "Account activation OTP was verified."
            );
        }

        return verified;
    }

    public boolean activateAccount(
            int resetId,
            String newPassword,
            String confirmPassword
    ) {

        if (resetId <= 0
                || newPassword == null
                || confirmPassword == null) {

            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            return false;
        }

        if (!isStrongPassword(newPassword)) {
            return false;
        }

        PasswordResetOtp activationOtp =
                repository.findOtpById(resetId);

        if (activationOtp == null
                || !activationOtp.isVerified()
                || activationOtp.isUsed()) {

            return false;
        }

        if (activationOtp.getExpiresAt() == null
                || LocalDateTime.now()
                        .isAfter(
                                activationOtp.getExpiresAt()
                        )) {

            repository.markOtpUsed(resetId);
            return false;
        }

        String passwordHash =
                BCrypt.hashpw(
                        newPassword,
                        BCrypt.gensalt(12)
                );

        boolean activated =
                repository.activateAccount(
                        resetId,
                        activationOtp.getUserId(),
                        passwordHash
                );

        if (activated) {

            activityLogService.logActivity(
                    activationOtp.getUserId(),
                    "ACCOUNT_ACTIVATED",
                    "New library account was activated successfully."
            );
        }

        return activated;
    }

    private boolean isStrongPassword(
            String password
    ) {

        if (password.length() < 8
                || password.length() > 72) {

            return false;
        }

        boolean hasUppercase =
                password.matches(".*[A-Z].*");

        boolean hasLowercase =
                password.matches(".*[a-z].*");

        boolean hasDigit =
                password.matches(".*\\d.*");

        boolean hasSpecialCharacter =
                password.matches(
                        ".*[@#$%&*!?_\\-].*"
                );

        return hasUppercase
                && hasLowercase
                && hasDigit
                && hasSpecialCharacter;
    }
}