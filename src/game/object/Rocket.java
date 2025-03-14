package game.object;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public class Rocket {
    private double x;
    private double y;
    private double targetX;
    private double targetY;
    private double angle;
    private final double size = 32; // Size of rocket image
    private final float speed = 1.5f; // Rocket speed
    private final BufferedImage image;
    private final double turnSpeed = 0.03; // How fast the rocket turns toward player
    
    public Rocket(double x, double y) {
        this.x = x;
        this.y = y;
        this.angle = Math.random() * 360; // Start with random angle
        
        // Load rocket image
        ImageIcon icon = new ImageIcon(getClass().getResource("/game/image/rocket.png"));
        image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        image.getGraphics().drawImage(icon.getImage(), 0, 0, 32, 32, null);
    }
    
    public void update(double playerX, double playerY) {
        // Update target to player position
        targetX = playerX + (Player.PLAYER_SIZE / 2);
        targetY = playerY + (Player.PLAYER_SIZE / 2);
        
        // Calculate angle to player
        double dx = targetX - (x + size/2);
        double dy = targetY - (y + size/2);
        double targetAngle = Math.toDegrees(Math.atan2(dy, dx));
        
        // Adjust current angle toward target angle (homing effect)
        double angleDiff = targetAngle - angle;
        
        // Handle angle wrapping
        if (angleDiff > 180) angleDiff -= 360;
        if (angleDiff < -180) angleDiff += 360;
        
        // Apply turn rate
        angle += angleDiff * turnSpeed;
        
        // Move rocket forward at its current angle
        x += Math.cos(Math.toRadians(angle)) * speed;
        y += Math.sin(Math.toRadians(angle)) * speed;
    }
    
    public void draw(Graphics2D g2) {
        AffineTransform oldTransform = g2.getTransform();
        g2.translate(x, y);
        
        // Rotate to face movement direction
        AffineTransform tran = new AffineTransform();
        tran.rotate(Math.toRadians(angle + 90), size/2, size/2);
        g2.drawImage(image, tran, null);
        
        g2.setTransform(oldTransform);
    }
    
    public boolean checkCollision(double playerX, double playerY) {
        // Simple collision detection
        double centerX = x + size/2;
        double centerY = y + size/2;
        double playerCenterX = playerX + Player.PLAYER_SIZE/2;
        double playerCenterY = playerY + Player.PLAYER_SIZE/2;
        
        double distance = Math.sqrt(
            Math.pow(centerX - playerCenterX, 2) + 
            Math.pow(centerY - playerCenterY, 2)
        );
        
        return distance < (size/2 + Player.PLAYER_SIZE/2 - 10); // -10 for tighter collision
    }
    
    public boolean check(int width, int height) {
        return x >= 0 && y >= 0 && x <= width && y <= height;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
}