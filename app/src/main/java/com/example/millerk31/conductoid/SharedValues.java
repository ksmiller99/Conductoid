package com.example.millerk31.conductoid;

/**
 * Created by millerk31 on 3/28/16.
 */
public class SharedValues {

    public static GameStatus levelGameStatus;
    public static int songLength;
    public static int cellsInUse;
    //controls cell highlight during playback
    public static int hlCol = -1;
    public static int hlRow = -1;

    public enum GameStatus {INITIAL, STARTED, RESET, FAILED, GRID_SONG_PLAYING, GRID_SONG_FINISHED, SUCCESS}



}
