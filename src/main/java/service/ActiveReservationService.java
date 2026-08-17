package service;

import model.ActiveReservation;
import repository.ActiveReservationRepository;

import java.util.ArrayList;
import java.util.List;

public class ActiveReservationService {

    private final ActiveReservationRepository repository;

    public ActiveReservationService() {
        repository = new ActiveReservationRepository();
    }

    public List<ActiveReservation> getAllActiveReservations() {

        List<ActiveReservation> reservations =
                repository.findAllActiveReservations();

        if (reservations == null) {
            return new ArrayList<>();
        }

        return reservations;
    }
}