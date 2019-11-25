package com.example.emotionmusicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.taishi.library.Indicator;

import java.io.IOException;
import java.sql.Time;
import java.util.concurrent.TimeUnit;

public class PlayMusicScreen extends AppCompatActivity {
    ImageButton playButton;
    TextView songLengthTV, songNameTV, singerNameTV;
    SeekBar songLengthSB;
    Indicator musicIndicator;
    MediaPlayer musicMedia = new MediaPlayer();

    boolean isPlay = true;

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

                songLengthTV.setText(String.format("-" +"%02d:%02d",songLeftMin, songLeftSec));

                update(); // method use to update time for SeekBar and song length TV
            }
        }
    };

    //method to check if user is playing music or not
    public void startSong(boolean isPlaying) {
        if (!isPlaying) {
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

        musicMedia = MediaPlayer.create(PlayMusicScreen.this, R.raw.spectre_alanwalker);
        try {
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
    public void pauseSong() {
        playButton.setImageResource(R.drawable.play_music_button);

        musicMedia.pause();
        musicHandler.removeCallbacks(musicRunnable);
    }

    // method to resume playing music
    public void resumeSong() {
        playButton.setImageResource(R.drawable.pause_button);

        musicMedia.start();
        update();
    }

    // method to stop playing music
    public void stopSong() {
        playButton.setImageResource(R.drawable.play_music_button);

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
        songLengthTV = (TextView) findViewById(R.id.songLengthTV);
        songNameTV = (TextView) findViewById(R.id.songNameTV);
        singerNameTV = (TextView) findViewById(R.id.singerNameTV);
        songLengthSB = (SeekBar) findViewById(R.id.songLengthSeekBar);
        musicIndicator = (Indicator) findViewById(R.id.musicIndicator);

        musicIndicator.setDuration(0);
    }

    // event method for play music button
    public void onPlayMusicButtonClickListener() {
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicIndicator.setDuration(18000);

                isPlay = !isPlay;

                startSong(isPlay);
            }
        });
    }

    public void onMusicSeekBarLengthChangeListener() {
        int currentTime = musicMedia.getCurrentPosition();
        long songDuration = musicMedia.getDuration();

        long leftTime = songDuration - currentTime;
        long songLeftMin = TimeUnit.MILLISECONDS.toMinutes(leftTime);
        long songLeftSec = TimeUnit.MILLISECONDS.toSeconds(leftTime) - TimeUnit.MINUTES.toSeconds(songLeftMin);

        songLengthTV.setText(String.format("-" +"%02d:%02d",songLeftMin, songLeftSec));

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

                    songLengthTV.setText(String.format("-" + "%2d:%2d", songLeftMin, songLeftSec));
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

                    songLengthTV.setText(String.format("-" + "%2d:%2d", songLeftMin, songLeftSec));

                    update();
                }
            }
        });
    }
}
