package com.example.emotionmusicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class ChooseEmotionActivity extends AppCompatActivity {

    ImageView mainBackGround, happyEarthEmotion, loveEarthEmotion, sadEarthEmotion, boringEarthEmotion, cryEarthEmotion, sickEarthEmotion, angryEarthEmotion;
    AbsoluteLayout mainScreen;
    LinearLayout questionTextLayout;
    TextView happyEarthEmotionText, loveEarthEmotionText, sadEarthEmotionText, boringEarthEmotionText, cryEarthEmotionText, sickEarthEmotionText, angryEarthEmotionText;

    TextView questionText;

    Animation chooseEmotionScreenAni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // code allow app to use full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_choose_emotion);

        castControl();
        setChooseEmotionScreenBackground();
        setControlAnimation();
    }

    @Override
    protected void onStart() {
        super.onStart();

        setChooseEmotionScreenBackground();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setChooseEmotionScreenBackground();
    }

    @Override
    public void finish() {
        super.finish();

        // add animation when user back to previous screen
        Intent startMainActivity = new Intent(ChooseEmotionActivity.this, MainActivity.class);
        startActivity(startMainActivity);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    // cast all icon and textview to set animation
    public void castControl() {
        mainBackGround = (ImageView) findViewById(R.id.chooseEmotionMainBackGround);
        mainScreen = (AbsoluteLayout) findViewById(R.id.mainScreen);
        questionTextLayout = (LinearLayout) findViewById(R.id.questionLayout);

        happyEarthEmotion = (ImageView) findViewById(R.id.happyEarthEmotion);
        happyEarthEmotionText = (TextView) findViewById(R.id.happyEmotionText);

        loveEarthEmotion = (ImageView) findViewById(R.id.loveEarthEmotion);
        loveEarthEmotionText = (TextView) findViewById(R.id.loveEmotionText);

        sadEarthEmotion = (ImageView) findViewById(R.id.sadEarthEmotion);
        sadEarthEmotionText = (TextView) findViewById(R.id.sadEmotionText);

        boringEarthEmotion = (ImageView) findViewById(R.id.boringEarthEmotion);
        boringEarthEmotionText = (TextView) findViewById(R.id.boringEmotionText);

        cryEarthEmotion = (ImageView) findViewById(R.id.cryEarthEmotion);
        cryEarthEmotionText = (TextView) findViewById(R.id.cryEmotionText);

        sickEarthEmotion = (ImageView) findViewById(R.id.sickEarthEmotion);
        sickEarthEmotionText = (TextView) findViewById(R.id.sickEmotionText);

        angryEarthEmotion = (ImageView) findViewById(R.id.angryEarthEmotion);
        angryEarthEmotionText = (TextView) findViewById(R.id.angryEmotionText);
    }

    // set animation for all emotion icon and textview
    public void setControlAnimation() {
        //chooseEmotionScreenAni = AnimationUtils.loadAnimation(ChooseEmotionActivity.this, R.anim.choose_emotion_screen_back_ground_ani);
        //mainBackGround.startAnimation(chooseEmotionScreenAni);

        chooseEmotionScreenAni = AnimationUtils.loadAnimation(ChooseEmotionActivity.this, R.anim.choose_emotion_screen_headertext_ani);
        questionTextLayout.startAnimation(chooseEmotionScreenAni);

        chooseEmotionScreenAni = AnimationUtils.loadAnimation(ChooseEmotionActivity.this, R.anim.happy_icon_and_text_ani);
        happyEarthEmotion.startAnimation(chooseEmotionScreenAni);
        chooseEmotionScreenAni = AnimationUtils.loadAnimation(ChooseEmotionActivity.this, R.anim.happy_icon_and_text_ani);
        happyEarthEmotionText.startAnimation(chooseEmotionScreenAni);

        chooseEmotionScreenAni = AnimationUtils.loadAnimation(ChooseEmotionActivity.this, R.anim.love_emotion_and_text_ani);
        loveEarthEmotion.startAnimation(chooseEmotionScreenAni);
        chooseEmotionScreenAni = AnimationUtils.loadAnimation(ChooseEmotionActivity.this, R.anim.love_emotion_and_text_ani);
        loveEarthEmotionText.startAnimation(chooseEmotionScreenAni);

        chooseEmotionScreenAni = AnimationUtils.loadAnimation(ChooseEmotionActivity.this, R.anim.sad_icon_and_text_ani);
        sadEarthEmotion.startAnimation(chooseEmotionScreenAni);
        chooseEmotionScreenAni = AnimationUtils.loadAnimation(ChooseEmotionActivity.this, R.anim.sad_icon_and_text_ani);
        sadEarthEmotionText.startAnimation(chooseEmotionScreenAni);

        chooseEmotionScreenAni = AnimationUtils.loadAnimation(ChooseEmotionActivity.this, R.anim.boring_icon_and_text_ani);
        boringEarthEmotion.startAnimation(chooseEmotionScreenAni);
        chooseEmotionScreenAni = AnimationUtils.loadAnimation(ChooseEmotionActivity.this, R.anim.boring_icon_and_text_ani);
        boringEarthEmotionText.startAnimation(chooseEmotionScreenAni);

        chooseEmotionScreenAni = AnimationUtils.loadAnimation(ChooseEmotionActivity.this, R.anim.cry_icon_and_text_ani);
        cryEarthEmotion.startAnimation(chooseEmotionScreenAni);
        chooseEmotionScreenAni = AnimationUtils.loadAnimation(ChooseEmotionActivity.this, R.anim.cry_icon_and_text_ani);
        cryEarthEmotionText.startAnimation(chooseEmotionScreenAni);

        chooseEmotionScreenAni = AnimationUtils.loadAnimation(ChooseEmotionActivity.this, R.anim.sick_icon_and_text_ani);
        sickEarthEmotion.startAnimation(chooseEmotionScreenAni);
        chooseEmotionScreenAni = AnimationUtils.loadAnimation(ChooseEmotionActivity.this, R.anim.sick_icon_and_text_ani);
        sickEarthEmotionText.startAnimation(chooseEmotionScreenAni);

        chooseEmotionScreenAni = AnimationUtils.loadAnimation(ChooseEmotionActivity.this, R.anim.angry_icon_and_text_ani);
        angryEarthEmotion.startAnimation(chooseEmotionScreenAni);
        chooseEmotionScreenAni = AnimationUtils.loadAnimation(ChooseEmotionActivity.this, R.anim.angry_icon_and_text_ani);
        angryEarthEmotionText.startAnimation(chooseEmotionScreenAni);

        setQuestionTextBackground();
    }

    public void setQuestionTextBackground() {
        chooseEmotionScreenAni.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // set background for question text after finish all animation to make a square for emotion icon
                questionText = (TextView) findViewById(R.id.questionText);

                Bitmap mainBackgroundBmp = ((BitmapDrawable) mainBackGround.getDrawable()).getBitmap();
                Bitmap cropImg = Bitmap.createBitmap(mainBackgroundBmp, 0, 0, mainBackgroundBmp.getWidth(), mainBackgroundBmp.getHeight() - 940);

                questionText.setBackground(new BitmapDrawable(getResources(), cropImg));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    // check current day is which day of the werk to set the correct background
    public void setChooseEmotionScreenBackground() {
        switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                mainBackGround.setImageResource(R.drawable.music_notes_background);
                break;
            case Calendar.TUESDAY:
                mainBackGround.setImageResource(R.drawable.music_word_background);
                break;
            case Calendar.WEDNESDAY:
                mainBackGround.setImageResource(R.drawable.feeling_girl_background);
                break;
            case Calendar.THURSDAY:
                mainBackGround.setImageResource(R.drawable.guitar_background);
                break;
            case Calendar.FRIDAY:
                mainBackGround.setImageResource(R.drawable.headphone_background);
                break;
            case Calendar.SATURDAY:
                mainBackGround.setImageResource(R.drawable.song_background);
                break;
            case Calendar.SUNDAY:
                mainBackGround.setImageResource(R.drawable.music_background);
                break;
        }
    }
}
