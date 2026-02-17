public class Sudoku_Solver 
{
    // The size of the Sudoku grid (9x9)
    private static final int GRID_SIZE = 9;

    // Solve the Sudoku board using backtracking.
    public static boolean solveBoard(int[][] board, int[] order) 
    {
        // Loop through each row
        for (int row = 0; row < GRID_SIZE; row++) 
        {
            // Loop through each column in the current row
            for (int col = 0; col < GRID_SIZE; col++) 
            {
                // Find an empty cell (0 represents empty)
                if (board[row][col] == 0) 
                {
                    // Try each number in the given order for this cell
                    for (int num : order) 
                    {
                        // Check if placing 'num' here is valid (row, column, 3x3 box)
                        if (isValidPlacement(board, num, row, col))
                        {
                            board[row][col] = num; // Place the number

                            // Recursively attempt to solve the rest of the board
                            if (solveBoard(board, order)) 
                                return true; // Found a solution

                            // Backtrack: reset the cell and try next number
                            board[row][col] = 0;
                        }
                    }
                    // If no number works in this empty cell, backtracking occurs
                    return false;
                }
            }
        }
        // All cells are filled successfully → board is solved
        return true;
    }

    // Overloaded solveBoard method. Goes through numbers 1..9 in natural order (1, 2, 3, ... , 9)
    public static boolean solveBoard(int[][] board) 
    {
        int[] order = new int[GRID_SIZE];
        
        // Fill order with numbers 1..9
        for (int i = 0; i < GRID_SIZE; i++) 
            order[i] = i + 1;
        
        // Call the main solver with default order
        return solveBoard(board, order);
    }

    // Checks whether placing a number in a given cell is valid. Ensures no conflicts in row, column, or 3x3 box.
    public static boolean isValidPlacement(int[][] board, int number, int row, int col) 
    {
        // Check the row 
        for (int i = 0; i < GRID_SIZE; i++) 
            if (board[row][i] == number) return false; // conflict in row

        // Check the column 
        for (int i = 0; i < GRID_SIZE; i++) 
            if (board[i][col] == number) return false; // conflict in column

        // Check the 3x3 box 
        // Calculate the starting row and column of the 3x3 box
        int boxRow = row - row % 3;
        int boxCol = col - col % 3;

        // Loop through all cells in the 3x3 box
        for (int r = boxRow; r < boxRow + 3; r++) 
        {
            for (int c = boxCol; c < boxCol + 3; c++) 
                if (board[r][c] == number) return false; // conflict in box
        }

        // No conflicts -> placement is valid
        return true;
    }
}