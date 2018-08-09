package directory.tripin.com.tripindirectory.NewLookCode;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import directory.tripin.com.tripindirectory.NewLookCode.pojos.UserProfile;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.manager.PreferenceManager;

public class FacebookRequiredActivity extends AppCompatActivity {

    private static final int FB_SIGN_IN = 100;
    private static final int PHONE_SIGN_IN = 101;
    CardView loginwithfacebbok;
    CardView loginwithphone;


    private TextView mNameWelcome;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_required2);
        init();

        loginwithfacebbok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpbyFacebook();
            }
        });

        loginwithphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignInFor(PHONE_SIGN_IN);
            }
        });


    }

    private void init() {
        preferenceManager = PreferenceManager.getInstance(this);
        loginwithfacebbok = findViewById(R.id.facebbok);
        loginwithphone = findViewById(R.id.phone);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==FB_SIGN_IN){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Log.v("onActivityResult: ",user.getDisplayName()+" "+user.getPhoneNumber()+" "+user.getProviderId());
                    UserProfile userProfile = new UserProfile(user.getDisplayName(),
                            preferenceManager.getRMN(),
                            user.getEmail(),
                            user.getPhotoUrl().toString(),
                            user.getUid(),
                            preferenceManager.getFcmToken()
                            );
                    preferenceManager.setDisplayName(userProfile.getmDisplayName());
                    preferenceManager.setImageUrl(userProfile.getmImageUrl());
                    FirebaseDatabase.getInstance().getReference().child("user_profiles").child(userProfile.getmUid()).setValue(userProfile).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(),"Connected with Facebook",Toast.LENGTH_LONG).show();
                            FirebaseAuth.getInstance().signOut();
                            preferenceManager.setisFacebboked(true);
                            preferenceManager.setFuid(user.getUid());
                            startSignInFor(PHONE_SIGN_IN);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Failed to create, Try Again",Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    Toast.makeText(getApplicationContext(),"User Null, Try Again",Toast.LENGTH_LONG).show();
                }
            }
            if(requestCode == PHONE_SIGN_IN){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    if(user.getPhoneNumber()!=null){
                        preferenceManager.setRMN(user.getPhoneNumber());
                        preferenceManager.setUserId(user.getUid());
//                        Toast.makeText(getApplicationContext(),"RMN: "+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhoneNumber(),Toast.LENGTH_LONG).show();
                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(),"RMN Null! Try again",Toast.LENGTH_LONG).show();
                    }
                }

            }
        }else {
            Toast.makeText(getApplicationContext(),"Try again",Toast.LENGTH_LONG).show();
        }
    }

    private void startSignInFor(int signInFor) {
        startActivityForResult(
                // Get an instance of AuthUI based on the default app
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(
                                Collections.singletonList(
                                        new AuthUI.IdpConfig.PhoneBuilder().build()))
                        .build(),
                signInFor);
    }

    private void signUpbyFacebook() {

        AuthUI.IdpConfig facebookIdp = new AuthUI.IdpConfig.FacebookBuilder()
                .setPermissions(Arrays.asList("user_friends"))
                .build();
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(facebookIdp))
                        .build(),
                FB_SIGN_IN);
    }
}