package service;

import model.PasswordResetOtp;
import model.User;
import org.mindrot.jbcrypt.BCrypt;
import repository.PasswordResetRepository;
import util.OtpGenerator;

import java.time.LocalDateTime;

public class PasswordResetService {

    private static final int OTP_VALIDITY_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 3;

    private final PasswordResetRepository repository;
    private final EmailService emailService;

    public PasswordResetService() {

        repository = new PasswordResetRepository();
        emailService = new EmailService();
    }

    public int sendOtp(
            String userId,
            String email
    ) {

        if (userId == null || userId.isBlank()
                || email == null || email.isBlank()) {

            return -1;
        }

        User user = repository.findActiveUser(
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

        boolean sent =
                emailService.sendPasswordResetOtp(
                        user.getEmail(),
                        user.getName(),
                        otp
                );

        if (!sent) {

            repository.deleteOtp(resetId);
            return -1;
        }

        return resetId;
    }

    public boolean verifyOtp(
            int resetId,
            String enteredOtp
    ) {

        if (resetId <= 0
                || enteredOtp == null
                || !enteredOtp.matches("\\d{6}")) {

            return false;
        }

        PasswordResetOtp resetOtp =
                repository.findOtpById(resetId);

        if (resetOtp == null || resetOtp.isUsed()) {
            return false;
        }

        if (resetOtp.getAttemptCount() >= MAX_ATTEMPTS) {

            repository.markOtpUsed(resetId);
            return false;
        }

        if (LocalDateTime.now()
                .isAfter(resetOtp.getExpiresAt())) {

            repository.markOtpUsed(resetId);
            return false;
        }

        boolean matches;

        try {

            matches = BCrypt.checkpw(
                    enteredOtp,
                    resetOtp.getOtpHash()
            );

        } catch (IllegalArgumentException exception) {

            repository.markOtpUsed(resetId);
            return false;
        }

        if (!matches) {

            repository.incrementAttemptCount(resetId);

            if (resetOtp.getAttemptCount() + 1
                    >= MAX_ATTEMPTS) {

                repository.markOtpUsed(resetId);
            }

            return false;
        }

        return repository.markOtpVerified(resetId);
    }

    public boolean resetPassword(
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

        if (newPassword.length() < 8
                || newPassword.length() > 72) {

            return false;
        }

        PasswordResetOtp resetOtp =
                repository.findOtpById(resetId);

        if (resetOtp == null
        || !resetOtp.isVerified()
        || resetOtp.isUsed()) {

    return false;
}

        if (LocalDateTime.now()
                .isAfter(resetOtp.getExpiresAt())) {

            repository.markOtpUsed(resetId);
            return false;
        }

        String newPasswordHash =
                BCrypt.hashpw(
                        newPassword,
                        BCrypt.gensalt(12)
                );

        return repository.resetPassword(
                resetId,
                resetOtp.getUserId(),
                newPasswordHash
        );
    }
}