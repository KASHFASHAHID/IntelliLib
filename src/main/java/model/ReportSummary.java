package model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ReportSummary {

    private final SimpleStringProperty title;
    private final SimpleIntegerProperty value;

    public ReportSummary(
            String title,
            int value
    ) {
        this.title = new SimpleStringProperty(title);
        this.value = new SimpleIntegerProperty(value);
    }

    public String getTitle() {
        return title.get();
    }

    public int getValue() {
        return value.get();
    }
}