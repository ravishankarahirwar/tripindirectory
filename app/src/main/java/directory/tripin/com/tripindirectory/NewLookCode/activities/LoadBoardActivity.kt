package directory.tripin.com.tripindirectory.NewLookCode.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import com.google.firebase.FirebaseApp
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.forum.fragment.MyPostsFragment
import directory.tripin.com.tripindirectory.forum.fragment.MyTopPostsFragment
import directory.tripin.com.tripindirectory.forum.fragment.RecentPostsFragment
import directory.tripin.com.tripindirectory.helper.Logger
import kotlinx.android.synthetic.main.content_main_loadboard.*

class LoadBoardActivity : AppCompatActivity() {
    private var mPagerAdapter: FragmentPagerAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_board2)

        FirebaseApp.initializeApp(applicationContext)

        // Create the adapter that will return a fragment for each section
        mPagerAdapter = object : FragmentPagerAdapter(supportFragmentManager) {
            private val mFragments = arrayOf<Fragment>(RecentPostsFragment())
            override fun getItem(position: Int): Fragment {
                return mFragments[position]
            }

            override fun getCount(): Int {
                return mFragments.size
            }
        }
        mViewPager.adapter = mPagerAdapter
        Logger.v("LoadBoardActivity")
    }
}
