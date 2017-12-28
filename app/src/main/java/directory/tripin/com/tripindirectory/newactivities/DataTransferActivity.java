package directory.tripin.com.tripindirectory.newactivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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

public class DataTransferActivity extends AppCompatActivity {

    Gson gson;
    DocumentReference mUserDocRef;
    String RMN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_transfer);
        gson = new Gson();

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

    public void push(View view) {
        String dump = loadJSONFromAsset();
        try {
            JSONArray jsonArray = new JSONArray(dump);
            for (int i = 0; i < jsonArray.length(); i++) {
                SinglePartnerDumpMain d = gson.fromJson(jsonArray.getJSONObject(i).toString(), SinglePartnerDumpMain.class);
                PartnerInfoPojo p = new PartnerInfoPojo();
                RMN = "null" + i;

                p.setCompanyName(d.getName());

                CompanyAddressPojo companyAddressPojo = new CompanyAddressPojo(d.getAddress(), "MUMBAI", "MAHARASHTRA");
                p.setCompanyAdderss(companyAddressPojo);

                HashMap<String, Boolean> hashMap = new HashMap<>();
                hashMap.put("MUMBAI", true);
                p.setmSourceCities(hashMap);

                HashMap<String, Boolean> hashMap2 = new HashMap<>();
                if (d.getAreaOfOperation() != null) {
                    for (int j = 0; j < d.getAreaOfOperation().length; j++) {
                        hashMap2.put(d.getAreaOfOperation()[j].getRegionName().toUpperCase(), true);
                    }
                    p.setmDestinationCities(hashMap2);
                }


                List<ContactPersonPojo> mobile = new ArrayList<>();
                for (int j = 0; j < d.getMobile().length; j++) {
                    ContactPersonPojo contactPersonPojo = new ContactPersonPojo(d.getMobile()[j].getName(), d.getMobile()[j].getCellNo());
                    mobile.add(contactPersonPojo);
                    if (j == 0) {
                        RMN = "+91"+d.getMobile()[0].getCellNo();

                    }
                }
                p.setContactPersonsList(mobile);


                List<String> landlines = new ArrayList<>();
                for (int j = 0; j < d.getPhone().length; j++) {
                    landlines.add(d.getPhone()[j].getLandline());
                }
                p.setmCompanyLandLineNumbers(landlines);

                HashMap<String, Boolean> hashmap3 = new HashMap<>();
                hashmap3.put("Fleet Owner".toUpperCase(), false);
                hashmap3.put("Transport Contractor".toUpperCase(), false);
                hashmap3.put("Commission Agent".toUpperCase(), false);
                for (int j = 0; j < d.getNatureOfbusiness().length; j++) {
                    hashMap.put(d.getNatureOfbusiness()[j].getName().toUpperCase(), true);
                }
                p.setmNatureOfBusiness(hashmap3);

                HashMap<String, Boolean> hashMap4 = new HashMap<>();
                hashMap4.put("FTL".toUpperCase(), false);
                hashMap4.put("Part Loads".toUpperCase(), false);
                hashMap4.put("Parcel".toUpperCase(), false);
                hashMap4.put("ODC".toUpperCase(), false);
                hashMap4.put("Import Containers".toUpperCase(), false);
                hashMap4.put("Export Containers".toUpperCase(), false);
                hashMap4.put("Chemical".toUpperCase(), false);
                hashMap4.put("Petrol".toUpperCase(), false);
                hashMap4.put("Diesel".toUpperCase(), false);
                hashMap4.put("Oil".toUpperCase(), false);
                for (int j = 0; j < d.getServiceType().length; j++) {
                    hashMap4.put(d.getServiceType()[j].getName().toUpperCase(), true);
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
                        Logger.v("on success: " + RMN);
                    }
                });


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void remove(View view) {
        String dump = loadJSONFromAsset();
        try {
            JSONArray jsonArray = new JSONArray(dump);
            for (int i = 0; i < jsonArray.length(); i++) {
                SinglePartnerDumpMain d = gson.fromJson(jsonArray.getJSONObject(i).toString(), SinglePartnerDumpMain.class);
                RMN = "null" + i;
                RMN = "+91"+d.getMobile()[0].getCellNo();

                mUserDocRef = FirebaseFirestore.getInstance()
                        .collection("partners").document(RMN);

                mUserDocRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Logger.v("on delete: " + RMN);
                    }
                });


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
