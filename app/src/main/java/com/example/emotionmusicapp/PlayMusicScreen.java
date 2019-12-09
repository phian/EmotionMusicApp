package com.example.emotionmusicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.AbsoluteLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cleveroad.audiovisualization.AudioVisualization;
import com.cleveroad.audiovisualization.DbmHandler;
import com.cleveroad.audiovisualization.SpeechRecognizerDbmHandler;
import com.cleveroad.audiovisualization.VisualizerDbmHandler;
import com.gauravk.audiovisualizer.visualizer.BlastVisualizer;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.rahman.dialog.Activity.SmartDialog;
import com.rahman.dialog.ListenerCallBack.SmartDialogClickListener;
import com.rahman.dialog.Utilities.SmartDialogBuilder;
import com.taishi.library.Indicator;
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import io.ghyeok.stickyswitch.widget.StickySwitch;

public class PlayMusicScreen extends AppCompatActivity {
    ImageButton playButton, skipNextButton, skipPreviousButton, repeatButton, shuffleButton;
    TextView songLengthTV, songNameTV, singerNameTV;
    SeekBar songLengthSB;
    MediaPlayer musicMedia = null;
    CircularImageView diskImageCIV;
    LinearLayout blastVisualizerLay, musicVisualizationViewLay;
    StickySwitch screenStyleSwitch, themeSwitch;
    BlastVisualizer blastVisualizer;
    AudioVisualization musicWaveVisualization;
    ObjectAnimator diskImgAni;
    RecyclerView songRV;
    RecyclerView.Adapter songListAdapter;
    RecyclerView.LayoutManager songLisLayoutManager;
    BottomSheetBehavior songListBottomSheetBe;
    View songListBottomSheet;
    AbsoluteLayout mainScreenScrollLayout;
    LinearLayout songListBottomSheetLay;

    boolean isPlay = false;
    int musicIndex = 0;
    float x1, x2, y1, y2;

    Field[] songNameList;
    int[] songIdList = new int[R.raw.class.getFields().length - 1];
    int indexCount = -1; // count index to insert song id
    String[] songNameArr = new String[R.raw.class.getFields().length - 1];
    String[] singerNameArr = new String[R.raw.class.getFields().length - 1];
    ImageButton[] removeSongButtons = new ImageButton[R.raw.class.getFields().length - 1];
    Indicator[] indicators = new Indicator[R.raw.class.getFields().length - 1];

    ArrayList<CustomRecyclerViewItem> customItems = new ArrayList<>();

    //--------------------------------------------------------------------------------------------//
    Handler musicHandler = new Handler();

    // Thread to update time for SeekBar
    Runnable musicRunnable = new Runnable() {
        @SuppressLint("DefaultLocale")
        @Override
        public void run() {
            if (musicMedia != null) {
                int currentTime = musicMedia.getCurrentPosition();
                long songDuration = musicMedia.getDuration();

                long leftTime = songDuration - currentTime;
                long songLeftMin = TimeUnit.MILLISECONDS.toMinutes(leftTime);
                long songLeftSec = TimeUnit.MILLISECONDS.toSeconds(leftTime) - TimeUnit.MINUTES.toSeconds(songLeftMin);

                songLengthSB.setProgress(currentTime);

                songLengthTV.setText(String.format("-" + "%02d:%02d", songLeftMin, songLeftSec));

                update(); // method use to update time for SeekBar and song length TV
            }
        }
    };

    //method to check if user is playing music or not
    public void startSong(boolean isPlaying) {
        if (isPlaying) {
            if (musicMedia == null) {
                playSong();
            } else {
                resumeSong();
            }
        } else {
            pauseSong();
        }
    }

