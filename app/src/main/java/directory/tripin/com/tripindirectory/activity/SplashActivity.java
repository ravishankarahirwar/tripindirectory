package directory.tripin.com.tripindirectory.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.newactivities.MainActivity1;


/**
 * @author Ravishankar Ahirwar
 * @version v3.0
 * @since 20/01/2017 modified 23/05/2017
 * <p>
 * This is the first class is appear in front of user we just show the Tripin-Shipper icon
 * for 1 second then if user already login Start HomeActivity otherwise start MainActivity
 */
public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private static int SPLASH_SHOW_TIME = 1000;
    private static final int RC_SIGN_IN = 123;

    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               startMainActivity();
            }
        }, SPLASH_SHOW_TIME);
    }

    /**
     * If user not login/first time login this screen will appear
     */
    private void startMainActivity() {
        Intent i = new Intent(SplashActivity.this, MainActivity1.class);
        startActivity(i);
        finish();
    }



}
