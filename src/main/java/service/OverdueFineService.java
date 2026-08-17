package service;

import model.LibrarySettings;
import model.OverdueFineNotice;
import repository.FineRepository;

import java.time.LocalDate;
import java.util.List;

public class OverdueFineService {

    private final FineRepository fineRepository;
    private final LibrarySettingsService settingsService;
    private final NotificationService notificationService;
    private final EmailService emailService;

    public OverdueFineService() {

        fineRepository =
                new FineRepository();

        settingsService =
                new LibrarySettingsService();

        notificationService =
                new NotificationService();

        emailService =
                new EmailService();
    }

    public int processOverdueFines() {

        LibrarySettings settings =
                settingsService.getSettings();

        if (settings == null
                || settings.getFinePerDay() == null
                || settings.getFinePerDay().signum() <= 0) {

            System.err.println(
                    "Overdue fines could not be processed: "
                            + "invalid fine-per-day setting."
            );

            return 0;
        }

        fineRepository.synchronizeRunningOverdueFines(
                settings.getFinePerDay()
        );

        List<OverdueFineNotice> notices =
                fineRepository
                        .getOverdueFinesNeedingNotification();

        int completedNotices = 0;

        for (OverdueFineNotice notice : notices) {

            boolean inAppNotificationCreated =
                    notificationService.createNotification(
                            notice.userId(),
                            "Overdue Book and Running Fine",
                            createNotificationMessage(
                                    notice,
                                    settings
                            )
                    );

            boolean emailSent =
                    emailService.sendOverdueFineEmail(
                            notice.memberEmail(),
                            notice.memberName(),
                            notice.bookTitle(),
                            notice.dueDate(),
                            notice.overdueDays(),
                            settings.getFinePerDay(),
                            notice.currentAmount()
                    );

            /*
             * Mark the notice as sent only when the in-app notification
             * succeeds. Email failure should not create repeated in-app
             * notifications every time the application starts.
             */
            if (!inAppNotificationCreated) {
                continue;
            }

            boolean markedAsNotified =
                    fineRepository.markFineNotificationSent(
                            notice.fineId(),
                            LocalDate.now()
                    );

            if (markedAsNotified) {

                completedNotices++;

                if (!emailSent) {

                    System.err.println(
                            "In-app overdue notice was created, "
                                    + "but email could not be sent for user: "
                                    + notice.userId()
                    );
                }
            }
        }

        return completedNotices;
    }

    private String createNotificationMessage(
            OverdueFineNotice notice,
            LibrarySettings settings
    ) {

        return "The book \""
                + notice.bookTitle()
                + "\" is overdue."
                + "\n\nDue Date: "
                + notice.dueDate()
                + "\nOverdue Days: "
                + notice.overdueDays()
                + "\nFine Per Day: ₹"
                + settings.getFinePerDay()
                + "\nCurrent Estimated Fine: ₹"
                + notice.currentAmount()
                + "\n\nThe fine will continue increasing each day "
                + "until the book is returned. After return, the final "
                + "fine amount will be frozen.";
    }
}