package io.fianco;

import java.util.Random;

public class ZobristHashing {
    private long[][][] zobristTable; // Zobrist table for pieces and positions
    private int boardSize;
    private Random random;

    public ZobristHashing(int boardSize) {
        this.boardSize = boardSize;
        zobristTable = new long[boardSize][boardSize][2]; // Only 2 types of pieces
        random = new Random();
        initializeZobristTable();
    }

    private void initializeZobristTable() {
        // Assign random bitstrings for each piece on each square
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                for (int pieceType = 0; pieceType < 2; pieceType++) {
                    zobristTable[i][j][pieceType] = random.nextLong();
                }
            }
        }
    }

    public long computeHash(int[][] board) {
        long hash = 0;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                int piece = board[i][j];
                if (piece == 1) {
                    // Player 1 piece
                    hash ^= zobristTable[i][j][0];
                } else if (piece == -1) {
                    // Player -1 piece
                    hash ^= zobristTable[i][j][1];
                }
            }
        }
        return hash;
    }

    public long updateHash(long currentHash, int startRow, int startCol, int endRow, int endCol, int pieceType) {
        // XOR out the piece at its old position
        currentHash ^= zobristTable[startRow][startCol][pieceType];
        // XOR in the piece at its new position
        currentHash ^= zobristTable[endRow][endCol][pieceType];
        return currentHash;
    }
}
