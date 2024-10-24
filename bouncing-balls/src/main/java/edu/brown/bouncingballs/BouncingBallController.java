package edu.brown.bouncingballs;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * BouncingBallController class manages the animation of bouncing balls
 * inside an AnchorPane in a JavaFX application.
 */
public class BouncingBallController {

    @FXML
    private AnchorPane anchorPane;  // The main pane where balls bounce
    String FILE_NAME = "src/main/resources/data/";
    @FXML
    private Label scoreLabel;  // Label to display the score

    private List<Ball> balls = new ArrayList<>(); // List to store multiple balls
    private final Random randomGenerator = new Random();
    private int score = 0;  // Variable to track the user's score
    private final String[] ballImages = {
            getClass().getResource("/images/ball1.png").toExternalForm(),
            getClass().getResource("/images/ball2.png").toExternalForm(),
            getClass().getResource("/images/ball3.png").toExternalForm(),
            getClass().getResource("/images/ball4.png").toExternalForm(),
            getClass().getResource("/images/ball5.png").toExternalForm()
    };

    private ExecutorService executorService;
    private final int NUMBER_OF_BALLS = 50;    // The number of balls
    private final int POOL_SIZE = 50;          // Thread pool size
    private BufferedWriter writer;
    private double MILLISECONDS = 1000000.0;
    /**
     * Initializes the controller. Creates balls with random images,
     * positions, and velocities and adds them to the AnchorPane.
     */
    public void initialize() {
        // Initialize the score label
        scoreLabel.setText("Score: 0");
        // Initialize the file name
        FILE_NAME = FILE_NAME + "b_" + NUMBER_OF_BALLS + "_t_" + POOL_SIZE + ".csv";
        // Initialize the logging system
        initializeLogger();

        // Initialize the ExecutorService with a fixed thread pool
        executorService = Executors.newFixedThreadPool(POOL_SIZE);

        // Initialize balls with random positions, velocities, and images
        for (int i = 0; i < NUMBER_OF_BALLS; i++) {
            Circle circle = new Circle(20);  // Create a circle with radius 20
            setRandomBallImage(circle);  // Set a random image for the ball

            circle.setLayoutX(randomGenerator.nextDouble() * (anchorPane.getPrefWidth() - 40) + 20);
            circle.setLayoutY(randomGenerator.nextDouble() * (anchorPane.getPrefHeight() - 40) + 20);
            double dx = randomGenerator.nextDouble() * 4 - 2;  // Random horizontal speed (-2 to 2)
            double dy = randomGenerator.nextDouble() * 4 - 2;  // Random vertical speed (-2 to 2)

            Ball ball = new Ball(circle, dx, dy, anchorPane.getPrefWidth(), anchorPane.getPrefHeight());
            balls.add(ball);
            anchorPane.getChildren().add(circle);

            // Add mouse click listener to the circle
            circle.setOnMouseClicked(event -> {
                anchorPane.getChildren().remove(circle);  // Remove circle from AnchorPane
                balls.remove(ball);  // Remove ball from the list
                int ballScore = calculateScore(ball);
                score += ballScore;
                scoreLabel.setText("Score: " + score);
            });
        }

        // AnimationTimer to update the position of balls at every frame
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        animationTimer.start(); // Start the animation
    }

    /**
     * Updates the positions of all the balls in the list.
     * Submits each ball's move operation to the thread pool and waits for completion.
     */
    private void update() {
        long startTime = System.nanoTime();

        // Submit move tasks for each ball and wait for all to complete
        List<Callable<Void>> moveTasks = new ArrayList<>();
        for (Ball ball : balls) {
            moveTasks.add(() -> {
                ball.move();
                return null;  // Callable must return something, so we return null
            });
        }

        try {
            // Invoke all tasks and wait for them to complete
            List<Future<Void>> results = executorService.invokeAll(moveTasks);
            for (Future<Void> result : results) {
                result.get(); // Ensure all tasks are done
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // Move the UI updates to the JavaFX thread
        Platform.runLater(() -> {
            for (Ball ball : balls) {
                Circle circle = ball.getCircle();
                circle.setLayoutX(ball.getPosX());
                circle.setLayoutY(ball.getPosY());
            }
        });

        long endTime = System.nanoTime();
        long interval = endTime - startTime;
        logFrameTime(interval / MILLISECONDS);
    }

    /**
     * Initialize the logger to record frame times.
     */
    private void initializeLogger() {

        try {
            writer = new BufferedWriter(new FileWriter(FILE_NAME, true));
            writer.write("ThreadPoolSize,NumberOfBalls,FrameTime(ms)\n");
        } catch (IOException e) {
            e.printStackTrace();
            writer = null; // If writer fails to initialize, explicitly set it to null
        }
    }

    /**
     * Logs the frame time along with the thread pool size and number of balls to the file.
     *
     * @param frameTime The time for each frame in milliseconds.
     */
    private void logFrameTime(double frameTime) {
        if (writer != null) {
            try {
                writer.write(String.format("%d,%d,%.3f\n", NUMBER_OF_BALLS, POOL_SIZE, frameTime));
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Warning: Attempted to log frame time, but writer is null.");
        }
    }


    /**
     * Sets a random ball image from the ballImages array to the given circle.
     *
     * @param circle The circle representing the ball.
     */
    private void setRandomBallImage(Circle circle) {
        String randomImagePath = ballImages[randomGenerator.nextInt(ballImages.length)];
        Image image = new Image(randomImagePath);
        circle.setFill(new ImagePattern(image));
    }

    /**
     * Calculates the score based on the ball's speed.
     * Formula: (dx + dy + 10) * 2
     *
     * @param ball The ball to calculate the score for.
     * @return The calculated score.
     */
    private int calculateScore(Ball ball) {
        return (int) ((Math.abs(ball.getDx()) + Math.abs(ball.getDy()) + 10) * 2);
    }

    /**
     * Shutdown the ExecutorService when the application stops.
     */
    public void stop() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
