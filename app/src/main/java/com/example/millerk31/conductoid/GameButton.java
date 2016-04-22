package com.example.millerk31.conductoid;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Kevin on 4/21/2016.
 */
public class GameButton extends Button {
    private int soundResourceId;
    private int imageResourceId;
    private String validGridLocations;
    private boolean isReusable;

    public GameButton(Context context) {
        super(context);
    }

    public GameButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GameButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getSoundResourceId() {
        return soundResourceId;
    }

    public void setSoundResourceId(int soundResourceId) {
        this.soundResourceId = soundResourceId;
    }

    public String getValidGridLocations() {
        return validGridLocations;
    }

    public void setValidGridLocations(String validGridLocations) {
        this.validGridLocations = validGridLocations;
    }

    public boolean isReusable() {
        return isReusable;
    }

    public void setReusable(boolean reusable) {
        isReusable = reusable;
    }

    public float[] getXY() {
        return new float[]{this.getX(), this.getY()};
    }

    public void setXY(float[] xy) {
        this.setX(xy[0]);
        this.setY(xy[1]);
    }

    public void setAll(int soundResourceId,
                       int imageResourceId,
                       String validGridLocations,
                       boolean isReusable) {

        this.soundResourceId = soundResourceId;
        this.imageResourceId = imageResourceId;
        this.validGridLocations = validGridLocations;
        this.isReusable = isReusable;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
}
