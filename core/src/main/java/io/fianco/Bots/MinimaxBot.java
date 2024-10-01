package io.fianco.Bots;

import java.util.Arrays;
import java.util.List;

public class MinimaxBot extends Bot {
    private int depth = 3;

    private class BestMove {
        int[] move;
        int score;

        public BestMove(int[] move, int score) {
            this.move = move;
            this.score = score;
        }
    }

    public MinimaxBot(int player){
        super(player);
    }

    @Override
    public int[] makeBotMove(int[][] board) {
        BestMove bestMove = negaMax(board, this.depth, -99999, 99999, player, true);

        return bestMove.move; // No valid moves
    }

    private BestMove negaMax(int[][] board, int depth, int a, int b, int currentPlayer, boolean isRoot) {
        if (isGameOver(board))
            return new BestMove(null, (currentPlayer == player) ? 10000 : -10000);
        if (depth == 0)
            return new BestMove(null, evaluate(board));
       
        int maxScore = Integer.MIN_VALUE;
        int[] bestMove = null;
        List<int[]> possibleMoves = getMoves(board, currentPlayer);

        for (int[] move : possibleMoves) {
            int[][] newBoard = copyBoard(board);

            simulateMove(newBoard, move[0], move[1], move[2], move[3]);

            BestMove res = negaMax(newBoard, depth - 1, -b, -a, -currentPlayer, false);
            int eval = -res.score;

            System.out.println(" Depth: " + depth + " (" + move[0] + ", " + move[1] + ") " + "(" + move[2] + ", " + move[3] + "): " + eval + " root: " + isRoot );
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

    private int evaluate(int[][] board) {
        int pieceDiff = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                pieceDiff += board[i][j] == player ? 10 : -10;
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

    private boolean isGameOver(int[][] board) {
        boolean hasWhite = false;
        boolean hasBlack = false;
        for (int i = 0; i < board.length; i++) {
            if (board[0][i] == -1 || board[board.length-1][i] == 1)
                return true;
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == 1) {
                    hasWhite = true;
                }
                if (board[i][j] == -1) {
                    hasBlack = true;
                }
            }
        }

        return !(hasWhite && hasBlack);
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    public List<int[]> getMoves(int[][] board, int currentPlayer) {
        return super.getMoves(board, currentPlayer);
    }
}
