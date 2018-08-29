package directory.tripin.com.tripindirectory.NewLookCode;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.keiferstone.nonet.NoNet;

import java.util.ArrayList;
import java.util.List;

import directory.tripin.com.tripindirectory.NewLookCode.activities.MainScrollingActivity;
import directory.tripin.com.tripindirectory.NewLookCode.activities.NewSplashActivity;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.activity.MainActivity;
import directory.tripin.com.tripindirectory.manager.PreferenceManager;

public class MainNewIntroActivity extends AhoyOnboarderActivity {

    private PreferenceManager preferenceManager;
    private FirebaseAnalytics firebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = PreferenceManager.getInstance(this);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);


        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard("Transport Directory", "Select route and get list of ILN trusted transporters.", R.drawable.route);
        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard("LoadBoard", "Post your requirements on Loadboard and get responces from interested people.", R.drawable.ic_developer_board_black_24dp);
        AhoyOnboarderCard ahoyOnboarderCard3 = new AhoyOnboarderCard("Chat", "Connect with transporters and proceed the further transaction on chat.", R.drawable.ic_chat_bubble_outline_black_24dp);

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

        setFinishButtonTitle("Get Started");
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

        Bundle bundle = new Bundle();
        String to = "";
        if (preferenceManager.isNewLookAccepted()) {
            startMainNewActivity();
            to = "New";
        } else {
            if (preferenceManager.isOnNewLook()) {
                startMainNewActivity();
                to = "New";
            } else {
                startMainActivity();
                to = "Old";
            }
        }
        bundle.putString("to", to);
        firebaseAnalytics.logEvent("z_from_splash", bundle);
    }

    private void startMainNewActivity() {
        Intent i = new Intent(MainNewIntroActivity.this, MainScrollingActivity.class);
        startActivity(i);
        finishAffinity();
    }

    private void startMainActivity() {
        Intent i = new Intent(MainNewIntroActivity.this, MainActivity.class);
        startActivity(i);
        finishAffinity();
    }


}
