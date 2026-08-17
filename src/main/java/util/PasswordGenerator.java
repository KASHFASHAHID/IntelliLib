package util;

import java.security.SecureRandom;

public class PasswordGenerator {

    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz" +
            "0123456789" +
            "@#$%&*!";

    private static final SecureRandom random =
            new SecureRandom();

    public static String generatePassword() {

        StringBuilder password =
                new StringBuilder();

        for (int i = 0; i < 10; i++) {

            password.append(
                    CHARACTERS.charAt(
                            random.nextInt(
                                    CHARACTERS.length()
                            )
                    )
            );
        }

        return password.toString();
    }
}