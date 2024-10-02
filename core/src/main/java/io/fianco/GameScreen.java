package io.fianco;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen implements Screen {
    private SpriteBatch batch;
    private BitmapFont font;
    private Stage stage;
    private Main game;
    private Skin skin;
    private int boardState;
    private List<int[][]> history;


    private Texture blackTile, whiteTile, whitePiece, blackPiece;
    private TextButton back, forward;
    private GameLogic logic;

    public static final int BOARD_SIZE = 9;
    public static final int TILE_SIZE = 64;

    // public int[][] board = {
    //         { 1, 1, 1, 1, 1, 1, 1, 1, 1 },
    //         { 0, 1, 0, 0, 0, 0, 0, 1, 0 },
    //         { 0, 0, 1, 0, 0, 0, 1, 0, 0 },
    //         { 0, 0, 0, 1, 0, 1, 0, 0, 0 },
    //         { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    //         { 0, 0, 0, -1, 0, -1, 0, 0, 0 },
    //         { 0, 0, -1, 0, 0, 0, -1, 0, 0 },
    //         { 0, -1, 0, 0, 0, 0, 0, -1, 0 },
    //         { -1, -1, -1, -1, -1, -1, -1, -1, -1 },
    // };

    public int[][] board = {
    { 0, 0, 0, 1, 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    { 0, -1, 0, 0, 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    };

    public GameScreen(Main game, boolean isHuman1, boolean isHuman2) {
        this.game = game;
        this.logic = new GameLogic(board, isHuman1, isHuman2, this);
        this.history = new ArrayList<int[][]>();
        this.history.add(copyBoard(board));
        this.boardState = 0;

        batch = new SpriteBatch();
        font = new BitmapFont();

        skin = new Skin(Gdx.files.internal("lwjgl3\\src\\main\\resources\\Skins\\pixthulhu-ui.json"));
        blackTile = new Texture("lwjgl3\\src\\main\\resources\\dark.png");
        whiteTile = new Texture("lwjgl3\\src\\main\\resources\\light.png");
        whitePiece = new Texture("lwjgl3\\src\\main\\resources\\white_piece.png");
        blackPiece = new Texture("lwjgl3\\src\\main\\resources\\black_piece.png");
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        createButtons();

        // Add buttons to the stage
        stage.addActor(back);
        stage.addActor(forward);
    }

    private void createButtons() {
        back = new TextButton("<", skin);
        forward = new TextButton(">", skin);
        // Set positions and sizes for the buttons (relative to window size)
        back.setPosition(Gdx.graphics.getWidth() * 0.75f, Gdx.graphics.getHeight() * 0.01f); // 10% from left, 50% from
        // bottom
        back.setSize(Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.111f); // Button width=35%, height=8%

        // Set positions and sizes for the buttons (relative to window size)
        forward.setPosition(Gdx.graphics.getWidth() * 0.85f, Gdx.graphics.getHeight() * 0.01f); // 10% from left, 50%
        // from bottom
        forward.setSize(Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.111f); // Button width=35%,
        // height=8%

        back.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                setBoard(true);
                return true;
            }
            return false;
        });

        forward.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                setBoard(false);
                return true;
            }
            return false;
        });
    }

    public void setBoard(boolean back) {
        if (back) {
            if (boardState > 0) {
                boardState--;
                // Set the board to the previous state
                int[][] previousState = history.get(boardState);
                logic.setCurrentPlayer(-logic.getPlayer());
                this.board = previousState;
                logic.setBoard(previousState);
            }
        } else{
            if (boardState < history.size() - 1) {
                boardState++;
                // Set the board to the next state
                int[][] nextState = history.get(boardState);
                logic.setCurrentPlayer(-logic.getPlayer());
                this.board = nextState;
                logic.setBoard(nextState);
            }
        }
    }

    public void addBoard() {
        System.out.println(logic.getPlayer());
        // If we are not at the most recent move (used 'back' to an earlier move), truncate future states
        if (boardState < history.size() - 1) {
            history = history.subList(0, boardState + 1); // Remove all future moves beyond the current state
        }
    
        // Add the current board state to history
        history.add(copyBoard(board));
        boardState = history.size() - 1;  // Set the board state index to the latest move
    }

    private int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[board.length][];
        for (int i = 0; i < board.length; i++) {
            newBoard[i] = java.util.Arrays.copyOf(board[i], board[i].length); // Copy each row (deep copy)
        }
        return newBoard;
    }


    @Override
    public void render(float delta) {
        // Clear screen with white color
        ScreenUtils.clear(1, 1, 1, 1);

        batch.begin();
        drawBoard();
        drawPieces();
        batch.end();

        // Draw the UI on the right half of the screen
        stage.act(delta);
        stage.draw();

        logic.handleInput();
    }

    @Override
    public void resize(int width, int height) {
        // Update button positions based on the new window size
        stage.getViewport().update(width, height, true); // Ensure the stage resizes properly
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    private void drawBoard() {
        // Loop through the board and draw the tiles
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Texture tile = (row + col) % 2 == 0 ? whiteTile : blackTile;
                batch.draw(tile, col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                // Draw the cell ID (row, col) in the top-left corner of each tile
                String cellId = convertToCoordinate(row, col);
                font.setColor(Color.BLACK); // Set the font color to black or any other color that fits
                font.getData().setScale(1); // Adjust the scale of the font if needed

                // Adjust the position where the text is drawn so it fits in the top-left corner
                float textX = col * TILE_SIZE + 5; // Offset by 5 pixels to add a small margin
                float textY = (row + 1) * TILE_SIZE - 5; // Adjust to the top of the tile
                font.draw(batch, cellId, textX, textY);
            }
        }
    }

    private void drawPieces() {
        // Loop through the board array and draw pieces based on the value
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                float pieceSize = TILE_SIZE * 0.8f;
                float pieceX = col * TILE_SIZE + (TILE_SIZE - pieceSize) / 2; // Center piece on tile
                float pieceY = row * TILE_SIZE + (TILE_SIZE - pieceSize) / 2; // Center piece on tile
                if (board[row][col] == 1) {
                    // Draw white piece
                    batch.draw(whitePiece, pieceX, pieceY, pieceSize, pieceSize);
                } else if (board[row][col] == -1) {
                    // Draw black piece
                    batch.draw(blackPiece, pieceX, pieceY, pieceSize, pieceSize);
                }
            }
        }
    }

    private static String convertToCoordinate(int x, int y) {
        // Mapping x to letters A-I
        char column = (char) ('A' + x);

        // Mapping y to numbers 1-9 (just add 1 to the y coordinate)
        int row = y + 1;

        // Return the coordinate as a string
        return String.valueOf(column) + row;
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        blackTile.dispose();
        whiteTile.dispose();
        whitePiece.dispose();
        blackPiece.dispose();
    }
}
