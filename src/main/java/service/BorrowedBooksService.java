package service;

import model.BorrowedBook;
import model.LibrarySettings;
import model.Role;
import model.User;
import repository.BorrowedBooksRepository;
import repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

public class BorrowedBooksService {

    private final BorrowedBooksRepository repository;
    private final UserRepository userRepository;
    private final LibrarySettingsService settingsService;
    private final NotificationService notificationService;
    private final ActivityLogService activityLogService;

    public BorrowedBooksService() {

        repository =
                new BorrowedBooksRepository();

        userRepository =
                new UserRepository();

        settingsService =
                new LibrarySettingsService();

        notificationService =
                new NotificationService();

        activityLogService =
                new ActivityLogService();
    }

    public List<BorrowedBook> getBorrowedBooks(
            String userId
    ) {

        if (userId == null || userId.isBlank()) {
            return List.of();
        }

        List<BorrowedBook> borrowedBooks =
                repository.getBorrowedBooks(
                        userId.trim()
                );

        return borrowedBooks == null
                ? List.of()
                : borrowedBooks;
    }

    public String getRenewalBlockReason(
            int loanId,
            String userId
    ) {

        if (loanId <= 0
                || userId == null
                || userId.isBlank()) {

            return "Invalid loan information.";
        }

        User user =
                userRepository.findActiveUserById(
                        userId.trim()
                );

        if (user == null) {

            return "Your account is not active. "
                    + "Please contact the library administration.";
        }

        if (user.getRole() != Role.STUDENT
                && user.getRole() != Role.TEACHER) {

            return "Only student and teacher loans can be renewed "
                    + "from this screen.";
        }

        return repository.getRenewalBlockReason(
                loanId,
                user.getUserId()
        );
    }

    public LocalDate renewLoan(
            int loanId,
            String userId
    ) {

        String blockReason =
                getRenewalBlockReason(
                        loanId,
                        userId
                );

        if (blockReason != null) {
            return null;
        }

        User user =
                userRepository.findActiveUserById(
                        userId.trim()
                );

        if (user == null) {
            return null;
        }

        LibrarySettings settings =
                settingsService.getSettings();

        if (settings == null) {
            return null;
        }

        int renewalDays =
                getRenewalDays(
                        user.getRole(),
                        settings
                );

        if (renewalDays <= 0) {
            return null;
        }

        LocalDate newDueDate =
                repository.renewLoan(
                        loanId,
                        user.getUserId(),
                        renewalDays
                );

        if (newDueDate == null) {
            return null;
        }

        notificationService.createNotification(
                user.getUserId(),
                "Book Loan Renewed",
                "Your book loan has been renewed successfully."
                        + "\n\nLoan ID: "
                        + loanId
                        + "\nNew Due Date: "
                        + newDueDate
                        + "\n\nThis loan has now reached its "
                        + "maximum renewal limit."
        );

        activityLogService.logActivity(
                user.getUserId(),
                "LOAN_RENEWED",
                "Loan "
                        + loanId
                        + " was renewed until "
                        + newDueDate
                        + "."
        );

        return newDueDate;
    }

    private int getRenewalDays(
            Role role,
            LibrarySettings settings
    ) {

        if (role == Role.TEACHER) {
            return settings.getTeacherDays();
        }

        if (role == Role.STUDENT) {

            return settings.isExamMode()
                    ? settings.getStudentExamDays()
                    : settings.getStudentNormalDays();
        }

        return 0;
    }
}