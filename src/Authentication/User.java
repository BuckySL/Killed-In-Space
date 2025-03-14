package Authentication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private int userId;
    private String username;
    private String password;
    
    // Constructor
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    // Full constructor with ID
    public User(int userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }
    
    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    // Register a new user
    public boolean register() {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password); // In a real app, you should hash this password
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeResources(ps, null);
        }
    }
    
    // Login verification
    public static User login(String username, String password) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT * FROM users WHERE username = ? AND password = ?"; // Use hashed password in real app
            ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            
            rs = ps.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                return new User(userId, username, password);
            }
            
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResources(ps, rs);
        }
        
        return null; // Return null if login fails
    }
    
    // Get current user's highest score
    public int getHighestScore() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int highScore = 0;
        
        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT MAX(score) AS highest_score FROM scores WHERE user_id = ?";
            ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            
            rs = ps.executeQuery();
            
            if (rs.next()) {
                highScore = rs.getInt("highest_score");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting highest score: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResources(ps, rs);
        }
        
        return highScore;
    }
    
    // Save a new score
    public boolean saveScore(int score) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            String query = "INSERT INTO scores (user_id, score) VALUES (?, ?)";
            ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setInt(2, score);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saving score: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeResources(ps, null);
        }
    }
}