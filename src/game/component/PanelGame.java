package game.component;

import Authentication.DatabaseConnection;
import Authentication.ScoreboardForm;
import game.audio.AudioPlayer;
import game.object.Banana;
import game.object.Bullet;
import game.object.Player;
import game.object.Rocket;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class PanelGame extends JComponent {

    private Graphics2D g2;
    private BufferedImage image;
    private int width;
    private int height;
    private Thread thread;
    private boolean start = true;
    private Key key;
    private int shotTime;
    // FPS for game rendering
    private final int FPS = 60;
    private final int Target_Time = 1000000000 / FPS; // Corrected to nanoseconds
    
    //Game Object
    private Player player;
    private List<Bullet> bullets;
    private List<Star> stars;
    private static final int NUM_STARS = 200;
    private List<Rocket> rockets;
    private List<Banana> bananas;
    private int rocketSpawnTime = 0;
    private final int ROCKET_SPAWN_RATE = 180;
    private int bananaSpawnTime = 3000; // Start with a delay before first banana spawns
    private final int BANANA_SPAWN_RATE = 6000; // Spawn bananas once per minute (approx)
    private int score = 0;
    private int highScore = 0;
    private Font scoreFont = new Font("Arial", Font.BOLD, 20);
    private boolean isPaused = false;
    private boolean isGameOver = false;
    private boolean isMuted = false;
    private boolean showingQuestion = false;
    private AudioPlayer audioPlayer;
    private String currentQuestion = "";
    private String[] currentAnswers = new String[4];
    private int correctAnswerIndex = 0;
    private JDialog questionDialog = null;
    
    // Retry with banana fields
    private boolean hasUsedBananaRetry = false;
    private Rectangle bananaRetryButtonBounds = new Rectangle();
    private boolean hasMouseListener = false;
    private int currentUserId = -1; // Will need to be set from outside
    
    public void start() {
        width = getWidth();
        height = getHeight();
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        // Initialize game objects
        initObjectGame();
        initStars();
        initKeyboard();
        initBullets();
        initRockets();
        initBananas();
        
        // Start game loop thread
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (start) {
                    long startTime = System.nanoTime();
                    
                    if (!isPaused && !isGameOver) {
                        drawBackground();
                        drawGame();
                    } else if (isGameOver) {
                        drawBackground();
                        drawGame();
                        drawGameOverScreen();
                    } else {
                        drawPauseMenu();
                    }
                    
                    render();
                    
                    long time = System.nanoTime() - startTime;
                    if (time < Target_Time) {
                        long sleepTime = (Target_Time - time) / 1000000;
                        sleep(sleepTime);
                    }
                }
            }
        });
        thread.start();
    }
    
    public void setAudioPlayer(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }
    
    // Set the current user ID from login
    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }
    
    private void initStars() {
        stars = new ArrayList<>();
        for (int i = 0; i < NUM_STARS; i++) {
            stars.add(new Star(width, height));
        }
    }
    
    public void addScore(int points) {
        score += points;
        if (score > highScore) {
            highScore = score;
        }
    }
    
    private void initRockets() {
        rockets = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (start) {
                    if (!isPaused && !isGameOver && !showingQuestion) {
                        // Spawn new rockets periodically
                        rocketSpawnTime++;
                        if (rocketSpawnTime >= ROCKET_SPAWN_RATE) {
                            spawnRocket();
                            rocketSpawnTime = 0;
                        }
                        
                        // Update existing rockets
                        for (int i = 0; i < rockets.size(); i++) {
                            Rocket rocket = rockets.get(i);
                            if (rocket != null) {
                                rocket.update(player.getX(), player.getY());
                                
                                // Check if rocket is out of bounds
                                if (!rocket.check(width, height)) {
                                    rockets.remove(rocket);
                                    continue;
                                }
                                
                                // Check for collision with player
                                if (rocket.checkCollision(player.getX(), player.getY())) {
                                    // Check if player has active shield
                                    if (player.hasActiveShield()) {
                                        // Shield absorbs the hit
                                        boolean shieldDestroyed = !player.shieldAbsorbHit();
                                        rockets.remove(rocket);
                                        
                                        if (shieldDestroyed) {
                                            // Shield was destroyed by this hit
                                            // Could add visual effect here
                                        }
                                    } else {
                                        // No shield, damage player directly
                                        player.damage(20);
                                        rockets.remove(rocket);
                                        
                                        // Check if player died
                                        if (!player.isAlive()) {
                                            showGameOver();
                                        }
                                    }
                                    continue;
                                }
                                
                                // Check for collision with bullets
                                for (int j = 0; j < bullets.size(); j++) {
                                    Bullet bullet = bullets.get(j);
                                    if (bullet != null) {
                                        double bulletCenterX = bullet.getX() + bullet.getSize()/2;
                                        double bulletCenterY = bullet.getY() + bullet.getSize()/2;
                                        double rocketCenterX = rocket.getX() + 16;
                                        double rocketCenterY = rocket.getY() + 16;
                                        
                                        double distance = Math.sqrt(
                                            Math.pow(bulletCenterX - rocketCenterX, 2) + 
                                            Math.pow(bulletCenterY - rocketCenterY, 2)
                                        );
                                        
                                        if (distance < 20) { // Bullet hit rocket
                                            rockets.remove(rocket);
                                            bullets.remove(bullet);
                                            addScore(10);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    sleep(10);
                }
            }
        }).start();
    }
    
    // Method to spawn a new rocket at a random position on the edge of the screen
    private void spawnRocket() {
        int side = (int)(Math.random() * 4); // 0=top, 1=right, 2=bottom, 3=left
        double x, y;
        
        switch (side) {
            case 0: // Top
                x = Math.random() * width;
                y = -32;
                break;
            case 1: // Right
                x = width;
                y = Math.random() * height;
                break;
            case 2: // Bottom
                x = Math.random() * width;
                y = height;
                break;
            case 3: // Left
                x = -32;
                y = Math.random() * height;
                break;
            default:
                x = -32;
                y = -32;
        }
        
        rockets.add(new Rocket(x, y));
    }
    
    private void initBananas() {
        bananas = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (start) {
                    if (!isPaused && !isGameOver && !showingQuestion) {
                        // Spawn new bananas periodically
                        bananaSpawnTime++;
                        if (bananaSpawnTime >= BANANA_SPAWN_RATE) {
                            spawnBanana();
                            bananaSpawnTime = 0;
                        }
                        
                        // Update existing bananas
                        for (int i = 0; i < bananas.size(); i++) {
                            Banana banana = bananas.get(i);
                            if (banana != null) {
                                banana.update();
                                
                                // Check for collision with player
                                if (banana.checkCollision(player.getX(), player.getY())) {
                                    // Remove the banana
                                    bananas.remove(banana);
                                    
                                    // Show question from API
                                    fetchQuestionFromAPI(false);
                                    
                                    break;
                                }
                            }
                        }
                    }
                    sleep(10);
                }
            }
        }).start();
    }
    
    // Method to spawn a banana
    private void spawnBanana() {
        // Spawn from random edge similar to rockets
        int side = (int)(Math.random() * 4);
        double x, y;
        
        switch (side) {
            case 0: // Top
                x = Math.random() * width;
                y = -32;
                break;
            case 1: // Right
                x = width;
                y = Math.random() * height;
                break;
            case 2: // Bottom
                x = Math.random() * width;
                y = height;
                break;
            case 3: // Left
                x = -32;
                y = Math.random() * height;
                break;
            default:
                x = -32;
                y = -32;
        }
        
        bananas.add(new Banana(x, y));
    }
    
    // Method to fetch question from API - Updated to use CustomJsonParser with improved error handling
    private void fetchQuestionFromAPI(boolean isRetryAttempt) {
        showingQuestion = true;
        isPaused = true;
        
        new Thread(() -> {
            try {
                // Make API request to the Banana API
                URL url = new URL("https://marcconrad.com/uob/banana/api.php?out=json&base64=no");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000); // Set timeout to 5 seconds
                conn.setReadTimeout(5000);    // Set read timeout to 5 seconds
                
                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    throw new Exception("HTTP error code: " + responseCode);
                }
                
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                
                // Debug: print the raw response to help diagnose issues
                System.out.println("API Response: " + response.toString());
                
                // Parse JSON response using CustomJsonParser
                Map<String, Object> jsonResponse = CustomJsonParser.parseJson(response.toString());
                
                // Check if required fields exist
                if (!jsonResponse.containsKey("question") || !jsonResponse.containsKey("solution")) {
                    throw new Exception("API response missing required fields");
                }
                
                currentQuestion = CustomJsonParser.getString(jsonResponse, "question");
                currentAnswers = new String[4];
                for (int i = 0; i < 4; i++) {
                    currentAnswers[i] = String.valueOf(i);
                }
                correctAnswerIndex = CustomJsonParser.getInt(jsonResponse, "solution");
                
                // Show question dialog on EDT
                SwingUtilities.invokeLater(() -> {
                    if (isRetryAttempt) {
                        showRetryQuestionDialog();
                    } else {
                        showQuestionDialog();
                    }
                });
                
            } catch (Exception e) {
                System.err.println("Error fetching question from API: " + e.getMessage());
                e.printStackTrace();
                
                // Handle error by showing a message to the user
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(PanelGame.this),
                        "Failed to load question: " + e.getMessage(),
                        "API Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    
                    // Reset game state
                    showingQuestion = false;
                    isPaused = isRetryAttempt ? false : isPaused;
                });
            }
        }).start();
    }
    
    // Method to show question dialog
    private void showQuestionDialog() {
        try {
            // Create a new dialog
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            questionDialog = new JDialog(parentFrame, "Banana Question", true);
            questionDialog.setLayout(new BorderLayout());
            
            // Create panel for question and image
            JPanel questionPanel = new JPanel(new BorderLayout());
            
            // Create HTML label for the question (with embedded image)
            // Handle potential image loading errors with a simple error message
            JLabel questionLabel = new JLabel("<html><div style='text-align: center;'>" +
                    "<img src='" + currentQuestion + "' width='300' height='300' " +
                    "onerror=\"this.onerror=null;this.src='';this.alt='Error loading image';\"><br>" +
                    "What is the solution?</div></html>");
            questionLabel.setHorizontalAlignment(JLabel.CENTER);
            questionPanel.add(questionLabel, BorderLayout.CENTER);
            
            // Create panel for answers
            JPanel answerPanel = new JPanel(new GridLayout(2, 2, 10, 10));
            for (int i = 0; i < 4; i++) {
                final int answerIndex = i;
                JButton answerButton = new JButton(currentAnswers[i]);
                answerButton.setPreferredSize(new Dimension(100, 50));
                answerButton.addActionListener(e -> {
                    questionDialog.dispose();
                    handleAnswer(answerIndex);
                });
                answerPanel.add(answerButton);
            }
            
            questionPanel.add(answerPanel, BorderLayout.SOUTH);
            questionDialog.add(questionPanel, BorderLayout.CENTER);
            
            // Set dialog properties
            questionDialog.setSize(400, 500);
            questionDialog.setLocationRelativeTo(parentFrame);
            questionDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            questionDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    showingQuestion = false;
                    isPaused = false;
                }
            });
            
            // Show dialog
            questionDialog.setVisible(true);
        } catch (Exception e) {
            System.err.println("Error showing question dialog: " + e.getMessage());
            e.printStackTrace();
            
            // Reset game state
            showingQuestion = false;
            isPaused = false;
        }
    }
    
    // Method to show the retry question dialog
    private void showRetryQuestionDialog() {
        try {
            // Create a new dialog
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            questionDialog = new JDialog(parentFrame, "One More Chance!", true);
            questionDialog.setLayout(new BorderLayout());
            
            // Create panel for question and image
            JPanel questionPanel = new JPanel(new BorderLayout());
            
            // Add a message at the top
            JLabel messageLabel = new JLabel("Answer correctly to get another life!");
            messageLabel.setHorizontalAlignment(JLabel.CENTER);
            messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
            messageLabel.setForeground(Color.RED);
            messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            questionPanel.add(messageLabel, BorderLayout.NORTH);
            
            // Create HTML label for the question (with embedded image)
            JLabel questionLabel = new JLabel("<html><div style='text-align: center;'>" +
                    "<img src='" + currentQuestion + "' width='300' height='300' " +
                    "onerror=\"this.onerror=null;this.src='';this.alt='Error loading image';\"><br>" +
                    "What is the solution?</div></html>");
            questionLabel.setHorizontalAlignment(JLabel.CENTER);
            questionPanel.add(questionLabel, BorderLayout.CENTER);
            
            // Create panel for answers
            JPanel answerPanel = new JPanel(new GridLayout(2, 2, 10, 10));
            for (int i = 0; i < 4; i++) {
                final int answerIndex = i;
                JButton answerButton = new JButton(currentAnswers[i]);
                answerButton.setPreferredSize(new Dimension(100, 50));
                answerButton.addActionListener(e -> {
                    questionDialog.dispose();
                    handleRetryAnswer(answerIndex);
                });
                answerPanel.add(answerButton);
            }
            
            questionPanel.add(answerPanel, BorderLayout.SOUTH);
            questionDialog.add(questionPanel, BorderLayout.CENTER);
            
            // Set dialog properties
            questionDialog.setSize(400, 500);
            questionDialog.setLocationRelativeTo(parentFrame);
            questionDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            questionDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    showingQuestion = false;
                    // If they close the dialog without answering, game remains over
                }
            });
            
            // Show dialog
            questionDialog.setVisible(true);
        } catch (Exception e) {
            System.err.println("Error showing question dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Method to handle answer
    private void handleAnswer(int selectedAnswer) {
        if (selectedAnswer == correctAnswerIndex) {
            // Correct answer - activate shield
            player.activateShield();
            
            // Show success message
            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(this),
                "Correct! Shield activated!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            // Wrong answer
            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(this),
                "Wrong answer! The correct answer was " + correctAnswerIndex,
                "Incorrect",
                JOptionPane.ERROR_MESSAGE
            );
        }
        
        showingQuestion = false;
        isPaused = false;
    }
    
    // Method to handle the retry answer
    private void handleRetryAnswer(int selectedAnswer) {
        hasUsedBananaRetry = true; // Mark that they've used their retry chance
        
        if (selectedAnswer == correctAnswerIndex) {
            // Correct answer - give another life
            isGameOver = false;
            showingQuestion = false;
            
            // Reset player
            player.heal(100); // Restore full health
            player.activateShield(); // Give them a shield as a bonus
            respawnPlayer(); // Reset position and give temporary invincibility
            
            // Show success message
            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(this),
                "Correct! You've earned another life!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            // Wrong answer - game is still over
            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(this),
                "Wrong answer! The correct answer was " + correctAnswerIndex + ".\nGame Over!",
                "Incorrect",
                JOptionPane.ERROR_MESSAGE
            );
            
            // Game remains over, player needs to quit and score will be saved
        }
    }
    
    private void initObjectGame(){
        player = new Player(image, image);
        player.changeLocation(width / 2, height / 2); // Start in the middle of the screen
    }
    
    private void initKeyboard(){
        key = new Key();
        requestFocus();
        addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()== KeyEvent.VK_A){
                    key.setKey_left(true);
                } else if(e.getKeyCode()== KeyEvent.VK_D){
                    key.setKey_right(true);
                } else if(e.getKeyCode()== KeyEvent.VK_SPACE){
                    key.setKey_space(true);                    
                } else if(e.getKeyCode()== KeyEvent.VK_J){
                    key.setKey_left_click(true);
                } else if(e.getKeyCode()== KeyEvent.VK_K){
                    key.setKey_right_click(true);
                } else if (e.getKeyCode() == KeyEvent.VK_P && !isGameOver && !showingQuestion) {
                    togglePause();
                } else if (e.getKeyCode() == KeyEvent.VK_M) {
                    toggleMute();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if (isPaused || isGameOver) {
                        if (isGameOver) {
                            // Save score and show scoreboard before quitting
                            saveScoreAndShowScoreboard();
                        }
                        
                        // Return to main menu
                        Window window = SwingUtilities.getWindowAncestor(PanelGame.this);
                        if (window != null) {
                            window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
                        }
                    }
                }
            }       

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode()== KeyEvent.VK_A){
                    key.setKey_left(false);
                } else if(e.getKeyCode()== KeyEvent.VK_D){
                    key.setKey_right(false);
                } else if(e.getKeyCode()== KeyEvent.VK_SPACE){
                    key.setKey_space(false);                    
                } else if(e.getKeyCode()== KeyEvent.VK_J){
                    key.setKey_left_click(false);
                } else if(e.getKeyCode()== KeyEvent.VK_K){
                    key.setKey_right_click(false);
                }
            }
        });
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                float s = 0.5f;
                while (start){
                    if (!isPaused && !isGameOver && !showingQuestion) {
                        float angle = player.getAngle();
                        if(key.isKey_left()){
                            angle -= s;
                        }
                        if(key.isKey_right()){
                            angle += s;
                        }
                        
                        if(key.isKey_left_click() || key.isKey_right_click()){
                            if(shotTime == 0){
                                if(key.isKey_left_click()){
                                    Shape bulletShape = new Ellipse2D.Double(0, 0, 5, 5);
                                    bullets.add(0, new Bullet(player.getX(), player.getY(), bulletShape, player.getAngle(), 1));
                                } else {
                                    Shape bulletShape = new Ellipse2D.Double(0, 0, 5, 5);
                                    bullets.add(0, new Bullet(player.getX(), player.getY(), bulletShape, player.getAngle(), 1.5));
                                }
                            }
                            shotTime++;
                            if (shotTime == 15) {
                                shotTime = 0;
                            }
                        } else {
                            shotTime = 0;
                        }
                        
                        if(key.isKey_space()){
                            player.speedUp();
                        } else {
                            player.speedDown();
                        }
                        
                        player.update();
                        player.changeAngle(angle);
                    }
                    sleep(5);
                }
            }
        }).start();
    }
    
    private void initBullets(){
        bullets = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (start){
                    if (!isPaused && !isGameOver && !showingQuestion) {
                        for(int i = 0; i < bullets.size(); i++){
                            Bullet bullet = bullets.get(i);
                            if(bullet != null){
                                bullet.update();
                                if(!bullet.check(width, height)){
                                    bullets.remove(bullet);
                                }
                            }
                        }
                    }
                    sleep(1);
                }
            }
        }).start();
    }
    
    private void drawBackground() {
        // Draw dark background
        g2.setColor(new Color(5, 5, 20)); // Very dark blue instead of pure black
        g2.fillRect(0, 0, width, height);
        
        // Update and draw stars
        for (Star star : stars) {
            star.update();
            star.draw(g2);
        }
    }

    private void drawGame() {
        // Draw game objects
        player.draw(g2);
        
        // Draw bullets
        for(int i = 0; i < bullets.size(); i++){
            Bullet bullet = bullets.get(i);
            if(bullet != null){
                bullet.draw(g2);
            }
        }
        
        // Draw rockets
        for (int i = 0; i < rockets.size(); i++) {
            Rocket rocket = rockets.get(i);
            if (rocket != null) {
                rocket.draw(g2);
            }
        }
        
        // Draw bananas
        for (int i = 0; i < bananas.size(); i++) {
            Banana banana = bananas.get(i);
            if (banana != null) {
                banana.draw(g2);
            }
        }
        
        // Draw score
        g2.setColor(Color.WHITE);
        g2.setFont(scoreFont);
        g2.drawString("SCORE: " + score, 20, 30);
        g2.drawString("HIGH SCORE: " + highScore, 20, 60);
    }
    
    private void drawPauseMenu() {
        // First draw the game in the background
        drawBackground();
        drawGame();
        
        // Draw semi-transparent overlay
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, width, height);
        
        // Draw pause menu text
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 40));
        g2.drawString("GAME PAUSED", width/2 - 150, height/2 - 50);
        
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.drawString("Press 'P' to resume", width/2 - 100, height/2);
        g2.drawString("Sound: " + (isMuted ? "OFF" : "ON") + " (Press 'M' to toggle)", width/2 - 150, height/2 + 40);
        g2.drawString("Press 'ESC' to quit", width/2 - 100, height/2 + 80);
    }
    
   private void drawGameOverScreen() {
    // Semi-transparent overlay
    g2.setColor(new Color(0, 0, 0, 180));
    g2.fillRect(0, 0, width, height);
    
    // Game Over text
    g2.setColor(Color.RED);
    g2.setFont(new Font("Arial", Font.BOLD, 50));
    String gameOverText = "GAME OVER";
    int textWidth = g2.getFontMetrics().stringWidth(gameOverText);
    g2.drawString(gameOverText, width/2 - textWidth/2, height/2 - 80);
    
    // Score text
    g2.setColor(Color.WHITE);
    g2.setFont(new Font("Arial", Font.BOLD, 30));
    String scoreText = "FINAL SCORE: " + score;
    textWidth = g2.getFontMetrics().stringWidth(scoreText);
    g2.drawString(scoreText, width/2 - textWidth/2, height/2 - 20);
    
    // Draw retry button if this is their first death
    if (!hasUsedBananaRetry) {
        g2.setColor(new Color(255, 255, 0)); // Yellow for banana theme
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        String retryText = "Try Banana for One More Chance!";
        textWidth = g2.getFontMetrics().stringWidth(retryText);
        
        // Draw button background
        int buttonWidth = textWidth + 40;
        int buttonHeight = 40;
        int buttonX = width/2 - buttonWidth/2;
        int buttonY = height/2 + 20;
        
        g2.fillRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 15, 15);
        
        // Draw button text
        g2.setColor(Color.BLACK);
        g2.drawString(retryText, width/2 - textWidth/2, buttonY + 30);
        
        // Store button coordinates for click detection
        bananaRetryButtonBounds.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
    }
    
    // Quit instruction
    g2.setColor(Color.WHITE);
    g2.setFont(new Font("Arial", Font.PLAIN, 20));
    String quitText = "Press ESC to quit and save score";
    textWidth = g2.getFontMetrics().stringWidth(quitText);
    g2.drawString(quitText, width/2 - textWidth/2, height/2 + 90);
}

