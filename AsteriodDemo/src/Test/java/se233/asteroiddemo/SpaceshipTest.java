package se233.asteroiddemo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import se233.asterioddemo.Spaceship;
import javafx.scene.canvas.GraphicsContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SpaceshipTest {
    private Spaceship spaceship;
    private GraphicsContext gc;

    @BeforeEach
    public void setUp() {
        spaceship = new Spaceship(400, 300);
        gc = Mockito.mock(GraphicsContext.class);
    }

    @Test
    public void testInitialPosition() {
        assertEquals(400, spaceship.getX());
        assertEquals(300, spaceship.getY());
    }

    @Test
    public void testMoveForward() {
        spaceship.moveForward(true);
        spaceship.update(800, 600, gc); // Pass the mock GraphicsContext
        assertNotEquals(300, spaceship.getX());
        assertNotEquals(400, spaceship.getY());
    }

    @Test
    public void testRotateLeft() {
        spaceship.rotateLeft(true);
        spaceship.update(800, 600, gc); // Pass the mock GraphicsContext
        assertTrue(spaceship.getCurrentAngle() < 0);
    }

    @Test
    public void testRotateRight() {
        spaceship.rotateRight(true);
        spaceship.update(800, 600, gc); // Pass the mock GraphicsContext
        assertTrue(spaceship.getCurrentAngle() > 0);
    }

    @Test
    public void testFriction() {
        spaceship.moveForward(false);
        spaceship.update(800, 600, gc); // Pass the mock GraphicsContext
        double initialVelocityX = spaceship.getVelocityX();
        double initialVelocityY = spaceship.getVelocityY();
        spaceship.moveForward(true);
        spaceship.update(800, 600, gc); // Pass the mock GraphicsContext
        assertTrue(spaceship.getVelocityX() < initialVelocityX);
        assertTrue(spaceship.getVelocityY() < initialVelocityY);
    }

    @Test
    public void testWrapAroundScreen() {
        spaceship.setX(-1);
        spaceship.update(800, 600, gc); // Pass the mock GraphicsContext
        assertEquals(800, spaceship.getX());

        spaceship.setX(801);
        spaceship.update(800, 600, gc); // Pass the mock GraphicsContext
        assertEquals(0, spaceship.getX());

        spaceship.setY(-1);
        spaceship.update(800, 600, gc); // Pass the mock GraphicsContext
        assertEquals(600, spaceship.getY());

        spaceship.setY(601);
        spaceship.update(800, 600, gc); // Pass the mock GraphicsContext
        assertEquals(0, spaceship.getY());
    }
}