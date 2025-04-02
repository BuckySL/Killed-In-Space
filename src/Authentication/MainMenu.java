package Authentication;

import game.audio.AudioPlayer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import killed.at.space.KilledAtSpace;

public class MainMenu extends JFrame {
    
    private User currentUser;
    private JLabel welcomeLabel;
    private JLabel scoreLabel;
    private AudioPlayer audioPlayer;
    private Random random = new Random();
    
    // Space theme colors
    private final Color SPACE_DARK = new Color(5, 5, 20);
    private final Color SPACE_BLUE = new Color(20, 70, 150);
    private final Color SPACE_GLOW = new Color(77, 183, 255);
    private final Color NEON_BLUE = new Color(0, 195, 255);
    private final Color NEON_GREEN = new Color(0, 255, 170);
    private final Color NEON_RED = new Color(255, 50, 50);
    private final Color NEON_PURPLE = new Color(178, 102, 255);
    
    public MainMenu(User user) {
        this.currentUser = user;
        setWindowIcon();
        initComponents();
        updateScoreDisplay();
    }
    
    private void initComponents() {
        setTitle("Killed-At-Space - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(700, 500));
        
        // Main space background panel
        SpaceBackgroundPanel mainPanel = new SpaceBackgroundPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Title Panel with cosmic effect
        JPanel titlePanel = new CosmicPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Game title with cosmic font
        JLabel titleLabel = new JLabel("KILLED-AT-SPACE");
        titleLabel.setFont(new Font("Orbitron", Font.BOLD, 36));
        titleLabel.setForeground(NEON_BLUE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titlePanel.add(titleLabel);
        
        // User Info Panel
        JPanel userInfoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw translucent background
                g2d.setColor(new Color(20, 40, 80, 120));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Draw border
                g2d.setColor(NEON_BLUE);
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            }
        };
        userInfoPanel.setOpaque(false);
        userInfoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        // Welcome label with glow effect
        welcomeLabel = new JLabel("WELCOME, " + currentUser.getUsername().toUpperCase() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        userInfoPanel.add(welcomeLabel);
        
        // Score label with cosmic styling
        scoreLabel = new JLabel("YOUR HIGHEST SCORE: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        scoreLabel.setForeground(NEON_GREEN);
        userInfoPanel.add(scoreLabel);
        
        // Buttons Panel
        JPanel buttonsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Don't paint background - keep transparent
            }
        };
        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new GridLayout(3, 1, 0, 20));
        buttonsPanel.setBorder(new EmptyBorder(20, 80, 20, 80));
        
        // Create cosmic buttons
        JButton playButton = createCosmicButton("PLAY GAME", NEON_GREEN);
        JButton scoreboardButton = createCosmicButton("SCOREBOARD", NEON_BLUE);
        JButton quitButton = createCosmicButton("QUIT GAME", NEON_RED);
        
        // Add buttons to panel
        buttonsPanel.add(playButton);
        buttonsPanel.add(scoreboardButton);
        buttonsPanel.add(quitButton);
        
