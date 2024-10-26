package se233.asterioddemo;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class AsteroidGame extends Application {

    private double spaceshipX = 400, spaceshipY = 300; // Initial spaceship position
    private double spaceshipSpeed = 4;
    private boolean left, right, up, down;
    private List<Bullet> bullets = new ArrayList<>(); // List of bullets on screen
    private List<Asteroid> asteroids = new ArrayList<>(); // List of asteroids on screen
    private Random random = new Random();
    private static final int MAX_ASTEROIDS = 10; // Maximum number of asteroids allowed on screen at once

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Canvas canvas = new Canvas(800, 600);
        root.getChildren().add(canvas);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Asteroid Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Handle key events
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) left = true;
            if (event.getCode() == KeyCode.RIGHT) right = true;
            if (event.getCode() == KeyCode.UP) up = true;
            if (event.getCode() == KeyCode.DOWN) down = true;
            if (event.getCode() == KeyCode.SPACE) {
                bullets.add(new Bullet(spaceshipX, spaceshipY));
            }
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.LEFT) left = false;
            if (event.getCode() == KeyCode.RIGHT) right = false;
            if (event.getCode() == KeyCode.UP) up = false;
            if (event.getCode() == KeyCode.DOWN) down = false;
        });

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Clear screen
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

                // Move spaceship
                moveSpaceship();

                // Draw spaceship
                gc.setFill(Color.BLUE);
                gc.fillRect(spaceshipX, spaceshipY, 20, 20);

                // Update and draw bullets
                Iterator<Bullet> bulletIterator = bullets.iterator();
                while (bulletIterator.hasNext()) {
                    Bullet bullet = bulletIterator.next();
                    bullet.update();
                    if (bullet.isOffScreen(canvas.getWidth(), canvas.getHeight())) {
                        bulletIterator.remove();
                    } else {
                        gc.setFill(Color.RED);
                        gc.fillRect(bullet.getX(), bullet.getY(), 5, 5);
                    }
                }

                // Spawn asteroids randomly if below the limit
                if (random.nextDouble() < 0.02 && asteroids.size() < MAX_ASTEROIDS) {
                    asteroids.add(new Asteroid(random.nextInt((int)canvas.getWidth()), 0));
                }

                // Update and draw asteroids
                Iterator<Asteroid> asteroidIterator = asteroids.iterator();
                while (asteroidIterator.hasNext()) {
                    Asteroid asteroid = asteroidIterator.next();
                    asteroid.update(canvas.getWidth(), canvas.getHeight());
                    gc.setFill(Color.GRAY);
                    gc.fillOval(asteroid.getX(), asteroid.getY(), asteroid.getSize(), asteroid.getSize());
                }

                // Collision detection
                checkCollisions();
            }
        }.start();
    }

    private void moveSpaceship() {
        if (left) spaceshipX -= spaceshipSpeed;
        if (right) spaceshipX += spaceshipSpeed;
        if (up) spaceshipY -= spaceshipSpeed;
        if (down) spaceshipY += spaceshipSpeed;

        // Check for out of bounds and wrap around
        if (spaceshipX < 0) spaceshipX = 800;
        if (spaceshipX > 800) spaceshipX = 0;
        if (spaceshipY < 0) spaceshipY = 600;
        if (spaceshipY > 600) spaceshipY = 0;
    }

    // This constantly checks for collisions between asteroids and bullets in their lists
    private void checkCollisions() {
        Iterator<Asteroid> asteroidIterator = asteroids.iterator();
        while (asteroidIterator.hasNext()) {
            Asteroid asteroid = asteroidIterator.next();
            Iterator<Bullet> bulletIterator = bullets.iterator();
            while (bulletIterator.hasNext()) {
                Bullet bullet = bulletIterator.next();
                if (isCollision(asteroid, bullet)) {
                    asteroidIterator.remove();
                    bulletIterator.remove();
                    break;
                }
            }
        }
    }

    // This checks if an asteroid and bullet have collided
    private boolean isCollision(Asteroid asteroid, Bullet bullet) {
        double dx = asteroid.getX() - bullet.getX();
        double dy = asteroid.getY() - bullet.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < asteroid.getSize() / 2;
    }
}