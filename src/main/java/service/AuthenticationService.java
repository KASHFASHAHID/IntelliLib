package service;

import model.User;
import org.mindrot.jbcrypt.BCrypt;
import repository.UserRepository;

public class AuthenticationService {

    private final UserRepository userRepository;

    public AuthenticationService() {
        this.userRepository = new UserRepository();
    }

    public User login(String userId, String password) {

        if (userId == null
                || userId.isBlank()
                || password == null
                || password.isEmpty()) {

            return null;
        }

        User user =
        userRepository.findLoginUserById(
                userId.trim()
        );
        

        if (user == null) {
            return null;
        }

        String storedPassword = user.getPassword();

        if (storedPassword == null
                || storedPassword.isEmpty()) {

            return null;
        }

        if (isBCryptHash(storedPassword)) {

            try {

                boolean passwordMatches =
                        BCrypt.checkpw(
                                password,
                                storedPassword
                        );

                if (passwordMatches) {
                    return user;
                }

            } catch (IllegalArgumentException e) {

                System.err.println(
                        "Invalid BCrypt password hash for user: "
                                + user.getUserId()
                );
            }

            return null;
        }

        /*
         * Temporary migration support:
         *
         * If the database still contains a plain-text password,
         * allow one successful login and immediately convert it
         * into a BCrypt password.
         */
        if (password.equals(storedPassword)) {

            String newPasswordHash =
                    BCrypt.hashpw(
                            password,
                            BCrypt.gensalt(12)
                    );

            boolean updated =
                    userRepository.updatePasswordHash(
                            user.getUserId(),
                            newPasswordHash
                    );

            if (updated) {
                user.setPassword(newPasswordHash);
            } else {
                System.err.println(
                        "Password migration failed for user: "
                                + user.getUserId()
                );
            }

            return user;
        }

        return null;
    }

    private boolean isBCryptHash(String password) {

        return password != null
                && password.length() == 60
                && (
                    password.startsWith("$2a$")
                    || password.startsWith("$2b$")
                    || password.startsWith("$2y$")
                );
    }
}