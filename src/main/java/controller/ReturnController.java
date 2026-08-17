package controller;

import service.ReturnService;

public class ReturnController {

    private ReturnService service;

    public ReturnController() {
        service = new ReturnService();
    }

    public boolean returnBook(int loanId, String copyNumber) {
        return service.returnBook(loanId, copyNumber);
    }
}