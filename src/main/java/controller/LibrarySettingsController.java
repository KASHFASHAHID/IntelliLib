package controller;

import model.LibrarySettings;
import service.LibrarySettingsService;

public class LibrarySettingsController {

    private final LibrarySettingsService service;

    public LibrarySettingsController() {
        service = new LibrarySettingsService();
    }

    public LibrarySettings getSettings() {
        return service.getSettings();
    }

    public boolean saveSettings(
        LibrarySettings settings
) {
    return service.saveSettings(settings);
}
}