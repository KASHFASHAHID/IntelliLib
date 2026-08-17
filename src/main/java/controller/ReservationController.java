package controller;

import service.ReservationService;
import model.Reservation;
import java.util.List;


public class ReservationController {

    private ReservationService service;

    public ReservationController() {
        service = new ReservationService();
    }

    public boolean reserveBook(String userId, String isbn) {
        return service.reserveBook(userId, isbn);
    }

    public List<Reservation> getReservationsByUser(String userId) {
    return service.getReservationsByUser(userId);
}
}