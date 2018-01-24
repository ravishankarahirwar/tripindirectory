package directory.tripin.com.tripindirectory.forum;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.forum.fragment.MyPostsFragment;
import directory.tripin.com.tripindirectory.forum.fragment.MyTopPostsFragment;
import directory.tripin.com.tripindirectory.forum.fragment.RecentPostsFragment;


public class  MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private Toolbar toolbar;
    private DatabaseReference mPostReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_forum);

        setupToolbar();

        FirebaseApp.initializeApp(getApplicationContext());

        // Create the adapter that will return a fragment for each section
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[] {
                    new RecentPostsFragment(),
                    new MyPostsFragment(),
                    new MyTopPostsFragment(),
            };
            private final String[] mFragmentNames = new String[] {
                    getString(R.string.heading_recent),
                    getString(R.string.heading_my_posts),
                    getString(R.string.heading_my_top_posts)
            };
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }
            @Override
            public int getCount() {
                return mFragments.length;
            }
//            @Override
//            public CharSequence getPageTitle(int position) {
//                return mFragmentNames[position];
//            }
        };
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_forum_post);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_user_post);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_top_post);

        // Button launches NewPostActivity
        findViewById(R.id.fab_new_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                final FirebaseUser currentUser = getCurrentUser();
//                currentUser.reload();
//
//                if(currentUser != null) {
//
//                    View.OnClickListener mOnClickListener = new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            currentUser.sendEmailVerification()
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()) {
//                                                Toast.makeText(MainActivity.this,"Verification Email Send", Toast.LENGTH_LONG).show();
//                                            } else {
//                                                Toast.makeText(MainActivity.this,"Not able to send Verification Email : due to" + task.getException(), Toast.LENGTH_LONG).show();
//                                                return;
//                                            }
//                                        }
//                                    });
//                        }
//                    };
//
//                    Snackbar snackbar = Snackbar
//                            .make(view,  "Pleasy Verify Your email address", Snackbar.LENGTH_LONG)
//                            .setAction("Verify", mOnClickListener);
//                    snackbar.setActionTextColor(Color.GREEN);
//                    View snackbarView = snackbar.getView();
//                    snackbarView.setBackgroundColor(Color.DKGRAY);
//                    snackbar.show();
//
//                } else {
                    startActivity(new Intent(MainActivity.this, NewPostActivity.class));
//                }

            }
        });


    }


    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

}
