package util;

import java.util.prefs.Preferences;

public final class RememberMeManager {

    private static final Preferences PREFERENCES =
            Preferences.userNodeForPackage(
                    RememberMeManager.class
            );

    private static final String REMEMBER_ME_KEY =
            "remember_me";

    private static final String USER_ID_KEY =
            "remembered_user_id";

    private RememberMeManager() {
    }

    public static void save(
            String userId
    ) {

        if (userId == null || userId.isBlank()) {
            clear();
            return;
        }

        PREFERENCES.putBoolean(
                REMEMBER_ME_KEY,
                true
        );

        PREFERENCES.put(
                USER_ID_KEY,
                userId.trim()
        );
    }

    public static boolean isRemembered() {

        return PREFERENCES.getBoolean(
                REMEMBER_ME_KEY,
                false
        );
    }

    public static String getRememberedUserId() {

        return PREFERENCES.get(
                USER_ID_KEY,
                ""
        );
    }

    public static void clear() {

        PREFERENCES.putBoolean(
                REMEMBER_ME_KEY,
                false
        );

        PREFERENCES.remove(
                USER_ID_KEY
        );
    }
}