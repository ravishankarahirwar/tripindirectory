package directory.tripin.com.tripindirectory.newactivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
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
import directory.tripin.com.tripindirectory.model.response.Contact;

public class DataTransferActivity extends AppCompatActivity {

    Gson gson;
    DocumentReference mUserDocRef;
    String RMN;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_transfer);
        gson =new Gson();
        String dump = loadJSONFromAsset();
        try {
            JSONArray jsonArray = new JSONArray(dump);
            for(int i=0;i<1;i++){
                SinglePartnerDumpMain d = gson.fromJson(jsonArray.getJSONObject(i).toString(),SinglePartnerDumpMain.class);
                PartnerInfoPojo p = new PartnerInfoPojo();
                RMN = "null"+i;

                p.setCompanyName(d.getName());

                CompanyAddressPojo companyAddressPojo = new CompanyAddressPojo(d.getAddress(),"Mumbai","Maharashtra");
                p.setCompanyAdderss(companyAddressPojo);

                HashMap<String,Boolean> hashMap =new HashMap<>();
                hashMap.put("Mumbai",true);
                p.setmSourceCities(hashMap);

                HashMap<String,Boolean> hashMap2 =new HashMap<>();
                if(d.getAreaOfOperation()!=null){
                    for(int j=0;j<d.getAreaOfOperation().length;j++){
                        hashMap2.put(d.getAreaOfOperation()[j].getRegionName(),true);
                    }
                    p.setmDestinationCities(hashMap2);
                }


                List<ContactPersonPojo> mobile = new ArrayList<>();
                for(int j=0; j<d.getMobile().length;j++){
                    ContactPersonPojo contactPersonPojo =new ContactPersonPojo(d.getMobile()[j].getName(),d.getMobile()[j].getCellNo());
                    mobile.add(contactPersonPojo);
                    if(j==0){
                        RMN = "+918394876737";
                       // RMN = d.getMobile()[0].getCellNo();

                    }
                }
                p.setContactPersonsList(mobile);



                List<String> landlines = new ArrayList<>();
                for(int j=0;j<d.getPhone().length;j++){
                    landlines.add(d.getPhone()[j].getLandline());
                }
                p.setmCompanyLandLineNumbers(landlines);

                HashMap<String,Boolean> hashmap3 =new HashMap<>();
                hashmap3.put("Fleet Owner",false);
                hashmap3.put("Transport Contractor",false);
                hashmap3.put("Commission Agent",false);
                for(int j=0; j<d.getNatureOfbusiness().length;j++){
                    hashMap.put(d.getNatureOfbusiness()[j].getName(),true);
                }
                p.setmNatureOfBusiness(hashmap3);

                HashMap<String,Boolean> hashMap4 = new HashMap<>();
                hashMap4.put("FTL",false);
                hashMap4.put("Part Loads",false);
                hashMap4.put("Parcel",false);
                hashMap4.put("ODC",false);
                hashMap4.put("Import Containers",false);
                hashMap4.put("Export Containers",false);
                hashMap4.put("Chemical",false);
                hashMap4.put("Petrol",false);
                hashMap4.put("Diesel",false);
                hashMap4.put("Oil",false);
                for(int j=0; j<d.getServiceType().length;j++){
                    hashMap4.put(d.getServiceType()[j].getName(),true);
                }
                p.setmTypesOfServices(hashMap4);

                p.setmLikes(Integer.parseInt(d.getLike()));
                p.setmDislikes(Integer.parseInt(d.getDislike()));
                p.setmRMN(RMN);


                mUserDocRef = FirebaseFirestore.getInstance()
                        .collection("partners").document(RMN);

                mUserDocRef.set(p).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Logger.v("on success: "+RMN);
                    }
                });


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("dump.json");
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
