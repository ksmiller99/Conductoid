package com.example.millerk31.conductoid;

import android.graphics.Bitmap;

/**
 * Created by Kevin on 4/21/2016.
 */
class Sprite {
    Bitmap bmp;
    int soundIndex;

    //these vars are for exploding
    float x,                    //current X postion
            y,                    //current Y position
            a,                    //current rotation angle
            xSpeed,               //explosion speed in X direction
            ySpeed,               //explosion speed in Y direction
            aSpeed;               //explosion rotation speed

    String gridPositions;       //CSV list of valid grid positions for this measure
    int measureSoundResource;   //resource ID for sound associated with this measure


    //constructor for when measure is dropped on an available cell
    public Sprite(Bitmap bmp, String gp, int msr) {
        this.bmp = bmp;
        this.gridPositions = gp;
        this.measureSoundResource = msr;
    }

    //exploding constructor
    public Sprite(Bitmap bmp, float x, float y) {
        this.bmp = bmp;
        this.x = x;
        this.y = y;
        a = 0;  //rotation angle

        //set random directions for exploding
        xSpeed = (float) (10.0 - (Math.random() * 20)) * 5;
        ySpeed = (float) (10.0 - (Math.random() * 20)) * 5;
        aSpeed = (float) (Math.random() * 10) + 10;

    }

}

