package directory.tripin.com.tripindirectory.FormActivities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import directory.tripin.com.tripindirectory.FormActivities.FormFragments.CompanyFromFragment;
import directory.tripin.com.tripindirectory.FormActivities.FormFragments.FleetFormFragment;
import directory.tripin.com.tripindirectory.FormActivities.FormFragments.ImagesFormFragment;
import directory.tripin.com.tripindirectory.FormActivities.FormFragments.RouteFormFragment;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;

public class CompanyInfoActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;
    private PartnerInfoPojo partnerInfoPojo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle("Updated 10sec ago");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        partnerInfoPojo = new PartnerInfoPojo();


        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
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
