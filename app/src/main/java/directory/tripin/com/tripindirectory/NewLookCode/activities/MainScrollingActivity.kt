package directory.tripin.com.tripindirectory.NewLookCode.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import directory.tripin.com.tripindirectory.R
import kotlinx.android.synthetic.main.activity_main_scrolling.*
import kotlinx.android.synthetic.main.layout_choose_vehicle.*
import kotlinx.android.synthetic.main.layout_route_input.*
import android.support.v4.view.ViewCompat
import android.view.animation.OvershootInterpolator
import android.arch.paging.PagedList
import android.content.DialogInterface
import android.net.Uri
import android.support.annotation.NonNull
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.bumptech.glide.util.Util.getSnapshot
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import directory.tripin.com.tripindirectory.ChatingActivities.ChatHeadsActivity
import directory.tripin.com.tripindirectory.NewLookCode.*
import directory.tripin.com.tripindirectory.activity.PartnerDetailScrollingActivity
import directory.tripin.com.tripindirectory.formactivities.CompanyInfoActivity
import directory.tripin.com.tripindirectory.formactivities.FormFragments.CompanyFromFragment
import directory.tripin.com.tripindirectory.forum.MainActivity
import directory.tripin.com.tripindirectory.forum.NewPostActivity
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo
import directory.tripin.com.tripindirectory.utils.DB
import kotlinx.android.synthetic.main.content_main_scrolling.*
import kotlinx.android.synthetic.main.layout_main_actionbar.*


class MainScrollingActivity : AppCompatActivity() {

    val fleets: ArrayList<FleetSelectPojo> = ArrayList()
    internal var PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE = 1
    internal var PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION = 2
    lateinit var context: Context
    var fabrotation = 0f
    lateinit var adapter: FirestorePagingAdapter<PartnerInfoPojo, PartnersViewHolder>
    lateinit var basicQueryPojo: BasicQueryPojo
    private val SIGN_IN_FOR_CREATE_COMPANY = 123


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Activity Itit
        setContentView(R.layout.activity_main_scrolling)
        context = this

        if(FirebaseAuth.getInstance().currentUser==null|| FirebaseAuth.getInstance().currentUser!!.phoneNumber==null){
            val i = Intent(this, FacebookRequiredActivity::class.java)
            startActivity(i)
        }


