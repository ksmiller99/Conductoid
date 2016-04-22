package com.example.millerk31.conductoid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by millerk31 on 3/28/16.
 */
public class MyPanel extends View {

    public int cellHeight, cellWidth;
    float startX;
    float startY;
    float endX;
    float endY;
    float width;
    float height;
    Paint bluePaint = new Paint();
    Paint activePaint = new Paint();
    Paint bgPaint = new Paint();
    Paint redPaint = new Paint();
    //list of exploding Sprites
    ArrayList<Sprite> expSprites;
    GameGrid gg;

    public MyPanel(Context context, AttributeSet set) {
        super(context, set);

        gg = GameGrid.getInstance();
        expSprites = new ArrayList<Sprite>();

        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        endX=startX=event.getX();
                        endY=startY=event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        endX=event.getX();
                        endY=event.getY();
                        break;
                    default:
                }
                return true;
            }
        });

        setOnDragListener(new EndDraggingListener());

    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        //from http://stackoverflow.com/questions/4166917/android-how-to-rotate-a-bitmap-on-a-center-point
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.cellHeight = h / GameGrid.ROWS;
        this.cellWidth = w / GameGrid.COLS;

        this.width = w;
        this.height = h;

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Log.d("KSM", "onDraw: " + String.valueOf(SharedValues.hlCol) + ". " + String.valueOf(SharedValues.hlRow));

        float leftX;
        float topY;
        float rightX;
        float btmY;

        if (SharedValues.levelGameStatus == SharedValues.GameStatus.RESET) {

        }

        bgPaint.setARGB(255, 255, 255, 0);
        bluePaint.setARGB(255, 0, 0, 255);
        redPaint.setARGB(255, 255, 0, 0);
        activePaint.setARGB(255, 192, 192, 192);
        canvas.drawARGB(255, 255, 255, 0);

        //draw the cells and contents
        for (int r = 0; r < GameGrid.ROWS; ++r) {
            for (int c = 0; c < GameGrid.COLS; ++c) {

                //calculate top-left and lower-right points of cell
                leftX = c * cellWidth;
                topY = r * cellHeight;
                rightX = ((c * cellWidth) + cellWidth) - 5;
                btmY = (r * cellHeight) + cellHeight;

                //different color for droppable cells
                if ((r * GameGrid.COLS + c) < SharedValues.songLength) {
                    //make active cells grey
                    canvas.drawRect(leftX, topY, rightX, btmY, activePaint);

                    //put line between cells
                    canvas.drawRect(rightX, topY, rightX + 5, btmY, bgPaint);
                }

                //if cell contains Sprite, process it
                if ((GameGrid.myGrid[c][r]) != null) {
                    if (SharedValues.levelGameStatus == SharedValues.GameStatus.RESET) {
                        expSprites.add(new Sprite(GameGrid.myGrid[c][r].bmp, c * cellWidth, r * cellHeight));
                        (GameGrid.myGrid[c][r]) = null;
                    }else {
                        canvas.drawBitmap((GameGrid.myGrid[c][r]).bmp, c * cellWidth, r * cellHeight, null);
                    }
                }

                //highlight cell during playback if flagged
                //todo cell highlight during playback not working OnClickListener blocks thread?
                if ((c == SharedValues.hlCol) && (r == SharedValues.hlRow)) {
                    //Log.d("KSM", "setting highlight");
                    float[] corners = {leftX + 5, topY,
                            rightX - 5, topY,
                            rightX - 5, btmY,
                            leftX + 5, btmY,
                            leftX + 5, topY};
                    canvas.drawLines(corners, bluePaint);
                }

                //check if sprite is in valid cell and highlight in red if not
                if (GameGrid.myGrid[c][r] != null) {
                    boolean wrongCell = true;
                    String gp = (GameGrid.myGrid[c][r]).gridPositions;
                    String cells[] = (gp.split(":"));
                    for (String cell : cells) {
                        String items[] = cell.split(",");
                        if ((Integer.parseInt(items[0]) == c) && (Integer.parseInt(items[1]) == r)) {
                            wrongCell = false;
                            break;
                        }
                    }

                    if (wrongCell) {
                        float[] corners = {leftX + 5, topY,
                                rightX - 5, topY,
                                rightX - 5, btmY,
                                leftX + 5, btmY,
                                leftX + 5, topY};
                        canvas.drawLines(corners, redPaint);

                    }
                }
            }
        }

        //draw the exploding sprites
        for(Sprite sp: new ArrayList<Sprite>(expSprites)){
            canvas.drawBitmap(sp.bmp,sp.x, sp.y, null);
            sp.x += sp.xSpeed;
            sp.y += sp.ySpeed;
            sp.a += sp.aSpeed;
            if((sp.x > this.width) || (sp.y > this.height)||(sp.x <0) ||(sp.y <0)){

                expSprites.remove(sp);
            }

            //rotation causing Out Of Memory errors
            //sp.bmp = rotateBitmap(sp.bmp, sp.a);
            //System.gc();

        }


        if (SharedValues.levelGameStatus == SharedValues.GameStatus.RESET)
            SharedValues.levelGameStatus = SharedValues.GameStatus.INITIAL;
        invalidate();

    }

    //called when a measure is dropped on Panel
    class EndDraggingListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            if (event.getAction() == DragEvent.ACTION_DROP) {

                //convert canvas X/Y to grid colum/row
                float x = event.getX();
                float y = event.getY();
                int row = (int) y / cellHeight;
                int col = (int) x / cellWidth;

                //extract image from shadow and convert to bitmap same size as cell
                GameButton btn = (GameButton) event.getLocalState();
                Drawable drw = btn.getBackground();
                Bitmap bmp = ((BitmapDrawable) drw).getBitmap();
                Bitmap bmp2 = Bitmap.createScaledBitmap(bmp, cellWidth - 5, cellHeight, false);

                //check to see if cell is active and empty
                if ((((row * GameGrid.COLS) + col) < SharedValues.songLength) && (GameGrid.myGrid[col][row] == null)) {
                    //(GameGrid.myGrid[col][row]) = new Sprite(bmp2, SharedValues.gridPosistions, SharedValues.measureSoundResource);
                    (GameGrid.myGrid[col][row]) = new Sprite(bmp2, btn.getValidGridLocations(), btn.getSoundResourceId());
                    btn.setBackground(null);
                    btn.setEnabled(false);
                } else {
                    //not an active cell - explode it!
                    expSprites.add(new Sprite(bmp2, x, y));
                }
            }
            return true;
        }
    }

}
