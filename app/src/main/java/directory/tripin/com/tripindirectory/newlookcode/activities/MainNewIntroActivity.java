package directory.tripin.com.tripindirectory.newlookcode.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.helper.IntroScreensHelperActivity;
import directory.tripin.com.tripindirectory.manager.PreferenceManager;
import directory.tripin.com.tripindirectory.newprofiles.activities.NewLandingNavActivity;

public class MainNewIntroActivity extends IntroScreensHelperActivity {

    private PreferenceManager preferenceManager;
    private FirebaseAnalytics firebaseAnalytics;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = PreferenceManager.getInstance(this);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);


        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard(getString(R.string.transport_directory), getString(R.string.directory_discription), R.drawable.route);
        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard(getString(R.string.load_board), getString(R.string.loadboard_discription), R.drawable.ic_developer_board_black_24dp);
        AhoyOnboarderCard ahoyOnboarderCard3 = new AhoyOnboarderCard(getString(R.string.chat), getString(R.string.chat_discription), R.drawable.ic_chat_bubble_outline_black_24dp);

        ahoyOnboarderCard1.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard2.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard3.setBackgroundColor(R.color.black_transparent);

        List<AhoyOnboarderCard> pages = new ArrayList<>();

        pages.add(ahoyOnboarderCard1);
        pages.add(ahoyOnboarderCard2);
        pages.add(ahoyOnboarderCard3);

        for (AhoyOnboarderCard page : pages) {
            page.setTitleColor(R.color.white);
            page.setDescriptionColor(R.color.grey_200);
            //page.setTitleTextSize(dpToPixels(12, this));
            //page.setDescriptionTextSize(dpToPixels(8, this));
            //page.setIconLayoutParams(width, height, marginTop, marginLeft, marginRight, marginBottom);
        }

        setFinishButtonTitle(R.string.get_started);
        showNavigationControls(true);
        setGradientBackground();

//        List<Integer> colorList = new ArrayList<>();
//        colorList.add(R.color.solid_one);
//        colorList.add(R.color.solid_two);
//        colorList.add(R.color.solid_three);
//
//        setColorBackground(colorList);

        //set the button style you created
        setFinishButtonDrawableStyle(ContextCompat.getDrawable(this, R.drawable.rounded_button));

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/roboto.light.ttf");
        setFont(face);

        setOnboardPages(pages);
    }

    @Override
    public void onFinishButtonPressed() {

        Bundle bun = new Bundle();
        firebaseAnalytics.logEvent("z_mainIntro_finished", bun);

        preferenceManager.setisMainIntroSeen(true);

        startMainNewActivity();

    }

    private void startMainNewActivity() {
        Intent i = new Intent(MainNewIntroActivity.this, NewLandingNavActivity.class);
        startActivity(i);
        finishAffinity();
    }




}