    // method to start playing music
    public void playSong() {
        musicMedia = new MediaPlayer();
        try {
            musicMedia = MediaPlayer.create(PlayMusicScreen.this, songIdList[0]);
            musicMedia.prepare();

            songLengthSB.setMax(musicMedia.getDuration());

            musicMedia.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        update();
    }

    // method to pause playing music
    @SuppressLint("DefaultLocale")
    public void pauseSong() {
        playButton.setImageResource(R.drawable.play_music_button);

        musicMedia.pause();

        int currentTime = musicMedia.getCurrentPosition();
        long songDuration = musicMedia.getDuration();

        long leftTime = songDuration - currentTime;
        long songLeftMin = TimeUnit.MILLISECONDS.toMinutes(leftTime);
        long songLeftSec = TimeUnit.MILLISECONDS.toSeconds(leftTime) - TimeUnit.MINUTES.toSeconds(songLeftMin);
        songLengthTV.setText(String.format("-" + "%02d:%02d", songLeftMin, songLeftSec));
    }

    // method to resume playing music
    @SuppressLint("DefaultLocale")
    public void resumeSong() {
        playButton.setImageResource(R.drawable.pause_music_button);
        musicHandler.removeCallbacks(musicRunnable);
        songLengthSB.setMax(musicMedia.getDuration());

        musicMedia.start();
        update();

        int currentTime = musicMedia.getCurrentPosition();
        long songDuration = musicMedia.getDuration();

        long leftTime = songDuration - currentTime;
        long songLeftMin = TimeUnit.MILLISECONDS.toMinutes(leftTime);
        long songLeftSec = TimeUnit.MILLISECONDS.toSeconds(leftTime) - TimeUnit.MINUTES.toSeconds(songLeftMin);
        songLengthTV.setText(String.format("-" + "%02d:%02d", songLeftMin, songLeftSec));
    }

    // method to stop playing music
    public void stopSong() {
        musicMedia.release();
        musicMedia.reset();
        musicMedia.stop();
        musicMedia = null;

        songLengthSB.setProgress(songLengthSB.getProgress());
        songLengthSB.setProgress(songLengthSB.getMax());
    }

    // method tp update time for SeekBar
    public void updateSeekBarTime(int progressTime) {
        musicMedia = new MediaPlayer();

        try {
            musicMedia = MediaPlayer.create(PlayMusicScreen.this, R.raw.spectre_alanwalker);
            musicMedia.prepare();
            musicMedia.seekTo(progressTime);

            songLengthSB.setMax(musicMedia.getDuration());

            musicMedia.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopSong();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // method to update delay
    public void update() {
        musicHandler.postDelayed(musicRunnable, 1000);
    }

    //--------------------------------------------------------------------------------------------//

    @SuppressLint({"DefaultLocale", "WrongConstant"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_play_music_screen);

        castControl();
        onPlayMusicButtonClickListener();
        onMusicSeekBarLengthChangeListener();
        onSkipNextButtonClickListener();
        onSkipPreviousButtonClickListener();
        readRawResourcesFileNameAndId();
        cutSongNameAndSingerNameFromRawResource();
        updateSongNameAndSingerNameTV(musicIndex);
        onScreenStyleSwitchChangeListener();
        onCreateRemoveButtonAndIndicatorsForSongListScreen();
        onCreateSongRecyclerView();
        onSongListItemDragListener();
        onCreateSongListScreenBottomSheetBehavior();

        // call music file
        musicMedia = new MediaPlayer();
        musicMedia = MediaPlayer.create(PlayMusicScreen.this, songIdList[0]);

        // set text for TextView song length
        int currentTime = musicMedia.getCurrentPosition();
        long songDuration = musicMedia.getDuration();

        long leftTime = songDuration - currentTime;
        long songLeftMin = TimeUnit.MILLISECONDS.toMinutes(leftTime);
        long songLeftSec = TimeUnit.MILLISECONDS.toSeconds(leftTime) - TimeUnit.MINUTES.toSeconds(songLeftMin);
        songLengthTV.setText(String.format("-" + "%02d:%02d", songLeftMin, songLeftSec));
        //----------------------------------------------------------------------------------------//

        // animation for disk
        diskImgAni = ObjectAnimator.ofFloat(diskImageCIV, View.ROTATION, 0f, 360f).setDuration(2500);
        diskImgAni.setRepeatCount(musicMedia.getDuration());
        diskImgAni.setInterpolator(new LinearInterpolator());

        // set speech recognizer handler
        SpeechRecognizerDbmHandler speechRecHandler = DbmHandler.Factory.newSpeechRecognizerHandler(PlayMusicScreen.this);
        speechRecHandler.innerRecognitionListener();
        musicWaveVisualization.linkTo(speechRecHandler);
        // set audio visualization handler. This will REPLACE previously set speech recognizer handler
        VisualizerDbmHandler visualizerHandler = DbmHandler.Factory.newVisualizerHandler(PlayMusicScreen.this, 0);
        musicWaveVisualization.linkTo(visualizerHandler);

        // Add click ani for button
        PushDownAnim.setPushDownAnimTo(playButton, skipPreviousButton, skipNextButton, repeatButton, shuffleButton)
                .setScale(PushDownAnim.MODE_SCALE | PushDownAnim.MODE_STATIC_DP, PushDownAnim.DEFAULT_PUSH_SCALE)
                .setDurationPush(PushDownAnim.DEFAULT_PUSH_DURATION)
                .setDurationRelease(PushDownAnim.DEFAULT_RELEASE_DURATION)
                .setInterpolatorPush(PushDownAnim.DEFAULT_INTERPOLATOR)
                .setInterpolatorRelease(PushDownAnim.DEFAULT_INTERPOLATOR);

        //get the AudioSessionId your MediaPlayer and pass it to the visualizer
//        int audioSessionId = musicMedia.getAudioSessionId();
//        if (audioSessionId != -1)
//            blastVisualizer.setAudioSessionId(audioSessionId);
    }

    // method to read all raw resources name and id
    public void readRawResourcesFileNameAndId() {
        songNameList = R.raw.class.getFields();

        String unusedFile = "av_workaround_1min";

        for (int i = 0; i < songNameList.length; i++) {
            if (unusedFile.equals(songNameList[i].getName()) == false) {
                indexCount++;
                songIdList[indexCount] = this.getResources().getIdentifier(songNameList[i].getName(), "raw", this.getPackageName());
            }
        }

        indexCount = -1;
    }

    // method to cut all song and singer name and add to arr to update textview
    public void cutSongNameAndSingerNameFromRawResource() {
        songNameList = R.raw.class.getFields();

        String unusedFile = "av_workaround_1min";

        for (int i = 0; i < songNameList.length; i++) {
            if (unusedFile.equals(songNameList[i].getName()) == false) {
                indexCount++;
                String[] temp = songNameList[i].getName().split("_");

                songNameArr[indexCount] = temp[0];
                singerNameArr[indexCount] = temp[1];
            }
        }
    }

    // method to update
    public void updateSongNameAndSingerNameTV(int songIndex) {
        songNameTV.setText(songNameArr[songIndex]);
        singerNameTV.setText(singerNameArr[songIndex]);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onBackPressed() {
        new SmartDialogBuilder(this)
                .setTitle("Alert")
                .setSubTitle("Are you sure you want to stop the music?")
                .setSubTitleFont(Typeface.SANS_SERIF)
                .setNegativeButtonHide(false)
                .setPositiveButton("Yes, stop it", new SmartDialogClickListener() {
                    @Override
                    public void onClick(SmartDialog smartDialog) {
                        musicWaveVisualization.release();
                        blastVisualizer.release();

                        // add animation when user back to previous screen
                        Intent startMainActivity = new Intent(PlayMusicScreen.this, ChooseEmotionActivity.class);
                        startActivity(startMainActivity);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                        if (musicMedia.isPlaying()) {
                            musicMedia.stop();
                        }
                        musicMedia = null;
                        musicWaveVisualization.release();
                        blastVisualizer.release();

                        smartDialog.dismiss();

                        finish();
                    }
                })
                .setNegativeButton("No, continue the music", new SmartDialogClickListener() {
                    @Override
                    public void onClick(SmartDialog smartDialog) {
                        smartDialog.dismiss();
                        onResume();

                        return;
                    }
                }).build().show();
    }

    // method use to cast all control need to interact in activity
    public void castControl() {
        playButton = (ImageButton) findViewById(R.id.playMusicButton);
        skipNextButton = (ImageButton) findViewById(R.id.skipNextButton);
        skipPreviousButton = (ImageButton) findViewById(R.id.skipPreviousButton);
        repeatButton = (ImageButton) findViewById(R.id.replayListButton);
        shuffleButton = (ImageButton) findViewById(R.id.mixListButton);

        songLengthTV = (TextView) findViewById(R.id.songLengthTV);
        songNameTV = (TextView) findViewById(R.id.songNameTV);
        singerNameTV = (TextView) findViewById(R.id.singerNameTV);

        blastVisualizerLay = (LinearLayout) findViewById(R.id.blastVisualizerLay);
        musicVisualizationViewLay = (LinearLayout) findViewById(R.id.musicVisualizationViewLay);

        songLengthSB = (SeekBar) findViewById(R.id.songLengthSeekBar);
        diskImageCIV = (CircularImageView) findViewById(R.id.diskImageCIV);
        musicWaveVisualization = (AudioVisualization) findViewById(R.id.musicWaveVisualization);

        blastVisualizer = (BlastVisualizer) findViewById(R.id.blastVisualizer);

        screenStyleSwitch = (StickySwitch) findViewById(R.id.screenStyleSwitch);
        themeSwitch = (StickySwitch) findViewById(R.id.themeSwitch);

        songRV = (RecyclerView) findViewById(R.id.songRV);
        songListBottomSheet = findViewById(R.id.songListScreenBottomSheet);

        mainScreenScrollLayout = (AbsoluteLayout) findViewById(R.id.mainScreenScrollLayout);
        songListBottomSheetLay = (LinearLayout) findViewById(R.id.songListBottomSheetLay);
    }

    // event method for play music button
    public void onPlayMusicButtonClickListener() {
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlay == false) {
                    isPlay = true;
                } else {
                    isPlay = false;
                }

                if (isPlay) {
                    if (screenStyleSwitch.getDirection() == StickySwitch.Direction.LEFT) {
                        musicWaveVisualization.onResume();
                        blastVisualizer.setEnabled(false);
                        blastVisualizer.release();
                    } else {
                        musicWaveVisualization.onPause();

                        //get the AudioSessionId your MediaPlayer and pass it to the visualizer
                        int audioSessionId = musicMedia.getAudioSessionId();
                        if (audioSessionId != -1)
                            blastVisualizer.setAudioSessionId(audioSessionId);
                        blastVisualizer.setEnabled(true);
                    }

                    if (diskImgAni.isRunning()) {
                        diskImgAni.resume();
                    } else {
                        diskImgAni.start();
                    }
                } else {
                    diskImgAni.pause();
                    musicWaveVisualization.onPause();
                }

                startSong(isPlay);
            }
        });
    }

