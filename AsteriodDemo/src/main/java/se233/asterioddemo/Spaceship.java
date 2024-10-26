package se233.asterioddemo;

import javafx.scene.canvas.GraphicsContext;

public class Spaceship {
    private final Animation animation;
    private double x, y;
    private double speed = 0.1; // Controls acceleration rate
    private double maxSpeed = 4; // Maximum speed limit
    private double velocityX = 0, velocityY = 0;
    private boolean movingLeft, movingRight, movingUp, movingDown;

    public Spaceship(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        this.animation = new Animation("/se233/asterioddemo/assets/PlayerplaneA.png");
    }

    // Handle direction control
    public void moveLeft(boolean move) { movingLeft = move; }
    public void moveRight(boolean move) { movingRight = move; }
    public void moveUp(boolean move) { movingUp = move; }
    public void moveDown(boolean move) { movingDown = move; }

    // Getters for x and y position
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    // Update spaceship position, velocity, and animation frame
    public void update(double canvasWidth, double canvasHeight) {
        // Adjust velocity based on input
        if (movingLeft) velocityX -= speed;
        if (movingRight) velocityX += speed;
        if (movingUp) velocityY -= speed;
        if (movingDown) velocityY += speed;

        // Apply maximum speed limit
        velocityX = Math.max(-maxSpeed, Math.min(maxSpeed, velocityX));
        velocityY = Math.max(-maxSpeed, Math.min(maxSpeed, velocityY));

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
    }

    // Draw spaceship with current animation frame
    public void draw(GraphicsContext gc) {
        // Calculate the angle based on velocity
        double angle = Math.atan2(-velocityY, velocityX); // In radians
        double angleInDegrees = Math.toDegrees(angle);

        // Save the current state of the graphics context
        gc.save();

        // Translate to the spaceship's position
        gc.translate(x, y);
        // Rotate the graphics context by the calculated angle
        gc.rotate(angleInDegrees);
        // Draw the spaceship
        animation.draw(gc, 0, 0); // Draw at the origin after translation and rotation

        // Restore the graphics context to its original state
        gc.restore();
    }
}
