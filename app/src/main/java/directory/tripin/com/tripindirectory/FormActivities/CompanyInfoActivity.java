package directory.tripin.com.tripindirectory.FormActivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.keiferstone.nonet.NoNet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import directory.tripin.com.tripindirectory.FormActivities.FormFragments.CompanyFromFragment;
import directory.tripin.com.tripindirectory.FormActivities.FormFragments.FleetFormFragment;
import directory.tripin.com.tripindirectory.FormActivities.FormFragments.ImagesFormFragment;
import directory.tripin.com.tripindirectory.FormActivities.FormFragments.RouteFormFragment;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.activity.ProfileHelpActivity;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.manager.PreferenceManager;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;

import static com.facebook.FacebookSdk.getApplicationContext;

public class CompanyInfoActivity extends AppCompatActivity {

    private ProgressBar myProgressBar;
    private TextView mProgressText;
    private TextView mAccountStatusText;
    private LottieAnimationView mSyncAnimation;

    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;
    private PartnerInfoPojo partnerInfoPojo;
    private DocumentReference mUserDocRef;
    CoordinatorLayout coordinatorLayout;
    private PreferenceManager mPreferenceManager;
    private Context mContext;
    ViewPager viewPager;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NoNet.monitor(this)
                .poll()
                .snackbar();
        setContentView(R.layout.activity_main_form);
        mContext = CompanyInfoActivity.this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        coordinatorLayout = findViewById(R.id.main_content);
        myProgressBar = findViewById(R.id.progressBar2);
        mProgressText = findViewById(R.id.textViewProgress);
        mAccountStatusText = findViewById(R.id.textViewStatus);
        mSyncAnimation = findViewById(R.id.animation_view);


        setSupportActionBar(toolbar);
        //toolbar.setSubtitle("Unverified, Updated 10sec ago");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        partnerInfoPojo = new PartnerInfoPojo();


        viewPager = findViewById(R.id.container);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        createViewPager(viewPager);
        viewPager.setAdapter(adapter);
        createTabIcons();

        mPreferenceManager = PreferenceManager.getInstance(mContext);

