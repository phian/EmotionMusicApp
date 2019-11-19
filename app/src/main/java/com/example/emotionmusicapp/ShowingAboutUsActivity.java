package com.example.emotionmusicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

public class ShowingAboutUsActivity extends AppCompatActivity {

    SlidrInterface showingAboutUsSlidr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showing_about_us);
    }

    @Override
    public void finish() {
        super.finish();

        Intent startMainActivity = new Intent(ShowingAboutUsActivity.this, MainActivity.class);
        startActivity(startMainActivity);
    }
}
