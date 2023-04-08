package application;

import java.util.function.BiFunction;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class FourInARow extends Application
{
    private static final int ROWS = 6;
    private static final int COLUMNS = 7;
    private static final double CELL_SIZE = 100;

    private Circle[][] board;
    private boolean player1Turn;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        GridPane grid = new GridPane();
        grid.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        board = new Circle[ROWS][COLUMNS];
        
        for (int row = 0; row < ROWS; row++)
            for (int col = 0; col < COLUMNS; col++)
            {
                Circle circle = new Circle(CELL_SIZE / 2, Color.WHITE);
                board[row][col] = circle;
                grid.add(circle, col, row);
            }

        player1Turn = true;

        grid.setOnMouseClicked(e ->
        {
            if (player1Turn)
            {
                int col = (int) (e.getX() / CELL_SIZE);

                if (col >= 0 && col < COLUMNS)
                    for (int row = ROWS - 1; row >= 0; row--)
                        if (board[row][col].getFill() == Color.WHITE)
                        {
                            board[row][col].setFill(Color.YELLOW);
                            
                            if (isWin(row, col))
                            {
                                showWinner("Player 1");
                                return;
                            }

                            player1Turn = !player1Turn;
                            break;
                        }
            }

            if (!player1Turn)
            {
                int aiCol = aiMove();
                
                if (aiCol != -1)
                    for (int aiRow = ROWS - 1; aiRow >= 0; aiRow--)
                        if (board[aiRow][aiCol].getFill() == Color.WHITE)
                        {
                            board[aiRow][aiCol].setFill(Color.RED);
                            
                            if (isWin(aiRow, aiCol)) {
                                showWinner("AI Player");
                                return;
                            }
                            
                            player1Turn = !player1Turn;
                            break;
                        }
            }
        });

        Scene scene = new Scene(grid, COLUMNS * CELL_SIZE, ROWS * CELL_SIZE);
        primaryStage.setTitle("Connect 4");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    private int minimax(int depth, boolean isMaximizingPlayer)
    {
        // Evaluate the board and return the score if the depth is reached or the game is over
        if (depth == 0 || isGameOver())
            return evaluateBoard();

        int bestValue;
        
        if (isMaximizingPlayer)
        {
            bestValue = Integer.MIN_VALUE;
            
            for (int col = 0; col < COLUMNS; col++)
                if (makeMove(col, Color.RED))
                {
                    int value = minimax(depth - 1, false);
                    bestValue = Math.max(bestValue, value);
                    undoMove(col);
                }
        }
        else
        {
            bestValue = Integer.MAX_VALUE;
            
            for (int col = 0; col < COLUMNS; col++)
                if (makeMove(col, Color.YELLOW))
                {
                    int value = minimax(depth - 1, true);
                    bestValue = Math.min(bestValue, value);
                    undoMove(col);
                }
        }

        return bestValue;
    }

    private boolean makeMove(int col, Color color)
    {
        for (int row = ROWS - 1; row >= 0; row--)
            if (board[row][col].getFill() == Color.WHITE)
            {
                board[row][col].setFill(color);
                return true;
            }
        
        return false;
    }

    private void undoMove(int col)
    {
        for (int row = 0; row < ROWS; row++)
            if (board[row][col].getFill() != Color.WHITE)
            {
                board[row][col].setFill(Color.WHITE);
                break;
            }
    }

    private boolean isGameOver()
    {
        for (int row = 0; row < ROWS; row++)
            for (int col = 0; col < COLUMNS; col++)
                if (board[row][col].getFill() != Color.WHITE && isWin(row, col))
                    return true;
                
        return false;
    }

    private int evaluateBoard()
    {
        int score = 0;

        // Helper function to count consecutive pieces in a given direction
        BiFunction<Integer, Integer, Integer> countConsecutive = (dr, dc) ->
        {
            int consecutiveAI = 0;
            int consecutiveHuman = 0;
            int maxConsecutiveAI = 0;
            int maxConsecutiveHuman = 0;

            for (int row = 0; row < ROWS; row++)
                for (int col = 0; col < COLUMNS; col++)
                {
                    Color current = (Color) board[row][col].getFill();
                    
                    if (current == Color.RED || current == Color.YELLOW)
                    {
                        int r = row, c = col;
                        int count = 0;

                        while (r >= 0 && r < ROWS && c >= 0 && c < COLUMNS && board[r][c].getFill() == current)
                        {
                            count++;
                            r += dr;
                            c += dc;
                        }

                        if (current == Color.RED)
                        {
                            consecutiveAI = Math.max(consecutiveAI, count);
                            maxConsecutiveAI = Math.max(maxConsecutiveAI, consecutiveAI);
                        }
                        else
                        {
                            consecutiveHuman = Math.max(consecutiveHuman, count);
                            maxConsecutiveHuman = Math.max(maxConsecutiveHuman, consecutiveHuman);
                        }
                    }
                }

            return 5 * (maxConsecutiveAI - maxConsecutiveHuman);
        };

        // Evaluate horizontal lines
        score += countConsecutive.apply(0, 1);

        // Evaluate vertical lines
        score += countConsecutive.apply(1, 0);

        // Evaluate diagonal lines (top-left to bottom-right)
        score += countConsecutive.apply(1, 1);

        // Evaluate diagonal lines (bottom-left to top-right)
        score += countConsecutive.apply(-1, 1);

        return score;
    }

    private int aiMove()
    { 
        int bestCol = -1;
        int bestValue = Integer.MIN_VALUE;

        for (int col = 0; col < COLUMNS; col++)
            if (makeMove(col, Color.RED))
            {
                int value = minimax(3, false); // Change depth as desired
                
                if (value > bestValue)
                {
                    bestValue = value;
                    bestCol = col;
                }
                
                undoMove(col);
            }

        return bestCol;
    }

    private boolean isWin(int row, int col)
    {
        Color playerColor = (Color) board[row][col].getFill();

        // Check horizontal
        int count = 0;
        for (int c = 0; c < COLUMNS; c++)
        {
            count = (board[row][c].getFill() == playerColor) ? count + 1 : 0;
            
            if (count >= 4)
                return true;
        }

        // Check vertical
        count = 0;
        for (int r = 0; r < ROWS; r++)
        {
            count = (board[r][col].getFill() == playerColor) ? count + 1 : 0;
            
            if (count >= 4)
                return true;
        }
        
        // Check diagonal (top-left to bottom-right)
        int startRow = Math.max(0, row - col);
        int startCol = Math.max(0, col - row);
        count = 0;
        
        for (int r = startRow, c = startCol; r < ROWS && c < COLUMNS; r++, c++)
        {
            count = (board[r][c].getFill() == playerColor) ? count + 1 : 0;
            
            if (count >= 4)
                return true;
        }

        // Check diagonal (bottom-left to top-right)
        startRow = Math.min(ROWS - 1, row + col);
        startCol = Math.max(0, col - (ROWS - 1 - row));
        count = 0;
        
        for (int r = startRow, c = startCol; r >= 0 && c < COLUMNS; r--, c++)
        {
            count = (board[r][c].getFill() == playerColor) ? count + 1 : 0;
            
            if (count >= 4)
                return true;
        }

        return false;
    }


    private void showWinner(String winner)
    {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Connect 4");
        alert.setHeaderText("Game Over");
        alert.setContentText(winner + " wins!");
        alert.showAndWait();
        javafx.application.Platform.exit();
    }
}
