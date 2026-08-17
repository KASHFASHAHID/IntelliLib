package service;

import model.FineRecord;
import repository.FineRecordRepository;

import java.util.List;

public class FineRecordService {

    private final FineRecordRepository repository;

    public FineRecordService() {
        repository = new FineRecordRepository();
    }

    public List<FineRecord> getAllFineRecords(String keyword) {
        return repository.getAllFineRecords(keyword);
    }
}