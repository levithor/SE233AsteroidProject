package se233.asterioddemo;

import javafx.application.Platform;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image; // Import the Image class
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class AsteroidGame extends Application {

    private static final int MAX_ASTEROIDS = 10;

    private List<Bullet> bullets = new ArrayList<>();
    private List<Asteroid> asteroids = new ArrayList<>();
    private Random random = new Random();

    private Spaceship spaceship;
    private AnimationTimer animationTimer;
    private Image backgroundImage; // Field for background image

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

        // Load the background image
        backgroundImage = new Image(getClass().getResourceAsStream("/se233/asterioddemo/assets/spaceBG.jpg"));

        spaceship = new Spaceship(400, 300);

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) spaceship.moveLeft(true);
            if (event.getCode() == KeyCode.RIGHT) spaceship.moveRight(true);
            if (event.getCode() == KeyCode.UP) spaceship.moveUp(true);
            if (event.getCode() == KeyCode.DOWN) spaceship.moveDown(true);
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.LEFT) spaceship.moveLeft(false);
            if (event.getCode() == KeyCode.RIGHT) spaceship.moveRight(false);
            if (event.getCode() == KeyCode.UP) spaceship.moveUp(false);
            if (event.getCode() == KeyCode.DOWN) spaceship.moveDown(false);
        });

        scene.setOnMouseClicked(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();

            double angle = Math.atan2(mouseY - spaceship.getY(), mouseX - spaceship.getX());
            double bulletDx = Math.cos(angle);
            double bulletDy = Math.sin(angle);

            bullets.add(new Bullet(spaceship.getX() + 7.5, spaceship.getY() + 7.5, bulletDx, bulletDy));
        });

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

                // Draw the background image
                gc.drawImage(backgroundImage, 0, 0, canvas.getWidth(), canvas.getHeight());

                // Update and draw the spaceship
                spaceship.update(canvas.getWidth(), canvas.getHeight());
                spaceship.draw(gc);

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

                // Spawn asteroids and update their positions
                if (random.nextDouble() < 0.02 && asteroids.size() < MAX_ASTEROIDS) {
                    spawnAsteroidFromRandomDirection(canvas.getWidth(), canvas.getHeight());
                }

                Iterator<Asteroid> asteroidIterator = asteroids.iterator();
                while (asteroidIterator.hasNext()) {
                    Asteroid asteroid = asteroidIterator.next();
                    asteroid.update(canvas.getWidth(), canvas.getHeight(), gc);
                    if (asteroid.isOffScreen(canvas.getWidth(), canvas.getHeight())) {
                        asteroidIterator.remove();
                    }
                }

                checkCollisions();
            }
        };

        animationTimer.start();
    }

    private void spawnAsteroidFromRandomDirection(double canvasWidth, double canvasHeight) {
        double startX = 0, startY = 0;
        double dx = 0, dy = 0;

        int side = random.nextInt(4);
        switch (side) {
            case 0:
                startX = random.nextDouble() * canvasWidth;
                startY = -30;
                dx = (random.nextDouble() - 0.5) * 2;
                dy = 1;
                break;
            case 1:
                startX = random.nextDouble() * canvasWidth;
                startY = canvasHeight + 30;
                dx = (random.nextDouble() - 0.5) * 2;
                dy = -1;
                break;
            case 2:
                startX = -30;
                startY = random.nextDouble() * canvasHeight;
                dx = 1;
                dy = (random.nextDouble() - 0.5) * 2;
                break;
            case 3:
                startX = canvasWidth + 30;
                startY = random.nextDouble() * canvasHeight;
                dx = -1;
                dy = (random.nextDouble() - 0.5) * 2;
                break;
        }

        asteroids.add(new Asteroid(startX, startY, dx, dy));
    }

    private void checkCollisions() {
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            Iterator<Asteroid> asteroidIterator = asteroids.iterator();
            while (asteroidIterator.hasNext()) {
                Asteroid asteroid = asteroidIterator.next();

                double dx = bullet.getX() - asteroid.getX();
                double dy = bullet.getY() - asteroid.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < asteroid.getSize() / 2) {
                    bulletIterator.remove();
                    asteroidIterator.remove();
                    break;
                }
            }
        }

        Iterator<Asteroid> asteroidIterator = asteroids.iterator();
        while (asteroidIterator.hasNext()) {
            Asteroid asteroid = asteroidIterator.next();

            double dx = spaceship.getX() - asteroid.getX();
            double dy = spaceship.getY() - asteroid.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance < asteroid.getSize() / 2) {
                spaceship.loseLife();
                asteroidIterator.remove();

                if (!spaceship.isAlive()) {
                    animationTimer.stop(); // Stop the game logic
                    Platform.runLater(this::showGameOverAlert); // Schedule the alert
                    break;
                }
                break;
            }
        }
    }

    // Method to show the Game Over alert
    private void showGameOverAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText("You have lost all your lives!"); // Customize the message
        alert.showAndWait(); // Show the alert
    }
}