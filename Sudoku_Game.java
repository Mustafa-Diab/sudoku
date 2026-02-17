import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Sudoku_Game extends JFrame implements MouseListener 
{
    private static final long serialVersionUID = 1L;

    // Game State Variables
    public static final int GRID_SIZE = 9;       // Sudoku is always 9x9
    public int[][] placeOption;                  // Stores current puzzle (board with numbers placed)
    public int roundCounter = 0;                 // Unused here, could track rounds
    public int numberMistakesMade = 0;           // How many mistakes the user made

    private int selectedBottomNumber = 0;        // Number chosen from bottom bar (0 = none selected)
    private int selectedRow = -1;                // Currently selected grid row (-1 means none)
    private int selectedCol = -1;                // Currently selected grid col (-1 means none)
    private int highlightNumber = 0;             // Number highlighted via right click
    private int clues = 0;                       // Number of starting clues (depends on difficulty)
    private boolean gameOver = false;            // True when game ends (win or lose)
    
    // Difficulty constants (how many numbers are given at start)
    private static final int EASY_CLUES = 38;
    private static final int MEDIUM_CLUES = 32;
    private static final int HARD_CLUES = 26;

    // Custom drawing panel (handles painting game board, numbers, etc.)
    private JPanel drawPanel;

    // Main Method
    public static void main(String[] args) { new Sudoku_Game(); }
    
    // Constructor for Display
    Sudoku_Game() 
    {
        super("Sudoku");                     // Window title
        setSize(1150, 1025);                 // Window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);         // Center the window

        // Custom drawing area
        drawPanel = new JPanel() 
        {
            private static final long serialVersionUID = 1L;

            // Called automatically whenever the panel needs to update
            protected void paintComponent(Graphics g) 
            {
                super.paintComponent(g);
                drawGame(g);                 // Draws the board, numbers, UI, etc.
            }
        };
        
        drawPanel.setLayout(null);           // Absolute positioning for buttons
        drawPanel.addMouseListener(this);    // Listen for clicks
        add(drawPanel);

        // Create UI buttons (Easy, Medium, Hard, Rules, Restart, Quit)
        createButtons();
        
        // Start with empty puzzle
        placeOption = new int[GRID_SIZE][GRID_SIZE];
        
        // Show window
        setVisible(true);                    
    }

    // Create Buttons for display
    private void createButtons() 
    {
        Font font = new Font("Arial", Font.BOLD, 24);
        addButton("Easy", 872, 75, 200, 50, font);
        addButton("Medium", 872, 225, 200, 50, font);
        addButton("Hard", 872, 375, 200, 50, font);
        addButton("Rules", 872, 525, 200, 50, font);
        addButton("Restart", 872, 675, 200, 50, font);
        addButton("Quit", 872, 825, 200, 50, font);
    }

    // Helper to create a button, attach behavior, and add it to the panel
    private void addButton(String title, int x, int y, int width, int height, Font font) 
    {
        JButton button = new JButton(title);
        button.setFont(font);                     // Button text font
        button.setBackground(Color.WHITE);        // Button background color
        button.setForeground(Color.BLACK);        // Button text color
        button.setBounds(x, y, width, height);    // Button position and size
        button.setFocusPainted(false);            // Removes blue focus outline

        // Delegate the button click to a handler method
        button.addActionListener(e -> handleButtonClick(title));

        drawPanel.add(button); // Add button to the game panel
    }

    // Decides what happens when a button is clicked
    private void handleButtonClick(String title) 
    {
        switch (title) 
        {
            case "Easy":
                handleDifficulty(EASY_CLUES);   // Start an easy game
                break;
            case "Medium":
                handleDifficulty(MEDIUM_CLUES); // Start a medium game
                break;
            case "Hard":
                handleDifficulty(HARD_CLUES);   // Start a hard game
                break;
            case "Rules":
                dispose();                       // Close game window
                new Sudoku_Rules(this);          // Open rules window
                break;
            case "Restart":
                resetGame();                     // Clear and restart the board
                break;
            default:                             // Quit button or anything else
                dispose();                       // Close the window
                break;
        }
        drawPanel.repaint(); // Always redraw after any action
    }

    // Shared logic for starting a game with a chosen difficulty
    private void handleDifficulty(int newClues) 
    {
        if (clues == 0) 
        {
            // No game started yet → initialize with the chosen difficulty
            clues = newClues;
            placeOption = Sudoku_Generator.generateSudoku(clues);
        } 
        else if (clues != newClues) 
            showPopUpMessage(); // If already playing with another difficulty, block & warn user
    }

    // Show warning if player tries to change difficulty mid-game
    private void showPopUpMessage() 
    {
        JOptionPane.showMessageDialog(this, 
                "You cannot change the difficulty mid-game.\n" +
                "Press Restart first, then choose your new difficulty.",
                "Difficulty Locked", JOptionPane.WARNING_MESSAGE);
    }

    // Drawing The Game
    private void drawGame(Graphics g) 
    {
        // Background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        int cellSize = 75;    // Each square is 75px
        int startX = 100;     // Where board starts horizontally
        int startY = 200;     // Where board starts vertically

        // Highlight selected row and column
        if (selectedRow != -1 && selectedCol != -1) 
        {
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(startX, startY + selectedRow * cellSize, GRID_SIZE * cellSize, cellSize);
            g.fillRect(startX + selectedCol * cellSize, startY, cellSize, GRID_SIZE * cellSize);
        }

        // Draw Sudoku grid lines (thin and thick)
        g.setColor(Color.WHITE);
        for (int j = 200; j < 875; j += 225) 
            for (int i = j + 75; i < j + 225 && i < 875; i += 75)
                g.fillRect(108, i, 664, 2); // horizontal small lines
        for (int j = 100; j < 775; j += 225) 
            for (int i = j + 75; i < j + 225 && i < 775; i += 75)
                g.fillRect(i, 208, 2, 664); // vertical small lines
        for (int i = 200; i <= 875; i += 225)
            g.fillRect(100, i, 680, 5);     // thick horizontal
        for (int i = 100; i <= 775; i += 225)
            g.fillRect(i, 200, 5, 680);     // thick vertical

        // Title
        Font titleFont = new Font("Arial", Font.BOLD, 50);
        g.setFont(titleFont);
        String title = "Sudoku";
        FontMetrics fm = g.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        int titleX = (getWidth() - titleWidth) / 2;
        g.setColor(Color.WHITE); 
        g.drawString(title, titleX - 130, 100);

        // Mistakes counter
        g.setFont(new Font("Arial", Font.BOLD, 25));
        g.drawString("Mistakes: " + numberMistakesMade + " / 3", titleX - 130, 160);

        // Draw board numbers
        for (int row = 0; row < GRID_SIZE; row++) 
            for (int col = 0; col < GRID_SIZE; col++) 
                displayPositions(g, row, col);

        // Draw number bar at bottom
        drawBottomNumbers(g);

        // Show endgame screen
        g.setFont(new Font("Arial", Font.BOLD, 150));
        if (gameOver && numberMistakesMade >= 3) 
        {
            g.setColor(Color.RED);
            g.drawString("You Lose!", titleX - 400, getHeight() / 2 + 55);
        } 
        else if (gameOver) 
        {
            g.setColor(Color.GREEN);
            g.drawString("You Win!", titleX - 400, getHeight() / 2 + 55);
        }
    }

    // Draws numbers 1–9 at bottom so user can pick one
    private void drawBottomNumbers(Graphics g) 
    {
        Font numFont = new Font("Arial", Font.BOLD, 30);
        g.setFont(numFont);

        for (int i = 1; i <= 9; i++) 
        {
            // Skip numbers that are already placed 9 times
            if (isNumberComplete(i)) 
                continue;
            
            int x = i * 86 - 20;
            int y = 935;

            // Highlight if currently selected
            if (i == selectedBottomNumber) 
            {
                g.setColor(Color.WHITE);
                g.fillRoundRect(x + 8, y - 17, 40, 40, 10, 10);
                g.setColor(Color.BLACK);
            } 
            else 
                g.setColor(Color.WHITE);
            
            g.drawString(String.valueOf(i), i * 86, 950);
        }
    }

    // Checks if a number already appears 9 times on board
    private boolean isNumberComplete(int number) 
    {
        int count = 0;
        for (int row = 0; row < GRID_SIZE; row++) 
            for (int col = 0; col < GRID_SIZE; col++) 
                if (placeOption[row][col] == number) count++;
        return count == 9;
    }

    // Draws a single cell's number on the Sudoku grid if the cell is not empty
    public void displayPositions(Graphics g, int row, int col) 
    {
        int cellSize = 75;       // Each Sudoku cell is 75x75 pixels
        int startX = 100;        // Top-left x-coordinate of the grid
        int startY = 200;        // Top-left y-coordinate of the grid
        int number = placeOption[row][col]; // Get the number stored at this cell (0 = empty)

        // Only draw if the cell has a number
        if (number != 0) 
        {
            String numStr = String.valueOf(number);  // Convert number to string for drawing

            // Compute the center position of the current cell
            int x = startX + col * cellSize + (cellSize / 2);
            int y = startY + row * cellSize + (cellSize / 2);

            // Get font metrics to measure text size (so we can center it properly)
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(numStr);   // Width of the number text
            int textHeight = fm.getAscent();          // Height of the number text

            // If this number is the highlighted one (from right-click), draw in blue, else white
            if (number == highlightNumber) 
                g.setColor(Color.BLUE);
            else 
                g.setColor(Color.WHITE);

            // Draw the number string centered within the cell
            // (x - textWidth/2 centers horizontally)
            // (y + textHeight/4 centers vertically)
            g.drawString(numStr, x - textWidth / 2, y + textHeight / 4 + 5);
        }    
    }

    // Resets everything for new game
    public void resetGame() 
    {
        roundCounter = 0;
        numberMistakesMade = 0;
        selectedRow = -1;
        selectedCol = -1;
        highlightNumber = 0;
        selectedBottomNumber = 0;
        gameOver = false;
        placeOption = new int[GRID_SIZE][GRID_SIZE]; 
        clues = 0;

        drawPanel.repaint();
    }

    // Handles all mouse click interactions (left + right click)
    public void mouseClicked(MouseEvent e) 
    {
        if (gameOver) return; // Ignore clicks if game has already ended

        // Get mouse click coordinates
        Point clickPoint = e.getPoint();
        int x = clickPoint.x;
        int y = clickPoint.y;

        // Grid and cell positioning setup
        int startGridX = 100;   // Top-left x of Sudoku grid
        int startGridY = 200;   // Top-left y of Sudoku grid
        int cellSize = 75;      // Width/height of each Sudoku cell

        // Calculate clicked row & column inside grid (if inside the grid area)
        int col = (x - startGridX) / cellSize;
        int row = (y - startGridY) / cellSize;

        // Left Click Handling
        if (SwingUtilities.isLeftMouseButton(e)) 
        {
            // Case 1: User clicks on the number selection bar at the bottom
            if (y >= 925 && y <= 955) // Bar Y-range
            {
                for (int i = 1; i <= 9; i++) 
                {
                    if (isNumberComplete(i)) continue; // Skip numbers that already appear 9 times

                    int numberX = i * 86; // Approximate x-center of number i on the bar

                    // If click is within "hitbox" of the number i
                    if (x >= numberX - 15 && x <= numberX + 15) 
                    {
                        selectedBottomNumber = i; // Store chosen number
                        drawPanel.repaint();      // Refresh display so highlight shows
                        return;                   // Exit method since selection handled
                    }
                }
            }

            // Case 2: User clicks inside the Sudoku grid 
            if (x >= startGridX && x <= startGridX + GRID_SIZE * cellSize && y >= startGridY && y <= startGridY + GRID_SIZE * cellSize) 
            {
                // If same cell clicked again → deselect
                if (selectedRow == row && selectedCol == col) 
                {
                    selectedRow = -1;
                    selectedCol = -1;
                } 
                else // Mark newly selected cell
                {
                    selectedRow = row;  
                    selectedCol = col;
                }

                // Placing a number 
                if (placeOption[row][col] == 0 && selectedBottomNumber != 0) 
                {
                    // Copy current board state for validation
                    int[][] solution = new int[GRID_SIZE][GRID_SIZE];
                    for (int r = 0; r < GRID_SIZE; r++)
                        for (int c = 0; c < GRID_SIZE; c++)
                            solution[r][c] = placeOption[r][c];

                    // Solve copy to get correct answer
                    Sudoku_Solver.solveBoard(solution);

                    // Check user’s selected number against solution
                    if (solution[row][col] == selectedBottomNumber)
                        placeOption[row][col] = selectedBottomNumber; // If its correct, place number
                    else
                        numberMistakesMade++; // Otherwise the user guessed wrong, increment mistake counter

                    selectedBottomNumber = 0; // Reset chosen number after placement

                    // Check if board is full
                    boolean full = true;
                    outer:
                    for (int r = 0; r < GRID_SIZE; r++)
                        for (int c = 0; c < GRID_SIZE; c++)
                            if (placeOption[r][c] == 0) // Found an empty cell
                            {
                                full = false;
                                break outer;
                            }

                    // End game conditions: board full OR 3 mistakes
                    if (full) gameOver = true;
                    if (numberMistakesMade >= 3) gameOver = true;
                }
                drawPanel.repaint(); // Refresh board after move
            }
        }

        // Right Click Handling
        if (SwingUtilities.isRightMouseButton(e)) 
        {
            // Right-click inside the Sudoku grid
            if (x >= startGridX && x <= startGridX + GRID_SIZE * cellSize && y >= startGridY && y <= startGridY + GRID_SIZE * cellSize) 
            {
                int clickedNumber = placeOption[row][col]; // Number in clicked cell

                if (clickedNumber != 0) // Only if the cell has a number
                {
                    // Toggle highlight on/off for this number
                    if (highlightNumber == clickedNumber) 
                        highlightNumber = 0; // If already highlighted, turn off
                    else 
                        highlightNumber = clickedNumber; // Otherwise, highlight it
                    drawPanel.repaint(); // Refresh board to show highlights
                }
            }
        }
    }

    // Required empty mouse methods
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}