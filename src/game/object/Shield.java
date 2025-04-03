package game.object;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Composite;

public class Shield {
    private int strength = 3; // Number of rocket hits it can absorb
    private long startTime;
    private boolean active = false;
    
    public Shield() {
        this.startTime = System.currentTimeMillis();
    }
    
    public void activate() {
        this.active = true;
        this.strength = 3;
        this.startTime = System.currentTimeMillis();
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void draw(Graphics2D g2, double x, double y) {
        if (!active) return;
        
        // Calculate pulsing effect
        long elapsed = System.currentTimeMillis() - startTime;
        float pulse = 0.7f + 0.3f * (float)Math.sin(elapsed * 0.005);
        
        // Save original composite
        Composite originalComposite = g2.getComposite();
        
        // Draw shield with transparency
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f * pulse));
        g2.setColor(Color.WHITE);
        g2.fillOval((int)x - 5, (int)y - 5, (int)Player.PLAYER_SIZE + 10, (int)Player.PLAYER_SIZE + 10);
        
        // Draw shield health indicator
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
        
        int barWidth = 30;
        int barHeight = 4;
        int barX = (int)(x + Player.PLAYER_SIZE/2) - barWidth/2;
        int barY = (int)(y - 20);
        
        // Background (red)
        g2.setColor(Color.RED);
        g2.fillRect(barX, barY, barWidth, barHeight);
        
        // Shield health (cyan)
        g2.setColor(Color.CYAN);
        int healthWidth = (int)(barWidth * (strength / 3.0f));
        g2.fillRect(barX, barY, healthWidth, barHeight);
        
        // Border
        g2.setColor(Color.WHITE);
        g2.drawRect(barX, barY, barWidth, barHeight);
        
        // Restore original composite
        g2.setComposite(originalComposite);
    }
    
    public boolean absorbHit() {
        if (!active) return false;
        
        strength--;
        if (strength <= 0) {
            active = false;
            return false;
        }
        return true;
    }
}