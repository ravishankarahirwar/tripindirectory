package directory.tripin.com.tripindirectory.newactivities;

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

        addSlide(AppIntroFragment.newInstance("Search", "", "Best search result for your enquiry", "", R.drawable.ic_search,
                ContextCompat.getColor(this, R.color.colorPrimary),
                Color.WHITE,
                Color.WHITE));
        addSlide(AppIntroFragment.newInstance("Up Vote", "", "Give an up-vote if you like ", "", R.drawable.ic_arrow_upward,
                ContextCompat.getColor(this, R.color.colorPrimary),
                Color.WHITE,
                Color.WHITE));
        addSlide(AppIntroFragment.newInstance("Down Vote", "", "Give an down-vote if you not like", "", R.drawable.ic_arrow_downward,
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
