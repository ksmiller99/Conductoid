package com.example.millerk31.conductoid;

import android.content.ClipData;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    StartDraggingListener myStartDraggingListener;
    EndDraggingListener myEndDraggingListener;
    Button btn1, btn2;

    MyPanel panel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myStartDraggingListener = new StartDraggingListener();
        myEndDraggingListener = new EndDraggingListener();

        findViewById(R.id.btn1).setOnLongClickListener(myStartDraggingListener );
        findViewById(R.id.btn2).setOnLongClickListener(myStartDraggingListener );
        findViewById(R.id.btn3).setOnLongClickListener(myStartDraggingListener );
        findViewById(R.id.btn4).setOnLongClickListener(myStartDraggingListener );
        findViewById(R.id.btn5).setOnLongClickListener(myStartDraggingListener );
        findViewById(R.id.btn6).setOnLongClickListener(myStartDraggingListener );

//        findViewById(R.id.btn1).setOnDragListener(myEndDraggingListener);
//        findViewById(R.id.btn2).setOnDragListener(myEndDraggingListener);

//        btn1 = (Button)findViewById(R.id.btn1);
//        btn2 = (Button)findViewById(R.id.btn2);

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.twinkle_1);
                mp.start();
            }
        });

        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.twinkle_2);
                mp.start();
            }
        });

        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.twinkle_3);
                mp.start();
            }
        });

        findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.twinkle_3);
                mp.start();
            }
        });

        findViewById(R.id.btn5).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.twinkle_1);
                mp.start();
            }
        });

        findViewById(R.id.btn6).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.twinkle_2);
                mp.start();
            }
        });


    }


    public void setRect(View view) {
        SharedValuesXY.drawingMode = "RECT";
    }
    public void setOval(View view) {
        SharedValuesXY.drawingMode = "OVAL";
    }

    private class EndDraggingListener implements View.OnDragListener{

        @Override
        public boolean onDrag(View v, DragEvent event) {
            if(event.getAction() == DragEvent.ACTION_DROP){
                ((Button)v).setBackground(((Button) event.getLocalState()).getBackground());
            }
            return true;
        }
    }

    private class StartDraggingListener implements View.OnLongClickListener{

        @Override
        public boolean onLongClick(View v) {

            WithDraggingShadow shadow = new WithDraggingShadow(v);
            ClipData data = ClipData.newPlainText("","");
            v.startDrag(data,shadow,v,0);
            return false;
        }
    }

    private class WithDraggingShadow extends View.DragShadowBuilder{
        Bitmap shdBitmap;
        Drawable myDrawable;

        public WithDraggingShadow(View v){
            super(v);
            switch(v.getId()){
                case R.id.btn1:
                case R.id.btn5:
                    myDrawable = getResources().getDrawable(R.mipmap.ic_twinkle1);
                    shdBitmap = ((BitmapDrawable)myDrawable).getBitmap();
                    break;

                case R.id.btn2:
                case R.id.btn6:
                    myDrawable = getResources().getDrawable(R.mipmap.ic_twinkle1);
                    shdBitmap = ((BitmapDrawable)myDrawable).getBitmap();
                    break;

                case R.id.btn3:
                case R.id.btn4:
                    myDrawable = getResources().getDrawable(R.mipmap.ic_twinkle1);
                    shdBitmap = ((BitmapDrawable)myDrawable).getBitmap();
                    break;

                default:
                    Log.d("MainActivity","StartDraggingListener unknown button");
                    break;

            }
        }

        @Override
        public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
            super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            //super.onDrawShadow(canvas);
            canvas.drawBitmap(shdBitmap,(canvas.getWidth()-shdBitmap.getWidth())/2,(canvas.getHeight()-shdBitmap.getHeight())/2,null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
