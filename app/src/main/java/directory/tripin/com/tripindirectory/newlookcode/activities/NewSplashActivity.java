package directory.tripin.com.tripindirectory.newlookcode.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.keiferstone.nonet.NoNet;

import directory.tripin.com.tripindirectory.newlookcode.MainNewIntroActivity;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.activity.MainActivity;
import directory.tripin.com.tripindirectory.manager.PreferenceManager;

public class NewSplashActivity extends AppCompatActivity {

    private static int SPLASH_SHOW_TIME = 1000;
    private PreferenceManager preferenceManager;
    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_splash);
        preferenceManager = PreferenceManager.getInstance(this);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                String to = "";
                if (!preferenceManager.isMainIntroSeen()) {
                    startIntroActivity();
                } else {
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


            }
        }, SPLASH_SHOW_TIME);

        internetCheck();
    }

    private void startIntroActivity() {
        Intent i = new Intent(NewSplashActivity.this, MainNewIntroActivity.class);
        startActivity(i);
        finish();
    }

    private void startMainNewActivity() {
        Intent i = new Intent(NewSplashActivity.this, MainScrollingActivity.class);
        startActivity(i);
        finish();
    }

    private void startMainActivity() {
        Intent i = new Intent(NewSplashActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * This method is use for checking internet connectivity
     * If there is no internet it will show an snackbar to user
     */
    private void internetCheck() {
        NoNet.monitor(this)
                .poll()
                .snackbar();
    }
}
