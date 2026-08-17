package util;

import javafx.stage.Stage;
import model.Role;
import model.User;
import repository.BorrowRepository;
import view.StudentDashboardView;
import view.SuspendedMemberDashboardView;
import view.TeacherDashboardView;

public final class MemberDashboardRouter {

    private MemberDashboardRouter() {
    }

    public static void openDashboard(
            Stage stage,
            User user
    ) {

        if (stage == null || user == null) {
            return;
        }

        boolean isMember =
                user.getRole() == Role.STUDENT
                        || user.getRole() == Role.TEACHER;

        if (!isMember) {
            return;
        }

        BorrowRepository borrowRepository =
                new BorrowRepository();

        boolean needsLimitedAccess =
                user.isSuspended()
                        || borrowRepository.hasOverdueActiveLoans(
                                user.getUserId()
                        );

        if (needsLimitedAccess) {

            SceneRouter.open(
                    stage,
                    new SuspendedMemberDashboardView(
                            user
                    ).createScene(),
                    "IntelliLib - Limited Account Access"
            );

            return;
        }

        if (user.getRole() == Role.TEACHER) {

            SceneRouter.open(
                    stage,
                    new TeacherDashboardView(
                            user
                    ).createScene(),
                    "IntelliLib - Teacher Dashboard"
            );

        } else {

            SceneRouter.open(
                    stage,
                    new StudentDashboardView(
                            user
                    ).createScene(),
                    "IntelliLib - Student Dashboard"
            );
        }
    }
}