package app;

import javafx.application.Application;
import javafx.stage.Stage;
import service.OverdueFineService;
import service.OverdueLoanService;
import service.ReservationExpiryService;
import util.SceneRouter;
import view.LoginView;

public class IntelliLibApp extends Application {

    @Override
    public void start(Stage stage) {

        ReservationExpiryService expiryService =
                new ReservationExpiryService();

        int processedReservations =
                expiryService.processExpiredReservations();

        System.out.println(
                "Expired reservations processed: "
                        + processedReservations
        );

        OverdueLoanService overdueLoanService =
                new OverdueLoanService();

        int overdueLoansUpdated =
                overdueLoanService.updateAllOverdueLoans();

        System.out.println(
                "Overdue loans updated: "
                        + overdueLoansUpdated
        );

        OverdueFineService overdueFineService =
                new OverdueFineService();

        int overdueNotificationsSent =
                overdueFineService.processOverdueFines();

        System.out.println(
                "Overdue fine notifications sent: "
                        + overdueNotificationsSent
        );

        LoginView loginView =
                new LoginView();

        stage.setTitle(
        "IntelliLib"
);

stage.setMinWidth(1200);
stage.setMinHeight(760);

SceneRouter.open(
        stage,
        loginView.createScene(),
        "IntelliLib"
);

stage.show();
stage.setMaximized(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}