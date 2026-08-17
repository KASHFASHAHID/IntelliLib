package util;

import javafx.scene.Scene;
import javafx.stage.Stage;

public final class SceneRouter {

    private static final double MIN_WIDTH = 1200;
    private static final double MIN_HEIGHT = 760;

    private SceneRouter() {
    }

    public static void open(
            Stage stage,
            Scene scene,
            String title
    ) {

        if (stage == null || scene == null) {
            return;
        }

        double currentWidth =
                Math.max(
                        stage.getWidth(),
                        MIN_WIDTH
                );

        double currentHeight =
                Math.max(
                        stage.getHeight(),
                        MIN_HEIGHT
                );

        boolean wasMaximized =
                stage.isMaximized();

        stage.setScene(scene);
        stage.setTitle(title);

        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);

        if (wasMaximized) {

            stage.setMaximized(true);

        } else {

            stage.setWidth(currentWidth);
            stage.setHeight(currentHeight);
        }
    }
}