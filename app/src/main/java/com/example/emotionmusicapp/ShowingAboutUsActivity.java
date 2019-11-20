package com.example.emotionmusicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.AbsoluteLayout;

public class ShowingAboutUsActivity extends AppCompatActivity {

    AbsoluteLayout aboutUsActivityScreenLay;

    Animation showingAboutUsScreenAni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_showing_about_us);

        castControl();

//        showingAboutUsScreenAni = AnimationUtils.loadAnimation(ShowingAboutUsActivity.this, R.anim.slide_up_screen_ani);
//        aboutUsActivityScreenLay.startAnimation(showingAboutUsScreenAni);
    }

    // cast all the control that need to interact in current activity
    public void castControl() {
        aboutUsActivityScreenLay = (AbsoluteLayout) findViewById(R.id.aboutUsScreenMainLay);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(0, R.anim.slide_down_screen_ani);
    }
}
