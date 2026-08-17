package service;

import model.DashboardStatistics;
import repository.DashboardRepository;

public class DashboardService {

    private final DashboardRepository repository;

    public DashboardService() {
        repository = new DashboardRepository();
    }

    public DashboardStatistics getStatistics() {

        DashboardStatistics statistics =
                repository.getStatistics();

        if (statistics == null) {
            return new DashboardStatistics();
        }

        return statistics;
    }
}