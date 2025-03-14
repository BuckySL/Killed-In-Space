package Authentication;

import game.audio.AudioPlayer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import killed.at.space.KilledAtSpace;


public class MainMenu extends JFrame {
    
    private User currentUser;
    private JLabel welcomeLabel;
    private JLabel scoreLabel;
    
    public MainMenu(User user) {
        this.currentUser = user;
        setWindowIcon();
        initComponents();
        updateScoreDisplay();
    }
    
    private void initComponents() {
        setTitle("Killed-At-Space - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(600, 450));
        
        // Main Panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("KILLED-AT-SPACE");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 100, 200));
        titlePanel.add(titleLabel);
        
        // User Info Panel
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userInfoPanel.add(welcomeLabel);
        
        scoreLabel = new JLabel("Your Highest Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        userInfoPanel.add(Box.createHorizontalStrut(20));
        userInfoPanel.add(scoreLabel);
        
        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new GridLayout(4, 1, 0, 20));
        buttonsPanel.setBorder(new EmptyBorder(30, 100, 30, 100));
        
        // Play Game Button
        JButton playButton = new JButton("Play Game");
        playButton.setFont(new Font("Arial", Font.BOLD, 18));
        playButton.setBackground(new Color(0, 150, 0));
        playButton.setForeground(Color.WHITE);
        
        // Scoreboard Button
        JButton scoreboardButton = new JButton("Scoreboard");
        scoreboardButton.setFont(new Font("Arial", Font.BOLD, 18));
        scoreboardButton.setBackground(new Color(0, 100, 200));
        scoreboardButton.setForeground(Color.WHITE);
        
        // Quit Game Button
        JButton quitButton = new JButton("Quit Game");
        quitButton.setFont(new Font("Arial", Font.BOLD, 18));
        quitButton.setBackground(new Color(150, 0, 0));
        quitButton.setForeground(Color.WHITE);
        
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
        getContentPane().add(mainPanel);
        
        pack();
        setLocationRelativeTo(null); // Center on screen
    }
    
    private void updateScoreDisplay() {
        int highestScore = currentUser.getHighestScore();
        scoreLabel.setText("Your Highest Score: " + highestScore);
    }
    
    private void startGame() {
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
                
                // Create the game panel
                KilledAtSpace gamePanel = new KilledAtSpace();
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
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to quit the game?",
                "Quit Game",
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Close the database connection
            DatabaseConnection.closeConnection();
            
            // Exit the application
            System.exit(0);
        }
    }
     private void setWindowIcon() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/game/image/plane.png"));
        setIconImage(icon.getImage());
    }
    void setAudioPlayer(AudioPlayer audioPlayer) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}