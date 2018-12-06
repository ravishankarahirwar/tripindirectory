package directory.tripin.com.tripindirectory.newprofiles.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.text.Html
import android.text.util.Linkify
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.adapters.CapsulsRecyclarAdapter
import directory.tripin.com.tripindirectory.helper.CircleTransform
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import directory.tripin.com.tripindirectory.newlookcode.FleetSelectPojo
import directory.tripin.com.tripindirectory.newlookcode.FleetsSelectAdapter
import directory.tripin.com.tripindirectory.newlookcode.OnFleetSelectedListner
import directory.tripin.com.tripindirectory.newprofiles.OperatorsAdapter
import kotlinx.android.synthetic.main.activity_company_profile_display.*
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import directory.tripin.com.tripindirectory.newprofiles.CompanyRatingsPojo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.gson.Gson
import com.keiferstone.nonet.NoNet
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.stepstone.apprating.AppRatingDialog
import com.stepstone.apprating.listener.RatingDialogListener
import directory.tripin.com.tripindirectory.chatingactivities.ChatRoomActivity
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo
import directory.tripin.com.tripindirectory.newlookcode.pojos.InteractionPojo
import directory.tripin.com.tripindirectory.newlookcode.utils.MixPanelConstants
import directory.tripin.com.tripindirectory.newprofiles.models.ConnectPojo
import directory.tripin.com.tripindirectory.newprofiles.models.RateReminderPojo
import directory.tripin.com.tripindirectory.utils.TextUtils
import kotlinx.android.synthetic.main.company_hits_item.*
import libs.mjn.prettydialog.PrettyDialog
import libs.mjn.prettydialog.PrettyDialogCallback
import org.json.JSONException
import org.json.JSONObject
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*


class CompanyProfileDisplayActivity : AppCompatActivity(), RatingDialogListener {


    lateinit var preferenceManager: PreferenceManager
    lateinit var context: Context
    val fleets: ArrayList<FleetSelectPojo> = ArrayList()
    val cities: ArrayList<String> = ArrayList()
    lateinit var mAuth: FirebaseAuth
    lateinit var partnerInfoPojo: PartnerInfoPojo
    lateinit var firebaseAnalytics: FirebaseAnalytics


    var mCompUid: String = ""
    var mCompRmn: String = ""
    var mCompFuid: String = ""
    var mCompName: String = ""
    var mCompPhotourl: String = ""


    var isDirectRateShown = false

    lateinit var textUtils: TextUtils
    var isConnected: Boolean = false
    var mRatingsPojo: CompanyRatingsPojo? = null
    lateinit var simpleDateFormat: SimpleDateFormat
    lateinit var mixpanelAPI: MixpanelAPI


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_profile_display)
        context = this
        preferenceManager = PreferenceManager.getInstance(context)
        mAuth = FirebaseAuth.getInstance()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mixpanelAPI = MixpanelAPI.getInstance(context, MixPanelConstants.MIXPANEL_TOKEN)
        textUtils = TextUtils()
        if (mAuth.currentUser == null) {
            finish()
        }
        simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

        setSelectFleetAdapter()
        setCitiesAdapter()
        setListners()
        internetCheck()
