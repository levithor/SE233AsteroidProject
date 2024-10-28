package se233.asterioddemo;

import javafx.scene.canvas.GraphicsContext;

public class Spaceship extends Character {
    private final Animation animation;
    private double velocityX = 0, velocityY = 0; // Start with zero velocity
    private boolean movingLeft, movingRight, movingUp, movingDown;
    private int lives = 3;
    private double currentAngle = 0;
    private final double speed = 0.1;

    public Spaceship(double startX, double startY) {
        super(startX, startY, 0, 0, 0.1, 0);
        this.animation = new Animation(startX, startY);
    }

    public void moveLeft(boolean move) { movingLeft = move; }
    public void moveRight(boolean move) { movingRight = move; }
    public void moveUp(boolean move) { movingUp = move; }
    public void moveDown(boolean move) { movingDown = move; }

    public int getLives() { return lives; }

    public void loseLife() {
        lives--;
    }

    public boolean isAlive() {
        return lives > 0;
    }

    @Override
    public void update(double canvasWidth, double canvasHeight, GraphicsContext gc) {
        if (movingLeft) {
            velocityX -= speed;
            currentAngle -= 5;
        }
        if (movingRight) {
            velocityX += speed;
            currentAngle += 5;
        }
        if (movingUp) {
            velocityY -= speed;
        }
        if (movingDown) {
            velocityY += speed;
        }

        velocityX = Math.max(-4, Math.min(4, velocityX));
        velocityY = Math.max(-4, Math.min(4, velocityY));

        // Calculate the combined velocity magnitude
        double velocity = Math.sqrt(velocityX * velocityX + velocityY * velocityY);

        // Update the animation frame based on combined velocity
        animation.setFrameByVelocity(velocity);
        animation.setAngle(currentAngle);
        x += velocityX;
        y += velocityY;

        animation.setPosition(x, y);

        // Print velocity and current frame for debugging
        System.out.println("Velocity: " + velocity);

        // Apply friction to gradually reduce speed when no input
        if (!movingLeft && !movingRight) velocityX *= 0.9;
        if (!movingUp && !movingDown) velocityY *= 0.9;

        if (x < 0) x = canvasWidth;
        if (x > canvasWidth) x = 0;
        if (y < 0) y = canvasHeight;
        if (y > canvasHeight) y = 0;

        animation.draw(gc);
    }

    public void reset() {
        this.x = 400; // Reset to initial X position
        this.y = 300; // Reset to initial Y position
        this.velocityX = 0;
        this.velocityY = 0;
        this.lives = 3; // Reset lives to initial value
        this.movingLeft = false;
        this.movingRight = false;
        this.movingUp = false;
        this.movingDown = false;
    }
}
