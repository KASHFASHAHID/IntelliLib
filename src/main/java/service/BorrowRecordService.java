package service;

import model.BorrowRecord;
import repository.BorrowRecordRepository;

import java.util.ArrayList;
import java.util.List;

public class BorrowRecordService {

    private final BorrowRecordRepository repository;

    public BorrowRecordService() {
        repository = new BorrowRecordRepository();
    }

    public List<BorrowRecord> getAllBorrowRecords(
            String keyword
    ) {
        return getBorrowRecords(
                keyword,
                "ALL"
        );
    }

    public List<BorrowRecord> getBorrowRecords(
            String keyword,
            String filterMode
    ) {

        String normalizedFilter =
                normalizeFilter(filterMode);

        List<BorrowRecord> records =
                repository.getBorrowRecords(
                        keyword,
                        normalizedFilter
                );

        if (records == null) {
            return new ArrayList<>();
        }

        return records;
    }

    private String normalizeFilter(
            String filterMode
    ) {

        if (filterMode == null) {
            return "ALL";
        }

        String normalized =
                filterMode.trim().toUpperCase();

        if ("ISSUED".equals(normalized)
                || "OVERDUE".equals(normalized)) {

            return normalized;
        }

        return "ALL";
    }
}