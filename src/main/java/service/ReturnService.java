package service;

import repository.ReturnRepository;

public class ReturnService {

    private ReturnRepository repository;

    
    public ReturnService() {
        repository = new ReturnRepository();
    }

    public boolean returnBook(int loanId, String copyNumber) {
        return repository.returnBook(loanId, copyNumber);
    }
}