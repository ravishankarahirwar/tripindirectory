package directory.tripin.com.tripindirectory.newprofiles.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.model.HubFetchedCallback
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo
import directory.tripin.com.tripindirectory.model.RouteCityPojo
import directory.tripin.com.tripindirectory.newlookcode.activities.NewSplashActivity
import directory.tripin.com.tripindirectory.newprofiles.CityInteractionCallbacks
import directory.tripin.com.tripindirectory.newprofiles.OperationCitiesAdapter
import kotlinx.android.synthetic.main.activity_company_profile_display.*
import kotlinx.android.synthetic.main.activity_manage_cities.*
import kotlinx.android.synthetic.main.content_main_scrolling.*
import kotlinx.android.synthetic.main.layout_route_input.*
import kotlin.collections.ArrayList

class ManageCitiesActivity : AppCompatActivity(), CityInteractionCallbacks, HubFetchedCallback {


    internal var PLACE_AUTOCOMPLETE_REQUEST_CODE = 1
    lateinit var context: Context
    lateinit var auth: FirebaseAuth
    lateinit var mUserDocRef: DocumentReference
    lateinit var cities: MutableList<String>
    lateinit var hubs: MutableList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_cities)
        context = this

        cities = ArrayList<String>()
        hubs = ArrayList<String>()

        rv_cities_manage.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_cities_manage.adapter = OperationCitiesAdapter(cities, this, this)


        //listen to pojo
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {

            mUserDocRef = FirebaseFirestore.getInstance()
                    .collection("partners").document(auth.uid!!)
            mUserDocRef.addSnapshotListener(this) { documentSnapshot, e ->
                if (documentSnapshot!!.exists()) {
                    val partnerInfoPojo = documentSnapshot.toObject(PartnerInfoPojo::class.java)
                    Logger.v("Event Triggered, CITIES modified")
                    if (partnerInfoPojo!!.getmOperationCities() != null) {
                        if (partnerInfoPojo.getmOperationCities().size > 0) {
                            citiesemptyinfo.visibility = View.GONE
                            cities.clear()
                            cities.addAll(partnerInfoPojo.getmOperationCities())
                            cities.sort()
                            rv_cities_manage.adapter.notifyDataSetChanged()

                            if (partnerInfoPojo.getmOperationHubs() != null) {
                                if (partnerInfoPojo.getmOperationHubs().size > 0) {
                                    hubs.clear()
                                    hubs.addAll(partnerInfoPojo.getmOperationHubs())
                                }
                            }

                        } else {
                            cities.clear()
                            rv_cities_manage.adapter.notifyDataSetChanged()
                            citiesemptyinfo.visibility = View.VISIBLE
                        }
                    } else {
                        citiesemptyinfo.visibility = View.VISIBLE
                        Logger.v("list cities null")
                    }
                }
            }
            setListners()


        } else {

            Toast.makeText(context, "Not Logged In. Retry!", Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    private fun setListners() {
        fabaddcity.setOnClickListener {
            startPickupfragment()
        }
        back_managecity.setOnClickListener {
            finish()
        }
        ocities_done.setOnClickListener {

            savingcities.visibility = View.VISIBLE
            Handler().postDelayed(({
                finish()
            }), 1000)

        }
    }

    override fun onCityRemoved(city: String) {
        cities.remove(city.toUpperCase())
        mUserDocRef.update("mOperationCities", cities).addOnCompleteListener {
            Toast.makeText(context, "$city removing", Toast.LENGTH_SHORT).show()
            updateSourceHubss(city, 2)
        }
    }

    override fun onDestinationHubFetched(destinationhub: String?, operaion: Int) {

    }

    override fun onSourceHubFetched(sourcehub: String?, operation: Int) {
        when (operation) {
            1 -> {
                //add
                hubs.add(sourcehub.toString().toUpperCase())
                mUserDocRef.update("mOperationHubs", hubs)

            }
            2 -> {
                //remove
                hubs.remove(sourcehub.toString().toUpperCase())
                mUserDocRef.update("mOperationHubs", hubs)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {

                PLACE_AUTOCOMPLETE_REQUEST_CODE -> {
                    if (data != null) {
                        val place = PlaceAutocomplete.getPlace(context, data)
                        cities.add(place.name.toString().toUpperCase())

                        mUserDocRef.update("mOperationCities", cities).addOnCompleteListener {
                            Toast.makeText(context, place.name.toString() + " added", Toast.LENGTH_SHORT).show()
                            updateSourceHubs(place.latLng, 1)

                        }

                    } else {
                        Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show()
                    }

                }

            }
        } else {
            //not selected
        }
    }

    private fun startPickupfragment() = try {

        val typeFilter = AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .setCountry("IN")
                .build()
        val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                .setFilter(typeFilter)
                .build(this)
        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)

    } catch (e: GooglePlayServicesRepairableException) {
        // TODO: Handle the error.
    } catch (e: GooglePlayServicesNotAvailableException) {
        // TODO: Handle the error.
    }

    private fun updateSourceHubs(city: LatLng, operation: Int) {
        val routeCityPojo = RouteCityPojo(context, 1, operation, this)
        routeCityPojo.setmLatLang(city)
    }

    private fun updateSourceHubss(city: String, operation: Int) {
        val routeCityPojo = RouteCityPojo(context, 1, operation, this)
        routeCityPojo.setmCityName(city)
    }
}
