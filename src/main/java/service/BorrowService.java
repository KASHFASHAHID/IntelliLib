package service;

import model.LibrarySettings;
import model.Role;
import model.User;
import repository.BorrowRepository;
import repository.FineRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BorrowService {

    private final BorrowRepository repository;
private final FineRepository fineRepository;
private final NotificationService notificationService;
private final LibrarySettingsService settingsService;

    public BorrowService() {

    repository =
            new BorrowRepository();

    fineRepository =
            new FineRepository();

    notificationService =
            new NotificationService();

    settingsService =
            new LibrarySettingsService();
}

    public boolean borrowBook(
            User user,
            String isbn
    ) {

        if (user == null
                || isbn == null
                || isbn.isBlank()) {

            return false;
        }

        /*
         * Only students and teachers may borrow books.
         */
        if (user.getRole() != Role.STUDENT
                && user.getRole() != Role.TEACHER) {

            return false;
        }

        /*
         * Suspended accounts cannot borrow new books.
         */
        if (user.isSuspended()) {


            return false;
        }

        /*
         * A member with any unreturned overdue loan cannot
         * borrow another book, even if they reached this screen
         * through an incorrect navigation route.
         */
        if (repository.hasOverdueActiveLoans(
                user.getUserId()
        )) {


            return false;
        }

        /*
 * Members with an unpaid pending fine cannot
 * borrow another book until the fine is paid.
 */
if (fineRepository
        .getOutstandingFineAmount(
                user.getUserId()
        )
        .compareTo(java.math.BigDecimal.ZERO) > 0) {

    return false;
}

        LibrarySettings settings =
                settingsService.getSettings();

        if (settings == null) {
            return false;
        }

        int borrowDays;

        if (user.getRole() == Role.STUDENT) {

            /*
             * A student cannot borrow the same title twice.
             */
            if (repository.hasBorrowedBook(
                    user.getUserId(),
                    isbn.trim()
            )) {

                return false;
            }

            int activeBooks =
                    repository.getActiveBorrowCount(
                            user.getUserId()
                    );

            if (activeBooks
                    >= settings.getStudentMaxBooks()) {

                return false;
            }

            borrowDays =
                    settings.isExamMode()
                            ? settings.getStudentExamDays()
                            : settings.getStudentNormalDays();

        } else {

            int activeBooks =
                    repository.getActiveBorrowCount(
                            user.getUserId()
                    );

            if (activeBooks
                    >= settings.getTeacherMaxBooks()) {

                return false;
            }

            borrowDays =
                    settings.getTeacherDays();
        }

        if (borrowDays <= 0) {
            return false;
        }

        boolean borrowed =
                repository.borrowBook(
                        user.getUserId(),
                        isbn.trim(),
                        borrowDays
                );

        if (borrowed) {

            LocalDate dueDate =
                    LocalDate.now()
                            .plusDays(borrowDays);

            createBorrowNotification(
                    user,
                    dueDate,
                    1
            );
        }

        return borrowed;
    }

    public boolean borrowBooks(
            User user,
            String isbn,
            int quantity
    ) {

        if (user == null
                || isbn == null
                || isbn.isBlank()
                || quantity <= 0) {

            return false;
        }

        /*
         * Multiple-copy borrowing is available only to teachers.
         */
        if (user.getRole() != Role.TEACHER) {
            return false;
        }

        if (user.isSuspended()) {
             return false;
        }

        if (repository.hasOverdueActiveLoans(
                user.getUserId()
        )) {


            return false;
        }

        if (fineRepository.hasPendingFine(
        user.getUserId()
)) {

    return false;
}

if (fineRepository.hasPendingFine(
        user.getUserId()
)) {

    return false;
}

        LibrarySettings settings =
                settingsService.getSettings();

        if (settings == null) {
            return false;
        }

        int activeBooks =
                repository.getActiveBorrowCount(
                        user.getUserId()
                );

        if (activeBooks + quantity
                > settings.getTeacherMaxBooks()) {

            return false;
        }

        int borrowDays =
                settings.getTeacherDays();

        if (borrowDays <= 0) {
            return false;
        }

        boolean borrowed =
                repository.borrowBooks(
                        user.getUserId(),
                        isbn.trim(),
                        borrowDays,
                        quantity
                );

        if (borrowed) {

            LocalDate dueDate =
                    LocalDate.now()
                            .plusDays(borrowDays);

            createBorrowNotification(
                    user,
                    dueDate,
                    quantity
            );
        }

        return borrowed;
    }

    private void createBorrowNotification(
            User user,
            LocalDate dueDate,
            int quantity
    ) {

        String formattedDueDate =
                dueDate.format(
                        DateTimeFormatter.ofPattern(
                                "dd MMMM yyyy"
                        )
                );

        String title;
        String message;

        if (quantity == 1) {

            title =
                    "Book Borrowed Successfully";

            message =
                    "Dear "
                            + user.getName()
                            + ",\n\n"
                            + "Your selected book has been issued "
                            + "successfully. Please return it on or before "
                            + formattedDueDate
                            + " to avoid overdue charges.\n\n"
                            + "Regards,\n"
                            + "IntelliLib Team";

        } else {

            title =
                    "Books Borrowed Successfully";

            message =
                    "Dear "
                            + user.getName()
                            + ",\n\n"
                            + quantity
                            + " copies have been issued successfully.\n\n"
                            + "Please return them on or before "
                            + formattedDueDate
                            + ".\n\n"
                            + "Regards,\n"
                            + "IntelliLib Team";
        }

        notificationService.createNotification(
                user.getUserId(),
                title,
                message
        );
    }
}