package game.object;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import javax.swing.ImageIcon;

public class Player {
    public static final double PLAYER_SIZE = 64;
    private double x;
    private double y;
    private final float MAX_SPEED = 1f;
    private float speed = 0f;
    private float angle = 0f;
    private final Image image;
    private final Image image_speed;
    private boolean speedUp;
    private int maxHealth = 100;
    private int currentHealth = 100;
    private boolean invincible = false;
    
    public Player(Image image, Image image_speed) {
        this.image = new ImageIcon(getClass().getResource("/game/image/plane.png")).getImage();       
        this.image_speed = new ImageIcon(getClass().getResource("/game/image/plane_speed.png")).getImage();
    }
    
    public void update() {
        x += Math.cos(Math.toRadians(angle)) * speed;
        y += Math.sin(Math.toRadians(angle)) * speed;
    }
    
    public void changeLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public void changeAngle(float angle) {
        if(angle < 0) {
            angle = 359;
        } else if(angle > 359) {
            angle = 0;
        }
        this.angle = angle;
    }
    
    public void draw(Graphics2D g2) {
        AffineTransform oldTransform = g2.getTransform();
        g2.translate(x, y);
        
        // Draw the player ship
        AffineTransform tran = new AffineTransform();
        tran.rotate(Math.toRadians(angle+45), PLAYER_SIZE/2, PLAYER_SIZE/2);
        g2.drawImage(speedUp ? image_speed : image, tran, null);
        
        // Draw health bar
        int barWidth = 40;
        int barHeight = 5;
        int barX = (int)(PLAYER_SIZE/2) - barWidth/2;
        int barY = -10;
        
        // Draw empty health bar (red background)
        g2.setColor(Color.RED);
        g2.fillRect(barX, barY, barWidth, barHeight);
        
        // Draw current health (green foreground)
        g2.setColor(Color.GREEN);
        int healthWidth = (int)(barWidth * ((float)currentHealth / maxHealth));
        g2.fillRect(barX, barY, healthWidth, barHeight);
        
        // Draw border
        g2.setColor(Color.WHITE);
        g2.drawRect(barX, barY, barWidth, barHeight);
        
        // If player is invincible, show visual indicator
        if (invincible) {
            g2.setColor(new Color(255, 255, 255, 80)); // Translucent white
            g2.fillOval(-5, -5, (int)PLAYER_SIZE + 10, (int)PLAYER_SIZE + 10);
        }
        
        g2.setTransform(oldTransform);
    }
    
    // Getters
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public float getAngle() {
        return angle;
    }
    
    public void speedDown() {
        speedUp = false;
        if(speed <= 0) {
            speed = 0;
        } else {
            speed -= 0.003f;
        }
    }
    
    public void speedUp() {
        speedUp = true;
        if(speed > MAX_SPEED) {
            speed = MAX_SPEED;
        } else {
            speed += 0.01f;
        }
    }
    
    public int getHealth() {
        return currentHealth;
    }
    
    public void damage(int amount) {
        if (!invincible) {
            currentHealth -= amount;
            if (currentHealth < 0) currentHealth = 0;
        }
    }
    
    public void heal(int amount) {
        currentHealth += amount;
        if (currentHealth > maxHealth) currentHealth = maxHealth;
    }
    
    public boolean isAlive() {
        return currentHealth > 0;
    }
    
    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }
    
    public void resetMovement() {
        this.speed = 0f;
        this.speedUp = false;
    }
}