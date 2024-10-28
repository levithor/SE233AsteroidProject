package se233.asterioddemo;

import javafx.application.Platform;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import java.util.logging.Logger;
import java.util.logging.Level;

import java.io.*;
import java.util.*;

public class AsteroidGame extends Application {

    private static final Logger logger = Logger.getLogger(AsteroidGame.class.getName());

    private static final String HIGH_SCORE_FILE = "highscore.txt";
    private int highScore = 0;
    private Scene mainMenuScene;
    private Scene gameScene;
    private Pane root;
    private Canvas canvas;

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
        primaryStage.setTitle("Asteroid Game");

        // Load the high score at the start of the game
        loadHighScore();

        // Initialize main menu
        initMainMenu(primaryStage);

        primaryStage.setScene(mainMenuScene);
        primaryStage.show();
    }

    private void initMainMenu(Stage primaryStage) {
        Pane menuRoot = new Pane();
        mainMenuScene = new Scene(menuRoot, 800, 600);

        // Load the background image
        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/se233/asterioddemo/assets/spaceBG.jpg")));
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, false, true);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        menuRoot.setBackground(new Background(background));

        // Create and position the label
        Label titleLabel = new Label("ASTEROIDZ");
        titleLabel.setLayoutX(275); // Adjust the X position as needed
        titleLabel.setLayoutY(50);  // Adjust the Y position as needed
        titleLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: orange;"); // Optional: style the label

        Button startButton = new Button("Start Game");
        startButton.setLayoutX(350);
        startButton.setLayoutY(250);
        startButton.setOnAction(event -> startGame(primaryStage));

        Button resetHighScoreButton = new Button("Reset High Score");
        resetHighScoreButton.setLayoutX(350);
        resetHighScoreButton.setLayoutY(300);
        resetHighScoreButton.setOnAction(event -> {
            highScore = 0;
            saveHighScore();
        });

        Button exitButton = new Button("Exit");
        exitButton.setLayoutX(350);
        exitButton.setLayoutY(350);
        exitButton.setOnAction(event -> Platform.exit());

        menuRoot.getChildren().addAll(titleLabel, startButton, resetHighScoreButton, exitButton);
    }

    private void startGame(Stage primaryStage) {
        root = new Pane();
        canvas = new Canvas(800, 600);
        root.getChildren().add(canvas);

        gameScene = new Scene(root);
        primaryStage.setScene(gameScene);

        // Initialize game elements and start the game loop
        initGame();
    }

    private void initGame() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/se233/asterioddemo/assets/spaceBG.jpg")));

        spaceship = new Spaceship(400, 300);

        gameScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) spaceship.moveLeft(true);
            if (event.getCode() == KeyCode.RIGHT) spaceship.moveRight(true);
            if (event.getCode() == KeyCode.UP) spaceship.moveUp(true);
            if (event.getCode() == KeyCode.DOWN) spaceship.moveDown(true);
        });

        gameScene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.LEFT) spaceship.moveLeft(false);
            if (event.getCode() == KeyCode.RIGHT) spaceship.moveRight(false);
            if (event.getCode() == KeyCode.UP) spaceship.moveUp(false);
            if (event.getCode() == KeyCode.DOWN) spaceship.moveDown(false);
        });

        gameScene.setOnMouseClicked(event -> {
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

                Iterator<Boss> bossIterator = bosses.iterator();
                while (bossIterator.hasNext()) {
                    Boss boss = bossIterator.next();
                    boss.update(canvas.getWidth(), canvas.getHeight(), gc);
                    if (boss.isOffScreen(canvas.getWidth(), canvas.getHeight())) {
                        bossIterator.remove();
                    }
                }

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

                gc.setFill(Color.VIOLET);
                gc.fillText("Score: " + scoreCount, 10, 20);
                gc.fillText("Lives: " + spaceship.getLives(), 10, 40);
                gc.fillText("High Score: " + highScore, 10, 60);
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
                    logger.log(Level.INFO, "Scored a point. Current score: " + scoreCount);

                    // Add explosion
                    explosions.add(new Explosion(asteroid.getX(), asteroid.getY()));

                    // Spawn a Boss if scoreCount is divisible by 10
                    if (scoreCount % 10 == 0) {
                        spawnBoss();
                    }
                    break;
                }
            }

            // Check collision with Boss
            Iterator<Boss> bossIterator = bosses.iterator();
            while (bossIterator.hasNext()) {
                Boss boss = bossIterator.next();

                double dx = bullet.getX() - boss.getX();
                double dy = bullet.getY() - boss.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < boss.getSize() / 2) {
                    bulletIterator.remove();
                    boss.incrementHitCount();
                    boss.changeImageTemporarily();
                    if (boss.getHitCount() >= 10) {
                        bossIterator.remove();
                        scoreCount++; // Increment the collision counter
                        logger.log(Level.INFO, "Scored a point. Current score: " + scoreCount);

                        // Add explosion
                        explosions.add(new Explosion(boss.getX(), boss.getY()));
                    }
                    break;
                }
            }
        }

        if (scoreCount > highScore) {
            highScore = scoreCount;
            saveHighScore();
            logger.log(Level.INFO, "New high score: " + highScore);
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
                    Platform.runLater(this::gameOverOption);
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

    private void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE))) {
            writer.write(String.valueOf(highScore));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGH_SCORE_FILE))) {
            String line = reader.readLine();
            if (line != null) {
                highScore = Integer.parseInt(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void gameOverOption() {
        VBox gameOverBox = new VBox(20);
        gameOverBox.setAlignment(Pos.CENTER);
        gameOverBox.setPrefSize(canvas.getWidth(), canvas.getHeight());
        gameOverBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        Label gameOverLabel = new Label("Game Over");
        gameOverLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: white;");

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(event -> {
            scoreCount = 0;
            spaceship.reset();
            asteroids.clear();
            bullets.clear();
            bosses.clear();
            explosions.clear();
            startGame((Stage) root.getScene().getWindow());
        });

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(event -> Platform.exit());

        gameOverBox.getChildren().addAll(gameOverLabel, restartButton, exitButton);
        root.getChildren().add(gameOverBox);
    }
}