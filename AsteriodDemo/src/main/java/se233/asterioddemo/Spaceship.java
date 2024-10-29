package se233.asterioddemo;

import javafx.scene.canvas.GraphicsContext;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Spaceship extends Character {
    private static final Logger logger = Logger.getLogger(Spaceship.class.getName());
    private final PlaneAnimation planeAnimation;
    private double velocityX = 0, velocityY = 0;
    private boolean rotatingLeft, rotatingRight, movingForward;
    private int lives = 3;
    private double currentAngle = 0; // Angle of spaceship facing direction
    private final double speed = 0.1;
    private final double rotationSpeed = 3; // Adjust rotation speed as needed
    private final double friction = 0.97; // Friction factor to gradually reduce speed
    private long lastLogTime = 0; // Timestamp for the last log

    public Spaceship(double startX, double startY) {
        super(startX, startY, 0, 0, 0.1, 0);
        this.planeAnimation = new PlaneAnimation(startX, startY);
    }

    public void rotateLeft(boolean rotate) {
        rotatingLeft = rotate;
        logger.log(Level.FINE, "Rotating left: " + rotate);
    }

    public void rotateRight(boolean rotate) {
        rotatingRight = rotate;
        logger.log(Level.FINE, "Rotating right: " + rotate);
    }

    public void moveForward(boolean move) {
        movingForward = move;
        logger.log(Level.FINE, "Moving forward: " + move);
    }

    public int getLives() { return lives; }

    public void loseLife() {
        lives--;
        logger.log(Level.WARNING, "Lost a life. Remaining lives: " + lives);
    }

    public boolean isAlive() {
        return lives > 0;
    }

    public double getCurrentAngle() {
        return currentAngle;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    @Override
    public void update(double canvasWidth, double canvasHeight, GraphicsContext gc) {
        // Rotate the spaceship when rotating left or right
        if (rotatingLeft) {
            currentAngle -= rotationSpeed;
        }
        if (rotatingRight) {
            currentAngle += rotationSpeed;
        }

        // Move forward in the direction of currentAngle when pressing forward
        if (movingForward) {
            velocityX += speed * Math.cos(Math.toRadians(currentAngle + 270));
            velocityY += speed * Math.sin(Math.toRadians(currentAngle + 270));
        }

        // Apply friction to gradually reduce speed when not moving forward
        if (!movingForward) {
            velocityX *= friction;
            velocityY *= friction;
        }

        // Calculate the combined velocity magnitude
        double velocity = Math.sqrt(velocityX * velocityX + velocityY * velocityY);

        // Limit the velocity to a maximum value for smoother movement
        if (velocity > 4) {
            double scale = 4 / velocity;
            velocityX *= scale;
            velocityY *= scale;
        }

        // Update the animation frame based on combined velocity
        planeAnimation.setFrameByVelocity(velocity);
        planeAnimation.setAngle(currentAngle); // Set rotation to face the current angle

        // Update spaceship position
        x += velocityX;
        y += velocityY;

        planeAnimation.setPosition(x, y);

        // Wrap-around screen logic
        if (x < 0) x = canvasWidth;
        if (x > canvasWidth) x = 0;
        if (y < 0) y = canvasHeight;
        if (y > canvasHeight) y = 0;

        // Log the spaceship's position and velocity once every second
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastLogTime >= 1000) {
            logger.log(Level.INFO, "Spaceship position: (" + x + ", " + y + "), velocity: (" + velocityX + ", " + velocityY + ")");
            lastLogTime = currentTime;
        }

        planeAnimation.draw(gc);
    }

    public Bullet createBullet() {
        double angle = Math.toRadians(currentAngle + 270);
        double bulletDx = Math.cos(angle);
        double bulletDy = Math.sin(angle);
        return new Bullet(x + 7.5, y + 7.5, bulletDx, bulletDy);
    }

    public Missile createMissile() {
        double angle = Math.toRadians(currentAngle + 270);
        double missileDx = Math.cos(angle);
        double missileDy = Math.sin(angle);
        return new Missile(x + 7.5, y + 7.5, missileDx, missileDy);
    }

    public void reset() {
        this.x = 400; // Reset to initial X position
        this.y = 300; // Reset to initial Y position
        this.velocityX = 0;
        this.velocityY = 0;
        this.lives = 3; // Reset lives to initial value
        this.rotatingLeft = false;
        this.rotatingRight = false;
        this.movingForward = false;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}