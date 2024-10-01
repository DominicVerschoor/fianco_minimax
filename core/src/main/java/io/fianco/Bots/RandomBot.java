package io.fianco.Bots;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomBot extends Bot {

    private Random random = new Random();

    public RandomBot(int player){
        super(player);
    }

    @Override
    public int[] makeBotMove(int[][] board) {
        List<int[]> validMoves = getMoves(board, player);
        if (!validMoves.isEmpty()) {
            return validMoves.get(random.nextInt(validMoves.size())); // Choose a random valid move
        }
        return null; // No valid moves
    }

    @Override
    public List<int[]> getMoves(int[][] board, int currentPlayer) {
        return super.getMoves(board, currentPlayer);
    }

}
