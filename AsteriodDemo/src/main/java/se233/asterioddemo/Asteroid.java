package se233.asterioddemo;

import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Rotate;
import java.util.List;
import java.util.Objects;

class Asteroid extends Character {
    public enum Size {
        SMALL(30, 1.25), MEDIUM(45, 0.75), LARGE(65, 0.25);

        private final double size;
        private final double speed;

        Size(double size, double speed) {
            this.size = size;
            this.speed = speed;
        }

        public double getSize() {
            return size;
        }

        public double getSpeed() {
            return speed;
        }

        public Size getNextSize() {
            switch (this) {
                case LARGE: return MEDIUM;
                case MEDIUM: return SMALL;
                default: return null;
            }
        }
    }

    public Size getAsteroidSize() {
        return asteroidSize;
    }

    private double angle = 0;
    private double rotationSpeed = 2; // Rotation speed in degrees per update
    private Image image;
    private Size asteroidSize;

    public Asteroid(double x, double y, double dx, double dy, Size size) {
        super(x, y, dx, dy, size.getSpeed(), size.getSize());
        this.asteroidSize = size;
        this.image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/se233/asterioddemo/assets/asteroidA.png")));
    }

    @Override
    public void update(double width, double height, GraphicsContext gc) {
        x += dx * speed;
        y += dy * speed;
        angle += rotationSpeed; // Update the rotation angle

        // Wrap asteroid location to the opposite side of the window
        if (x < -size) x = width + size;
        if (x > width + size) x = -size;
        if (y < -size) y = height + size;
        if (y > height + size) y = -size;

        // Draw the asteroid image with rotation
        gc.save();
        Rotate r = new Rotate(angle, x + size / 2, y + size / 2);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        gc.drawImage(image, x, y, size, size);
        gc.restore();
    }

    public boolean isOffScreen(double width, double height) {
        return x < -size || x > width + size || y < -size || y > height + size;
    }

    public void duplicate(List<Asteroid> asteroids) {
        Size nextSize = asteroidSize.getNextSize();
        if (nextSize != null) {
            asteroids.add(new Asteroid(x, y, dx, dy, nextSize));
            asteroids.add(new Asteroid(x, y, -dx, -dy, nextSize));
        }
    }
}