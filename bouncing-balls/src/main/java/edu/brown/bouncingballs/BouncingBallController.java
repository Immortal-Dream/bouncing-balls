package edu.brown.bouncingballs;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
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
    private AnchorPane anchorPane;

    private List<Ball> balls = new ArrayList<>(); // List to store multiple balls
    private Random random = new Random();

    /**
     * Initializes the controller. Creates 10 balls with random colors,
     * positions, and velocities and adds them to the AnchorPane.
     */
    public void initialize() {
        for (int i = 0; i < 10; i++) {
            Circle circle = new Circle(20, getRandomColor()); // Create a circle with radius 20 and random color
            circle.setLayoutX(random.nextDouble() * anchorPane.getPrefWidth());
            circle.setLayoutY(random.nextDouble() * anchorPane.getPrefHeight());
            double dx = random.nextDouble() * 4 - 2; // Random horizontal speed (-2 to 2)
            double dy = random.nextDouble() * 4 - 2; // Random vertical speed (-2 to 2)
            balls.add(new Ball(circle, dx, dy)); // Add ball to list
            anchorPane.getChildren().add(circle); // Add circle to the AnchorPane
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
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    // Inner class to represent a Ball with position, velocity, and the circle shape
    public static class Ball {
        private Circle circle;
        private double dx; // Horizontal speed
        private double dy; // Vertical speed

        public Ball(Circle circle, double dx, double dy) {
            this.circle = circle;
            this.dx = dx;
            this.dy = dy;
        }

        public Circle getCircle() {
            return circle;
        }

        public double getDx() {
            return dx;
        }

        public void setDx(double dx) {
            this.dx = dx;
        }

        public double getDy() {
            return dy;
        }

        public void setDy(double dy) {
            this.dy = dy;
        }
    }
}
