package com.example.emotionmusicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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
import com.cunoraz.gifview.library.GifView;
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
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.ghyeok.stickyswitch.widget.StickySwitch;

public class PlayMusicScreen extends AppCompatActivity {
    ImageButton playButton, skipNextButton, skipPreviousButton, repeatButton, shuffleButton, songListSkipPreviousButton, songListPlayMusicButton, songListSkipNextButton, songListReplayListButton, songListMixListButton;
    TextView songLengthTV, songNameTV, singerNameTV, songListSongNameTV, songListSingerNameTV;
    SeekBar songLengthSB;
    MediaPlayer musicMedia = null;
    CircularImageView diskImageCIV, songListDiskImageCIV;
    LinearLayout blastVisualizerLay, musicVisualizationViewLay;
    StickySwitch screenStyleSwitch, themeSwitch;
    BlastVisualizer blastVisualizer;
    AudioVisualization musicWaveVisualization;
    ObjectAnimator diskImgAni, passScreenButtonAni, songListDiskImgAni;
    RecyclerView songRV;
    CustomRecyclerViewAdapter songListAdapter;
    RecyclerView.LayoutManager songLisLayoutManager;
    BottomSheetBehavior songListBottomSheetBe;
    View songListBottomSheet;
    AbsoluteLayout mainScreenScrollLayout;
    LinearLayout songListBottomSheetLay;
    GifView passScreenButton;
    Indicator songListSongIndicator;

    boolean isPlay = false, isOnSongListScreen = false, isShuffled = false;
    int musicIndex = 0, repeatedClickTime = 0;

    Random randMusicIndex = new Random(); // use for shuffle button click

    Field[] songNameList;
    ArrayList<Integer> songIdList = new ArrayList<>();
    int indexCount = -1; // count index to insert song id
    ArrayList<String> songNameArr = new ArrayList<>();
    ArrayList<String> singerNameArr = new ArrayList<>();
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
            musicMedia = MediaPlayer.create(PlayMusicScreen.this, songIdList.get(0));
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
        songListPlayMusicButton.setImageResource(R.drawable.play_music_button);

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
        songListPlayMusicButton.setImageResource(R.drawable.pause_music_button);

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

    // method to update time for SeekBar
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
        onSongListPlayMusicButtonClickListener();
        onMusicSeekBarLengthChangeListener();
        onSkipNextButtonClickListener();
        onSongListSkipNextButtonClickListener();
        onSkipPreviousButtonClickListener();
        onSongListSkipPreviousButtonClickListener();
        readRawResourcesFileNameAndId();
        cutSongNameAndSingerNameFromRawResource();
        updateSongNameAndSingerNameTV(musicIndex);
        onScreenStyleSwitchChangeListener();
        onCreateRemoveButtonAndIndicatorsForSongListScreen();
        onCreateSongRecyclerView();
        onSongListItemDragListener();
        onCreateSongListScreenBottomSheetBehavior();
        onPassButtonGifViewClickListener();
        onRepeatListButtonClickListener();
        onSongListRepeatButtonClickListener();
        onShuffleListButtonClickListener();
        onSongListShuffleButtonClickListener();
        onSongListItemClickListener();

        // call music file
        musicMedia = new MediaPlayer();
        musicMedia = MediaPlayer.create(PlayMusicScreen.this, songIdList.get(0));

