package io.fianco.Bots;

import java.util.ArrayList;
import java.util.List;

import io.fianco.GameScreen;

public abstract class Bot {

    public int[] makeBotMove(int[][] board, int currentPlayer) {
        return null;
    }

    // This method combines both capture and regular move checks
    public List<int[]> getMoves(int[][] board, int currentPlayer) {
        List<int[]> captureMoves = new ArrayList<>();
        List<int[]> regularMoves = new ArrayList<>();

        for (int row = 0; row < GameScreen.BOARD_SIZE; row++) {
            for (int col = 0; col < GameScreen.BOARD_SIZE; col++) {
                if (board[row][col] == currentPlayer) {
                    // Check for capture moves
                    if (canCapture(board, row, col, currentPlayer)) {
                        for (int[] direction : getCaptureDirections(currentPlayer)) {
                            int newRow = row + direction[0];
                            int newCol = col + direction[1];
                            int jumpedRow = row + direction[0] / 2;
                            int jumpedCol = col + direction[1] / 2;

                            if (isValidCapture(board, row, col, jumpedRow, jumpedCol, newRow, newCol, currentPlayer)) {
                                captureMoves.add(new int[] { row, col, newRow, newCol });
                            }
                        }
                    }

                    // If no captures were found, check regular moves
                    if (captureMoves.isEmpty()) {
                        for (int[] direction : getMoveDirections(currentPlayer)) {
                            int newRow = row + direction[0];
                            int newCol = col + direction[1];

                            if (isValidMove(board, row, col, newRow, newCol)) {
                                regularMoves.add(new int[] { row, col, newRow, newCol });
                            }
                        }
                    }
                }
            }
        }

        // If capture moves are available, return only those; otherwise, return regular
        // moves
        return !captureMoves.isEmpty() ? captureMoves : regularMoves;
    }

    // Check if a move is a valid regular move
    private boolean isValidMove(int[][] board, int startRow, int startCol, int endRow, int endCol) {
        // Check bounds
        if (endRow < 0 || endRow >= GameScreen.BOARD_SIZE || endCol < 0 || endCol >= GameScreen.BOARD_SIZE) {
            return false;
        }

        // Check if the end position is empty
        return board[endRow][endCol] == 0;
    }

    // Check if the current piece can capture
    private boolean canCapture(int[][] board, int row, int col, int currentPlayer) {
        for (int[] direction : getCaptureDirections(currentPlayer)) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];
            int jumpedRow = row + direction[0] / 2;
            int jumpedCol = col + direction[1] / 2;

            if (isValidCapture(board, row, col, jumpedRow, jumpedCol, newRow, newCol, currentPlayer)) {
                return true;
            }
        }
        return false;
    }

    // Check if a move is a valid capture move, using the capture logic from the
    // GameLogic class
    private boolean isValidCapture(int[][] board, int startRow, int startCol, int middleRow, int middleCol, int endRow,
            int endCol, int currentPlayer) {
        // Check bounds
        if (endRow < 0 || endRow >= GameScreen.BOARD_SIZE || endCol < 0 || endCol >= GameScreen.BOARD_SIZE) {
            return false;
        }

        // Check that there is an opponent's piece to capture
        if (board[middleRow][middleCol] != -currentPlayer) {
            return false;
        }

        // Check that the end position is empty
        return board[endRow][endCol] == 0;
    }

    // Return possible move directions (up, down, left, right)
    private int[][] getMoveDirections(int currentPlayer) {
        if (currentPlayer == 1) {
            return new int[][] {
                    { 1, 0 }, { 0, 1 }, { 0, -1 }
            };
        } else {
            return new int[][] {
                    { -1, 0 }, { 0, 1 }, { 0, -1 }
            };
        }
    }

    // Return possible capture directions (captures are always 2 squares away)
    private int[][] getCaptureDirections(int currentPlayer) {
        // Depending on the player, capture directions can vary (adjust as necessary)
        if (currentPlayer == 1) {
            return new int[][] {
                    { 2, 2 }, { 2, -2 }
            };
        } else {
            return new int[][] {
                    { -2, 2 }, { -2, -2 }
            };
        }
    }
}
