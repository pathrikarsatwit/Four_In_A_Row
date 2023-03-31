package application;
import java.util.Scanner;
public class FourInARow {

	// Define constants
    private static final int ROWS = 6;
    private static final int COLS = 7;
    private static final int MAX_MOVES = ROWS * COLS;
    private static final char PLAYER1_TOKEN = 'X';
    private static final char PLAYER2_TOKEN = 'O';
    private static final char EMPTY_SPACE = ' ';

    // Initializes the board with empty spaces
    private static void initializeBoard(char[][] board)
    {
        for(int i = 0; i < ROWS; i++)
            for(int j = 0; j < COLS; j++)
                board[i][j] = EMPTY_SPACE;
    }

    // Prints the current state of the board
    private static void printBoard(char[][] board)
    {
        System.out.println(" 1 2 3 4 5 6 7");
        
        for(int i = 0; i < ROWS; i++)
        {
            for(int j = 0; j < COLS; j++)
                System.out.print("|" + board[i][j]);
                
            System.out.println("|");
        }
        
        System.out.println("---------------");
    }

    // Gets the player's move and validates it
    private static int getPlayerMove(Scanner scanner)
    {
        int col;
        
        while(true)
        {
            System.out.print("Enter your move(1-7): ");
            col = scanner.nextInt() - 1;
            
            if(col >= 0 && col < COLS)
                return col;
            
            System.out.println("Invalid move. Please try again.");
        }
    }
    
    // Makes a move on the board
    private static void makeMove(char[][] board, int col, char token)
    {
        for(int i = ROWS - 1; i >= 0; i--)
            if(board[i][col] == EMPTY_SPACE)
            {
                board[i][col] = token;
                return;
            }
    }

    // Gets the AI's move
    private static int getAIMove(char[][] board)
    {
        // Choose a random move for now
        int col;
        
        do
        {
            col = (int)(Math.random() * COLS);
        }while(board[0][col] != EMPTY_SPACE);
        
        return col;
    }

    // Checks if a player has won the game
    private static boolean hasWon(char[][] board, int col, char token)
    {
        // Check for a horizontal win
        for(int i = 0; i < ROWS; i++)
        {
            int count = 0;
            
            for(int j = 0; j < COLS; j++)
                if(board[i][j] == token)
                {
                    count++;
                    
                    if(count == 4)
                        return true;
                }
                else
                    count = 0;
        }

        // Check for a vertical win
        for(int j = 0; j < COLS; j++)
        {
            int count = 0;
            
            for(int i = 0; i < ROWS; i++)
            {
                if(board[i][j] == token)
                {
                    count++;
                    
                    if(count == 4)
                        return true;
                }
                else
                    count = 0;
            }
        }

        // Check for a diagonal win(top-left to bottom-right)
        for(int i = 0; i <= ROWS - 4; i++)
            for(int j = 0; j <= COLS - 4; j++)
            {
                int count = 0;
                
                for(int k = 0; k < 4; k++)
                    if(board[i+k][j+k] == token)
                    {
                        count++;
                        
                        if(count == 4)
                            return true;
                    }
                    else
                        break;
            }

        // Check for a diagonal win(top-right to bottom-left)
        for(int i = 0; i <= ROWS - 4; i++)
            for(int j = COLS - 1; j >= 3; j--)
            {
                int count = 0;
                
                for(int k = 0; k < 4; k++)
                    if(board[i+k][j-k] == token)
                    {
                        count++;
                        
                        if(count == 4)
                            return true;
                    }
                    else
                        break;
            }

        // No winner yet
        return false;
    }
    
    public static void main(String[] args)
    {
        // Create a new Connect 4 board
        char[][] board = new char[ROWS][COLS];
        initializeBoard(board);

        // Create a new scanner to read user input
        Scanner k = new Scanner(System.in);

        // Choose who goes first
        boolean player1Turn = true;
        System.out.println("Welcome to Connect 4!");
        System.out.print("Would you like to play against a person or a computer?(1 for pvp, 2 for computer)");
        int pvp = k.nextInt();
   
        System.out.print("Who goes first?(1 for you, 2 for the AI): ");
        int firstPlayer = k.nextInt();
        
        if(firstPlayer == 2)
            player1Turn = false;

        // Start the game loop
        int moveCount = 0;
        boolean gameOver = false;
        
        while(!gameOver && moveCount < MAX_MOVES)
        {
            // Print the current state of the board
            printBoard(board);

            // Get the current player's move
            int col;
            
            if(player1Turn)
            {
                col = getPlayerMove(k);
                makeMove(board, col, PLAYER1_TOKEN);
            } 
            else
            {
                col = getAIMove(board);
                makeMove(board, col, PLAYER2_TOKEN);
            }

            // Check if the game is over
            if(hasWon(board, col, player1Turn ? PLAYER1_TOKEN : PLAYER2_TOKEN))
            {
                printBoard(board);
                System.out.println("Player " +(player1Turn ? "1" : "2") + " wins!");
                gameOver = true;
            }
            else if(moveCount == MAX_MOVES - 1)
            {
                printBoard(board);
                System.out.println("It's a draw!");
                gameOver = true;
            }

            // Switch to the other player's turn
            player1Turn = !player1Turn;
            moveCount++;
        }
    }
}

