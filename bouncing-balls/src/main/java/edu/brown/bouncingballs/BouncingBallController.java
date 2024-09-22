package edu.brown.bouncingballs;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    /**
     * Initializes the controller. Creates 10 balls with random colors,
     * positions, and velocities and adds them to the AnchorPane.
     */
    public void initialize() {
        // Initialize the score label
        scoreLabel.setText("Score: 0");

        // Initialize 10 balls with random positions and velocities
        for (int i = 0; i < 10; i++) {
            Circle circle = new Circle(20, getRandomColor());  // Create a circle with radius 20 and random color
            circle.setLayoutX(randomGenerator.nextDouble() * anchorPane.getPrefWidth());
            circle.setLayoutY(randomGenerator.nextDouble() * anchorPane.getPrefHeight());
            double dx = randomGenerator.nextDouble() * 4 - 2;  // Random horizontal speed (-2 to 2)
            double dy = randomGenerator.nextDouble() * 4 - 2;  // Random vertical speed (-2 to 2)

            // Add ball to the list
            Ball ball = new Ball(circle, dx, dy);
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
     * Updates the positions of all the balls in the ballList.
     * Checks for collisions with the AnchorPane's boundaries and
     * reverses the direction of the balls if they hit the boundary.
     */
    private void update() {
        for (Ball ball : balls) {
            Circle circle = ball.getCircle();

            // Update ball position
            circle.setLayoutX(circle.getLayoutX() + ball.getDx());
            circle.setLayoutY(circle.getLayoutY() + ball.getDy());

            // Check for collision with the bounds of the AnchorPane and reverse direction if necessary
            if (circle.getLayoutX() <= circle.getRadius() || circle.getLayoutX() >= anchorPane.getPrefWidth() - circle.getRadius()) {
                ball.setDx(-ball.getDx()); // Reverse horizontal direction
            }
            if (circle.getLayoutY() <= circle.getRadius() || circle.getLayoutY() >= anchorPane.getPrefHeight() - circle.getRadius()) {
                ball.setDy(-ball.getDy()); // Reverse vertical direction
            }
        }
    }

    /**
     * Generates a random color for a ball.
     * @return A randomly generated Color object.
     */
    private Color getRandomColor() {
        return Color.rgb(randomGenerator.nextInt(256), randomGenerator.nextInt(256), randomGenerator.nextInt(256));
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
}
