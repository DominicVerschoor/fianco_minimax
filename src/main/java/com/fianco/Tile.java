package com.fianco;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class Tile extends Rectangle {

    private int xp;
    private int yp;
    private Piece piece;

    public boolean hasPiece() {
        return piece != null;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public int getXp() {
        return xp;
    }

    public int getYp() {
        return yp;
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

    public Tile(boolean light, int x, int y) {
        this.xp = x;
        this.yp = y;
        setWidth(BoardController.TILE_SIZE);
        setHeight(BoardController.TILE_SIZE);

        relocate(x * BoardController.TILE_SIZE, y * BoardController.TILE_SIZE);

        setFill(light ? Color.CADETBLUE : Color.	DARKOLIVEGREEN);
    }
}
