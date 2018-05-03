package directory.tripin.com.tripindirectory.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.manager.PreferenceManager;


/**
 * @author Ravishankar Ahirwar
 * @version v3.0
 * @since 20/01/2017 modified 23/05/2017
 * <p>
 * This is the first class is appear in front of user we just show the Tripin-Shipper icon
 * for 1 second then if user already login Start HomeActivity otherwise start CompanyInfoActivity
 */
public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private static int SPLASH_SHOW_TIME = 1000;
    private static final int RC_SIGN_IN = 123;
    private PreferenceManager mPreferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mPreferenceManager = PreferenceManager.getInstance(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showAppIntro();

            }
        }, SPLASH_SHOW_TIME);
    }

    /**
     * If user not login/first time login this screen will appear
     */
    private void startMainActivity() {
        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * This method show app intro screen if user coming first time in the app
     */
    private void showAppIntro() {
        if (mPreferenceManager.isFirstTime()) {
            mPreferenceManager.setFirstTime(false);
            startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
            finish();
        } else {
            startMainActivity();
        }
    }

}
