package directory.tripin.com.tripindirectory.newlookcode.activities

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
import android.net.Uri
import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.andrognito.flashbar.Flashbar
import com.andrognito.flashbar.anim.FlashAnim
import com.appsee.Appsee
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.keiferstone.nonet.NoNet
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import directory.tripin.com.tripindirectory.chatingactivities.ChatHeadsActivity
import directory.tripin.com.tripindirectory.chatingactivities.ChatRoomActivity
import directory.tripin.com.tripindirectory.chatingactivities.models.UserPresensePojo
import directory.tripin.com.tripindirectory.newlookcode.*
import directory.tripin.com.tripindirectory.helper.CircleTransform
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.helper.RecyclerViewAnimator
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import directory.tripin.com.tripindirectory.model.HubFetchedCallback
import directory.tripin.com.tripindirectory.model.RouteCityPojo
import directory.tripin.com.tripindirectory.newlookcode.pojos.BasicQueryPojo
import directory.tripin.com.tripindirectory.newlookcode.pojos.FleetSelectPojo
import directory.tripin.com.tripindirectory.newlookcode.pojos.InteractionPojo
import directory.tripin.com.tripindirectory.newlookcode.utils.MixPanelConstants
import directory.tripin.com.tripindirectory.newprofiles.activities.CompanyProfileDisplayActivity
import directory.tripin.com.tripindirectory.newprofiles.activities.UserEditProfileActivity
import directory.tripin.com.tripindirectory.newprofiles.models.CompanyCardPojo
import directory.tripin.com.tripindirectory.newprofiles.models.CompanyProfilePojo
import directory.tripin.com.tripindirectory.newprofiles.models.DirectorySearchPojo
import directory.tripin.com.tripindirectory.newprofiles.models.RateReminderPojo
import directory.tripin.com.tripindirectory.utils.TextUtils
import kotlinx.android.synthetic.main.content_main_scrolling.*
import kotlinx.android.synthetic.main.layout_directory_actionbar.*
import kotlinx.android.synthetic.main.layout_main_actionbar.*
import kotlinx.android.synthetic.main.newlookfeedback.*
import libs.mjn.prettydialog.PrettyDialog
import libs.mjn.prettydialog.PrettyDialogCallback
import org.json.JSONException
import org.json.JSONObject
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*


class MainScrollingActivity : LocalizationActivity() , HubFetchedCallback {


    val fleets: ArrayList<FleetSelectPojo> = ArrayList()
    internal var PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE = 1
    internal var PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION = 2
    lateinit var context: Context
    var fabrotation = 0f
    lateinit var adapter2: FirestorePagingAdapter<CompanyProfilePojo, PartnersViewHolder>
    lateinit var adapter: FirestorePagingAdapter<CompanyCardPojo, PartnersViewHolder>
//    lateinit var adapter3: FirestorePagingAdapter<PartnerInfoPojo, PartnersViewHolder>


    lateinit var basicQueryPojo: BasicQueryPojo
    private val SIGN_IN_FOR_CREATE_COMPANY = 123
    lateinit var preferenceManager: PreferenceManager
    lateinit var mSourceRouteCityPojo: RouteCityPojo
    lateinit var mDestinationRouteCityPojo: RouteCityPojo
    lateinit var textUtils: TextUtils
    lateinit var firebaseAnalytics: FirebaseAnalytics
    val REQUEST_INVITE = 1001
    lateinit var gson: Gson
    lateinit var recyclerViewAnimator: RecyclerViewAnimator
    lateinit var mixpanelAPI: MixpanelAPI
    var isRatePopuped = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Activity Itit
        setContentView(R.layout.activity_main_scrolling)
        context = this
        gson = Gson()
        textUtils = TextUtils()
        preferenceManager = PreferenceManager.getInstance(this)
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        mixpanelAPI = MixpanelAPI.getInstance(context, MixPanelConstants.MIXPANEL_TOKEN)

        recyclerViewAnimator = RecyclerViewAnimator(rv_transporters_att)

        if (FirebaseAuth.getInstance().currentUser == null
                || preferenceManager.rmn == null
                || preferenceManager.userId == null
                || FirebaseAuth.getInstance().currentUser!!.phoneNumber == null) {
            val i = Intent(this, FacebookRequiredActivity::class.java)
            i.putExtra("from", "MainActivity")
            startActivityForResult(i, 3)
        } else {
            if (preferenceManager.userId != null) {
                val userPresensePojo2 = UserPresensePojo(false, Date().time, "")
                FirebaseDatabase.getInstance().reference
                        .child("chatpresence")
                        .child("users")
                        .child(preferenceManager.userId)
                        .onDisconnect()
                        .setValue(userPresensePojo2)
            }
        }



        fabFlip.setOnClickListener {
            flipthefab()
        }
        fabClear.setOnClickListener {
            clearCities()
        }
        setSelectFleetAdapter()
        setRoutePickup()
        basicQueryPojo = BasicQueryPojo("", "", "", "", ArrayList<String>())
        getIntentData()

        setListners()
        setBottomNavogation()

        mSourceRouteCityPojo = RouteCityPojo(context, 1, 0, this)
        mDestinationRouteCityPojo = RouteCityPojo(context, 2, 0, this)

        internetCheck()

