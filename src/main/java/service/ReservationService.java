package service;

import model.Reservation;
import model.Role;
import model.User;
import repository.BorrowRepository;
import repository.ReservationRepository;
import repository.UserRepository;

import java.util.List;

public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BorrowRepository borrowRepository;
    private final UserRepository userRepository;

    public ReservationService() {

        this.reservationRepository =
                new ReservationRepository();

        this.borrowRepository =
                new BorrowRepository();

        this.userRepository =
                new UserRepository();
    }

    public boolean reserveBook(
            String userId,
            String isbn
    ) {

        if (userId == null
                || userId.isBlank()
                || isbn == null
                || isbn.isBlank()) {

            return false;
        }

        User user =
                userRepository.findLoginUserById(
                        userId
                );

        if (user == null) {
            return false;
        }

        boolean isMember =
                user.getRole() == Role.STUDENT
                        || user.getRole() == Role.TEACHER;

        if (!isMember) {
            return false;
        }

        /*
         * Suspended members cannot create reservations.
         */
        if (user.isSuspended()) {
            return false;
        }

        /*
         * Members with active overdue loans cannot
         * create new reservations.
         */
        if (borrowRepository.hasOverdueActiveLoans(
                userId
        )) {

            return false;
        }

        /*
         * A member cannot reserve a title that they
         * have already borrowed.
         */
        if (borrowRepository.hasBorrowedBook(
                userId,
                isbn
        )) {

            return false;
        }

        /*
         * Prevent duplicate active reservations.
         */
        if (reservationRepository.hasReservedBook(
                userId,
                isbn
        )) {

            return false;
        }

        return reservationRepository.reserveBook(
                userId,
                isbn
        );
    }

    public List<Reservation> getReservationsByUser(
            String userId
    ) {

        if (userId == null || userId.isBlank()) {
            return List.of();
        }

        return reservationRepository
                .getReservationsByUser(userId);
    }
}