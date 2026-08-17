package service;

import model.Profile;
import org.mindrot.jbcrypt.BCrypt;
import repository.ProfileRepository;

public class ProfileService {

    private static final String EMAIL_PATTERN =
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    private static final String PHONE_PATTERN =
            "^\\+91[6-9]\\d{9}$";

    private final ProfileRepository repository;

    public ProfileService() {
        this.repository =
                new ProfileRepository();
    }

    public Profile getProfileByUserId(
            String userId
    ) {

        if (userId == null || userId.isBlank()) {
            return null;
        }

        return repository.getProfileByUserId(
                userId.trim()
        );
    }

    public String validateContactDetails(
            String userId,
            String email,
            String phone
    ) {

        if (userId == null || userId.isBlank()) {

            return "The user account could not be identified.";
        }

        if (email == null || email.isBlank()) {

            return "Email address is required.";
        }

        String normalizedEmail =
                email.trim()
                        .toLowerCase();

        if (!normalizedEmail.matches(
                EMAIL_PATTERN
        )) {

            return "Please enter a valid email address.";
        }

        if (repository.isEmailUsedByAnotherUser(
                normalizedEmail,
                userId.trim()
        )) {

            return "This email address is already associated with another account.";
        }

        if (phone == null || phone.isBlank()) {

            return "Phone number is required.";
        }

        if (!phone.trim().matches(
                PHONE_PATTERN
        )) {

            return "Use +91 followed by exactly 10 digits. "
                    + "The mobile number must begin with 6, 7, 8, or 9.";
        }

        return null;
    }

    public boolean updateContactDetails(
            String userId,
            String email,
            String phone
    ) {

        String validationError =
                validateContactDetails(
                        userId,
                        email,
                        phone
                );

        if (validationError != null) {
            return false;
        }

        return repository.updateContactDetails(
                userId.trim(),
                email.trim().toLowerCase(),
                phone.trim()
        );
    }

    public boolean changePassword(
            String userId,
            String currentPassword,
            String newPassword
    ) {

        if (userId == null || userId.isBlank()) {
            return false;
        }

        if (currentPassword == null
                || currentPassword.isEmpty()) {

            return false;
        }

        if (newPassword == null
                || newPassword.isEmpty()) {

            return false;
        }

        if (newPassword.length() < 8
                || newPassword.length() > 72) {

            return false;
        }

        if (currentPassword.equals(newPassword)) {
            return false;
        }

        String storedPassword =
                repository.getPasswordHashByUserId(
                        userId.trim()
                );

        if (storedPassword == null
                || storedPassword.isEmpty()) {

            return false;
        }

        boolean currentPasswordMatches;

        if (isBCryptHash(storedPassword)) {

            try {

                currentPasswordMatches =
                        BCrypt.checkpw(
                                currentPassword,
                                storedPassword
                        );

            } catch (IllegalArgumentException exception) {

                return false;
            }

        } else {

            currentPasswordMatches =
                    currentPassword.equals(
                            storedPassword
                    );
        }

        if (!currentPasswordMatches) {
            return false;
        }

        String newPasswordHash =
                BCrypt.hashpw(
                        newPassword,
                        BCrypt.gensalt(12)
                );

        return repository.updatePasswordHash(
                userId.trim(),
                newPasswordHash
        );
    }

    private boolean isBCryptHash(
            String password
    ) {

        return password != null
                && password.length() == 60
                && (
                password.startsWith("$2a$")
                        || password.startsWith("$2b$")
                        || password.startsWith("$2y$")
        );
    }
}