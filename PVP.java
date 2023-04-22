package application;

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

public class PVP <T extends Circle> extends Application
{

    private static final int ROWS = 6;
    private static final int COLUMNS = 7;
    private static final double CELL_SIZE = 100;

    private T[][] board;
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
        
        board = (T[][])new Circle[ROWS][COLUMNS];
        for (int row = 0; row < ROWS; row++)
            for (int col = 0; col < COLUMNS; col++)
            {
                T circle = (T)new Circle(CELL_SIZE / 2, Color.WHITE);
                board[row][col] = circle;
                grid.add(circle, col, row);
            }

        player1Turn = true;

        grid.setOnMouseClicked(e ->
        {
            int col = (int) (e.getX() / CELL_SIZE);
            
            if (col >= 0 && col < COLUMNS)
                for (int row = ROWS - 1; row >= 0; row--)
                    if (board[row][col].getFill() == Color.WHITE)
                    {
                        board[row][col].setFill(player1Turn ? Color.RED : Color.YELLOW);
                        if (isWin(row, col))
                        {
                            showWinner(player1Turn ? "Player 1" : "Player 2");
                            break;
                        }
                        
                        player1Turn = !player1Turn;
                        break;
                    }
        });

        Scene scene = new Scene(grid, COLUMNS * CELL_SIZE, ROWS * CELL_SIZE);
        primaryStage.setTitle("Connect 4");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
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
    }
}