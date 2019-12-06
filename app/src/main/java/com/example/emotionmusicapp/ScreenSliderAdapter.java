package com.example.emotionmusicapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.cleveroad.audiovisualization.AudioVisualization;

public class ScreenSliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater screenLayoutInflator;

    public ScreenSliderAdapter(Context context) {
        this.context = context;
    }

    public int[] slideImage = {
            R.drawable.skip_next_button,
            R.drawable.skip_previous_button,
            R.drawable.nath,
            R.drawable.play_music_button,
            R.drawable.shuffle_music_button,
            R.drawable.repeat_music_button
    };

    public AudioVisualization musicWaveVisualization;

    public int[] slideSwitch = {
            R.id.screenStyleSwitch,
            R.id.themeSwitch
    };

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return false;
    }
}
