package service;

import model.LibrarySettings;
import repository.LibrarySettingsRepository;

public class LibrarySettingsService {

    private final LibrarySettingsRepository repository;

    public LibrarySettingsService() {
        repository = new LibrarySettingsRepository();
    }

    public LibrarySettings getSettings() {
        return repository.getSettings();
    }

    public boolean saveSettings(
        LibrarySettings settings
) {

    if (settings == null) {
        return false;
    }

    return repository.saveSettings(settings);
}

}
