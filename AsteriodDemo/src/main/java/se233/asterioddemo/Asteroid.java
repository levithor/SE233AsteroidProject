package se233.asterioddemo;

import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Rotate;

class Asteroid extends Character {
    private double angle = 0;
    private double rotationSpeed = 2; // Rotation speed in degrees per update
    private Image image;

    public Asteroid(double x, double y, double dx, double dy) {
        super(x, y, dx, dy, 2, 30);
        this.image = new Image(getClass().getResourceAsStream("/se233/asterioddemo/assets/asteroid.png"));
    }

    @Override
    public void update(double width, double height, GraphicsContext gc) {
        x += dx * speed;
        y += dy * speed;
        angle += rotationSpeed; // Update the rotation angle

        // Wrap asteroid location to the opposite side of the window
        if (x < -size) x = width + size;
        if (x > width + size) x = -size;
        if (y < -size) y = height + size;
        if (y > height + size) y = -size;

        // Draw the asteroid image with rotation
        gc.save();
        Rotate r = new Rotate(angle, x + size / 2, y + size / 2);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        gc.drawImage(image, x, y, size, size);
        gc.restore();
    }

    public boolean isOffScreen(double width, double height) {
        return x < -size || x > width + size || y < -size || y > height + size;
    }
}