        MediaPlayer media = new MediaPlayer();
        try {
            media.setDataSource("https://firebasestorage.googleapis.com/v0/b/emotionmusicapp.appspot.com/o/Can%20You%20See%20My%20Heart%20-%20Heize.flac?alt=media&token=3d25ef10-4e7e-4648-8969-b22779f9fadf");
            media.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });

            media.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

        songListDiskImgAni = ObjectAnimator.ofFloat(songListDiskImageCIV, View.ROTATION, 0f, 360f).setDuration(2500);
        songListDiskImgAni.setRepeatCount(musicMedia.getDuration());
        songListDiskImgAni.setInterpolator(new LinearInterpolator());

        // set speech recognizer handler
        SpeechRecognizerDbmHandler speechRecHandler = DbmHandler.Factory.newSpeechRecognizerHandler(PlayMusicScreen.this);
        speechRecHandler.innerRecognitionListener();
        musicWaveVisualization.linkTo(speechRecHandler);
        // set audio visualization handler. This will REPLACE previously set speech recognizer handler
        VisualizerDbmHandler visualizerHandler = DbmHandler.Factory.newVisualizerHandler(PlayMusicScreen.this, 0);
        musicWaveVisualization.linkTo(visualizerHandler);

        // Add click ani for button
        PushDownAnim.setPushDownAnimTo(playButton, skipPreviousButton, skipNextButton, repeatButton,
                shuffleButton, passScreenButton, songListSkipNextButton, songListSkipPreviousButton, songListPlayMusicButton,
                songListReplayListButton, songListMixListButton)
                .setScale(PushDownAnim.MODE_SCALE | PushDownAnim.MODE_STATIC_DP, PushDownAnim.DEFAULT_PUSH_SCALE)
                .setDurationPush(PushDownAnim.DEFAULT_PUSH_DURATION)
                .setDurationRelease(PushDownAnim.DEFAULT_RELEASE_DURATION)
                .setInterpolatorPush(PushDownAnim.DEFAULT_INTERPOLATOR)
                .setInterpolatorRelease(PushDownAnim.DEFAULT_INTERPOLATOR);

        // set up for pass screen button
        passScreenButton.setVisibility(View.VISIBLE);
        passScreenButton.pause();

        songListSongIndicator.setAlpha(0);

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
                songIdList.add(this.getResources().getIdentifier(songNameList[i].getName(), "raw", this.getPackageName()));
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

                songNameArr.add(temp[0]);
                singerNameArr.add(temp[1]);
            }
        }
    }

    // method to update
    public void updateSongNameAndSingerNameTV(int songIndex) {
        songNameTV.setText(songNameArr.get(songIndex));
        singerNameTV.setText(singerNameArr.get(songIndex));

        songListSongNameTV.setText(songNameArr.get(songIndex));
        songListSingerNameTV.setText(singerNameArr.get(songIndex));
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        musicIndex = 0;
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
        songListSkipPreviousButton = (ImageButton) findViewById(R.id.songListSkipPreviousButton);
        songListSkipNextButton = (ImageButton) findViewById(R.id.songListSkipNextButton);
        songListPlayMusicButton = (ImageButton) findViewById(R.id.songListPlayMusicButton);
        songListReplayListButton = (ImageButton) findViewById(R.id.songListReplayListButton);
        songListMixListButton = (ImageButton) findViewById(R.id.songListMixListButton);

        songLengthTV = (TextView) findViewById(R.id.songLengthTV);
        songNameTV = (TextView) findViewById(R.id.songNameTV);
        singerNameTV = (TextView) findViewById(R.id.singerNameTV);
        songListSongNameTV = (TextView) findViewById(R.id.songListSongNameTV);
        songListSingerNameTV = (TextView) findViewById(R.id.songListSingerNameTV);

        blastVisualizerLay = (LinearLayout) findViewById(R.id.blastVisualizerLay);
        musicVisualizationViewLay = (LinearLayout) findViewById(R.id.musicVisualizationViewLay);

        songLengthSB = (SeekBar) findViewById(R.id.songLengthSeekBar);
        diskImageCIV = (CircularImageView) findViewById(R.id.diskImageCIV);
        musicWaveVisualization = (AudioVisualization) findViewById(R.id.musicWaveVisualization);
        songListDiskImageCIV = (CircularImageView) findViewById(R.id.songListDiskImageCIV);

        blastVisualizer = (BlastVisualizer) findViewById(R.id.blastVisualizer);

        screenStyleSwitch = (StickySwitch) findViewById(R.id.screenStyleSwitch);
        themeSwitch = (StickySwitch) findViewById(R.id.themeSwitch);

        songRV = (RecyclerView) findViewById(R.id.songRV);
        songListBottomSheet = findViewById(R.id.songListScreenBottomSheet);

        mainScreenScrollLayout = (AbsoluteLayout) findViewById(R.id.mainScreenScrollLayout);
        songListBottomSheetLay = (LinearLayout) findViewById(R.id.songListBottomSheetLay);

        passScreenButton = (GifView) findViewById(R.id.passScreenButton);

        songListSongIndicator = (Indicator) findViewById(R.id.songListSongIndicator);
    }

    //------------------------------------ Play music button -------------------------------------//
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
                        songListDiskImgAni.resume();
                        songListSongIndicator.setAlpha(1);
                    } else {
                        diskImgAni.start();
                        songListDiskImgAni.start();
                        songListSongIndicator.setAlpha(1);
                    }
                } else {
                    diskImgAni.pause();
                    songListDiskImgAni.pause();
                    musicWaveVisualization.onPause();
                    songListSongIndicator.setAlpha(0);
                }

                startSong(isPlay);
            }
        });
    }

    // event for play music button in song list screen click
    public void onSongListPlayMusicButtonClickListener() {
        songListPlayMusicButton.setOnClickListener(new View.OnClickListener() {
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
                        songListDiskImgAni.resume();
                        songListSongIndicator.setAlpha(1);
                    } else {
                        diskImgAni.start();
                        songListDiskImgAni.start();
                        songListSongIndicator.setAlpha(1);
                    }
                } else {
                    diskImgAni.pause();
                    songListDiskImgAni.pause();
                    musicWaveVisualization.onPause();
                    songListSongIndicator.setAlpha(0);
                }

                startSong(isPlay);
            }
        });
    }
    //--------------------------------------------------------------------------------------------//

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
                            if (repeatedClickTime == 0 || repeatedClickTime == 3) { // if user don't repeat the list
                                if (musicIndex == R.raw.class.getFields().length - 2) { // check if current index is the last song of the list
                                    if (diskImgAni.isRunning()) {
                                        diskImgAni.end();
                                        songListDiskImgAni.end();
                                        songListSongIndicator.setAlpha(0);
                                    }
                                    if (musicMedia.isPlaying()) {
                                        musicWaveVisualization.release();
                                    }
                                    playButton.setImageResource(R.drawable.play_music_button);
                                    songListPlayMusicButton.setImageResource(R.drawable.play_music_button);
                                    isPlay = false;

                                    musicMedia = MediaPlayer.create(PlayMusicScreen.this, songIdList.get(musicIndex));
                                } else if (musicIndex < R.raw.class.getFields().length - 2) { // check if current index is not the last song of the list
                                    musicIndex++;

                                    // check if music media is null or not to create and call music file
//                                    if (musicMedia == null) {
//                                        musicMedia = new MediaPlayer();
//                                    } else {
//                                        musicMedia.release();
//                                        musicMedia = new MediaPlayer();
//                                    }

                                    musicMedia = MediaPlayer.create(PlayMusicScreen.this, songIdList.get(musicIndex));

                                    songLengthSB.setMax(musicMedia.getDuration()); // update song length on seek bar (use for repeat song case)
                                    songLengthSB.setProgress(0);

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
                                            songListDiskImgAni.start();
                                            songListSongIndicator.setAlpha(1);
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

                                    update();

                                    return;
                                }
                            } else if (repeatedClickTime == 1) { // if user repeat only the current song
                                musicMedia.start();

                                songLengthSB.setMax(musicMedia.getDuration()); // update song length on seek bar (use for repeat song case)
                                songLengthSB.setProgress(0);

                                update();
                            } else if (repeatedClickTime == 2) { // if user repeat the list
                                if (musicIndex == R.raw.class.getFields().length - 2) {
                                    musicIndex = 0;

                                    // check if music media is null or not to create and call music file
                                    if (musicMedia == null) {
                                        musicMedia = new MediaPlayer();
                                    } else {
                                        musicMedia.release();
                                        musicMedia = new MediaPlayer();
                                    }

                                    musicMedia = MediaPlayer.create(PlayMusicScreen.this, songIdList.get(musicIndex));

                                    songLengthSB.setMax(musicMedia.getDuration()); // update song length on seek bar (use for repeat song case)
                                    songLengthSB.setProgress(0);

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
                                            songListDiskImgAni.start();
                                            songListSongIndicator.setAlpha(1);
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

                                    update();

                                    return;
                                } else if (musicIndex < R.raw.class.getFields().length - 2) {
                                    musicIndex++;

                                    // check if music media is null or not to create and call music file
                                    if (musicMedia == null) {
                                        musicMedia = new MediaPlayer();
                                    } else {
                                        musicMedia.release();
                                        musicMedia = new MediaPlayer();
                                    }

                                    musicMedia = MediaPlayer.create(PlayMusicScreen.this, songIdList.get(musicIndex));

                                    songLengthSB.setMax(musicMedia.getDuration()); // update song length on seek bar (use for repeat song case)
                                    songLengthSB.setProgress(0);

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
                                            songListDiskImgAni.start();
                                            songListSongIndicator.setAlpha(1);
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

                                    update();

                                    return;
                                }
                            }
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
        });


    }

    //------------------------------------- Skip next button -------------------------------------//
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

                musicMedia = MediaPlayer.create(PlayMusicScreen.this, songIdList.get(musicIndex));

                songLengthSB.setMax(musicMedia.getDuration()); // update song length on seek bar (use for repeat song case)
                songLengthSB.setProgress(0);

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
                        songListDiskImgAni.start();
                        songListSongIndicator.setAlpha(1);
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

    // event for skip next button in song list screen click
    public void onSongListSkipNextButtonClickListener() {
        songListSkipNextButton.setOnClickListener(new View.OnClickListener() {
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

                musicMedia = MediaPlayer.create(PlayMusicScreen.this, songIdList.get(musicIndex));

                songLengthSB.setMax(musicMedia.getDuration()); // update song length on seek bar (use for repeat song case)
                songLengthSB.setProgress(0);

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
                        songListDiskImgAni.start();
                        songListSongIndicator.setAlpha(1);
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
    //--------------------------------------------------------------------------------------------//

    //------------------------------------ Skip previous button ----------------------------------//
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

                musicMedia = MediaPlayer.create(PlayMusicScreen.this, songIdList.get(musicIndex));

                songLengthSB.setMax(musicMedia.getDuration()); // update song length on seek bar (use for repeat song case)
                songLengthSB.setProgress(0);

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
                        songListDiskImgAni.start();
                        songListSongIndicator.setAlpha(1);
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

    // event for skip previous button in song list screen click
    public void onSongListSkipPreviousButtonClickListener() {
        songListSkipPreviousButton.setOnClickListener(new View.OnClickListener() {
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

                musicMedia = MediaPlayer.create(PlayMusicScreen.this, songIdList.get(musicIndex));

                songLengthSB.setMax(musicMedia.getDuration()); // update song length on seek bar (use for repeat song case)
                songLengthSB.setProgress(0);

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
                        songListDiskImgAni.start();
                        songListSongIndicator.setAlpha(1);
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
    //--------------------------------------------------------------------------------------------//

    // event for changing screen style by switch
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

    //------------------------------- custom recycler view setting -------------------------------//
    // method to create indicators and remove button for song list item
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
        for (int i = 0; i < songNameArr.size(); i++) {
            customItems.add(new CustomRecyclerViewItem(songNameArr.get(i), singerNameArr.get(i), indicators[i], removeSongButtons[i]));
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
    //--------------------------------------------------------------------------------------------//

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

    // event for pass screen button click
    public void onPassButtonGifViewClickListener() {
        passScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passScreenButton.play();

                // check of user is on music list screen or not
                if (isOnSongListScreen == false) {
                    mainScreenScrollLayout.animate().translationX(480).setDuration(300).setStartDelay(0);
                    songListBottomSheetLay.animate().translationX(0).setDuration(300).setStartDelay(0);

                    // animation for pass screen button
                    passScreenButtonAni = ObjectAnimator.ofFloat(passScreenButton, View.ROTATION, 0f, 180f).setDuration(300);
                    passScreenButtonAni.setInterpolator(new LinearInterpolator());
                    passScreenButtonAni.setStartDelay(300);
                    passScreenButtonAni.start();
                    passScreenButtonAni = ObjectAnimator.ofFloat(passScreenButton, View.TRANSLATION_X, 350).setDuration(600);
                    passScreenButtonAni.setStartDelay(600);
                    passScreenButtonAni.start();

                    passScreenButtonAni.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            passScreenButton.pause();
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });

                    isOnSongListScreen = true;
                } else {
                    mainScreenScrollLayout.animate().translationX(0).setDuration(300).setStartDelay(0);
                    songListBottomSheetLay.animate().translationX(-480).setDuration(300).setStartDelay(0);

                    // animation for pass screen button
                    passScreenButtonAni = ObjectAnimator.ofFloat(passScreenButton, View.ROTATION, 180f, 0f).setDuration(300);
                    passScreenButtonAni.setInterpolator(new LinearInterpolator());
                    passScreenButtonAni.setStartDelay(300);
                    passScreenButtonAni.start();
                    passScreenButtonAni = ObjectAnimator.ofFloat(passScreenButton, View.TRANSLATION_X, 0).setDuration(600);
                    passScreenButtonAni.setStartDelay(600);
                    passScreenButtonAni.start();

                    passScreenButtonAni.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            passScreenButton.pause();
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });

                    isOnSongListScreen = false;
                }
            }
        });
    }

    //----------------------------------- Repeat music button ------------------------------------//
    // event for repeat music button click
    public void onRepeatListButtonClickListener() {
        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repeatedClickTime < 3) {
                    repeatedClickTime++; // increase choice time
                }

                // check the choice of the user
                if (repeatedClickTime == 1) {
                    repeatButton.setImageResource(R.drawable.repeat_one_button);
                    songListReplayListButton.setImageResource(R.drawable.repeat_one_button);
                } else if (repeatedClickTime == 2) {
                    repeatButton.setImageResource(R.drawable.repeated_button);
                    songListReplayListButton.setImageResource(R.drawable.repeated_button);
                } else {
                    repeatButton.setImageResource(R.drawable.repeat_button);
                    songListReplayListButton.setImageResource(R.drawable.repeat_button);

                    repeatedClickTime = 0;
                }
            }
        });
    }

    // event for repeat button in song list screen click
    public void onSongListRepeatButtonClickListener() {
        songListReplayListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repeatedClickTime < 3) {
                    repeatedClickTime++; // increase choice time
                }

                // check the choice of the user
                if (repeatedClickTime == 1) {
                    repeatButton.setImageResource(R.drawable.repeat_one_button);
                    songListReplayListButton.setImageResource(R.drawable.repeat_one_button);
                } else if (repeatedClickTime == 2) {
                    repeatButton.setImageResource(R.drawable.repeated_button);
                    songListReplayListButton.setImageResource(R.drawable.repeated_button);
                } else {
                    repeatButton.setImageResource(R.drawable.repeat_button);
                    songListReplayListButton.setImageResource(R.drawable.repeat_button);

                    repeatedClickTime = 0;
                }
            }
        });
    }
    //--------------------------------------------------------------------------------------------//

    //------------------------------------- Shuffle music button ---------------------------------//
    // event for shuffle song list button click
    public void onShuffleListButtonClickListener() {
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShuffled) {
                    shuffleButton.setImageResource(R.drawable.shuffle_button);
                    songListMixListButton.setImageResource(R.drawable.shuffle_button);

                    isShuffled = false;
                } else {
                    shuffleButton.setImageResource(R.drawable.shuffled_button);
                    songListMixListButton.setImageResource(R.drawable.shuffled_button);

                    isShuffled = true;
                }
            }
        });
    }

    // event for shuffle song list button in song list screen click
    public void onSongListShuffleButtonClickListener() {
        songListMixListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShuffled) {
                    shuffleButton.setImageResource(R.drawable.shuffle_button);
                    songListMixListButton.setImageResource(R.drawable.shuffle_button);

                    isShuffled = false;
                } else {
                    shuffleButton.setImageResource(R.drawable.shuffled_button);
                    songListMixListButton.setImageResource(R.drawable.shuffled_button);

                    isShuffled = true;
                }
            }
        });
    }
    //--------------------------------------------------------------------------------------------//

    // event for song list item click
    public void onSongListItemClickListener() {
        songListAdapter.setOnItemClickListener(new CustomRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                musicIndex = position;

                updateSongNameAndSingerNameTV(musicIndex); // update the current song name and singer name for all text view
                changedSongListSelectedItemIndicatorAlpha(position, Color.BLUE);

                // check if music media is null or not to create and call music file
                if (musicMedia == null) {
                    musicMedia = new MediaPlayer();
                } else {
                    musicMedia.release();
                    musicMedia = new MediaPlayer();
                }
                musicMedia = MediaPlayer.create(PlayMusicScreen.this, songIdList.get(musicIndex));

                isPlay = true;

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
                        songListDiskImgAni.resume();
                        songListSongIndicator.setAlpha(1);
                    } else {
                        diskImgAni.start();
                        songListDiskImgAni.start();
                        songListSongIndicator.setAlpha(1);
                    }
                } else {
                    diskImgAni.pause();
                    songListDiskImgAni.pause();
                    musicWaveVisualization.onPause();
                    songListSongIndicator.setAlpha(0);
                }

                startSong(isPlay);
            }

            @Override
            public void onDeleteItemButtonClick(int position) {
                if (musicIndex == customItems.size() - 1 && musicIndex == position) {
                    musicIndex = 0;

                    // check if music media is null or not to create and call music file
                    if (musicMedia == null) {
                        musicMedia = new MediaPlayer();
                    } else {
                        musicMedia.release();
                        musicMedia = new MediaPlayer();
                    }
                    musicMedia = MediaPlayer.create(PlayMusicScreen.this, songIdList.get(musicIndex));

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
                            songListDiskImgAni.resume();
                            songListSongIndicator.setAlpha(1);
                        } else {
                            diskImgAni.start();
                            songListDiskImgAni.start();
                            songListSongIndicator.setAlpha(1);
                        }
                    } else {
                        diskImgAni.pause();
                        songListDiskImgAni.pause();
                        musicWaveVisualization.onPause();
                        songListSongIndicator.setAlpha(0);
                    }

                    if (isPlay) {
                        startSong(true);
                        updateSongNameAndSingerNameTV(musicIndex); // update the current song name and singer name for all text view
                        changedSongListSelectedItemIndicatorAlpha(position, Color.BLUE);
                    }

                    removeSongListItem(position);
                    songIdList.remove(position);
                    songNameArr.remove(position);
                    singerNameArr.remove(position);

                    if (customItems.size() == 0) {
                        if (diskImgAni.isRunning()) {
                            diskImgAni.end();
                            songListDiskImgAni.end();
                            songListSongIndicator.setAlpha(0);
                        }
                        if (musicMedia.isPlaying()) {
                            musicWaveVisualization.release();
                        }
                        playButton.setImageResource(R.drawable.play_music_button);
                        songListPlayMusicButton.setImageResource(R.drawable.play_music_button);
                        isPlay = false;

                        musicMedia.release();
                        musicMedia = new MediaPlayer();

                        songLengthSB.setProgress(0);
                    }
                } else if (musicIndex != position) {
                    removeSongListItem(position);
                    songIdList.remove(position);
                    songNameArr.remove(position);
                    singerNameArr.remove(position);

                    if (customItems.size() == 0) {
                        if (diskImgAni.isRunning()) {
                            diskImgAni.end();
                            songListDiskImgAni.end();
                            songListSongIndicator.setAlpha(0);
                        }
                        if (musicMedia.isPlaying()) {
                            musicWaveVisualization.release();
                        }
                        playButton.setImageResource(R.drawable.play_music_button);
                        songListPlayMusicButton.setImageResource(R.drawable.play_music_button);
                        isPlay = false;

                        musicMedia.release();
                        musicMedia = new MediaPlayer();

                        songLengthSB.setProgress(0);
                    }
                } else if (musicIndex < customItems.size() - 1 && musicIndex == position) {
                    musicIndex++;

                    // check if music media is null or not to create and call music file
                    if (musicMedia == null) {
                        musicMedia = new MediaPlayer();
                    } else {
                        musicMedia.release();
                        musicMedia = new MediaPlayer();
                    }
                    musicMedia = MediaPlayer.create(PlayMusicScreen.this, songIdList.get(musicIndex));

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
                            songListDiskImgAni.resume();
                            songListSongIndicator.setAlpha(1);
                        } else {
                            diskImgAni.start();
                            songListDiskImgAni.start();
                            songListSongIndicator.setAlpha(1);
                        }
                    } else {
                        diskImgAni.pause();
                        songListDiskImgAni.pause();
                        musicWaveVisualization.onPause();
                        songListSongIndicator.setAlpha(0);
                    }

                    if (isPlay) {
                        startSong(true);
                        updateSongNameAndSingerNameTV(musicIndex); // update the current song name and singer name for all text view
                        changedSongListSelectedItemIndicatorAlpha(position, Color.BLUE);
                    }

                    removeSongListItem(position);
                    songIdList.remove(position);
                    songNameArr.remove(position);
                    singerNameArr.remove(position);

                    if (customItems.size() == 0) {
                        if (diskImgAni.isRunning()) {
                            diskImgAni.end();
                            songListDiskImgAni.end();
                            songListSongIndicator.setAlpha(0);
                        }
                        if (musicMedia.isPlaying()) {
                            musicWaveVisualization.release();
                        }
                        playButton.setImageResource(R.drawable.play_music_button);
                        songListPlayMusicButton.setImageResource(R.drawable.play_music_button);
                        isPlay = false;

                        musicMedia.release();
                        musicMedia = new MediaPlayer();

                        songLengthSB.setProgress(0);
                    }
                }
            }
        });
    }

    // method to change the alpha of the current song indicator
    public void changedSongListSelectedItemIndicatorAlpha(int itemPosition, int indicatorColor) {
        customItems.get(itemPosition).changedSongIndicatorBarColor(indicatorColor);
        songListAdapter.notifyItemChanged(itemPosition);
    }

    // method use to remove the song that user want to delete
    public void removeSongListItem(int position) {
        customItems.remove(position);
        songListAdapter.notifyItemRemoved(position);
    }
}
