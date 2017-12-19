package directory.tripin.com.tripindirectory.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.adapters.ImagesRecyclarAdapter;
import directory.tripin.com.tripindirectory.getcity.managers.ContentManager;
import directory.tripin.com.tripindirectory.model.AddImage;

import directory.tripin.com.tripindirectory.getcity.general.CityAutoCompleteTextView;
import directory.tripin.com.tripindirectory.getcity.general.MyAutoCompleteAdapter;
import directory.tripin.com.tripindirectory.model.CompanyAddressPojo;
import directory.tripin.com.tripindirectory.model.ContactPersonPojo;
import directory.tripin.com.tripindirectory.model.ImageData;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;
import directory.tripin.com.tripindirectory.utils.EasyImagePickUP;

import directory.tripin.com.tripindirectory.newactivities.MainActivity1;
import directory.tripin.com.tripindirectory.utils.SpaceTokenizer;
import directory.tripin.com.tripindirectory.viewmodel.AddPerson;

public class AddCompanyActivity extends AppCompatActivity implements AddImage, EasyImagePickUP.ImagePickerListener {

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


    boolean canSubmit = true;
    boolean areImagesUploaded = false;
    RecyclerView recyclerView;
    List<ImageData> images;
    List<Uri> imagesUriList;
    EasyImagePickUP easyImagePickUP;
    ImagesRecyclarAdapter imagesRecyclarAdapter;
    int position;
    private StorageReference mStorageRef;
    StorageReference imagesRef;
    ProgressDialog progressDialog;
    List<String> mUrlList;


    private CityAutoCompleteTextView mPickUpCities;
    private CityAutoCompleteTextView mDropCities;
    private MyAutoCompleteAdapter mCityAdapter;
    private static final String[] CITIES = new String[] {
            "Mumbai", "Rajkot", "Nagpur", "Ahmedabad", "Bhopal","Indore","Kanpur","New Delhi"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_company);
        init();
        setListners();

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        mUrlList = new ArrayList<>();

        //initially add 3 blank ImageData Objects
        images = new ArrayList<>();
        images.add(new ImageData());
        images.add(new ImageData());
        images.add(new ImageData());

        imagesRecyclarAdapter = new ImagesRecyclarAdapter(images,this,this);

        fetchImagesURL();
        fetchUserDataandDispaly();

