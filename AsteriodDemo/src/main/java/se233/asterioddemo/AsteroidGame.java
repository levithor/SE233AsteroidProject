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
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.io.*;
import java.util.*;

public class AsteroidGame extends Application {

    private static final Logger logger = Logger.getLogger(AsteroidGame.class.getName());
    private long lastMissileTime = 0;
    private static final long MISSILE_COOLDOWN = 2000; // Cooldown period in milliseconds
    private int hitCount = 0;
    private static final int HITS_FOR_BOSS = 10; // Threshold for spawning a Boss


    private static final String HIGH_SCORE_FILE = "highscore.txt";
    private int highScore = 0;
    private Scene mainMenuScene;
    private Scene gameScene;
    private Pane root;
    private Canvas canvas;
    private static final double SCOUTBOT_SPAWN_DISTANCE = 500; // Configurable spawn distance
    private static final int POINTS_ASTEROID_SMALL = 100;
    private static final int POINTS_ASTEROID_MEDIUM = 50;
    private static final int POINTS_ASTEROID_LARGE = 20;
    private static final int POINTS_BOSS = 500;
    private static final int POINTS_SCOUTBOT = 250;

    private static final int MAX_ASTEROIDS = 10;

    private List<Missile> missiles = new ArrayList<>();
    private List<Explosion> explosions = new ArrayList<>();
    private List<Bullet> bullets = new ArrayList<>();
    private List<Asteroid> asteroids = new ArrayList<>();
    private List<Boss> bosses = new ArrayList<>();
    private Random random = new Random();
    private List<Scoutbot> scoutbots = new ArrayList<>();
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
        try {
            Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/se233/asterioddemo/assets/spaceBG.jpg")));
            BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, false, true);
            BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
            menuRoot.setBackground(new Background(background));
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "Background image not found", e);
        }

        // Create and position the label
        Label titleLabel = new Label("ASTEROIDZ");
        titleLabel.setLayoutX(275); // Adjust the X position as needed
        titleLabel.setLayoutY(50);  // Adjust the Y position as needed
        titleLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: orange;"); // Optional: style the label

        Button startButton = new Button("Start Game");
        startButton.setLayoutX(332);
        startButton.setLayoutY(250);
        startButton.setStyle("-fx-background-color: black;-fx-font-size:22;-fx-text-fill:white");
        startButton.setOnAction(event -> startGame(primaryStage));
        addHoverEffect(startButton);

        Button resetHighScoreButton = new Button("Reset High Score");
        resetHighScoreButton.setLayoutX(305);
        resetHighScoreButton.setLayoutY(300);
        resetHighScoreButton.setStyle("-fx-background-color: black;-fx-font-size:22;-fx-text-fill:white");
        resetHighScoreButton.setOnAction(event -> {
            highScore = 0;
            saveHighScore();
        });
        addHoverEffect(resetHighScoreButton);

        Button exitButton = new Button("Exit");
        exitButton.setLayoutX(370);
        exitButton.setLayoutY(350);
        exitButton.setStyle("-fx-background-color: black;-fx-font-size:22;-fx-text-fill:white");
        exitButton.setOnAction(event -> Platform.exit());
        addHoverEffect(exitButton);

        // Create and position the calculator button
        Button calculatorButton = new Button("Calculator");
        calculatorButton.setLayoutX(670); // Adjust the X position as needed
        calculatorButton.setLayoutY(550); // Adjust the Y position as needed
        calculatorButton.setStyle("-fx-background-color: black;-fx-font-size:22;-fx-text-fill:white");
        calculatorButton.setOnAction(event -> {
            Calculator calculator = new Calculator();
            Stage calculatorStage = new Stage();
            calculator.start(calculatorStage);
        });
        addHoverEffect(calculatorButton);

        menuRoot.getChildren().addAll(titleLabel, startButton, resetHighScoreButton, exitButton, calculatorButton);
    }

    private void addHoverEffect(Button button) {
        button.setOnMouseEntered(event -> button.setStyle("-fx-background-color: gray;-fx-font-size:22;-fx-text-fill:white"));
        button.setOnMouseExited(event -> button.setStyle("-fx-background-color: black;-fx-font-size:22;-fx-text-fill:white"));
    }

    private void startGame(Stage primaryStage) {
        root = new Pane();
        canvas = new Canvas(800, 600);
        root.getChildren().add(canvas);

        gameScene = new Scene(root);
        primaryStage.setScene(gameScene);

        // Initialize game elements and start the game loop
        initGame();
        spawnScoutbot();

        // Create a Timeline to spawn a Scoutbot every 15 seconds
        Timeline scoutbotSpawner = new Timeline(new KeyFrame(Duration.seconds(15), event -> spawnScoutbot()));
        scoutbotSpawner.setCycleCount(Timeline.INDEFINITE); // Run indefinitely
        scoutbotSpawner.play(); // Start the Timeline
    }

    private void spawnScoutbot() {
        double angle = random.nextDouble() * 10 * Math.PI; // Random angle

        double startX = spaceship.getX() + SCOUTBOT_SPAWN_DISTANCE * Math.cos(angle);
        double startY = spaceship.getY() + SCOUTBOT_SPAWN_DISTANCE * Math.sin(angle);

        scoutbots.add(new Scoutbot(startX, startY, spaceship, SCOUTBOT_SPAWN_DISTANCE));
    }

    public void initGame() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/se233/asterioddemo/assets/spaceBG.jpg")));

        spaceship = new Spaceship(400, 300);
        // Configurable spawn distance
        Scoutbot scoutbot = new Scoutbot(spaceship.getX() + SCOUTBOT_SPAWN_DISTANCE, spaceship.getY(), spaceship, SCOUTBOT_SPAWN_DISTANCE);
        scoutbots.add(scoutbot);
        scoutbot.attackPlayer(spaceship);

        gameScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.A) {
                spaceship.rotateLeft(true);
            }
            if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.D) {
                spaceship.rotateRight(true);
            }
            if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.W) {
                spaceship.moveForward(true);
            }
            if (event.getCode() == KeyCode.SPACE || event.getCode() == KeyCode.ENTER) {
                bullets.add(spaceship.createBullet());
            }
            if (event.getCode() == KeyCode.M) { // Use 'M' key for missile
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastMissileTime >= MISSILE_COOLDOWN) {
                    missiles.add(spaceship.createMissile());
                    lastMissileTime = currentTime;
                }
            }
        });

        gameScene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.A) {
                spaceship.rotateLeft(false);
            }
            if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.D) {
                spaceship.rotateRight(false);
            }
            if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.W) {
                spaceship.moveForward(false);
            }
        });


        gameScene.setOnMouseClicked(event -> {
            bullets.add(spaceship.createBullet()); //No longer tracks mouse position
        });


        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

                gc.drawImage(backgroundImage, 0, 0, canvas.getWidth(), canvas.getHeight());

                spaceship.update(canvas.getWidth(), canvas.getHeight(), gc);

                for (Scoutbot scoutbot : scoutbots) {
                    scoutbot.update(canvas.getWidth(), canvas.getHeight(), gc);
                }

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

                // Ensure constant spawning of asteroids
                while (asteroids.size() < MAX_ASTEROIDS) {
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

                Iterator<Missile> missileIterator = missiles.iterator();
                while (missileIterator.hasNext()) {
                    Missile missile = missileIterator.next();
                    missile.update();
                    if (missile.isOffScreen(canvas.getWidth(), canvas.getHeight())) {
                        missileIterator.remove();
                    } else if (missile.shouldExplode()) {
                        explosions.add(new Explosion(missile.getX(), missile.getY()));
                        handleMissileExplosion(missile);
                        missileIterator.remove();
                    } else {
                        missile.draw(gc);
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

    public void checkCollisions() {
        try {
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
                        asteroid.duplicate(asteroids);

                        hitCount++; // Increment hit count
                        logger.log(Level.INFO, "Hit count: " + hitCount);

                        if (hitCount >= HITS_FOR_BOSS) {
                            spawnBoss();
                            hitCount = 0; // Reset hit count after spawning a Boss
                        }

                        switch (asteroid.getAsteroidSize()) {
                            case SMALL:
                                scoreCount += POINTS_ASTEROID_SMALL;
                                break;
                            case MEDIUM:
                                scoreCount += POINTS_ASTEROID_MEDIUM;
                                break;
                            case LARGE:
                                scoreCount += POINTS_ASTEROID_LARGE;
                                break;
                        }

                        logger.log(Level.INFO, "Scored points. Current score: " + scoreCount);
                        explosions.add(new Explosion(asteroid.getX(), asteroid.getY()));
                        break;
                    }
                }

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
                            scoreCount += POINTS_BOSS;
                            logger.log(Level.INFO, "Scored points. Current score: " + scoreCount);

                            explosions.add(new Explosion(boss.getX(), boss.getY()));
                        }
                        break;
                    }
                }

                // Collision logic for Scoutbots
                Iterator<Scoutbot> scoutbotIterator = scoutbots.iterator();
                while (scoutbotIterator.hasNext()) {
                    Scoutbot scoutbot = scoutbotIterator.next();

                    double dx = bullet.getX() - scoutbot.getX();
                    double dy = bullet.getY() - scoutbot.getY();
                    double distance = Math.sqrt(dx * dx + dy * dy);

                    if (distance < scoutbot.getSize() / 2) {
                        bulletIterator.remove();
                        scoutbotIterator.remove();
                        scoreCount += POINTS_SCOUTBOT;
                        logger.log(Level.INFO, "Scored points. Current score: " + scoreCount);

                        explosions.add(new Explosion(scoutbot.getX(), scoutbot.getY()));
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
                    asteroidIterator.remove();
                    spaceship.loseLife();
                    logger.log(Level.INFO, "Spaceship hit by asteroid. Lives remaining: " + spaceship.getLives());

                    if (!spaceship.isAlive()) {
                        throw new GameException("Spaceship destroyed");
                    }
                }
            }

            if (scoreCount > highScore) {
                highScore = scoreCount;
                saveHighScore();
                logger.log(Level.INFO, "New high score: " + highScore);
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void handleMissileExplosion(Missile missile) {
        try {
            double explosionX = missile.getX();
            double explosionY = missile.getY();
            double explosionRadius = missile.getExplosionRadius();

            List<Asteroid> newAsteroids = new ArrayList<>();

            Iterator<Asteroid> asteroidIterator = asteroids.iterator();
            while (asteroidIterator.hasNext()) {
                Asteroid asteroid = asteroidIterator.next();

                double dx = explosionX - asteroid.getX();
                double dy = explosionY - asteroid.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < explosionRadius) {
                    asteroidIterator.remove();
                    asteroid.duplicate(newAsteroids);

                    // Add points based on asteroid size
                    switch (asteroid.getAsteroidSize()) {
                        case SMALL:
                            scoreCount += POINTS_ASTEROID_SMALL;
                            break;
                        case MEDIUM:
                            scoreCount += POINTS_ASTEROID_MEDIUM;
                            break;
                        case LARGE:
                            scoreCount += POINTS_ASTEROID_LARGE;
                            break;
                    }

                    logger.log(Level.INFO, "Scored points. Current score: " + scoreCount);
                    explosions.add(new Explosion(asteroid.getX(), asteroid.getY()));
                }

                if (missile == null) {
                    throw new GameException("Missile is null");
                }
            }

            asteroids.addAll(newAsteroids);

            Iterator<Boss> bossIterator = bosses.iterator();
            while (bossIterator.hasNext()) {
                Boss boss = bossIterator.next();

                double dx = explosionX - boss.getX();
                double dy = explosionY - boss.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < explosionRadius) {
                    boss.incrementHitCount();
                    boss.changeImageTemporarily();
                    if (boss.getHitCount() >= 10) {
                        bossIterator.remove();
                        scoreCount += POINTS_BOSS;
                        logger.log(Level.INFO, "Scored points. Current score: " + scoreCount);

                        explosions.add(new Explosion(boss.getX(), boss.getY()));
                    }
                }
            }

            Iterator<Scoutbot> scoutbotIterator = scoutbots.iterator();
            while (scoutbotIterator.hasNext()) {
                Scoutbot scoutbot = scoutbotIterator.next();

                double dx = explosionX - scoutbot.getX();
                double dy = explosionY - scoutbot.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < explosionRadius) {
                    scoutbotIterator.remove();
                    scoreCount += POINTS_SCOUTBOT;
                    logger.log(Level.INFO, "Scored points. Current score: " + scoreCount);

                    explosions.add(new Explosion(scoutbot.getX(), scoutbot.getY()));
                }
            }
        } catch (Exception e) {
            handleException(e);
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
            logger.log(Level.SEVERE, "Failed to save high score", e);
        }
    }

    private void loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGH_SCORE_FILE))) {
            String line = reader.readLine();
            if (line != null) {
                highScore = Integer.parseInt(line);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load high score", e);
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Invalid high score format", e);
        }
    }

    // This method is used to handle exceptions that occur during the game
    private void handleException(Exception e) {
        logger.log(Level.SEVERE, e.getMessage(), e);
        if (e instanceof GameException) {
            // Handle game-specific exceptions
            animationTimer.stop();
            Platform.runLater(this::gameOverOption);
        }
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public List<Asteroid> getAsteroids() {
        return asteroids;
    }

    public int getScoreCount() {
        return scoreCount;
    }

    private void gameOverOption() {
        VBox gameOverBox = new VBox(20);
        gameOverBox.setAlignment(Pos.CENTER);
        gameOverBox.setPrefSize(canvas.getWidth(), canvas.getHeight());
        gameOverBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        Label gameOverLabel = new Label("Game Over");
        gameOverLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: white;");

        Button restartButton = new Button("Restart");
        restartButton.setStyle("-fx-background-color: black;-fx-font-size:22;-fx-text-fill:white");
        restartButton.setOnAction(event -> {
            scoreCount = 0;
            spaceship.reset();
            asteroids.clear();
            bullets.clear();
            bosses.clear();
            explosions.clear();
            startGame((Stage) root.getScene().getWindow());
        });
        addHoverEffect(restartButton);

        Button returnToMenuButton = new Button("Return to Menu");
        returnToMenuButton.setStyle("-fx-background-color: black;-fx-font-size:22;-fx-text-fill:white");
        returnToMenuButton.setOnAction(event -> {
            ((Stage) root.getScene().getWindow()).setScene(mainMenuScene);
        });
        addHoverEffect(returnToMenuButton);

        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-background-color: black;-fx-font-size:22;-fx-text-fill:white");
        exitButton.setOnAction(event -> Platform.exit());
        addHoverEffect(exitButton);

        gameOverBox.getChildren().addAll(gameOverLabel, restartButton, returnToMenuButton, exitButton);
        root.getChildren().add(gameOverBox);
    }
}