package Authentication;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
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
import javax.swing.ImageIcon;

public class LoginForm extends JFrame {
    
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    private JTextField registerUsernameField;
    private JPasswordField registerPasswordField;
    private JPasswordField confirmPasswordField;
    private AudioPlayer audioPlayer;
    
    public LoginForm() {
        audioPlayer = new AudioPlayer();
        setWindowIcon();
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Killed-At-Space - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(400, 350));
        
        // Create tabbed pane for login and register
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Login Panel
        JPanel loginPanel = createLoginPanel();
        tabbedPane.addTab("Login", loginPanel);
        
        // Register Panel
        JPanel registerPanel = createRegisterPanel();
        tabbedPane.addTab("Register", registerPanel);
        
        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("KILLED-AT-SPACE");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 100, 200));
        titlePanel.add(titleLabel);
        
        // Add components to frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(titlePanel, BorderLayout.NORTH);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(null); // Center on screen
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        
        loginUsernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(loginUsernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        
        loginPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(loginPasswordField, gbc);
        
        // Login button
        JButton loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel.add(loginButton, gbc);
        
        // Add action to login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = loginUsernameField.getText();
                String password = new String(loginPasswordField.getPassword());
                
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginForm.this, 
                            "Please enter both username and password", 
                            "Login Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                User user = User.login(username, password);
                
                if (user != null) {
                    // Login successful
                    JOptionPane.showMessageDialog(LoginForm.this, 
                            "Login successful!", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    // Open main menu
                    openMainMenu(user);
                } else {
                    // Login failed
                    JOptionPane.showMessageDialog(LoginForm.this, 
                            "Invalid username or password", 
                            "Login Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        return panel;
    }
    
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        
        registerUsernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(registerUsernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        
        registerPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(registerPasswordField, gbc);
        
        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Confirm Password:"), gbc);
        
        confirmPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(confirmPasswordField, gbc);
        
        // Register button
        JButton registerButton = new JButton("Register");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel.add(registerButton, gbc);
        
        // Add action to register button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = registerUsernameField.getText();
                String password = new String(registerPasswordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                
                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginForm.this, 
                            "Please fill all fields", 
                            "Registration Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(LoginForm.this, 
                            "Passwords do not match", 
                            "Registration Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                User newUser = new User(username, password);
                boolean registered = newUser.register();
                
                if (registered) {
                    JOptionPane.showMessageDialog(LoginForm.this, 
                            "Registration successful! Please login.", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    // Clear fields and switch to login tab
                    registerUsernameField.setText("");
                    registerPasswordField.setText("");
                    confirmPasswordField.setText("");
                    JTabbedPane tabbedPane = (JTabbedPane) panel.getParent();
                    tabbedPane.setSelectedIndex(0);
                } else {
                    JOptionPane.showMessageDialog(LoginForm.this, 
                            "Registration failed. Username may already exist.", 
                            "Registration Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        return panel;
    }
    
    private void openMainMenu(User user) {
        // Close login form
         audioPlayer.playMusic("/game/audio/Star Wars Main Theme (Full).wav", true);
        dispose();
        
        // Open main menu
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainMenu mainMenu = new MainMenu(user);
                mainMenu.setVisible(true);
                mainMenu.setAudioPlayer(audioPlayer);
                mainMenu.setVisible(true);
            }
        });
    }
     private void setWindowIcon() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/game/image/plane.png"));
        setIconImage(icon.getImage());
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