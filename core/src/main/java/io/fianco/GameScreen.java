package io.fianco;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen implements Screen {
    private SpriteBatch batch;
    private BitmapFont font;
    private Stage stage;
    private Main game;
    private Skin skin;
    private int boardState;
    private List<int[][]> history;
    private boolean isPaused;

    private Texture blackTile, whiteTile, whitePiece, blackPiece;
    private TextButton back, forward, pause, begin, end;
    private GameLogic logic;

    public static final int BOARD_SIZE = 9;
    public static final int TILE_SIZE = 66;
    public static final int Y_OFFSET = 95;
    public static final int X_OFFSET = 95;

    public int[][] board = {
            { 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 0, 1, 0, 0, 0, 0, 0, 1, 0 },
            { 0, 0, 1, 0, 0, 0, 1, 0, 0 },
            { 0, 0, 0, 1, 0, 1, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, -1, 0, -1, 0, 0, 0 },
            { 0, 0, -1, 0, 0, 0, -1, 0, 0 },
            { 0, -1, 0, 0, 0, 0, 0, -1, 0 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1 },
    };

    public GameScreen(Main game, boolean isHuman1, boolean isHuman2) {
        this.game = game;
        this.logic = new GameLogic(board, isHuman1, isHuman2, this);
        this.history = new ArrayList<int[][]>();
        this.history.add(copyBoard(board));
        this.boardState = 0;
        this.isPaused = false;

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
        stage.addActor(begin);
        stage.addActor(back);
        stage.addActor(pause);
        stage.addActor(forward);
        stage.addActor(end);
    }

    private void createButtons() {
        begin = new TextButton("<<", skin);
        back = new TextButton("<", skin);
        forward = new TextButton(">", skin);
        pause = new TextButton("||", skin);
        end = new TextButton(">>", skin);

        begin.setPosition(Gdx.graphics.getWidth() * 0.23f, Gdx.graphics.getHeight() * 0.01f);
        begin.setSize(Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.111f);

        back.setPosition(Gdx.graphics.getWidth() * 0.35f, Gdx.graphics.getHeight() * 0.01f);
        back.setSize(Gdx.graphics.getWidth() * 0.08f, Gdx.graphics.getHeight() * 0.111f);

        pause.setPosition(Gdx.graphics.getWidth() * 0.45f, Gdx.graphics.getHeight() * 0.01f);
        pause.setSize(Gdx.graphics.getWidth() * 0.08f, Gdx.graphics.getHeight() * 0.111f);

        forward.setPosition(Gdx.graphics.getWidth() * 0.55f, Gdx.graphics.getHeight() * 0.01f);
        forward.setSize(Gdx.graphics.getWidth() * 0.08f, Gdx.graphics.getHeight() * 0.111f);

        end.setPosition(Gdx.graphics.getWidth() * 0.65f, Gdx.graphics.getHeight() * 0.01f);
        end.setSize(Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.111f);

        begin.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                togglePause(true);
                setBoard(0);
                return true;
            }
            return false;
        });

        back.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                togglePause(true);
                setBoard(1);
                return true;
            }
            return false;
        });

        forward.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                togglePause(true);
                setBoard(2);
                return true;
            }
            return false;
        });

        end.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                togglePause(true);
                setBoard(3);
                return true;
            }
            return false;
        });

        pause.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                togglePause(!isPaused);
                return true;
            }
            return false;
        });
    }

    public void togglePause(boolean paused) {
        this.isPaused = paused;
        if (isPaused)
            pause.setText("=>");
        else
            pause.setText("||");
    }

    public void setBoard(int button) {
        if (button == 0) {
            if (boardState > 0) {
                boardState = 0;
                // Set the board to the next state using a copy
                int[][] nextState = copyBoard(history.get(boardState));
                logic.setCurrentPlayer(-logic.getPlayer());
                this.board = nextState;
                logic.setBoard(nextState);
            }
        } else if (button == 1) {
            if (boardState > 0) {
                boardState--;
                // Set the board to the previous state using a copy
                int[][] previousState = copyBoard(history.get(boardState));
                logic.setCurrentPlayer(-logic.getPlayer());
                this.board = previousState;
                logic.setBoard(previousState);
            }
        } else if (button == 2) {
            if (boardState < history.size() - 1) {
                boardState++;
                // Set the board to the next state using a copy
                int[][] nextState = copyBoard(history.get(boardState));
                logic.setCurrentPlayer(-logic.getPlayer());
                this.board = nextState;
                logic.setBoard(nextState);
            }
        } else if (button == 3) {
            if (boardState < history.size() - 1) {
                boardState = history.size() - 1;
                // Set the board to the next state using a copy
                int[][] nextState = copyBoard(history.get(boardState));
                logic.setCurrentPlayer(-logic.getPlayer());
                this.board = nextState;
                logic.setBoard(nextState);
            }
        }
    }

    public boolean getPause() {
        return this.isPaused;
    }

    public Main getGame() {
        return this.game;
    }

    public List<int[][]> getHistory() {
        return history;
    }

    public void addBoard() {
        // If we are not at the most recent move (used 'back' to an earlier move),
        // truncate future states
        if (boardState < history.size() - 1) {
            history = history.subList(0, boardState + 1); // Remove all future moves beyond the current state
        }

        // Add the current board state to history
        history.add(copyBoard(board));
        boardState = history.size() - 1; // Set the board state index to the latest move
    }

    private int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                newBoard[i][j] = board[i][j]; // Copy each row (deep copy)
            }
        }
        return newBoard;
    }

    @Override
    public void render(float delta) {
        // Clear screen with white color
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        drawBoard();
        drawPieces();
        batch.end();

        stage.act(delta);
        stage.draw();

        logic.handleInput();
    }

    private void drawBoard() {
        // Loop through the board and draw the tiles
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Texture tile = (row + col) % 2 == 0 ? whiteTile : blackTile;
                batch.draw(tile, col * TILE_SIZE + X_OFFSET, row * TILE_SIZE + Y_OFFSET, TILE_SIZE, TILE_SIZE);

                // Draw the cell ID (row, col) in the top-left corner of each tile
                String cellId = convertToCoordinate(row, col);
                font.setColor(Color.BLACK); // Set the font color to black or any other color that fits
                font.getData().setScale(1); // Adjust the scale of the font if needed

                // Adjust the position where the text is drawn so it fits in the top-left corner
                float textX = col * TILE_SIZE + 5 + X_OFFSET;
                float textY = (row + 1) * TILE_SIZE - 5 + Y_OFFSET;
                font.draw(batch, cellId, textX, textY);
            }
        }
    }

    private void drawPieces() {
        // Loop through the board array and draw pieces based on the value
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                float pieceSize = TILE_SIZE * 0.8f;
                float pieceX = col * TILE_SIZE + (TILE_SIZE - pieceSize) / 2 + X_OFFSET;
                float pieceY = row * TILE_SIZE + (TILE_SIZE - pieceSize) / 2 + Y_OFFSET;
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
    public void resize(int width, int height) {
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
