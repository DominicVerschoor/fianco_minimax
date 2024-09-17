package io.fianco;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {
    private SpriteBatch batch;
    private BitmapFont font;
    private Main game;

    private Texture blackTile;
    private Texture whiteTile;
    private Texture whitePiece;
    private Texture blackPiece;
    private GameLogic logic;

    public static final int BOARD_SIZE = 9;
    public static final int TILE_SIZE = 64;

    // public int[][] board = {
    // { 1, 1, 1, 1, 1, 1, 1, 1, 1 },
    // { 0, 1, 0, 0, 0, 0, 0, 1, 0 },
    // { 0, 0, 1, 0, 0, 0, 1, 0, 0 },
    // { 0, 0, 0, 1, 0, 1, 0, 0, 0 },
    // { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    // { 0, 0, 0, -1, 0, -1, 0, 0, 0 },
    // { 0, 0, -1, 0, 0, 0, -1, 0, 0 },
    // { 0, -1, 0, 0, 0, 0, 0, -1, 0 },
    // { -1, -1, -1, -1, -1, -1, -1, -1, -1 },
    // };

    public int[][] board = {
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 1, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, -1, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    };

    public GameScreen(Main game, boolean isHuman1, boolean isHuman2) {
        this.game = game;
        this.logic = new GameLogic(board, isHuman1, isHuman2);
        batch = new SpriteBatch();
        font = new BitmapFont();

        blackTile = new Texture("lwjgl3\\src\\main\\resources\\dark.png");
        whiteTile = new Texture("lwjgl3\\src\\main\\resources\\light.png");
        whitePiece = new Texture("lwjgl3\\src\\main\\resources\\white_piece.png");
        blackPiece = new Texture("lwjgl3\\src\\main\\resources\\black_piece.png");
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 1, 1, 1); // Clear screen with white color
        batch.begin();
        drawBoard();
        drawPieces();
        batch.end();

        logic.handleInput();
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

    private void drawBoard() {
        // Loop through the board and draw the tiles
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Texture tile = (row + col) % 2 == 0 ? whiteTile : blackTile;
                batch.draw(tile, col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    private void drawPieces() {
        // Loop through the board array and draw pieces based on the value
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] == 1) {
                    // Draw white piece
                    batch.draw(whitePiece, col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                } else if (board[row][col] == -1) {
                    // Draw black piece
                    batch.draw(blackPiece, col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
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
