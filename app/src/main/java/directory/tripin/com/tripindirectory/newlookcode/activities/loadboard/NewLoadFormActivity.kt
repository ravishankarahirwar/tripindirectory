package directory.tripin.com.tripindirectory.newlookcode.activities.loadboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jaredrummler.materialspinner.MaterialSpinner
import com.keiferstone.nonet.NoNet
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import directory.tripin.com.tripindirectory.model.HubFetchedCallback
import directory.tripin.com.tripindirectory.model.RouteCityPojo
import directory.tripin.com.tripindirectory.newlookcode.activities.FacebookRequiredActivity
import directory.tripin.com.tripindirectory.newlookcode.pojos.BasicQueryPojo
import directory.tripin.com.tripindirectory.newprofiles.models.LoadPostPojo
import kotlinx.android.synthetic.main.item_loadpost_input.*
import kotlinx.android.synthetic.main.layout_fsnewload_actionbar.*
import libs.mjn.prettydialog.PrettyDialog

class NewLoadFormActivity : LocalizationActivity(), HubFetchedCallback {


    /**
     * NewLoadFormActivity is to generate and upload a new load post into firestore/loadposts
     * @author shubhamsardar
     */

    internal var PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE = 3
    internal var PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION = 4
    private lateinit var preferenceManager: PreferenceManager

