package killed.at.space;

import Authentication.User;
import game.component.PanelGame;
import java.awt.BorderLayout;
import javax.swing.JPanel;

public class KilledAtSpace extends JPanel {
    
    private PanelGame panelGame;
    private User currentUser;
    private int score = 0; // Simple score tracker
    
    public KilledAtSpace() {
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Create the game panel
        panelGame = new PanelGame();
        add(panelGame, BorderLayout.CENTER);
    }
    
    @Override
    public void requestFocus() {
        // Delegate focus to the game panel
        if (panelGame != null) {
            panelGame.requestFocus();
        }
    }
    
    public void startGame(User currentUser) {
        this.currentUser = currentUser;
        
        // Start the game panel
        if (panelGame != null) {
            panelGame.start();
            panelGame.requestFocus();
        }
    }
    
    public void stopGame() {
        // In a real implementation, you would calculate the score based on gameplay
        // For now, let's just simulate a score
        this.score = (int)(Math.random() * 450) + 50;
    }
    
    public int getScore() {
        return score;
    }
    
    // Remove the main method since this is now a JPanel component
    // If you need a test method, you can add it separately
}