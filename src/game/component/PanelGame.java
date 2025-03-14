/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.component;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;

public class PanelGame extends JComponent{
    
    private int width;
    private int height;
    private Thread thread;
    private boolean start = true;
   
    
    //FPS for game rendering
    private final int FPS=60;
    private final int Target_Time=10000000/FPS;
    
    public void start(){
    
        width=getWidth();
        height=getHeight;
        thread = new Thread(new Runnable()){
            @Override
            public void run(){
            while(start){
            
            
            }
            
            
            }
        });
        thread.start();
            
        }
        private void sleep(long speed){
        try {
            Thread.sleep(speed);
        } catch (InterruptedException ex) {
            Logger.getLogger(PanelGame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        }
        
        
    
    }
        
    

