package se233.asterioddemo;

import javafx.scene.canvas.GraphicsContext;

public class Spaceship extends Character {
    private final Animation animation;
    private double velocityX = 0, velocityY = 0;
    private boolean movingLeft, movingRight, movingUp, movingDown;
    private int lives = 3; // Start with 3 lives
    private double currentAngle = 0; // Current rotation angle
    private double rotationSpeed = 10; // Speed of rotation (degrees per update)

    public Spaceship(double startX, double startY) {
        super(startX, startY, 0, 0, 0.1, 0);
        this.animation = new Animation("/se233/asterioddemo/assets/PlayerplaneA.png");
    }

    // Handle direction control
    public void moveLeft(boolean move) { movingLeft = move; }
    public void moveRight(boolean move) { movingRight = move; }
    public void moveUp(boolean move) { movingUp = move; }
    public void moveDown(boolean move) { movingDown = move; }

    // Getters for lives
    public int getLives() { return lives; }

    // Reduce lives by 1
    public void loseLife() {
        lives--;
    }

    // Check if the spaceship is still alive
    public boolean isAlive() {
        return lives > 0;
    }

    // Update spaceship position, velocity, and animation frame
    @Override
    public void update(double canvasWidth, double canvasHeight, GraphicsContext gc) {
        // Adjust velocity based on input
        if (movingLeft) {
            velocityX -= speed;
            animation.faceLeft();
        }
        if (movingRight) {
            velocityX += speed;
            animation.faceRight();
        }
        if (movingUp) {
            velocityY -= speed;
            animation.faceUp();
        }
        if (movingDown) {
            velocityY += speed;
            animation.faceDown();
        }

        // Apply maximum speed limit
        velocityX = Math.max(-4, Math.min(4, velocityX));
        velocityY = Math.max(-4, Math.min(4, velocityY));

        // Update position based on velocity
        x += velocityX;
        y += velocityY;

        // Apply friction to gradually reduce speed when no input
        if (!movingLeft && !movingRight) velocityX *= 0.95;
        if (!movingUp && !movingDown) velocityY *= 0.95;

        // Wrap spaceship around edges
        if (x < 0) x = canvasWidth;
        if (x > canvasWidth) x = 0;
        if (y < 0) y = canvasHeight;
        if (y > canvasHeight) y = 0;

        // Draw spaceship with current animation frame
        animation.draw(gc, x, y);
    }
}
