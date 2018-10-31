package directory.tripin.com.tripindirectory.newprofiles.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_data_modifier.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo
import directory.tripin.com.tripindirectory.newprofiles.models.DenormalizerPojo
import kotlinx.android.synthetic.main.activity_company_profile_edit.*


class DataModifierActivity : AppCompatActivity() {

    var oppName : String = "Update Hubs"
    lateinit var doclist: QuerySnapshot




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_modifier)

        docname.text = oppName


//        status.text = "Getting Docs... Hold On!"
//        FirebaseFirestore.getInstance().collection("partners").get().addOnCompleteListener {
//            status.text = it.result.size().toString()
//            doclist = it.result
//        }

        startmodif.setOnClickListener {
            if(doclist!=null){
                startOperation(doclist)
            }else{
                Toast.makeText(applicationContext,"Wait",Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun startOperation(doclist: QuerySnapshot) {

        var nullcont = 0
        for(doc in doclist){

            val partnerPojo = doc.toObject(PartnerInfoPojo::class.java)

            if(partnerPojo.getmCompanyName()!=null){

                if(!partnerPojo.getmCompanyName().isEmpty()){
                    Logger.v(partnerPojo.getmCompanyName()+"updating")

                    var operationCitieslist = ArrayList<String>()

                    //get source cities
                    if(partnerPojo.getmSourceCities()!=null){
                        for(city in partnerPojo.getmSourceCities()){
                            if(city.value){
                                operationCitieslist.add(city.key.toUpperCase())
                            }
                        }
                    }

                    //get destination cities
                    if(partnerPojo.getmDestinationCities()!=null){
                        for(city in partnerPojo.getmDestinationCities()){
                            if(city.value){
                                operationCitieslist.add(city.key.toUpperCase())
                            }
                        }
                    }

                    //set opetation cities
                    partnerPojo.setmOperationCities(operationCitieslist.distinct())


                    //hubs
                    var operationHubs = HashMap<String,Boolean>()

                    //get source hubs
                    if(partnerPojo.getmSourceHubs()!=null){
                        for(hub in partnerPojo.getmSourceHubs()){
                            if(hub.value){
                                operationHubs.put(hub.key.toUpperCase(),true)
                            }
                        }
                    }

                    //get destination hubs
                    if(partnerPojo.getmDestinationHubs()!=null){
                        for(hub in partnerPojo.getmDestinationHubs()){
                            if(hub.value){
                                operationHubs.put(hub.key.toUpperCase(),true)
                            }
                        }
                    }

                    //set opetation hubs
                    partnerPojo.setmOperationHubs(operationHubs)

                    //update fleets sorter

                    if(partnerPojo.fleetVehicle==null){
                        var fllv = HashMap<String,Boolean>()
                        fllv.put("LCV",false)
                        fllv.put("Truck",false)
                        fllv.put("Tusker",false)
                        fllv.put("Taurus",false)
                        fllv.put("Trailers",false)
                        partnerPojo.fleetVehicle = fllv
                    }

                    val arr = ArrayList<String>()
                    for(fleets in partnerPojo.fleetVehicle){
                        if(fleets.value){
                            arr.add(fleets.key)
                        }
                    }
                    arr.sort()
                    Logger.v(arr.toString())
                    var fleetss = ArrayList<String>()
                    partnerPojo.setmFleetsSort(fleetss)

                    for (i in 0 until (1 shl arr.size)) {

                        var possiblity = ""
                        for (j in 0 until arr.size){
                            if (i and (1 shl j) > 0){
                                possiblity =  possiblity + arr[j] + "_"
                            }
                        }
                        partnerPojo.getmFleetsSort().add(possiblity)
                    }


                    partnerPojo.setmAvgRating(0.0)
                    partnerPojo.setmNumRatings(0.0)

                    if(partnerPojo.getmLastActive()==null){
                        partnerPojo.setmLastActive(0.0)
                    }

                    if(partnerPojo.getmCompanyAdderss()!=null){
                        if(partnerPojo.getmCompanyAdderss().city!=null){
                            partnerPojo.setmCity(partnerPojo.getmCompanyAdderss().city)
                        }

                        if(partnerPojo.getmCompanyAdderss().isLatLongSet){
                            if(partnerPojo.getmCompanyAdderss().getmLatitude()!=null&&partnerPojo.getmCompanyAdderss().getmLongitude()!=null){
                                partnerPojo.setmLocation(GeoPoint(partnerPojo.getmCompanyAdderss().getmLatitude().toDouble(),
                                        partnerPojo.getmCompanyAdderss().getmLongitude().toDouble()))
                            }
                        }
                    }

                    FirebaseFirestore.getInstance().collection("partners").document(doc.id).set(partnerPojo).addOnCompleteListener {
                        Logger.v("doc updated")
                    }.addOnCanceledListener {
                        Logger.v("doc update cenceled")
                    }

                    val denormPojo = DenormalizerPojo(partnerPojo.getmCompanyName(),
                            partnerPojo.getmDisplayName(),
                            partnerPojo.getmRMN(),
                            doc.id,
                            partnerPojo.getmFUID(),
                            partnerPojo.getmPhotoUrl(),
                            partnerPojo.getmCity(),
                            partnerPojo.getmFcmToken(),
                            partnerPojo.getmFleetsSort(),
                            partnerPojo.getmOperationHubs(),
                            partnerPojo.getmLastActive(),
                            partnerPojo.isActive,
                            partnerPojo.getmAvgRating(),
                            partnerPojo.getmNumRatings()
                    )

                    //denormaliser update
                    FirebaseFirestore.getInstance().collection("denormalizers").document(doc.id).set(denormPojo).addOnCompleteListener {
                        Logger.v("denormaliser updated")
                    }.addOnCanceledListener {
                        Logger.v("denormaliser update canceled")
                    }

                }else{
                    FirebaseFirestore.getInstance().collection("partners").document(doc.id).delete().addOnCompleteListener {
                        Logger.v("doc removed emptyname")
                    }
                }

            }else{
                FirebaseFirestore.getInstance().collection("partners").document(doc.id).delete().addOnCompleteListener {
                    Logger.v("doc removed")
                }
            }

        }

        docname.text = nullcont.toString()
    }


}
