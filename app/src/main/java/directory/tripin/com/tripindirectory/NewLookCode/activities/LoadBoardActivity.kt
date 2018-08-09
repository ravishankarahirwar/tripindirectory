package directory.tripin.com.tripindirectory.NewLookCode.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentPagerAdapter
import com.google.firebase.FirebaseApp
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import directory.tripin.com.tripindirectory.NewLookCode.activities.fragments.loadboard.RecentLoadsFragment
import directory.tripin.com.tripindirectory.NewLookCode.activities.fragments.loadboard.YourLoadsFragment
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.helper.Logger
import kotlinx.android.synthetic.main.content_main_loadboard.*
import kotlinx.android.synthetic.main.layout_loadboard_actionbar.*
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.util.Log
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crash.FirebaseCrash
import com.google.firebase.database.*
import com.jaredrummler.materialspinner.MaterialSpinner
import directory.tripin.com.tripindirectory.NewLookCode.BasicQueryPojo
import directory.tripin.com.tripindirectory.NewLookCode.FacebookRequiredActivity
import directory.tripin.com.tripindirectory.NewLookCode.activities.fragments.loadboard.models.Post
import directory.tripin.com.tripindirectory.forum.MainActivity
import directory.tripin.com.tripindirectory.forum.models.User
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import kotlinx.android.synthetic.main.activity_load_board2.*
import kotlinx.android.synthetic.main.chat_list_item.view.*
import kotlinx.android.synthetic.main.item_loadpost_input.*
import kotlinx.android.synthetic.main.layout_route_input.*
import java.text.SimpleDateFormat
import java.util.*


class LoadBoardActivity : AppCompatActivity() {
    private var mPagerAdapter: FragmentPagerAdapter? = null

    private val POST_LOAD = 1
    private val POST_TRUCK = 2
    private var POST_TYPE = POST_LOAD

    internal var PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE = 3
    internal var PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION = 4

