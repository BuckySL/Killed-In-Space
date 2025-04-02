package killed.at.space;

import Authentication.User;
import game.audio.AudioPlayer;
import game.component.PanelGame;
import java.awt.BorderLayout;
import javax.swing.JPanel;

public class KilledAtSpace extends JPanel {
    
    private PanelGame panelGame;
    private User currentUser;
    private int score = 0; // Simple score tracker
    private AudioPlayer audioPlayer;
    
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
        //Focus to the game panel
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
        this.score = (int)(Math.random() * 450) + 50;
    }
    
    public int getScore() {
        return score;
    }

    public void setAudioPlayer(AudioPlayer audioPlayer) {
         this.audioPlayer = audioPlayer;
    }

   
    
}