package com.example.millerk31.conductoid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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

    Paint paint=new Paint();

    //each item is an image dragged to the canvas
    ArrayList<Sprite> sprites;

    //grid of canvas - keeps track of which graphic is each cell of the canvas
    int myGrid[][];

    int cellHeight, cellWidth;

    public MyPanel(Context context, AttributeSet set) {
        super(context, set);

        sprites = new ArrayList();
        myGrid = new int[5][10];

        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        SharedValuesXY.endX=SharedValuesXY.startX=event.getX();
                        SharedValuesXY.endY=SharedValuesXY.startY=event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        SharedValuesXY.endX=event.getX();
                        SharedValuesXY.endY=event.getY();
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
//                col -= bmp.getWidth()/2;
//                row -= bmp.getHeight()/2;
                sprites.add(new Sprite(bmp,col,row));
            }
            return true;
        }
    }

    class Sprite{
        Bitmap bmp;
        float col, row;

        public Sprite(Bitmap bmp, int col, int row) {
            this.bmp = bmp;
            this.col = col;
            this.row = row;
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.cellHeight = h/5;
        this. cellWidth = w/10;

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawARGB(255, 255, 255, 0);
        paint.setARGB(255, 0, 0, 255);

        for(Sprite sp:sprites){
            canvas.drawBitmap(sp.bmp,sp.col*cellWidth,sp.row*cellHeight,null);
        }

//        if(SharedValuesXY.drawingMode.equals("RECT")) {
//            canvas.drawRect(
//                    Math.min(SharedValuesXY.startX, SharedValuesXY.endX),
//                    Math.min(SharedValuesXY.startY, SharedValuesXY.endY),
//                    Math.max(SharedValuesXY.startX, SharedValuesXY.endX),
//                    Math.max(SharedValuesXY.startY, SharedValuesXY.endY)
//                    , paint);
//        }else{
//            canvas.drawOval(
//
//                    Math.min(SharedValuesXY.startX, SharedValuesXY.endX),
//                    Math.min(SharedValuesXY.startY, SharedValuesXY.endY),
//                    Math.max(SharedValuesXY.startX, SharedValuesXY.endX),
//                    Math.max(SharedValuesXY.startY, SharedValuesXY.endY)
//                    , paint);
//
//        }

        invalidate();

    }
}
