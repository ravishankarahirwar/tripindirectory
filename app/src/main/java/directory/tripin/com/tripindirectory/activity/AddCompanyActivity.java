package directory.tripin.com.tripindirectory.activity;

import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.model.CompanyAddressPojo;
import directory.tripin.com.tripindirectory.model.ContactPersonPojo;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;
import directory.tripin.com.tripindirectory.viewmodel.AddPerson;

public class AddCompanyActivity extends AppCompatActivity {

    private static final String TAG = "AddCompanyActivity";
    //firebase module fields
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private EditText mCompanyNmae;
    private EditText mLandlineNmber;
    private EditText mPersonName;
    private EditText mPersonContact;
    private EditText mCompanyAddress;
    private EditText mCompanyCity;
    private EditText mCompanyState;

    private TextView mAddPerson;
    private TextView mLandLine;
    private LinearLayout mAddPersonLayout;
    private LinearLayout mAddLandLineLayout;
    private List<AddPerson> mContactPersons;
    private List<AddPerson> mLandlineNumbers;
    private Context mContext;
    List<String> companyLandlineNumbers;
    //form ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_company);
        init();
        setListners();

    }

    @Override
    protected void onResume() {
        super.onResume();

        CollectionReference cities = db.collection("partners");
        DocumentReference docRef = db.collection("partners").document(mAuth.getUid());

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    PartnerInfoPojo company = documentSnapshot.toObject(PartnerInfoPojo.class);
                    mCompanyNmae.setText(company.getmCompanyName());
                    mCompanyAddress.setText(company.getmCompanyAdderss().getmAddress().toString());
                    mCompanyCity.setText(company.getmCompanyAdderss().getmCity().toString());
                    mCompanyState.setText(company.getmCompanyAdderss().getmState().toString());

                if(company.getmContactPersonsList().size() > 1) {
                    String name = company.getmContactPersonsList().get(0).getmContactPresonName();
                    String number = company.getmContactPersonsList().get(0).getGetmContactPersonMobile();
                    mPersonName.setText(name);
                    mPersonContact.setText(number);

                    for(int i=1; i < company.getmContactPersonsList().size(); i++) {
                        String name1 = company.getmContactPersonsList().get(i).getmContactPresonName();
                        String number1 = company.getmContactPersonsList().get(i).getGetmContactPersonMobile();
                        addContactPerson( name1,  number1);
                    }
                } else {
                    String name = company.getmContactPersonsList().get(0).getmContactPresonName();
                    String number = company.getmContactPersonsList().get(0).getGetmContactPersonMobile();
                    mPersonName.setText(name);
                    mPersonContact.setText(number);
                }

            }
        }});
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

        mContactPersons = new ArrayList<AddPerson>();
        mLandlineNumbers = new ArrayList<AddPerson>();
        companyLandlineNumbers = new ArrayList<>();

        mContext = AddCompanyActivity.this;
        mCompanyNmae = (EditText)this.findViewById(R.id.company_name);
        mCompanyAddress  = (EditText)this.findViewById(R.id.input_company_address);
        mCompanyCity  = (EditText)this.findViewById(R.id.input_city);
        mCompanyState  = (EditText)this.findViewById(R.id.input_state);
        mAddPerson = (TextView)this.findViewById(R.id.add_person);
        mLandLine = (TextView)this.findViewById(R.id.add_landline);

        mLandlineNmber = findViewById(R.id.landline_number);

        mPersonName = findViewById(R.id.contact_person_name);
        mPersonContact = findViewById(R.id.contact_person_number);

        AddPerson addPerson = new AddPerson(mContext);
        addPerson.setPersonName(mPersonName);
        addPerson.setPesonContact(mPersonContact);
        mContactPersons.add(addPerson);

        mAddPersonLayout = (LinearLayout)this.findViewById(R.id.add_person_layout);
        mAddLandLineLayout = (LinearLayout)this.findViewById(R.id.landline_number_layout);

        AddPerson addLandline = new AddPerson(mContext);
        addLandline.setPersonName(mLandlineNmber);
        mLandlineNumbers.add(addLandline);

        mAddPerson.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addContactPerson();
                    }
                });

        mLandLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLandLineNumber();
