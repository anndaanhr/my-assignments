import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BrandNewTypingGame extends Application {

    private Pane root;
    private Pane pauseMenu;
    private List<Label> fallingTexts = new ArrayList<>();
    private Label focusedText = null;
    private Label healthLabel;
    private Random random = new Random();
    private int score = 0;
    private int health = 5;
    private int highestScore = 0;
    private double fallingSpeed = 0.5;
    private Timeline fallingTimeline;
    private Timeline spawnTimeline;
    private boolean isMultiplayer = false;
    private boolean isPaused = false;
    private boolean isWordFocused = false;  // Flag to track if a word is being focused on
    private int currentPlayer = 1;
    private int player1Score = 0;
    private int player2Score = 0;
    private int correctLetters = 0;
    private int totalTypedLetters = 0;


    @Override
    public void start(Stage primaryStage) {
        showMainMenu(primaryStage);

        // Ensure the app closes cleanly
        primaryStage.setOnCloseRequest(e -> {
            stopAllTimelines(); // Stop all timelines
            Platform.exit();    // Exit JavaFX application
            System.exit(0);     // Ensure JVM shuts down
        });
    }


    private void showMainMenu(Stage primaryStage) {
        stopAllTimelines(); // Stop timelines before transitioning

        root = new Pane();
        Scene scene = new Scene(root, 600, 400);
        root.setStyle("-fx-background-color: black;");

        Label title = new Label("Typing Game");
        title.setTextFill(Color.WHITE);
        title.setFont(new Font(30));
        title.setLayoutX(200);
        title.setLayoutY(50);

        Button singlePlayerButton = new Button("Single Player");
        singlePlayerButton.setLayoutX(250);
        singlePlayerButton.setLayoutY(150);
        singlePlayerButton.setOnAction(e -> startGame(primaryStage, false));

        Button multiplayerButton = new Button("Multiplayer");
        multiplayerButton.setLayoutX(250);
        multiplayerButton.setLayoutY(200);
        multiplayerButton.setOnAction(e -> startGame(primaryStage, true));

        root.getChildren().addAll(title, singlePlayerButton, multiplayerButton);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Typing Game Main Menu");
        primaryStage.show();
    }


    private void startGame(Stage primaryStage, boolean multiplayer) {
        isMultiplayer = multiplayer;
        score = 0;
        health = 5;
        fallingSpeed = 0.5;

        correctLetters = 0;
        totalTypedLetters = 0;

        root = new Pane();
        Scene scene = new Scene(root, 600, 400);
        root.setStyle("-fx-background-color: black;");

        root.getChildren().clear();  // Clears previous children
        // Add your UI components again (including healthLabel, etc.)
        healthLabel = new Label("Health: 5");
        healthLabel.setTextFill(Color.WHITE);
        healthLabel.setFont(new Font(20));
        healthLabel.setLayoutX(10);
        healthLabel.setLayoutY(40);
        if (!root.getChildren().contains(healthLabel)) {
            root.getChildren().add(healthLabel);
        }


        spawnTimeline = new Timeline(new KeyFrame(Duration.seconds(0.8), e -> spawnText()));
        spawnTimeline.setCycleCount(Timeline.INDEFINITE);
        spawnTimeline.play();

        fallingTimeline = new Timeline(new KeyFrame(Duration.millis(50), e -> updateFallingTexts(healthLabel, primaryStage)));
        fallingTimeline.setCycleCount(Timeline.INDEFINITE);
        fallingTimeline.play();

        setupPauseMenu(primaryStage);
        scene.setOnKeyPressed(event -> handleKeyPress(event, primaryStage));
        scene.setOnKeyTyped(this::handleTyping);

        primaryStage.setScene(scene);
        primaryStage.setTitle(isMultiplayer ? "Typing Game - Player " + currentPlayer : "Typing Game");
        primaryStage.show();
    }

    
    
    private void setupPauseMenu(Stage primaryStage) {
        pauseMenu = new Pane();
        pauseMenu.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");
        pauseMenu.setVisible(false);

        Label pauseLabel = new Label("Game Paused");
        pauseLabel.setTextFill(Color.WHITE);
        pauseLabel.setFont(new Font(30));
        pauseLabel.setLayoutX(200);
        pauseLabel.setLayoutY(100);

        Button resumeButton = new Button("Resume Game");
        resumeButton.setLayoutX(250);
        resumeButton.setLayoutY(180);
        resumeButton.setOnAction(e -> resumeGame());

        Button resetButton = new Button("Restart Game");
        resetButton.setLayoutX(250);
        resetButton.setLayoutY(230);
        resetButton.setOnAction(e -> restartGame(primaryStage));

        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.setLayoutX(250);
        mainMenuButton.setLayoutY(300);
        mainMenuButton.setOnAction(e -> {
            System.out.println("Main Menu button clicked.");
            stopAllTimelines();
            fallingTexts.clear();
            focusedText = null;
            root.getChildren().clear();
            showMainMenu(primaryStage);
        });



        pauseMenu.getChildren().addAll(pauseLabel, resumeButton, resetButton, mainMenuButton);
        pauseMenu.setPrefSize(600, 400);
        root.getChildren().add(pauseMenu);
    }
    
    
    private void handleKeyPress(KeyEvent event, Stage primaryStage) {
        if (event.getCode() == KeyCode.ESCAPE) {
            if (!isPaused) {
                pauseGame();
            } else {
                resumeGame();
            }
        }
    }


    private void spawnText() {
        String[] words = {"java", "code", "typing", "game", "fun", "random", "fast", "react", "keyboard", "rain"};
        String randomWord = words[random.nextInt(words.length)];
        Label text = new Label(randomWord);
        text.setTextFill(Color.GREEN);
        text.setFont(new Font(18));
        text.setLayoutX(random.nextInt((int) root.getWidth() - 50));
        text.setLayoutY(0);
        fallingTexts.add(text);
        root.getChildren().add(text);
    }
    
    
    private void pauseGame() {
        isPaused = true;
        spawnTimeline.pause();
        fallingTimeline.pause();
        pauseMenu.setVisible(true);
    }

    private void resumeGame() {
        isPaused = false;
        spawnTimeline.play();
        fallingTimeline.play();
        pauseMenu.setVisible(false);
    }


    private void restartGame(Stage primaryStage) {
        // Hentikan semua Timeline
        stopAllTimelines();

        // Bersihkan elemen permainan
        fallingTexts.clear();
        root.getChildren().clear();
        focusedText = null;
        isWordFocused = false;

        // Reset variabel permainan
        score = 0;
        health = 5;
        fallingSpeed = 0.5;
        correctLetters = 0;
        totalTypedLetters = 0;

        // Mulai ulang permainan
        startGame(primaryStage, isMultiplayer);
    }


    private void updateFallingTexts(Label healthLabel, Stage primaryStage) {
        List<Label> toRemove = new ArrayList<>();
        for (Label text : fallingTexts) {
            text.setLayoutY(text.getLayoutY() + fallingSpeed);
            if (text.getLayoutY() > root.getHeight()) {
                toRemove.add(text);
                health--;
                healthLabel.setText("Health: " + health);
                if (health <= 0) {
                    endGame(primaryStage);
                    return;
                }
            }
        }
        for (Label text : toRemove) {
            fallingTexts.remove(text);
            root.getChildren().remove(text);
        }
    }

    
    private void stopAllTimelines() {
        if (spawnTimeline != null) {
            spawnTimeline.stop();
            spawnTimeline = null; // Dereference to avoid lingering
        }
        if (fallingTimeline != null) {
            fallingTimeline.stop();
            fallingTimeline = null; // Dereference to avoid lingering
        }
        System.out.println("All timelines stopped.");
    }



    private void handleTyping(KeyEvent event) {
        String typed = event.getCharacter().trim();  // Get the typed character and trim any extra spaces

        if (!typed.isEmpty()) {
            totalTypedLetters++;  // Increment total typed letters
            if (focusedText != null && focusedText.getText().startsWith(typed)) {
                correctLetters++;  // Increment correct letters if the typed character matches
            }

            if (focusedText != null) {
                String currentWord = focusedText.getText();
                if (currentWord.startsWith(typed)) {
                    focusedText.setText(currentWord.substring(1));  // Remove the first character
                    if (focusedText.getText().isEmpty()) {
                        fallingTexts.remove(focusedText);  // Remove the word from the list
                        root.getChildren().remove(focusedText);  // Remove the word from the UI
                        focusedText = null;  // Clear the focus
                        score += 1;  // Increase the score
                        fallingSpeed += 0.05;  // Increase falling speed slightly
                        updateScoreLabel();  // Update the score label
                        isWordFocused = false;  // Unlock the focus after completing the word
                    }
                }
            } else {
                for (Label text : fallingTexts) {
                    if (text.getText().startsWith(typed) && !isWordFocused) {
                        focusedText = text;
                        text.setText(text.getText().substring(1));  // Remove the first character immediately
                        focusedText.setTextFill(Color.BLUE);  // Indicate focus
                        isWordFocused = true;

                        if (focusedText.getText().isEmpty()) {
                            fallingTexts.remove(focusedText);
                            root.getChildren().remove(focusedText);
                            focusedText = null;
                            score += 1;
                            fallingSpeed += 0.05;
                            updateScoreLabel();
                            isWordFocused = false;
                        }
                        break;
                    }
                }
            }
        }
    }


    private void updateScoreLabel() {
        for (javafx.scene.Node node : root.getChildren()) {
            if (node instanceof Label && ((Label) node).getText().startsWith("Score:")) {
                ((Label) node).setText("Score: " + score);
            }
        }
    }

    private void adjustLayout(Scene scene) {
        for (Label text : fallingTexts) {
            text.setLayoutX(random.nextInt((int) scene.getWidth() - 50));
        }
    }
    
    
    private void clearGameElements() {
        fallingTexts.clear();
        root.getChildren().removeIf(node -> node instanceof Label || node instanceof Button);
        focusedText = null;
        isWordFocused = false;
        System.out.println("Game elements cleared.");
    }


    private void endGame(Stage primaryStage) {
        spawnTimeline.stop();
        fallingTimeline = new Timeline(new KeyFrame(Duration.millis(50), e -> updateFallingTexts(healthLabel, primaryStage)));
        highestScore = Math.max(highestScore, score);

        if (isMultiplayer) {
            if (currentPlayer == 1) {
                // Store Player 1's score and switch to Player 2
                player1Score = score;
                currentPlayer = 2;
                startGame(primaryStage, true);
            } else {
                // Store Player 2's score and display results
                player2Score = score;
                showMultiplayerResults(primaryStage);
            }
        } else {
            // Single-player: Show game over screen
            showGameOverScreen(primaryStage);
        }
    }
    
    
    private void resetScene(Stage primaryStage) {
        root.getChildren().clear(); // Clear all UI elements
        root = new Pane(); // Reset the root container
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene); // Apply the new scene
        System.out.println("Scene reset.");
    }

     
    private void showMultiplayerResults(Stage primaryStage) {
        root = new Pane();
        Scene scene = new Scene(root, 600, 400);
        root.setStyle("-fx-background-color: black;");

        Label resultsLabel = new Label("Multiplayer Results");
        resultsLabel.setFont(new Font(30));
        resultsLabel.setTextFill(Color.WHITE);
        resultsLabel.setLayoutX(150);
        resultsLabel.setLayoutY(50);

        Label player1ScoreLabel = new Label("Player 1 Score: " + player1Score);
        player1ScoreLabel.setFont(new Font(20));
        player1ScoreLabel.setTextFill(Color.WHITE);
        player1ScoreLabel.setLayoutX(150);
        player1ScoreLabel.setLayoutY(150);

        Label player2ScoreLabel = new Label("Player 2 Score: " + player2Score);
        player2ScoreLabel.setFont(new Font(20));
        player2ScoreLabel.setTextFill(Color.WHITE);
        player2ScoreLabel.setLayoutX(150);
        player2ScoreLabel.setLayoutY(200);

        String winner = player1Score > player2Score ? "Player 1 Wins!" : player2Score > player1Score ? "Player 2 Wins!" : "It's a Tie!";
        Label winnerLabel = new Label(winner);
        winnerLabel.setFont(new Font(20));
        winnerLabel.setTextFill(Color.WHITE);
        winnerLabel.setLayoutX(150);
        winnerLabel.setLayoutY(250);
        
        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.setLayoutX(250);
        mainMenuButton.setLayoutY(300);
        mainMenuButton.setOnAction(e -> {
            stopAllTimelines();
            clearGameElements();
            resetScene(primaryStage);
            showMainMenu(primaryStage);
        });

        root.getChildren().addAll(resultsLabel, player1ScoreLabel, player2ScoreLabel, winnerLabel, mainMenuButton);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void showGameOverScreen(Stage primaryStage) {
        root = new Pane();
        Scene scene = new Scene(root, 600, 400);
        root.setStyle("-fx-background-color: black;");

        Label gameOverLabel = new Label("Game Over!");
        gameOverLabel.setFont(new Font(30));
        gameOverLabel.setTextFill(Color.WHITE);
        gameOverLabel.setLayoutX(200);
        gameOverLabel.setLayoutY(50);

        Label finalScoreLabel = new Label("Score: " + score);
        finalScoreLabel.setFont(new Font(20));
        finalScoreLabel.setTextFill(Color.WHITE);
        finalScoreLabel.setLayoutX(200);
        finalScoreLabel.setLayoutY(120);

        highestScore = Math.max(highestScore, score);
        Label highestScoreLabel = new Label("Highest Score: " + highestScore);
        highestScoreLabel.setFont(new Font(20));
        highestScoreLabel.setTextFill(Color.WHITE);
        highestScoreLabel.setLayoutX(200);
        highestScoreLabel.setLayoutY(170);

        double accuracy = (totalTypedLetters == 0) ? 0 : (correctLetters / (double) totalTypedLetters) * 100;
        Label accuracyLabel = new Label(String.format("Accuracy: %.2f%%", accuracy));
        accuracyLabel.setFont(new Font(20));
        accuracyLabel.setTextFill(Color.WHITE);
        accuracyLabel.setLayoutX(200);
        accuracyLabel.setLayoutY(220);

        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.setLayoutX(250);
        mainMenuButton.setLayoutY(300);
        mainMenuButton.setOnAction(e -> {
            stopAllTimelines();
            showMainMenu(primaryStage);
        });

        // Tambahkan event handler untuk Escape key
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                stopAllTimelines();
                showMainMenu(primaryStage);
            }
        });

        root.getChildren().addAll(gameOverLabel, finalScoreLabel, highestScoreLabel, accuracyLabel, mainMenuButton);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
