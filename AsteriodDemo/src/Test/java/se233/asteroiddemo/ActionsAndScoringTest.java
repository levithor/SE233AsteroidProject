package se233.asteroiddemo;

import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import javafx.scene.canvas.GraphicsContext;
import se233.asterioddemo.Asteroid;
import se233.asterioddemo.AsteroidGame;
import se233.asterioddemo.Bullet;
import se233.asterioddemo.Spaceship;
import javafx.application.Platform;
import javafx.stage.Stage;
import java.util.concurrent.CountDownLatch;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ActionsAndScoringTest {
    private Spaceship spaceship;
    private GraphicsContext gc;
    private AsteroidGame game;

    @BeforeEach
    public void setUp() {
        spaceship = new Spaceship(400, 300);
        gc = Mockito.mock(GraphicsContext.class);
        game = new AsteroidGame();
    }

    @Test
    public void testSpaceshipShooting() {
        // Simulate shooting action
        Bullet bullet = spaceship.createBullet();
        assertNotNull(bullet, "Bullet should be created when shooting");

        // Verify bullet properties
        assertEquals(407.5, bullet.getX(), 0.1);
        assertEquals(307.5, bullet.getY(), 0.1);
    }

    @Test
    public void testScoringPoints() throws InterruptedException {
        // Initialize JavaFX toolkit
        CountDownLatch toolkitLatch = new CountDownLatch(1);
        Platform.startup(() -> {
            toolkitLatch.countDown();
        });
        toolkitLatch.await(); // Wait for the JavaFX toolkit to initialize

        // Initialize game elements on the JavaFX Application Thread
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                game.start(new Stage());
                game.initGame();
            } finally {
                latch.countDown();
            }
        });
        latch.await(); // Wait for the JavaFX initialization to complete

        List<Bullet> bullets = game.getBullets();
        List<Asteroid> asteroids = game.getAsteroids();

        // Simulate shooting and hitting an asteroid
        Bullet bullet = spaceship.createBullet();
        bullets.add(bullet);
        Asteroid asteroid = new Asteroid(407.5, 307.5, 0, 0, Asteroid.Size.SMALL);
        asteroids.add(asteroid);

        // Check initial score
        int initialScore = game.getScoreCount();
        assertEquals(0, initialScore, "Initial score should be 0");

        // Update game to process collision
        game.checkCollisions();

        // Verify score after hitting the asteroid
        int updatedScore = game.getScoreCount();
        assertEquals(initialScore + 100, updatedScore, "Score should increase by 100 after hitting a small asteroid");
    }
}
