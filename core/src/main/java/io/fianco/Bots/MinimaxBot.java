package io.fianco.Bots;

import java.util.List;
import io.fianco.GameLogic;
import io.fianco.GameScreen;
import java.util.ArrayList;

public class MinimaxBot extends Bot {
    private int depth = 2;

    private class BestMove {
        int[] move;
        int score;

        public BestMove(int[] move, int score) {
            this.move = move;
            this.score = score;
        }

        public void flipScore() {
            this.score = -this.score;
        }
    }

    @Override
    public int[] makeBotMove(int[][] board, int currentPlayer) {
        BestMove bestMove = negaMax(board, this.depth, -99999, 99999, currentPlayer);

        return bestMove.move; // No valid moves
    }

    private BestMove negaMax(int[][] board, int depth, int a, int b, int currentPlayer) {
        if (depth == 0 || isGameOver(board, currentPlayer)) {
            return new BestMove(null, currentPlayer * evaluate(board, currentPlayer));
        }

        int maxScore = Integer.MIN_VALUE;
        int[] bestMove = null;
        List<int[]> possibleMoves = getMoves(board, currentPlayer);

        for (int[] move : possibleMoves) {
            int[][] newBoard = copyBoard(board);
            
            simulateMove(newBoard, move[0], move[1], move[2], move[3]);

            BestMove res = negaMax(newBoard, depth - 1, -b, -a, -currentPlayer);
            int eval = -res.score;
            // Update the best move if this is the best evaluation so far
            if (eval > maxScore) {
                maxScore = eval;
                bestMove = move; // Store the best move
            }

            // Alpha-beta pruning
            a = Math.max(a, eval);
            if (a >= b) {
                break; // Beta cutoff
            }
        }

        return new BestMove(bestMove, maxScore);
    }

    private int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[board.length][];
        for (int i = 0; i < board.length; i++) {
            newBoard[i] = java.util.Arrays.copyOf(board[i], board[i].length); // Copy each row (deep copy)
        }
        return newBoard;
    }    

    private int evaluate(int[][] board, int currentPlayer) {
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
        for (int i = 0; i < board.length; i++) {
            if (board[0][i] == -1 || board[8][i] == 1)
                return true;
        }

        return false;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    public List<int[]> getMoves(int[][] board, int currentPlayer) {
        return super.getMoves(board, currentPlayer);
    }
}
