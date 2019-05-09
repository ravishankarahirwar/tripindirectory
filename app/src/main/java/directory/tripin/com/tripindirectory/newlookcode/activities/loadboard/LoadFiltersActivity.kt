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
import com.google.gson.Gson
import com.jaredrummler.materialspinner.MaterialSpinner
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import directory.tripin.com.tripindirectory.model.HubFetchedCallback
import directory.tripin.com.tripindirectory.model.RouteCityPojo
import directory.tripin.com.tripindirectory.newprofiles.models.LoadPostPojo
import kotlinx.android.synthetic.main.activity_load_filters.*
import kotlinx.android.synthetic.main.layout_fsfilterloads_actionbar.*

class LoadFiltersActivity : LocalizationActivity(), HubFetchedCallback {


    /**
     * LoadFilterActivity manages the filters selection UI for Loadboard
     * This activity saves the selected filters in preference manager
     * and is applied when we set the main loadposts adapter
     * @author shubhamsardar
     *
     */

    internal var PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE = 3
    internal var PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION = 4
    lateinit var context: Context
    lateinit var postpojo: LoadPostPojo
    lateinit var mSourceRouteCityPojo: RouteCityPojo
    lateinit var mDestinationRouteCityPojo: RouteCityPojo
    lateinit var preferenceManager : PreferenceManager
    lateinit var spinnervehicle: MaterialSpinner
    lateinit var spinnerbody: MaterialSpinner
    lateinit var spinnerweight: MaterialSpinner
    lateinit var spinnerlength: MaterialSpinner



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_filters)
        context = this
        preferenceManager = PreferenceManager.getInstance(context)
        mSourceRouteCityPojo = RouteCityPojo(context, 1, 0, this)
        mDestinationRouteCityPojo = RouteCityPojo(context, 2, 0, this)

        setListners()
        setSpinners()
        setRoutePickup()
        postpojo = LoadPostPojo()

        if(preferenceManager.prefLBFilter.isNotEmpty()){
            setInitialFilters()
        }

    }


    private fun setInitialFilters() {

        postpojo = Gson().fromJson(preferenceManager.prefLBFilter, LoadPostPojo::class.java)
        if(postpojo.getmSourceCity()!=null){
            select_source.text = postpojo.getmSourceCity()
            select_source.setTextColor(ContextCompat.getColor(context, R.color.blue_grey_900))
        }
        if(postpojo.getmDestinationCity()!=null){
            select_destination.text = postpojo.getmDestinationCity()
            select_destination.setTextColor(ContextCompat.getColor(context, R.color.blue_grey_900))
        }
        if(postpojo.getmPayload()!=null){
            weightf.setText(postpojo.getmPayload())
        }
        if(postpojo.getmVehichleLenght()!=null){
            lengthf.setText(postpojo.getmVehichleLenght())
        }

        if(postpojo.getmVehicleType()!=null){
            spinnervehicle.selectedIndex = spinnervehicle.getItems<String>().indexOf(postpojo.getmVehicleType())
        }

        if(postpojo.getmBodyType()!=null){
            spinnerbody.selectedIndex = spinnerbody.getItems<String>().indexOf(postpojo.getmBodyType())
        }

        if(postpojo.getmPayloadUnit()!=null){
            spinnerweight.selectedIndex = spinnerweight.getItems<String>().indexOf(postpojo.getmPayloadUnit())
        }

        if(postpojo.getmVehichleLenghtUnit()!=null){
            spinnerlength.selectedIndex = spinnerlength.getItems<String>().indexOf(postpojo.getmVehichleLenghtUnit())
        }

    }


    private fun setListners() {

        backfslbf.setOnClickListener {
            finish()
        }
        setfilters.setOnClickListener {
            setFilters()
        }
        fab_clearall.setOnClickListener {
            clearroute()
        }
    }

    private fun clearroute() {

        select_source.text = getString(R.string.source)
        select_destination.text = getString(R.string.destination)
        postpojo.setmDestinationHub(null)
        postpojo.setmSourceHub(null)
        postpojo.setmSourceCity(null)
        postpojo.setmDestinationCity(null)

    }

    private fun setFilters() {
        if(weightf.text != null){
            //add unit
            if(!weightf.text.isEmpty()){
                val list = spinnerweightf.getItems<String>()
                postpojo.setmPayload(weightf.text.toString())
                postpojo.setmPayloadUnit(list.get(spinnerweightf.selectedIndex))
            }else{
                postpojo.setmPayload("")
            }
        }
        if(lengthf.text != null){
            //add unit
            if(!lengthf.text.isEmpty()){
                val list = spinnerlengthf.getItems<String>()
                postpojo.setmVehichleLenght(lengthf.text.toString())
                postpojo.setmVehichleLenghtUnit(list.get(spinnerlengthf.selectedIndex))
            }else{
                postpojo.setmVehichleLenght("")
            }
        }

        preferenceManager.prefLBFilter = Gson().toJson(postpojo)
        Logger.v(postpojo.toString())
        setResult(Activity.RESULT_OK)
        finish()

    }

    private fun setRoutePickup() {

        select_source.setOnClickListener {
            starttheplacesfragment(PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE)
        }
        select_destination.setOnClickListener {
            starttheplacesfragment(PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION)
        }

    }

    /**
     * Method that sets slectables in spinners
     * for vehicle type, body type, payload and truck length
     */

    private fun setSpinners() {

        spinnervehicle = findViewById<MaterialSpinner>(R.id.spinnervtypef)
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

        spinnerbody = findViewById<MaterialSpinner>(R.id.spinnerbtypef)
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

        spinnerweight = findViewById<MaterialSpinner>(R.id.spinnerweightf)
        spinnerweight.setItems(getString(R.string.kg), getString(R.string.ton), getString(R.string.mt))
        spinnerweight.setOnItemSelectedListener { view, position, id, item -> }
        spinnerlength = findViewById<MaterialSpinner>(R.id.spinnerlengthf)
        spinnerlength.setItems(getString(R.string.meter), getString(R.string.feet))
        spinnerlength.setOnItemSelectedListener { view, position, id, item -> }
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
                    if(data!=null){
                        val place = PlaceAutocomplete.getPlace(context, data)
                        select_source.text = ". ${place.name}"
                        mSourceRouteCityPojo.setmLatLang(place.latLng)
                        select_source.setTextColor(ContextCompat.getColor(context, R.color.blue_grey_900))
                        postpojo.setmSourceCity(place.name.toString())
                    }else{
                        Toast.makeText(context,R.string.try_again, Toast.LENGTH_LONG).show()
                    }

                }
                PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION -> {
                    if(data!=null){
                        val place = PlaceAutocomplete.getPlace(context, data)
                        select_destination.text = ". ${place.name}"
                        mDestinationRouteCityPojo.setmLatLang(place.latLng)
                        select_destination.setTextColor(ContextCompat.getColor(context, R.color.blue_grey_900))
                        postpojo.setmDestinationCity(place.name.toString())
                    }else{
                        Toast.makeText(context,R.string.try_again, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }else{
            when (requestCode) {
                3->{
                    finish()
                }
            }
        }
    }

    /**
     * Method that helps selecting city by opening Places Fragment by Google
     */

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
}
