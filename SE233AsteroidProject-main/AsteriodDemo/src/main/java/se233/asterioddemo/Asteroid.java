package se233.asterioddemo;

class Asteroid {
    private double x, y;
    private double dx, dy;
    private double speed = 4;
    private double size = 30;

    public Asteroid(double x, double y, double dx, double dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    public void update(double width, double height) {
        x += dx * speed;
        y += dy * speed;

        // Wrap asteroid location to the opposite side of the window
        if (x < -size) x = width + size;
        if (x > width + size) x = -size;
        if (y < -size) y = height + size;
        if (y > height + size) y = -size;
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