package se233.asterioddemo;

public class Bullet {
    private double x, y;
    private double dx, dy;
    private double speed = 10;

    public Bullet(double x, double y, double dx, double dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    public void update() {
        x += dx * speed;
        y += dy * speed;
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
}