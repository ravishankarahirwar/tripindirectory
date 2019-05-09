package directory.tripin.com.tripindirectory.newprofiles.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.keiferstone.nonet.NoNet
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.adapters.CapsulsRecyclarAdapter
import directory.tripin.com.tripindirectory.chatingactivities.ChatRoomActivity
import directory.tripin.com.tripindirectory.chatingactivities.models.UserPresensePojo
import directory.tripin.com.tripindirectory.helper.CircleTransform
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo
import directory.tripin.com.tripindirectory.newlookcode.pojos.FleetSelectPojo
import directory.tripin.com.tripindirectory.newlookcode.FleetsSelectAdapter
import directory.tripin.com.tripindirectory.newlookcode.OnFleetSelectedListner
import directory.tripin.com.tripindirectory.newlookcode.activities.ProfileRoleInputActivity
import directory.tripin.com.tripindirectory.newlookcode.utils.MixPanelConstants
import directory.tripin.com.tripindirectory.newprofiles.OperatorsAdapter
import directory.tripin.com.tripindirectory.newprofiles.models.DenormalizerPojo
import directory.tripin.com.tripindirectory.newprofiles.models.DenormalizerUpdateManager
import kotlinx.android.synthetic.main.activity_company_profile_display.*
import kotlinx.android.synthetic.main.activity_company_profile_edit.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class CompanyProfileEditActivity : LocalizationActivity() {

    /**
     * CompanyProfileEditActivity lets you edit and format the company details visible
     */

    internal var PLACE_AUTOCOMPLETE_REQUEST_CODE = 1
    internal var PLACE_PICKER_REQUEST = 2
    lateinit var mixpanelAPI: MixpanelAPI
    lateinit var preferenceManager: PreferenceManager
    lateinit var context: Context
    lateinit var partnerInfoPojo: PartnerInfoPojo
    val fleets: ArrayList<FleetSelectPojo> = ArrayList()
    val cities: ArrayList<String> = ArrayList()
    lateinit var reference: DocumentReference
    lateinit var db: FirebaseFirestore
    lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var refinedHubs: HashMap<String, Boolean>
    lateinit var denormalizerUpdateManager: DenormalizerUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_profile_edit)
        context = this

        db = FirebaseFirestore.getInstance()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mixpanelAPI = MixpanelAPI.getInstance(context, MixPanelConstants.MIXPANEL_TOKEN)
        preferenceManager = PreferenceManager.getInstance(context)
        partnerInfoPojo = PartnerInfoPojo()


        reference = db.collection("partners")
                .document(preferenceManager.userId)
        setSelectFleetAdapter()
        setCitiesAdapter()
        setListners()
        internetCheck()

    }

    override fun onResume() {
        super.onResume()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        if (preferenceManager.imageUrl != null) {
            setUpImage(preferenceManager.imageUrl)
        }
        if (preferenceManager.profileType == 1L) {
            profiletypecard.visibility = View.GONE
        }
        fetchData()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent()
        setResult(Activity.RESULT_CANCELED, i)
        finish()
        editprofilePageAction("back")
    }


    private fun setListners() {

        back_fromedit.setOnClickListener {
            val i = Intent()
            setResult(Activity.RESULT_CANCELED, i)
            finish()
            editprofilePageAction("back")
        }
        managecities.setOnClickListener {
            val i = Intent(this, ManageCitiesActivity::class.java)
            startActivity(i)
            editprofilePageAction("manage_cities")
        }

        manageoperators.setOnClickListener {
            val i = Intent(this, ManageOperatorsActivity::class.java)
            startActivity(i)
            editprofilePageAction("manage_operators")

        }
        cityedit.setOnClickListener {
            //remove focus
            compnametext.isSelected = false
            bioedit.isSelected = false
            startPickupfragment()
            Toast.makeText(context, "Type City Name", Toast.LENGTH_SHORT).show()
            editprofilePageAction("pick_city")
        }

        changelogo.setOnClickListener {
            Toast.makeText(context, "Feature Coming Soon", Toast.LENGTH_SHORT).show()
            editprofilePageAction("change_logo")
        }

        pinpoint.setOnClickListener {
            startPlacePicker()
            editprofilePageAction("pick_location")
        }

        editprofiletype.setOnClickListener {
            if (partnerInfoPojo.getmProfileType() != null) {
                if (partnerInfoPojo.getmProfileType().equals("0.5")
                        || partnerInfoPojo.getmProfileType().equals("2.5")
                        || partnerInfoPojo.getmProfileType().equals("1.5")) {
                    chatwithassistant()
                } else {
                    val i = Intent(this, ProfileRoleInputActivity::class.java)
                    i.putExtra("isFromEditProfile", true);
                    startActivity(i)
                }
            } else {
                val i = Intent(this, ProfileRoleInputActivity::class.java)
                i.putExtra("isFromEditProfile", true);
                startActivity(i)
            }


        }

        doneeditprofile.setOnClickListener {

            editprofilePageAction("submit_form")

            mainscrolledit.scrollTo(0, 0)
            mixpanelAPI.timeEvent(MixPanelConstants.EVENT_UPLOAD_COMPANY_FORM)

            if (compnametext.text.toString().isEmpty()) {
                Toast.makeText(context, getString(R.string.company_name_cant_be_empty), Toast.LENGTH_SHORT).show()
            } else {
                //Upload
                //save bio, name, timestamp
                saving.visibility = View.VISIBLE
                doneeditprofile.visibility = View.GONE

                val hashMap = HashMap<String, String>()
                hashMap.put("mCompanyName", compnametext.text.toString().toUpperCase().trim())
                hashMap.put("mFUID", preferenceManager.fuid)
                hashMap.put("mDisplayName", preferenceManager.getDisplayName())
                hashMap.put("mPhotoUrl", preferenceManager.imageUrl)
                hashMap.put("mFcmToken", preferenceManager.getFcmToken())
                hashMap.put("mBio", bioedit.text.toString().trim())
                hashMap.put("mRMN", preferenceManager.rmn)
                hashMap.put("mProfileType", preferenceManager.profileType.toString())

                reference.set(hashMap as Map<String, Any>, SetOptions.merge()).addOnCompleteListener {

                    //update fleets sorter
                    val arr = ArrayList<String>()
                    for (fleets in partnerInfoPojo.fleetVehicle) {
                        if (fleets.value) {
                            arr.add(fleets.key)
                        }
                    }
                    arr.sort()
                    Logger.v(arr.toString())
                    var fleetss = ArrayList<String>()
                    partnerInfoPojo.setmFleetsSort(fleetss)
                    printSubsets(arr)
                    val hashMap3 = HashMap<String, Any>()
                    hashMap3.put("mFleetsSort", partnerInfoPojo.getmFleetsSort())

                    reference.set(hashMap3 as Map<String, Any>, SetOptions.merge()).addOnCompleteListener {

                        val operationHubsHash = HashMap<String, Boolean>()
                        if (partnerInfoPojo.getmOperationHubs() != null) {
                            for (map in partnerInfoPojo.getmOperationHubs()) {
                                if (map.value) {
                                    operationHubsHash.put(map.key, true)
                                }
                            }
                        }


                        val denormPojo = DenormalizerPojo(partnerInfoPojo.getmCompanyName(),
                                partnerInfoPojo.getmDisplayName(),
                                partnerInfoPojo.getmRMN(),
                                preferenceManager.userId,
                                preferenceManager.fuid,
                                preferenceManager.imageUrl,
                                partnerInfoPojo.getmCity(),
                                partnerInfoPojo.getmFcmToken(),
                                partnerInfoPojo.getmFleetsSort(),
                                operationHubsHash,
                                Date().time.toDouble(),
                                true,
                                partnerInfoPojo.getmAvgRating(),
                                partnerInfoPojo.getmNumRatings(),
                                preferenceManager.profileType.toString()
                        )

                        val userPresensePojo2 = UserPresensePojo(false, Date().time, "")
                        FirebaseDatabase.getInstance()
                                .reference
                                .child("chatpresence")
                                .child("users")
                                .child(preferenceManager.userId)
                                .onDisconnect()
                                .setValue(userPresensePojo2)
                                .addOnSuccessListener {
                                    Logger.v("onResume userpresence updated")
                                    db.collection("denormalizers").document(preferenceManager.userId).set(denormPojo).addOnCompleteListener {
                                        preferenceManager.setCompanyName(compnametext.text.toString().toUpperCase().trim())

                                        //form saved
                                        val bundle = Bundle()
                                        firebaseAnalytics.logEvent("z_form_uploaded", bundle)
                                        mixpanelAPI.track(MixPanelConstants.EVENT_UPLOAD_COMPANY_FORM)
                                        mixpanelAPI.people.set("CompanyName", preferenceManager.comapanyName)
                                        val i = Intent()
                                        setResult(Activity.RESULT_OK, i)
                                        finish()
                                    }.addOnCanceledListener {
                                        saving.visibility = View.GONE
                                        doneeditprofile.visibility = View.VISIBLE
                                        Toast.makeText(context, "Error, Try Again", Toast.LENGTH_SHORT).show()
                                    }
                                }.addOnCanceledListener {
                                    saving.visibility = View.GONE
                                    doneeditprofile.visibility = View.VISIBLE
                                    Toast.makeText(context, "Error, Try Again", Toast.LENGTH_SHORT).show()
                                }


                    }.addOnCanceledListener {
                        saving.visibility = View.GONE
                        doneeditprofile.visibility = View.VISIBLE
                        Toast.makeText(context, "Error, Try Again", Toast.LENGTH_SHORT).show()
                    }


                }.addOnCanceledListener {
                    saving.visibility = View.GONE
                    doneeditprofile.visibility = View.VISIBLE
                    Toast.makeText(context, "Error, Try Again", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun printSubsets(set: ArrayList<String>) {
        val n = set.size

        // Run a loop for printing all 2^n
        // subsets one by obe
        for (i in 0 until (1 shl n)) {

            // Print current subset
            var possiblity = ""
            for (j in 0 until n) {
                if (i and (1 shl j) > 0) {
                    possiblity = possiblity + set[j] + "_"
                }
            }
            partnerInfoPojo.getmFleetsSort().add(possiblity)

        }
        Logger.v(partnerInfoPojo.getmFleetsSort().toString())
    }


    override fun onPause() {
        super.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {

                PLACE_AUTOCOMPLETE_REQUEST_CODE -> {
                    if (data != null) {
                        val place = PlaceAutocomplete.getPlace(context, data)
                        cityedit.text = place.name.toString()
                        Toast.makeText(context, place.name, Toast.LENGTH_SHORT).show()
                        //Upload
                        val hashMap3 = HashMap<String, String>()
                        hashMap3.put("mCity", place.name.toString().toUpperCase())
                        reference.set(hashMap3 as Map<String, Any>, SetOptions.merge()).addOnCompleteListener {
                            mixpanelAPI.people.set("CompanyCity", place.name.toString().toUpperCase())
                        }

                    } else {
                        Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show()
                    }

                }
                PLACE_PICKER_REQUEST -> {
                    if (data != null) {
                        val place = PlaceAutocomplete.getPlace(context, data)
                        Toast.makeText(context, "Set On Map", Toast.LENGTH_SHORT).show()
                        //Upload
                        val geoPoint = GeoPoint(place.latLng.latitude, place.latLng.longitude)
                        val hashMap3 = HashMap<String, GeoPoint>()
                        hashMap3.put("mLocation", geoPoint)
                        reference.set(hashMap3 as Map<String, Any>, SetOptions.merge())

                    } else {
                        Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        } else {
            //not selected
        }
    }

    private fun fetchData() {

        mainscrolledit.visibility = View.GONE
        reference.addSnapshotListener(this, EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null) {
                finish()
                Toast.makeText(context, "Error, Try Again!", Toast.LENGTH_SHORT).show()
                return@EventListener
            }
            if (snapshot != null && snapshot.exists()) {
                partnerInfoPojo = snapshot.toObject(PartnerInfoPojo::class.java)!!
                bindData(partnerInfoPojo!!)
                if (partnerInfoPojo.fleetVehicle == null) {
                    val ifleets = HashMap<String, Boolean>()
                    ifleets["LCV"] = false
                    ifleets["Truck"] = false
                    ifleets["Tusker"] = false
                    ifleets["Taurus"] = false
                    ifleets["Trailers"] = false
                    partnerInfoPojo.fleetVehicle = HashMap<String, Boolean>()
                    partnerInfoPojo.fleetVehicle.putAll(ifleets)
                    addFleets(partnerInfoPojo.fleetVehicle)
                }

            } else {
                Toast.makeText(context, "Create New!", Toast.LENGTH_SHORT).show()
                mainscrolledit.visibility = View.VISIBLE
                Logger.v("Current data: null")
                if (partnerInfoPojo.fleetVehicle == null) {
                    val ifleets = HashMap<String, Boolean>()
                    ifleets["LCV"] = false
                    ifleets["Truck"] = false
                    ifleets["Tusker"] = false
                    ifleets["Taurus"] = false
                    ifleets["Trailers"] = false
                    partnerInfoPojo.fleetVehicle = HashMap<String, Boolean>()
                    partnerInfoPojo.fleetVehicle.putAll(ifleets)
                    addFleets(partnerInfoPojo.fleetVehicle)
                }
            }
        })


    }

    /**
     * Bind Company details fetched from firestore
     * @param partnerInfoPojo POJO for company details
     */

    private fun bindData(partnerInfoPojo: PartnerInfoPojo) {


        //company name
        if (partnerInfoPojo.getmCompanyName() != null) {
            if (!partnerInfoPojo.getmCompanyName().isEmpty()) {
                if (compnametext.text.toString().isEmpty())
                    compnametext.setText(partnerInfoPojo.getmCompanyName())
            }
        }
        //company bio
        if (partnerInfoPojo.getmBio() != null) {
            if (!partnerInfoPojo.getmBio().isEmpty()) {
                if (bioedit.text.toString().isEmpty())
                    bioedit.setText(partnerInfoPojo.getmBio())
            }
        }
        //company profiletype
        if (partnerInfoPojo.getmProfileType() != null) {
            if (!partnerInfoPojo.getmProfileType().isEmpty()) {

                var type = ""
                if (partnerInfoPojo.getmProfileType() == "0") {
                    type = getString(R.string.load_provider)
                    fleetstitle.text = getString(R.string.fleets_need)
                }
                if (partnerInfoPojo.getmProfileType() == "2") {
                    type = getString(R.string.fleet_provider)
                    fleetstitle.text = getString(R.string.fleets_provide)
                }
                showprofiletype.text = type

            }
        }

        //city
        if (partnerInfoPojo.getmCity() != null) {
            if (!partnerInfoPojo.getmCity().isEmpty()) {
                cityedit.text = partnerInfoPojo.getmCity()
                cityedit.setTextColor(ContextCompat.getColor(context, R.color.blue_grey_700))
                cityindicator.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_read_24dp))
            } else {
                cityindicator.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_readgrey_24dp))
                cityedit.setTextColor(ContextCompat.getColor(context, R.color.blue_grey_300))
            }
        } else {
            cityedit.setTextColor(ContextCompat.getColor(context, R.color.blue_grey_300))
            cityindicator.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_readgrey_24dp))
        }

        //latlong
        if (partnerInfoPojo.getmLocation() != null) {
            pinpointcheck.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_read_24dp))
            pinpoint.text = partnerInfoPojo.getmLocation().toString()
        } else {
            pinpointcheck.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_readgrey_24dp))
            pinpoint.text = "Tap To Point"
        }

        //fleets
        if (partnerInfoPojo.fleetVehicle != null) {
            addFleets(partnerInfoPojo.fleetVehicle)
        }

        //cities
        if (partnerInfoPojo.getmOperationCities() != null) {
            if (partnerInfoPojo.getmOperationCities().size > 0) {
                addCities(partnerInfoPojo.getmOperationCities())
            } else {
                val nocity: java.util.ArrayList<String> = java.util.ArrayList()
                nocity.add(getString(R.string.no_cities_yet_tap_manage))
                addCities(nocity)
            }
        } else {
            val nocity: java.util.ArrayList<String> = java.util.ArrayList()
            nocity.add(getString(R.string.no_cities_yet_tap_manage))
            addCities(nocity)
        }
        mainscrolledit.visibility = View.VISIBLE

    }


    private fun addCities(getmOperationCities: MutableList<String>) {
        cities.clear()
        for (city: String in getmOperationCities) {
            cities.add(city)
        }
        if (cities.isEmpty()) {
            cities.add(getString(R.string.no_cities_yet_tap_manage))
        }
        rv_citiess.adapter!!.notifyDataSetChanged()
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
        rv_fleetss.adapter!!.notifyDataSetChanged()

    }

    private fun addFleets() {
        fleets.clear()
        fleets.add(FleetSelectPojo("LCV", ContextCompat.getDrawable(this, R.mipmap.ic_fleet_lcv_round)!!, false))
        fleets.add(FleetSelectPojo("Truck", ContextCompat.getDrawable(this, R.mipmap.ic_fleet_truckpng_round)!!, false))
        fleets.add(FleetSelectPojo("Tusker", ContextCompat.getDrawable(this, R.mipmap.ic_fleet_tuskerpng_round)!!, false))
        fleets.add(FleetSelectPojo("Taurus", ContextCompat.getDrawable(this, R.mipmap.ic_fleet_tauruspng_round)!!, false))
        fleets.add(FleetSelectPojo("Trailers", ContextCompat.getDrawable(this, R.mipmap.ic_fleet_trailerpng_round)!!, false))
        rv_fleetss.adapter!!.notifyDataSetChanged()

    }


    private fun setUpImage(getmPhotoUrl: String) {
        if (getmPhotoUrl != null) {
            if (!getmPhotoUrl.isEmpty()) {
                Picasso.with(applicationContext)
                        .load("$getmPhotoUrl?width=160&width=160")
                        .placeholder(ContextCompat.getDrawable(applicationContext, R.mipmap.ic_launcher_round))
                        .transform(CircleTransform())
                        .into(compimagee, object : Callback {

                            override fun onSuccess() {
                                Logger.v("image set: profile thumb")
                                Logger.v(getmPhotoUrl)
                            }

                            override fun onError() {
                                Logger.v("image profile Error")
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
        rv_fleetss.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        // You can use GridLayoutManager if you want multiple columns. Enter the number of columns as a parameter.
//        rv_animal_list.layoutManager = GridLayoutManager(this, 2)
        // Access the RecyclerView Adapter and load the data into it
        rv_fleetss.adapter = FleetsSelectAdapter(fleets, this, object : OnFleetSelectedListner {
            override fun onFleetSelected(mFleetName: String) {
                if (partnerInfoPojo.fleetVehicle != null) {
                    partnerInfoPojo.fleetVehicle[mFleetName] = true
                    reference.update("fleetVehicle", partnerInfoPojo.fleetVehicle).addOnCompleteListener {
                        Logger.v("++ $mFleetName")
                        editprofilePageAction("fleet_select")
                        val props = JSONObject()
                        try {
                            props.put("fleet_selected", mFleetName)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        mixpanelAPI.track(MixPanelConstants.EVENT_AVAILABLE_FLEETS, props)
                    }
                } else {
                    Logger.v("null $mFleetName")
                }
            }

            override fun onFleetDeslected(mFleetName: String) {
                if (partnerInfoPojo.fleetVehicle != null) {
                    partnerInfoPojo.fleetVehicle[mFleetName] = false
                    reference.update("fleetVehicle", partnerInfoPojo.fleetVehicle).addOnCompleteListener {
                        Logger.v("-- $mFleetName")
                        editprofilePageAction("fleet_deselect")
                        val props = JSONObject()
                        try {
                            props.put("fleet_deselected", mFleetName)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        mixpanelAPI.track(MixPanelConstants.EVENT_AVAILABLE_FLEETS, props)
                    }
                } else {
                    Logger.v("null $mFleetName")
                }
            }

        }, 0)
    }


    private fun setOperatorAdapter() {
        // Loads animals into the ArrayList
//        addFleets()
        // Creates a vertical Layout Manager
        rv_operatorss.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // You can use GridLayoutManager if you want multiple columns. Enter the number of columns as a parameter.
        // rv_animal_list.layoutManager = GridLayoutManager(this, 2)

        // Access the RecyclerView Adapter and load the data into it
        rv_operatorss.adapter = OperatorsAdapter(fleets, this, object : OnFleetSelectedListner {
            override fun onFleetSelected(mFleetName: String) {
            }

            override fun onFleetDeslected(mFleetName: String) {
            }

        })
    }

    private fun setCitiesAdapter() {

        rv_citiess.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL)

        // You can use GridLayoutManager if you want multiple columns. Enter the number of columns as a parameter.
        // rv_animal_list.layoutManager = GridLayoutManager(this, 2)

        // Access the RecyclerView Adapter and load the data into it
        rv_citiess.adapter = CapsulsRecyclarAdapter(cities)
    }


    private fun startPickupfragment() {

        try {

            val typeFilter = AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                    .setCountry("IN")
                    .build()
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setFilter(typeFilter)
                    .build(this)
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)

        } catch (e: GooglePlayServicesRepairableException) {
            // TODO: Handle the error.
        } catch (e: GooglePlayServicesNotAvailableException) {
            // TODO: Handle the error.
        }

    }

    @Throws(GooglePlayServicesNotAvailableException::class, GooglePlayServicesRepairableException::class)
    private fun startPlacePicker() {
        val builder: PlacePicker.IntentBuilder
        builder = PlacePicker.IntentBuilder()
        startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST)

    }

    private fun internetCheck() {
        NoNet.monitor(this)
                .poll()
                .snackbar()
    }

    private fun editprofilePageAction(action: String) {
        val props = JSONObject()
        try {
            props.put("action", action)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        mixpanelAPI.track(MixPanelConstants.EVENT_EDIT_COMPANY_PAGE_ACTION, props)
    }

    private fun chatwithassistant() {
        val intent = Intent(this@CompanyProfileEditActivity, ChatRoomActivity::class.java)
        intent.putExtra("ormn", "+919284089759")
        intent.putExtra("imsg", "Hi, I need to change my role.")
        intent.putExtra("ouid", "pKeXxKD5HjS09p4pWoUcu8Vwouo1")
        intent.putExtra("ofuid", "4zRHiYyuLMXhsiUqA7ex27VR0Xv1")
        startActivity(intent)
        val bundle = Bundle()
        firebaseAnalytics.logEvent("z_assistant", bundle)
    }
}
