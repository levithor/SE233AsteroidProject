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

    private static final int MAX_ASTEROIDS = 10; // Limit to the number of asteroids

    private double spaceshipX = 400, spaceshipY = 300;
    private double spaceshipSpeed = 4;
    private boolean left, right, up, down;
    private List<Bullet> bullets = new ArrayList<>();
    private List<Asteroid> asteroids = new ArrayList<>();
    private Random random = new Random();

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

        // Handle keyboard input for spaceship movement
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) left = true;
            if (event.getCode() == KeyCode.RIGHT) right = true;
            if (event.getCode() == KeyCode.UP) up = true;
            if (event.getCode() == KeyCode.DOWN) down = true;
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.LEFT) left = false;
            if (event.getCode() == KeyCode.RIGHT) right = false;
            if (event.getCode() == KeyCode.UP) up = false;
            if (event.getCode() == KeyCode.DOWN) down = false;
        });

        // Handle mouse click to shoot bullets
        scene.setOnMouseClicked(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();

            // Calculate direction for bullet
            double angle = Math.atan2(mouseY - spaceshipY, mouseX - spaceshipX);
            double bulletDx = Math.cos(angle);
            double bulletDy = Math.sin(angle);

            // Create a bullet in the direction of the mouse click
            bullets.add(new Bullet(spaceshipX + 7.5, spaceshipY + 7.5, bulletDx, bulletDy));
        });

        new AnimationTimer() {
            @Override
            public void handle(long now) {
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

                // Spawn asteroids randomly from all directions
                if (random.nextDouble() < 0.02 && asteroids.size() < MAX_ASTEROIDS) {
                    spawnAsteroidFromRandomDirection(canvas.getWidth(), canvas.getHeight());
                }

                // Update and draw asteroids
                Iterator<Asteroid> asteroidIterator = asteroids.iterator();
                while (asteroidIterator.hasNext()) {
                    Asteroid asteroid = asteroidIterator.next();
                    asteroid.update(canvas.getWidth(), canvas.getHeight());
                    if (asteroid.isOffScreen(canvas.getWidth(), canvas.getHeight())) {
                        asteroidIterator.remove();
                    } else {
                        gc.setFill(Color.GRAY);
                        gc.fillOval(asteroid.getX(), asteroid.getY(), asteroid.getSize(), asteroid.getSize());
                    }
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

        // Wrap spaceship location to the opposite side of the window
        if (spaceshipX < 0) spaceshipX = 800;
        if (spaceshipX > 800) spaceshipX = 0;
        if (spaceshipY < 0) spaceshipY = 600;
        if (spaceshipY > 600) spaceshipY = 0;
    }

    private void spawnAsteroidFromRandomDirection(double canvasWidth, double canvasHeight) {
        double startX = 0, startY = 0;
        double dx = 0, dy = 0;

        // Choose a random side (0 = top, 1 = bottom, 2 = left, 3 = right)
        int side = random.nextInt(4);
        switch (side) {
            case 0: // Top
                startX = random.nextDouble() * canvasWidth;
                startY = -30;
                dx = (random.nextDouble() - 0.5) * 2;
                dy = 1;
                break;
            case 1: // Bottom
                startX = random.nextDouble() * canvasWidth;
                startY = canvasHeight + 30;
                dx = (random.nextDouble() - 0.5) * 2;
                dy = -1;
                break;
            case 2: // Left
                startX = -30;
                startY = random.nextDouble() * canvasHeight;
                dx = 1;
                dy = (random.nextDouble() - 0.5) * 2;
                break;
            case 3: // Right
                startX = canvasWidth + 30;
                startY = random.nextDouble() * canvasHeight;
                dx = -1;
                dy = (random.nextDouble() - 0.5) * 2;
                break;
        }

        asteroids.add(new Asteroid(startX, startY, dx, dy));
    }

    private void checkCollisions() {
        // Check for collisions between bullets and asteroids
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            Iterator<Asteroid> asteroidIterator = asteroids.iterator();
            while (asteroidIterator.hasNext()) {
                Asteroid asteroid = asteroidIterator.next();

                // Calculate distance between the bullet and asteroid
                double dx = bullet.getX() - asteroid.getX();
                double dy = bullet.getY() - asteroid.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                // Check for collision
                if (distance < asteroid.getSize() / 2) {
                    bulletIterator.remove();
                    asteroidIterator.remove();
                    break;
                }
            }
        }
    }
}