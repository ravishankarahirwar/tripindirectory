package directory.tripin.com.tripindirectory.FormActivities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import directory.tripin.com.tripindirectory.FormActivities.FormFragments.BaseFragment;
import directory.tripin.com.tripindirectory.FormActivities.FormFragments.CompanyFromFragment;
import directory.tripin.com.tripindirectory.FormActivities.FormFragments.FleetFormFragment;
import directory.tripin.com.tripindirectory.FormActivities.FormFragments.ImagesFormFragment;
import directory.tripin.com.tripindirectory.FormActivities.FormFragments.RouteFormFragment;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.activity.Main2Activity;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;

public class CompanyInfoActivity extends AppCompatActivity {

    public static final String TAG = "Company Info Activity";
    private ViewPager mViewPager;
    TabLayout tabLayout;
    ViewPagerAdapter adapter;
    FirebaseAuth auth;
    DocumentReference mUserDocRef;
    Fragment fragment;
    private PartnerInfoPojo partnerInfoPojo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle("Last Update : 10sec ago");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        partnerInfoPojo = new PartnerInfoPojo();


        ViewPager viewPager = (ViewPager) findViewById(R.id.container);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        auth = FirebaseAuth.getInstance();
        mUserDocRef = FirebaseFirestore.getInstance()
                .collection("partners").document(auth.getUid());
        createViewPager(viewPager);
        viewPager.setAdapter(adapter);
        createTabIcons();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.customtab, null);
        tabOne.setText("Company");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_contact_phone_black_24dp, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.customtab, null);
        tabTwo.setText("Route");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_directions_black_24dp, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);


        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.customtab, null);
        tabThree.setText("Fleet");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_rv_hookup_black_24dp, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.customtab, null);
        tabFour.setText("Image");
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_image_black_24dp, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFour);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void createViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CompanyFromFragment(), "Tab Comp");
        adapter.addFrag(new RouteFormFragment(), "Tab Route");
        adapter.addFrag(new FleetFormFragment(), "Tab Fleet");
        adapter.addFrag(new ImagesFormFragment(), "Tab Images");


        fragment = adapter.getItem(0);
        mUserDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    partnerInfoPojo = task.getResult().toObject(PartnerInfoPojo.class);
                    ((BaseFragment)fragment)
                            .onUpdate(partnerInfoPojo);
                }

            }
        });


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



    @Override
    public void onBackPressed() {
    }
}
