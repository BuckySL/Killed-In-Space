package game.component;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;

public class PanelGame extends JComponent {

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
        // Implementation here
    }

    private void drawGame() {
        // Implementation here
    }

    private void render() {
        // Implementation here
    }

    private void sleep(long speed) {
        try {
            Thread.sleep(speed);
        } catch (InterruptedException ex) {
            System.err.println(ex);
        }
    }
}
