package se233.asterioddemo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javafx.scene.paint.Color;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class Boss extends Character {
    private Image image;
    private Image hitImage;
    private double angle = 0;
    private double rotationSpeed = 0; // Rotation speed in degrees per update
    private boolean descending = true; // Flag to indicate if the boss is descending
    private double initialY; // Initial Y position to track descent
    private List<Bullet> bullets = new ArrayList<>(); // List to track bullets
    private long lastBulletTime = 0; // Last time a bullet was emitted
    private long bulletInterval = 1000; // Interval between bullets in milliseconds
    private int hitCount = 0; // Counter for bullet hits

    public Boss(double x, double y, double dx, double dy) {
        super(x, y, dx, dy, 0.75, 100); // Initialize with a size of 100 and speed of 0.75
        this.image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/se233/asterioddemo/assets/bossFace.png")));
        this.hitImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/se233/asterioddemo/assets/ainsleyFace.png")));
        this.initialY = y; // Set the initial Y position
    }

    @Override
    public void update(double width, double height, GraphicsContext gc) {
        if (descending) {
            y += dy * speed;
            if (y >= initialY + 100) {
                descending = false; // Stop descending after 100 pixels
                dx = 1; // Start moving laterally
                dy = 0; // Stop vertical movement
            }
        } else {
            x += dx * speed;
            if (x < 0 || x > width - size) {
                dx = -dx; // Reverse direction upon hitting a border
            }
        }

        angle += rotationSpeed; // Update the rotation angle

        // Emit bullets at the specified interval
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBulletTime >= bulletInterval) {
            emitBullet();
            lastBulletTime = currentTime;
        }

        // Update and render bullets
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.update();
            if (bullet.isOffScreen(width, height)) {
                bulletIterator.remove();
            } else {
                gc.setFill(Color.BLUE);
                gc.fillRect(bullet.getX(), bullet.getY(), 5, 5);
            }
        }

        // Draw the boss image with rotation
        gc.save();
        gc.translate(x + size / 2, y + size / 2);
        gc.rotate(angle);
        gc.drawImage(image, -size / 2, -size / 2, size, size);
        gc.restore();
    }

    private void emitBullet() {
        double bulletX = x + size / 2;
        double bulletY = y + size;
        bullets.add(new Bullet(bulletX, bulletY, 0, 1)); // Bullet travels downward
    }

    public boolean isOffScreen(double width, double height) {
        return x < -size || x > width + size || y < -size || y > height + size;
    }

    public void incrementHitCount() {
        hitCount++;
    }

    public int getHitCount() {
        return hitCount;
    }

    public void changeImageTemporarily() {
        Image originalImage = this.image;
        this.image = this.hitImage;

        PauseTransition pause = new PauseTransition(Duration.seconds(0.25));
        pause.setOnFinished(event -> this.image = originalImage);
        pause.play();
    }
}