        if(mPreferenceManager.isAutoSyncGot){
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Your data is saved automatically", Snackbar.LENGTH_LONG)
                    .setActionTextColor(ContextCompat.getColor(getApplicationContext(),R.color.primaryColor))
                    .setAction("GOT IT!", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                        Snackbar snackbar1 = Snackbar.make(coordinatorLayout, "Message is restored!", Snackbar.LENGTH_SHORT);
//                        snackbar1.show();
                            mPreferenceManager.setIsAutoSyncGot(true);
                        }
                    });

            snackbar.show();
        }

        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            FirebaseFirestore.getInstance()
                    .collection("partners")
                    .document(FirebaseAuth.getInstance().getUid())
                    .addSnapshotListener(CompanyInfoActivity.this,new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                    if(documentSnapshot.exists()){
                        partnerInfoPojo = documentSnapshot.toObject(PartnerInfoPojo.class);
                        mSyncAnimation.resumeAnimation();
                        int p = getProgressFromObject(partnerInfoPojo);
                        mProgressText.setText(p+"% Profile Complete...");
                        myProgressBar.setProgress(p);
                        mAccountStatusText.setText(R.string.unverified);
                        if(partnerInfoPojo.getmAccountStatus()>=2){
                            mAccountStatusText.setText(R.string.verified);
                        }else {
                            if(partnerInfoPojo.getmAccountStatus()==1){
                                mAccountStatusText.setText("Pending");

                            }
                        }
                    }else {
                        mProgressText.setText("0% Profile Complete...");
                        mAccountStatusText.setText(R.string.unverified);
                    }

                }
            });
        }

        mAccountStatusText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(3);
            }
        });




    }

    private int getProgressFromObject(PartnerInfoPojo partnerInfoPojo) {
        int progress = 0;

        //30 percent for Company Tab

        if(partnerInfoPojo.getmCompanyName()!=null){
            if(!partnerInfoPojo.getmCompanyName().isEmpty()){
                progress = progress +3;
            }
        }

        if(partnerInfoPojo.getmContactPersonsList() !=null){

            if(partnerInfoPojo.getmContactPersonsList().size()==1){
                if(!partnerInfoPojo.getmContactPersonsList().get(0).getGetmContactPersonMobile().isEmpty())
                    progress = progress + 3;
            }
            if(partnerInfoPojo.getmContactPersonsList().size()>=2){
                int n = 0;
                for(int i=0; i<partnerInfoPojo.getmContactPersonsList().size();i++){
                    if(!partnerInfoPojo.getmContactPersonsList().get(i).getGetmContactPersonMobile().isEmpty())
                        n++;
                }
                if(n>2){
                    progress = progress + 3*2;
                }else {
                    progress = progress + 3*n;
                }
            }
        }

        if(partnerInfoPojo.getmCompanyLandLineNumbers()!=null){
            if(partnerInfoPojo.getmCompanyLandLineNumbers().size()>0){
                if(!partnerInfoPojo.getmCompanyLandLineNumbers().get(0).isEmpty()){
                    progress = progress + 3;
                }
            }

        }


        if(partnerInfoPojo.getmCompanyEmail()!=null){
            if(!partnerInfoPojo.getmCompanyEmail().isEmpty()){
                progress = progress + 3;
            }
        }


        if(partnerInfoPojo.getmCompanyWebsite()!=null){
            if(!partnerInfoPojo.getmCompanyWebsite().isEmpty()){
                progress = progress + 3;
            }
        }


        if(partnerInfoPojo.getmCompanyAdderss()!=null){
            if(!partnerInfoPojo.getmCompanyAdderss().getAddress().isEmpty()){
                progress = progress + 3;
            }

            if(partnerInfoPojo.getmCompanyAdderss().isLatLongSet()){
                progress = progress + 3;
            }

        }


        if(partnerInfoPojo.getmTypesOfServices()!=null){
            for(Boolean b :partnerInfoPojo.getmTypesOfServices().values()){
                if(b){
                    progress = progress + 3;
                    break;
                }
            }
        }


        if(partnerInfoPojo.getmNatureOfBusiness()!=null){
            for(Boolean b :partnerInfoPojo.getmNatureOfBusiness().values()){
                if(b){
                    progress = progress + 3;
                    break;
                }
            }
        }


        //20 percent for route TAB
        if(partnerInfoPojo.getmSourceCities()!=null){
            if(partnerInfoPojo.getmSourceCities().size()>0)
            progress = progress + 10;
        }
        if(partnerInfoPojo.getmDestinationCities()!=null){
            if(partnerInfoPojo.getmDestinationCities().size()>0)
                progress = progress + 10;
        }

        //20 percent for FleetTab
        if(partnerInfoPojo.getVehicles()!=null){
            if(partnerInfoPojo.getVehicles().size()>0){
                if(!partnerInfoPojo.getVehicles().get(0).getNumber().isEmpty()){
                    progress = progress + 10;
                }
                if(!partnerInfoPojo.getVehicles().get(0).getDriver().getNumber().isEmpty()){
                    progress = progress + 10;
                }
            }

        }

        //30 percent for images
        if(partnerInfoPojo.getmImagesUrl()!=null){
            for(int i=0; i<partnerInfoPojo.getmImagesUrl().size();i++){
                if(!partnerInfoPojo.getmImagesUrl().get(i).isEmpty()){
                    progress = progress + 10;
                }
            }
        }


        return progress;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_help) {
            startActivity(new Intent(CompanyInfoActivity.this, ProfileHelpActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void createTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.customtab, null);
        tabOne.setText("Company");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.office, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.customtab, null);
        tabTwo.setText("Route");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.route, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);


        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.customtab, null);
        tabThree.setText("Fleets");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.fleet, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.customtab, null);
        tabFour.setText("Image");
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.picture, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFour);
    }

    private void createViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CompanyFromFragment(), "Tab Comp");
        adapter.addFrag(new RouteFormFragment(), "Tab Route");
        adapter.addFrag(new FleetFormFragment(), "Tab Fleet");
        adapter.addFrag(new ImagesFormFragment(), "Tab Images");
    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }



}
