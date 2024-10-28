package se233.asterioddemo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Objects;

public class Animation {
    private final Image spriteSheet;
    private final int frameWidth;
    private final int frameHeight;
    private int currentFrame = 0;
    private final int columns = 3; // Number of columns in the sprite sheet
    private final int rows = 1;    // Number of rows in the sprite sheet
    private final int totalFrames = columns * rows;
    private double scale = 0.5;
    private double angle = 0;
    private double x, y;

    public Animation(double x, double y) {
        this.x = x;
        this.y = y;
        this.spriteSheet = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/se233/asterioddemo/assets/PlayerplaneFA.png")));
        this.frameWidth = (int) spriteSheet.getWidth() / columns;
        this.frameHeight = (int) spriteSheet.getHeight() / rows;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void setFrameByVelocity(double velocity) {
        if (velocity >= 0 && velocity <= 1) {
            currentFrame = 0;
        } else if (velocity > 1 && velocity <= 3) {
            currentFrame = 1;
        } else if (velocity > 3) {
            currentFrame = 2;
        }
    }

    public void draw(GraphicsContext gc) {
        int frameX = (currentFrame % columns) * frameWidth;
        int frameY = (currentFrame / columns) * frameHeight;

        gc.save(); // Save the current state of GraphicsContext
        gc.translate(x, y);
        gc.rotate(angle);
        gc.drawImage(spriteSheet, frameX, frameY, frameWidth, frameHeight, -frameWidth * scale / 2, -frameHeight * scale / 2, frameWidth * scale, frameHeight * scale);
        gc.restore(); // Restore the original state (un-rotate)
    }
}