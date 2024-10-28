package se233.asterioddemo;

import javafx.scene.canvas.GraphicsContext;

public class MissileAnimation extends Animation {
    public MissileAnimation(double x, double y) {
        super(x, y, "/se233/asterioddemo/assets/bird.png", 5, 3, 1.0); // Assuming 4 frames in a row
    }

    @Override
    public void draw(GraphicsContext gc) {
        int frameX = (currentFrame % columns) * frameWidth;
        int frameY = (currentFrame / columns) * frameHeight;

        gc.save(); // Save the current state of GraphicsContext
        gc.translate(x, y);
        gc.drawImage(spriteSheet, frameX, frameY, frameWidth, frameHeight, -frameWidth * scale / 2, -frameHeight * scale / 2, frameWidth * scale, frameHeight * scale);
        gc.restore(); // Restore the original state
    }
}