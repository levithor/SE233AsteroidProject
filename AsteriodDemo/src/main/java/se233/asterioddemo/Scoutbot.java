package se233.asterioddemo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;

public class Scoutbot extends Character {
    private Image image;
    private double angle = 0;
    private final double rotationSpeed = 1; // Speed of rotation
    private List<Bullet> bullets = new ArrayList<>(); // List to hold bullets
    private long lastBulletTime = 0; // Last time a bullet was fired
    private final long bulletInterval = 1000; // Interval between bullets in milliseconds
    private int hitCount = 0; // Number of times the bot has been hit
    private final EnemyAnimation enemyAnimation;

    // Range within which Scoutbot will start firing at the player
    private final double attackRange = 200.0;

    public Scoutbot(double startX, double startY) {
        super(startX, startY, 0, 0, 0, 20); // Adjust the size as needed
        this.enemyAnimation = new EnemyAnimation(startX, startY);
        System.out.println("Scoutbot spawned!");
    }

    @Override
    public void update(double width, double height, GraphicsContext gc) {
        // Rotate Scoutbot
        angle += rotationSpeed;

        // Update bullets and remove those off-screen
        bullets.removeIf(bullet -> bullet.isOffScreen(width, height));
        bullets.forEach(Bullet::update);

        // Draw Scoutbot
        gc.save();
        gc.translate(x, y);
        gc.rotate(angle);
        enemyAnimation.draw(gc); // Use the animation to draw
        gc.restore();

        // Draw bullets
        for (Bullet bullet : bullets) {
            gc.fillOval(bullet.getX(), bullet.getY(), 5, 5); // Render bullets
        }
    }

    public void attackPlayer(Spaceship player) {
        // Check distance to player
        double distance = Math.sqrt(Math.pow(player.getX() - x, 2) + Math.pow(player.getY() - y, 2));

        if (distance <= attackRange && System.currentTimeMillis() - lastBulletTime > bulletInterval) {
            // Calculate direction towards the player
            double dx = player.getX() - x;
            double dy = player.getY() - y;
            double magnitude = Math.sqrt(dx * dx + dy * dy);
            dx /= magnitude;
            dy /= magnitude;

            // Fire bullet
            bullets.add(new Bullet(x, y, dx, dy));
            lastBulletTime = System.currentTimeMillis();
            System.out.println("Scoutbot is attacking player!");
        }
    }

    public void takeHit() {
        hitCount++;
        if (hitCount >= 2) {
            System.out.println("Scoutbot destroyed!");
            // Code to remove or disable the bot
        } else {
            System.out.println("Scoutbot hit! Remaining hits: " + (2 - hitCount));
        }
    }

    public List<Bullet> getBullets() {
        return bullets;
    }
}
