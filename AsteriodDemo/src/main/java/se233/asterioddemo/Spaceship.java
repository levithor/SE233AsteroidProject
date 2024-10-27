package se233.asterioddemo;

import javafx.scene.canvas.GraphicsContext;

public class Spaceship extends Character {
    private final Animation animation;
    private double velocityX = 0, velocityY = 0;
    private boolean movingLeft, movingRight, movingUp, movingDown;
    private int lives = 3; // Start with 3 lives
    private double angle = 0; // Angle for the spaceship rotation

    public Spaceship(double startX, double startY) {
        super(startX, startY, 0, 0, 0.1, 0);
        this.animation = new Animation("/se233/asterioddemo/assets/PlayerplaneA.png");
    }

    // Handle direction control
    public void moveLeft(boolean move) {
        movingLeft = move;
    }

    public void moveRight(boolean move) {
        movingRight = move;
    }

    public void moveUp(boolean move) {
        movingUp = move;
    }

    public void moveDown(boolean move) {
        movingDown = move;
    }

    // Getters for lives
    public int getLives() {
        return lives;
    }

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
        // Set direction-based angle in Animation
        if (movingUp) {
            velocityY = -speed;
            animation.faceUp();
        } else if (movingDown) {
            velocityY = speed;
            animation.faceDown();
        } else {
            velocityY = 0;
        }

        if (movingLeft) {
            velocityX = -speed;
            animation.faceLeft();
        } else if (movingRight) {
            velocityX = speed;
            animation.faceRight();
        } else {
            velocityX = 0;
        }

        // Update position based on velocity
        x += velocityX;
        y += velocityY;

        // Wrap spaceship around edges
        if (x < 0) x = canvasWidth;
        if (x > canvasWidth) x = 0;
        if (y < 0) y = canvasHeight;
        if (y > canvasHeight) y = 0;

        // Draw spaceship with current animation angle
        animation.draw(gc, x - animation.getWidth() / 2, y - animation.getHeight() / 2);
    }
}