package controller;

import model.ActiveReservation;
import service.ActiveReservationService;

import java.util.List;

public class ActiveReservationController {

    private final ActiveReservationService service;

    public ActiveReservationController() {
        service = new ActiveReservationService();
    }

    public List<ActiveReservation> getAllActiveReservations() {
        return service.getAllActiveReservations();
    }
}