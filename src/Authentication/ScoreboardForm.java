package Authentication;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ScoreboardForm extends JFrame {
    
    private JTable scoreTable;
    private DefaultTableModel tableModel;
    private Random random = new Random();
    
    // Space theme colors
    private final Color SPACE_DARK = new Color(5, 5, 20);
    private final Color SPACE_BLUE = new Color(20, 70, 150);
    private final Color NEON_BLUE = new Color(0, 195, 255);
    private final Color NEON_GREEN = new Color(0, 255, 170);
    private final Color NEON_GOLD = new Color(255, 215, 0);
    
    public ScoreboardForm() {
        setWindowIcon();
        initComponents();
        loadTopScores();
    }
    
    private void initComponents() {
        setTitle("Killed-At-Space - Scoreboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(600, 500));
        
        // Main Panel with space background
        SpaceBackgroundPanel mainPanel = new SpaceBackgroundPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title Panel with cosmic effect
        JPanel titlePanel = new CosmicPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title with cosmic glow effect
        JLabel titleLabel = new JLabel("TOP 10 COSMIC WARRIORS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(NEON_GOLD);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titlePanel.add(titleLabel);
        
        // Table Panel with cosmic background
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Create the table model
        String[] columnNames = {"RANK", "WARRIOR", "SCORE", "MISSION DATE"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        
        // Create the table with space theme
        scoreTable = new JTable(tableModel);
        scoreTable.setRowHeight(35);
        scoreTable.setShowGrid(false);
        scoreTable.setIntercellSpacing(new Dimension(0, 0));
        scoreTable.setBackground(new Color(10, 15, 30));
        scoreTable.setForeground(Color.WHITE);
        scoreTable.setSelectionBackground(NEON_BLUE);
        scoreTable.setSelectionForeground(Color.WHITE);
        scoreTable.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Style the table header
        JTableHeader header = scoreTable.getTableHeader();
        header.setBackground(SPACE_BLUE);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBorder(BorderFactory.createLineBorder(NEON_BLUE));
        
        // Set column widths
        scoreTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        scoreTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        scoreTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        scoreTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        
        // Center align all columns with custom renderer
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Rank column with special styling
                if (column == 0) {
                    if (row == 0) {
                        comp.setForeground(NEON_GOLD); // Gold for 1st place
                    } else if (row == 1) {
                        comp.setForeground(new Color(192, 192, 192)); // Silver for 2nd place
                    } else if (row == 2) {
                        comp.setForeground(new Color(205, 127, 50)); // Bronze for 3rd place
                    } else {
                        comp.setForeground(Color.WHITE);
                    }
                    comp.setFont(new Font("Arial", Font.BOLD, 16));
                } 
                // Score column with special styling
                else if (column == 2) {
                    comp.setForeground(NEON_GREEN);
                    comp.setFont(new Font("Arial", Font.BOLD, 14));
                }
                // Default styling for other columns
                else {
                    comp.setForeground(isSelected ? Color.WHITE : table.getForeground());
                    comp.setFont(new Font("Arial", Font.PLAIN, 14));
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                return comp;
            }
        };
        
        for (int i = 0; i < scoreTable.getColumnCount(); i++) {
            scoreTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Create custom scroll pane
        JScrollPane scrollPane = new JScrollPane(scoreTable) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create a translucent background
                g2d.setColor(new Color(10, 20, 40, 200));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw border
                g2d.setColor(NEON_BLUE);
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                
                super.paintComponent(g);
            }
        };
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        
        // Close button with cosmic style
        JButton closeButton = new JButton("RETURN TO BASE") {
            private boolean isHovered = false;
            
            {
                // Initialize button with mouse listeners
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background gradient
                Color darkColor = NEON_BLUE.darker().darker();
                Color brightColor = isHovered ? NEON_BLUE.brighter() : NEON_BLUE;
                
                GradientPaint gradient = new GradientPaint(
                        0, 0, darkColor,
                        0, getHeight(), brightColor);
                
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Border
                g2d.setColor(isHovered ? Color.WHITE : NEON_BLUE);
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                
                // Text
                g2d.setFont(getFont());
                g2d.setColor(Color.WHITE);
                
                // Center text
                java.awt.FontMetrics metrics = g2d.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                
                g2d.drawString(getText(), x, y);
                
                // Glow effect when hovered
                if (isHovered) {
                    g2d.setColor(new Color(NEON_BLUE.getRed(), NEON_BLUE.getGreen(), NEON_BLUE.getBlue(), 50));
                    g2d.fillRoundRect(3, 3, getWidth()-6, getHeight()-6, 15, 15);
                }
            }
        };
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(NEON_BLUE);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setPreferredSize(new Dimension(200, 40));
        
        buttonPanel.add(closeButton);
        
        // Add action to close button
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the scoreboard window
            }
        });
        
        // Assemble the main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add to frame
        setContentPane(mainPanel);
        
        pack();
        setLocationRelativeTo(null); // Center on screen
    }
    
    private void loadTopScores() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT u.username, s.score, s.game_date " +
                          "FROM scores s " +
                          "JOIN users u ON s.user_id = u.user_id " +
                          "ORDER BY s.score DESC " +
                          "LIMIT 10";
            
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            
            // Clear existing data
            tableModel.setRowCount(0);
            
            // Format for dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            
            // Add rows to table
            int rank = 1;
            while (rs.next()) {
                String username = rs.getString("username");
                int score = rs.getInt("score");
                Date gameDate = rs.getTimestamp("game_date");
                String formattedDate = dateFormat.format(gameDate);
                
                Object[] row = {rank, username, score, formattedDate};
                tableModel.addRow(row);
                
                rank++;
            }
            
            // If there are no scores yet, add a placeholder row
            if (tableModel.getRowCount() == 0) {
                Object[] placeholderRow = {"-", "NO WARRIORS YET", "-", "-"};
                tableModel.addRow(placeholderRow);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading scoreboard: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResources(ps, rs);
        }
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
    
    // Cosmic gradient panel for title
    class CosmicPanel extends JPanel {
        public CosmicPanel() {
            setOpaque(false);
            setLayout(new BorderLayout());
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Create a cosmic gradient background
            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(20, 50, 100, 150),
                    getWidth(), getHeight(), new Color(50, 0, 80, 150));
            
            g2d.setPaint(gp);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            
            // Add a glowing border
            g2d.setColor(NEON_BLUE);
            g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
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
            java.util.logging.Logger.getLogger(ScoreboardForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ScoreboardForm().setVisible(true);
            }
        });
    }
}