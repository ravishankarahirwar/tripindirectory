package directory.tripin.com.tripindirectory.NewLookCode.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.activity.MainActivity;
import directory.tripin.com.tripindirectory.activity.SplashActivity;
import directory.tripin.com.tripindirectory.manager.PreferenceManager;

public class NewSplashActivity extends AppCompatActivity {

    private static int SPLASH_SHOW_TIME = 1000;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_splash);
        preferenceManager = PreferenceManager.getInstance(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(preferenceManager.isNewLookAccepted()){
                    startMainNewActivity();
                }else {
                    if(preferenceManager.isOnNewLook()){
                        startMainNewActivity();
                    }else {
                        startMainActivity();
                    }
                }
            }
        }, SPLASH_SHOW_TIME);
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
}
