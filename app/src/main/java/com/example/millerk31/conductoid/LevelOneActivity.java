package com.example.millerk31.conductoid;

import android.content.ClipData;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class LevelOneActivity extends AppCompatActivity {

    StartDraggingListener myStartDraggingListener;
    EndDraggingListener myEndDraggingListener;

    GameButton btn1, btn2, btn3, btn4, btn5, btn6;
    MyPanel panel;

    GameGrid gg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.conductoid);

        //set length of this song in measures
        SharedValues.songLength=6;

        gg = GameGrid.getInstance();
        panel = (MyPanel) findViewById(R.id.myPanel);

        myStartDraggingListener = new StartDraggingListener();
        myEndDraggingListener = new EndDraggingListener();

        btn1 = (GameButton) findViewById(R.id.btn1);
        btn2 = (GameButton) findViewById(R.id.btn2);
        btn3 = (GameButton) findViewById(R.id.btn3);
        btn4 = (GameButton) findViewById(R.id.btn4);
        btn5 = (GameButton) findViewById(R.id.btn5);
        btn6 = (GameButton) findViewById(R.id.btn6);

        //this data determines what this level is
        btn1.setAll(R.raw.twinkle_1, R.mipmap.ic_twinkle1, "0,0:4,0", false);
        btn2.setAll(R.raw.twinkle_2, R.mipmap.ic_twinkle2, "1,0:5,0", false);
        btn3.setAll(R.raw.twinkle_1, R.mipmap.ic_twinkle1, "2,0:3,0", false);
        btn4.setAll(R.raw.twinkle_1, R.mipmap.ic_twinkle1, "0,0:0,4", false);
        btn5.setAll(R.raw.twinkle_1, R.mipmap.ic_twinkle1, "0,0:4,0", false);
        btn6.setAll(R.raw.twinkle_2, R.mipmap.ic_twinkle2, "1,0:5,0", false);

        //shuffle buttons in layout
        final ArrayList<GameButton> gbl = new ArrayList<>();
        gbl.add(btn1);
        gbl.add(btn2);
        gbl.add(btn3);
        gbl.add(btn4);
        gbl.add(btn5);
        gbl.add(btn6);

        long seed = System.nanoTime();
        Collections.shuffle(gbl, new Random(seed));

        android.widget.RelativeLayout.LayoutParams params;
        params = (RelativeLayout.LayoutParams) gbl.get(0).getLayoutParams();
        params.removeRule(RelativeLayout.END_OF);
        params.addRule(RelativeLayout.ALIGN_PARENT_START);
        gbl.get(0).setLayoutParams(params);

        params = (RelativeLayout.LayoutParams) gbl.get(1).getLayoutParams();
        params.removeRule(RelativeLayout.END_OF);
        params.removeRule(RelativeLayout.ALIGN_PARENT_START);
        params.addRule(RelativeLayout.END_OF, gbl.get(0).getId());
        gbl.get(1).setLayoutParams(params);

        params = (RelativeLayout.LayoutParams) gbl.get(2).getLayoutParams();
        params.removeRule(RelativeLayout.END_OF);
        params.removeRule(RelativeLayout.ALIGN_PARENT_START);
        params.addRule(RelativeLayout.END_OF, gbl.get(1).getId());
        gbl.get(2).setLayoutParams(params);

        params = (RelativeLayout.LayoutParams) gbl.get(3).getLayoutParams();
        params.removeRule(RelativeLayout.END_OF);
        params.removeRule(RelativeLayout.ALIGN_PARENT_START);
        params.addRule(RelativeLayout.END_OF, gbl.get(2).getId());
        gbl.get(3).setLayoutParams(params);

        params = (RelativeLayout.LayoutParams) gbl.get(4).getLayoutParams();
        params.removeRule(RelativeLayout.END_OF);
        params.removeRule(RelativeLayout.ALIGN_PARENT_START);
        params.addRule(RelativeLayout.END_OF, gbl.get(3).getId());
        gbl.get(4).setLayoutParams(params);

        params = (RelativeLayout.LayoutParams) gbl.get(5).getLayoutParams();
        params.removeRule(RelativeLayout.END_OF);
        params.removeRule(RelativeLayout.ALIGN_PARENT_START);
        params.addRule(RelativeLayout.END_OF, gbl.get(4).getId());
        gbl.get(5).setLayoutParams(params);


