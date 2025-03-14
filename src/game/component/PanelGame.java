package game.component;

import game.object.Bullet;
import game.object.Player;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;

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
    
    public void start() {
        width = getWidth();
        height = getHeight();
        image=new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (start) {
                    long startTime = System.nanoTime();
                    drawBackground();
                    drawGame();
                    render();
                    long time = System.nanoTime() - startTime;
                    if (time < Target_Time) {
                        long sleep = (Target_Time - time) / 1000000;
                        sleep(sleep);
                        System.out.println(sleep);
                    }
                }
            }
        });
        initObjectGame();
        initStars();
        initKeyboard();
        initBullets();
        thread.start();
    }
    private void initStars() {
    stars = new ArrayList<>();
    for (int i = 0; i < NUM_STARS; i++) {
        stars.add(new Star(width, height));
    }
}
    
    private void initObjectGame(){
        player = new Player(image, image);
        player.changeLocation(150, 150);
    }
    
    private void initKeyboard(){
        key=new Key();
        requestFocus();
        addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()== KeyEvent.VK_A){
                    key.setKey_left(true);
                } else if(e.getKeyCode()== KeyEvent.VK_D){
                    key.setKey_right(true);
                }else if(e.getKeyCode()== KeyEvent.VK_SPACE){
                    key.setKey_space(true);                    
                }else if(e.getKeyCode()== KeyEvent.VK_J){
                    key.setKey_left_click(true);
                }else if(e.getKeyCode()== KeyEvent.VK_K){
                    key.setKey_right_click(true);
                }
                
            }       

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode()== KeyEvent.VK_A){
                    key.setKey_left(false);
                } else if(e.getKeyCode()== KeyEvent.VK_D){
                    key.setKey_right(false);
                }else if(e.getKeyCode()== KeyEvent.VK_SPACE){
                    key.setKey_space(false);                    
                }else if(e.getKeyCode()== KeyEvent.VK_J){
                    key.setKey_left_click(false);
                }else if(e.getKeyCode()== KeyEvent.VK_K){
                    key.setKey_right_click(false);
                }
            }
            
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                float s = 0.5f;
                while (start){
                    float angle=player.getAngle();
                    if(key.isKey_left()){
                        angle-=s;
                    }
                    if(key.isKey_right()){
                    
                    angle +=s;
                    }
                    if(key.isKey_left_click()||key.isKey_right_click()){
                        if(shotTime==0){
                        if(key.isKey_left_click()){
                            Shape bulletShape = new Ellipse2D.Double(0, 0, 5, 5);
                            bullets.add(0, new Bullet(player.getX(), player.getY(), bulletShape, player.getAngle(), 1));
                        
                        }else{
                            Shape bulletShape = new Ellipse2D.Double(0, 0, 5, 5);
                            bullets.add(0, new Bullet(player.getX(), player.getY(), bulletShape, player.getAngle(), 1.5));
                            
                        }
                        
                        }
                        shotTime++;
                        if(shotTime==15){
                            shotTime = 0;
                        }
                        else{
                        shotTime = 0;
                    }
                        
                    }
                    
                    
                    
                    
                    if(key.isKey_space()){
                        player.speedUp();
                    }else{
                        player.speedDown();
                    }
                    player.update();
                    player.changeAngle(angle);
                    sleep(5);
                }
            }
        }).start();
    }
    
    private void initBullets(){
        bullets = new ArrayList<>();
        new Thread (new Runnable() {
            @Override
            public void run() {
                while (start){
                  for(int i=0;i<bullets.size();i++){
                  Bullet bullet=bullets.get(i);
                  if(bullet!=null){
                     bullet.update();
                     if(!bullet.check(width,height)){
                         bullets.remove(bullet);
                     }
                  }
                }
                  sleep(1);
               } 
            }
        }).start();;
    
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
        player.draw(g2);
        for(int i = 0;i<bullets.size();i++){
            Bullet bullet=bullets.get(i);
            if(bullet !=null){
                bullet.draw(g2);
            }
        }
    }

    private void render() {
            Graphics g=getGraphics();
            g.drawImage(image,0,0,null);
            g.dispose();
    }

    private void sleep(long speed) {
        try {
            Thread.sleep(speed);
        } catch (InterruptedException ex) {
            System.err.println(ex);
        }
    }
}