    // event for seek bar change
    public void onMusicSeekBarLengthChangeListener() {
        songLengthSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (musicMedia != null && b) {
                    musicMedia.seekTo(i);

                    int currentTime = musicMedia.getCurrentPosition();
                    long songDuration = musicMedia.getDuration();

                    long leftTime = songDuration - currentTime;
                    long songLeftMin = TimeUnit.MILLISECONDS.toMinutes(leftTime);
                    long songLeftSec = TimeUnit.MILLISECONDS.toSeconds(leftTime) - TimeUnit.MINUTES.toSeconds(songLeftMin);

                    songLengthTV.setText(String.format("-" + "%02d:%02d", songLeftMin, songLeftSec));

                    // check if media finished remove all animation
                    musicMedia.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            if (diskImgAni.isRunning()) {
                                diskImgAni.end();
                            }
                            if (musicMedia.isPlaying()) {
                                musicWaveVisualization.release();
                            }
                            playButton.setImageResource(R.drawable.play_music_button);
                            isPlay = false;
                        }
                    });
                } else if (musicMedia == null && b) {
                    updateSeekBarTime(i);
                    update();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (musicMedia != null) {
                    musicHandler.removeCallbacks(musicRunnable);
                }
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (musicMedia != null) {
                    if (seekBar.getProgress() == seekBar.getMax()) {
                        if (diskImgAni.isRunning()) {
                            diskImgAni.end();
                        }
                        if (musicMedia.isPlaying()) {
                            musicWaveVisualization.release();
                        }
                        playButton.setImageResource(R.drawable.play_music_button);
                        isPlay = false;
                    } else {
                        musicHandler.removeCallbacks(musicRunnable); // remove thread playing song
                        musicMedia.seekTo(seekBar.getProgress());

                        int currentTime = musicMedia.getCurrentPosition();
                        long songDuration = musicMedia.getDuration();

                        long leftTime = songDuration - currentTime;
                        long songLeftMin = TimeUnit.MILLISECONDS.toMinutes(leftTime);
                        long songLeftSec = TimeUnit.MILLISECONDS.toSeconds(leftTime) - TimeUnit.MINUTES.toSeconds(songLeftMin);

                        songLengthTV.setText(String.format("-" + "%02d:%02d", songLeftMin, songLeftSec));

                        update();
                    }
                }
            }
        });
    }

    // event for skip next button
    public void onSkipNextButtonClickListener() {
        skipNextButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View view) {
                if (musicIndex < R.raw.class.getFields().length - 2) {
                    musicIndex++;
                } else {
                    musicIndex = 0;
                }

                // check if music media is null or not to create and call music file
                if (musicMedia == null) {
                    musicMedia = new MediaPlayer();
                } else {
                    musicMedia.release();
                    musicMedia = new MediaPlayer();
                }

                musicMedia = MediaPlayer.create(PlayMusicScreen.this, songIdList[musicIndex]);

                updateSongNameAndSingerNameTV(musicIndex);

                if (screenStyleSwitch.getDirection() == StickySwitch.Direction.RIGHT) {
                    //get the AudioSessionId your MediaPlayer and pass it to the visualizer
                    int audioSessionId = musicMedia.getAudioSessionId();
                    if (audioSessionId != -1)
                        blastVisualizer.setAudioSessionId(audioSessionId);
                }

                if (isPlay == true) {
                    musicMedia.start();

                    if (diskImgAni.isRunning() == false) {
                        diskImgAni.start();
                    }
                    if (musicMedia.isPlaying() == false) {
                        musicWaveVisualization.onResume();
                    }
                    playButton.setImageResource(R.drawable.pause_music_button);
                }

                // update time text
                int currentTime = musicMedia.getCurrentPosition();
                long songDuration = musicMedia.getDuration();

                long leftTime = songDuration - currentTime;
                long songLeftMin = TimeUnit.MILLISECONDS.toMinutes(leftTime);
                long songLeftSec = TimeUnit.MILLISECONDS.toSeconds(leftTime) - TimeUnit.MINUTES.toSeconds(songLeftMin);

                songLengthTV.setText(String.format("-" + "%02d:%02d", songLeftMin, songLeftSec));
            }
        });
    }

    // event for skip previous button
    public void onSkipPreviousButtonClickListener() {
        skipPreviousButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View view) {
                if (musicIndex > 0) {
                    musicIndex--;
                } else {
                    musicIndex = R.raw.class.getFields().length - 2;
                }

                // check if music media is null or not to create and call music file
                if (musicMedia == null) {
                    musicMedia = new MediaPlayer();
                } else {
                    musicMedia.release();
                    musicMedia = new MediaPlayer();
                }

                musicMedia = MediaPlayer.create(PlayMusicScreen.this, songIdList[musicIndex]);

                updateSongNameAndSingerNameTV(musicIndex);

                if (screenStyleSwitch.getDirection() == StickySwitch.Direction.RIGHT) {
                    //get the AudioSessionId your MediaPlayer and pass it to the visualizer
                    int audioSessionId = musicMedia.getAudioSessionId();
                    if (audioSessionId != -1)
                        blastVisualizer.setAudioSessionId(audioSessionId);
                }

                if (isPlay == true) {
                    musicMedia.start();

                    if (diskImgAni.isRunning() == false) {
                        diskImgAni.start();
                    }
                    if (musicMedia.isPlaying() == false) {
                        musicWaveVisualization.onResume();
                    }
                    playButton.setImageResource(R.drawable.pause_music_button);
                }

                // update time text
                int currentTime = musicMedia.getCurrentPosition();
                long songDuration = musicMedia.getDuration();

                long leftTime = songDuration - currentTime;
                long songLeftMin = TimeUnit.MILLISECONDS.toMinutes(leftTime);
                long songLeftSec = TimeUnit.MILLISECONDS.toSeconds(leftTime) - TimeUnit.MINUTES.toSeconds(songLeftMin);

                songLengthTV.setText(String.format("-" + "%02d:%02d", songLeftMin, songLeftSec));

            }
        });
    }

    public void onScreenStyleSwitchChangeListener() {
        screenStyleSwitch.setOnSelectedChangeListener(new StickySwitch.OnSelectedChangeListener() {
            @Override
            public void onSelectedChange(@NotNull StickySwitch.Direction direction, @NotNull String s) {
                if (direction == StickySwitch.Direction.LEFT) {
                    if (isPlay && musicMedia != null) {
                        musicWaveVisualization.onResume();
                        blastVisualizer.setEnabled(false);
                    } else if (isPlay == false && musicMedia != null) {
                        musicWaveVisualization.onPause();
                        blastVisualizer.setEnabled(false);
                    }
                    musicVisualizationViewLay.setAlpha(1);
                    themeSwitch.setVisibility(View.INVISIBLE);
                    blastVisualizerLay.setAlpha(0);
                } else {
                    if (isPlay && musicMedia != null) {
                        musicWaveVisualization.onPause();

                        //get the AudioSessionId from MediaPlayer and pass it to the visualizer
                        int audioSessionId = musicMedia.getAudioSessionId();
                        if (audioSessionId != -1)
                            blastVisualizer.setAudioSessionId(audioSessionId);
                    } else if (isPlay == false && musicMedia != null) {
                        musicWaveVisualization.onPause();

                        //get the AudioSessionId from MediaPlayer and pass it to the visualizer
                        int audioSessionId = musicMedia.getAudioSessionId();
                        if (audioSessionId != -1)
                            blastVisualizer.setAudioSessionId(audioSessionId);
                    }
                    musicVisualizationViewLay.setAlpha(0);
                    themeSwitch.setVisibility(View.VISIBLE);
                    blastVisualizerLay.setAlpha(1);
                }
            }
        });
    }

    public void onCreateRemoveButtonAndIndicatorsForSongListScreen() {
        for (int i = 0; i < removeSongButtons.length; i++) {
            removeSongButtons[i] = new ImageButton(this);
            indicators[i] = new Indicator(this);

            removeSongButtons[i].setImageResource(R.drawable.remove_song_ic);

            indicators[i].setStepNum(10);
            indicators[i].setBarNum(4);
            indicators[i].setDuration(3000);
            indicators[i].setVisibility(View.INVISIBLE);
        }
    }

    // method to create song list for recycler view
    public void onCreateSongRecyclerView() {
        for (int i = 0; i < songNameArr.length; i++) {
            customItems.add(new CustomRecyclerViewItem(songNameArr[i], singerNameArr[i], indicators[i], removeSongButtons[i]));
        }

        songRV.setHasFixedSize(true);

        songLisLayoutManager = new LinearLayoutManager(this);
        songListAdapter = new CustomRecyclerViewAdapter(customItems);

        songRV.setLayoutManager(songLisLayoutManager);
        songRV.setAdapter(songListAdapter);
        songRV.addOnItemTouchListener(new RecyclerViewItemClickListener(this, new RecyclerViewItemClickListener.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {

            }
        }));
    }

    // event for song list item change position listener
    public void onSongListItemDragListener() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder draggedViewHolder, @NonNull RecyclerView.ViewHolder target) {
                int draggedPosition = draggedViewHolder.getAdapterPosition();
                int targetPosition = target.getAdapterPosition();

                Collections.swap(customItems, draggedPosition, targetPosition);
                songListAdapter.notifyItemMoved(draggedPosition, targetPosition);

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });

        itemTouchHelper.attachToRecyclerView(songRV);
    }

    // method to create behavior for song list screen bottom sheet
    public void onCreateSongListScreenBottomSheetBehavior() {
        songListBottomSheetBe = BottomSheetBehavior.from(songListBottomSheet);

        songListBottomSheetBe.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_SETTLING:
                        if (songListBottomSheetBe.getPeekHeight() < 65) {
                            songListBottomSheetBe.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        songListBottomSheetBe.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        break;

                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();

                if (x1 < x2) {
                    mainScreenScrollLayout.animate().translationX(480).setDuration(300).setStartDelay(0);
                    songListBottomSheetLay.animate().translationX(0).setDuration(300).setStartDelay(0);
                } else if (x1 > x2) {
                    mainScreenScrollLayout.animate().translationX(0).setDuration(300).setStartDelay(0);
                    songListBottomSheetLay.animate().translationX(-323).setDuration(300).setStartDelay(0);
                }
                break;
        }

        return true;
    }
}
