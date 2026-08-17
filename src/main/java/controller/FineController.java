package controller;

import model.Fine;
import service.FineService;

import java.math.BigDecimal;
import java.util.List;

public class FineController {

    private final FineService service;

    public FineController() {
        service = new FineService();
    }

    public List<Fine> getFinesByUser(
            String userId
    ) {

        return service.getFinesByUser(
                userId
        );
    }

    public BigDecimal getOutstandingFineAmount(
            String userId
    ) {

        return service.getOutstandingFineAmount(
                userId
        );
    }

    public String validatePayment(
            int fineId,
            String memberUserId,
            String paymentMethod,
            String paidByUserId
    ) {

        return service.validatePayment(
                fineId,
                memberUserId,
                paymentMethod,
                paidByUserId
        );
    }

    public BigDecimal payFine(
            int fineId,
            String memberUserId,
            String paymentMethod,
            String paymentReference,
            String paidByUserId
    ) {

        return service.payFine(
                fineId,
                memberUserId,
                paymentMethod,
                paymentReference,
                paidByUserId
        );
    }
}