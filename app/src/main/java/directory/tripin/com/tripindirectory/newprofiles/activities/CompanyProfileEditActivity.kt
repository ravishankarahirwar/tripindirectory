package directory.tripin.com.tripindirectory.newprofiles.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ScrollView
import android.widget.Toast
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.gson.Gson
import com.keiferstone.nonet.NoNet
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.adapters.CapsulsRecyclarAdapter
import directory.tripin.com.tripindirectory.chatingactivities.models.UserPresensePojo
import directory.tripin.com.tripindirectory.helper.CircleTransform
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo
import directory.tripin.com.tripindirectory.newlookcode.FleetSelectPojo
import directory.tripin.com.tripindirectory.newlookcode.FleetsSelectAdapter
import directory.tripin.com.tripindirectory.newlookcode.OnFleetSelectedListner
import directory.tripin.com.tripindirectory.newlookcode.utils.MixPanelConstants
import directory.tripin.com.tripindirectory.newprofiles.OperatorsAdapter
import directory.tripin.com.tripindirectory.newprofiles.models.DenormUpdateListner
import directory.tripin.com.tripindirectory.newprofiles.models.DenormalizerPojo
import directory.tripin.com.tripindirectory.newprofiles.models.DenormalizerUpdateManager
import directory.tripin.com.tripindirectory.newprofiles.models.ProfileData
import kotlinx.android.synthetic.main.activity_company_profile_display.*
import kotlinx.android.synthetic.main.activity_company_profile_edit.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class CompanyProfileEditActivity : AppCompatActivity() {

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
//    private var dialog: AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_profile_edit)
        context = this
//        dialog = SpotsDialog.Builder()
//                .setContext(this)
//                .setCancelable(false)
//                .setMessage("Hold On! This may take a while.")
//                .build()
        db = FirebaseFirestore.getInstance()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mixpanelAPI = MixpanelAPI.getInstance(context,MixPanelConstants.MIXPANEL_TOKEN)
        preferenceManager = PreferenceManager.getInstance(context)
        partnerInfoPojo = PartnerInfoPojo()


        reference = db.collection("partners")
                .document(preferenceManager.userId)
        setSelectFleetAdapter()
        setCitiesAdapter()
        setListners()
        internetCheck()
//        setOperatorAdapter()


    }

    override fun onResume() {
        super.onResume()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        if(preferenceManager.imageUrl!=null){
            setUpImage(preferenceManager.imageUrl)
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

        doneeditprofile.setOnClickListener {

            editprofilePageAction("submit_form")

            mainscrolledit.scrollTo(0, 0)
            mixpanelAPI.timeEvent(MixPanelConstants.EVENT_UPLOAD_COMPANY_FORM)

            if (compnametext.text.toString().isEmpty()) {
                Toast.makeText(context, "Company Name Cant be empty", Toast.LENGTH_SHORT).show()
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
                        if(partnerInfoPojo.getmOperationHubs()!=null){
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
                                partnerInfoPojo.getmNumRatings()
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
                                        firebaseAnalytics.logEvent("z_form_uploaded",bundle)
                                        mixpanelAPI.track(MixPanelConstants.EVENT_UPLOAD_COMPANY_FORM)
                                        mixpanelAPI.people.set("CompanyName",preferenceManager.comapanyName)
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
                            mixpanelAPI.people.set("CompanyCity",place.name.toString().toUpperCase())
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
                if(partnerInfoPojo.fleetVehicle==null){
                    val ifleets= HashMap<String,Boolean>()
                    ifleets["LCV"] = false
                    ifleets["Truck"] = false
                    ifleets["Tusker"] = false
                    ifleets["Taurus"] = false
                    ifleets["Trailers"] = false
                    partnerInfoPojo.fleetVehicle = HashMap<String,Boolean>()
                    partnerInfoPojo.fleetVehicle.putAll(ifleets)
                    addFleets(partnerInfoPojo.fleetVehicle)
                }

            } else {
                Toast.makeText(context, "Create New!", Toast.LENGTH_SHORT).show()
                mainscrolledit.visibility = View.VISIBLE
                Logger.v("Current data: null")
                if(partnerInfoPojo.fleetVehicle==null){
                    val ifleets= HashMap<String,Boolean>()
                    ifleets["LCV"] = false
                    ifleets["Truck"] = false
                    ifleets["Tusker"] = false
                    ifleets["Taurus"] = false
                    ifleets["Trailers"] = false
                    partnerInfoPojo.fleetVehicle = HashMap<String,Boolean>()
                    partnerInfoPojo.fleetVehicle.putAll(ifleets)
                    addFleets(partnerInfoPojo.fleetVehicle)
                }
            }
        })


    }

    private fun bindData(partnerInfoPojo: PartnerInfoPojo) {

        //image
//        setUpImage(partnerInfoPojo.getmPhotoUrl())

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
                nocity.add("No Cities Added Yet, tap on manage cities.")
                addCities(nocity)
            }
        } else {
            val nocity: java.util.ArrayList<String> = java.util.ArrayList()
            nocity.add("No Cities Added Yet, tap on Manage Cities.")
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
            cities.add("No Cities Added, Tap On Manage Cities")
        }
        rv_citiess.adapter.notifyDataSetChanged()
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
        rv_fleetss.adapter.notifyDataSetChanged()

    }
    private fun addFleets() {
        fleets.clear()
        fleets.add(FleetSelectPojo("LCV", ContextCompat.getDrawable(this, R.mipmap.ic_fleet_lcv_round)!!, false))
        fleets.add(FleetSelectPojo("Truck", ContextCompat.getDrawable(this, R.mipmap.ic_fleet_truckpng_round)!!, false))
        fleets.add(FleetSelectPojo("Tusker", ContextCompat.getDrawable(this, R.mipmap.ic_fleet_tuskerpng_round)!!, false))
        fleets.add(FleetSelectPojo("Taurus", ContextCompat.getDrawable(this, R.mipmap.ic_fleet_tauruspng_round)!!, false))
        fleets.add(FleetSelectPojo("Trailers", ContextCompat.getDrawable(this, R.mipmap.ic_fleet_trailerpng_round)!!, false))

        rv_fleetss.adapter.notifyDataSetChanged()

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
                if(partnerInfoPojo.fleetVehicle!=null){
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
                }else{
                    Logger.v("null $mFleetName")
                }
            }

            override fun onFleetDeslected(mFleetName: String) {
                if(partnerInfoPojo.fleetVehicle!=null){
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
                }else{
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

//    private fun itirateBatches(refsToProcess: ConcurrentHashMap<String, Boolean>, hashMapp: Map<String, Any>, fleetsAvailable: ArrayList<String>) {
//
//        val batch = db.batch()
//
//        var count = 400
//
//        for(process in refsToProcess){
//            if(process.value){
//                //update
//                val updateRef = db.collection("denormalised")
//                        .document("routes")
//                        .collection(process.key)
//                        .document(preferenceManager.userId)
//                val hashMappp = mapOf<String, Any>(
//                        "mFleets" to fleetsAvailable
//                )
//                batch.set(updateRef,hashMapp, SetOptions.merge())
//                batch.set(updateRef,hashMappp, SetOptions.merge())
//                count -= 2
//
//            }else{
//                //delete
//                val delRef = db.collection("denormalised")
//                        .document("routes")
//                        .collection(process.key)
//                        .document(preferenceManager.userId)
//                batch.delete(delRef)
//                count--
//            }
//            refsToProcess.remove(process.key)
//            if(count<=0){
//                break
//            }
//            Logger.v("Count $count")
//            saving.text = "Loading Batch $count"
//
//
//        }
//
//
//        saving.text = "Denormalising Data. Please Wait..."
//        dialog!!.show()
//        batch.commit().addOnCompleteListener {
//            if(refsToProcess.size==0){
//                saving.text = "Clearing Junk Data..."
//                reference.update("mOperationHubs", refinedHubs).addOnCompleteListener {
//                    dialog!!.dismiss()
//                    Toast.makeText(context,"Updated Successfully",Toast.LENGTH_SHORT).show()
//                    finish()
//                }
//            }else{
//                itirateBatches(refsToProcess, hashMapp, fleetsAvailable)
//            }
//
//        }.addOnCanceledListener {
//            saving.visibility = View.GONE
//            Toast.makeText(context,"Error, Try Again",Toast.LENGTH_SHORT).show()
//        }
//    }

//    private fun uploadinbatched(hubs: Map<String, Boolean>) {
//
//        saving.text = "Verifying Routes..."
//        //generate profile data obj
//        val fleetsAvailable : ArrayList<String> = ArrayList()
//        for(fleet in partnerInfoPojo.fleetVehicle){
//            if(fleet.value){
//                fleetsAvailable.add(fleet.key)
//            }
//        }
//        val mProfileData = ProfileData(partnerInfoPojo.getmCompanyName(),
//                partnerInfoPojo.getmDisplayName(),
//                partnerInfoPojo.getmCity(),
//                partnerInfoPojo.getmPhotoUrl(),
//                fleetsAvailable,preferenceManager.rmn,preferenceManager.userId,preferenceManager.fuid)
//
//        val gson : Gson = Gson()
//        val mProfileDataString = gson.toJson(mProfileData)
//        val hashMapp = mapOf<String, Any>(
//                "mProfileData" to mProfileDataString
//        )
//
//        //Denormalise
//        refinedHubs = HashMap<String,Boolean>()
//        val refsToProcess = ConcurrentHashMap<String,Boolean>()
//        for(hub  in hubs){
//
//            for(hubb  in hubs){
//                if(hub.value){
//                    //update
//                    if(hubb.value){
//
//                        refsToProcess.put("${hub.key}_${hubb.key}",true)
//                        Logger.v("${hub.key}_${hubb.key} Add")
//                    }else{
//                        //Delete
//
//                        refsToProcess.put("${hub.key}_${hubb.key}",false)
//
//                        Logger.v("${hub.key}_${hubb.key} Remove")
//                    }
//
//                    refinedHubs[hub.key] = true
//                }else{
//                    //delete
//
//                    refsToProcess.put("${hub.key}_${hubb.key}",false)
//
//                    Logger.v("${hub.key}_${hubb.key} Remove")
//
//                }
//            }
//
//        }
//        refsToProcess.put("ALL",true)
//
//        itirateBatches(refsToProcess, hashMapp , fleetsAvailable)
//
//
//    }

    private fun editprofilePageAction(action: String) {
        val props = JSONObject()
        try {
            props.put("action", action)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        mixpanelAPI.track(MixPanelConstants.EVENT_EDIT_COMPANY_PAGE_ACTION, props)
    }
}
