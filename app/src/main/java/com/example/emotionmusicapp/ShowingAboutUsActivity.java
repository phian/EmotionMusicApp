package com.example.emotionmusicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mancj.slideup.SlideUp;

public class ShowingAboutUsActivity extends AppCompatActivity {

    SlideUp slideUpScreen;
    FrameLayout dimScreen; // screen use when user slide the activity screen
    LinearLayout aboutUsActivityScreenLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showing_about_us);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        castControl();
    }

    // cast all the control that need to interact in current activity
    public void castControl() {
        aboutUsActivityScreenLay = (LinearLayout) findViewById(R.id.aboutUsActivityScreenLay);
        dimScreen = (FrameLayout) findViewById(R.id.dimScreen);

        // hide the slide screen
        slideUpScreen = new SlideUp(aboutUsActivityScreenLay);
        slideUpScreen.hideImmediately();
    }

    @Override
    protected void onStart() {
        super.onStart();

        slideUpScreen.animateIn();
        onSlideScreenSlideListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        slideUpScreen.animateIn();
        onSlideScreenSlideListener();
    }

    public void onSlideScreenSlideListener() {
        slideUpScreen.setSlideListener(new SlideUp.SlideListener() {
            @Override
            public void onSlideDown(float v) {
                dimScreen.setAlpha(1 - (v / 100));
            }

            @Override
            public void onVisibilityChanged(int i) {
                if(i == View.GONE) {
                    finish();
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();

        Intent startMainActivity = new Intent(ShowingAboutUsActivity.this, MainActivity.class);
        startActivity(startMainActivity);
    }
}