    lateinit var context: Context
    lateinit var postpojo : Post
    private val TAG = "LoadBoardActivity"
    private var mDatabase: DatabaseReference? = null
    var fabrotation = 0f
    private lateinit var preferenceManager: PreferenceManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_board2)
        context = this
        FirebaseApp.initializeApp(applicationContext)
        mDatabase = FirebaseDatabase.getInstance().reference
        preferenceManager = PreferenceManager.getInstance(this)

        if(FirebaseAuth.getInstance().currentUser==null
                || FirebaseAuth.getInstance().currentUser!!.phoneNumber==null
                || !preferenceManager.isFacebooked){
            val i = Intent(this, FacebookRequiredActivity::class.java)
            startActivity(i)
        }

        setFragmentsAdapter()
        setSpinners()
        setListners()
        setRoutePickup()
        postpojo = Post()

        val basicQueryPojo:BasicQueryPojo =  intent.extras.getSerializable("query") as BasicQueryPojo
        if(!basicQueryPojo.mSourceCity.isEmpty()){
            select_source.text = basicQueryPojo.mSourceCity
            postpojo.mSource = basicQueryPojo.mSourceCity
        }
        if(!basicQueryPojo.mDestinationCity.isEmpty()){
            select_destination.text = basicQueryPojo.mDestinationCity
            postpojo.mDestination = basicQueryPojo.mDestinationCity
        }



    }

    private fun setListners() {

        fab_revert.setOnClickListener {
            flipthefab()
        }

        showall.setOnClickListener {
            startAllLoadsActivity()
        }

        back.setOnClickListener {
            finish()
        }
        post.setOnClickListener {
            post.text = "..."
            if(postpojo.mSource==null||postpojo.mDestination==null){
                Toast.makeText(context,"Please Enter Route",Toast.LENGTH_LONG).show()
                post.text = "POST NOW"

            }else{
                if(weight.text != null){
                    //add unit
                    val list = spinnerweight.getItems<String>()
                    postpojo.mPayload = weight.text.toString()+list.get(spinnerweight.selectedIndex)

                }
                if(length.text != null){
                    //add unit
                    val list = spinnerlength.getItems<String>()
                    postpojo.mTruckLength = length.text.toString()+list.get(spinnerlength.selectedIndex)

                }
                if(otherreq.text != null){
                    postpojo.mRemark = otherreq.text.toString()
                }
                if(material.text != null){
                    postpojo.mMeterial = material.text.toString()
                }

                postpojo.setmFuid(getFuid())
                postpojo.setmUid(getUid())
                postpojo.setmContactNo(getUserPhoneNo())
                postpojo.setmDate(getDate())
                postpojo.setmPhotoUrl(preferenceManager.imageUrl)
                postpojo.setmAuthor(preferenceManager.displayName)
                postpojo.mFindOrPost = POST_TYPE

                Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show()

                // [START single_value_read]
                val userId = getUid()

                if(FirebaseAuth.getInstance().currentUser!=null)
                writeNewPost(userId, postpojo)


            }
        }
    }



    private fun startAllLoadsActivity() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

    private fun writeNewPost(userId: String, postpojo: Post) {
        val key = mDatabase!!.child("posts").push().getKey()
        val postValues = postpojo.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/posts/" + key!!] = postValues
        childUpdates["/user-posts/$userId/$key"] = postValues

        mDatabase!!.updateChildren(childUpdates).addOnCompleteListener {
            cleanUpPostInput()
            Toast.makeText(context,"Successfully Posted!",Toast.LENGTH_LONG).show()
            post.text = "POST NOW"

        }.addOnCanceledListener {
            Toast.makeText(context,"Try Again",Toast.LENGTH_LONG).show()
        }
    }

    private fun cleanUpPostInput() {
        select_destination.text = "Destination"
        select_destination.setTextColor(ContextCompat.getColor(context,R.color.blue_grey_200))
        select_source.text = "Source"
        select_source.setTextColor(ContextCompat.getColor(context,R.color.blue_grey_200))
        postpojo.mSource = null
        postpojo.mDestination = null
        postpojo.mMeterial = null
        postpojo.mPayload = null
        postpojo.mTruckLength = null
        weight.setText("")
        length.setText("")
        material.setText("")
        otherreq.setText("")

    }
    private fun flipthefab() {
        fabrotation = if (fabrotation == 0f) {
            180f
        } else {
            0f
        }
        val interpolator = OvershootInterpolator()
        ViewCompat.animate(fab_revert)
                .rotation(fabrotation)
                .withLayer()
                .setDuration(300)
                .setInterpolator(interpolator)
                .start()

        //swap cities
        if(postpojo.mSource==null||postpojo.mDestination==null){
            Toast.makeText(context,"Please Enter Route",Toast.LENGTH_LONG).show()
        }else{
            val t1 = select_source.text.toString()
            val t2 = select_destination.text.toString()

            select_source.text = t2
            select_destination.text = t1
            postpojo.mDestination = t1.substring(2)
            postpojo.mSource = t2.substring(2)
        }


    }
    // [END write_fan_out]

    private fun setSpinners() {
        val spinnervehicle = findViewById<MaterialSpinner>(R.id.spinnervtype)
        spinnervehicle.setItems("Select",
                "LCV",
                "Truck",
                "Tusker",
                "Taurus",
                "Trailers",
                "Container Body",
                "Refrigerated Van",
                "Tankers",
                "Tippers",
                "Bulkers",
                "Car Carriers",
                "Scooter Body",
                "Hydraulic Axles")
        spinnervehicle.setOnItemSelectedListener { view, position, id, item ->
            postpojo.setmTruckType(item.toString())
        }

        val spinnerbody = findViewById<MaterialSpinner>(R.id.spinnerbtype)
        spinnerbody.setItems("Select",
                "Normal",
                "Full Body",
                "Half Body",
                "Open Body",
                "Platform",
                "Skeleton",
                "Semi-low",
                "Low",
                "Trolla/Body Trailer",
                "Refer",
                "High Cube")
        spinnerbody.setOnItemSelectedListener { view, position, id, item ->
            postpojo.setmTruckBodyType(item.toString())
        }
        val spinnerweight = findViewById<MaterialSpinner>(R.id.spinnerweight)
        spinnerweight.setItems("KG", "Tons", "MT")
        spinnerweight.setOnItemSelectedListener { view, position, id, item ->  }
        val spinnerlength = findViewById<MaterialSpinner>(R.id.spinnerlength)
        spinnerlength.setItems("Meters", "Feets")
        spinnerlength.setOnItemSelectedListener { view, position, id, item ->  }    }

    private fun setFragmentsAdapter() {

        val mPagerAdapter = FragmentPagerItemAdapter(
                supportFragmentManager, FragmentPagerItems.with(this)
                .add("All", RecentLoadsFragment::class.java)
                .add("Yours", YourLoadsFragment::class.java)
                .create())
        mViewPager.adapter = mPagerAdapter
        Logger.v("LoadBoardActivity")
        val viewPagerTab = findViewById<SmartTabLayout>(R.id.tabs)
        viewPagerTab.setViewPager(mViewPager)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val place = PlaceAutocomplete.getPlace(context, data)

            when (requestCode) {

                PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE -> {
                    select_source.text = ". ${place.name}"
                    select_source.setTextColor(ContextCompat.getColor(context, R.color.blue_grey_900))
                    postpojo.mSource = place.name.toString()
                }
                PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION -> {
                    select_destination.text = ". ${place.name}"
                    select_destination.setTextColor(ContextCompat.getColor(context, R.color.blue_grey_900))
                    postpojo.mDestination = place.name.toString()

                }

            }
        }
    }

    private fun setRoutePickup() {

        select_source.setOnClickListener {
            starttheplacesfragment(PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE)
        }

        select_destination.setOnClickListener {
            starttheplacesfragment(PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION)
        }

    }

    private fun starttheplacesfragment(code: Int) = try {
        val typeFilter = AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .setCountry("IN")
                .build()
        val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                .setFilter(typeFilter)
                .build(this)
        startActivityForResult(intent, code)
    } catch (e: GooglePlayServicesRepairableException) {
        // TODO: Handle the error.
    } catch (e: GooglePlayServicesNotAvailableException) {
        // TODO: Handle the error.
    }

    fun getUid(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
    private fun getFuid(): String {
        return preferenceManager.fuid!!
    }

    fun getUserPhoneNo(): String? {
        val user = getCurrentUser()
        return user!!.phoneNumber
    }

    fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    private fun getDate(): String {
        val c = Calendar.getInstance()
        println("Current time => " + c.time)

        val df = SimpleDateFormat("MMM dd, HH:mm")
        return df.format(c.time)
    }

}
