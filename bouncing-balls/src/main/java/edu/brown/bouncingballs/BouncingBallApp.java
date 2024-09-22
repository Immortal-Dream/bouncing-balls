package edu.brown.bouncingballs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BouncingBallApp extends Application {
    /**
     * Height of the Screen
     */
    private static final int HEIGHT = 550;

    /**
     * Screen's width
     */
    private static final int WIDTH = 850;
    /**
     * Start function for the bouncing game
     *
     * @param stage
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BouncingBallApp.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        stage.setTitle("Bouncing Balls");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}