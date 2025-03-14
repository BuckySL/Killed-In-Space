/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package killed.at.space.game.main;

import game.component.PanelGame;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

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
    
    public static void main(String[] args) {
        Main main=new Main();
        main.setVisible(true);
                
    }
}
