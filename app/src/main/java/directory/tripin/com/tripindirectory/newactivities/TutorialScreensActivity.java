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

        addSlide(AppIntroFragment.newInstance("100+ Category", "", "Groups by category", "", R.drawable.ic_arrow_forward_white,
                ContextCompat.getColor(this, R.color.colorPrimary),
                Color.WHITE,
                Color.WHITE));
        addSlide(AppIntroFragment.newInstance("Join Group", "", "Join as many as group you want in one click", "", R.drawable.ic_arrow_downward,
                ContextCompat.getColor(this, R.color.colorPrimary),
                Color.WHITE,
                Color.WHITE));
        addSlide(AppIntroFragment.newInstance("Add Group", "", "You can add your group so other can join", "", R.drawable.ic_arrow_upward,
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
