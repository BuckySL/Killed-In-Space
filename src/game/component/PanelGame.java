package game.component;

import game.audio.AudioPlayer;
import game.object.Bullet;
import game.object.Player;
import game.object.Rocket;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import javax.swing.JComponent;
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
    private int rocketSpawnTime = 0;
    private final int ROCKET_SPAWN_RATE = 180;
    private int score = 0;
    private int highScore = 0;
    private Font scoreFont = new Font("Arial", Font.BOLD, 20);
    private boolean isPaused = false;
    private boolean isGameOver = false;
    private boolean isMuted = false;
    private AudioPlayer audioPlayer;

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
                    if (!isPaused && !isGameOver) {
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
                                    // Damage the player when hit by rocket
                                    player.damage(20);
                                    
                                    // Remove the rocket after collision
                                    rockets.remove(rocket);
                                    
                                    // Check if player is still alive
                                    if (!player.isAlive()) {
                                        // Player died, show game over
                                        showGameOver();
                                    }
                                    
                                    // Play hit sound if audio is available
                                    if (audioPlayer != null && !isMuted) {
                                        // Uncomment if you add a hit sound
                                        // audioPlayer.playMusic("/game/audio/hit.wav", false);
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
                } else if (e.getKeyCode() == KeyEvent.VK_P && !isGameOver) {
                    togglePause();
                } else if (e.getKeyCode() == KeyEvent.VK_M) {
                    toggleMute();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if (isPaused || isGameOver) {
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
                    if (!isPaused && !isGameOver) {
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
                    if (!isPaused && !isGameOver) {
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
        
        // Draw score
        g2.setColor(Color.WHITE);
        g2.setFont(scoreFont);
        g2.drawString("SCORE: " + score, 20, 30);
        g2.drawString("HIGH SCORE: " + highScore, 20, 60);
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
        g2.drawString(gameOverText, width/2 - textWidth/2, height/2 - 50);
        
        // Score text
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 30));
        String scoreText = "FINAL SCORE: " + score;
        textWidth = g2.getFontMetrics().stringWidth(scoreText);
        g2.drawString(scoreText, width/2 - textWidth/2, height/2 + 20);
        
        // Restart instruction
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        String restartText = "Press ESC to return to menu";
        textWidth = g2.getFontMetrics().stringWidth(restartText);
        g2.drawString(restartText, width/2 - textWidth/2, height/2 + 80);
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
    
    private void showGameOver() {
        isGameOver = true;
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