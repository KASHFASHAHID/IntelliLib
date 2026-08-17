package controller;

import model.DashboardStatistics;
import service.DashboardService;

public class DashboardController {

    private final DashboardService service;

    public DashboardController() {
        service = new DashboardService();
    }

    public DashboardStatistics getStatistics() {
        return service.getStatistics();
    }
}