//                LayoutInflater inflater = LayoutInflater.from(AddCompanyActivity.this);
//                View  addPersonView = inflater.inflate(R.layout.include_add_landline, null);
//                mAddLandLineLayout.addView(addPersonView);
            }
        });
   }

   private void addContactPerson() {
       LayoutInflater inflater = LayoutInflater.from(AddCompanyActivity.this);
       View addPersonView = inflater.inflate(R.layout.include_add_person, null);
       EditText personName = addPersonView.findViewById(R.id.contact_person_name);
       EditText personContact = addPersonView.findViewById(R.id.contact_person_number);

       AddPerson addPerson = new AddPerson(mContext);
       addPerson.setPersonName(personName);
       addPerson.setPesonContact(personContact);

       mContactPersons.add(addPerson);
       mAddPersonLayout.addView(addPersonView);
   }

    private void addLandLineNumber() {
        LayoutInflater inflater = LayoutInflater.from(AddCompanyActivity.this);
        View addPersonView = inflater.inflate(R.layout.include_add_landline, null);
        EditText landlineNmber = addPersonView.findViewById(R.id.landline_number);

        AddPerson addPerson = new AddPerson(mContext);
        addPerson.setPersonName(landlineNmber);

        mLandlineNumbers.add(addPerson);
        mAddLandLineLayout.addView(addPersonView);
    }

    private void addContactPerson(String name, String number) {
        LayoutInflater inflater = LayoutInflater.from(AddCompanyActivity.this);
        View addPersonView = inflater.inflate(R.layout.include_add_person, null);
        EditText personName = addPersonView.findViewById(R.id.contact_person_name);
        EditText personContact = addPersonView.findViewById(R.id.contact_person_number);

        personName.setText(name);
        personContact.setText(number);

        AddPerson addPerson = new AddPerson(mContext);
        addPerson.setPersonName(personName);
        addPerson.setPesonContact(personContact);

        mContactPersons.add(addPerson);
        mAddPersonLayout.addView(addPersonView);
    }

    private void addLandLineNumber(String landlineNumber) {
        LayoutInflater inflater = LayoutInflater.from(AddCompanyActivity.this);
        View addPersonView = inflater.inflate(R.layout.include_add_landline, null);
        EditText landlineNmber = addPersonView.findViewById(R.id.landline_number);

        AddPerson addPerson = new AddPerson(mContext);
        addPerson.setPersonName(landlineNmber);

        mLandlineNumbers.add(addPerson);
        mAddLandLineLayout.addView(addPersonView);
    }

    private void setListners(){
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
        String companyName = mCompanyNmae.getText().toString();
        String companyAddress = mCompanyAddress.getText().toString();
        String companyCity = mCompanyCity.getText().toString();
        String companyState = mCompanyState.getText().toString();

        List<ContactPersonPojo> contactPersonPojos = new ArrayList<>();

        for(int i = 0; i < mContactPersons.size(); i++) {
            AddPerson addPerson = mContactPersons.get(i);
            String name = addPerson.getPersonName().getText().toString();
            String number = addPerson.getPesonContact().getText().toString();
            contactPersonPojos.add(new ContactPersonPojo(name, number));
        }


        for(int i = 0; i < mLandlineNumbers.size(); i++) {
            AddPerson addPerson = mLandlineNumbers.get(i);
            String landlineNumber = addPerson.getPersonName().getText().toString();
            companyLandlineNumbers.add(landlineNumber);
        }


        CompanyAddressPojo companyAddressPojo = new CompanyAddressPojo(companyAddress,companyCity,companyState);


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
                new PartnerInfoPojo(companyName,
                        contactPersonPojos,
                        companyLandlineNumbers,
                        companyAddressPojo,
                        urllist, false, source, destination);

        db.collection("partners").document(mAuth.getUid()).set(partnerInfoPojo);
        Toast.makeText(this, "uploaded", Toast.LENGTH_LONG).show();

    }
}
