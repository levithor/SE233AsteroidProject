package se233.asterioddemo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Objects;

public class Animation {
    private Image spriteSheet;
    private int frameWidth = 256;
    private int frameHeight = 122;
    private int currentFrame = 0;
    private double scale = 0.5; // Adjust scale as needed for smaller display
    private double angle = 0; // Angle for rotation

    public Animation(String resourcePath) {
        this.spriteSheet = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/se233/asterioddemo/assets/PlayerplaneA.png")));
    }

    // Set the current frame of the animation
    public void setFrame(int frame) {
        currentFrame = frame;
    }

    // Set angle based on direction
    public void faceUp() {
        angle = 0; // Face upwards
    }

    public void faceDown() {
        angle = 180; // Face downwards
    }

    public void faceLeft() {
        angle = 270; // Face left
    }

    public void faceRight() {
        angle = 90; // Face right
    }

    // Draw the animation at the specified position and angle
    public void draw(GraphicsContext gc, double x, double y) {
        int sx = currentFrame * frameWidth;
        double scaledWidth = frameWidth * scale;
        double scaledHeight = frameHeight * scale;

        // Save the current transformation state
        gc.save();

        // Move to the spaceship's position and apply rotation
        gc.translate(x + scaledWidth / 2, y + scaledHeight / 2);  // Translate to the center of the image
        gc.rotate(angle);                                         // Rotate by the angle

        // Draw the image centered around the spaceship position
        gc.drawImage(spriteSheet, sx, 0, frameWidth, frameHeight,
                -scaledWidth / 2, -scaledHeight / 2, scaledWidth, scaledHeight);

        // Restore the original transformation state
        gc.restore();
    }

    // Methods to get scaled width and height for positioning purposes
    public double getWidth() {
        return frameWidth * scale;
    }

    public double getHeight() {
        return frameHeight * scale;
    }
}