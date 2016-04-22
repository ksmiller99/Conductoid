package com.example.millerk31.conductoid;

/**
 * Created by Kevin on 4/21/2016.
 */
public class GameGrid {
    public static final int ROWS = 4;
    public static final int COLS = 8;


    //grid of canvas - keeps track of which graphic is each cell of the canvas
    public static Sprite myGrid[][];

    public static GameGrid instance = null;

    private GameGrid(int cols, int rows) {
        myGrid = new Sprite[cols][rows];
    }

    public static GameGrid getInstance() {
        if (instance == null) {
            instance = new GameGrid(COLS, ROWS);
        }
        return instance;
    }
}