private void render() {
    Graphics g = getGraphics();
    g.drawImage(image, 0, 0, null);
    g.dispose();
}

private void sleep(long speed) {
    try {
        Thread.sleep(speed);
    } catch (InterruptedException ex) {
        System.err.println(ex);
    }
}

private void togglePause() {
    isPaused = !isPaused;
}

private void toggleMute() {
    isMuted = !isMuted;
    if (audioPlayer != null) {
        if (isMuted) {
            audioPlayer.pauseMusic();
        } else {
            audioPlayer.resumeMusic();
        }
    }
}

private void respawnPlayer() {
    // Give player invincibility for a moment
    player.setInvincible(true);
    
    // Reset health to full
    player.heal(100);
    
    // Move player to center of screen
    player.changeLocation(width/2, height/2);
    
    // Reset player speed and angle
    player.resetMovement();
    
    // Start a timer to remove invincibility
    new Thread(() -> {
        sleep(3000); // 3 seconds of invincibility
        player.setInvincible(false);
    }).start();
}

// Add this method to handle mouse clicks
private void handleMouseClick(MouseEvent e) {
    // Check if game is over and banana retry button was clicked
    if (isGameOver && !hasUsedBananaRetry && bananaRetryButtonBounds.contains(e.getPoint())) {
        // Start the banana question challenge
        tryBananaRetry();
    }
}

