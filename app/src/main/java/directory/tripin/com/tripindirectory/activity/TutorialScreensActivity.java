package directory.tripin.com.tripindirectory.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

import directory.tripin.com.tripindirectory.R;


/**
 * Created by Yogesh N. Tikam on 10/25/2017.
 */

public class TutorialScreensActivity extends AppIntro2 {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Grow Your Business", "", "Add your business in INL so other can search you in the world", "", R.drawable.agreement,
                ContextCompat.getColor(this, R.color.colorPrimary),
                Color.WHITE,
                Color.WHITE));
        addSlide(AppIntroFragment.newInstance("Search", "", "Search other by Route, Company Name and City", "", R.drawable.ic_search_tutorial,
                ContextCompat.getColor(this, R.color.colorPrimary),
                Color.WHITE,
                Color.WHITE));
        addSlide(AppIntroFragment.newInstance("Just Speak", "", "No need to type just speak we will show you what you want", "", R.drawable.ic_record_voice_over_black_24dp,
                ContextCompat.getColor(this, R.color.colorPrimary),
                Color.WHITE,
                Color.WHITE));
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }

    @Override
    public void onBackPressed() {
    }
}
