package Authentication;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import game.audio.AudioPlayer;

public class LoginForm extends JFrame {
    
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    private JTextField registerUsernameField;
    private JPasswordField registerPasswordField;
    private JPasswordField confirmPasswordField;
    private AudioPlayer audioPlayer;
    private Random random = new Random();
    
    // Colors for space theme
    private final Color SPACE_DARK = new Color(5, 5, 20);
    private final Color SPACE_BLUE = new Color(20, 70, 150);
    private final Color SPACE_GLOW = new Color(77, 183, 255);
    private final Color NEON_BLUE = new Color(0, 195, 255);
    private final Color NEON_PURPLE = new Color(178, 102, 255);
    
    public LoginForm() {
        audioPlayer = new AudioPlayer();
        setWindowIcon();
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Killed-At-Space - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(450, 400));
        
        // Create space background panel
        SpaceBackgroundPanel mainPanel = new SpaceBackgroundPanel();
        mainPanel.setLayout(new BorderLayout());
        
        // Title panel with space theme
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw gradient background
                GradientPaint gp = new GradientPaint(0, 0, SPACE_DARK, 0, getHeight(), SPACE_BLUE);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Title with cosmic glow effect
        JLabel titleLabel = new JLabel("KILLED-AT-SPACE");
        titleLabel.setFont(new Font("Orbitron", Font.BOLD, 28)); // Sci-fi font (falls back to default if unavailable)
        titleLabel.setForeground(NEON_BLUE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // Create tabbed pane with custom styling
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setForeground(Color.WHITE);
        tabbedPane.setBackground(SPACE_DARK);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Login Panel
        JPanel loginPanel = createLoginPanel();
        tabbedPane.addTab("Login", loginPanel);
        
        // Register Panel
        JPanel registerPanel = createRegisterPanel();
        tabbedPane.addTab("Register", registerPanel);
        
        // Add components to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Set content pane
        setContentPane(mainPanel);
        
        pack();
        setLocationRelativeTo(null); // Center on screen
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new SpaceBackgroundPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(7, 7, 7, 7);
        
        // Username
        JLabel userLabel = new JLabel("USERNAME:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(userLabel, gbc);
        
        loginUsernameField = new JTextField(20);
        loginUsernameField.setBackground(new Color(20, 30, 50));
        loginUsernameField.setForeground(Color.WHITE);
        loginUsernameField.setCaretColor(Color.WHITE);
        loginUsernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NEON_BLUE, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(loginUsernameField, gbc);
        
        // Password
        JLabel passLabel = new JLabel("PASSWORD:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passLabel, gbc);
        
        loginPasswordField = new JPasswordField(20);
        loginPasswordField.setBackground(new Color(20, 30, 50));
        loginPasswordField.setForeground(Color.WHITE);
        loginPasswordField.setCaretColor(Color.WHITE);
        loginPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NEON_BLUE, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(loginPasswordField, gbc);
        
        // Login button
        JButton loginButton = createSpaceButton("LOGIN");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 7, 7, 7);
        panel.add(loginButton, gbc);
        
        // Add action to login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = loginUsernameField.getText();
                String password = new String(loginPasswordField.getPassword());
                
                if (username.isEmpty() || password.isEmpty()) {
                    showErrorMessage("Please enter both username and password", "Login Error");
                    return;
                }
                
                User user = User.login(username, password);
                
                if (user != null) {
                    // Login successful
                    showSuccessMessage("Login successful!", "Success");
                    
                    // Open main menu
                    openMainMenu(user);
                } else {
                    // Login failed
                    showErrorMessage("Invalid username or password", "Login Error");
                }
            }
        });
        
        return panel;
    }
    
    private JPanel createRegisterPanel() {
        JPanel panel = new SpaceBackgroundPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(7, 7, 7, 7);
        
        // Username
        JLabel userLabel = new JLabel("USERNAME:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(userLabel, gbc);
        
        registerUsernameField = new JTextField(20);
        registerUsernameField.setBackground(new Color(20, 30, 50));
        registerUsernameField.setForeground(Color.WHITE);
        registerUsernameField.setCaretColor(Color.WHITE);
        registerUsernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NEON_PURPLE, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(registerUsernameField, gbc);
        
        // Password
        JLabel passLabel = new JLabel("PASSWORD:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passLabel, gbc);
        
        registerPasswordField = new JPasswordField(20);
        registerPasswordField.setBackground(new Color(20, 30, 50));
        registerPasswordField.setForeground(Color.WHITE);
        registerPasswordField.setCaretColor(Color.WHITE);
        registerPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NEON_PURPLE, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(registerPasswordField, gbc);
        
        // Confirm Password
        JLabel confirmLabel = new JLabel("CONFIRM:");
        confirmLabel.setForeground(Color.WHITE);
        confirmLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(confirmLabel, gbc);
        
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setBackground(new Color(20, 30, 50));
        confirmPasswordField.setForeground(Color.WHITE);
        confirmPasswordField.setCaretColor(Color.WHITE);
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NEON_PURPLE, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(confirmPasswordField, gbc);
        
        // Register button
        JButton registerButton = createSpaceButton("REGISTER");
        registerButton.setBackground(NEON_PURPLE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 7, 7, 7);
        panel.add(registerButton, gbc);
        
        // Add action to register button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = registerUsernameField.getText();
                String password = new String(registerPasswordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                
                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    showErrorMessage("Please fill all fields", "Registration Error");
                    return;
                }
                
                if (!password.equals(confirmPassword)) {
                    showErrorMessage("Passwords do not match", "Registration Error");
                    return;
                }
                
                User newUser = new User(username, password);
                boolean registered = newUser.register();
                
                if (registered) {
                    showSuccessMessage("Registration successful! Please login.", "Success");
                    
                    // Clear fields and switch to login tab
                    registerUsernameField.setText("");
                    registerPasswordField.setText("");
                    confirmPasswordField.setText("");
                    JTabbedPane tabbedPane = (JTabbedPane) panel.getParent();
                    tabbedPane.setSelectedIndex(0);
                } else {
                    showErrorMessage("Registration failed. Username may already exist.", "Registration Error");
                }
            }
        });
        
        return panel;
    }
    
    private JButton createSpaceButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(NEON_BLUE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(200, 40));
        return button;
    }
    
    private void showErrorMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    private void showSuccessMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void openMainMenu(User user) {
        // Play theme music
        audioPlayer.playMusic("/game/audio/Star Wars Main Theme (Full).wav", true);
        
        // Close login form
        dispose();
        
        // Open main menu
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainMenu mainMenu = new MainMenu(user);
                mainMenu.setAudioPlayer(audioPlayer);
                mainMenu.setVisible(true);
            }
        });
    }
    
    private void setWindowIcon() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/game/image/plane.png"));
        setIconImage(icon.getImage());
    }
    
    // Inner class for space background with stars
    class SpaceBackgroundPanel extends JPanel {
        private Star[] stars;
        
        public SpaceBackgroundPanel() {
            setOpaque(false);
            stars = new Star[100];
            for (int i = 0; i < stars.length; i++) {
                stars[i] = new Star();
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw space background
            g2d.setColor(SPACE_DARK);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // Draw stars
            for (Star star : stars) {
                star.draw(g2d, getWidth(), getHeight());
            }
        }
        
        // Inner class for stars
        class Star {
            private int x, y;
            private int size;
            private float brightness;
            
            public Star() {
                reset(100, 100);
            }
            
            public void reset(int maxWidth, int maxHeight) {
                x = random.nextInt(maxWidth);
                y = random.nextInt(maxHeight);
                size = random.nextInt(3) + 1;
                brightness = random.nextFloat();
            }
            
            public void draw(Graphics2D g, int maxWidth, int maxHeight) {
                if (x >= maxWidth || y >= maxHeight) {
                    reset(maxWidth, maxHeight);
                }
                
                int alpha = (int) (brightness * 255);
                g.setColor(new Color(255, 255, 255, alpha));
                g.fillOval(x, y, size, size);
                
                // Occasionally add a glow to some stars
                if (size > 1 && random.nextInt(10) == 0) {
                    g.setColor(new Color(200, 200, 255, 50));
                    g.fillOval(x - 2, y - 2, size + 4, size + 4);
                }
            }
        }
    }
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginForm().setVisible(true);
            }
        });
    }
}