import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.*;

public class TypingGame extends Application {

    private static final Map<String, String[]> WORD_CATEGORIES = new HashMap<>();
    private static final Random RANDOM = new Random();
    private static final int GAME_DURATION = 30; // seconds
    private static final int BONUS_POINTS = 5;

    private Label wordLabel;
    private TextField inputField;
    private Label timerLabel;
    private Label scoreLabel;
    private Label livesLabel;
    private Label highScoreLabel;
    private ComboBox<String> difficultyBox;
    private ComboBox<String> categoryBox;

    private String currentWord;
    private int score = 0;
    private int lives = 3;
    private int timeLeft = GAME_DURATION;
    private int highScore = 0;

    private Timeline gameTimeline;
    private TranslateTransition wordAnimation;

    public static void main(String[] args) {
        WORD_CATEGORIES.put("Fruits", new String[]{"apple", "banana", "cherry", "date", "fig", "grape"});
        WORD_CATEGORIES.put("Animals", new String[]{"cat", "dog", "elephant", "fox", "giraffe", "hippo"});
        WORD_CATEGORIES.put("Technology", new String[]{"computer", "keyboard", "monitor", "internet", "mouse"});

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        loadHighScore();

        // Initialize UI components
        wordLabel = new Label("");
        wordLabel.setFont(new Font(30));
        wordLabel.setTextFill(Color.WHITE);

        inputField = new TextField();
        inputField.setDisable(true);
        inputField.setOnAction(e -> checkInput());

        timerLabel = new Label("Time: " + GAME_DURATION);
        timerLabel.setFont(new Font(20));
        timerLabel.setTextFill(Color.WHITE);

        scoreLabel = new Label("Score: 0");
        scoreLabel.setFont(new Font(20));
        scoreLabel.setTextFill(Color.WHITE);

        livesLabel = new Label("Lives: 3");
        livesLabel.setFont(new Font(20));
        livesLabel.setTextFill(Color.WHITE);

        highScoreLabel = new Label("High Score: " + highScore);
        highScoreLabel.setFont(new Font(20));
        highScoreLabel.setTextFill(Color.WHITE);

        Button startButton = new Button("Start Game");
        startButton.setOnAction(e -> startGame());

        difficultyBox = new ComboBox<>();
        difficultyBox.getItems().addAll("Easy", "Medium", "Hard");
        difficultyBox.setValue("Easy");

        categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll(WORD_CATEGORIES.keySet());
        categoryBox.setValue("Fruits");

        VBox controls = new VBox(10, difficultyBox, categoryBox, startButton);
        controls.setStyle("-fx-alignment: center;");

        VBox stats = new VBox(10, timerLabel, scoreLabel, livesLabel, highScoreLabel);
        stats.setStyle("-fx-alignment: center;");

        Pane gamePane = new Pane();
        gamePane.setStyle("-fx-background-color: black;");

        VBox layout = new VBox(20, wordLabel, inputField, controls, stats, gamePane);
        layout.setStyle("-fx-background-color: black; -fx-padding: 20; -fx-alignment: center;");
        VBox.setVgrow(gamePane, Priority.ALWAYS);

        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setTitle("Typing Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        animateSnow(gamePane);
