package edu.brown.bouncingballs;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * BouncingBallController class manages the animation of bouncing balls
 * inside an AnchorPane in a JavaFX application.
 *
 * Author: Junhan
 * Purpose: Manage the creation, movement, and collision handling of bouncing balls.
 */
public class BouncingBallController {

    @FXML
    private AnchorPane anchorPane;  // The main pane where balls bounce

    @FXML
    private Label scoreLabel;  // Label to display the score

    private List<Ball> balls = new ArrayList<>(); // List to store multiple balls

    // Random object to generate random values
    private final Random randomGenerator = new Random();

    private int score = 0;  // Variable to track the user's score

    // Array to hold the five ball images
    private final String[] ballImages = {
            getClass().getResource("/images/ball1.png").toExternalForm(),
            getClass().getResource("/images/ball2.png").toExternalForm(),
            getClass().getResource("/images/ball3.png").toExternalForm(),
            getClass().getResource("/images/ball4.png").toExternalForm(),
            getClass().getResource("/images/ball5.png").toExternalForm()
    };

    // ExecutorService to manage a pool of threads
    private ExecutorService executorService;

    /**
     * Initializes the controller. Creates balls with random images,
     * positions, and velocities and adds them to the AnchorPane.
     */
    public void initialize() {
        // Initialize the score label
        scoreLabel.setText("Score: 0");

        // Define the number of balls and thread pool size
        int numberOfBalls = 10;    // The number of balls
        int poolSize = 5;          // Thread pool size

        // Initialize the ExecutorService with a fixed thread pool
        executorService = Executors.newFixedThreadPool(poolSize);

        // Initialize balls with random positions, velocities, and images
        for (int i = 0; i < numberOfBalls; i++) {
            Circle circle = new Circle(20);  // Create a circle with radius 20
            setRandomBallImage(circle);  // Set a random image for the ball

            circle.setLayoutX(randomGenerator.nextDouble() * (anchorPane.getPrefWidth() - 40) + 20);
            circle.setLayoutY(randomGenerator.nextDouble() * (anchorPane.getPrefHeight() - 40) + 20);
            double dx = randomGenerator.nextDouble() * 4 - 2;  // Random horizontal speed (-2 to 2)
            double dy = randomGenerator.nextDouble() * 4 - 2;  // Random vertical speed (-2 to 2)

            // Add ball to the list
            Ball ball = new Ball(circle, dx, dy, anchorPane.getPrefWidth(), anchorPane.getPrefHeight());
            balls.add(ball);

            // Add the circle to the AnchorPane
            anchorPane.getChildren().add(circle);

            // Add mouse click listener to the circle
            circle.setOnMouseClicked(event -> {
                // Remove the ball from the AnchorPane and the list when clicked
                anchorPane.getChildren().remove(circle);  // Remove circle from AnchorPane
                balls.remove(ball);  // Remove ball from the list

                // Calculate score and update it
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
     * Submits each ball's move operation to the thread pool.
     * Handles collision detection and response.
     */
    private void update() {
        // Move all balls
        for (Ball ball : balls) {
            executorService.submit(() -> {
                // Perform complex computations and update ball's position
                ball.move();
            });
        }

        // Collision detection and response
        for (int i = 0; i < balls.size(); i++) {
            Ball ballA = balls.get(i);
            for (int j = i + 1; j < balls.size(); j++) {
                Ball ballB = balls.get(j);
                handleCollision(ballA, ballB);
            }
        }

        // Update UI
        Platform.runLater(() -> {
            for (Ball ball : balls) {
                Circle circle = ball.getCircle();
                circle.setLayoutX(ball.getPosX());
                circle.setLayoutY(ball.getPosY());
            }
        });
    }

    /**
     * Handles collision detection and response between two balls.
     *
     * @param ballA The first ball.
     * @param ballB The second ball.
     */
    private void handleCollision(Ball ballA, Ball ballB) {
        double dx = ballB.getPosX() - ballA.getPosX();
        double dy = ballB.getPosY() - ballA.getPosY();
        double distance = Math.hypot(dx, dy);
        double minDist = ballA.getRadius() + ballB.getRadius();

        if (distance < minDist) {
            // Overlap detected, adjust positions to remove overlap
            double overlap = 0.5 * (minDist - distance);

            // Normalize the distance vector
            double nx = dx / distance;
            double ny = dy / distance;

            // Adjust positions
            ballA.setPosX(ballA.getPosX() - overlap * nx);
            ballA.setPosY(ballA.getPosY() - overlap * ny);

            ballB.setPosX(ballB.getPosX() + overlap * nx);
            ballB.setPosY(ballB.getPosY() + overlap * ny);

            // Calculate relative velocity
            double vx = ballA.getDx() - ballB.getDx();
            double vy = ballA.getDy() - ballB.getDy();
            double vn = vx * nx + vy * ny;

            // If balls are moving apart, no need to adjust velocities
            if (vn > 0) {
                return;
            }

            // Calculate impulse scalar
            double restitution = 1.0; // Elastic collision
            double m1 = ballA.getMass();
            double m2 = ballB.getMass();

            double impulse = (-(1 + restitution) * vn) / (1 / m1 + 1 / m2);

            // Apply impulse to the balls
            double impulseX = impulse * nx;
            double impulseY = impulse * ny;

            ballA.setDx(ballA.getDx() + impulseX / m1);
            ballA.setDy(ballA.getDy() + impulseY / m1);

            ballB.setDx(ballB.getDx() - impulseX / m2);
            ballB.setDy(ballB.getDy() - impulseY / m2);
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
    }
}
