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
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.mixpanel.android.mpmetrics.MixpanelAPI
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.model.HubFetchedCallback
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo
import directory.tripin.com.tripindirectory.model.RouteCityPojo
import directory.tripin.com.tripindirectory.newlookcode.activities.NewSplashActivity
import directory.tripin.com.tripindirectory.newlookcode.utils.MixPanelConstants
import directory.tripin.com.tripindirectory.newprofiles.CityInteractionCallbacks
import directory.tripin.com.tripindirectory.newprofiles.OperationCitiesAdapter
import kotlinx.android.synthetic.main.activity_company_profile_display.*
import kotlinx.android.synthetic.main.activity_manage_cities.*
import kotlinx.android.synthetic.main.content_main_scrolling.*
import kotlinx.android.synthetic.main.layout_route_input.*
import org.json.JSONException
import org.json.JSONObject
import kotlin.collections.ArrayList

class ManageCitiesActivity : LocalizationActivity(), CityInteractionCallbacks, HubFetchedCallback {

    /**
     * ManageCitiesActivity lets you add or remove operation cities
     * it also update the profile by updating the operation hubs
     * @author shubhamsardar
     */


    internal var PLACE_AUTOCOMPLETE_REQUEST_CODE = 1
    lateinit var context: Context
    lateinit var auth: FirebaseAuth
    lateinit var mUserDocRef: DocumentReference
    lateinit var cities: MutableList<String>
    lateinit var hubs: HashMap<String, Boolean>
    lateinit var mixpanelAPI: MixpanelAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_cities)
        context = this
        mixpanelAPI = MixpanelAPI.getInstance(context, MixPanelConstants.MIXPANEL_TOKEN)

        cities = ArrayList<String>()
        hubs = HashMap()
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
                            countcity.text = cities.size.toString()
                            if (cities.size > 20) {
                                limitcity.visibility = View.VISIBLE
                            } else {
                                limitcity.visibility = View.GONE
                            }
                            cities.sort()
                            (rv_cities_manage.adapter as OperationCitiesAdapter).notifyDataSetChanged()

                            if (partnerInfoPojo.getmOperationHubs() != null) {
                                if (partnerInfoPojo.getmOperationHubs().size > 0) {
                                    hubs.clear()
                                    hubs.putAll(partnerInfoPojo.getmOperationHubs())
                                }
                            }

                        } else {
                            cities.clear()
                            (rv_cities_manage.adapter as OperationCitiesAdapter).notifyDataSetChanged()
                            citiesemptyinfo.visibility = View.VISIBLE
                        }
                    } else {
                        citiesemptyinfo.visibility = View.VISIBLE
                        Logger.v("list cities null")
                    }
                    if (partnerInfoPojo!!.getmOperationHubs() != null) {
                        hubs.clear()
                        hubs.putAll(partnerInfoPojo!!.getmOperationHubs())
                    }
                }
            }
            setListners()
        } else {

            Toast.makeText(context, "Not Logged In. Retry!", Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        manageCitiesAction("back")
    }

    private fun setListners() {
        fabaddcity.setOnClickListener {
            startPickupfragment()
        }
        back_managecity.setOnClickListener {
            finish()
            manageCitiesAction("back")

        }
        ocities_done.setOnClickListener {

            savingcities.visibility = View.VISIBLE
            manageCitiesAction("done")
            val hubsss = ArrayList<String>()
            for (hub in hubs) {
                if (hub.value) {
                    hubsss.add(hub.key)
                }
            }

            val hashMap = HashMap<String, Any>()
            hashMap.put("mHubs", hubsss)
            FirebaseFirestore.getInstance().collection("hubslookup").document(auth.uid!!).set(hashMap).addOnCompleteListener {
                Handler().postDelayed(({
                    finish()
                }), 500)
            }

        }
    }

    override fun onCityRemoved(city: String) {
        cities.remove(city.toUpperCase())
        mUserDocRef.update("mOperationCities", cities).addOnCompleteListener {
            Toast.makeText(context, "$city removing", Toast.LENGTH_SHORT).show()
            updateSourceHubss(city, 2)
            manageCitiesAction("city_removed")
        }
    }

    override fun onDestinationHubFetched(destinationhub: String?, operaion: Int) {

    }

    override fun onSourceHubFetched(sourcehub: String?, operation: Int) {
        when (operation) {
            1 -> {
                //add
                hubs[sourcehub.toString().toUpperCase()] = true
                mUserDocRef.update("mOperationHubs", hubs)

            }
            2 -> {
                //remove
                hubs[sourcehub.toString().toUpperCase()] = false
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
                            manageCitiesAction("city_added")
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

    private fun manageCitiesAction(action: String) {
        val props = JSONObject()
        try {
            props.put("action", action)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        mixpanelAPI.track(MixPanelConstants.EVENT_MANAGE_CITIES_ACTION, props)
    }
}
