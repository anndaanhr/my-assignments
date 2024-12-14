Perbesar layar
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
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BrandNewTypingGame extends Application {

    private Pane root;
    private Pane pauseMenu;
    private List<Label> fallingTexts = new ArrayList<>();
    private Label focusedText = null;
    private Label healthLabel;
    private Label scoreLabel;
    private Label playerModeLabel;
    private Random random = new Random();
    private int score = 0;
    private int health = 5;
    private int highestScore = 0;
    private double fallingSpeed = 0.5;
    private Timeline fallingTimeline;
    private Timeline spawnTimeline;
    private boolean isMultiplayer = false;
    private boolean isPaused = false;
    private boolean isWordFocused = false;
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
    
     private void styleButton(Button button) {
        button.setStyle(
            "-fx-background-color: #4CAF50;" +  // Green background
            "-fx-text-fill: white;" +           // White text
            "-fx-font-size: 14px;" +            // Font size
            "-fx-font-weight: bold;" +          // Bold text
            "-fx-padding: 10px 20px;" +         // Padding
            "-fx-background-radius: 5px;" +     // Rounded corners
            "-fx-cursor: hand;"                 // Hand cursor on hover
        );

        // Hover effect
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: #45a049;" +  // Slightly darker green on hover
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10px 20px;" +
            "-fx-background-radius: 5px;" +
            "-fx-cursor: hand;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: #4CAF50;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10px 20px;" +
            "-fx-background-radius: 5px;" +
            "-fx-cursor: hand;"
        ));
    }

   private void showMainMenu(Stage primaryStage) {
        stopAllTimelines();

        root = new Pane();
        Scene scene = new Scene(root, 800, 600); // Ganti ukuran layar
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #000000, #1a1a1a);");

        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setLayoutX(150);
        menuBox.setLayoutY(100);

        Label title = new Label("Typing Rain");
        title.setTextFill(Color.WHITE);
        title.setFont(new Font(40));

        Button singlePlayerButton = new Button("Single Player");
        singlePlayerButton.setOnAction(e -> startGame(primaryStage, false));
        styleButton(singlePlayerButton);

        Button multiplayerButton = new Button("Multiplayer");
        multiplayerButton.setOnAction(e -> startGame(primaryStage, true));
        styleButton(multiplayerButton);

        menuBox.getChildren().addAll(title, singlePlayerButton, multiplayerButton);
        root.getChildren().add(menuBox);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Typing Rain");
        primaryStage.show();
    }



     private void startGame(Stage primaryStage, boolean multiplayer) {
    // Reset semua variabel penting
    isMultiplayer = multiplayer;
    score = 0;
    health = 5;  // Pastikan direset ke 5
    fallingSpeed = 0.5;
    currentPlayer = 1;
    correctLetters = 0;
    totalTypedLetters = 0;
    fallingTexts.clear();  // Bersihkan daftar teks yang jatuh

    root = new Pane();
    Scene scene = new Scene(root, 800, 600); // Ganti ukuran layar
    root.setStyle("-fx-background-color: linear-gradient(to bottom, #000000, #1a1a1a);");

        // Bottom Info Panel
    HBox bottomPanel = new HBox(20);
    bottomPanel.setAlignment(Pos.CENTER); // Pusatkan elemen secara horizontal
    bottomPanel.setLayoutX(10);
    bottomPanel.setLayoutY(550); // Tempatkan di bagian bawah layar (disesuaikan dengan tinggi layar)

    playerModeLabel = new Label(isMultiplayer ? "Multiplayer - Player 1" : "Single Player");
    playerModeLabel.setTextFill(Color.WHITE);
    playerModeLabel.setFont(new Font(16));

    healthLabel = new Label("Health: " + health);
    healthLabel.setTextFill(Color.WHITE);
    healthLabel.setFont(new Font(16));

    scoreLabel = new Label("Score: 0");
    scoreLabel.setTextFill(Color.WHITE);
    scoreLabel.setFont(new Font(16));

    bottomPanel.getChildren().addAll(playerModeLabel, healthLabel, scoreLabel);
    root.getChildren().add(bottomPanel);


    // Start game timelines
    spawnTimeline = new Timeline(new KeyFrame(Duration.seconds(0.8), e -> spawnText()));
    spawnTimeline.setCycleCount(Timeline.INDEFINITE);
    spawnTimeline.play();

    fallingTimeline = new Timeline(new KeyFrame(Duration.millis(50), e -> updateFallingTexts(primaryStage)));
    fallingTimeline.setCycleCount(Timeline.INDEFINITE);
    fallingTimeline.play();

    setupPauseMenu(primaryStage);
    scene.setOnKeyPressed(event -> handleKeyPress(event, primaryStage));
    scene.setOnKeyTyped(this::handleTyping);

    primaryStage.setScene(scene);
    primaryStage.setTitle("Typing Rain");
    primaryStage.show();
}

    
    private void setupPauseMenu(Stage primaryStage) {
        pauseMenu = new Pane();
        pauseMenu.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");
        pauseMenu.setVisible(false);

        VBox pauseBox = new VBox(20);
        pauseBox.setAlignment(Pos.CENTER);
        pauseBox.setLayoutX(150);
        pauseBox.setLayoutY(100);

        Label pauseLabel = new Label("Game Paused");
        pauseLabel.setTextFill(Color.WHITE);
        pauseLabel.setFont(new Font(30));

        Button resumeButton = new Button("Resume Game");
        resumeButton.setOnAction(e -> resumeGame());
        styleButton(resumeButton);

        Button restartButton = new Button("Restart Game");
        restartButton.setOnAction(e -> restartGame(primaryStage));
        styleButton(restartButton);

        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.setOnAction(e -> {
            stopAllTimelines();
            fallingTexts.clear();
            focusedText = null;
            root.getChildren().clear();
            showMainMenu(primaryStage);
        });
        styleButton(mainMenuButton);

        pauseBox.getChildren().addAll(pauseLabel, resumeButton, restartButton, mainMenuButton);
        pauseMenu.getChildren().add(pauseBox);
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

    // Reset semua variabel
    score = 0;
    health = 5;  // Reset ke 5 di sini juga
    fallingSpeed = 0.5;
    correctLetters = 0;
    totalTypedLetters = 0;

    // Bersihkan dan reset elemen permainan
    fallingTexts.clear();
    root.getChildren().clear();
    focusedText = null;
    isWordFocused = false;

    // Mulai ulang permainan dengan mode yang sama
    startGame(primaryStage, isMultiplayer);
}


   private void updateFallingTexts(Stage primaryStage) {
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
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + score);
        }
    }
 
    private void updatePlayerModeLabel() {
        if (playerModeLabel != null && isMultiplayer) {
            playerModeLabel.setText("Multiplayer - Player " + currentPlayer);
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
    stopAllTimelines();
    highestScore = Math.max(highestScore, score);

    if (isMultiplayer) {
        if (currentPlayer == 1) {
            // Store Player 1's score dan reset health untuk player 2
            player1Score = score;
            currentPlayer = 2;
            health = 5;  // Reset health sebelum memulai giliran player 2
            startGame(primaryStage, true);
        } else {
            // Store Player 2's score dan tampilkan hasil
            player2Score = score;
            showMultiplayerResults(primaryStage);
        }
    } else {
        // Single-player: Tampilkan layar game over
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
        
        Button restartButton = new Button("Restart Game");
        restartButton.setLayoutX(200);
        restartButton.setLayoutY(300);
        restartButton.setOnAction(e -> {
            stopAllTimelines();
            clearGameElements();
            startGame(primaryStage, isMultiplayer);
        });
        styleButton(restartButton);

        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.setLayoutX(350);
        mainMenuButton.setLayoutY(300);
        mainMenuButton.setOnAction(e -> {
            stopAllTimelines();
            clearGameElements();
            resetScene(primaryStage);
            showMainMenu(primaryStage);
        });
        styleButton(mainMenuButton);

        root.getChildren().addAll(resultsLabel, player1ScoreLabel, player2ScoreLabel, winnerLabel, restartButton, mainMenuButton);
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

        Button restartButton = new Button("Restart Game");
        restartButton.setLayoutX(200);
        restartButton.setLayoutY(300);
        restartButton.setOnAction(e -> {
            stopAllTimelines();
            startGame(primaryStage, isMultiplayer);
        });
        styleButton(restartButton);

        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.setLayoutX(350);
        mainMenuButton.setLayoutY(300);
        mainMenuButton.setOnAction(e -> {
            stopAllTimelines();
            showMainMenu(primaryStage);
        });
        styleButton(mainMenuButton);

        // Tambahkan event handler untuk Escape key
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                stopAllTimelines();
                showMainMenu(primaryStage);
            }
        });

        root.getChildren().addAll(gameOverLabel, finalScoreLabel, highestScoreLabel, accuracyLabel, restartButton, mainMenuButton);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


   public static void main(String[] args) {
        launch(args);
    }
}
