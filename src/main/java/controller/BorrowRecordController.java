package controller;

import model.BorrowRecord;
import service.BorrowRecordService;

import java.util.List;

public class BorrowRecordController {

    private final BorrowRecordService service;

    public BorrowRecordController() {
        service = new BorrowRecordService();
    }

    public List<BorrowRecord> getAllBorrowRecords(
            String keyword
    ) {
        return service.getAllBorrowRecords(
                keyword
        );
    }

    public List<BorrowRecord> getBorrowRecords(
            String keyword,
            String filterMode
    ) {
        return service.getBorrowRecords(
                keyword,
                filterMode
        );
    }
}