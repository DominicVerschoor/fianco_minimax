package io.fianco.Bots;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MinimaxBot extends Bot {
    private int depth = 3;

    private class BestMove {
        int[] move;
        int score;

        public BestMove(int[] move, int score) {
            this.move = move;
            this.score = score;
        }

        public void negate() {
            this.score = -score;
        }
    }

    public MinimaxBot(int player) {
        super(player);
    }

    @Override
    public int[] makeBotMove(int[][] board) {
        BestMove bestMove = negaMax(board, this.depth, -99999, 99999, player, true);

        return bestMove.move; // No valid moves
    }

    private BestMove negaMax(int[][] board, int depth, int a, int b, int currentPlayer, boolean isRoot) {
        int maxScore = Integer.MIN_VALUE;
        int[] bestMove = null;
        List<int[]> possibleMoves = getMoves(board, currentPlayer);

        if (isGameOver(board))
            return new BestMove(null, (currentPlayer == player) ? 10000 : -10000);
        if (depth == 0)
            return new BestMove(null, evaluate(board, currentPlayer, possibleMoves.size()));

        for (int[] move : possibleMoves) {
            int[][] newBoard = copyBoard(board);

            simulateMove(newBoard, move[0], move[1], move[2], move[3]);

            BestMove res = negaMax(newBoard, depth - 1, -b, -a, -currentPlayer, false);
            res.negate();

            System.out.println(" Depth: " + depth + " (" + move[0] + ", " + move[1] + ") " + "(" + move[2] + ", "
                    + move[3] + "): " + res.score + " root: " + isRoot);
            // Update the best move if this is the best evaluation so far
            if (res.score > maxScore) {
                maxScore = res.score;
                bestMove = move; // Store the best move
            }

            // Alpha-beta pruning
            a = Math.max(a, res.score);
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

    private int evaluate(int[][] board, int currentPlayer, int possibleMoves) {
        int finalScore = 0;
        int captureScore = 0;
        int pieceScore = 0;
        int progressScore = 0;
        int furthestPlayer1Row = -1;
        int furthestPlayerMinus1Row = board.length - 1;

        Random rand = new Random();
        int randomFactor = rand.nextInt(5) - 2;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] == 1) {
                    pieceScore++;
                    // For player 1, find the furthest row (highest row index)
                    furthestPlayer1Row = Math.max(furthestPlayer1Row, i);
                } else if (board[i][j] == -1) {
                    pieceScore--;
                    // For player -1, find the furthest row (lowest row index)
                    furthestPlayerMinus1Row = Math.min(furthestPlayerMinus1Row, i);
                }
            }
        }

        if (this.isCapture)
            captureScore += 2 * possibleMoves;

        progressScore = furthestPlayer1Row - (board.length - 1 - furthestPlayerMinus1Row);

        finalScore += captureScore + possibleMoves + (currentPlayer * pieceScore) + (currentPlayer * progressScore) + randomFactor;
        return finalScore;
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
            if (board[0][i] == -1 || board[board.length - 1][i] == 1)
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
