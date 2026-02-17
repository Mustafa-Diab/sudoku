import javax.swing.*;
import java.awt.*;

public class Sudoku_Rules extends JFrame
{
    JPanel rulesPanel; 
    JButton button; 
    
    private Sudoku_Game parentGame; // Reference to the main Sudoku game window
    private static final long serialVersionUID = 1L; 
    
    // Constructor - builds the Rules window
    public Sudoku_Rules(Sudoku_Game parent)
    {
        this.parentGame = parent; // Store reference to game so we can return to it
        
        // Basic window setup
        setTitle("Sudoku Rules"); 
        setSize(750, 950); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setLocationRelativeTo(null); // Center the window on screen

        // Custom JPanel to draw decorative borders (see paintScreen)
        rulesPanel = new JPanel()
        {
            private static final long serialVersionUID = 1L; 
            protected void paintComponent(Graphics g) 
            {
                super.paintComponent(g); 
                paintScreen(g); // Draw custom white border lines
            }
        };
        
        rulesPanel.setBackground(Color.BLACK); // Dark theme background
        rulesPanel.setLayout(null); // Absolute positioning for all components

        // Title label for "Sudoku"
        JLabel titleLabel = new JLabel("Sudoku", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48)); // Large bold title
        titleLabel.setForeground(Color.WHITE); 
        titleLabel.setBounds(0, 80, 750, 60); // Centered across window
        rulesPanel.add(titleLabel);

        // Instructions for how to play Sudoku
        String[] instructions = {
            "Rules:",
            "     1. The Sudoku grid is 9x9.",
            "     2. Each row, column, and 3x3 box must contain numbers 1-9.",
            "     3. Numbers cannot repeat in a row, column, or 3x3 box.",
            "     4. The game starts with clues (numbers already filled in).",
            "     5. Choose your difficulty: easier = more clues, harder = fewer.",
            "     6. Select a number from the bottom bar, then click a cell to place it.",
            "     7. Fill the grid correctly to win the game.",
            "",
            "Features:",
            "     • Left-clicking a cell will highlight its entire row and column", 
            "       in light gray to help you track placements.",
            "     • Right-clicking a number highlights all identical numbers on", 
            "       the board in blue so you can easily spot them.",
            "     • Mistakes are tracked, make 3 mistakes and you lose the game."
        };
        
        // Positioning and styling for instructions
        int yPosition = 150; 
        Font instructionFont = new Font("Arial", Font.PLAIN, 20); 

        // Create a label for each instruction line
        for (String instruction : instructions) 
        {
            JLabel instructionLabel = new JLabel(instruction);
            instructionLabel.setFont(instructionFont); 
            instructionLabel.setForeground(Color.WHITE); // White text on black background
            instructionLabel.setBounds(70, yPosition, 650, 30); // Fixed spacing
            rulesPanel.add(instructionLabel);
            yPosition += 40; // Move down for next line
        }

        // "Return to Game" button setup
        Font font = new Font("Arial", Font.BOLD, 24); 
        button = new JButton("Return to Game"); 
        button.setFont(font); 
        button.setBackground(Color.WHITE); 
        button.setForeground(Color.BLACK); 
        button.setBounds(235, 770, 275, 50); // Centered horizontally
        button.setBorder(BorderFactory.createEmptyBorder()); 
        button.setFocusPainted(false); // Remove focus outline
        
        // Button action: close rules window and return to parent game
        button.addActionListener(e -> 
        {
            dispose(); // Close this Rules window
            parentGame.setVisible(true); // Show the main game again
        });
        
        // Add button and panel to frame
        rulesPanel.add(button); 
        add(rulesPanel); 
        setVisible(true); 
    }
    
    // Draws decorative white borders at top and bottom of window
    private void paintScreen(Graphics g) 
    {
        g.setColor(Color.WHITE);
        int lineHeight = 5; // Thickness of the border lines
        
        // Outer top and bottom lines
        int line1Width = 690;
        int x1 = (getWidth() - line1Width) / 2;
        g.fillRect(x1, 35, line1Width, lineHeight);   // Top outer border
        g.fillRect(x1, 865, line1Width, lineHeight); // Bottom outer border

        // Inner top and bottom lines
        int line2Width = 650;
        int x2 = (getWidth() - line2Width) / 2;
        g.fillRect(x2, 50, line2Width, lineHeight);   // Top inner border
        g.fillRect(x2, 850, line2Width, lineHeight);  // Bottom inner border
    }
}