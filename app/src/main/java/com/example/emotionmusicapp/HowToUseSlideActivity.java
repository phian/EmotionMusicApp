package com.example.emotionmusicapp;

import android.content.Intent;
import android.os.Bundle;

import com.cuneytayyildiz.onboarder.OnboarderActivity;
import com.cuneytayyildiz.onboarder.OnboarderPage;
import com.cuneytayyildiz.onboarder.utils.OnboarderPageChangeListener;

import java.util.ArrayList;
import java.util.List;

public class HowToUseSlideActivity extends OnboarderActivity implements OnboarderPageChangeListener {

    private int[] slideImgId = {
            R.drawable.internet_connect_img,
            R.drawable.main_screen_img,
            R.drawable.choose_emotion_img,
            R.drawable.music_wave_img,
            R.drawable.song_list_img
    };

    private int[] slideContentId = {
            R.string.permission_detail,
            R.string.permission_detail1,
            R.string.permission_detail2,
            R.string.permission_detail3,
            R.string.permission_detail3
    };

    private List<OnboarderPage> slidePages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_how_to_use_slide);

        onCreateSlideContent();
    }

    // method to create slide content for how to use activity
    public void onCreateSlideContent() {
        for (int i = 0; i < slideImgId.length; i++) {
            slidePages.add(new OnboarderPage.Builder()
                    .title("How to use app")
                    .description(String.valueOf(slideContentId[i]))
                    .imageResourceId(slideImgId[i])
                    .backgroundColorId(R.color.howToUseScreenBackground)
                    .titleColorId(R.color.lightModeThemeColor)
                    .descriptionColorId(R.color.switchStateColor)
                    .multilineDescriptionCentered(true)
                    .build()
            );
        }

        initOnboardingPages(slidePages);
        setOnboarderPageChangeListener(this);
    }

    @Override
    public void finish() {
        super.finish();

        Intent startMainActivity = new Intent(HowToUseSlideActivity.this, MainActivity.class);
        startMainActivity.putExtra("stopActivity", "stopActivity");
        startActivity(startMainActivity);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onPageChanged(int position) {

    }

    @Override
    public void onFinishButtonPressed() {
        finish();
    }
}
