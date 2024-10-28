package se233.asterioddemo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Objects;

public abstract class Animation {
    protected final Image spriteSheet;
    protected final int frameWidth;
    protected final int frameHeight;
    protected int currentFrame = 0;
    protected final int columns;
    protected final int rows;
    protected final int totalFrames;
    protected double scale;
    protected double x, y;

    public Animation(double x, double y, String spriteSheetPath, int columns, int rows, double scale) {
        this.x = x;
        this.y = y;
        this.spriteSheet = new Image(Objects.requireNonNull(getClass().getResourceAsStream(spriteSheetPath)));
        this.columns = columns;
        this.rows = rows;
        this.totalFrames = columns * rows;
        this.frameWidth = (int) spriteSheet.getWidth() / columns;
        this.frameHeight = (int) spriteSheet.getHeight() / rows;
        this.scale = scale;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        currentFrame = (currentFrame + 1) % totalFrames; // Loop through frames
    }

    public void draw(GraphicsContext gc) {
        int frameX = (currentFrame % columns) * frameWidth;
        int frameY = (currentFrame / columns) * frameHeight;

        gc.save(); // Save the current state of GraphicsContext
        gc.translate(x, y);
        gc.drawImage(spriteSheet, frameX, frameY, frameWidth, frameHeight, -frameWidth * scale / 2, -frameHeight * scale / 2, frameWidth * scale, frameHeight * scale);
        gc.restore(); // Restore the original state
    }
}