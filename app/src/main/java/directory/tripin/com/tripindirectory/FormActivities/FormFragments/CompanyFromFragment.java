package directory.tripin.com.tripindirectory.FormActivities.FormFragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import directory.tripin.com.tripindirectory.FormActivities.CompanyLandLineNumbersAdapter;
import directory.tripin.com.tripindirectory.FormActivities.ContactPersonsAdapter;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.model.CompanyAddressPojo;
import directory.tripin.com.tripindirectory.model.ContactPersonPojo;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompanyFromFragment extends BaseFragment {


    DocumentReference mUserDocRef;
    FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private EditText mCompanyNmae;
    private EditText mCompanyAddress;
    private EditText mCompanyCity;
    private EditText mCompanyState;
    private TextView mAddContactPersonTxt;
    private TextView mAddCompanyTxt;
    private RecyclerView mPersonsRecyclarView;
    private RecyclerView mLandlineRecyclarView;
    private List<ContactPersonPojo> mContactPersonsList;
    private List<String> mCompanyLandLineNumbers;
    private ContactPersonsAdapter contactPersonsAdapter;
    private CompanyLandLineNumbersAdapter companyLandLineNumbersAdapter;
    PartnerInfoPojo partnerInfoPojo;
    private LinearLayout mLoadingDataLin;

    private static final int CONTACT_PICKER_RESULT = 1001;



    public CompanyFromFragment() {
        mContactPersonsList = new ArrayList<>();
        mCompanyLandLineNumbers = new ArrayList<>();

        //creat adapters and set adapters
        mContactPersonsList.add(new ContactPersonPojo("", ""));
        contactPersonsAdapter = new ContactPersonsAdapter(mContactPersonsList);

        mCompanyLandLineNumbers.add("");
        companyLandLineNumbersAdapter = new CompanyLandLineNumbersAdapter(mCompanyLandLineNumbers);
    }


    public void FetchUserData() {
        //get the updated partner pojo and set all fields if not null
        auth = FirebaseAuth.getInstance();
        mUserDocRef = FirebaseFirestore.getInstance()
                .collection("partners").document(auth.getUid());
        Logger.v("Fetching Data");
        //mLoadingDataLin.setVisibility(View.VISIBLE);
        mUserDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    mLoadingDataLin.setVisibility(View.GONE);
                    partnerInfoPojo = task.getResult().toObject(PartnerInfoPojo.class);

                    if(partnerInfoPojo.getmContactPersonsList()!=null){
                        mContactPersonsList.clear();
                        mContactPersonsList.addAll(partnerInfoPojo.getmContactPersonsList());
                    }

                    if(partnerInfoPojo.getmContactPersonsList()!=null){
                        mCompanyLandLineNumbers.clear();
                        mCompanyLandLineNumbers.addAll(partnerInfoPojo.getmCompanyLandLineNumbers());
                    }

                    companyLandLineNumbersAdapter.notifyDataSetChanged();
                    contactPersonsAdapter.notifyDataSetChanged();

                    mCompanyNmae.setText(partnerInfoPojo.getmCompanyName());
                    if(partnerInfoPojo.getmCompanyAdderss()!=null){
                        mCompanyAddress.setText(partnerInfoPojo.getmCompanyAdderss().getAddress());
                        mCompanyCity.setText(partnerInfoPojo.getmCompanyAdderss().getCity());
                        mCompanyState.setText(partnerInfoPojo.getmCompanyAdderss().getState());
                    }


                    Logger.v("On Data Fetch and set Company Data");

                }

            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FetchUserData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        //setUpView();

    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.v("OnPauseCompanyFormFragment");

        if(partnerInfoPojo!=null){
            //send the modified data to parent activity

            //set contacts
            List<ContactPersonPojo> contacts = new ArrayList<>();
            for(int i=0;i<mContactPersonsList.size();i++){
                View v = mPersonsRecyclarView.getLayoutManager().findViewByPosition(i);
                EditText name = v.findViewById(R.id.contact_person_name);
                EditText number = v.findViewById(R.id.contact_person_number);
                contacts.add(new ContactPersonPojo(name.getText().toString().trim(),number.getText().toString().trim()));
            }
            List<String> landlines = new ArrayList<>();
            for(int i=0;i<mCompanyLandLineNumbers.size();i++){
                View v = mLandlineRecyclarView.getLayoutManager().findViewByPosition(i);
                EditText number = v.findViewById(R.id.landline_number);
                landlines.add(number.getText().toString().trim());
            }
            partnerInfoPojo.setContactPersonsList(contacts);
            partnerInfoPojo.setmCompanyLandLineNumbers(landlines);


            //setname
            partnerInfoPojo.setCompanyName(mCompanyNmae.getText().toString().trim());

            //setaddress
            partnerInfoPojo
                    .setCompanyAdderss(new CompanyAddressPojo(mCompanyAddress.getText().toString().trim(),
                            mCompanyCity.getText().toString().trim(),
                            mCompanyState.getText().toString().trim()));

            mUserDocRef.set(partnerInfoPojo, SetOptions.merge());

        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_company_from, container, false);

        init();

        mCompanyNmae = v.findViewById(R.id.company_name);
        mCompanyAddress = v.findViewById(R.id.input_company_address);
        mCompanyCity = v.findViewById(R.id.input_city);
        mCompanyState = v.findViewById(R.id.input_state);
        mAddContactPersonTxt = v.findViewById(R.id.add_person);
        mAddCompanyTxt = v.findViewById(R.id.add_landline);
        mLoadingDataLin = v.findViewById(R.id.ll_loading);


        mPersonsRecyclarView = v.findViewById(R.id.contactpersons_recyclar);
        mLandlineRecyclarView = v.findViewById(R.id.landlinerecycler);
        mPersonsRecyclarView.setAdapter(contactPersonsAdapter);
        mLandlineRecyclarView.setAdapter(companyLandLineNumbersAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
        mPersonsRecyclarView.setLayoutManager(linearLayoutManager);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
//        linearLayoutManager2.setReverseLayout(true);
//        linearLayoutManager2.setStackFromEnd(true);
        mLandlineRecyclarView.setLayoutManager(linearLayoutManager2);
        mPersonsRecyclarView.setNestedScrollingEnabled(false);
        mLandlineRecyclarView.setNestedScrollingEnabled(false);


        mAddContactPersonTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add a blank Person Object in contact Persons list
                Logger.v(" add contact person");
                for(int i=0;i<mContactPersonsList.size();i++){
                    View v = mPersonsRecyclarView.getLayoutManager().findViewByPosition(i);
                    EditText name = v.findViewById(R.id.contact_person_name);
                    EditText number = v.findViewById(R.id.contact_person_number);
                    mContactPersonsList.set(i,new ContactPersonPojo(name.getText().toString().trim(),number.getText().toString().trim()));
                }
                mContactPersonsList.add(new ContactPersonPojo("", ""));
                contactPersonsAdapter.notifyDataSetChanged();
            }
        });

        mAddCompanyTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add a blank landline in landline numbers list
                Logger.v(" add landline person");
                for(int i=0;i<mCompanyLandLineNumbers.size();i++){
                    View v = mLandlineRecyclarView.getLayoutManager().findViewByPosition(i);
                    EditText number = v.findViewById(R.id.landline_number);
                    mCompanyLandLineNumbers.set(i,number.getText().toString().trim());
                }
                mCompanyLandLineNumbers.add("");
                companyLandLineNumbersAdapter.notifyDataSetChanged();

            }
        });
        return v;
    }

    private void init() {
        //firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onUpdate(PartnerInfoPojo partnerInfoPojo) {

    }

    public void doLaunchContactPicker(View view) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    // handle contact results
                    Bundle extras = data.getExtras();
                    Set keys = extras.keySet();
                    Iterator iterate = keys.iterator();
                    while (iterate.hasNext()) {
                        String key = iterate.next().toString();
                        Logger.v("CONTACTS :"+ key + "[" + extras.get(key) + "]");
                    }
                    Uri result = data.getData();
                    Logger.v("CONTACTS :"+ "Got a result: "
                            + result.toString());
                    break;
            }
        } else {
            // gracefully handle failure
            Logger.v("CONTACTS :"+ "Warning: activity result not ok");
        }

    }


}
