package killed.at.space.game.main;

import Authentication.LoginForm;
import game.component.PanelGame;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main extends JFrame{
    
    public Main (){
        init();
    }
   
    private void init(){
        setTitle("Killed-In-Space");
        setSize(1366,768);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        PanelGame panelGame=new PanelGame();
        add(panelGame);
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowOpened(WindowEvent e) {
                panelGame.start();
            }
            
    });
    }
    
    // Original game launcher - now used for direct game testing
    private static void startGame() {
        Main main = new Main();
        main.setVisible(true);
    }
    
    // New main method that launches the login form
    public static void main(String[] args) {
        // Check if we want to bypass login (for testing)
        boolean bypassLogin = false;
        
        if (bypassLogin) {
            // Original game launcher for testing
            startGame();
        } else {
            // Start with login form
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new LoginForm().setVisible(true);
                }
            });
        }    
    }
}