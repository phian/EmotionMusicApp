package com.example.emotionmusicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;

public class ShowingAboutUsActivity extends AppCompatActivity {

    AbsoluteLayout aboutUsActivityScreenLay;

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

        Intent startMainActivity = new Intent(ShowingAboutUsActivity.this, MainActivity.class);
        startActivity(startMainActivity);
    }
}
