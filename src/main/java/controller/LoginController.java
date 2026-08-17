package controller;

import model.User;
import repository.BorrowRepository;
import service.ActivityLogService;
import service.AuthenticationService;

public class LoginController {

    private final AuthenticationService authenticationService;
    private final ReservationExpiryController reservationExpiryController;
    private final ActivityLogService activityLogService;
    private final BorrowRepository borrowRepository;

    public LoginController() {

        authenticationService =
                new AuthenticationService();

        reservationExpiryController =
                new ReservationExpiryController();

        activityLogService =
                new ActivityLogService();

        borrowRepository =
                new BorrowRepository();
    }

    public User handleLogin(
            String userId,
            String password
    ) {

        User user =
                authenticationService.login(
                        userId,
                        password
                );

        if (user != null) {

            int processedReservations =
                    reservationExpiryController
                            .processExpiredReservations();

            System.out.println(
                    "Expired reservations processed: "
                            + processedReservations
            );

            activityLogService.logActivity(
                    user.getUserId(),
                    "LOGIN_SUCCESS",
                    user.getRole()
                            + " logged into the system."
            );
        }

        return user;
    }

    public boolean hasOverdueActiveLoans(
            String userId
    ) {

        return borrowRepository.hasOverdueActiveLoans(
                userId
        );
    }
}