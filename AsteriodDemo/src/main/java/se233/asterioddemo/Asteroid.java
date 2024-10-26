package se233.asterioddemo;

import java.util.Random;

class Asteroid {
    private double x, y;
    private double vx, vy;
    private double size;
    private Random random = new Random();

    public Asteroid(double x, double y) {
        this.x = x;
        this.y = y;
        this.size = 20 + random.nextDouble() * 30; // Random size between 20 and 50
        this.vx = -1 + random.nextDouble() * 2; // Random velocity between -1 and 1
        this.vy = 1 + random.nextDouble() * 2; // Random velocity between 1 and 3
    }

    public void update(double width, double height) {
        x += vx;
        y += vy;

        // Wrap around logic
        if (x < 0) x = width;
        if (x > width) x = 0;
        if (y < 0) y = height;
        if (y > height) y = 0;
    }

    public boolean isOffScreen(double width, double height) {
        return x < 0 || x > width || y < 0 || y > height;
    }

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