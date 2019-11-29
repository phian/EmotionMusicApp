package com.example.emotionmusicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cleveroad.audiovisualization.AudioVisualization;
import com.cleveroad.audiovisualization.DbmHandler;
import com.cleveroad.audiovisualization.SpeechRecognizerDbmHandler;
import com.cleveroad.audiovisualization.VisualizerDbmHandler;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

public class PlayMusicScreen extends AppCompatActivity {
    ImageButton playButton, skipNextButton, skipPreviousButton, repeatButton, shuffleButton;
    TextView songLengthTV, songNameTV, singerNameTV;
    SeekBar songLengthSB;
    MediaPlayer musicMedia = null;
    CircularImageView diskImageCIV;

    AudioVisualization musicWaveVisualization;

    ObjectAnimator diskImgAni;

    boolean isPlay = false;
    int musicIndex = 0;

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
            musicMedia = MediaPlayer.create(PlayMusicScreen.this, R.raw.spectre_alanwalker);
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

        // call music file
        musicMedia = new MediaPlayer();
        musicMedia = MediaPlayer.create(PlayMusicScreen.this, R.raw.spectre_alanwalker);

        int currentTime = musicMedia.getCurrentPosition();
        long songDuration = musicMedia.getDuration();

        long leftTime = songDuration - currentTime;
        long songLeftMin = TimeUnit.MILLISECONDS.toMinutes(leftTime);
        long songLeftSec = TimeUnit.MILLISECONDS.toSeconds(leftTime) - TimeUnit.MINUTES.toSeconds(songLeftMin);
        songLengthTV.setText(String.format("-" + "%2d:%2d", songLeftMin, songLeftSec));
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
    }

    @Override
    public void finish() {
        super.finish();

        // add animation when user back to previous screen
        Intent startMainActivity = new Intent(PlayMusicScreen.this, ChooseEmotionActivity.class);
        startActivity(startMainActivity);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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

        songLengthSB = (SeekBar) findViewById(R.id.songLengthSeekBar);
        diskImageCIV = (CircularImageView) findViewById(R.id.diskImageCIV);
        musicWaveVisualization = (AudioVisualization) findViewById(R.id.musicWaveVisualization);
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
                    musicWaveVisualization.onResume();

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

    public void onSkipNextButtonClickListener() {
        skipNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicIndex < R.raw.class.getFields().length - 1) {
                    musicIndex++;
                } else {
                    musicIndex = 0;
                }

                // call music file
                musicMedia.release();
                musicMedia = new MediaPlayer();

                if (musicIndex == 0) {
                    musicMedia = MediaPlayer.create(PlayMusicScreen.this, R.raw.spectre_alanwalker);
                } else if (musicIndex == 1) {
                    musicMedia = MediaPlayer.create(PlayMusicScreen.this, R.raw.alone_alanwalker);
                } else {
                    musicMedia = MediaPlayer.create(PlayMusicScreen.this, R.raw.faded_alanwalker);
                }

                if(isPlay == true) {
                    musicMedia.start();

                    if (diskImgAni.isRunning() == false) {
                        diskImgAni.start();
                    }
                    if (musicMedia.isPlaying() == false) {
                        musicWaveVisualization.onResume();
                    }
                    playButton.setImageResource(R.drawable.pause_music_button);
                } else {
                    if (diskImgAni.isRunning() == true) {
                        diskImgAni.end();
                    }
                    if (musicMedia.isPlaying() == false) {
                        musicWaveVisualization.release();
                    }
                    playButton.setImageResource(R.drawable.play_music_button);
                }
            }
        });
    }

    public void onSkipPreviousButtonClickListener() {
        skipPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicIndex > 0) {
                    musicIndex--;
                } else {
                    musicIndex = R.raw.class.getFields().length - 1;
                }

                // call music file
                musicMedia.release();
                musicMedia = new MediaPlayer();

                if (musicIndex == 0) {
                    musicMedia = MediaPlayer.create(PlayMusicScreen.this, R.raw.spectre_alanwalker);
                } else if (musicIndex == 1) {
                    musicMedia = MediaPlayer.create(PlayMusicScreen.this, R.raw.alone_alanwalker);
                } else {
                    musicMedia = MediaPlayer.create(PlayMusicScreen.this, R.raw.faded_alanwalker);
                }


                if(isPlay == true) {
                    musicMedia.start();

                    if (diskImgAni.isRunning() == false) {
                        diskImgAni.start();
                    }
                    if (musicMedia.isPlaying() == false) {
                        musicWaveVisualization.onResume();
                    }
                    playButton.setImageResource(R.drawable.pause_music_button);
                } else {
                    if (diskImgAni.isRunning() == true) {
                        diskImgAni.end();
                    }
                    if (musicMedia.isPlaying() == false) {
                        musicWaveVisualization.release();
                    }
                    playButton.setImageResource(R.drawable.play_music_button);
                }
            }
        });
    }
}
