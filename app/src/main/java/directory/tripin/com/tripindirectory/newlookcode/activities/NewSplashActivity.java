package directory.tripin.com.tripindirectory.newlookcode.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.keiferstone.nonet.NoNet;

import java.util.Date;

import directory.tripin.com.tripindirectory.chatingactivities.models.UserPresensePojo;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.newlookcode.MainNewIntroActivity;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.activity.MainActivity;
import directory.tripin.com.tripindirectory.manager.PreferenceManager;

public class NewSplashActivity extends AppCompatActivity {

    private static int SPLASH_SHOW_TIME = 2000;
    private PreferenceManager preferenceManager;
    private FirebaseAnalytics firebaseAnalytics;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_splash);
        preferenceManager = PreferenceManager.getInstance(this);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){

            if(preferenceManager.getComapanyName()!=null){
                UserPresensePojo userPresensePojo = new UserPresensePojo(true, new Date().getTime(), "");
                FirebaseDatabase.getInstance().getReference()
                        .child("chatpresence")
                        .child("users")
                        .child(firebaseAuth.getUid())
                        .setValue(userPresensePojo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Logger.v("onResume userpresence updated1");
                                UserPresensePojo userPresensePojo2 = new UserPresensePojo(false, new Date().getTime(), "");
                                FirebaseDatabase.getInstance().getReference()
                                        .child("chatpresence")
                                        .child("users")
                                        .child(firebaseAuth.getUid())
                                        .onDisconnect()
                                        .setValue(userPresensePojo2)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Logger.v("onResume userpresence updated");
                                                timer();
                                            }
                                        });
                            }
                        });
            }else {
                timer();
            }

        }else {
            timer();
        }



        internetCheck();
    }

    private void timer() {
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