        setUpView();
    }

    private void fetchUserDataandDispaly() {

    }

    private void setUpView() {

        CollectionReference cities = db.collection("partners");
        DocumentReference docRef = db.collection("partners").document(mAuth.getUid());

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    PartnerInfoPojo company = documentSnapshot.toObject(PartnerInfoPojo.class);
                    mCompanyNmae.setText(company.getmCompanyName());
                    mCompanyAddress.setText(company.getmCompanyAdderss().getmAddress().toString());
                    mCompanyCity.setText(company.getmCompanyAdderss().getmCity().toString());
                    mCompanyState.setText(company.getmCompanyAdderss().getmState().toString());

                    Iterator pickupCityIterator = company.getmSourceCities().keySet().iterator();
                    while(pickupCityIterator.hasNext()) {
                        String key=(String)pickupCityIterator.next();
                        mPickUpCities.append(key);
                    }

                    Iterator dropCityIterator = company.getDestinationCities().keySet().iterator();
                    while(dropCityIterator.hasNext()) {
                        String key=(String)dropCityIterator.next();
                        mDropCities.append(key);
                    }

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

                if(company.getmCompanyLandLineNumbers().size() > 1) {
                    String number = company.getmCompanyLandLineNumbers().get(0);
                    mLandlineNmber.setText(number);
                    for (int i = 1; i < company.getmCompanyLandLineNumbers().size(); i++) {
                        String number1 = company.getmCompanyLandLineNumbers().get(i);
                        addLandLineNumber(number1);
                    }
                    }
            }
        }});

    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    private void fetchImagesURL() {

        db.collection("partners").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    PartnerInfoPojo partnerInfoPojo = task.getResult().toObject(PartnerInfoPojo.class);
                    mUrlList = partnerInfoPojo.getImagesUrl();
                    if(mUrlList!=null){
                        for(int i=0;i<mUrlList.size();i++){
                            images.get(i).setmImageUrl(mUrlList.get(i));
                        }
                        imagesRecyclarAdapter.notifyDataSetChanged();
                    }

                }
            }
        });


        recyclerView.setAdapter(imagesRecyclarAdapter);
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

    @Override
    public void onPickClicked(int position) {
        easyImagePickUP.imagepicker(1);
        Log.e("tagg", "onPickClicked");
        this.position = position;

    }

    @Override
    public void onCancelClicked(int position) {
        Log.e("tagg", "onCancel");

        images.set(position, new ImageData());
        imagesRecyclarAdapter.notifyDataSetChanged();

    }

    @Override
    public void onPicked(int i, String s, Bitmap bitmap, Uri uri) {
        Log.e("tagg", "onPicked" + s);
        images.set(position, new ImageData(uri, bitmap));
        imagesUriList.add(uri);
        imagesRecyclarAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCropped(int i, String s, Bitmap bitmap, Uri uri) {
        Log.e("tagg", "onPicked" + s);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        easyImagePickUP.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        easyImagePickUP.request_permission_result(requestCode, permissions, grantResults);
    }


    private void init() {
        //firebase
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        easyImagePickUP = new EasyImagePickUP(this);
        imagesUriList = new ArrayList<>();
        progressDialog = new ProgressDialog(AddCompanyActivity.this);

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
        mPickUpCities = (CityAutoCompleteTextView) this.findViewById(R.id.city_title);
        mDropCities = (CityAutoCompleteTextView) this.findViewById(R.id.drop_city);
        setupCityNameField();

        mLandlineNmber = findViewById(R.id.landline_number);

        mPersonName = findViewById(R.id.contact_person_name);
        mPersonContact = findViewById(R.id.contact_person_number);

        AddPerson addPerson = new AddPerson(mContext);
        addPerson.setPersonName(mPersonName);
        addPerson.setPesonContact(mPersonContact);
        mContactPersons.add(addPerson);

        mAddPersonLayout = (LinearLayout) this.findViewById(R.id.add_person_layout);
        mAddLandLineLayout = (LinearLayout) this.findViewById(R.id.landline_number_layout);

        AddPerson addLandline = new AddPerson(mContext);
        addLandline.setPersonName(mLandlineNmber);
        mLandlineNumbers.add(addLandline);

        recyclerView = findViewById(R.id.imageslist);
        recyclerView.hasFixedSize();
        recyclerView.setNestedScrollingEnabled(false);


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

        landlineNmber.setText(landlineNumber);

        mLandlineNumbers.add(addPerson);
        mAddLandLineLayout.addView(addPersonView);
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

        uploadImagesandGetURL(0);
    }

    private void uploadData() {
        String companyName = mCompanyNmae.getText().toString();
        String companyAddress = mCompanyAddress.getText().toString();
        String companyCity = mCompanyCity.getText().toString();
        String companyState = mCompanyState.getText().toString();
        String pickupCities = mPickUpCities.getText().toString();
        String dropCities = mDropCities.getText().toString();


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

        String [] pickupArr = pickupCities.split(",");
        String [] dropArr = dropCities.split(",");

        for(String pickupcityString : pickupArr) {
            source.put(pickupcityString.trim(), true);
        }

        for(String dropcityString : dropArr) {
            destination.put(dropcityString.trim(), true);
        }

//        source.put("mumbai", true);
//        source.put("nagpur", true);
//        destination.put("rajkot", true);
//        destination.put("gandhinagar", true);

        List<String> urls = new ArrayList<>();
        for(ImageData imageData: images){
            urls.add(imageData.getmImageUrl());
        }


        PartnerInfoPojo partnerInfoPojo =
                new PartnerInfoPojo(companyName,
                        contactPersonPojos,
                        companyLandlineNumbers,
                        companyAddressPojo,
                        urls, false, source, destination);

        db.collection("partners").document(mAuth.getUid()).set(partnerInfoPojo);
        Toast.makeText(this, "Data uploaded!", Toast.LENGTH_LONG).show();
    }

    void uploadImagesandGetURL(final int index) {
        if(images.get(index).getSet()){
            Uri file = images.get(index).getmImageUri();
            if (file != null) {
                if (mAuth.getCurrentUser() != null) {
                    imagesRef = mStorageRef.child(mAuth.getUid()).child(index + ".jpeg");
                    progressDialog.setTitle("uploading image " + index);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    imagesRef.putFile(file)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get a URL to the uploaded content
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    images.get(index).setmImageUrl(downloadUrl.toString());
                                    progressDialog.hide();
                                    if (index + 1 <= 2) {
                                        uploadImagesandGetURL(index + 1);
                                    } else {
                                        uploadData();
                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Log.e("onSuccessUpload..", exception.toString());

                                    // Handle unsuccessful uploads
                                    // ...
                                }
                            });
                }
            }
        }else {
            if (index < 2) {
                    uploadImagesandGetURL(index + 1);
            }else {
                Toast.makeText(mContext, "no images to upload", Toast.LENGTH_SHORT).show();
                uploadData();
            }

        }
        Toast.makeText(this, "uploaded", Toast.LENGTH_LONG).show();
    }


    private void setupCityNameField() {
        mPickUpCities.setTokenizer(new SpaceTokenizer());
        mPickUpCities.setThreshold(1);
        mPickUpCities.setCursorVisible(true);

        mDropCities.setTokenizer(new SpaceTokenizer());
        mDropCities.setThreshold(1);
        mDropCities.setCursorVisible(true);

        mCityAdapter = new MyAutoCompleteAdapter(this);

//        If you want city come from google
//        ArrayList<String> predictionList = new ArrayList<>();
//        predictionList = ContentManager.getInstance(mContext).getPredictionDescriptionList();

        ArrayAdapter citiesAdapter= new ArrayAdapter<String>(AddCompanyActivity.this,  android.R.layout.simple_dropdown_item_1line, CITIES);
        mPickUpCities.setAdapter(citiesAdapter);
        mDropCities.setAdapter(citiesAdapter);

        mPickUpCities.setLoadingIndicator((ProgressBar) findViewById(R.id.progressBar));
        mDropCities.setLoadingIndicator((ProgressBar) findViewById(R.id.progressBarDestination));

        mPickUpCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String city = (String) adapterView.getItemAtPosition(position);
//                String cityStr = "";
//                String[] itemList = city.split(",");
//                if (itemList.length > 0) {
//                    cityStr = itemList[0];
//                }

                mPickUpCities.append(city.toString()+",");
                mPickUpCities.setSelection(mPickUpCities.getText().length());
            }
        });
    }

}
