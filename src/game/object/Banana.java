package game.object;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import javax.swing.ImageIcon;

public class Banana {
    private double x;
    private double y;
    private double speed = 0.8f;
    private double angle = 0; // For rotation effect
    private final Image image;
    public static final double BANANA_SIZE = 32;
    
    public Banana(double x, double y) {
        this.x = x;
        this.y = y;
        this.image = new ImageIcon(getClass().getResource("/game/image/banana.png")).getImage();
    }
    
    public void update() {
        // Make the banana float around with slight movement
        x += Math.sin(angle * 0.05) * 0.5;
        y += Math.cos(angle * 0.05) * 0.5;
        angle += 0.05; // Slowly rotate
    }
    
    public void draw(Graphics2D g2) {
        AffineTransform oldTransform = g2.getTransform();
        g2.translate(x, y);
        
        // Apply rotation transform to graphics context
        AffineTransform tran = new AffineTransform();
        tran.rotate(Math.toRadians(angle), BANANA_SIZE/2, BANANA_SIZE/2);
        g2.transform(tran);
        
        // Draw banana with explicit size (32x32) to match rockets
        g2.drawImage(image, 0, 0, (int)BANANA_SIZE, (int)BANANA_SIZE, null);
        
        // Restore original transform
        g2.setTransform(oldTransform);
    }
    
    public boolean checkCollision(double playerX, double playerY) {
        double centerX = x + BANANA_SIZE/2;
        double centerY = y + BANANA_SIZE/2;
        double playerCenterX = playerX + Player.PLAYER_SIZE/2;
        double playerCenterY = playerY + Player.PLAYER_SIZE/2;
        
        double distance = Math.sqrt(
            Math.pow(centerX - playerCenterX, 2) + 
            Math.pow(centerY - playerCenterY, 2)
        );
        
        return distance < (BANANA_SIZE/2 + Player.PLAYER_SIZE/2 - 10);
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
}