package se233.asterioddemo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Objects;

public class EnemyAnimation extends Animation {
    private static final String RESOURCE_PATH = "/se233/asterioddemo/assets/enemyone.jpg";
    private Image image;

    public EnemyAnimation(double x, double y) {
        // Call superclass constructor with null for image path and assign other parameters
        super(x, y, "/se233/asterioddemo/assets/enemyone.jpg", 3, 1, 0.2);

        // Load image after superclass constructor is called
        try {
            this.image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(RESOURCE_PATH),
                    "Image could not be loaded from " + RESOURCE_PATH));
        } catch (NullPointerException e) {
            System.err.println("Failed to load image at: " + RESOURCE_PATH);
            e.printStackTrace();
        }
    }

    @Override
    public void draw(GraphicsContext gc) {
        int frameX = (currentFrame % columns) * frameWidth;
        int frameY = (currentFrame / columns) * frameHeight;

        gc.save(); // Save the current state of GraphicsContext
        gc.translate(x, y);
        gc.drawImage(image, frameX, frameY, frameWidth, frameHeight,
                -frameWidth * scale / 2, -frameHeight * scale / 2,
                frameWidth * scale, frameHeight * scale);
        gc.restore(); // Restore the original state
    }
}