        // Add action listeners
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        
        scoreboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openScoreboard();
            }
        });
        
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                quitGame();
            }
        });
        
        // Assemble the main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(userInfoPanel, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Add to frame
        setContentPane(mainPanel);
        
        pack();
        setLocationRelativeTo(null); // Center on screen
    }
    
    private JButton createCosmicButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;
            
            {
                // Initialize button with mouse listeners
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background gradient
                Color darkColor = baseColor.darker().darker();
                Color brightColor = isHovered ? baseColor.brighter() : baseColor;
                
                GradientPaint gradient = new GradientPaint(
                        0, 0, darkColor,
                        0, getHeight(), brightColor);
                
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Border
                g2d.setColor(isHovered ? Color.WHITE : baseColor);
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                
                // Text
                g2d.setFont(getFont());
                g2d.setColor(Color.WHITE);
                
                // Center text
                java.awt.FontMetrics metrics = g2d.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                
                g2d.drawString(getText(), x, y);
                
                // Glow effect when hovered
                if (isHovered) {
                    g2d.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 50));
                    g2d.fillRoundRect(3, 3, getWidth()-6, getHeight()-6, 15, 15);
                }
            }
        };
        
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(0, 60));
        
        return button;
    }
    
    private void updateScoreDisplay() {
        int highestScore = currentUser.getHighestScore();
        scoreLabel.setText("YOUR HIGHEST SCORE: " + highestScore);
    }
    
    private void startGame() {
        // Pause menu music
        if (audioPlayer != null) {
            audioPlayer.pauseMusic();
        }
        
        // Hide the menu
        setVisible(false);
        
        // Create and show the game window
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Create the game window
                JFrame gameFrame = new JFrame("Killed-At-Space - Game");
                gameFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                gameFrame.setSize(800, 600);
                
                // Set icon
                ImageIcon icon = new ImageIcon(getClass().getResource("/game/image/plane.png"));
                gameFrame.setIconImage(icon.getImage());
                
                // Create the game panel
                KilledAtSpace gamePanel = new KilledAtSpace();
                gamePanel.setAudioPlayer(audioPlayer);
                gameFrame.add(gamePanel);
                
                // Add window listener to handle game closing
                gameFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        // Stop the game
                        gamePanel.stopGame();
                        
                        // Get the final score
                        int finalScore = gamePanel.getScore();
                        
                        // Save the score to the database
                        if (finalScore > 0) {
                            currentUser.saveScore(finalScore);
                        }
                        
                        // Close the game window
                        gameFrame.dispose();
                        
                        // Update the score display and show the menu again
                        updateScoreDisplay();
                        MainMenu.this.setVisible(true);
                        
                        // Resume menu music
                        if (audioPlayer != null) {
                            audioPlayer.resumeMusic();
                        }
                    }
                });
                
                // Start the game
                gameFrame.setVisible(true);
                gamePanel.requestFocus();
                gamePanel.startGame(currentUser);
            }
        });
    }
    
    private void openScoreboard() {
        // Open the scoreboard window
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ScoreboardForm scoreboard = new ScoreboardForm();
                scoreboard.setVisible(true);
            }
        });
    }
    
    private void quitGame() {
        // Create custom JOptionPane with space theme
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to quit the game?",
                "Quit Game",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Stop the music
            if (audioPlayer != null) {
                audioPlayer.stopMusic();
            }
            
            // Close the database connection
            DatabaseConnection.closeConnection();
            
            // Exit the application
            System.exit(0);
        }
    }
    
    public void setAudioPlayer(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }
    
    private void setWindowIcon() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/game/image/plane.png"));
        setIconImage(icon.getImage());
    }
    
    // Inner class for space background with stars
    class SpaceBackgroundPanel extends JPanel {
        private Star[] stars;
        
        public SpaceBackgroundPanel() {
            setOpaque(false);
            stars = new Star[150];
            for (int i = 0; i < stars.length; i++) {
                stars[i] = new Star();
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw space background
            g2d.setColor(SPACE_DARK);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // Draw stars
            for (Star star : stars) {
                star.draw(g2d, getWidth(), getHeight());
            }
        }
        
        // Inner class for stars
        class Star {
            private int x, y;
            private int size;
            private float brightness;
            
            public Star() {
                reset(100, 100);
            }
            
            public void reset(int maxWidth, int maxHeight) {
                x = random.nextInt(maxWidth);
                y = random.nextInt(maxHeight);
                size = random.nextInt(3) + 1;
                brightness = random.nextFloat();
            }
            
            public void draw(Graphics2D g, int maxWidth, int maxHeight) {
                if (x >= maxWidth || y >= maxHeight) {
                    reset(maxWidth, maxHeight);
                }
                
                int alpha = (int) (brightness * 255);
                g.setColor(new Color(255, 255, 255, alpha));
                g.fillOval(x, y, size, size);
                
                // Occasionally add a glow to some stars
                if (size > 1 && random.nextInt(10) == 0) {
                    g.setColor(new Color(200, 200, 255, 50));
                    g.fillOval(x - 2, y - 2, size + 4, size + 4);
                }
            }
        }
    }
    
    // Cosmic gradient panel for title
    class CosmicPanel extends JPanel {
        public CosmicPanel() {
            setOpaque(false);
            setLayout(new BorderLayout());
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Create a cosmic gradient background
            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(20, 50, 100, 150),
                    getWidth(), getHeight(), new Color(50, 0, 80, 150));
            
            g2d.setPaint(gp);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            
            // Add a glowing border
            g2d.setColor(NEON_BLUE);
            g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
        }
    }
}