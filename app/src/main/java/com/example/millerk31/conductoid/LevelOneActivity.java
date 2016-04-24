package com.example.millerk31.conductoid;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class LevelOneActivity extends AppCompatActivity {

    //used for storing objects in text fields
    Gson gson;

    StartDraggingListener myStartDraggingListener;
    EndDraggingListener myEndDraggingListener;
    GbOnClickListener myGbOnClickListener;

    ImageButton btnReset, btnPlayOriginalSong, btnPlayGridSong;
    GameButton btn1, btn2, btn3, btn4, btn5, btn6;
    ImageView ivSatisfaction;

    Animation alphaAnim;

    MyPanel panel;
    GameGrid gg;

    //media player and play list
    MediaPlayer mp = new MediaPlayer();
    ArrayList<PlayListRecord> playList;
    EditText etHidden;

    IntentFilter myPanelMessageFilter = new IntentFilter();
    BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("KSM", "Broadcast intent received");
            if (intent.getAction().equals("myPanelMessage")) {
                switch (intent.getStringExtra("status")) {
                    case "success":
                        ivSatisfaction.setImageResource(R.drawable.ic_sentiment_very_satisfied_black_24dp);
                        break;

                    case "failed":
                        ivSatisfaction.setImageResource(R.drawable.ic_sentiment_very_dissatisfied_black_24dp);

                        break;

                    default:
                        break;
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_level_one);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.conductoid);

        SharedValues.levelGameStatus = SharedValues.GameStatus.INITIAL;
        //set length of this song in measures
        SharedValues.songLength = 6;

        gson = new Gson();

        alphaAnim = AnimationUtils.loadAnimation(LevelOneActivity.this, R.anim.alpha);
        gg = GameGrid.getInstance();
        panel = (MyPanel) findViewById(R.id.myPanel);

        myPanelMessageFilter.addAction("myPanelMessage");
        registerReceiver(myReceiver, myPanelMessageFilter);

        playList = new ArrayList<>();
        etHidden = (EditText) findViewById(R.id.etHiddenPlaylist);
        etHidden.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //hack to get recursive calls MediaPlayer tp play playlist
            @Override
            public void afterTextChanged(Editable s) {
                //no error checking - should only contain next sound resource ID
                PlayListRecord plr = gson.fromJson(s.toString(), PlayListRecord.class);
                mp = MediaPlayer.create(LevelOneActivity.this, plr.soundResourceId);
                SharedValues.hlCol = plr.column;
                SharedValues.hlRow = plr.row;
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                        if (!playList.isEmpty()) {
                            etHidden.setText(gson.toJson(playList.get(0)));
                            playList.remove(0);
                        } else {
                            SharedValues.hlCol = -1;
                            SharedValues.hlRow = -1;
                            if (SharedValues.levelGameStatus != SharedValues.GameStatus.SUCCESS) {
                                btnReset.startAnimation(alphaAnim);
                                SharedValues.levelGameStatus = SharedValues.GameStatus.FAILED;
                            }
                        }
                    }
                });

            }
        });

        myStartDraggingListener = new StartDraggingListener();
        myEndDraggingListener = new EndDraggingListener();
        myGbOnClickListener = new GbOnClickListener();

        btnReset = (ImageButton) findViewById(R.id.btnReset);
        btnPlayOriginalSong = (ImageButton) findViewById(R.id.btnPlaySong);
        btnPlayOriginalSong.startAnimation(alphaAnim);
        btnPlayGridSong = (ImageButton) findViewById(R.id.btnPlayGrid);

        ivSatisfaction = (ImageView) findViewById(R.id.iv_Satisfaction);

        btn1 = (GameButton) findViewById(R.id.btn1);
        btn2 = (GameButton) findViewById(R.id.btn2);
        btn3 = (GameButton) findViewById(R.id.btn3);
        btn4 = (GameButton) findViewById(R.id.btn4);
        btn5 = (GameButton) findViewById(R.id.btn5);
        btn6 = (GameButton) findViewById(R.id.btn6);

        //this data determines what this level is
        //attach resources and config info to GameButton
        //GameButton.setAll(soundResourceId, imageResourceId, validGridLocations, isButtonReusable)
        btn1.setAll(R.raw.twinkle_1, R.mipmap.ic_twinkle1, "0,0:4,0", false);
        btn2.setAll(R.raw.twinkle_2, R.mipmap.ic_twinkle2, "1,0:5,0", false);
        btn3.setAll(R.raw.twinkle_3, R.mipmap.ic_twinkle3, "2,0:3,0", false);
        btn4.setAll(R.raw.twinkle_3, R.mipmap.ic_twinkle3, "2,0:3,0", false);
        btn5.setAll(R.raw.twinkle_1, R.mipmap.ic_twinkle1, "0,0:4,0", false);
        btn6.setAll(R.raw.twinkle_2, R.mipmap.ic_twinkle2, "1,0:5,0", false);

        //btn1.setLayoutParams(new RelativeLayout.LayoutParams(panel.cellHeight,panel.cellWidth));
        btn1.setHeight(panel.cellHeight);
        btn1.setWidth(panel.cellWidth);
        btn2.setHeight(panel.cellHeight);
        btn2.setWidth(panel.cellWidth);
        btn3.setHeight(panel.cellHeight);
        btn3.setWidth(panel.cellWidth);
        btn4.setHeight(panel.cellHeight);
        btn4.setWidth(panel.cellWidth);
        btn5.setHeight(panel.cellHeight);
        btn5.setWidth(panel.cellWidth);
        btn6.setHeight(panel.cellHeight);
        btn6.setWidth(panel.cellWidth);

        //shuffle buttons in layout
        //addd buttons to list to br shuffled
        final ArrayList<GameButton> gbl = new ArrayList<>();
        gbl.add(btn1);
        gbl.add(btn2);
        gbl.add(btn3);
        gbl.add(btn4);
        gbl.add(btn5);
        gbl.add(btn6);

        //shuffle list
        long seed = System.nanoTime();
        Collections.shuffle(gbl, new Random(seed));

        // put buttons in new place in layout
        android.widget.RelativeLayout.LayoutParams params;
        params = (RelativeLayout.LayoutParams) gbl.get(0).getLayoutParams();
        params.removeRule(RelativeLayout.END_OF);
        params.addRule(RelativeLayout.ALIGN_PARENT_START);
        params.setMarginStart(0);
        gbl.get(0).setLayoutParams(params);

        params = (RelativeLayout.LayoutParams) gbl.get(1).getLayoutParams();
        params.removeRule(RelativeLayout.END_OF);
        params.removeRule(RelativeLayout.ALIGN_PARENT_START);
        params.addRule(RelativeLayout.END_OF, gbl.get(0).getId());
        params.setMarginStart(15);
        gbl.get(1).setLayoutParams(params);

        params = (RelativeLayout.LayoutParams) gbl.get(2).getLayoutParams();
        params.removeRule(RelativeLayout.END_OF);
        params.removeRule(RelativeLayout.ALIGN_PARENT_START);
        params.addRule(RelativeLayout.END_OF, gbl.get(1).getId());
        params.setMarginStart(15);
        gbl.get(2).setLayoutParams(params);

        params = (RelativeLayout.LayoutParams) gbl.get(3).getLayoutParams();
        params.removeRule(RelativeLayout.END_OF);
        params.removeRule(RelativeLayout.ALIGN_PARENT_START);
        params.addRule(RelativeLayout.END_OF, gbl.get(2).getId());
        params.setMarginStart(15);
        gbl.get(3).setLayoutParams(params);

        params = (RelativeLayout.LayoutParams) gbl.get(4).getLayoutParams();
        params.removeRule(RelativeLayout.END_OF);
        params.removeRule(RelativeLayout.ALIGN_PARENT_START);
        params.addRule(RelativeLayout.END_OF, gbl.get(3).getId());
        params.setMarginStart(15);
        gbl.get(4).setLayoutParams(params);

        params = (RelativeLayout.LayoutParams) gbl.get(5).getLayoutParams();
        params.removeRule(RelativeLayout.END_OF);
        params.removeRule(RelativeLayout.ALIGN_PARENT_START);
        params.addRule(RelativeLayout.END_OF, gbl.get(4).getId());
        params.setMarginStart(15);
        gbl.get(5).setLayoutParams(params);

        btn1.setOnLongClickListener(myStartDraggingListener);
        btn2.setOnLongClickListener(myStartDraggingListener);
        btn3.setOnLongClickListener(myStartDraggingListener);
        btn4.setOnLongClickListener(myStartDraggingListener);
        btn5.setOnLongClickListener(myStartDraggingListener);
        btn6.setOnLongClickListener(myStartDraggingListener);

        btn1.setOnClickListener(myGbOnClickListener);
        btn2.setOnClickListener(myGbOnClickListener);
        btn3.setOnClickListener(myGbOnClickListener);
        btn4.setOnClickListener(myGbOnClickListener);
        btn5.setOnClickListener(myGbOnClickListener);
        btn6.setOnClickListener(myGbOnClickListener);

        btnPlayOriginalSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.clearAnimation();
                if (mp.isPlaying())
                    mp.stop();
                mp = MediaPlayer.create(LevelOneActivity.this, R.raw.twinkle_twinkle_little_star_one_cycle);
                mp.start();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            v.clearAnimation();
                                            SharedValues.levelGameStatus = SharedValues.GameStatus.RESET;
                                            for (GameButton gb : gbl) {
                                                gb.setEnabled(true);
                                                gb.setBackgroundResource(gb.getImageResourceId());
                                            }
                                        }
                                    }
        );


        btnPlayGridSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("KSM", "Entering onClickVew");
                for (int r = 0; r < GameGrid.ROWS; ++r) {
                    for (int c = 0; c < GameGrid.COLS; ++c) {
                        Sprite sp = (GameGrid.myGrid[c][r]);
                        if ((sp != null) && (sp.measureSoundResource != 0)) {
                            playList.add(new PlayListRecord(c, r, sp.measureSoundResource));
                        }
                    }
                }

                //start playList
                etHidden.setText(gson.toJson(playList.get(0)));
                playList.remove(0);
                SharedValues.levelGameStatus = SharedValues.GameStatus.GRID_SONG_PLAYING;
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

    private class EndDraggingListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            if (event.getAction() == DragEvent.ACTION_DROP) {
                v.setBackground(((Button) event.getLocalState()).getBackground());
            }
            return true;
        }
    }

    private class StartDraggingListener implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {

            GameButton gb = (GameButton) v;
            WithDraggingShadow shadow = new WithDraggingShadow(gb);
            ClipData data = ClipData.newPlainText("", "");
            v.startDrag(data, shadow, v, 0);
            return false;
        }
    }

    private class WithDraggingShadow extends View.DragShadowBuilder {
        Bitmap shdBitmap;

        //public WithDraggingShadow(View v){
        public WithDraggingShadow(GameButton gb) {
            super(gb);

            shdBitmap = ((BitmapDrawable) gb.getBackground()).getBitmap();
        }

        @Override
        public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
            super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            //super.onDrawShadow(canvas);
            canvas.drawBitmap(shdBitmap, (canvas.getWidth() - shdBitmap.getWidth()) / 2, (canvas.getHeight() - shdBitmap.getHeight()) / 2, null);
        }
    }

    class GbOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            GameButton gb = (GameButton) v;
            if (mp.isPlaying())
                mp.stop();

            mp = MediaPlayer.create(LevelOneActivity.this, gb.getSoundResourceId());
            mp.start();
        }
    }

    class PlayListRecord {
        int soundResourceId;
        int column;
        int row;

        public PlayListRecord(int column, int row, int soundResourceId) {
            this.column = column;
            this.row = row;
            this.soundResourceId = soundResourceId;
        }
    }


}