// Add this method to handle the retry process
private void tryBananaRetry() {
    fetchQuestionFromAPI(true); // Pass true to indicate this is a retry attempt
}

private void showGameOver() {
    isGameOver = true;
    
    // If no event listener is already added, add one for mouse clicks
    if (!hasMouseListener) {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }
        });
        hasMouseListener = true;
    }
}

// Add method to save the score and show scoreboard
public void saveScoreAndShowScoreboard() {
    // Only save score if game is over
    if (isGameOver && currentUserId > 0) {
        try {
            // Save score to database
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO scores (user_id, score, game_date) VALUES (?, ?, NOW())"
            );
            ps.setInt(1, currentUserId);
            ps.setInt(2, score);
            ps.executeUpdate();
            ps.close();
            
            // Show scoreboard
            SwingUtilities.invokeLater(() -> {
                new ScoreboardForm().setVisible(true);
            });
        } catch (SQLException e) {
            System.err.println("Error saving score: " + e.getMessage());
            e.printStackTrace();
            
            // Still show the scoreboard even if there was an error saving
            SwingUtilities.invokeLater(() -> {
                new ScoreboardForm().setVisible(true);
            });
        }
    } else if (isGameOver) {
        // No user ID, but still show scoreboard
        SwingUtilities.invokeLater(() -> {
            new ScoreboardForm().setVisible(true);
        });
    }
}

// Method to get current score for saving to database
public int getScore() {
    return score;
}

// Method to stop the game
public void stopGame() {
    this.start = false;
}
}