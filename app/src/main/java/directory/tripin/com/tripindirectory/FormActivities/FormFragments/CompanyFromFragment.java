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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import directory.tripin.com.tripindirectory.FormActivities.CheckBoxRecyclarAdapter;
import directory.tripin.com.tripindirectory.FormActivities.CompanyLandLineNumbersAdapter;
import directory.tripin.com.tripindirectory.FormActivities.ContactPersonsAdapter;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.model.CompanyAddressPojo;
import directory.tripin.com.tripindirectory.model.ContactPersonPojo;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;
import directory.tripin.com.tripindirectory.utils.TextUtils;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompanyFromFragment extends BaseFragment {

    private static final int CONTACT_PICKER_RESULT = 1001;
    private static final int PLACE_PICKER_REQUEST = 1;

    DocumentReference mUserDocRef;
    FirebaseAuth auth;
    TextUtils textUtils;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private EditText mCompanyNmae;
    private EditText mCompanyAddress;
    private EditText mCompanyCity;
    private EditText mCompanyState;
    private TextView mAddContactPersonTxt;
    private LatLng latLng;


    private TextView mAddCompanyTxt;
    private TextView mLocateBusiness;

    private RecyclerView mPersonsRecyclarView;
    private RecyclerView mLandlineRecyclarView;
    private RecyclerView mNatureOfBusinessRecyclarView;
    private RecyclerView mTypesOfServicesRecyclarView;
    private HashMap<String, Boolean> mNatureofBusinessHashMap;
    private HashMap<String, Boolean> mTypesofServicesHashMap;
    private CheckBoxRecyclarAdapter checkBoxRecyclarAdapter1;
    private CheckBoxRecyclarAdapter checkBoxRecyclarAdapter2;


    private List<ContactPersonPojo> mContactPersonsList;
    private List<String> mCompanyLandLineNumbers;
    private ContactPersonsAdapter contactPersonsAdapter;
    private CompanyLandLineNumbersAdapter companyLandLineNumbersAdapter;
    private LinearLayout mLoadingDataLin;

    private ImageView togglenoblist, toggletoslist;

    public CompanyFromFragment() {

        //initialize hashmaps
        mContactPersonsList = new ArrayList<>();
        mCompanyLandLineNumbers = new ArrayList<>();

        mNatureofBusinessHashMap = new HashMap<>();
        mNatureofBusinessHashMap.put("Fleet Owner".toUpperCase(), false);
        mNatureofBusinessHashMap.put("Transport Contractor".toUpperCase(), false);
        mNatureofBusinessHashMap.put("Commission Agent".toUpperCase(), false);

        mTypesofServicesHashMap = new HashMap<>();
        mTypesofServicesHashMap.put("FTL".toUpperCase(), false);
        mTypesofServicesHashMap.put("Part Loads".toUpperCase(), false);
        mTypesofServicesHashMap.put("Open Body Truck Load".toUpperCase(), false);
        mTypesofServicesHashMap.put("Trailer Load".toUpperCase(), false);
        mTypesofServicesHashMap.put("Parcel".toUpperCase(), false);
        mTypesofServicesHashMap.put("ODC".toUpperCase(), false);
        mTypesofServicesHashMap.put("Import Containers".toUpperCase(), false);
        mTypesofServicesHashMap.put("Export Containers".toUpperCase(), false);
        mTypesofServicesHashMap.put("Chemical".toUpperCase(), false);
        mTypesofServicesHashMap.put("Petrol".toUpperCase(), false);
        mTypesofServicesHashMap.put("Diesel".toUpperCase(), false);
        mTypesofServicesHashMap.put("Oil".toUpperCase(), false);


        //creat adapters and set adapters
        mContactPersonsList.add(new ContactPersonPojo("", ""));
        contactPersonsAdapter = new ContactPersonsAdapter(mContactPersonsList);

        mCompanyLandLineNumbers.add("");
        companyLandLineNumbersAdapter = new CompanyLandLineNumbersAdapter(mCompanyLandLineNumbers);

        checkBoxRecyclarAdapter1 = new CheckBoxRecyclarAdapter(mNatureofBusinessHashMap);
        checkBoxRecyclarAdapter2 = new CheckBoxRecyclarAdapter(mTypesofServicesHashMap);
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
                isDataFatched = true;
                if (task.isSuccessful() && task.getResult().exists()) {
                    mLoadingDataLin.setVisibility(View.GONE);
                    PartnerInfoPojo partnerInfoPojo = task.getResult().toObject(PartnerInfoPojo.class);

                    if (partnerInfoPojo.getmContactPersonsList() != null) {
                        mContactPersonsList.clear();
                        mContactPersonsList.addAll(partnerInfoPojo.getmContactPersonsList());
                        contactPersonsAdapter.notifyDataSetChanged();
                    }

                    if (partnerInfoPojo.getmCompanyLandLineNumbers() != null) {
                        mCompanyLandLineNumbers.clear();
                        mCompanyLandLineNumbers.addAll(partnerInfoPojo.getmCompanyLandLineNumbers());
                        companyLandLineNumbersAdapter.notifyDataSetChanged();
                    }




                    mCompanyNmae.setText(partnerInfoPojo.getmCompanyName());

                    if (partnerInfoPojo.getmCompanyAdderss() != null) {
                        mCompanyAddress.setText(partnerInfoPojo.getmCompanyAdderss().getAddress());
                        mCompanyCity.setText(partnerInfoPojo.getmCompanyAdderss().getCity());
                        mCompanyState.setText(partnerInfoPojo.getmCompanyAdderss().getState());
                    }

                    if (partnerInfoPojo.getmNatureOfBusiness() != null) {
                        if (partnerInfoPojo.getmNatureOfBusiness().size() < 3) {
                            Set<String> set = partnerInfoPojo.getmNatureOfBusiness().keySet();
                            List<String> list = new ArrayList<>();
                            list.addAll(set);
                            for (int i = 0; i < list.size(); i++) {
                                mNatureofBusinessHashMap.put(list.get(i), true);
                            }
                            checkBoxRecyclarAdapter1.notifyDataSetChanged();

                        } else {
                            mNatureofBusinessHashMap.clear();
                            mNatureofBusinessHashMap.putAll(partnerInfoPojo.getmNatureOfBusiness());
                            checkBoxRecyclarAdapter1.notifyDataSetChanged();
                        }

                    }

                    if (partnerInfoPojo.getmTypesOfServices() != null) {
                        if (partnerInfoPojo.getmTypesOfServices().size() < 10) {
                            Set<String> set = partnerInfoPojo.getmTypesOfServices().keySet();
                            List<String> list = new ArrayList<>();
                            list.addAll(set);
                            for (int i = 0; i < list.size(); i++) {
                                mTypesofServicesHashMap.put(list.get(i), true);
                            }
                            checkBoxRecyclarAdapter2.notifyDataSetChanged();
                        }
                        mTypesofServicesHashMap.clear();
                        mTypesofServicesHashMap.putAll(partnerInfoPojo.getmTypesOfServices());
                        checkBoxRecyclarAdapter2.notifyDataSetChanged();
                    }

                    if(partnerInfoPojo.getmLatLng()!=null){
                        mLocateBusiness.setText("Locate Your Business On Map(set)");
                    }


                    Logger.v("On Data Fetch and set Company Data");

                }

            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isDataFatched = false;
        textUtils = new TextUtils();
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
        if (isDataFatched) {
            //set contacts
            List<ContactPersonPojo> contacts = new ArrayList<>();
            for (int i = 0; i < mContactPersonsList.size(); i++) {
                View v = mPersonsRecyclarView.getLayoutManager().findViewByPosition(i);
                EditText name = v.findViewById(R.id.contact_person_name);
                EditText number = v.findViewById(R.id.contact_person_number);
                contacts.add(new ContactPersonPojo(name.getText().toString().trim(), number.getText().toString().trim()));
            }
            List<String> landlines = new ArrayList<>();
            for (int i = 0; i < mCompanyLandLineNumbers.size(); i++) {
                View v = mLandlineRecyclarView.getLayoutManager().findViewByPosition(i);
                EditText number = v.findViewById(R.id.landline_number);
                landlines.add(number.getText().toString().trim());
            }
            PartnerInfoPojo partnerInfoPojo = new PartnerInfoPojo();
            partnerInfoPojo.setContactPersonsList(contacts);
            partnerInfoPojo.setmCompanyLandLineNumbers(landlines);


            //setname
            partnerInfoPojo.setCompanyName(mCompanyNmae.getText().toString().trim().toUpperCase());

            //setaddress
            partnerInfoPojo
                    .setCompanyAdderss(new CompanyAddressPojo(mCompanyAddress.getText().toString().trim(),
                            mCompanyCity.getText().toString().trim().toUpperCase(),
                            mCompanyState.getText().toString().trim().toUpperCase()));

            //mUserDocRef.set(partnerInfoPojo, SetOptions.merge());
            mUserDocRef.update("mCompanyName", partnerInfoPojo.getmCompanyName());

            HashMap<String, List<ContactPersonPojo>> hashMap = new HashMap<>();
            hashMap.put("mContactPersonsList", partnerInfoPojo.getmContactPersonsList());
            mUserDocRef.set(hashMap, SetOptions.merge());

            HashMap<String, List<String>> hashMap2 = new HashMap<>();
            hashMap2.put("mCompanyLandLineNumbers", partnerInfoPojo.getmCompanyLandLineNumbers());
            mUserDocRef.set(hashMap2, SetOptions.merge());

            HashMap<String, CompanyAddressPojo> hashMap3 = new HashMap<>();
            hashMap3.put("mCompanyAdderss", partnerInfoPojo.getmCompanyAdderss());
            mUserDocRef.set(hashMap3, SetOptions.merge());

            HashMap<String, HashMap<String, Boolean>> hashMap4 = new HashMap<>();
            hashMap4.put("mNatureOfBusiness", checkBoxRecyclarAdapter1.getmDataMap());
            mUserDocRef.set(hashMap4, SetOptions.merge());

            HashMap<String, HashMap<String, Boolean>> hashMap5 = new HashMap<>();
            hashMap5.put("mTypesOfServices", checkBoxRecyclarAdapter2.getmDataMap());
            mUserDocRef.set(hashMap5, SetOptions.merge());

            HashMap<String, String> hashMap6 = new HashMap<>();
            hashMap6.put("mRMN", mAuth.getCurrentUser().getPhoneNumber() + "");
            mUserDocRef.set(hashMap6, SetOptions.merge());

            HashMap<String, LatLng> hashMap7 = new HashMap<>();
            if(latLng!=null){
                hashMap7.put("mLatlng",latLng );
                mUserDocRef.set(hashMap7, SetOptions.merge());
            }

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
        mLocateBusiness = v.findViewById(R.id.locate);
        mLoadingDataLin = v.findViewById(R.id.ll_loading);
        togglenoblist = v.findViewById(R.id.nobup);
        toggletoslist = v.findViewById(R.id.nobup2);


        mPersonsRecyclarView = v.findViewById(R.id.contactpersons_recyclar);
        mPersonsRecyclarView.setAdapter(contactPersonsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mPersonsRecyclarView.setLayoutManager(linearLayoutManager);
        mPersonsRecyclarView.setNestedScrollingEnabled(false);


        mLandlineRecyclarView = v.findViewById(R.id.landlinerecycler);
        mLandlineRecyclarView.setAdapter(companyLandLineNumbersAdapter);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
        mLandlineRecyclarView.setLayoutManager(linearLayoutManager2);
        mLandlineRecyclarView.setNestedScrollingEnabled(false);

        mNatureOfBusinessRecyclarView = v.findViewById(R.id.rv_natureofbusiness);
        mNatureOfBusinessRecyclarView.setAdapter(checkBoxRecyclarAdapter1);
        mNatureOfBusinessRecyclarView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mNatureOfBusinessRecyclarView.setNestedScrollingEnabled(false);

        mTypesOfServicesRecyclarView = v.findViewById(R.id.rv_typesofservices);
        mTypesOfServicesRecyclarView.setAdapter(checkBoxRecyclarAdapter2);
        mTypesOfServicesRecyclarView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTypesOfServicesRecyclarView.setNestedScrollingEnabled(false);


        mAddContactPersonTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add a blank Person Object in contact Persons list
                Logger.v(" add contact person");
                for (int i = 0; i < mContactPersonsList.size(); i++) {
                    View v = mPersonsRecyclarView.getLayoutManager().findViewByPosition(i);
                    EditText name = v.findViewById(R.id.contact_person_name);
                    EditText number = v.findViewById(R.id.contact_person_number);
                    mContactPersonsList.set(i, new ContactPersonPojo(name.getText().toString().trim(), number.getText().toString().trim()));
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
                for (int i = 0; i < mCompanyLandLineNumbers.size(); i++) {
                    View v = mLandlineRecyclarView.getLayoutManager().findViewByPosition(i);
                    EditText number = v.findViewById(R.id.landline_number);
                    mCompanyLandLineNumbers.set(i, number.getText().toString().trim());
                }
                mCompanyLandLineNumbers.add("");
                companyLandLineNumbersAdapter.notifyDataSetChanged();
            }
        });

        toggletoslist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTypesOfServicesRecyclarView.getVisibility() == View.VISIBLE) {
                    mTypesOfServicesRecyclarView.setVisibility(View.GONE);
                    toggletoslist.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                } else {
                    mTypesOfServicesRecyclarView.setVisibility(View.VISIBLE);
                    toggletoslist.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);

                }
            }
        });
        togglenoblist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mNatureOfBusinessRecyclarView.getVisibility() == View.VISIBLE) {
                    mNatureOfBusinessRecyclarView.setVisibility(View.GONE);
                    togglenoblist.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                } else {
                    mNatureOfBusinessRecyclarView.setVisibility(View.VISIBLE);
                    togglenoblist.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);

                }
            }
        });

        mLocateBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    startPlacePicker();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                }

            }
        });
        return v;
    }

    private void startPlacePicker() throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        PlacePicker.IntentBuilder builder;
        builder = new PlacePicker.IntentBuilder();
        startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);

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
                        Logger.v("CONTACTS :" + key + "[" + extras.get(key) + "]");
                    }
                    Uri result = data.getData();
                    Logger.v("CONTACTS :" + "Got a result: "
                            + result.toString());
                    break;

                case PLACE_PICKER_REQUEST: {
                    Place place = PlacePicker.getPlace(getContext(),data);
                    if(place.getLatLng()!=null){
                        latLng = place.getLatLng();
                        mLocateBusiness.setText("Locate Your Business On Map(set)");
                    }


                }

            }
        } else {
            // gracefully handle failure
            Logger.v("CONTACTS :" + "Warning: activity result not ok");
        }

    }


}