        fabFlip.setOnClickListener {
            flipthefab()
        }
        setSelectFleetAdapter()
        setRoutePickup()
        basicQueryPojo = BasicQueryPojo("", "", ArrayList<String>())
        setMainAdapter(basicQueryPojo)
        setListners()
        setBottomNavogation()


    }

    private fun setBottomNavogation() {

//        val ahBottomNavidATION: AHBottomNavigation
//        ahBottomNavidATION = findViewById(R.id.bottom_navigation)
//
//        val item1 = AHBottomNavigationItem("Directory", ContextCompat.getDrawable(context, R.drawable.ic_home_black_24dp))
//        val item2 = AHBottomNavigationItem("Loadboard", ContextCompat.getDrawable(context, R.drawable.ic_widgets_grey_24dp))
//        val item3 = AHBottomNavigationItem("Profile", ContextCompat.getDrawable(context, R.drawable.ic_account_circle_black_24dp))
//
//        ahBottomNavidATION.addItem(item1)
//        ahBottomNavidATION.addItem(item2)
//        ahBottomNavidATION.addItem(item3)
//        ahBottomNavidATION.defaultBackgroundColor = ContextCompat.getColor(context, R.color.blue_grey_100)
//        ahBottomNavidATION.accentColor = ContextCompat.getColor(context, R.color.blue_grey_800)
//        ahBottomNavidATION.inactiveColor = ContextCompat.getColor(context, R.color.blue_grey_300)

        fabMenu.bindAnchorView(fab)
        fabMenu.setOnFABMenuSelectedListener { view, id ->
            when (id) {
                R.id.action_loadboard -> {
                    startLoadboardActivity()
                }
                R.id.action_profile -> {
                    startProfileActivity()
                }
            }
        }


    }


    private fun setListners() {
//        app_bar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
//
//            if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
//            {
//                //  Collapsed
//                routeinput.visibility = View.GONE
//
//            }
//            else
//            {
//                showroute.visibility = View.VISIBLE
//
//            }
//        }

        showall.setOnClickListener {
            startAllTransportersActivity()
        }
        yourbusiness.setOnClickListener {
            startYourBusinessActivity()
        }
        showchats.setOnClickListener {
            setChatHeadsActivity()
        }
        posttolb.setOnClickListener {
            startLoadboardActivity()
        }
        posttoselected.setOnClickListener {

        }

    }

    private fun startProfileActivity() {
        val i = Intent(this, FacebookRequiredActivity::class.java)
        startActivity(i)
    }

    private fun startLoadboardActivity() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            // already signed in
            val i = Intent(this, LoadBoardActivity::class.java)
            i.putExtra("query", basicQueryPojo)
            startActivity(i)
        } else {
            // not signed in
            startSignInFor(SIGN_IN_FOR_CREATE_COMPANY)
        }
    }

    private fun setChatHeadsActivity() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            // already signed in
            val i = Intent(this, ChatHeadsActivity::class.java)
            startActivity(i)
        } else {
            // not signed in
            startSignInFor(SIGN_IN_FOR_CREATE_COMPANY)
        }
    }

    private fun startYourBusinessActivity() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            // already signed in
            val i = Intent(this, CompanyInfoActivity::class.java)
            startActivity(i)
        } else {
            // not signed in
            startSignInFor(SIGN_IN_FOR_CREATE_COMPANY)
        }

    }

    private fun startSignInFor(signInFor: Int) {
        startActivityForResult(
                // Get an instance of AuthUI based on the default app
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(
                                listOf<AuthUI.IdpConfig>(AuthUI.IdpConfig.PhoneBuilder().build()))
                        .build(),
                signInFor)
    }

    private fun startAllTransportersActivity() {
        val i = Intent(this, AllTransportersActivity::class.java)
        i.putExtra("query", basicQueryPojo)
        startActivity(i)
    }


    private fun setMainAdapter(basicQueryPojo: BasicQueryPojo) {

        Logger.v(basicQueryPojo.toString())

        var baseQuery: Query = FirebaseFirestore.getInstance()
                .collection("partners")

        if (!basicQueryPojo.mSourceCity.isEmpty() && basicQueryPojo.mSourceCity != "Select City") {
            baseQuery = baseQuery.whereEqualTo("mSourceCities.${basicQueryPojo.mSourceCity.toUpperCase()}", true)

        }
        if (!basicQueryPojo.mDestinationCity.isEmpty() && basicQueryPojo.mDestinationCity != "Select City") {
            baseQuery = baseQuery.whereEqualTo("mDestinationCities.${basicQueryPojo.mDestinationCity.toUpperCase()}", true)

        }

        for (fleet in basicQueryPojo.mFleets!!) {
            baseQuery = baseQuery.whereEqualTo("fleetVehicle.$fleet", true)
        }

        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(1)
                .setPageSize(4)
                .build()

        val options = FirestorePagingOptions.Builder<PartnerInfoPojo>()
                .setLifecycleOwner(this)
                .setQuery(baseQuery, config, PartnerInfoPojo::class.java)
                .build()
        adapter = object : FirestorePagingAdapter<PartnerInfoPojo, PartnersViewHolder>(options) {


            @NonNull
            override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): PartnersViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_new_transporter, parent, false)
                return PartnersViewHolder(view)
            }

            override fun onBindViewHolder(@NonNull holder: PartnersViewHolder,
                                          position: Int,
                                          @NonNull model: PartnerInfoPojo) {

                holder.mCompany.text = model.getmCompanyName()


                if (model != null) {
                    if (model.getmCompanyAdderss() != null)
                        holder.mAddress.text = model.getmCompanyAdderss().city
                }

                holder.itemView.setOnClickListener {

                    val i = Intent(context, PartnerDetailScrollingActivity::class.java)
                    i.putExtra("uid", getItem(position)!!.id)
                    i.putExtra("cname", model.getmCompanyName())
                    startActivity(i)
                }

                holder.mCall.setOnClickListener {


                    val phoneNumbers = java.util.ArrayList<String>()
                    val contactPersonPojos = model.getmContactPersonsList()

                    if (contactPersonPojos != null && contactPersonPojos!!.size > 1) {
                        for (i in contactPersonPojos!!.indices) {
                            if (model.getmContactPersonsList()[i] != null) {
                                val number = model.getmContactPersonsList()[i].getmContactPersonMobile
                                phoneNumbers.add(number)

                            }
                        }

                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Looks like there are multiple phone numbers.")
                                .setCancelable(false)
                                .setAdapter(ArrayAdapter(context, R.layout.dialog_multiple_no_row, R.id.dialog_number, phoneNumbers)
                                ) { dialog, item ->
                                    Logger.v("Dialog number selected :" + phoneNumbers[item])

                                    callNumber(phoneNumbers[item])
                                }

                        builder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, id: Int) {
                                // User cancelled the dialog
                            }
                        })
                        builder.create()
                        builder.show()


                    } else {

                        val number = model.getmContactPersonsList()[0].getmContactPersonMobile
                        callNumber(number)
                    }
                }


            }

            override fun onLoadingStateChanged(state: LoadingState) {
                when (state) {

                    LoadingState.LOADING_INITIAL -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                        loading.visibility = View.VISIBLE
                        showall.visibility = View.GONE

                    }

                    LoadingState.LOADING_MORE -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                    }

                    LoadingState.LOADED -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                        loading.visibility = View.GONE
                        showall.visibility = View.VISIBLE

                    }

                    LoadingState.ERROR -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                    }

                }
            }
        }

        rv_transporters.layoutManager = LinearLayoutManager(this)
        rv_transporters.adapter = adapter


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val place = PlaceAutocomplete.getPlace(context, data)

            when (requestCode) {

                PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE -> {
                    textViewSource.text = ". ${place.name}"
                    textViewSource.setTextColor(ContextCompat.getColor(context, R.color.blue_grey_900))
                    basicQueryPojo.mSourceCity = place.name.toString()
                    setMainAdapter(basicQueryPojo)
                }
                PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION -> {
                    textViewDestination.text = ". ${place.name}"
                    textViewDestination.setTextColor(ContextCompat.getColor(context, R.color.blue_grey_900))
                    basicQueryPojo.mDestinationCity = place.name.toString()
                    setMainAdapter(basicQueryPojo)

                }
                SIGN_IN_FOR_CREATE_COMPANY -> {
                    startYourBusinessActivity()
                }
            }
        }
    }

    private fun setRoutePickup() {

        lin_source.setOnClickListener {
            starttheplacesfragment(PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE)
        }

        lin_des.setOnClickListener {
            starttheplacesfragment(PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION)
        }

    }

    private fun flipthefab() {
        fabrotation = if (fabrotation == 0f) {
            180f
        } else {
            0f
        }
        val interpolator = OvershootInterpolator()
        ViewCompat.animate(fabFlip)
                .rotation(fabrotation)
                .withLayer()
                .setDuration(300)
                .setInterpolator(interpolator)
                .start()

        //swap cities
        val t1 = textViewSource.text.toString()
        val t2 = textViewDestination.text.toString()

        textViewSource.text = t2
        textViewDestination.text = t1
        basicQueryPojo.mDestinationCity = t1.substring(2)
        basicQueryPojo.mSourceCity = t2.substring(2)
        setMainAdapter(basicQueryPojo)
    }

    private fun setSelectFleetAdapter() {
        // Loads animals into the ArrayList
        addFleets()

        // Creates a vertical Layout Manager
        rv_fleets.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // You can use GridLayoutManager if you want multiple columns. Enter the number of columns as a parameter.
//        rv_animal_list.layoutManager = GridLayoutManager(this, 2)

        // Access the RecyclerView Adapter and load the data into it
        rv_fleets.adapter = FleetsSelectAdapter(fleets, this, object : OnFleetSelectedListner {
            override fun onFleetSelected(mFleetName: String) {
                basicQueryPojo.mFleets!!.add(mFleetName)
                setMainAdapter(basicQueryPojo)
            }

            override fun onFleetDeslected(mFleetName: String) {
                if (basicQueryPojo.mFleets != null) {
                    if (basicQueryPojo.mFleets!!.contains(mFleetName)) {
                        basicQueryPojo.mFleets!!.remove(mFleetName)
                        setMainAdapter(basicQueryPojo)
                    }
                }
            }

        })
    }

    private fun addFleets() {
        fleets.add(FleetSelectPojo("LCV", ContextCompat.getDrawable(this, R.mipmap.ic_fleet_lcv_round)!!, false))
        fleets.add(FleetSelectPojo("Truck", ContextCompat.getDrawable(this, R.mipmap.ic_fleet_truckpng_round)!!, false))
        fleets.add(FleetSelectPojo("Tusker", ContextCompat.getDrawable(this, R.mipmap.ic_fleet_tuskerpng_round)!!, false))
        fleets.add(FleetSelectPojo("Taurus", ContextCompat.getDrawable(this, R.mipmap.ic_fleet_tauruspng_round)!!, false))
        fleets.add(FleetSelectPojo("Trailers", ContextCompat.getDrawable(this, R.mipmap.ic_fleet_trailerpng_round)!!, false))

    }

    private fun starttheplacesfragment(code: Int) {
        try {
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

    }

    private fun callNumber(number: String) {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:" + Uri.encode(number.trim { it <= ' ' }))
        callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(callIntent)
    }
}
