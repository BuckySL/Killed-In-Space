package Authentication;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
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

public class ScoreboardForm extends JFrame {
    
    private JTable scoreTable;
    private DefaultTableModel tableModel;
    
    public ScoreboardForm() {
        initComponents();
        loadTopScores();
    }
    
    private void initComponents() {
        setTitle("Killed-At-Space - Scoreboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(500, 500));
        
        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("TOP 10 SCORES");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 100, 200));
        titlePanel.add(titleLabel);
        
        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Create the table model
        String[] columnNames = {"Rank", "Player", "Score", "Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        
        // Create the table
        scoreTable = new JTable(tableModel);
        scoreTable.setRowHeight(30);
        scoreTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        scoreTable.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < scoreTable.getColumnCount(); i++) {
            scoreTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
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
        getContentPane().add(mainPanel);
        
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
                Object[] placeholderRow = {"-", "No scores yet", "-", "-"};
                tableModel.addRow(placeholderRow);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading scoreboard: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResources(ps, rs);
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