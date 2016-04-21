package com.example.millerk31.conductoid;

/**
 * Created by millerk31 on 3/28/16.
 */
public class SharedValues {

    public static Status levelStatus;
    public static int songLength;
    public static int measureSoundResource;
    public static String gridPosistions; //when a measure is dropped, this is the CSV list of valid grid positions for that measure
    //controls cell highlight during playback
    public static int hlCol = -1;
    public static int hlRow = -1;

    public enum Status {INITIAL, STARTED, FAILED}

}
