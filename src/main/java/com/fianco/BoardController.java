package com.fianco;

import javafx.fxml.FXML;
import javafx.print.PageOrientation;
import javafx.scene.layout.Pane;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

public class BoardController {
    @FXML
    private Pane boardPane;

    public static final int SIZE = 9; // 8x8 board
    public static final int TILE_SIZE = 60; // Size of each tile

    private Tile[][] board = new Tile[SIZE][SIZE];

    private Group tileGroup = new Group();
    private Group pieceGroup = new Group();

    @FXML
    public void initialize() {
        createBoard();
        createPieces();
        boardPane.getChildren().addAll(tileGroup, pieceGroup);
    }

    public void createPieces() {
        for (int row = 0; row < SIZE; row++) {

            if (row == 0) {

                for (int col = 0; col < SIZE; col++) {
                    Piece piece = new Piece(col, row, false);
                    board[row][col].setPiece(piece);
                    pieceGroup.getChildren().add(piece.getPiece());
                }

            } else if (row <= 3 && row != 0) {
                int x1 = row; // Offset from left edge
                int x2 = SIZE - row - 1; // Offset from right edge

                Piece piece1 = new Piece(x1, row, false);
                Piece piece2 = new Piece(x2, row, false);

                board[x1][row].setPiece(piece1);
                board[x2][row].setPiece(piece2);
                pieceGroup.getChildren().addAll(piece1.getPiece(), piece2.getPiece());

            } else if (row >= 5 && row != 8) {
                // For rows 6 to 8: reverse the logic from rows 3 to 1
                int reverseRow = 8 - row; // Calculate corresponding row for reverse logic
                int x1 = reverseRow; // Offset from left edge
                int x2 = SIZE - reverseRow - 1; // Offset from right edge

                Piece piece1 = new Piece(x1, row, true);
                Piece piece2 = new Piece(x2, row, true);

                board[x1][row].setPiece(piece1);
                board[x2][row].setPiece(piece2);
                pieceGroup.getChildren().addAll(piece1.getPiece(), piece2.getPiece());

            } else if (row == 8) {

                for (int col = 0; col < SIZE; col++) {
                    Piece piece = new Piece(col, row, true);
                    board[row][col].setPiece(piece);
                    pieceGroup.getChildren().add(piece.getPiece());
                }
            }
        }
    }

    private void createBoard() {
        Pane root = new Pane();
        root.setPrefSize(SIZE * TILE_SIZE, SIZE * TILE_SIZE);
        root.getChildren().addAll(tileGroup, pieceGroup);

        for (int col = 0; col < SIZE; col++) {
            for (int row = 0; row < SIZE; row++) {
                Tile tile = new Tile((row + col) % 2 == 0, row, col);
                board[row][col] = tile;

                tileGroup.getChildren().add(tile);
            }
        }

        for (Tile[] tiles : board) {
            for (Tile tile : tiles) {
                System.out.println(tile.getXp());
                System.out.println(tile.getYp());
                System.out.println(tile.getCoord());
                System.out.println();
            }
        }
    }
}
