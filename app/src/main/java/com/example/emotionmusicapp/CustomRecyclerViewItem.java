package com.example.emotionmusicapp;

import android.widget.ImageButton;

import com.taishi.library.Indicator;


public class CustomRecyclerViewItem {
    private String songName;
    private String singerName;
    private Indicator songIndicator;

    public ImageButton getRemoveSongButton() {
        return removeSongButton;
    }

    public void setRemoveSongButton(ImageButton removeSongButton) {
        this.removeSongButton = removeSongButton;
    }

    private ImageButton removeSongButton;

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public Indicator getSongIndicator() {
        return songIndicator;
    }

    public void setSongIndicator(Indicator songIndicator) {
        this.songIndicator = songIndicator;
    }

    public CustomRecyclerViewItem(String songName, String singerName, Indicator songIndicator, ImageButton removeSongButton) {
        this.songName = songName;
        this.singerName = singerName;
        this.songIndicator = songIndicator;
        this.removeSongButton = removeSongButton;
    }
}
