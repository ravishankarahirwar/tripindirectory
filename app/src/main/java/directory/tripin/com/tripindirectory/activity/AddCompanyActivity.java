package directory.tripin.com.tripindirectory.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.model.CompanyAddressPojo;
import directory.tripin.com.tripindirectory.model.ContactPersonPojo;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;

public class AddCompanyActivity extends AppCompatActivity {

    private static final String TAG = "AddCompanyActivity";
    //firebase module fields
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //form ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_company);
        init();
        setListners();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.form_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.logout:
                Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT)
                        .show();
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // user is now signed out
                                startActivity(new Intent(AddCompanyActivity.this, SplashActivity.class));
                                finish();
                            }
                        });
                finish();
                break;
            case R.id.query:
                Toast.makeText(this, "Query Printed", Toast.LENGTH_SHORT)
                        .show();

                db.collection("partners").whereEqualTo("mSourceCities.nagpur", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

                break;
        }
        return super.onOptionsItemSelected(item);

    }

    private void init() {
        //firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void setListners() {
        //firebase db listner

        if (mAuth.getCurrentUser() != null) {
            db.collection("partners").document(mAuth.getUid()).addSnapshotListener(AddCompanyActivity.this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                    //Log.e("onEvent",e.getMessage());

                }
            });
        }

    }

    public void submit(View view) {
        List<ContactPersonPojo> contactPersonPojos = new ArrayList<>();
        contactPersonPojos.add(new ContactPersonPojo("Pranav", "7845122585"));
        contactPersonPojos.add(new ContactPersonPojo("Shubham", "8394876737"));
        contactPersonPojos.add(new ContactPersonPojo("Ravi", "8394856737"));

        CompanyAddressPojo companyAddressPojo = new CompanyAddressPojo("andheri", "mumbai", "maharashtra");

        List<String> urllist = new ArrayList<>();
        urllist.add("url1");
        urllist.add("url2");
        urllist.add("url3");

        Map<String, Boolean> source = new HashMap<>();
        Map<String, Boolean> destination = new HashMap<>();

        source.put("mumbai", true);
        source.put("nagpur", true);
        destination.put("rajkot", true);
        destination.put("gandhinagar", true);


        PartnerInfoPojo partnerInfoPojo =
                new PartnerInfoPojo("ABC company",
                        contactPersonPojos,
                        "78456215",
                        companyAddressPojo,
                        urllist, false, source, destination);

        db.collection("partners").document(mAuth.getUid()).set(partnerInfoPojo);
        Toast.makeText(this, "uploaded", Toast.LENGTH_LONG).show();

    }
}