    lateinit var context: Context
    lateinit var postpojo: LoadPostPojo
    lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var mSourceRouteCityPojo: RouteCityPojo
    lateinit var mDestinationRouteCityPojo: RouteCityPojo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_load_form)
        context = this
        FirebaseApp.initializeApp(applicationContext)
        preferenceManager = PreferenceManager.getInstance(this)
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        postpojo = LoadPostPojo()

        if (FirebaseAuth.getInstance().currentUser == null
                || FirebaseAuth.getInstance().currentUser!!.phoneNumber == null
                || !preferenceManager.isFacebooked) {
            val i = Intent(this@NewLoadFormActivity, FacebookRequiredActivity::class.java)
            i.putExtra("from", "Loadboard")
            startActivityForResult(i, 3)
            Toast.makeText(applicationContext, getString(R.string.login_to_use_loadboard), Toast.LENGTH_LONG).show()
        }

        mSourceRouteCityPojo = RouteCityPojo(context, 1, 0, this)
        mDestinationRouteCityPojo = RouteCityPojo(context, 2, 0, this)


        setListners()
        setSpinners()
        setRoutePickup()
        getIntentData()
        internetCheck()


    }

    private fun getIntentData() {
        if (intent.extras != null) {
            if (intent.extras.getSerializable("query") != null) {

                val basicQueryPojo: BasicQueryPojo = intent.extras.getSerializable("query") as BasicQueryPojo
                if (!basicQueryPojo.mSourceCity.isEmpty()) {
                    select_sourcef.text = basicQueryPojo.mSourceCity
                    select_sourcef.setTextColor(ContextCompat.getColor(context, R.color.blue_grey_800))
                    postpojo.setmSourceCity(basicQueryPojo.mSourceCity)
                }
                if (!basicQueryPojo.mDestinationCity.isEmpty()) {
                    select_destinationf.text = basicQueryPojo.mDestinationCity
                    select_destinationf.setTextColor(ContextCompat.getColor(context, R.color.blue_grey_800))
                    postpojo.setmDestinationCity(basicQueryPojo.mDestinationCity)
                }
            }


        }
    }

    private fun setRoutePickup() {

        select_sourcef.setOnClickListener {
            starttheplacesfragment(PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE)
        }

        select_destinationf.setOnClickListener {
            starttheplacesfragment(PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION)
        }

    }

    override fun onDestinationHubFetched(destinationhub: String?, operaion: Int) {
        postpojo.setmDestinationHub(destinationhub)
    }

    override fun onSourceHubFetched(sourcehub: String?, operation: Int) {
        postpojo.setmSourceHub(sourcehub)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {

                PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE -> {
                    if (data != null) {
                        val place = PlaceAutocomplete.getPlace(context, data)
                        select_sourcef.text = ". ${place.name}"
                        mSourceRouteCityPojo.setmLatLang(place.latLng)
                        select_sourcef.setTextColor(ContextCompat.getColor(context, R.color.blue_grey_900))
                        postpojo.setmSourceCity(place.name.toString())
                    } else {
                        Toast.makeText(context, "Try Again!", Toast.LENGTH_LONG).show()
                    }

                }
                PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION -> {
                    if (data != null) {
                        val place = PlaceAutocomplete.getPlace(context, data)
                        select_destinationf.text = ". ${place.name}"
                        mDestinationRouteCityPojo.setmLatLang(place.latLng)
                        select_destinationf.setTextColor(ContextCompat.getColor(context, R.color.blue_grey_900))
                        postpojo.setmDestinationCity(place.name.toString())
                    } else {
                        Toast.makeText(context, getString(R.string.try_again), Toast.LENGTH_LONG).show()
                    }


                }

                3 -> {
                    Toast.makeText(applicationContext, getString(R.string.welcome), Toast.LENGTH_LONG).show()
                }

            }
        } else {
            when (requestCode) {
                3 -> {
                    finish()
                }
            }
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

    private fun setListners() {

        backfslbnn.setOnClickListener {
            finish()
        }

        post.setOnClickListener {
            if (postpojo.getmSourceHub() == null || postpojo.getmDestinationHub() == null) {
                Toast.makeText(context, getString(R.string.please_enter_route), Toast.LENGTH_LONG).show()
                post.text = "POST NOW"

            } else {
                if (weight.text != null) {
                    //add unit
                    if (!weight.text.isEmpty()) {
                        val list = spinnerweight.getItems<String>()
                        postpojo.setmPayload(weight.text.toString())
                        postpojo.setmPayloadUnit(list.get(spinnerweight.selectedIndex))
                    } else {
                        postpojo.setmPayload("")
                    }

                }
                if (length.text != null) {
                    //add unit
                    if (!length.text.isEmpty()) {
                        val list = spinnerlength.getItems<String>()
                        postpojo.setmVehichleLenght(length.text.toString())
                        postpojo.setmVehichleLenghtUnit(list.get(spinnerlength.selectedIndex))
                    } else {
                        postpojo.setmVehichleLenght("")
                    }


                }
                if (otherreq.text != null) {
                    postpojo.setmRemark(otherreq.text.toString())
                }
                if (material.text != null) {
                    postpojo.setmMaterial(material.text.toString())
                }

                postpojo.setmFuid(preferenceManager.fuid)
                postpojo.setmUid(preferenceManager.userId)
                postpojo.setmRmn(preferenceManager.rmn)
                postpojo.setmPhotoUrl(preferenceManager.imageUrl)
                Logger.v("image set: " + postpojo.getmPhotoUrl())
                postpojo.setmDisplayName(preferenceManager.displayName)
                postpojo.setmCompanyName(preferenceManager.comapanyName)


                showaggrementdialog()


            }
        }
    }

    /**
     * Method that sets slectables in spinners
     * for vehicle type, body type, payload and truck length
     */

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
            postpojo.setmVehicleType(item.toString())
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
            postpojo.setmBodyType(item.toString())
        }
        val spinnerweight = findViewById<MaterialSpinner>(R.id.spinnerweight)
        spinnerweight.setItems("KG", "Tons", "MT")
        spinnerweight.setOnItemSelectedListener { view, position, id, item -> }
        val spinnerlength = findViewById<MaterialSpinner>(R.id.spinnerlength)
        spinnerlength.setItems("Meters", "Feets")
        spinnerlength.setOnItemSelectedListener { view, position, id, item -> }
    }

    /**
     * To show confermation dialog before uploading
     */

    private fun showaggrementdialog() {

        val prettyDialog: PrettyDialog = PrettyDialog(this)

        prettyDialog
                .setTitle(getString(R.string.loadboard_agreement))
                .setMessage(getString(R.string.loadboard_agreement_details))
                .addButton(
                        getString(R.string.agreed_post_now),
                        R.color.pdlg_color_white,
                        R.color.green_400
                ) {
                    prettyDialog.dismiss()
                    writeNewPost(postpojo)

                }.addButton(
                        getString(R.string.find_transporter_on_directory),
                        R.color.pdlg_color_white,
                        R.color.blue_grey_100
                ) {
                    prettyDialog.dismiss()
                    finish()
                }.addButton(
                        getString(R.string.cencel),
                        R.color.pdlg_color_white,
                        R.color.blue_grey_100
                ) {
                    prettyDialog.dismiss()

                }
        prettyDialog.show()


    }

    private fun writeNewPost(postpojo: LoadPostPojo) {
        post.text = "..."
        Toast.makeText(this, getString(R.string.posting), Toast.LENGTH_SHORT).show()
        postpojo.setmNumViews(2)
        FirebaseFirestore.getInstance()
                .collection("loadposts")
                .add(postpojo).addOnCompleteListener {
                    Toast.makeText(context, getString(R.string.after_load_post_success), Toast.LENGTH_LONG).show()
                    val bundle = Bundle()
                    firebaseAnalytics.logEvent("z_loadboard_post", bundle)
                    finish()

                }.addOnCanceledListener {
                    Toast.makeText(context, getString(R.string.try_again), Toast.LENGTH_LONG).show()
                }

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
}
