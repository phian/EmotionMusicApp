package com.example.emotionmusicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // appNameAndInstructionIcon, instructionIconLayout, startAppButtonLay

    ImageView mainScreenBackground, cloverImg, aboutUsIcon, howToUseIcon;

    LinearLayout appMainGreeting, appNameAndIcon, iconMenu, startAppButLay;
    AbsoluteLayout mainScreen;

    Button startButton;
    TextView mainGreetingTV, aboutUsTV, howToUseTV, appName;

    Animation appNameAndIconAni, startButtonAni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        castControl();
    }

    // check if application is running then update the greeting text
    @Override
    protected void onStart() {
        super.onStart();

        setMainGreetingText(); // set the main greeting (Good morning,...)
    }

    // check if application is resumed then update the greeting text
    @Override
    protected void onResume() {
        super.onResume();

        setMainGreetingText(); // set the main greeting (Good morning,...)
    }

    // cast all the control need to set animation
    public void castControl() {
        mainScreenBackground = (ImageView) findViewById(R.id.mainBackGround);
        cloverImg = (ImageView) findViewById(R.id.clover);
        aboutUsIcon = (ImageView) findViewById(R.id.aboutUsIcon);
        howToUseIcon = (ImageView) findViewById(R.id.howToUseAppIcon);

        appMainGreeting = (LinearLayout) findViewById(R.id.splashScreen);
        appNameAndIcon = (LinearLayout) findViewById(R.id.appNameAndInstructionIcon);
        iconMenu = (LinearLayout) findViewById(R.id.instructionIconLayout);
        startAppButLay = (LinearLayout) findViewById(R.id.startAppButtonLay);

        appNameAndIcon.setVisibility(View.INVISIBLE);
        iconMenu.setVisibility(View.INVISIBLE);
        startAppButLay.setVisibility(View.INVISIBLE);

        mainScreen = (AbsoluteLayout) findViewById(R.id.mainScreen);

        startButton = (Button) findViewById(R.id.startAppButton);

        mainGreetingTV = (TextView) findViewById(R.id.greetingTV);
        aboutUsTV = (TextView) findViewById(R.id.aboutUsTV);
        howToUseTV = (TextView) findViewById(R.id.howToUseTV);
        appName = (TextView) findViewById(R.id.appNameText);

        setMainGreetingText();
    }

    // get the system time to set the greeting text
    public void setMainGreetingText() {
        Date currentTime = Calendar.getInstance().getTime();

        if (currentTime.getHours() >= 0 && currentTime.getHours() < 12) {
            mainGreetingTV.setText("Good Morning");
        } else if (currentTime.getHours() >= 12 && currentTime.getHours() < 18) {
            mainGreetingTV.setText("Good Afternoon");
        } else if (currentTime.getHours() >= 18 && currentTime.getHours() <= 24) {
            mainGreetingTV.setText("Good Evening");
        }
    }


    // set the animation for each control
    public void setControlAnimation() {
        appNameAndIconAni = AnimationUtils.loadAnimation(this, R.anim.instruction_icon_ani);
        appNameAndIcon.startAnimation(appNameAndIconAni);

        appNameAndIconAni = AnimationUtils.loadAnimation(this, R.anim.icon_menu_ani);
        iconMenu.startAnimation(appNameAndIconAni);

        startButtonAni = AnimationUtils.loadAnimation(this, R.anim.start_button_ani);
        startButton.startAnimation(startButtonAni);
    }

    // event for main screen touch to begin setting animation for all control
    public void onMainScreenTouchListener(View view) {
        mainScreenBackground.animate().translationY(-670).setDuration(500).setStartDelay(300);

        //cloverImg.animate().alpha(0).setDuration(800).setStartDelay(600);
        cloverImg.animate().translationX(-1000).setDuration(500).setStartDelay(600);

        appMainGreeting.animate().translationY(140).alpha(0).setDuration(500).setStartDelay(300);

        appNameAndIcon.setVisibility(View.VISIBLE);
        iconMenu.setVisibility(View.VISIBLE);
        startAppButLay.setVisibility(View.VISIBLE);

        setControlAnimation();

        mainScreen.setOnClickListener(null); // disable touch event after finish the animation
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // event when user click the start app button
    public void onStartButtonClickListener(View view) {
        startButtonAni = AnimationUtils.loadAnimation(MainActivity.this, R.anim.start_button_gone_ani);
        startButton.startAnimation(startButtonAni);

        startButtonAni.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mainScreenBackground.animate().translationY(mainScreenBackground.getY() - 150).alpha(0).setDuration(200).setStartDelay(0);
                aboutUsIcon.animate().translationX(-80).alpha(0).setDuration(200).setStartDelay(0);
                howToUseIcon.animate().translationX(80).alpha(0).setDuration(200).setStartDelay(0);
                aboutUsTV.animate().translationX(-80).alpha(0).setDuration(200).setStartDelay(0);
                howToUseTV.animate().translationX(80).alpha(0).setDuration(200).setStartDelay(0);
                appName.animate().alpha(0).setDuration(100).setStartDelay(0);
                startButton.animate().translationY(100).alpha(0).setDuration(200).setStartDelay(0);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent startChooseEmotionScreen = new Intent(MainActivity.this, ChooseEmotionActivity.class);
                startActivity(startChooseEmotionScreen);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
