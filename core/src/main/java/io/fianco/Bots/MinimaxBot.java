package io.fianco.Bots;

import java.util.List;
import io.fianco.GameLogic;
import io.fianco.GameScreen;

import java.util.ArrayList;


public class MinimaxBot extends Bot{
    @Override
    public int[] makeBotMove(int[][] board, int currentPlayer) {
        return null; // No valid moves
    }

    private int negaMax(int[][] board, int depth, int a, int b, int currentPlayer){
        if (depth == 0 || isGameOver(board, currentPlayer)){
            return evaluate(board, currentPlayer);
        }

        int maxScore = Integer.MIN_VALUE;
        List<int[]> possibleMoves = getMoves(board, currentPlayer);

        for (int[] move : possibleMoves){
            int[][] newBoard = board.clone();

            simulateMove(newBoard, move[0], move[1], move[2], move[3]);

            int score = -negaMax(newBoard, depth - 1, -b, -a, -currentPlayer);
            maxScore = Math.max(maxScore, score);
            a = Math.max(a, score);

            if (a >= b) {
                break;
            }
        }
        

        return maxScore;
    }

    private int evaluate(int[][] board, int currentPlayer){
        int pieceDiff = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                pieceDiff += board[i][j] == currentPlayer ? 10 : -10;
            }
        }

        return pieceDiff;
    }

    private void simulateCapture(int[][] board, int startRow, int startCol, int endRow, int endCol) {
        if (Math.abs(endRow - startRow) == 2) {
            // This was a capture move, remove the jumped piece
            int jumpedRow = (startRow + endRow) / 2;
            int jumpedCol = (startCol + endCol) / 2;
            board[jumpedRow][jumpedCol] = 0; // Remove the captured piece
        }
    }

    private void simulateMove(int[][] board, int startRow, int startCol, int endRow, int endCol) {
        // Move the piece
        board[endRow][endCol] = board[startRow][startCol];
        board[startRow][startCol] = 0; // Clear the original position

        simulateCapture(board, startRow, startCol, endRow, endCol);
    }

    private boolean isGameOver(int[][] board, int currentPlayer) {
        if (currentPlayer == 1){
            for (int i = 0; i < board.length; i++) {
                if (board[8][i] == currentPlayer) return true;
            }
        } else if (currentPlayer == -1){
            for (int i = 0; i < board.length; i++) {
                if (board[0][i] == currentPlayer) return true;
            }
        }

        return false;
    }


    @Override
    public List<int[]> getMoves(int[][] board, int currentPlayer) {
        return super.getMoves(board, currentPlayer);
    }

}
