package edu.brown.bouncingballs;

import javafx.scene.shape.Circle;

/**
 * class to represent a Ball with position, velocity, and the circle shape
 *
 * @author Junhan
 */

public class Ball {
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
