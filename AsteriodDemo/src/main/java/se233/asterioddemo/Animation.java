package se233.asterioddemo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Animation {
    private Image spriteSheet;
    private int frameWidth = 256;
    private int frameHeight = 256;
    private int currentFrame = 0;
    private double scale = 0.5; // Adjust scale as needed for smaller display

    public Animation(String resourcePath) {
        this.spriteSheet = new Image(getClass().getResourceAsStream(resourcePath));
    }

    public void setFrame(int frame) {
        currentFrame = frame;
    }

    public void draw(GraphicsContext gc, double x, double y) {
        int sx = currentFrame * frameWidth;
        double scaledWidth = frameWidth * scale;
        double scaledHeight = frameHeight * scale;

        gc.drawImage(spriteSheet, sx, 0, frameWidth, frameHeight, x, y, scaledWidth, scaledHeight);
    }
}
