package com.fianco;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.input.MouseEvent;


public class Piece extends Pane {
    private int xp;
    private int yp;
    private Ellipse piece;

    private boolean isWhite;

    public Piece(int xp, int yp, boolean isWhite) {
        move(xp, yp);
        this.isWhite = isWhite;
        this.piece = drawPiece(xp, yp, isWhite);

        this.piece.setOnMouseClicked(this::handleClick);
    }

    private void handleClick(MouseEvent event) {
        System.out.println("Piece clicked at: " + this.getCoord()); // Test message for debugging

        // Change the piece's color to green when clicked
        this.piece.setFill(Color.GREEN);
    }

    public Ellipse drawPiece(int xp, int yp, boolean isWhite) {
        Ellipse piece = new Ellipse(BoardController.TILE_SIZE * 0.4, BoardController.TILE_SIZE * 0.4); 
        if (isWhite) {
            piece.setFill(Color.WHITE);
        } else {
            piece.setFill(Color.BLACK);
        }

        piece.setTranslateX(xp * BoardController.TILE_SIZE + BoardController.TILE_SIZE / 2);
        piece.setTranslateY(yp * BoardController.TILE_SIZE + BoardController.TILE_SIZE / 2);

        return piece;
    }

    public Ellipse getPiece() {
        return piece;
    }

    public void move(int x, int y) {
        this.xp = x;
        this.yp = y;
    }

    public int getXp() {
        return xp;
    }

    public int getYp() {
        return yp;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public void setWhite(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public String toCoord(int x, int y){
        char column = (char) ('A' + x);
        int row = 9 - y;

        return String.valueOf(column) + row;
    }

    public String getCoord(){
        char column = (char) ('A' + this.xp);
        int row = 9 - this.yp;

        return String.valueOf(column) + row;
    }
}
