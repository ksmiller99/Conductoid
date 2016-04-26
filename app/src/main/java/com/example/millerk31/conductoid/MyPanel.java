package com.example.millerk31.conductoid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
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
    Paint redPaint = new Paint();
    Paint bluePaint = new Paint();
    Paint greenPaint = new Paint();
    Paint activePaint = new Paint();
    Paint bgPaint = new Paint();
    //list of exploding Sprites
    ArrayList<Sprite> expSprites;
    GameGrid gg;
    int gridErrorCount;

    //intents to send messages to actvities
    Intent myPanelMessage;

    public MyPanel(Context context, AttributeSet set) {
        super(context, set);

        gg = GameGrid.getInstance();
        expSprites = new ArrayList<Sprite>();

        //count number of sprites in wrong cell
        gridErrorCount = 0;


        myPanelMessage = new Intent();
        myPanelMessage.setAction("myPanelMessage");

        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        endX = startX = event.getX();
                        endY = startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        endX = event.getX();
                        endY = event.getY();
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

        bgPaint.setARGB(255, 255, 255, 0);
        bluePaint.setARGB(255, 0, 0, 255);
        redPaint.setARGB(255, 255, 0, 0);
        greenPaint.setARGB(255, 0, 255, 0);
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
                    } else {
                        canvas.drawBitmap((GameGrid.myGrid[c][r]).bmp, c * cellWidth, r * cellHeight, null);
                    }
                }

                if (SharedValues.hlCol == c && SharedValues.hlRow == r) {
                    float[] corners = {
                            leftX, topY,
                            rightX - 5, topY,
                            rightX - 5, topY,
                            rightX - 5, btmY,
                            rightX - 5, btmY,
                            leftX, btmY,
                            leftX, btmY,
                            leftX, topY};

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
                            redPaint.setStrokeWidth(5);
                            canvas.drawLines(corners, redPaint);
                        } else {
                            greenPaint.setStrokeWidth(5);
                            canvas.drawLines(corners, greenPaint);
                        }
                    }
                }
            }
        }

        //draw the exploding sprites
        for (Sprite sp : new ArrayList<Sprite>(expSprites)) {
            canvas.drawBitmap(sp.bmp, sp.x, sp.y, null);
            sp.x += sp.xSpeed;
            sp.y += sp.ySpeed;
            sp.a += sp.aSpeed;
            if ((sp.x > this.width) || (sp.y > this.height) || (sp.x < 0) || (sp.y < 0)) {

                expSprites.remove(sp);
            }

            //rotation causing Out Of Memory errors
            //sp.bmp = rotateBitmap(sp.bmp, sp.a);
            //System.gc();

        }

        switch (SharedValues.levelGameStatus) {
            case RESET:
                SharedValues.levelGameStatus = SharedValues.GameStatus.INITIAL;
                gridErrorCount = 0;
                break;

            case GRID_SONG_FINISHED:
                if (gridErrorCount == 0) {
                    SharedValues.levelGameStatus = SharedValues.GameStatus.SUCCESS;
                    myPanelMessage.putExtra("status", "success");
                } else {
                    int score = SharedValues.songLength - gridErrorCount;
                    score = Math.round(((score / SharedValues.songLength) * 3));
                    switch (score) {

                        case 0:
                            SharedValues.levelGameStatus = SharedValues.GameStatus.FAILED;
                            myPanelMessage.putExtra("status", "score:0");
                            break;

                        case 1:
                            SharedValues.levelGameStatus = SharedValues.GameStatus.FAILED;
                            myPanelMessage.putExtra("status", "score:1");
                            break;

                        case 2:
                            SharedValues.levelGameStatus = SharedValues.GameStatus.FAILED;
                            myPanelMessage.putExtra("status", "score:2");
                            break;

                        case 3:
                            SharedValues.levelGameStatus = SharedValues.GameStatus.FAILED;
                            myPanelMessage.putExtra("status", "score:3");
                            break;

                        default:
                            SharedValues.levelGameStatus = SharedValues.GameStatus.FAILED;
                            myPanelMessage.putExtra("status", "score:Error");
                            Log.d("MyPanel", "onDraw unexpected score value: " + String.valueOf(score));
                            break;

                    }
                }
                getContext().sendBroadcast(myPanelMessage);
                myPanelMessage.removeExtra("status");
                break;

            default:
                break;

        }
        invalidate();

    }

    //called when a measure is dropped on Panel
    class EndDraggingListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            if (event.getAction() == DragEvent.ACTION_DROP) {

                //convert canvas X/Y to grid column/row
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
                    (GameGrid.myGrid[col][row]) = new Sprite(bmp2, btn.getValidGridLocations(), btn.getSoundResourceId());
                    if (!btn.isReusable()) {
                        btn.setBackground(null);
                        btn.setEnabled(false);
                    }

                    myPanelMessage.putExtra("status", "cellDropped");
                    getContext().sendBroadcast(myPanelMessage);
                    myPanelMessage.removeExtra("status");

                    //update error
                    //check if sprite is in valid cell and increment error count if not

                    boolean wrongCell = true;
                    String gp = (GameGrid.myGrid[col][row]).gridPositions;
                    String cells[] = (gp.split(":"));
                    for (String cell : cells) {
                        String items[] = cell.split(",");
                        if ((Integer.parseInt(items[0]) == col) && (Integer.parseInt(items[1]) == row)) {
                            wrongCell = false;
                            break;
                        }
                    }

                    if (wrongCell) {
                        gridErrorCount++;
                        Log.d("gridErrorCount, C,R", String.valueOf(gridErrorCount) + ", " + String.valueOf(col) + ", " + String.valueOf(row));
                    }

                    ++SharedValues.cellsInUse;
                    if (SharedValues.cellsInUse == SharedValues.songLength) {
                        //enable playback
                        myPanelMessage.putExtra("status", "gridFull");
                        getContext().sendBroadcast(myPanelMessage);
                        myPanelMessage.removeExtra("status");
                    }
                } else {
                    //not an active cell - explode it!
                    expSprites.add(new Sprite(bmp2, x, y));
                }
            }
            return true;
        }
    }

}
