package io.fianco.Bots;

import java.util.List;
import java.util.Random;

import io.fianco.GameLogic;
import io.fianco.GameScreen;
import io.fianco.ZobristHashing;

public class MinimaxBot extends Bot {
    private int depth = 3;
    private ZobristHashing zobirst;
    private TranspositionTable transpositionTable;

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

    public MinimaxBot(int player, GameScreen game, GameLogic logic) {
        super(player, game, logic);
        this.zobirst = new ZobristHashing(game.BOARD_SIZE);
        this.transpositionTable = new TranspositionTable((int) Math.pow(2, 15));
    }

    @Override
    public int[] makeBotMove(int[][] board) {
        long initialHash = zobirst.computeHash(board);
        BestMove bestMove = negaMax(board, this.depth, -99999, 99999, player, zobirst, initialHash, transpositionTable);

        System.out.println();
        System.out.println(" Score: " + bestMove.score + " (" + bestMove.move[0] + ", " + bestMove.move[1] + ") " + "("
                + bestMove.move[2] + ", "
                + bestMove.move[3] + ")");

        return bestMove.move;
    }

    private BestMove negaMax(int[][] board, int depth, int a, int b, int currentPlayer, ZobristHashing zobrist,
            long zobristHash, TranspositionTable transTable) {
        int alphaOrig = a; // Original alpha for to get flag
        int maxScore = Integer.MIN_VALUE;
        int[] bestMove = null;

        // Check if the current board state is in the transposition table
        TranspositionTable.Entry entry = transTable.getEntry(zobristHash);
        // If entry is found at same or deeper depth we reuse computation
        if (entry != null && entry.depth >= depth) {
            if (entry.flag == TranspositionTable.EXACT) {
                return new BestMove(entry.bestMove, entry.score); // Return cached exact score
            } else if (entry.flag == TranspositionTable.LOWER_BOUND) {
                a = Math.max(a, entry.score); // New lower bound
            } else if (entry.flag == TranspositionTable.UPPER_BOUND) {
                b = Math.min(b, entry.score); // New upper bound
            }

            if (a >= b) {
                return new BestMove(null, entry.score); // Prune
            }
        }

        List<int[]> possibleMoves = getMoves(board, currentPlayer);

        // If game over we return a large negative score
        if (logic.isGameOver(board)) {
            return new BestMove(null, -10000);
        }
        // If reached defined depth evaluate and return
        if (depth == 0) {
            return new BestMove(null, evaluate(board, currentPlayer, possibleMoves.size()));
        }

        // Loop through all possible moves
        for (int[] move : possibleMoves) {
            int[][] newBoard = copyBoard(board);

            // Simulate the move and update the Zobrist hash
            simulateMove(newBoard, move[0], move[1], move[2], move[3]);
            // Update zobristhash for new board state (player 1 = pieceType 0, player -1 =
            // pieceType 1)
            long newZobristHash = zobrist.updateHash(zobristHash, move[0], move[1], move[2], move[3],
                    currentPlayer == 1 ? 0 : 1);

            // Recursive call with negated score and flipped alpha beta and new hash
            BestMove res = negaMax(newBoard, depth - 1, -b, -a, -currentPlayer, zobrist, newZobristHash, transTable);
            res.negate();

            // Update score if its new max
            if (res.score > maxScore) {
                maxScore = res.score;
                bestMove = move; // Update best move
            }

            // Update alpha
            a = Math.max(a, res.score);
            // Check Beta cutoff
            if (a >= b) {
                break;
            }
        }

        // Store the result in the transposition table
        int flag;
        // Determine flag
        if (maxScore <= alphaOrig) {
            // Upper bound (beta cutoff)
            flag = TranspositionTable.UPPER_BOUND;
        } else if (maxScore >= b) {
            // Lower bound (alpha cutoff)
            flag = TranspositionTable.LOWER_BOUND;
        } else {
            // Exact score
            flag = TranspositionTable.EXACT;
        }
        transTable.storeEntry(zobristHash, depth, maxScore, flag, bestMove);

        return new BestMove(bestMove, maxScore);
    }

    private int evaluate(int[][] board, int currentPlayer, int possibleMoves) {
        if (logic.isDraw(game.getHistory())){
            return -5000;
        }

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

        progressScore = furthestPlayer1Row - (board.length - 1 -
                furthestPlayerMinus1Row);

        finalScore += captureScore + possibleMoves + (currentPlayer * pieceScore) +
                (currentPlayer * progressScore)
                + randomFactor;
        return finalScore;
    }


    // Deep copy array
    private int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                newBoard[i][j] = board[i][j];
            }
        }
        return newBoard;
    }

    // simulates a capture
    private void simulateCapture(int[][] board, int startRow, int startCol, int endRow, int endCol) {
        if (Math.abs(endRow - startRow) == 2) {
            // This was a capture move, remove the jumped piece
            int jumpedRow = (startRow + endRow) / 2;
            int jumpedCol = (startCol + endCol) / 2;
            // Remove the captured piece
            board[jumpedRow][jumpedCol] = 0; 
        }
    }

    // simulates a move
    private void simulateMove(int[][] board, int startRow, int startCol, int endRow, int endCol) {
        // Move the piece
        board[endRow][endCol] = board[startRow][startCol];
        board[startRow][startCol] = 0;

        simulateCapture(board, startRow, startCol, endRow, endCol);
    }

    @Override
    public List<int[]> getMoves(int[][] board, int currentPlayer) {
        return super.getMoves(board, currentPlayer);
    }

    class TranspositionTable {

        class Entry {
            long zobristKey; // Stores Zobrist key
            int depth; // Depth at which this score was calculated
            int score; // Evaluation score
            int flag; // Exact, upper bound, or lower bound
            int[] bestMove;// Store best move in this situation

            public Entry(long zobristKey, int depth, int score, int flag, int[] bestMove) {
                this.zobristKey = zobristKey;
                this.depth = depth;
                this.score = score;
                this.flag = flag;
                this.bestMove = bestMove;
            }
        }

        private Entry[] table;
        private int tableSize;

        private static final int EXACT = 0;
        private static final int LOWER_BOUND = 1;
        private static final int UPPER_BOUND = 2;

        public TranspositionTable(int size) {
            this.tableSize = size;
            this.table = new Entry[tableSize];
        }

        // Compute the hash index using the Zobrist hash
        private int getHashIndex(long zobristHash) {
            // Use binary operation
            return (int) (zobristHash & (tableSize - 1)); // Keep index in range of table size
        }

        // Get entry from table based on hash
        public Entry getEntry(long zobristHash) {
            int index = getHashIndex(zobristHash);
            Entry entry = table[index];
            if (entry != null && entry.zobristKey == zobristHash) {
                return entry;
            }
            return null;
        }

        // Store an entry in the table
        public void storeEntry(long zobristHash, int depth, int score, int flag, int[] move) {
            int index = getHashIndex(zobristHash);
            Entry entry = table[index];

            // Replace only if the new entry is deeper or the current entry is empty
            if (entry == null || entry.depth <= depth) {
                table[index] = new Entry(zobristHash, depth, score, flag, move);
            }
        }
    }
}
