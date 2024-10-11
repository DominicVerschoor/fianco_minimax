package io.fianco;

import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import io.fianco.Bots.*;

public class GameLogic {
    private GameScreen screen;
    private int[][] board;
    private int currentPlayer;
    private boolean pieceSelected = false;
    private int selectedRow = -1, selectedCol = -1;
    private Player player1, player2;
    private ZobristHashing zobristHashing;

    public GameLogic(int[][] board, boolean isHuman1, boolean isHuman2, GameScreen screen) {
        this.screen = screen;
        this.board = board;
        this.currentPlayer = 1;
        this.zobristHashing = new ZobristHashing(screen.BOARD_SIZE);

        this.player1 = isHuman1 ? new HumanPlayer() : new BotPlayer(1, this);
        this.player2 = isHuman2 ? new HumanPlayer() : new BotPlayer(-1, this);
    }

    public void handleInput() {
        if (isGameOver(board) && !screen.getPause()) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new GameOver(screen.getGame(), -currentPlayer));
        } else if (isDraw(screen.getHistory()) && !screen.getPause()){
            ((Game) Gdx.app.getApplicationListener()).setScreen(new GameOver(screen.getGame(), 0));
        }
        else {
            getCurrentPlayer().takeTurn(this);
        }
    }

    private Player getCurrentPlayer() {
        return currentPlayer == 1 ? player1 : player2;
    }

    public void setCurrentPlayer(int player) {
        currentPlayer = player;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public int getPlayer() {
        return currentPlayer;
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
                    if (canCapture(board, row, col, currentPlayer)) {
                        return true; // If any piece can capture, return true
                    }
                }
            }
        }
        return false;
    }

    public boolean canCapture(int[][] board, int row, int col, int currentPlayer) {
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

    // Check for draws
    public boolean isDraw(List<int[][]> history) {
        HashMap<Long, Integer> hashCount = new HashMap<>();

        for (int[][] board : history) {
            long hash = zobristHashing.computeHash(board);
            // Keep track of how many times each state was seen
            hashCount.put(hash, hashCount.getOrDefault(hash, 0) + 1);

            // Check if any hash has been seen three times
            if (hashCount.get(hash) >= 3) {
                return true;
            }
        }

        return false;
    }

    public boolean isValidCapture(int[][] board, int startRow, int startCol, int middleRow, int middleCol, int endRow,
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

        screen.addBoard();

        if (isGameOver(board) || isDraw(screen.getHistory())) {
            screen.togglePause(true);
        }

    }

    public boolean isGameOver(int[][] board) {
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

    public interface Player {
        boolean isHuman();

        void takeTurn(GameLogic game);
    }

    public class HumanPlayer implements Player {
        @Override
        public boolean isHuman() {
            return true;
        }

        @Override
        public void takeTurn(GameLogic game) {
            if (Gdx.input.justTouched()) {
                int mouseX = Gdx.input.getX();
                int mouseY = Gdx.input.getY();
                int clickedCol = mouseX / GameScreen.TILE_SIZE;
                int clickedRow = (Gdx.graphics.getHeight() - mouseY) / GameScreen.TILE_SIZE;

                if (clickedRow >= 0 && clickedRow < GameScreen.BOARD_SIZE && clickedCol >= 0
                        && clickedCol < GameScreen.BOARD_SIZE) {
                    screen.togglePause(false);
                    if (!game.pieceSelected) {
                        if (game.board[clickedRow][clickedCol] == game.currentPlayer) {
                            game.selectedRow = clickedRow;
                            game.selectedCol = clickedCol;
                            game.pieceSelected = true;
                        }
                    } else {
                        if (game.isValidMove(game.selectedRow, game.selectedCol, clickedRow, clickedCol)) {
                            game.makeMove(game.selectedRow, game.selectedCol, clickedRow, clickedCol);
                            game.currentPlayer = -game.currentPlayer;
                        }
                        if (game.board[clickedRow][clickedCol] == game.currentPlayer) {
                            game.selectedRow = clickedRow;
                            game.selectedCol = clickedCol;
                            game.pieceSelected = true;
                        } else {
                            game.pieceSelected = false;
                        }
                    }
                }
            }
        }
    }

    public class BotPlayer implements Player {
        // private RandomBot bot;
        private MinimaxBot bot;

        public BotPlayer(int player, GameLogic logic) {
            // this.bot = new RandomBot(player, screen);
            this.bot = new MinimaxBot(player, screen, logic);
        }

        @Override
        public boolean isHuman() {
            return false;
        }

        @Override
        public void takeTurn(GameLogic game) {
            if (!screen.getPause()) {
                int[] botMove = bot.makeBotMove(game.board);
                if (botMove != null) {
                    game.makeMove(botMove[0], botMove[1], botMove[2], botMove[3]);
                    game.currentPlayer = -game.currentPlayer;
                }
            }
        }
    }
}