        if (!preferenceManager.isDirectoryGuided) {
            showIntro()
        }

        Appsee.start();


    }

    private fun getIntentData() {
        if(intent.extras!=null){
            if(intent.extras.getSerializable("query")!=null){
                val directorySearchPojo = intent.extras.getSerializable("query") as DirectorySearchPojo

                basicQueryPojo.mSourceCity = directorySearchPojo.getmSourceCity()
                basicQueryPojo.mDestinationCity = directorySearchPojo.getmDestinationCity()

                basicQueryPojo.mSourceHub = directorySearchPojo.getmSourceHub()
                basicQueryPojo.mDestinationHub = directorySearchPojo.getmDestinationHub()

                textViewDestination.text = basicQueryPojo.mDestinationCity
                textViewSource.text = basicQueryPojo.mSourceCity

                setMainAdapter(basicQueryPojo)
            }else{
                setMainAdapter(basicQueryPojo)
            }

        }else{
            setMainAdapter(basicQueryPojo)
        }
    }

    private fun clearCities() {
        textViewSource.text = getString(R.string.select_city)
        textViewDestination.text = getString(R.string.select_city)
        basicQueryPojo.mDestinationCity = ""
        basicQueryPojo.mSourceCity = ""
        basicQueryPojo.mDestinationHub = ""
        basicQueryPojo.mSourceHub = ""
//        fabFlip.visibility = View.INVISIBLE
//        fabClear.visibility = View.INVISIBLE
        setMainAdapter(basicQueryPojo)
        val bundle = Bundle()
        firebaseAnalytics.logEvent("z_route_clear", bundle)
    }






    override fun onDestroy() {
        mixpanelAPI.flush()
        super.onDestroy()
    }

    private fun showIntro() {

        val tapTargetSequence: TapTargetSequence = TapTargetSequence(this)
                .targets(
                        TapTarget.forView(rv_fleets, getString(R.string.select_fleet_type), getString(R.string.fleet_type_discription))
                                .transparentTarget(true)
                                .targetRadius(75)
                                .drawShadow(true)
                                .cancelable(true).outerCircleColor(R.color.primaryColor),
                        TapTarget.forView(fromtitle, getString(R.string.select_source_city), getString(R.string.select_source_discription))
                                .transparentTarget(true)
                                .drawShadow(true)
                                .cancelable(true).outerCircleColor(R.color.primaryColor),
                        TapTarget.forView(totitle, getString(R.string.select_destination_city), getString(R.string.select_destination_discription))
                                .transparentTarget(true)
                                .drawShadow(true)
                                .cancelable(true).outerCircleColor(R.color.primaryColor),
                        TapTarget.forView(searchbyname, getString(R.string.search_by_name), getString(R.string.search_a_company_bio))
                                .transparentTarget(true)
                                .drawShadow(true)
                                .cancelable(true).outerCircleColor(R.color.primaryColor)

//                        TapTarget.forView(posttoselected, "Post To Selected Transporters", "Now you can select multiple transporters in your results list and send your requirement to all of them in a single tap")
//                                .transparentTarget(true)
//                                .drawShadow(true)
//                                .cancelable(true).outerCircleColor(R.color.primaryColor),
//                        TapTarget.forView(posttolb, "Post to Loadboard", "Not sure about the results? Don't worry, Post your requirement on Loadboard. All trusted transportes will be notified")
//                                .transparentTarget(true)
//                                .drawShadow(true)
//                                .cancelable(true).outerCircleColor(R.color.primaryColor),

                )
                .listener(object : TapTargetSequence.Listener {
                    override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {
                    }

                    override fun onSequenceFinish() {
                        Toast.makeText(applicationContext, getString(R.string.use_iln_wisely), Toast.LENGTH_SHORT).show()
                        preferenceManager.setisDirectoryGuided(true)
                        val bundle = Bundle()
                        firebaseAnalytics.logEvent("z_mainguidefifished", bundle)
                    }

                    override fun onSequenceCanceled(lastTarget: TapTarget) {
                        // Boo
                        preferenceManager.setisDirectoryGuided(true)
                        Toast.makeText(applicationContext, getString(R.string.restart_guide_from_menu), Toast.LENGTH_SHORT).show()
                    }
                })

        tapTargetSequence.start()
    }

    override fun onResume() {
        super.onResume()



//        if (preferenceManager.displayName != null) {
//            var name = preferenceManager.displayName.substringBefore(" ")
//            lookque.text = "Hello $name, how is the new look?"
//        }

//        if(preferenceManager.isInboxRead){
//            chatbadge.visibility = View.INVISIBLE
//        }else{
//            chatbadge.visibility = View.VISIBLE
//        }


        if (preferenceManager.isInsuranceResponded) {
            feedback.visibility = View.GONE
        } else {
            feedback.visibility = View.VISIBLE
        }



        showCompanyRatingSnackBar(preferenceManager.prefRateReminder)
    }

    private fun showCompanyRatingSnackBar(prefRateReminder: String) {

        if (!prefRateReminder.isEmpty()) {
            val rateReminderPojo = Gson().fromJson(prefRateReminder, RateReminderPojo::class.java)

            if (rateReminderPojo.getmIsActive()) {
                if (!isRatePopuped) {

                    var title = ""
                    if (rateReminderPojo.getmRatings() != null) {
                        if (rateReminderPojo.getmCompanyName() != null) {
                            title = rateReminderPojo.getmCompanyName() + " || " + rateReminderPojo.getmRatings().toBigDecimal().setScale(1, RoundingMode.UP).toString()
                        } else {
                            title = rateReminderPojo.getmDisplayName() + " || " + rateReminderPojo.getmRatings().toBigDecimal().setScale(1, RoundingMode.UP).toString()
                        }

                    } else {
                        if (rateReminderPojo.getmCompanyName() != null) {
                            title = rateReminderPojo.getmCompanyName() + " || New"
                        } else {
                            title = rateReminderPojo.getmDisplayName() + " || New"
                        }
                    }

                    var subTitle = ""
                    if (rateReminderPojo.getmAction() == "call") {
                        subTitle = "You called this company, How was your experience? Visit & Rate now or swipe to dismiss."
                    } else {
                        subTitle = "You chatted with this company, How was your experience? Visit & Rate now or swipe to dismiss."
                    }

                    Flashbar.Builder(this)
                            .gravity(Flashbar.Gravity.BOTTOM)
                            .title(title)
                            .message(subTitle)
                            .positiveActionText("Visit and Rate Now!")
                            .backgroundDrawable(R.drawable.thrid_bg)
                            .positiveActionTextColorRes(R.color.amber_50)
                            .negativeActionTextColorRes(R.color.colorAccent)
                            .showIcon(0.8f, ImageView.ScaleType.CENTER_CROP)
                            .icon(R.drawable.abc_ratingbar_material)
                            .iconColorFilterRes(R.color.white)
                            .enterAnimation(FlashAnim.with(this)
                                    .animateBar()
                                    .duration(750)
                                    .alpha()
                                    .overshoot())
                            .exitAnimation(FlashAnim.with(this)
                                    .animateBar()
                                    .duration(400)
                                    .accelerateDecelerate())
                            .iconAnimation(FlashAnim.with(this)
                                    .animateIcon()
                                    .pulse()
                                    .alpha()
                                    .duration(750)
                                    .accelerate())
                            .enableSwipeToDismiss()
                            .barDismissListener(object : Flashbar.OnBarDismissListener {
                                override fun onDismissing(bar: Flashbar, isSwiped: Boolean) {
                                    Log.d("Directory", "Flashbar is dismissing with $isSwiped")
                                }

                                override fun onDismissProgress(bar: Flashbar, progress: Float) {
                                    Log.d("Directory", "Flashbar is dismissing with progress $progress")
                                }

                                override fun onDismissed(bar: Flashbar, event: Flashbar.DismissEvent) {
                                    Log.d("Directory", "Flashbar is dismissed with event $event")
                                    //not interested
                                    val rateReminderPojoNew = RateReminderPojo(rateReminderPojo.getmCompanyName(),
                                            rateReminderPojo.getmDisplayName(),
                                            rateReminderPojo.getmRMN(),
                                            rateReminderPojo.getmUID(),
                                            rateReminderPojo.getmFUID(),
                                            "call",
                                            rateReminderPojo.getmTimeStamp(),
                                            rateReminderPojo.getmRatings(), false)

                                    preferenceManager.prefRateReminder = Gson().toJson(rateReminderPojoNew)
                                    val bundle = Bundle()
                                    bundle.putInt("isRated", 0)
                                    firebaseAnalytics.logEvent("z_rate_bottom_snackbar", bundle)
                                    isRatePopuped = false
                                }
                            })
                            .positiveActionTapListener(object : Flashbar.OnActionTapListener {
                                override fun onActionTapped(bar: Flashbar) {
                                    bar.dismiss()

                                    val bundle = Bundle()
                                    bundle.putInt("isRated", 1)
                                    firebaseAnalytics.logEvent("z_rate_bottom_snackbar", bundle)

                                    val rateReminderPojoNew = RateReminderPojo(rateReminderPojo.getmCompanyName(),
                                            rateReminderPojo.getmDisplayName(),
                                            rateReminderPojo.getmRMN(),
                                            rateReminderPojo.getmUID(),
                                            rateReminderPojo.getmFUID(),
                                            "call",
                                            rateReminderPojo.getmTimeStamp(),
                                            rateReminderPojo.getmRatings(), false)

                                    preferenceManager.prefRateReminder = Gson().toJson(rateReminderPojoNew)

                                    val i = Intent(context, CompanyProfileDisplayActivity::class.java)
                                    i.putExtra("uid", rateReminderPojo.getmUID())
                                    i.putExtra("rmn", rateReminderPojo.getmRMN())
                                    i.putExtra("fuid", rateReminderPojo.getmFUID())
                                    i.putExtra("action", "direct_rate")
                                    startActivity(i)
                                    isRatePopuped = false

                                }
                            })
                            .build().show()
                    isRatePopuped = true
                }

            }

        }

    }


    override fun onBackPressed() {
        //finishAffinity()
        super.onBackPressed()
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


        posttolb2.setOnClickListener {
            startLoadboardActivity("ActionBar")
        }

        posttoselected2.setOnClickListener {
            startPostToSelectedActivity()
        }

        oldlook.setOnClickListener {
            startOldActivity()
        }
        givefeedback.setOnClickListener {
//            shownewlookfeedbackdialog()
            openOfferActivity()
        }



        searchbyname.setOnClickListener {
            startSearchCompanyActivity()
        }

        backfromdirectory.setOnClickListener {
            finish()
        }



    }


    private fun shownewlookfeedbackdialog() {

        val prettyDialog: PrettyDialog = PrettyDialog(this)

        prettyDialog
                .setTitle("ILN New Look")
                .setMessage("Do you like the new look?")
                .addButton(
                        "Yes! Keep it",
                        R.color.pdlg_color_white,
                        R.color.green_400
                ) {

                    Toast.makeText(context, "Welcome!", Toast.LENGTH_LONG).show()
                    preferenceManager.setisNewLookAccepted(true)
                    feedback.visibility = View.GONE
                    prettyDialog.dismiss()
                    val bundle = Bundle()
                    bundle.putString("responce", "KeepIt")
                    firebaseAnalytics.logEvent("z_newlook_responce", bundle)

                }.addButton(
                        "Go Back to Old Look",
                        R.color.pdlg_color_white,
                        R.color.blue_grey_400
                ) {
                    Toast.makeText(context, "Launching Old Look!", Toast.LENGTH_LONG).show()
                    prettyDialog.dismiss()
                    preferenceManager.setisOnNewLook(false)
                    startOldActivity()

                    val bundle = Bundle()
                    bundle.putString("responce", "GoBack")
                    firebaseAnalytics.logEvent("z_newlook_responce", bundle)

                }.addButton(
                        "Cancel",
                        R.color.pdlg_color_white,
                        R.color.blue_grey_100,
                        PrettyDialogCallback {
                            prettyDialog.dismiss()

                            val bundle = Bundle()
                            bundle.putString("responce", "Cancel")
                            firebaseAnalytics.logEvent("z_newlook_responce", bundle)

                        }
                )
        prettyDialog.show()

        val bundle = Bundle()
        if (preferenceManager.displayName == null) {
            bundle.putString("iswithname", "No")
        } else {
            bundle.putString("iswithname", "Yes")
        }
        firebaseAnalytics.logEvent("z_respond_clicked", bundle)
    }

    private fun startPostToSelectedActivity() {
        val bundle = Bundle()

        if (!basicQueryPojo.mSourceCity.isEmpty() && basicQueryPojo.mSourceCity != "Select City") {

            if (!basicQueryPojo.mDestinationCity.isEmpty() && basicQueryPojo.mDestinationCity != "Select City") {
                bundle.putString("iswithroute", "Yes")
                val i = Intent(this, PostToSelectedActivity::class.java)
                i.putExtra("query", basicQueryPojo)
                startActivity(i)
                mainPageAction("post_to_selected")
            } else {
                bundle.putString("iswithroute", "No")
                Toast.makeText(context, "Enter Destination City!", Toast.LENGTH_LONG).show()
            }
        } else {
            bundle.putString("iswithroute", "No")
            Toast.makeText(context, "Enter Source City!", Toast.LENGTH_LONG).show()
        }

        firebaseAnalytics.logEvent("z_posttoselected_clicked", bundle)


    }

    private fun startOldActivity() {
        val i = Intent(this, NewSplashActivity::class.java)
        startActivity(i)
        finishAffinity()
    }
    private fun openOfferActivity() {
        val i = Intent(this, NewOfferActivity::class.java)
        startActivity(i)
    }



    private fun startSearchCompanyActivity() {
        val i = Intent(this, SearchCompanyActivity::class.java)
        startActivity(i)
    }

    private fun startProfileActivity() {
        val i = Intent(this, FacebookRequiredActivity::class.java)
        startActivity(i)
    }

    private fun startLoadboardActivity(s: String) {
        if (FirebaseAuth.getInstance().currentUser != null) {
            // already signed in
            val i = Intent(this, FSLoadBoardActivity::class.java)
            if (s != "BottomMenu") {
                i.putExtra("query", basicQueryPojo)
            }
            startActivity(i)
            val bundle = Bundle()
            bundle.putString("from", s)
            firebaseAnalytics.logEvent("z_openloadboard", bundle)
            mainPageAction("loadboard")

        } else {
            // not signed in
            startSignInFor(SIGN_IN_FOR_CREATE_COMPANY)
        }
    }

    private fun setChatHeadsActivity(s: String) {
        if (FirebaseAuth.getInstance().currentUser != null) {
            // already signed in
            mainPageAction("chats")
            val i = Intent(this, ChatHeadsActivity::class.java)
            startActivity(i)


            val bundle = Bundle()
            bundle.putString("from", s)
            firebaseAnalytics.logEvent("z_openchatheads", bundle)

        } else {
            // not signed in
            startSignInFor(SIGN_IN_FOR_CREATE_COMPANY)
        }

    }

    private fun startYourBusinessActivity(s: String) {
//        if (FirebaseAuth.getInstance().currentUser != null) {
//            // already signed in
//            val i = Intent(this, CompanyInfoActivity::class.java)
//            startActivity(i)
//            val bundle = Bundle()
//            bundle.putString("from", s)
//            firebaseAnalytics.logEvent("z_selfcompanyprofile", bundle)
//        } else {
//            // not signed in
//            startSignInFor(SIGN_IN_FOR_CREATE_COMPANY)
//        }

        val i = Intent(this, UserEditProfileActivity::class.java)
        startActivity(i)
        mainPageAction("profile")


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
        mainPageAction("see_all")
        val bundle = Bundle()
        firebaseAnalytics.logEvent("z_seeall_transporters", bundle)
    }




    private fun setMainAdapter(basicQueryPojo: BasicQueryPojo) {


        val bundle = Bundle()
        Logger.v(basicQueryPojo.toString())

        var source = "ANYWHERE"
        if (!basicQueryPojo.mSourceHub.isEmpty() && basicQueryPojo.mSourceHub != "Select City") {
            bundle.putString("source", basicQueryPojo.mSourceHub)
            source = basicQueryPojo.mSourceHub.toUpperCase()
            textViewSource.setTextColor(ContextCompat.getColor(context, R.color.blue_grey_900))
        } else {
            bundle.putString("source", "Empty")
            textViewSource.setTextColor(ContextCompat.getColor(context, R.color.app_version))
        }

        var destination = "ANYWHERE"
        if (!basicQueryPojo.mDestinationHub.isEmpty() && basicQueryPojo.mDestinationHub != "Select City") {
            bundle.putString("destination", basicQueryPojo.mDestinationHub)
            destination = basicQueryPojo.mDestinationHub.toUpperCase()
            textViewDestination.setTextColor(ContextCompat.getColor(context, R.color.blue_grey_900))
        } else {
            bundle.putString("destination", "Empty")
            textViewDestination.setTextColor(ContextCompat.getColor(context, R.color.app_version))
        }

        var baseQuery: Query = FirebaseFirestore.getInstance()
                .collection("denormalised")
                .document("routes")
                .collection(source)
                .document(destination)
                .collection("companies")

        var numberofFleets: Int = 0

        var fleetssorter = ""
        var list = basicQueryPojo.mFleets!!
        list.sort()
        Logger.v("Selected Fleets : $list")
        for (fleet in list) {
            fleetssorter = fleetssorter + fleet + "_"
            numberofFleets++
        }
        Logger.v("mFleetsSorter: $fleetssorter")
        bundle.putInt("fleetsselected", numberofFleets)
        firebaseAnalytics.logEvent("z_set_main_adapter", bundle)

        val props = JSONObject()
        try {
            props.put("source", source)
            props.put("destination", destination)
            props.put("fleets", fleetssorter)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        mixpanelAPI.track(MixPanelConstants.EVENT_SET_DIRECTORY_ADAPTER, props)
        mainPageAction("search")

        //fitler and sort
        baseQuery = baseQuery.orderBy("mDetails.mProfileType", Query.Direction.DESCENDING)
        baseQuery = baseQuery.whereGreaterThanOrEqualTo("mDetails.mProfileType","1")
        baseQuery = baseQuery.whereEqualTo("mDetails.isSpammed", false)
        baseQuery = baseQuery.whereArrayContains("mDetails.mFleetsSort",fleetssorter)
        baseQuery = baseQuery.orderBy("mBidValue",Query.Direction.DESCENDING)
        baseQuery = baseQuery.orderBy("mDetails.isActive",Query.Direction.DESCENDING)
        baseQuery =  baseQuery.orderBy("mDetails.mLastActive", Query.Direction.DESCENDING)
        baseQuery = baseQuery.orderBy("mDetails.mAvgRating",Query.Direction.DESCENDING)



        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(1)
                .setPageSize(4)
                .build()

        val options = FirestorePagingOptions.Builder<CompanyCardPojo>()
                .setLifecycleOwner(this)
                .setQuery(baseQuery, config, CompanyCardPojo::class.java)
                .build()
        adapter = object : FirestorePagingAdapter<CompanyCardPojo, PartnersViewHolder>(options) {

            @NonNull
            override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): PartnersViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_new_transporter, parent, false)
                recyclerViewAnimator.onCreateViewHolder(view)
                return PartnersViewHolder(view)
            }

            override fun onBindViewHolder(@NonNull holder: PartnersViewHolder,
                                          position: Int,
                                          @NonNull model: CompanyCardPojo) {

                if (model != null) {
                    recyclerViewAnimator.onBindViewHolder(holder.itemView, position)

                    //Photo
                    if (model.getmDetails().getmPhotoUrl() != null) {
                        if (!model.getmDetails().getmPhotoUrl().isEmpty()) {
                            Picasso.with(applicationContext)
                                    .load(model.getmDetails().getmPhotoUrl() + "?width=100&width=100")
                                    .placeholder(ContextCompat.getDrawable(applicationContext, R.mipmap.ic_launcher_round))
                                    .transform(CircleTransform())
                                    .fit()
                                    .into(holder.mThumbnail, object : Callback {

                                        override fun onSuccess() {
                                            Logger.v("image set: transporter thumb")
                                        }

                                        override fun onError() {
                                            Logger.v("image transporter Error")
                                        }
                                    })
                        }

                    } else {
                        holder.mThumbnail.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.emoji_google_category_travel))
                    }

                    //CompName
                    if (model.getmDetails().getmCompanyName() != null) {
                        if (!model.getmDetails().getmCompanyName().isEmpty()) {
                            holder.mCompany.text = textUtils.toTitleCase(model.getmDetails().getmCompanyName())
                        } else {
                            if (model.getmDetails().getmDisplayName() != null) {
                                if (!model.getmDetails().getmDisplayName().isEmpty()) {
                                    holder.mCompany.text = model.getmDetails().getmDisplayName()
                                } else {
                                    holder.mCompany.text = "Unknown Name"
                                }
                            }
                        }
                    } else {
                        if (model.getmDetails().getmDisplayName() != null) {
                            if (!model.getmDetails().getmDisplayName().isEmpty()) {
                                holder.mCompany.text = model.getmDetails().getmDisplayName()
                            } else {
                                holder.mCompany.text = "Unknown Name"
                            }
                        }
                    }


                    //City
                    if (model.getmDetails().getmLocationCity() != null) {
                        if (model.getmDetails().getmLocationCity().isNotEmpty()) {
                            holder.mAddress.text = textUtils.toTitleCase(model.getmDetails().getmLocationCity())
                        }
                    }

                    //Role
                    if (model.getmDetails().getmProfileType() != null) {
                        if (model.getmDetails().getmProfileType().isNotEmpty()) {
                            var type = ""
                            if(model.getmDetails().getmProfileType()=="0"||model.getmDetails().getmProfileType()=="0.5"){
                                type = "LOAD PROVIDER"
                            }
                            if(model.getmDetails().getmProfileType()=="2"||model.getmDetails().getmProfileType()=="2.5"){
                                type = "FLEET PROVIDER"
                            }
                            holder.mRole.text = type
                        }
                    }

                    //Premium
                    if (model.getmDetails().getmProfileType() == "0.5" ||
                            model.getmDetails().getmProfileType() == "1.5"||
                            model.getmDetails().getmProfileType() == "2.5" ) {
                        holder.mIsSuper.visibility = View.VISIBLE
                        holder.mActionsLayout.setBackgroundColor(ContextCompat.getColor(applicationContext,R.color.orange_50))
                    }else{
                        holder.mIsSuper.visibility = View.GONE
                        holder.mActionsLayout.setBackgroundColor(ContextCompat.getColor(applicationContext,R.color.browser_actions_bg_grey))

                    }





