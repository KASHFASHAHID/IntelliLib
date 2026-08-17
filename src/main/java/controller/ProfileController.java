package controller;

import model.Profile;
import service.ProfileService;

public class ProfileController {

    private final ProfileService service;

    public ProfileController() {
        this.service =
                new ProfileService();
    }

    public Profile getProfileByUserId(
            String userId
    ) {

        return service.getProfileByUserId(
                userId
        );
    }

    public String validateContactDetails(
            String userId,
            String email,
            String phone
    ) {

        return service.validateContactDetails(
                userId,
                email,
                phone
        );
    }

    public boolean updateContactDetails(
            String userId,
            String email,
            String phone
    ) {

        return service.updateContactDetails(
                userId,
                email,
                phone
        );
    }

    public boolean changePassword(
            String userId,
            String currentPassword,
            String newPassword
    ) {

        return service.changePassword(
                userId,
                currentPassword,
                newPassword
        );
    }
}