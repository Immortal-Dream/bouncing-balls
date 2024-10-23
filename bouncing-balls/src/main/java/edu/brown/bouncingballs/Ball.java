package edu.brown.bouncingballs;

import javafx.scene.shape.Circle;

/**
 * Class to represent a Ball with position, velocity, and the circle shape.
 * The Ball class holds information about the ball's position and speed
 * (dx and dy), which are updated in separate threads using a thread pool.
 *
 * Author: Junhan
 */
public class Ball {
    private Circle circle;  // The visual representation of the ball (a Circle)
    private double dx;      // Horizontal speed
    private double dy;      // Vertical speed
    private double posX;    // Current X position
    private double posY;    // Current Y position
    private double paneWidth;   // Width of the AnchorPane
    private double paneHeight;  // Height of the AnchorPane

    public Ball(Circle circle, double dx, double dy, double paneWidth, double paneHeight) {
        this.circle = circle;
        this.dx = dx;
        this.dy = dy;
        this.posX = circle.getLayoutX();
        this.posY = circle.getLayoutY();
        this.paneWidth = paneWidth;
        this.paneHeight = paneHeight;
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

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    /**
     * Simulates complex computations and updates the position of the ball.
     * This method is called within separate threads managed by a thread pool.
     */
    public void move() {
        // Simulate complex computations (e.g., physics calculations)
        for (int i = 0; i < 100000; i++) {
            Math.sin(i);
        }

        // Update ball position
        posX += dx;
        posY += dy;

        // Check for collision with the bounds of the AnchorPane and reverse direction if necessary
        if (posX <= circle.getRadius() || posX >= paneWidth - circle.getRadius()) {
            dx = -dx; // Reverse horizontal direction
        }
        if (posY <= circle.getRadius() || posY >= paneHeight - circle.getRadius()) {
            dy = -dy; // Reverse vertical direction
        }
    }
}
