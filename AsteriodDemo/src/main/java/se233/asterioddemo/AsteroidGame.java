package se233.asterioddemo;

import javafx.application.Platform;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.*;

public class AsteroidGame extends Application {

    private static final int MAX_ASTEROIDS = 10;

    private List<Explosion> explosions = new ArrayList<>();
    private List<Bullet> bullets = new ArrayList<>();
    private List<Asteroid> asteroids = new ArrayList<>();
    private List<Boss> bosses = new ArrayList<>();
    private Random random = new Random();

    private Spaceship spaceship;
    private AnimationTimer animationTimer;
    private Image backgroundImage;
    private int scoreCount = 0; // Counter for collisions

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

        backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/se233/asterioddemo/assets/spaceBG.jpg")));

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

                gc.drawImage(backgroundImage, 0, 0, canvas.getWidth(), canvas.getHeight());

                spaceship.update(canvas.getWidth(), canvas.getHeight(), gc);

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

                // Update and render Boss instances
                Iterator<Boss> bossIterator = bosses.iterator();
                while (bossIterator.hasNext()) {
                    Boss boss = bossIterator.next();
                    boss.update(canvas.getWidth(), canvas.getHeight(), gc);
                    if (boss.isOffScreen(canvas.getWidth(), canvas.getHeight())) {
                        bossIterator.remove();
                    }
                }

                // Update and render explosions
                Iterator<Explosion> explosionIterator = explosions.iterator();
                while (explosionIterator.hasNext()) {
                    Explosion explosion = explosionIterator.next();
                    explosion.update();
                    if (explosion.isFinished()) {
                        explosionIterator.remove();
                    } else {
                        explosion.draw(gc);
                    }
                }

                checkCollisions();

                // Display the collision count
                gc.setFill(Color.WHITE);
                gc.fillText("Score: " + scoreCount, 10, 20);

                // Display the lives count
                gc.fillText("Lives: " + spaceship.getLives(), 10, 40);
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

        // Randomly select an asteroid size
        Asteroid.Size[] sizes = Asteroid.Size.values();
        Asteroid.Size size = sizes[random.nextInt(sizes.length)];

        asteroids.add(new Asteroid(startX, startY, dx, dy, size));
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
                    asteroid.duplicate(asteroids); // Duplicate the asteroid
                    scoreCount++; // Increment the collision counter

                    // Add explosion
                    explosions.add(new Explosion(asteroid.getX(), asteroid.getY()));

                    // Spawn a Boss if scoreCount is divisible by 10
                    if (scoreCount % 10 == 0) {
                        spawnBoss();
                    }
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
                    animationTimer.stop();
                    Platform.runLater(this::showGameOverAlert);
                    break;
                }
                break;
            }
        }
    }

    private void spawnBoss() {
        double startX = 400; // Middle of the window (assuming window width is 800)
        double startY = -60; // Start from the top of the window
        double dx = 0; //
        double dy = 1; // Move downwards

        bosses.add(new Boss(startX, startY, dx, dy));
    }

    private void showGameOverAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText("You have lost all your lives!");
        alert.showAndWait();
    }
}