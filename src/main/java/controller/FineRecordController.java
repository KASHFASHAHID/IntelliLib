package controller;

import model.FineRecord;
import service.FineRecordService;
import service.FineService;

import java.math.BigDecimal;
import java.util.List;

public class FineRecordController {

    private final FineRecordService fineRecordService;
    private final FineService fineService;

    public FineRecordController() {

        fineRecordService =
                new FineRecordService();

        fineService =
                new FineService();
    }

    public List<FineRecord> getAllFineRecords(
            String keyword
    ) {

        return fineRecordService.getAllFineRecords(
                keyword
        );
    }

    public String validatePayment(
            int fineId,
            String memberUserId,
            String paymentMethod,
            String paidByUserId
    ) {

        return fineService.validatePayment(
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

        return fineService.payFine(
                fineId,
                memberUserId,
                paymentMethod,
                paymentReference,
                paidByUserId
        );
    }

    public BigDecimal getOutstandingFineAmount(
            String memberUserId
    ) {

        return fineService.getOutstandingFineAmount(
                memberUserId
        );
    }
}