//        setOperatorAdapter()


    }

    override fun onResume() {
        super.onResume()
        getIntentData()

    }

    private fun getIntentData() {

        if (intent.extras != null) {
            if (intent.extras.getString("uid") != null && intent.extras.getString("rmn") != null) {
                if (!intent.extras.getString("uid").isEmpty() && !intent.extras.getString("rmn").isEmpty()) {
                    //get uid
                    mCompUid = intent.extras.getString("uid")
                    mCompRmn = intent.extras.getString("rmn")
                    if (intent.extras.getString("fuid") != null) {
                        if (!intent.extras.getString("fuid").isEmpty()) {
                            mCompFuid = intent.extras.getString("fuid")
                        }
                    }

                    if (intent.extras.getBoolean("fromchat") != null) {
                        if (intent.extras.getBoolean("fromchat")) {
                            chatwithcomp.visibility = View.INVISIBLE
                        }
                    }


                } else {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        if (mCompUid.isEmpty()) {
            finish()
            Toast.makeText(context, "Try Again!", Toast.LENGTH_SHORT).show()
        } else {

            //Profile Analytics: Profile visit
            val interactionPojo = InteractionPojo(preferenceManager.userId,
                    preferenceManager.fuid,
                    preferenceManager.rmn,
                    preferenceManager.comapanyName, preferenceManager.displayName,
                    preferenceManager.fcmToken, mCompUid, mCompFuid, mCompRmn, "", "", "")

            FirebaseFirestore.getInstance().collection("partners")
                    .document(mCompUid).collection("mProfileVisits").document(getDateString())
                    .collection("interactors").document(preferenceManager.userId).set(interactionPojo)

            adjustIfYourProfile()
        }


    }

    private fun getDateString(): String {
        return SimpleDateFormat("dd-MM-yyyy").format(Date())
    }

    private fun fetchData(mCompUid: String) {

        mainscrollprofile.visibility = View.GONE
        comptitle.text = "Company Details Loading..."
        FirebaseFirestore.getInstance()
                .collection("partners")
                .document(mCompUid)
                .addSnapshotListener(this, EventListener<DocumentSnapshot> { snapshot, e ->
                    if (e != null) {
                        finish()
                        Toast.makeText(context, "Error, Try Again!", Toast.LENGTH_SHORT).show()
                        return@EventListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        partnerInfoPojo = snapshot.toObject(PartnerInfoPojo::class.java)!!
                        bindData(partnerInfoPojo)

                    } else {
                        if (mCompUid.equals(preferenceManager.userId)) {
                            adjustIfYourProfile()
                        } else {
                            Toast.makeText(context, "Not Available", Toast.LENGTH_SHORT).show()
                            comptitle.text = "Not Available"
                        }
                        Logger.v("Current data: null")
                    }
                })
        fetchConnections()
        setRatingsAdapter()

    }

    private fun fetchConnections() {
        FirebaseFirestore.getInstance()
                .collection("partners")
                .document(mCompUid)
                .collection("mConnections")
                .addSnapshotListener(this, EventListener<QuerySnapshot> { snapshot, e ->
                    if (e != null) {
                        finish()
                        Toast.makeText(context, "Error, Try Again!", Toast.LENGTH_SHORT).show()
                        return@EventListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        var connections: Int = 0
                        snapshot.forEach {
                            val connectPojo: ConnectPojo = it.toObject(ConnectPojo::class.java)
                            if (connectPojo.getmStatus() == true) {
                                connections++
                            }
                            if (connectPojo.getmUid() == preferenceManager.userId) {
                                if (connectPojo.getmStatus()) {
                                    //You are Connected
                                    isConnected = true
                                    connect.text = "Disconnect"

                                    val bottom = connect.paddingBottom
                                    val top = connect.paddingTop
                                    val right = connect.paddingRight
                                    val left = connect.paddingLeft
                                    connect.background = ContextCompat.getDrawable(context, R.drawable.border_sreoke_orange_bg)
                                    connect.setPadding(left, top, right, bottom)


                                } else {
                                    //You are Diconnected
                                    isConnected = false
                                    connect.text = "Connect"
                                    val bottom = connect.paddingBottom
                                    val top = connect.paddingTop
                                    val right = connect.paddingRight
                                    val left = connect.paddingLeft
                                    connect.background = ContextCompat.getDrawable(context, R.drawable.round_gradient_orange_bg)
                                    connect.setPadding(left, top, right, bottom)

                                }
                            }
                        }
                        innet.text = connections.toString()

                    } else {
                        innet.text = "0"
                    }
                })
    }

//    private fun fetchRatings() {
//        FirebaseFirestore.getInstance().collection("partners").document(mCompUid).collection("mRatings").addSnapshotListener(this, EventListener<QuerySnapshot> { snapshot, e ->
//            if (e != null) {
//                finish()
//                Toast.makeText(context, "Error, Try Again!", Toast.LENGTH_SHORT).show()
//                return@EventListener
//            }
//            if (snapshot != null && !snapshot.isEmpty) {
//                var totalrate: Double = 0.0
//
//                snapshot.forEach {
//
//                    val ratingsPojo: CompanyRatingsPojo = it.toObject(CompanyRatingsPojo::class.java)
//
//                    totalrate += ratingsPojo.getmRitings()
//
//                    if (ratingsPojo.getmUid() == preferenceManager.userId) {
//                        mRatingsPojo = ratingsPojo
//                    }
//
//                }
//                if (snapshot.size() != 0) {
//                    ratings.text = (totalrate.div(snapshot.size())).toString()
//                } else {
//                    ratings.text = "0.0"
//                }
//
//                setRatingsAdapter()
//
//
//            } else {
//                ratings.text = "0.0"
//                Toast.makeText(context, "No Ratings Yet!", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }

    private fun bindData(partnerInfoPojo: PartnerInfoPojo?) {

        if (partnerInfoPojo != null) {

            //Company Image

            if (partnerInfoPojo.getmPhotoUrl() != null) {
                setUpImage(partnerInfoPojo.getmPhotoUrl())
                mCompPhotourl = partnerInfoPojo.getmPhotoUrl()
            }

            //is spammed
            if(partnerInfoPojo.isSpammed){
                blockedui.visibility = View.VISIBLE
                Logger.v("spammed")
                if(mCompUid==preferenceManager.userId){
                    unblockrequest.visibility = View.VISIBLE
                }else{
                    unblockrequest.visibility = View.GONE
                }

            }else{
                blockedui.visibility = View.GONE
                unblockrequest.visibility = View.GONE
                Logger.v("not spammed")
            }

            //status statusindicator

            if (partnerInfoPojo.isActive != null) {
                if (partnerInfoPojo.isActive) {
                    Logger.v("active..")
                    statusindicator.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_brightness_1_black_24dp))
                    statusindicator.setColorFilter(ContextCompat.getColor(context, R.color.green_A200), android.graphics.PorterDuff.Mode.SRC_IN)
                } else {
                    Logger.v("inactive..")
                    statusindicator.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_panorama_fish_eye_black_24dp))
                }
            } else {
                Logger.v("null..")
                statusindicator.setColorFilter(ContextCompat.getColor(context, R.color.gray2), android.graphics.PorterDuff.Mode.SRC_IN)

            }

            //Company Name
            if (partnerInfoPojo.getmCompanyName() != null) {
                if (!partnerInfoPojo.getmCompanyName().isEmpty()) {
                    compname.text = partnerInfoPojo.getmCompanyName().toUpperCase()
                    comptitle.text = textUtils.toTitleCase(partnerInfoPojo.getmCompanyName())
                    mCompName = partnerInfoPojo.getmCompanyName()

                }
            } else {
                Toast.makeText(context, "Not Available", Toast.LENGTH_SHORT).show()
                finish()
            }

            //city
            if (partnerInfoPojo.getmCity() != null) {
                city.text = partnerInfoPojo.getmCity()
            }

            //bio
            if (partnerInfoPojo.getmBio() != null) {
                biodisplay.text = partnerInfoPojo.getmBio()
                Linkify.addLinks(biodisplay, Linkify.ALL)

            }

            if (partnerInfoPojo.getmOperationCities() != null) {
                if (partnerInfoPojo.getmOperationCities().size > 0) {
                    addCities(partnerInfoPojo.getmOperationCities())
                    operators.text = partnerInfoPojo.getmOperationCities().size.toString()
                } else {
                    val nocity: ArrayList<String> = ArrayList()
                    nocity.add("No Cities Added Yet")
                    addCities(nocity)
                    operators.text = "0"
                }
            } else {
                val nocity: ArrayList<String> = ArrayList()
                nocity.add("No Cities Added Yet")
                addCities(nocity)
                operators.text = "0"
            }

            if (partnerInfoPojo.fleetVehicle != null) {
                addFleets(partnerInfoPojo.fleetVehicle)
            }

            mainscrollprofile.visibility = View.VISIBLE

            FetchProfileVisits()


        }

    }

    private fun FetchProfileVisits() {

        val oldDate = Date(Date().time - 604800000L)
        FirebaseFirestore.getInstance()
                .collection("partners")
                .document(mCompUid)
                .collection("mProfileVisits").orderBy("mDate",Query.Direction.DESCENDING).limit(7)
                .addSnapshotListener(this, EventListener<QuerySnapshot> { snapshot, e ->

                    if (e != null) {
                        finish()
                        Logger.v(e.toString())
                        Toast.makeText(context, "Error, Try Again!", Toast.LENGTH_SHORT).show()
                        return@EventListener
                    }

                    if (snapshot != null) {

                        var visits: Long = 0

                        snapshot.forEach {
                            if (it.id.length == 10) {
                                val count: Long = it.getLong("mNumVisits")!!
                                visits += count
                                Logger.v(visits.toString())
                            }
                        }

                        val s = "<b>$visits</b> profile visits in last 7 active days"
                        visits_text.text = Html.fromHtml(s)

                        if(snapshot.size()<7){
                            insightll.visibility = View.GONE
                        }

                    } else {

                        visits_text.text = "No profile visits yet!"

                    }
                })

    }

    private fun adjustIfYourProfile() {

        if (mCompUid == preferenceManager.userId) {
            //your profile
            if (preferenceManager.comapanyName != null) {

                connect.visibility = View.GONE
                rate.visibility = View.GONE
                editprofile.visibility = View.VISIBLE
                promote.visibility = View.VISIBLE
                fetchData(mCompUid)
            } else {
                val i = Intent(this, CompanyProfileEditActivity::class.java)
                startActivityForResult(i, 1)
            }

        } else {
            //others profile
            connect.visibility = View.VISIBLE
            rate.visibility = View.VISIBLE
            editprofile.visibility = View.GONE
            promote.visibility = View.GONE
            fetchData(mCompUid)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                //saved successfully
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
            } else {
                //back from edit activity
                if (preferenceManager.comapanyName == null) {
                    finish()
                    Toast.makeText(context, "Canceled Creation", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Canceled Edit", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun setListners() {

        rate.setOnClickListener {
            showDialog()
        }

        back_fromdisplay.setOnClickListener {
            finish()
        }

        editprofile.setOnClickListener {
            val i = Intent(this, CompanyProfileEditActivity::class.java)
            startActivityForResult(i, 1)
        }

        promote.setOnClickListener {
            showpromotedialog()
            val bundle = Bundle()
            firebaseAnalytics.logEvent("z_promote_clicked", bundle)
        }

        connect.setOnClickListener {

            var collection = FirebaseFirestore.getInstance()
                    .collection("partners")
                    .document(mCompUid)
                    .collection("mConnections")

            val bundle = Bundle()

            if (isConnected) {

                val connectPojo = ConnectPojo(preferenceManager.userId,
                        false,
                        preferenceManager.displayName,
                        preferenceManager.imageUrl,
                        preferenceManager.rmn,
                        preferenceManager.fuid,
                        preferenceManager.comapanyName)

                collection.document(preferenceManager.userId).set(connectPojo).addOnCompleteListener {

                    var mycollection = FirebaseFirestore.getInstance()
                            .collection("networks")
                            .document(preferenceManager.userId)
                            .collection("mNetwork")
                    val connectPojo2 = ConnectPojo(mCompUid,
                            false,
                            partnerInfoPojo.getmDisplayName(),
                            partnerInfoPojo.getmPhotoUrl(),
                            partnerInfoPojo.getmRMN(),
                            partnerInfoPojo.getmFUID(),
                            partnerInfoPojo.getmCompanyName()
                    )
                    mycollection.document(partnerInfoPojo.getmRMN()).set(connectPojo2).addOnCompleteListener {
                        Toast.makeText(context, "Removed from Your Network", Toast.LENGTH_SHORT).show()
                        bundle.putInt("connect_status", 0)
                        firebaseAnalytics.logEvent("z_profile_connected", bundle)
                        mixpanelConnect("Connected")

                    }
                }

            } else {

                val connectPojo = ConnectPojo(preferenceManager.userId,
                        true,
                        preferenceManager.displayName,
                        preferenceManager.imageUrl,
                        preferenceManager.rmn,
                        preferenceManager.fuid,
                        preferenceManager.comapanyName)

                collection.document(preferenceManager.userId).set(connectPojo).addOnCompleteListener {

                    var mycollection = FirebaseFirestore.getInstance()
                            .collection("networks")
                            .document(preferenceManager.userId)
                            .collection("mNetwork")
                    val connectPojo2 = ConnectPojo(mCompUid,
                            true,
                            partnerInfoPojo.getmDisplayName(),
                            partnerInfoPojo.getmPhotoUrl(),
                            partnerInfoPojo.getmRMN(),
                            partnerInfoPojo.getmFUID(),
                            partnerInfoPojo.getmCompanyName()
                    )
                    mycollection.document(partnerInfoPojo.getmRMN()).set(connectPojo2).addOnCompleteListener {
                        Toast.makeText(context, "Added to your Network", Toast.LENGTH_SHORT).show()
                        bundle.putInt("connect_status", 1)
                        firebaseAnalytics.logEvent("z_profile_connected", bundle)
                        mixpanelConnect("Disconnected")

                    }
                }
            }


        }

        chatwithcomp.setOnClickListener {


            val intent = Intent(context, ChatRoomActivity::class.java)
            intent.putExtra("imsg", "From Your Company Profile")
            intent.putExtra("ormn", mCompRmn)
            intent.putExtra("ouid", mCompUid)
            intent.putExtra("ofuid", mCompFuid)
            startActivity(intent)
        }

        see_insight.setOnClickListener {
            val intent = Intent(context, MainProfileInsightActivity::class.java)
            intent.putExtra("uid",mCompUid)
            intent.putExtra("name",mCompName)
            intent.putExtra("rmn",mCompRmn)
            intent.putExtra("photourl",mCompPhotourl)
            startActivity(intent)
        }

        reportprofile.setOnClickListener {
            val intent = Intent(context, SubmitReportActivity::class.java)
            intent.putExtra("uid",mCompUid)
            intent.putExtra("name",mCompName)
            intent.putExtra("rmn",mCompRmn)
            intent.putExtra("photourl",mCompPhotourl)
            intent.putExtra("fuid",mCompFuid)

            startActivity(intent)

        }

        unblockrequest.setOnClickListener {
            val intent = Intent(this@CompanyProfileDisplayActivity, ChatRoomActivity::class.java)
            intent.putExtra("ormn", "+919284089759")
            intent.putExtra("imsg", "Hi, I am requesting to unblock my profile.")
            intent.putExtra("ouid", "pKeXxKD5HjS09p4pWoUcu8Vwouo1")
            intent.putExtra("ofuid", "4zRHiYyuLMXhsiUqA7ex27VR0Xv1")
            startActivity(intent)
            val bundle = Bundle()
            firebaseAnalytics.logEvent("z_assistant", bundle)
        }
    }

    private fun setUpImage(getmPhotoUrl: String) {
        if (getmPhotoUrl != null) {
            if (!getmPhotoUrl.isEmpty()) {
                Picasso.with(applicationContext)
                        .load("$getmPhotoUrl?width=160&width=160")
                        .placeholder(ContextCompat.getDrawable(applicationContext, R.mipmap.ic_launcher_round))
                        .transform(CircleTransform())
                        .into(compimage, object : Callback {

                            override fun onSuccess() {
                                Logger.v("image set: profile thumb $getmPhotoUrl")
                            }

                            override fun onError() {
                                Logger.v("image profile Error $getmPhotoUrl")
                            }
                        })
            } else {
                compimage.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_launcher_round))
            }

        } else {
            compimage.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_launcher_round))
        }
    }

    private fun setSelectFleetAdapter() {


        // Creates a vertical Layout Manager
        rv_available_fleets.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // You can use GridLayoutManager if you want multiple columns. Enter the number of columns as a parameter.
//        rv_animal_list.layoutManager = GridLayoutManager(this, 2)

        // Access the RecyclerView Adapter and load the data into it
        rv_available_fleets.adapter = FleetsSelectAdapter(fleets, this, object : OnFleetSelectedListner {
            override fun onFleetSelected(mFleetName: String) {

            }

            override fun onFleetDeslected(mFleetName: String) {
            }

        }, 1)
    }

    private fun addFleets(fleetVehicle: MutableMap<String, Boolean>) {

        fleets.clear()
        fleets.add(FleetSelectPojo("LCV", ContextCompat.getDrawable(this, R.mipmap.ic_fleet_lcv_round)!!, false))
        fleets.add(FleetSelectPojo("Truck", ContextCompat.getDrawable(this, R.mipmap.ic_fleet_truckpng_round)!!, false))
        fleets.add(FleetSelectPojo("Tusker", ContextCompat.getDrawable(this, R.mipmap.ic_fleet_tuskerpng_round)!!, false))
        fleets.add(FleetSelectPojo("Taurus", ContextCompat.getDrawable(this, R.mipmap.ic_fleet_tauruspng_round)!!, false))
        fleets.add(FleetSelectPojo("Trailers", ContextCompat.getDrawable(this, R.mipmap.ic_fleet_trailerpng_round)!!, false))

        for (f in fleets) {
            f.isSelected = fleetVehicle[f.name]!!
            Logger.v(f.isSelected.toString())

        }
        rv_available_fleets.adapter.notifyDataSetChanged()

    }

    private fun setOperatorAdapter() {
        // Loads animals into the ArrayList
//        addFleets()

        // Creates a vertical Layout Manager
        rv_operators.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // You can use GridLayoutManager if you want multiple columns. Enter the number of columns as a parameter.
        // rv_animal_list.layoutManager = GridLayoutManager(this, 2)

        // Access the RecyclerView Adapter and load the data into it
        rv_operators.adapter = OperatorsAdapter(fleets, this, object : OnFleetSelectedListner {
            override fun onFleetSelected(mFleetName: String) {
            }

            override fun onFleetDeslected(mFleetName: String) {
            }

        })
    }

    private fun setCitiesAdapter() {


        rv_cities.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL)
        rv_cities.adapter = CapsulsRecyclarAdapter(cities)
    }

    private fun addCities(getmOperationCities: MutableList<String>) {
        cities.clear()
        for (city: String in getmOperationCities) {
            cities.add(city)
        }
        if (cities.isEmpty()) {
            cities.add("No Cities Added Yet")
        }
        rv_cities.adapter.notifyDataSetChanged()
    }

    private fun setRatingsAdapter() {

        val query = FirebaseFirestore.getInstance()
                .collection("partners").document(mCompUid).collection("mRatings")
                .orderBy("serverTimestamp", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<CompanyRatingsPojo>()
                .setQuery(query, CompanyRatingsPojo::class.java)
                .setLifecycleOwner(this)
                .build()

        val adapter: FirestoreRecyclerAdapter<CompanyRatingsPojo, RateHolder>
        adapter = object : FirestoreRecyclerAdapter<CompanyRatingsPojo, RateHolder>(options) {

            override fun onBindViewHolder(holder: RateHolder, position: Int, model: CompanyRatingsPojo) {
                holder.username.text = model.getmUserName()
                holder.comment.text = "\" ${model.getmReview()} \""
                holder.rmn.text = model.getmRMN()
                holder.ratings.text = model.getmRitings().toString()
                if (model.serverTimestamp != null) {
                    holder.timestamp.text = simpleDateFormat.format(model.serverTimestamp)
                } else {
                    holder.timestamp.visibility = View.GONE
                }
                if (model.getmImageUrl() != null) {
                    if (!model.getmImageUrl().isEmpty()) {
                        Picasso.with(applicationContext)
                                .load(model.getmImageUrl() + "?width=100&width=100")
                                .placeholder(ContextCompat.getDrawable(applicationContext, R.mipmap.ic_launcher_round))
                                .transform(CircleTransform())
                                .into(holder.thubmnail, object : Callback {

                                    override fun onSuccess() {
                                        Logger.v("image set: profile thumb")
                                        Logger.v(preferenceManager.imageUrl)
                                    }

                                    override fun onError() {
                                        Logger.v("image profile Error")
                                    }
                                })
                    }

                } else {
                    holder.thubmnail.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_launcher_round))
                }

            }


            override fun onCreateViewHolder(group: ViewGroup, i: Int): RateHolder {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                val view = LayoutInflater.from(group.context)
                        .inflate(R.layout.item_ratings_review, group, false)
                return RateHolder(view)
            }

            override fun onDataChanged() {
                super.onDataChanged()
                var totalrate = 0.0
                for (i in 0..(itemCount - 1)) {


                    val ratingsPojo = getItem(i)

                    totalrate += ratingsPojo.getmRitings()

                    if (ratingsPojo.getmUid() == preferenceManager.userId) {
                        mRatingsPojo = ratingsPojo

                    }

                }
                if (itemCount != 0) {
                    ratings.text = (totalrate.div(itemCount)).toBigDecimal().setScale(1, RoundingMode.UP).toString()
                    reviewstitle.text = " Ratings and Reviews. ($itemCount)"
                } else {
                    ratings.text = "0.0"
                }

                //launch rate dialog
                if (intent.extras != null) {
                    if (intent.extras.getString("action") != null) {
                        if (intent.extras.getString("action") == "direct_rate") {
                            if (!isDirectRateShown) {
                                showDialog()
                                isDirectRateShown = true
                            }
                        }
                    }
                }
            }
        }

        rv_ratingsrecent.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_ratingsrecent.adapter = adapter

    }


    private fun showDialog() {

        var defaultrate: Int
        var defaultcomment: String

        if (mRatingsPojo != null) {
            defaultrate = mRatingsPojo!!.getmRitings().toInt()
            defaultcomment = mRatingsPojo!!.getmReview()
        } else {
            defaultrate = 3
            defaultcomment = ""
        }
        val builder = AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                .setDefaultRating(defaultrate)
                .setTitle("Rate this company")
                .setDescription("Please select some stars and give your feedback")
                .setCommentInputEnabled(true)
                .setStarColor(R.color.yellow_700)
                .setNoteDescriptionTextColor(R.color.blue_grey_900)
                .setTitleTextColor(R.color.blue_grey_900)
                .setDescriptionTextColor(R.color.blue_grey_200)
                .setHint("Please write your comment here ...")
                .setHintTextColor(R.color.blue_grey_200)
                .setCommentTextColor(R.color.blue_grey_900)
                .setCommentBackgroundColor(R.color.brown_50)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)

        if (!defaultcomment.isEmpty()) {
            builder.setDefaultComment(defaultcomment)
        }

        builder.create(this@CompanyProfileDisplayActivity)
                .show()

    }

    override fun onNegativeButtonClicked() {
        //canceled
    }

    override fun onNeutralButtonClicked() {
    }

    override fun onPositiveButtonClicked(rate: Int, comment: String) {
        val companyRatingsPojo = CompanyRatingsPojo(preferenceManager.displayName,
                preferenceManager.imageUrl,
                preferenceManager.rmn,
                preferenceManager.userId,
                rate.toDouble(),
                comment, partnerInfoPojo.getmFcmToken())

        FirebaseFirestore.getInstance()
                .collection("partners")
                .document(mCompUid)
                .collection("mRatings")
                .document(preferenceManager.userId).set(companyRatingsPojo).addOnCompleteListener {
                    val bundle = Bundle()
                    bundle.putInt("rating", rate)
                    firebaseAnalytics.logEvent("z_profile_rated", bundle)
                    mixpanelRate(rate)
                    Toast.makeText(context, "Review Submitted. Thankyou!", Toast.LENGTH_LONG).show()
                    if (!preferenceManager.prefRateReminder.isEmpty()) {
                        val rateReminderPojo = Gson().fromJson(preferenceManager.prefRateReminder, RateReminderPojo::class.java)

                        if (rateReminderPojo.getmIsActive()) {
                            if (rateReminderPojo.getmUID() == mCompUid) {
                                val rateReminderPojoNew = RateReminderPojo(rateReminderPojo.getmCompanyName(),
                                        rateReminderPojo.getmDisplayName(),
                                        rateReminderPojo.getmRMN(),
                                        rateReminderPojo.getmUID(),
                                        rateReminderPojo.getmFUID(),
                                        "call",
                                        rateReminderPojo.getmTimeStamp(),
                                        rateReminderPojo.getmRatings(), false)
                                preferenceManager.prefRateReminder = Gson().toJson(rateReminderPojoNew)

                            }

                        }

                    }


                }

    }

    private fun internetCheck() {
        NoNet.monitor(this)
                .poll()
                .snackbar()
    }

    private fun showpromotedialog() {

        val prettyDialog: PrettyDialog = PrettyDialog(this)

        prettyDialog
                .setTitle("Promote My Business")
                .setMessage("This feature is still in development. For now, You can ask for promotion personally to ILN Assistant.")
                .addButton(
                        "Yes, Talk to Assistant",
                        R.color.pdlg_color_white,
                        R.color.green_400
                ) {
                    prettyDialog.dismiss()
                    chatwithassistant()

                }.addButton(
                        "Cancel",
                        R.color.pdlg_color_white,
                        R.color.blue_grey_100,
                        PrettyDialogCallback {
                            prettyDialog.dismiss()

                        }
                )
        prettyDialog.show()


    }

    private fun chatwithassistant() {
        val intent = Intent(this@CompanyProfileDisplayActivity, ChatRoomActivity::class.java)
        intent.putExtra("ormn", "+919284089759")
        intent.putExtra("imsg", "Hi, I want to know more about my Company Promotion on ILN.")
        intent.putExtra("ouid", "pKeXxKD5HjS09p4pWoUcu8Vwouo1")
        intent.putExtra("ofuid", "4zRHiYyuLMXhsiUqA7ex27VR0Xv1")
        startActivity(intent)
        val bundle = Bundle()
        firebaseAnalytics.logEvent("z_assistant", bundle)
    }

    private fun mixpanelRate(reting: Int) {
        val props = JSONObject()
        try {
            props.put("rating", reting)
            props.put("ormn", partnerInfoPojo.getmRMN())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        mixpanelAPI.track(MixPanelConstants.EVENT_RATE, props)
    }

    private fun mixpanelConnect(action: String) {
        val props = JSONObject()
        try {
            props.put("action", action)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        mixpanelAPI.track(MixPanelConstants.EVENT_CONNECT, props)
    }


}
