package com.example.emotionmusicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.AbsoluteLayout;

public class ShowingAboutUsActivity extends AppCompatActivity {

    AbsoluteLayout aboutUsActivityScreenLay;

    public boolean isFinished;

    Animation showingAboutUsScreenAni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_showing_about_us);

        castControl();
    }

    // cast all the control that need to interact in current activity
    public void castControl() {
        aboutUsActivityScreenLay = (AbsoluteLayout) findViewById(R.id.aboutUsScreenMainLay);
    }

    @Override
    protected void onStart() {
        super.onStart();

        isFinished = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        isFinished = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(0, R.anim.slide_down_screen_ani);
        isFinished = true;

//        Intent startMainActivity = new Intent(ShowingAboutUsActivity.this, MainActivity.class);
//        startActivity(startMainActivity);
    }
}
