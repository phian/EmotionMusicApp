package com.example.emotionmusicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // appNameAndInstructionIcon, instructionIconLayout, startAppButtonLay

    ImageView mainScreenBackground, cloverImg;

    LinearLayout appMainGreeting, appNameAndIcon, iconMenu, startAppButLay, instructionIconLayout, aboutUsContentLay;
    AbsoluteLayout mainScreen;
    RelativeLayout aboutUsHeaderTextLay;

    Button startButton;
    ImageButton dropDownScreenButton, aboutUsIcon, howToUseIcon;

    TextView mainGreetingTV, aboutUsTV, howToUseTV, appName;

    Animation appNameAndIconAni, startButtonAni, aboutUsHeaderTextAni;

    boolean isAboutUsIconClick, isHowToUseIconClick, isMainScreenPrevious;

    @SuppressLint({"ClickableViewAccessibility", "WrongConstant"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        castControl();

        // Add click ani for button
        PushDownAnim.setPushDownAnimTo(startButton, dropDownScreenButton, aboutUsIcon, howToUseIcon)
                .setDurationPush(PushDownAnim.DEFAULT_PUSH_DURATION)
                .setDurationRelease(PushDownAnim.DEFAULT_RELEASE_DURATION)
                .setInterpolatorPush(PushDownAnim.DEFAULT_INTERPOLATOR)
                .setInterpolatorRelease(PushDownAnim.DEFAULT_INTERPOLATOR);
    }

    // check if application is running then update the greeting text
    @Override
    protected void onStart() {
        super.onStart();

        setMainGreetingText(); // set the main greeting (Good morning,...)
        onMainScreenTouchListener();
    }

    // check if application is resumed then update the greeting text
    @Override
    protected void onResume() {
        super.onResume();

        setMainGreetingText(); // set the main greeting (Good morning,...)
    }

    // Check if user click back button to go back previous activity
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBackPressed() {
         if (isAboutUsIconClick == true && isMainScreenPrevious == false) {
            aboutUsHeaderTextAni = AnimationUtils.loadAnimation(MainActivity.this, R.anim.stretch_up_control_ani);
            aboutUsHeaderTextLay.startAnimation(aboutUsHeaderTextAni);

            aboutUsContentLay.animate().translationY(800).setDuration(300).setStartDelay(0);

            appNameAndIconAni = AnimationUtils.loadAnimation(MainActivity.this, R.anim.icon_zoom_in_layout_ani);
            instructionIconLayout.startAnimation(appNameAndIconAni);

            appName.animate().alpha(1).setDuration(200).setStartDelay(0);
            startButton.animate().translationY(0).alpha(1).setDuration(200).setStartDelay(0);
            mainScreenBackground.animate().translationY(mainScreenBackground.getY() + 150).alpha(1).setDuration(200).setStartDelay(0);

            onStartButtonClickListener();
            onAboutUsIconTouchListener();
            onHowToUseIconTouchListener();

            isAboutUsIconClick = false;
            isMainScreenPrevious = true;

            aboutUsHeaderTextAni.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    aboutUsHeaderTextLay.setAlpha(0);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

             // reset add click ani for button
             PushDownAnim.setPushDownAnimTo(startButton, dropDownScreenButton, aboutUsIcon, howToUseIcon)
                     .setDurationPush(PushDownAnim.DEFAULT_PUSH_DURATION)
                     .setDurationRelease(PushDownAnim.DEFAULT_RELEASE_DURATION)
                     .setInterpolatorPush(PushDownAnim.DEFAULT_INTERPOLATOR)
                     .setInterpolatorRelease(PushDownAnim.DEFAULT_INTERPOLATOR);
        } else if (isHowToUseIconClick == true && isMainScreenPrevious == false) {
            aboutUsContentLay.animate().translationY(800).setDuration(300).setStartDelay(0);

            appNameAndIconAni = AnimationUtils.loadAnimation(MainActivity.this, R.anim.icon_zoom_in_layout_ani);
            instructionIconLayout.startAnimation(appNameAndIconAni);

            appName.animate().alpha(1).setDuration(200).setStartDelay(0);
            startButton.animate().translationY(0).alpha(1).setDuration(200).setStartDelay(0);
            mainScreenBackground.animate().translationY(mainScreenBackground.getY() + 150).alpha(1).setDuration(200).setStartDelay(0);

            onStartButtonClickListener();
            onAboutUsIconTouchListener();
            onHowToUseIconTouchListener();

            isHowToUseIconClick = false;
            isMainScreenPrevious = true;
        } else if (isMainScreenPrevious == true && isAboutUsIconClick == false && isHowToUseIconClick == false) {
            mainScreenBackground.animate().translationY(0).setDuration(500).setStartDelay(300);
            //cloverImg.animate().alpha(0).setDuration(800).setStartDelay(600);
            cloverImg.animate().translationX(0).setDuration(500).setStartDelay(600);
            appMainGreeting.animate().translationY(0).alpha(1).setDuration(500).setStartDelay(300);

            // disable animation of control in next screen
            appNameAndIcon.setVisibility(View.INVISIBLE);
            iconMenu.setAlpha(0);
            startAppButLay.setVisibility(View.INVISIBLE);

            isMainScreenPrevious = false;

             aboutUsIcon.setOnTouchListener(null);
             howToUseIcon.setOnTouchListener(null);
             startButton.setOnClickListener(null);

            onMainScreenTouchListener();

             // reset add click ani for button
             PushDownAnim.setPushDownAnimTo(startButton, dropDownScreenButton, aboutUsIcon, howToUseIcon)
                     .setDurationPush(PushDownAnim.DEFAULT_PUSH_DURATION)
                     .setDurationRelease(PushDownAnim.DEFAULT_RELEASE_DURATION)
                     .setInterpolatorPush(PushDownAnim.DEFAULT_INTERPOLATOR)
                     .setInterpolatorRelease(PushDownAnim.DEFAULT_INTERPOLATOR);
        } else if (false == false && false == false && false == false) {
            finish();
        }
    }

    // cast all the control need to set animation
    public void castControl() {
        mainScreenBackground = (ImageView) findViewById(R.id.mainBackGround);
        cloverImg = (ImageView) findViewById(R.id.clover);


        appMainGreeting = (LinearLayout) findViewById(R.id.splashScreen);
        appNameAndIcon = (LinearLayout) findViewById(R.id.appNameAndInstructionIcon);
        iconMenu = (LinearLayout) findViewById(R.id.instructionIconLayout);
        startAppButLay = (LinearLayout) findViewById(R.id.startAppButtonLay);
        instructionIconLayout = (LinearLayout) findViewById(R.id.instructionIconLayout);
        aboutUsContentLay = (LinearLayout) findViewById(R.id.aboutUsContentLay);

        appNameAndIcon.setVisibility(View.INVISIBLE);
        iconMenu.setVisibility(View.INVISIBLE);
        startAppButLay.setVisibility(View.INVISIBLE);
        mainScreen = (AbsoluteLayout) findViewById(R.id.mainScreen);

        aboutUsHeaderTextLay = (RelativeLayout) findViewById(R.id.aboutUsHeaderTextLay);

        startButton = (Button) findViewById(R.id.startAppButton);
        dropDownScreenButton = (ImageButton) findViewById(R.id.dropDownScreenButton);
        aboutUsIcon = (ImageButton) findViewById(R.id.aboutUsIcon);
        howToUseIcon = (ImageButton) findViewById(R.id.howToUseAppIcon);

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
        iconMenu.setAlpha(1);
        iconMenu.startAnimation(appNameAndIconAni);

        startButtonAni = AnimationUtils.loadAnimation(this, R.anim.start_button_ani);
        startButton.startAnimation(startButtonAni);
    }

    // event for main screen touch to begin setting animation for all control
    public void onMainScreenTouchListener() {
        mainScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainScreenBackground.animate().translationY(-670).setDuration(500).setStartDelay(300);
                //cloverImg.animate().alpha(0).setDuration(800).setStartDelay(600);
                cloverImg.animate().translationX(-1000).setDuration(500).setStartDelay(600);
                appMainGreeting.animate().translationY(140).alpha(0).setDuration(500).setStartDelay(300);

                appNameAndIcon.setVisibility(View.VISIBLE);
                iconMenu.setVisibility(View.VISIBLE);
                startAppButLay.setVisibility(View.VISIBLE);

                setControlAnimation();

                mainScreen.setOnClickListener(null); // disable touch event after finish the animation

                isMainScreenPrevious = true;

                onAboutUsIconTouchListener();
                onHowToUseIconTouchListener();
                onStartButtonClickListener();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
    }

    // event when user click the start app button
    @SuppressLint("ClickableViewAccessibility")
    public void onStartButtonClickListener() {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                startButton.setOnClickListener(null);
            }
        });
    }

    // event for About Us icon
    @SuppressLint("ClickableViewAccessibility")
    public void onAboutUsIconTouchListener() {
        aboutUsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAboutUsIconClick = true;
                isMainScreenPrevious = false;

                appNameAndIconAni = AnimationUtils.loadAnimation(MainActivity.this, R.anim.icon_zoom_out_ani);
                instructionIconLayout.startAnimation(appNameAndIconAni);

                appName.animate().alpha(0).setDuration(200).setStartDelay(0);
                startButton.animate().translationY(100).alpha(0).setDuration(200).setStartDelay(0);
                mainScreenBackground.animate().translationY(mainScreenBackground.getY() - 150).alpha(0).setDuration(200).setStartDelay(0);

                aboutUsIcon.setOnTouchListener(null);
                howToUseIcon.setOnTouchListener(null);
                startButton.setOnClickListener(null);

                aboutUsContentLay.animate().translationY(95).setDuration(300).setStartDelay(0);

                aboutUsHeaderTextLay.setAlpha(1);
                aboutUsHeaderTextAni = AnimationUtils.loadAnimation(MainActivity.this, R.anim.stretch_down_control_ani);
                aboutUsHeaderTextLay.startAnimation(aboutUsHeaderTextAni);

                onDropDownScreenButtonClickListener();
            }
        });
    }

    // event for How To Use icon
    @SuppressLint("ClickableViewAccessibility")
    public void onHowToUseIconTouchListener() {
        howToUseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appNameAndIconAni = AnimationUtils.loadAnimation(MainActivity.this, R.anim.icon_zoom_out_ani);
                instructionIconLayout.startAnimation(appNameAndIconAni);

                appName.animate().alpha(0).setDuration(100).setStartDelay(0);
                startButton.animate().translationY(100).alpha(0).setDuration(200).setStartDelay(0);
                mainScreenBackground.animate().translationY(mainScreenBackground.getY() - 150).alpha(0).setDuration(200).setStartDelay(0);

                howToUseIcon.setOnTouchListener(null);
                startButton.setOnClickListener(null);
                aboutUsIcon.setOnTouchListener(null);

                isAboutUsIconClick = true;
                isMainScreenPrevious = false;
            }
        });
    }

    // event for drop down screen button click
    @SuppressLint("ClickableViewAccessibility")
    public void onDropDownScreenButtonClickListener() {
        dropDownScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAboutUsIconClick = false;
                isMainScreenPrevious = true;

                aboutUsHeaderTextAni = AnimationUtils.loadAnimation(MainActivity.this, R.anim.stretch_up_control_ani);
                aboutUsHeaderTextLay.startAnimation(aboutUsHeaderTextAni);

                aboutUsContentLay.animate().translationY(800).setDuration(300).setStartDelay(0);

                appNameAndIconAni = AnimationUtils.loadAnimation(MainActivity.this, R.anim.icon_zoom_in_layout_ani);
                instructionIconLayout.startAnimation(appNameAndIconAni);

                appName.animate().alpha(1).setDuration(200).setStartDelay(0);
                startButton.animate().translationY(0).alpha(1).setDuration(200).setStartDelay(0);
                mainScreenBackground.animate().translationY(mainScreenBackground.getY() + 150).alpha(1).setDuration(200).setStartDelay(0);

                aboutUsHeaderTextAni.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        aboutUsHeaderTextLay.setAlpha(0);

                        onStartButtonClickListener();
                        onAboutUsIconTouchListener();
                        onHowToUseIconTouchListener();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                dropDownScreenButton.setOnClickListener(null);

                // reset add click ani for button
                PushDownAnim.setPushDownAnimTo(startButton, dropDownScreenButton, aboutUsIcon, howToUseIcon)
                        .setDurationPush(PushDownAnim.DEFAULT_PUSH_DURATION)
                        .setDurationRelease(PushDownAnim.DEFAULT_RELEASE_DURATION)
                        .setInterpolatorPush(PushDownAnim.DEFAULT_INTERPOLATOR)
                        .setInterpolatorRelease(PushDownAnim.DEFAULT_INTERPOLATOR);
            }
        });
    }
}
