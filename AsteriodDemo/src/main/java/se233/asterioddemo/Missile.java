package se233.asterioddemo;

import javafx.scene.canvas.GraphicsContext;

public class Missile {
    private double x, y;
    private double dx, dy;
    private double speed = 4; // Slower than Bullet but can have different properties
    private long creationTime;
    private static final long EXPLOSION_TIME = 500; // Time in milliseconds before explosion
    private static final double EXPLOSION_RADIUS = 100; // Radius of the explosion
    private MissileAnimation animation;

    public Missile(double x, double y, double dx, double dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.creationTime = System.currentTimeMillis();
        this.animation = new MissileAnimation(x, y);
    }

    public void update() {
        x += dx * speed;
        y += dy * speed;
        animation.setPosition(x, y);
        animation.update();
    }

    public void draw(GraphicsContext gc) {
        animation.draw(gc);
    }

    public boolean isOffScreen(double width, double height) {
        return x < 0 || x > width || y < 0 || y > height;
    }

    public boolean shouldExplode() {
        return System.currentTimeMillis() - creationTime >= EXPLOSION_TIME;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getExplosionRadius() {
        return EXPLOSION_RADIUS;
    }
}