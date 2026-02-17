import java.util.Arrays;
import java.util.Random;

public class Sudoku_Generator 
{
    // The size of the Sudoku grid
    private static final int GRID_SIZE = 9;
    
    // Random number generator for shuffling and random cell selection
    private static final Random rand = new Random();

    // Generates a Sudoku puzzle with a given number of clues. Ensures the puzzle is solvable and has a unique solution. 
    public static int[][] generateSudoku(int clues) 
    {
        // Start with an empty 9x9 board
        int[][] board = new int[GRID_SIZE][GRID_SIZE];

        // PHASE A: Fill the board completely using the solver with shuffled numbers
        int[] shuffled = getShuffledNumbers();
        Sudoku_Solver.solveBoard(board, shuffled);
        // At this point, 'board' contains a fully solved Sudoku puzzle

        // PHASE B: Remove numbers while ensuring solvability and uniqueness
        int cellsToRemove = GRID_SIZE * GRID_SIZE - clues; // total cells to erase

        while (cellsToRemove > 0) 
        {
            // Pick a random cell
            int row = rand.nextInt(GRID_SIZE);
            int col = rand.nextInt(GRID_SIZE);

            // Only try to remove if the cell is not already empty
            if (board[row][col] != 0) 
            {
                int backup = board[row][col];   // store the number in case we revert
                board[row][col] = 0;            // temporarily remove the number

                int[][] copy = deepCopy(board); // copy the board to test uniqueness

                // Only allow removal if the puzzle still has a unique solution
                if (!hasUniqueSolution(copy))
                    board[row][col] = backup;   // revert if removal breaks uniqueness
                else
                    cellsToRemove--; 			// removal is safe, count it
            }
        }

        // Return the final puzzle with exactly 'clues' numbers remaining
        return board;
    }

    // Checks if a given Sudoku board has exactly one solution. Returns true if unique, false if multiple solutions exist. 
    private static boolean hasUniqueSolution(int[][] board) 
    {
        // countSolutions will recursively explore all solutions
        return countSolutions(board, 0) == 1;
    }

    // Recursively counts the number of solutions for a given board. Exits early if more than 1 solution is found to save time. 
    private static int countSolutions(int[][] board, int count) 
    {
        for (int row = 0; row < GRID_SIZE; row++) 
        {
            for (int col = 0; col < GRID_SIZE; col++) 
            {
                if (board[row][col] == 0) // found empty cell
                {
                    // Try all numbers 1-9 in this cell
                    for (int num = 1; num <= GRID_SIZE; num++) 
                    {
                        if (Sudoku_Solver.isValidPlacement(board, num, row, col)) 
                        {
                            board[row][col] = num; // place number
                            count = countSolutions(board, count); // recurse

                            // Early exit: more than 1 solution found
                            if (count > 1) 
                            {
                                board[row][col] = 0; // backtrack
                                return count;        // no need to search further
                            }

                            board[row][col] = 0; // backtrack
                        }
                    }
                    return count; // no number worked here, return current count
                }
            }
        }
        return count + 1; // board completely filled = found one solution
    }

    
    // Generates a shuffled array of numbers 1-9. This randomization ensures different Sudoku solutions each time. 
    private static int[] getShuffledNumbers() 
    {
        int[] numbers = new int[GRID_SIZE];
        
        // Fill array with numbers 1..9
        for (int i = 0; i < GRID_SIZE; i++) 
            numbers[i] = i + 1;

        // Shuffles array
        for (int i = 0; i < GRID_SIZE - 1; i++) 
        {
            int j = i + rand.nextInt(GRID_SIZE - i); // pick random index from i..8
            int temp = numbers[i];
            numbers[i] = numbers[j];
            numbers[j] = temp;
        }
        
        return numbers; // return shuffled array
    }
    
    // Creates a deep copy of a Sudoku board. Necessary so that changes in one board don't affect others. 
    private static int[][] deepCopy(int[][] original) 
    {
        int[][] copy = new int[GRID_SIZE][GRID_SIZE];
        
        for (int i = 0; i < GRID_SIZE; i++) 
            copy[i] = Arrays.copyOf(original[i], GRID_SIZE);
        
        return copy;
    }
}