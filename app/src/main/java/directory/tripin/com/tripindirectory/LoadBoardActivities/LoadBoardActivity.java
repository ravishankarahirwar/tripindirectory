package directory.tripin.com.tripindirectory.LoadBoardActivities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.ArrayList;
import java.util.List;

import directory.tripin.com.tripindirectory.FormActivities.CompanyInfoActivity;
import directory.tripin.com.tripindirectory.FormActivities.FormFragments.CompanyFromFragment;
import directory.tripin.com.tripindirectory.FormActivities.FormFragments.FleetFormFragment;
import directory.tripin.com.tripindirectory.FormActivities.FormFragments.ImagesFormFragment;
import directory.tripin.com.tripindirectory.FormActivities.FormFragments.RouteFormFragment;
import directory.tripin.com.tripindirectory.LoadBoardActivities.fragments.FleetsFragment;
import directory.tripin.com.tripindirectory.LoadBoardActivities.fragments.IntrestedInFragment;
import directory.tripin.com.tripindirectory.LoadBoardActivities.fragments.LoadsFragment;
import directory.tripin.com.tripindirectory.LoadBoardActivities.fragments.MyPostsFragment;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.activity.BaseActivity;
import directory.tripin.com.tripindirectory.manager.PreferenceManager;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;

public class LoadBoardActivity extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;
    private CoordinatorLayout coordinatorLayout;
    private Context mContext;
    private ShowcaseView showcaseView;
    private int mTutCounter = 0;
    private Activity mActivity;
    private TextView tabOne;
    private TextView tabTwo;
    private TextView tabThree;
    private TextView tabFour;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_board);
        mActivity = this;
        mContext = LoadBoardActivity.this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        coordinatorLayout = findViewById(R.id.main_content);
        setSupportActionBar(toolbar);
        //toolbar.setSubtitle("501 Active Posts");
        toolbar.setTitle("LoadBoard");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FirebaseMessaging.getInstance().subscribeToTopic("NewFleetPost");
        FirebaseMessaging.getInstance().subscribeToTopic("NewLoadPost");

        ViewPager viewPager = findViewById(R.id.container);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        createViewPager(viewPager);
        viewPager.setAdapter(adapter);
        createTabIcons();

        com.github.clans.fab.FloatingActionButton fabFleet = findViewById(R.id.menu_fleet);
        fabFleet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        com.github.clans.fab.FloatingActionButton fabLoad = findViewById(R.id.menu_load);
        fabLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(LoadBoardActivity.this,PostLoadActivity.class));
            }
        });

        if(getIntent().getExtras()!=null){
            if(getIntent().getExtras().getString("frag")!=null){
                if(getIntent().getExtras().getString("frag").equals("3")){
                    viewPager.setCurrentItem(3);
                }
            }

        }

        showUserTutorial();
    }

    private void showUserTutorial() {
        int margin = ((Number) (getResources().getDisplayMetrics().density * 16)).intValue();

        final RelativeLayout.LayoutParams lpsBottomLeft = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpsBottomLeft.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lpsBottomLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lpsBottomLeft.setMargins(margin, margin, margin, margin);

        final RelativeLayout.LayoutParams lpsBottomRight = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpsBottomRight.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lpsBottomRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lpsBottomRight.setMargins(margin, margin, margin, margin);

        RelativeLayout.LayoutParams lpsTopRight = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpsTopRight.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lpsTopRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lpsTopRight.setMargins(margin, margin, margin, margin);

        final RelativeLayout.LayoutParams lpsTopLeft = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpsTopLeft.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lpsTopLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lpsTopLeft.setMargins(margin, margin, margin, margin);

        ViewTarget target = new ViewTarget(tabOne.getId(), this);
        showcaseView = new ShowcaseView.Builder(this)
                .setStyle(R.style.CustomShowcaseTheme3)
                .setTarget(target)
                .setContentTitle("Loads Tab")
                .singleShot(43)
                .setContentText("See the list of recently posted loads. you can quote, mark interested, share, inbox, comment or call.")
                .hideOnTouchOutside()
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        switch (mTutCounter){
                            case 0:{
                                showcaseView.setShowcase(new ViewTarget(tabTwo.getId(), mActivity), true);
                                showcaseView .setContentTitle("Trucks Tab");
                                showcaseView.setContentText("List of recently posted fleets. tap on see all for filtered search");

                                break;
                            }
                            case 1:{
                                showcaseView.setShowcase(new ViewTarget(tabThree.getId(),mActivity), true);
                                showcaseView .setContentTitle("Interested In");
                                showcaseView.setContentText("List of Loads and Fleets you marked interested");
                                break;
                            }
                            case 2:{
                                showcaseView.setShowcase(new ViewTarget(tabFour.getId(), mActivity), true);
                                showcaseView .setContentTitle("Your Posts");
                                showcaseView.setContentText("Manage you posts, see response and analytics.");
                                break;
                            }

                            case 3:{

                                showcaseView.setShowcase(new ViewTarget(R.id.action_addpost, mActivity), true);
                                showcaseView .setContentTitle("Add Post");
                                showcaseView.setContentText("Add new Load or Truck, Relevant transports will get notified");
                                break;
                            }
                            case 4:{
                                showcaseView.setShowcase(new ViewTarget(R.id.action_map, mActivity), true);
                                showcaseView .setContentTitle("Loadboard Mapview");
                                showcaseView.setButtonText("Done");
                                showcaseView.setContentText("See the routes of loads to be transported to get an overall feel of the market");
                                break;
                            }

                            case 5:{
                                showcaseView.hide();
                                break;
                            }

                        }
                        mTutCounter++;

                    }
                })
                .build();
        showcaseView.setButtonPosition(lpsBottomRight);

    }


    @Override
    protected void init() {

    }

    @Override
    protected void viewSetup() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_load_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_addpost) {
            final CharSequence[] items = {
                    "Post Load", "Post Fleet", "Cancel"
            };

            final AlertDialog alert;
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Make your selection");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                 switch (item){
                     case 0:{
                         startActivity(new Intent(LoadBoardActivity.this, PostLoadActivity.class));
                         break;
                     }
                     case 1:{
                         startActivity(new Intent(LoadBoardActivity.this, PostFleetActivity.class));

                         break;
                     }
                     case 2:{
                         break;
                     }

                 }
                }
            });
            alert = builder.create();
            alert.show();
            return true;
        }else if(id == R.id.action_map){
            startActivity(new Intent(LoadBoardActivity.this,LoadBoardMapViewActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void createTabIcons() {

        tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.customtab, null);
        tabOne.setText("Loads");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_widgets_black_24dp, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.customtab, null);
        tabTwo.setText("Trucks");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.delivery_truck_white, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);



        tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.customtab, null);
        tabThree.setText("Intrested");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favorite_white_24dp, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.customtab, null);
        tabFour.setText("My Posts");
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_assignment_ind_black_24dp, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFour);
    }

    private void createViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new LoadsFragment(), "Tab Loads");
        adapter.addFrag(new FleetsFragment(), "Tab Trucks");
        adapter.addFrag(new IntrestedInFragment(), "Tab Intrested In");
        adapter.addFrag(new MyPostsFragment(), "Tab My Posts");
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