//                    if(model.getmAccountStatus()!=null){
//                        if(model.getmAccountStatus()>=2){
//                            holder.mThumbnail.background = ContextCompat.getDrawable(context,R.drawable.border_sreoke_yollo_bg)
//                        }else{
//                            holder.mThumbnail.background = ContextCompat.getDrawable(context,R.drawable.border_stroke_bg)
//                        }
//                    }

                    if (model.getmDetails().isActive != null) {
                        if (model.getmDetails().isActive) {
                            Logger.v("active..")
                            holder.mOnlineStatus.setColorFilter(ContextCompat.getColor(context, R.color.green_A200), android.graphics.PorterDuff.Mode.SRC_IN)
                        } else {
                            Logger.v("inactive..")
                            holder.mOnlineStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_panorama_fish_eye_black_24dp))
                        }
                    } else {
                        Logger.v("null..")
                        holder.mOnlineStatus.setColorFilter(ContextCompat.getColor(context, R.color.gray2), android.graphics.PorterDuff.Mode.SRC_IN)

                    }

                    if (model.getmDetails().getmAvgRating() != null) {
                        if (model.getmDetails().getmAvgRating().toInt() == 0) {
                            holder.mRatings.text = "New"
                        } else {
                            holder.mRatings.text = model.getmDetails().getmAvgRating().toBigDecimal().setScale(1, RoundingMode.UP).toString()
                        }
                    } else {
                        holder.mRatings.text = "New"
                    }

                    if (model.getmDetails().getmNumRatings() != null) {
                        holder.mReviews.text = model.getmDetails().getmNumRatings().toInt().toString() + " reviews"
                    }

                    if (model.getmBidValue() != null) {
                        if (model.getmBidValue() != 0.0) {
                            holder.mIsPromoted.visibility = View.VISIBLE
                        } else {
                            holder.mIsPromoted.visibility = View.GONE
                        }
                    }



                    holder.itemView.setOnClickListener {

                        val i = Intent(context, CompanyProfileDisplayActivity::class.java)
                        i.putExtra("uid", getItem(position)!!.id)
                        i.putExtra("rmn", model.getmDetails().getmRMN())
                        i.putExtra("fuid", model.getmDetails().getmFUID())
                        startActivity(i)

                    }

                    holder.mCall.setOnClickListener {

                        val interactionPojo = InteractionPojo(preferenceManager.userId,
                                preferenceManager.fuid,
                                preferenceManager.rmn,
                                preferenceManager.comapanyName, preferenceManager.displayName,
                                preferenceManager.fcmToken,
                                model.getmDetails().getmUID(),
                                model.getmDetails().getmFUID(),model.getmDetails().getmRMN(),model.getmDetails().getmCompanyName(),model.getmDetails().getmDisplayName(),model.getmDetails().getmFcmToken())


                        FirebaseFirestore.getInstance().collection("partners")
                                .document(model.getmDetails().getmUID()).collection("mCallsDump").document(getDateString())
                                .collection("interactors").document(preferenceManager.userId).set(interactionPojo)

                        FirebaseFirestore.getInstance().collection("partners")
                                .document(model.getmDetails().getmUID())
                                .collection("mCalls").document(preferenceManager.userId).set(interactionPojo).addOnCompleteListener {
                                    FirebaseFirestore.getInstance().collection("partners")
                                            .document(preferenceManager.userId)
                                            .collection("mCalls").document((model.getmDetails().getmUID())).set(interactionPojo)
                                }


                        if (model.getmDetails().getmRMN() != null) {
                            callNumber(model.getmDetails().getmRMN())
                        } else {
                            Toast.makeText(context, "No RMN, Visit Profile!", Toast.LENGTH_SHORT).show()
                        }

                        val bundle = Bundle()

                        if (!basicQueryPojo.mSourceHub.isEmpty() &&
                                basicQueryPojo.mSourceHub != "Select City" &&
                                !basicQueryPojo.mDestinationHub.isEmpty() &&
                                basicQueryPojo.mDestinationHub != "Select City") {
                            bundle.putString("is_route_queried", "Yes")
                        } else {
                            bundle.putString("is_route_queried", "No")
                        }

                        bundle.putInt("fleets_queried", basicQueryPojo.mFleets!!.size)

                        firebaseAnalytics.logEvent("z_call_clicked_pl", bundle)

                        val rateReminderPojo = RateReminderPojo(model.getmDetails().getmCompanyName(),
                                model.getmDetails().getmDisplayName(),
                                model.getmDetails().getmRMN(),
                                model.getmDetails().getmUID(),
                                model.getmDetails().getmFUID(),
                                "call",
                                Date().time.toString(),
                                model.getmDetails().getmAvgRating(), true)


                        if(model.getmDetails().getmAvgRating()!=null){
                            if(!model.getmDetails().getmAvgRating().isNaN())
                                preferenceManager.prefRateReminder = Gson().toJson(rateReminderPojo)
                        }


                    }

                    holder.mChatParent.setOnClickListener {

                        val intent = Intent(context, ChatRoomActivity::class.java)
                        intent.putExtra("imsg", basicQueryPojo.toString())
                        intent.putExtra("ormn", model.getmDetails().getmRMN())
                        intent.putExtra("ouid", getItem(position)!!.id)
                        intent.putExtra("ofuid", model.getmDetails().getmFUID())
                        Logger.v("Ofuid :" + model.getmDetails().getmFUID())
                        startActivity(intent)

                        val bundle = Bundle()
                        if (preferenceManager.rmn != null) {
                            bundle.putString("by_rmn", preferenceManager.rmn)
                        } else {
                            bundle.putString("by_rmn", "Unknown")
                        }
                        bundle.putString("to_rmn", model.getmDetails().getmRMN())
                        if (model.getmDetails().getmFUID() != null) {
                            bundle.putString("is_opponent_updated", "Yes")
                        } else {
                            bundle.putString("is_opponent_updated", "No")
                        }
                        if (!basicQueryPojo.mSourceCity.isEmpty() &&
                                basicQueryPojo.mSourceCity != "Select City" &&
                                !basicQueryPojo.mDestinationCity.isEmpty() &&
                                basicQueryPojo.mDestinationCity != "Select City") {
                            bundle.putString("is_route_queried", "Yes")
                        } else {
                            bundle.putString("is_route_queried", "No")
                        }
                        bundle.putInt("fleets_queried", basicQueryPojo.mFleets!!.size)
                        firebaseAnalytics.logEvent("z_chat_clicked_pl", bundle)


                    }
                }


            }

            override fun onLoadingStateChanged(state: LoadingState) {
                when (state) {

                    LoadingState.LOADING_INITIAL -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                        loading.visibility = View.VISIBLE
                        showall.visibility = View.GONE
                        noresult.visibility = View.GONE

                    }

                    LoadingState.LOADING_MORE -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                    }

                    LoadingState.LOADED -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                        loading.visibility = View.GONE
                        showall.visibility = View.VISIBLE
                        noresult.visibility = View.GONE

                        val dPojo = DirectorySearchPojo(basicQueryPojo.mSourceCity
                                ,basicQueryPojo.mDestinationCity
                                ,basicQueryPojo.mSourceHub
                                ,basicQueryPojo.mDestinationHub)

                        if(dPojo.getmSourceCity().isNotEmpty()&&dPojo.getmDestinationCity().isNotEmpty()){
                            FirebaseFirestore.getInstance().collection("partners")
                                    .document(preferenceManager.userId)
                                    .collection("mDirectoryUsage")
                                    .document("${dPojo.getmSourceHub().toUpperCase()}_${dPojo.getmDestinationHub().toUpperCase()}").set(dPojo).addOnCompleteListener {
                                        Logger.v("Collected in directory usage")
                                        FirebaseMessaging.getInstance().subscribeToTopic("${dPojo.getmSourceHub()
                                                .replace(" ","")
                                                .toUpperCase()}_${dPojo.getmDestinationHub()
                                                .replace(" ","")
                                                .toUpperCase()}")
                                    }

                        }

                    }
                    LoadingState.FINISHED -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                        loading.visibility = View.GONE
                        if (itemCount == 0) {
                            //no result
                            val bundle = Bundle()
                            firebaseAnalytics.logEvent("z_no_result", bundle)
                            mainPageAction("no_result")

                            noresult.visibility = View.VISIBLE
                            showall.visibility = View.GONE
                            posttoselected2.visibility = View.GONE

                        } else {
                            noresult.visibility = View.GONE
                            showall.visibility = View.VISIBLE
                            posttoselected2.visibility = View.VISIBLE

                            if (itemCount < 12) {
                                showall.visibility = View.GONE
                            }








                        }
                    }

                    LoadingState.ERROR -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                    }

                }
            }
        }

        rv_transporters_att.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rv_transporters_att.adapter = adapter


    }

    private fun getDateString(): String {
        return SimpleDateFormat("dd-MM-yyyy").format(Date())
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {

                PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE -> {
                    if (data != null) {
                        val place = PlaceAutocomplete.getPlace(context, data)

                        basicQueryPojo.mSourceCity = place.name.toString()
                        textViewSource.text = ". ${basicQueryPojo.mSourceCity}"
//                        fabFlip.visibility = View.VISIBLE
//                        fabClear.visibility = View.VISIBLE

//                    basicQueryPojo.mSourceCity = place.name.toString()
//                    setMainAdapter(basicQueryPojo)

                        mSourceRouteCityPojo.setmLatLang(place.latLng)
                        loading.visibility = View.VISIBLE
                        val bundle = Bundle()
                        bundle.putString("source", place.name.toString())
                        firebaseAnalytics.logEvent("z_source_city", bundle)
                    } else {
                        Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show()
                    }


                }
                PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION -> {
                    if (data != null) {
                        val place = PlaceAutocomplete.getPlace(context, data)

                        basicQueryPojo.mDestinationCity = place.name.toString()
                        textViewDestination.text = ". ${basicQueryPojo.mDestinationCity}"
//                        fabFlip.visibility = View.VISIBLE
//                        fabClear.visibility = View.VISIBLE

//                    basicQueryPojo.mDestinationCity = place.name.toString()
//                    setMainAdapter(basicQueryPojo)

                        mDestinationRouteCityPojo.setmLatLang(place.latLng)
                        loading.visibility = View.VISIBLE
                        val bundle = Bundle()
                        bundle.putString("destination", place.name.toString())
                        firebaseAnalytics.logEvent("z_destination_city", bundle)
                    } else {
                        Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show()
                    }


                }

            }
        } else {
            when (requestCode) {

                3 -> {
                    val i = Intent(this, NewSplashActivity::class.java)
                    startActivity(i)
                    finishAffinity()
                }
            }

        }
    }

    override fun onDestinationHubFetched(destinationhub: String, operaion: Int) {
        basicQueryPojo.mDestinationHub = destinationhub
        setMainAdapter(basicQueryPojo)
    }

    override fun onSourceHubFetched(sourcehub: String, operation: Int) {
        basicQueryPojo.mSourceHub = sourcehub
        setMainAdapter(basicQueryPojo)
    }

    private fun setRoutePickup() {

        lin_source.setOnClickListener {
            //            Toast.makeText(context,"Select Source City",Toast.LENGTH_LONG).show()
            starttheplacesfragment(PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE)
        }

        lin_des.setOnClickListener {
            //            Toast.makeText(context,"Select Destination City",Toast.LENGTH_LONG).show()
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
        val dest: String = basicQueryPojo.mDestinationCity
        basicQueryPojo.mDestinationCity = basicQueryPojo.mSourceCity
        basicQueryPojo.mSourceCity = dest
        setMainAdapter(basicQueryPojo)
        val bundle = Bundle()
        firebaseAnalytics.logEvent("z_route_flip", bundle)
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
                val bundle = Bundle()
                bundle.putString("fleet", mFleetName)
                firebaseAnalytics.logEvent("z_fleet_selected", bundle)
                val props = JSONObject()
                try {
                    props.put("fleet_selected", mFleetName)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                mixpanelAPI.track(MixPanelConstants.EVENT_REQUIRED_FLEETS, props)

            }

            override fun onFleetDeslected(mFleetName: String) {
                if (basicQueryPojo.mFleets != null) {
                    if (basicQueryPojo.mFleets!!.contains(mFleetName)) {
                        basicQueryPojo.mFleets!!.remove(mFleetName)
                        setMainAdapter(basicQueryPojo)
                        val bundle = Bundle()
                        bundle.putString("fleet", mFleetName)
                        firebaseAnalytics.logEvent("z_fleet_deselected", bundle)
                        val props = JSONObject()
                        try {
                            props.put("fleet_deselected", mFleetName)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        mixpanelAPI.track(MixPanelConstants.EVENT_REQUIRED_FLEETS, props)
                    }
                }
            }

        }, 0)
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
            val bundle = Bundle()
            bundle.putInt("for", code)
            firebaseAnalytics.logEvent("z_route_pick", bundle)
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



    /**
     * This method is use for checking internet connectivity
     * If there is no internet it will show an snackbar to user
     */
    private fun internetCheck() {
        NoNet.monitor(this)
                .poll()
                .snackbar()
    }



    private fun mainPageAction(action: String) {
        val props = JSONObject()
        try {
            props.put("action", action)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        mixpanelAPI.track(MixPanelConstants.EVENT_MAIN_PAGE_ACTION, props)
    }
}
