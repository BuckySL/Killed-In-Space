package game.component;
import java.awt.Color;
import java.awt.Graphics2D;

class Star {
    private double x, y;
    private double size;
    private float brightness;
    private float twinkleSpeed;
    private double twinkleState;
    
    public Star(int width, int height) {
        this.x = Math.random() * width;
        this.y = Math.random() * height;
        this.size = Math.random() * 2 + 0.5; // Size between 0.5 and 2.5
        this.brightness = (float)(Math.random() * 0.7f + 0.3f); // Brightness between 0.3 and 1.0
        this.twinkleSpeed = (float)(Math.random() * 0.03f + 0.01f); // Speed of twinkling
        this.twinkleState = Math.random() * Math.PI * 2; // Random starting point
    }
    
    public void update() {
        twinkleState += twinkleSpeed;
        if (twinkleState > Math.PI * 10) {
            twinkleState = 0;
        }
    }
    
    public void draw(Graphics2D g2) {
        // Calculate current brightness based on sine wave
        float currentBrightness = (float)((Math.sin(twinkleState) + 1) * 0.5 * brightness);
        
        // Set color with appropriate alpha for twinkling effect
        int alpha = (int)(currentBrightness * 255);
        g2.setColor(new Color(255, 255, 255, alpha));
        
        // Draw the star
        g2.fillOval((int)x, (int)y, (int)size, (int)size);
    }
}