package se233.asterioddemo;

import javafx.scene.canvas.GraphicsContext;

public class PlaneAnimation extends Animation {
    private double angle = 0;

    public PlaneAnimation(double x, double y) {
        super(x, y, "/se233/asterioddemo/assets/PlayerplaneFA.png", 3, 1, 0.5);
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void setFrameByVelocity(double velocity) {
        if (velocity >= 0 && velocity <= 1) {
            currentFrame = 0;
        } else if (velocity > 1 && velocity <= 2) {
            currentFrame = 1;
        } else {
            currentFrame = 2;
        }
    }

    @Override
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