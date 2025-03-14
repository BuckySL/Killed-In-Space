package game.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
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

    // FPS for game rendering
    private final int FPS = 60;
    private final int Target_Time = 1000000000 / FPS; // Corrected to nanoseconds

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
        thread.start();
    }

    private void drawBackground() {
        g2.setColor(new Color(30,30,30));
        g2.fillRect(0, 0, width, height);
    }

    private void drawGame() {
        // Implementation here
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
