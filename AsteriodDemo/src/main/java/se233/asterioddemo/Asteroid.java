package se233.asterioddemo;

class Asteroid {
    private double x, y;
    private double dx, dy;
    private double speed = 5;
    private double size = 30;

    public Asteroid(double x, double y, double dx, double dy) {
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
        return x < -size || x > width + size || y < -size || y > height + size;
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