//        btn1.setLayoutParams(new LinearLayout.LayoutParams(panel.cellHeight,panel.cellWidth));
//        //btn1.setHeight(panel.cellHeight); btn1.setWidth(panel.cellWidth);
//        btn2.setHeight(panel.cellHeight); btn2.setWidth(panel.cellWidth);
//        btn3.setHeight(panel.cellHeight); btn3.setWidth(panel.cellWidth);
//        btn4.setHeight(panel.cellHeight); btn4.setWidth(panel.cellWidth);
//        btn5.setHeight(panel.cellHeight); btn5.setWidth(panel.cellWidth);
//        btn6.setHeight(panel.cellHeight); btn6.setWidth(panel.cellWidth);

        btn1.setOnLongClickListener(myStartDraggingListener);
        btn2.setOnLongClickListener(myStartDraggingListener);
        btn3.setOnLongClickListener(myStartDraggingListener);
        btn4.setOnLongClickListener(myStartDraggingListener);
        btn5.setOnLongClickListener(myStartDraggingListener);
        btn6.setOnLongClickListener(myStartDraggingListener);

        findViewById(R.id.btnPlaySong).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mp = MediaPlayer.create(LevelOneActivity.this, R.raw.twinkle_twinkle_little_star_one_cycle);
                mp.start();
            }
        });

        findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  SharedValues.levelGameStatus = SharedValues.GameStatus.RESET;
                  for (GameButton gb : gbl) {
                      gb.setEnabled(true);
                      gb.setBackgroundResource(gb.getImageResourceId());
                  }
              }
          }
        );


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mp = MediaPlayer.create(LevelOneActivity.this, R.raw.twinkle_1);
                mp.start();
            }
        });

        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MediaPlayer mp = MediaPlayer.create(LevelOneActivity.this, R.raw.twinkle_2);
                mp.start();
            }
        });

        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MediaPlayer mp = MediaPlayer.create(LevelOneActivity.this, R.raw.twinkle_3);
                mp.start();
            }
        });

        findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MediaPlayer mp = MediaPlayer.create(LevelOneActivity.this, R.raw.twinkle_3);
                mp.start();
            }
        });

        findViewById(R.id.btn5).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MediaPlayer mp = MediaPlayer.create(LevelOneActivity.this, R.raw.twinkle_1);
                mp.start();
            }
        });

        findViewById(R.id.btn6).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MediaPlayer mp = MediaPlayer.create(LevelOneActivity.this, R.raw.twinkle_2);
                mp.start();
            }
        });

        findViewById(R.id.btnPlayGrid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedValues.levelGameStatus = SharedValues.GameStatus.GRID_SONG_PLAYING;
                Log.d("KSM", "Entering onClickVew");
                for (int r = 0; r < GameGrid.ROWS; ++r) {
                    for (int c = 0; c < GameGrid.COLS; ++c) {
                        Sprite sp = (GameGrid.myGrid[c][r]);
                        if ((sp != null) && (sp.measureSoundResource != 0)) {
                            Log.d("KSM", "setting highlight");
                            SharedValues.hlCol = c; //cell highlighting during playback
                            SharedValues.hlRow = r; //cell highlighting during playback
                            findViewById(R.id.myPanel).invalidate();
                            Log.d("KSM", "invalidated");
                            MediaPlayer mp = MediaPlayer.create(LevelOneActivity.this, sp.measureSoundResource);
                            try {
                                int dur = mp.getDuration();
                                mp.start();
                                Thread.sleep(dur);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                //disable cell highlighting
                SharedValues.hlCol = -1;
                SharedValues.hlRow = -1;
                findViewById(R.id.myPanel).invalidate();

            }
        });

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
        switch (id) {
            case R.id.action_settings:
                return true;

            case R.id.action_exit:
                //todo causes window leak error
                this.finishAffinity();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class EndDraggingListener implements View.OnDragListener{

        @Override
        public boolean onDrag(View v, DragEvent event) {
            if(event.getAction() == DragEvent.ACTION_DROP){
                v.setBackground(((Button) event.getLocalState()).getBackground());
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
                    SharedValues.gridPosistions = "1,5";
                    SharedValues.measureSoundResource = R.raw.twinkle_1;
                    break;

                case R.id.btn2:
                case R.id.btn6:
                    myDrawable = getResources().getDrawable(R.mipmap.ic_twinkle1);
                    shdBitmap = ((BitmapDrawable)myDrawable).getBitmap();
                    SharedValues.gridPosistions = "2,6";
                    SharedValues.measureSoundResource = R.raw.twinkle_2;
                    break;

                case R.id.btn3:
                case R.id.btn4:
                    myDrawable = getResources().getDrawable(R.mipmap.ic_twinkle1);
                    shdBitmap = ((BitmapDrawable)myDrawable).getBitmap();
                    SharedValues.gridPosistions = "3,4";
                    SharedValues.measureSoundResource = R.raw.twinkle_3;
                    break;

                default:
                    Log.d("LevelOneActivity","StartDraggingListener unknown button");
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


}
