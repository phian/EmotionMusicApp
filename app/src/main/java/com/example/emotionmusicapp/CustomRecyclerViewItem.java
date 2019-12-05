package com.example.emotionmusicapp;

import android.content.Context;

import com.taishi.library.Indicator;

import net.igenius.customcheckbox.CustomCheckBox;

public class CustomRecyclerViewItem {
    private String songName;
    private String singerName;
    private CustomCheckBox deleteCB;
    private Indicator songIndicator;

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

    public CustomCheckBox getDeleteCB() {
        return deleteCB;
    }

    public void setDeleteCB(CustomCheckBox deleteCB) {
        this.deleteCB = deleteCB;
    }

    public Indicator getSongIndicator() {
        return songIndicator;
    }

    public void setSongIndicator(Indicator songIndicator) {
        this.songIndicator = songIndicator;
    }

    public CustomRecyclerViewItem(String songName, String singerName, CustomCheckBox deleteCB, Indicator songIndicator) {
        this.songName = songName;
        this.singerName = singerName;
        this.deleteCB = deleteCB;
        this.songIndicator = songIndicator;
    }
}
