package io.fianco;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Null;

import io.fianco.Bots.*;

public class GameLogic {
    private int[][] board;
    private int currentPlayer;
    private boolean pieceSelected = false;
    private int selectedRow = -1, selectedCol = -1;
    private boolean gameOver = false;

    private Bot bot;

    public GameLogic(int[][] board) {
        this.board = board;
        this.currentPlayer = 1;
        this.bot = new RandomBot();
    }

    public void handleInput() {
        if (this.gameOver) {
            System.out.println("Player: " + -currentPlayer + " WINS!");
            ((Game) Gdx.app.getApplicationListener()).setScreen(new GameOver(-currentPlayer));
        }

        if (this.bot != null) {
            // Bot's turn
            if (currentPlayer == -1) {
                int[] botMove = bot.makeBotMove(board, currentPlayer);
                if (botMove != null) {
                    makeMove(botMove[0], botMove[1], botMove[2], botMove[3]);
                    currentPlayer = -currentPlayer; // Switch to player's turn
                }
            }
        }
        if (Gdx.input.justTouched()) {
            int mouseX = Gdx.input.getX();
            int mouseY = Gdx.input.getY();
            int clickedCol = mouseX / GameScreen.TILE_SIZE;
            int clickedRow = (Gdx.graphics.getHeight() - mouseY) / GameScreen.TILE_SIZE;

            if (clickedRow >= 0 && clickedRow < GameScreen.BOARD_SIZE && clickedCol >= 0
                    && clickedCol < GameScreen.BOARD_SIZE) {
                System.out.println("ROW: " + clickedRow + " COL: " + clickedCol);
                if (!pieceSelected) {
                    // First click: select a piece
                    if (board[clickedRow][clickedCol] == currentPlayer) {
                        selectedRow = clickedRow;
                        selectedCol = clickedCol;
                        pieceSelected = true;
                    }
                } else {
                    // Second click: try to move the piece
                    if (isValidMove(selectedRow, selectedCol, clickedRow, clickedCol)) {
                        // Move the piece
                        makeMove(selectedRow,selectedCol, clickedRow, clickedCol);
                        
                        // End the turn and switch players
                        currentPlayer = -currentPlayer;
                    }
                    if (board[clickedRow][clickedCol] == currentPlayer) {
                        selectedRow = clickedRow;
                        selectedCol = clickedCol;
                        pieceSelected = true;
                    } else {
                        pieceSelected = false;
                    }
                }
            }
        }
    }

    private boolean isValidMove(int startRow, int startCol, int endRow, int endCol) {
        // Check that the target square is empty
        if (board[endRow][endCol] != 0)
            return false;

        // Check if it's a valid diagonal move (distance of 1 for regular moves)
        int rowDiff = endRow - startRow;
        int colDiff = Math.abs(endCol - startCol);

        if (hasCaptureAvailable()) {
            // Only allow capture moves
            // Check for captures (distance of 2)
            if (Math.abs(rowDiff) == 2 && colDiff == 2) {
                int jumpedRow = (startRow + endRow) / 2;
                int jumpedCol = (startCol + endCol) / 2;
                if (board[jumpedRow][jumpedCol] == -currentPlayer) {
                    return true; // Valid capture
                }
            }
        } else {
            // Allow vertical or horizontal moves (1 square)
            if (Math.abs(rowDiff) == 1 && colDiff == 0) {
                // Vertical movement by 1 square
                if (currentPlayer == 1 && rowDiff == 1)
                    return true; // White moves up
                if (currentPlayer == -1 && rowDiff == -1)
                    return true; // Black moves down
            } else if (rowDiff == 0 && Math.abs(colDiff) == 1) {
                // Horizontal movement by 1 square
                return true;
            }
        }

        return false; // Otherwise, the move is invalid
    }

    private boolean hasCaptureAvailable() {
        for (int row = 0; row < GameScreen.BOARD_SIZE; row++) {
            for (int col = 0; col < GameScreen.BOARD_SIZE; col++) {
                if (board[row][col] == currentPlayer) {
                    if (canCapture(row, col)) {
                        return true; // If any piece can capture, return true
                    }
                }
            }
        }
        return false;
    }

    private boolean canCapture(int row, int col) {
        // Check horizontal captures (both left and right)
        if (currentPlayer == 1) {
            if (isValidCapture(row, col, row + 1, col - 1, row + 2, col - 2))
                return true; // Left capture
            if (isValidCapture(row, col, row + 1, col + 1, row + 2, col + 2))
                return true; // Right capture
        } else {
            if (isValidCapture(row, col, row - 1, col - 1, row - 2, col - 2))
                return true; // Left capture
            if (isValidCapture(row, col, row - 1, col + 1, row - 2, col + 2))
                return true; // Right capture
        }

        return false;
    }

    private boolean isValidCapture(int startRow, int startCol, int middleRow, int middleCol, int endRow, int endCol) {
        // Check bounds
        if (endRow < 0 || endRow >= GameScreen.BOARD_SIZE || endCol < 0 || endCol >= GameScreen.BOARD_SIZE)
            return false;

        // Check that there is an opponent's piece to capture
        if (board[middleRow][middleCol] != -currentPlayer)
            return false;

        // Check that the end position is empty
        return board[endRow][endCol] == 0;
    }

    private void handleCapture(int startRow, int startCol, int endRow, int endCol) {
        if (Math.abs(endRow - startRow) == 2) {
            // This was a capture move, remove the jumped piece
            int jumpedRow = (startRow + endRow) / 2;
            int jumpedCol = (startCol + endCol) / 2;
            board[jumpedRow][jumpedCol] = 0; // Remove the captured piece
        }
    }

    private void makeMove(int startRow, int startCol, int endRow, int endCol) {
        // Move the piece
        board[endRow][endCol] = board[startRow][startCol];
        board[startRow][startCol] = 0; // Clear the original position

        handleCapture(startRow, startCol, endRow, endCol);

        isGameOver(endRow);
    }

    private void isGameOver(int endRow) {
        if ((endRow == 0 && currentPlayer == -1) ||
                (currentPlayer == 1 && endRow == GameScreen.BOARD_SIZE - 1)) {
            this.gameOver = true;
        }
    }
}
