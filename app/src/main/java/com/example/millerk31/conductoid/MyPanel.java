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
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by millerk31 on 3/28/16.
 */
public class MyPanel extends View {

    float startX;
    float startY;
    float endX;
    float endY;
    float width;
    float height;

    static final int ROWS = 5;
    static final int COLS = 10;

    Paint bgPaint = new Paint();
    Paint activePaint = new Paint();

     //grid of canvas - keeps track of which graphic is each cell of the canvas
    Sprite myGrid[][];

    //list of exploding Sprites
    ArrayList<Sprite> expSprites;


    int cellHeight, cellWidth;

    public MyPanel(Context context, AttributeSet set) {
        super(context, set);

        myGrid = new Sprite[COLS][ROWS];
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

    class EndDraggingListener implements View.OnDragListener{

        @Override
        public boolean onDrag(View v, DragEvent event) {
            if(event.getAction() == DragEvent.ACTION_DROP){
                //                ((Button)v).setBackground(((Button) event.getLocalState()).getBackground());
                float x = event.getX();
                float y = event.getY();
                int row = (int)y/cellHeight;
                int col = (int)x/cellWidth;

                Button btn = (Button) event.getLocalState();
                Drawable drw = btn.getBackground();
                Bitmap bmp = ((BitmapDrawable)drw).getBitmap();
                Bitmap bmp2 = Bitmap.createScaledBitmap(bmp,cellWidth,cellHeight,false);

                //check to see if cell is active
                if(((row*COLS)+col) < SharedValues.songLength){
                    (myGrid[col][row])= new Sprite(bmp2);
                }else{
                    //explode?
                    expSprites.add(new Sprite(bmp2,x,y));
                }


            }
            return true;
        }
    }

    class Sprite{
        Bitmap bmp;
        int soundIndex;

        //these vars are for exploding
        float x, y, a, xSpeed, ySpeed, aSpeed;


        public Sprite(Bitmap bmp) {
            this.bmp = bmp;
        }

        public Sprite(Bitmap bmp, float x, float y  ){
            this(bmp);
            this.x = x;
            this.y = y;
            a = 0;  //rotation angle

            xSpeed = (float)(10.0 - (Math.random()*20))*5;
            ySpeed = (float)(10.0 - (Math.random()*20))*5;
            aSpeed = (float)(Math.random()*10)+10;

        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.cellHeight = h/5;
        this. cellWidth = w/10;

        this.width = w;
        this.height = h;

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(SharedValues.levelStatus == SharedValues.Status.FAILED){

        }

        canvas.drawARGB(255, 255, 255, 0);
        bgPaint.setARGB(255, 0, 0, 255);
        activePaint.setARGB(255,64,64,64);

        //draw the cells and contents
        for(int r = 0; r < ROWS; ++r){
            for(int c = 0; c < COLS; ++c) {
                //different color for droppable cells
                if((r*COLS + c)< SharedValues.songLength){
                    canvas.drawRect(c * cellWidth, r * cellHeight,(c * cellWidth)+cellWidth,(r * cellHeight)+cellHeight,activePaint);
                }
                if((myGrid[c][r]) != null) {
                    if(SharedValues.levelStatus == SharedValues.Status.FAILED){
                        expSprites.add(new Sprite(myGrid[c][r].bmp,c * cellWidth,r * cellHeight));
                        (myGrid[c][r]) = null;
                    }else {
                        canvas.drawBitmap((myGrid[c][r]).bmp, c * cellWidth, r * cellHeight, null);
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

//        if(SharedValues.drawingMode.equals("RECT")) {
//            canvas.drawRect(
//                    Math.min(SharedValues.startX, SharedValues.endX),
//                    Math.min(SharedValues.startY, SharedValues.endY),
//                    Math.max(SharedValues.startX, SharedValues.endX),
//                    Math.max(SharedValues.startY, SharedValues.endY)
//                    , bgPaint);
//        }else{
//            canvas.drawOval(
//
//                    Math.min(SharedValues.startX, SharedValues.endX),
//                    Math.min(SharedValues.startY, SharedValues.endY),
//                    Math.max(SharedValues.startX, SharedValues.endX),
//                    Math.max(SharedValues.startY, SharedValues.endY)
//                    , bgPaint);
//
//        }

        if(SharedValues.levelStatus == SharedValues.Status.FAILED)
            SharedValues.levelStatus = SharedValues.Status.INITIAL;
        invalidate();

    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        //from http://stackoverflow.com/questions/4166917/android-how-to-rotate-a-bitmap-on-a-center-point
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


}
