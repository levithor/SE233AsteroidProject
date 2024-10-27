package se233.asterioddemo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Objects;

public class Explosion {
    private double x, y;
    private Image spriteSheet;
    private int frame = 0;
    private int maxFrames = 20; // Number of frames the explosion will last
    private int columns = 5; // Number of columns in the sprite sheet
    private int rows = 4; // Number of rows in the sprite sheet
    private int totalFrames = columns * rows;
    private double frameWidth;
    private double frameHeight;

    public Explosion(double x, double y) {
        this.x = x;
        this.y = y;
        this.spriteSheet = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/se233/asterioddemo/assets/explosion.png")));
        this.frameWidth = spriteSheet.getWidth() / columns;
        this.frameHeight = spriteSheet.getHeight() / rows;
    }

    public void update() {
        frame++;
    }

    public boolean isFinished() {
        return frame >= maxFrames;
    }

    public void draw(GraphicsContext gc) {
        int currentFrame = frame % totalFrames;
        int frameX = (currentFrame % columns) * (int) frameWidth;
        int frameY = (currentFrame / columns) * (int) frameHeight;

        gc.drawImage(spriteSheet, frameX, frameY, frameWidth, frameHeight, x - frameWidth / 2, y - frameHeight / 2, frameWidth, frameHeight);
    }
}