package directory.tripin.com.tripindirectory.FormActivities.FormFragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.activity.AddCompanyActivity;
import directory.tripin.com.tripindirectory.getcity.general.CityAutoCompleteTextView;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.model.CompanyAddressPojo;
import directory.tripin.com.tripindirectory.model.ContactPersonPojo;
import directory.tripin.com.tripindirectory.model.ImageData;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;
import directory.tripin.com.tripindirectory.utils.EasyImagePickUP;
import directory.tripin.com.tripindirectory.viewmodel.AddPerson;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompanyFromFragment extends BaseFragment {


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

    private StorageReference mStorageRef;
    StorageReference imagesRef;
    ProgressDialog progressDialog;
    List<String> mUrlList;


    public CompanyFromFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        //setUpView();
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.v("uploadingdata1.....");
        //uploadData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_company_from, container, false);

        init(v);

        String companyName = mCompanyNmae.getText().toString();
        String companyAddress = mCompanyAddress.getText().toString();
        String companyCity = mCompanyCity.getText().toString();
        String companyState = mCompanyState.getText().toString();

        List<ContactPersonPojo> contactPersonPojos = new ArrayList<>();

        for (int i = 0; i < mContactPersons.size(); i++) {
            AddPerson addPerson = mContactPersons.get(i);
            String name = addPerson.getPersonName().getText().toString();
            String number = addPerson.getPesonContact().getText().toString();
            contactPersonPojos.add(new ContactPersonPojo(name, number));
        }


        for (int i = 0; i < mLandlineNumbers.size(); i++) {
            AddPerson addPerson = mLandlineNumbers.get(i);
            String landlineNumber = addPerson.getPersonName().getText().toString();
            companyLandlineNumbers.add(landlineNumber);
        }
        return v;
    }

    private void init(View v) {
        //firebase
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(getActivity());

        mContactPersons = new ArrayList<AddPerson>();
        mLandlineNumbers = new ArrayList<AddPerson>();
        companyLandlineNumbers = new ArrayList<>();

        mContext = getActivity();

        mCompanyNmae = (EditText)v.findViewById(R.id.company_name);
        mCompanyAddress  = (EditText)v.findViewById(R.id.input_company_address);
        mCompanyCity  = (EditText)v.findViewById(R.id.input_city);
        mCompanyState  = (EditText)v.findViewById(R.id.input_state);
        mAddPerson = (TextView)v.findViewById(R.id.add_person);
        mLandLine = (TextView)v.findViewById(R.id.add_landline);


        mLandlineNmber = v.findViewById(R.id.landline_number);

        mPersonName = v.findViewById(R.id.contact_person_name);
        mPersonContact = v.findViewById(R.id.contact_person_number);

        AddPerson addPerson = new AddPerson(mContext);
        addPerson.setPersonName(mPersonName);
        addPerson.setPesonContact(mPersonContact);
        mContactPersons.add(addPerson);

        mAddPersonLayout = (LinearLayout) v.findViewById(R.id.add_person_layout);
        mAddLandLineLayout = (LinearLayout) v.findViewById(R.id.landline_number_layout);

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
            }
        });
    }

    private void addContactPerson() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
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
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View addPersonView = inflater.inflate(R.layout.include_add_landline, null);
        EditText landlineNmber = addPersonView.findViewById(R.id.landline_number);

        AddPerson addPerson = new AddPerson(mContext);
        addPerson.setPersonName(landlineNmber);

        mLandlineNumbers.add(addPerson);
        mAddLandLineLayout.addView(addPersonView);
    }

    private void addContactPerson(String name, String number) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
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
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View addPersonView = inflater.inflate(R.layout.include_add_landline, null);
        EditText landlineNmber = addPersonView.findViewById(R.id.landline_number);

        AddPerson addPerson = new AddPerson(mContext);
        addPerson.setPersonName(landlineNmber);

        landlineNmber.setText(landlineNumber);

        mLandlineNumbers.add(addPerson);
        mAddLandLineLayout.addView(addPersonView);
    }

    @Override
    public void onUpdate(PartnerInfoPojo partnerInfoPojo) {

    }

    private void uploadData() {
        String companyName = mCompanyNmae.getText().toString();
        String companyAddress = mCompanyAddress.getText().toString();
        String companyCity = mCompanyCity.getText().toString();
        String companyState = mCompanyState.getText().toString();

        List<ContactPersonPojo> contactPersonPojos = new ArrayList<>();

        for (int i = 0; i < mContactPersons.size(); i++) {
            AddPerson addPerson = mContactPersons.get(i);
            String name = addPerson.getPersonName().getText().toString();
            String number = addPerson.getPesonContact().getText().toString();
            contactPersonPojos.add(new ContactPersonPojo(name, number));
        }


        for (int i = 0; i < mLandlineNumbers.size(); i++) {
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

        PartnerInfoPojo partnerInfoPojo =
                new PartnerInfoPojo(companyName,
                        contactPersonPojos,
                        companyLandlineNumbers,
                        companyAddressPojo,
                        urllist, false, source, destination);

        db.collection("partners").document(mAuth.getUid()).set(partnerInfoPojo);
        Toast.makeText(getActivity(), "Data uploaded!", Toast.LENGTH_LONG).show();
    }

    private void setUpView() {

        CollectionReference cities = db.collection("partners");
        DocumentReference docRef = cities.document(mAuth.getUid());

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
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

                    } else if(company.getmContactPersonsList().size() == 1) {
                        String name = company.getmContactPersonsList().get(0).getmContactPresonName();
                        String number = company.getmContactPersonsList().get(0).getGetmContactPersonMobile();
                        mPersonName.setText(name);
                        mPersonContact.setText(number);

                    } else if(company.getmContactPersonsList().size() == 1) {
                        String number = company.getmCompanyLandLineNumbers().get(0);
                        mLandlineNmber.setText(number);
                    }

//                    if(company.getmCompanyLandLineNumbers().size() > 1) {
//                        String number = company.getmCompanyLandLineNumbers().get(0);
//                        mLandlineNmber.setText(number);
//                        for (int i = 1; i < company.getmCompanyLandLineNumbers().size(); i++) {
//                            String number1 = company.getmCompanyLandLineNumbers().get(i);
//                            addLandLineNumber(number1);
//                        }
//                    }
                }




            }});

    }

}
