package directory.tripin.com.tripindirectory.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.model.CompanyAddressPojo;
import directory.tripin.com.tripindirectory.model.ContactPersonPojo;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;
import directory.tripin.com.tripindirectory.model.RawDataPojos.MainRawPojo;
import directory.tripin.com.tripindirectory.model.RawDataPojos.PartnerRawDataPojo;
import directory.tripin.com.tripindirectory.model.response.Vehicle;

public class DataTransferActivity extends AppCompatActivity {


    Gson gson;
    CollectionReference mRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_transfer);
        String jsonDump = loadJSONFromAsset();
        gson = new Gson();
        mRef = FirebaseFirestore.getInstance()
                .collection("partners");


        try {
            JSONArray jsonArray = new JSONArray(jsonDump);

            for(int i=0;i<jsonArray.length();i++){
                String RMN = "null";
                MainRawPojo d = gson.fromJson(jsonArray.getJSONObject(i).toString(), MainRawPojo.class);
//                Logger.v(gson.toJson(jsonArray.getJSONObject(i)));
                PartnerInfoPojo partnerInfoPojo = new PartnerInfoPojo();

                partnerInfoPojo.setCompanyName(d.getName());

//                Logger.v("compp name: "+d.getName());

                CompanyAddressPojo companyAddressPojo = new CompanyAddressPojo(d.getAddress(),"Mumbai","Maharashtra");
                partnerInfoPojo.setCompanyAdderss(companyAddressPojo);

                partnerInfoPojo.setmEmailId(d.getEmailId());

                if(d.getLike()!=null){
                    partnerInfoPojo.setmLikes(Integer.parseInt(d.getLike()));
                }else {
                    partnerInfoPojo.setmLikes(0);

                }

                if(d.getDislike()!=null){
                    partnerInfoPojo.setmDislikes(Integer.parseInt(d.getDislike()));
                }else {
                    partnerInfoPojo.setmDislikes(0);
                }


                List<ContactPersonPojo> contactPersonPojoList = new ArrayList<>();

                if(d.getMobile()!=null){
                    for(int j=0; i<d.getMobile().length;i++){
                        ContactPersonPojo contactPersonPojo = new ContactPersonPojo(d.getMobile()[j].getName(),d.getMobile()[j].getCellNo());
                        contactPersonPojoList.add(contactPersonPojo);
                        if(j==0){
                            RMN = d.getMobile()[j].getCellNo();
                            partnerInfoPojo.setmRMN(RMN);
                        }
                    }
                }

                partnerInfoPojo.setContactPersonsList(contactPersonPojoList);

                HashMap<String,Boolean> hashMap = new HashMap<>();
                if(d.getNatureOfbusiness()!=null){
                    for(int j=0; j<d.getNatureOfbusiness().length;j++){
                        hashMap.put(d.getNatureOfbusiness()[j].getName(),true);
                    }
                    partnerInfoPojo.setmNatureOfBusiness(hashMap);
                }


                if(d.getServiceType()!=null){
                    HashMap<String,Boolean> hashMap2 = new HashMap<>();
                    for(int j=0; j<d.getServiceType().length;j++){
                        hashMap2.put(d.getServiceType()[j].getName(),true);
                    }
                    partnerInfoPojo.setmTypesOfServices(hashMap2);
                }


//                if(d.getPhone()!=null){
//                    List<String> landlines =new ArrayList<>();
//                    for(int j=0;j<d.getPhone().length; j++){
//                        landlines.add(d.getPhone()[j]);
//                    }
//                    partnerInfoPojo.setmCompanyLandLineNumbers(landlines);
//                }


//                if(d.getFleet()!=null){
//                    List<Vehicle> vlist = new ArrayList<>();
//                    for(int j=0 ; i<d.getFleet().length; j++){
//                        Vehicle vehicle =new Vehicle();
//                        vehicle.setAvailable(true);
//                        vehicle.setType(d.getFleet()[j].getVehicleType());
//                        vehicle.setBodyType(d.getFleet()[j].getBodyType()[0].getName());
//                        vlist.add(vehicle);
//                    }
//                    partnerInfoPojo.setVehicles(vlist);
//                }



                HashMap<String,Boolean> hashsource = new HashMap<>();
                hashsource.put("Mumbai",true);
                partnerInfoPojo.setmSourceCities(hashsource);

                if(d.getAreaOfOperation()!=null){
                    HashMap<String,Boolean> hashdestination = new HashMap<>();
                    for(int j=0; j<d.getAreaOfOperation().length;j++){
                        hashdestination.put(d.getAreaOfOperation()[j].getRegionName(),true);
                    }
                    partnerInfoPojo.setmDestinationCities(hashdestination);
                }


                DocumentReference mUserDocRef = FirebaseFirestore.getInstance()
                        .collection("partners").document(RMN);
                final int finalI = i;
                mUserDocRef.set(partnerInfoPojo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Logger.v("pushing doc "+ finalI);
                    }
                });

//                final int finalI = i;
//                mRef.document(RMN).set(partnerInfoPojo).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Logger.v("pushing doc "+ finalI);
//                    }
//                });




            }
        } catch (JSONException e) {
            e.printStackTrace();
        }




    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getApplication().getAssets().open("jsondump.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
