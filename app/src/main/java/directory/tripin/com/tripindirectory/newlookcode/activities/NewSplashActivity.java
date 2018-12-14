package directory.tripin.com.tripindirectory.newlookcode.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.keiferstone.nonet.NoNet;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.Date;

import directory.tripin.com.tripindirectory.chatingactivities.models.UserPresensePojo;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.manager.PreferenceManager;
import directory.tripin.com.tripindirectory.newlookcode.utils.MixPanelConstants;
import directory.tripin.com.tripindirectory.newprofiles.activities.NewLandingNavActivity;

public class NewSplashActivity extends AppCompatActivity {

    private static int SPLASH_SHOW_TIME = 2000;
    private PreferenceManager preferenceManager;
    private FirebaseAnalytics firebaseAnalytics;
    private FirebaseAuth firebaseAuth;
    private MixpanelAPI mixpanelAPI;
    private TextView splashInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_splash);
        splashInfo = findViewById(R.id.splashinfo);
        preferenceManager = PreferenceManager.getInstance(this);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseAuth = FirebaseAuth.getInstance();
        mixpanelAPI = MixpanelAPI.getInstance(this,MixPanelConstants.MIXPANEL_TOKEN);

        if (firebaseAuth.getCurrentUser() != null) {

            splashInfo.setText(R.string.updating_presence);
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
                            splashInfo.setText(R.string.syncing_data);
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
                                    }).addOnCanceledListener(new OnCanceledListener() {
                                @Override
                                public void onCanceled() {
                                    timer();
                                }
                            });
                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    timer();
                }
            });


        } else {
            timer();
        }


        internetCheck();
    }

    private void timer() {
        splashInfo.setText(R.string.setting_up);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                String to = "";
                if (!preferenceManager.isMainIntroSeen()) {
                    startIntroActivity();
                } else {
                    splashInfo.setText(R.string.launching_iln);

                    startMainNewActivity();

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
        //mixpanel identify
        if(preferenceManager.getRMN()!=null){
            mixpanelAPI.identify(preferenceManager.getRMN());
            mixpanelAPI.getPeople().identify(preferenceManager.getRMN());
            mixpanelAPI.getPeople().set("RMN",preferenceManager.getRMN());
            if(preferenceManager.getDisplayName()!=null){
                mixpanelAPI.getPeople().set("UserName",preferenceManager.getDisplayName());

            }
        }

        Intent i = new Intent(NewSplashActivity.this, NewLandingNavActivity.class);
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
