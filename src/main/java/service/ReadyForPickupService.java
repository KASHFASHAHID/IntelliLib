package service;

import model.ReadyForPickup;
import repository.ReadyForPickupRepository;

import java.util.List;

public class ReadyForPickupService {

    private ReadyForPickupRepository repository;

    public ReadyForPickupService() {
        repository = new ReadyForPickupRepository();
    }

    public List<ReadyForPickup> getReadyForPickupReservations() {
        return repository.getReadyForPickupReservations();
    }

    public boolean issueReservedBook(
        int reservationId,
        String userId,
        String isbn,
        int borrowDays
) {
    return repository.issueReservedBook(
            reservationId,
            userId,
            isbn,
            borrowDays
    );
}


}
