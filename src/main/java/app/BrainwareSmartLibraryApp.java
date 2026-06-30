package app;

import javafx.application.Application;
import javafx.stage.Stage;
import view.LoginView;

public class BrainwareSmartLibraryApp extends Application {

    @Override
    public void start(Stage stage) {

        LoginView loginView = new LoginView();

        stage.setTitle("Brainware Smart Library");
        stage.setScene(loginView.createScene());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}