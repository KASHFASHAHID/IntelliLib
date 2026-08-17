package controller;

import model.ReadyForPickup;
import service.ReadyForPickupService;

import java.util.List;

public class ReadyForPickupController {

    private ReadyForPickupService service;

    public ReadyForPickupController() {
        service = new ReadyForPickupService();
    }

    public List<ReadyForPickup> getReadyForPickupReservations() {
        return service.getReadyForPickupReservations();
    }

    public boolean issueReservedBook(
        int reservationId,
        String userId,
        String isbn,
        int borrowDays
) {
    return service.issueReservedBook(
            reservationId,
            userId,
            isbn,
            borrowDays
    );
}
}
