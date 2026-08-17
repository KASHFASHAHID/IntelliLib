package controller;

import service.ReservationExpiryService;

public class ReservationExpiryController {

    private final ReservationExpiryService service;

    public ReservationExpiryController() {
        service = new ReservationExpiryService();
    }

    public int processExpiredReservations() {
        return service.processExpiredReservations();
    }
}
