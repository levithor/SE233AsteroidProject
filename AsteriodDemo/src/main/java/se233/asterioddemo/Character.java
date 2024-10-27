package se233.asterioddemo;

import javafx.scene.canvas.GraphicsContext;

public abstract class Character {
    protected double x, y;
    protected double dx, dy;
    protected double speed;
    protected double size;

    public Character(double x, double y, double dx, double dy, double speed, double size) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.speed = speed;
        this.size = size;
    }

    public abstract void update(double width, double height, GraphicsContext gc);

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getSize() {
        return size;
    }
}