package edu.brown.bouncingballs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Reference
 * images source: https://msrana.itch.io/
 */
public class BouncingBallApp extends Application {
    /**
     * Height of the Screen
     */
    private static final int HEIGHT = 600;

    /**
     * Screen's width
     */
    private static final int WIDTH = 900;

    /**
     * Start function for the bouncing game
     *
     * @param stage The primary stage for this application.
     * @throws Exception if an error occurs during loading the FXML.
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(BouncingBallApp.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        stage.setTitle("Bouncing Balls");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Stops the application and shuts down any background threads.
     */
    @Override
    public void stop() throws Exception {
        super.stop();
        // Get the controller to shut down the ExecutorService
        BouncingBallController controller = new FXMLLoader(BouncingBallApp.class.getResource("main-view.fxml")).getController();
        if (controller != null) {
            controller.stop();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
