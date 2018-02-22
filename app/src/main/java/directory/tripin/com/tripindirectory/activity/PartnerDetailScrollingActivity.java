package directory.tripin.com.tripindirectory.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.keiferstone.nonet.NoNet;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.adapters.CapsulsRecyclarAdapter;
import directory.tripin.com.tripindirectory.adapters.ContactPersonsAdapterViewOnly;
import directory.tripin.com.tripindirectory.adapters.FleetForViewerAdapter;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.model.ContactPersonPojo;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;
import directory.tripin.com.tripindirectory.model.response.Vehicle;
import directory.tripin.com.tripindirectory.model.search.Fleet;
import directory.tripin.com.tripindirectory.model.search.Truck;
import directory.tripin.com.tripindirectory.model.search.TruckProperty;
import directory.tripin.com.tripindirectory.utils.TextUtils;

public class PartnerDetailScrollingActivity extends AppCompatActivity implements OnMapReadyCallback,RatingDialogListener {
    SliderLayout sliderLayout;
    DocumentReference mUserDocRef;
    String uid;
    Toolbar toolbar;
    CollapsingToolbarLayout toolbarLayout;
    FloatingActionButton fabCall;
    MapView mapView;
    GoogleMap map;
    PartnerInfoPojo partnerInfoPojo;
    Context mContext;
    RatingBar ratingBar;
    List<String> mSourceList;
    List<String> mDestList;
    TextView mAddress;
    TextUtils textUtils;

    private TextView mFleetWorkingWith;
    private TextView mServiceTypes;
    private TextView mNatureOfBusiness;
    private TextView mTitleRating;
    private TextView mImagesUploadedInst;
    private List<ContactPersonPojo> mContactPersonsList;
    private List<Vehicle> fleetlist;


    private RecyclerView mCompanyContacts;

    RecyclerView mFleetRecycler;
    RecyclerView mSourceCitiesRecycler;
    RecyclerView mDestCitiesRecycler;

    private ContactPersonsAdapterViewOnly mContactPersonsAdapter;
    CapsulsRecyclarAdapter capsulsRecyclarAdapter;
    CapsulsRecyclarAdapter capsulsRecyclarAdapter2;

    FleetForViewerAdapter fleetForViewerAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NoNet.monitor(this)
                .poll()
                .snackbar();

        setContentView(R.layout.activity_partner_detail_scrolling);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarLayout = findViewById(R.id.toolbar_layout);
        textUtils = new TextUtils();
        toolbarLayout.setTitle(textUtils.toTitleCase(getIntent().getExtras().getString("cname") + ""));
        uid = getIntent().getExtras().getString("uid");
        mUserDocRef = FirebaseFirestore.getInstance()
                .collection("partners").document(uid);
        // Gets the MapView from the XML layout and creates it
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        init();

        setListners();

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    private void setListners() {

        fabCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle params = new Bundle();
                params.putString("call", "Click");
                mFirebaseAnalytics.logEvent("ClickOnCall", params);

                final ArrayList<String> phoneNumbers = new ArrayList<>();
                List<ContactPersonPojo> contactPersonPojos = partnerInfoPojo.getmContactPersonsList();

                if (contactPersonPojos != null && contactPersonPojos.size() > 1) {
                    for (int i = 0; i < contactPersonPojos.size(); i++) {
                        if (partnerInfoPojo.getmContactPersonsList() != null && partnerInfoPojo.getmContactPersonsList().get(i) != null) {
                            String number = partnerInfoPojo.getmContactPersonsList().get(i).getGetmContactPersonMobile();
                            phoneNumbers.add(number);
                        }
                    }

                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Looks like there are multiple phone numbers.")
                            .setCancelable(false)
                            .setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.dialog_multiple_no_row, R.id.dialog_number, phoneNumbers),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int item) {

                                            Logger.v("Dialog number selected :" + phoneNumbers.get(item));

                                            callNumber(phoneNumbers.get(item));
                                        }
                                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

                    builder.create();
                    builder.show();
                } else {
                    String number = partnerInfoPojo.getmContactPersonsList().get(0).getGetmContactPersonMobile();
                    callNumber(number);
                }
            }
        });

        ratingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //showDialog();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        mContext = PartnerDetailScrollingActivity.this;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        sliderLayout = findViewById(R.id.slider);
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Stack);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(5000);
        ratingBar = findViewById(R.id.ratingBar);

        mAddress = findViewById(R.id.textAddress);

        mFleetWorkingWith = findViewById(R.id.fleet_working_with);
        mServiceTypes = findViewById(R.id.typesofservicetv);
        mTitleRating = findViewById(R.id.titleratingtext);
        mImagesUploadedInst = findViewById(R.id.imagesinstruction);
        mNatureOfBusiness = findViewById(R.id.natureofbustv);

        mSourceCitiesRecycler = findViewById(R.id.rv_source);
        mSourceList = new ArrayList<>();
        mSourceList.add("Loading");
        capsulsRecyclarAdapter = new CapsulsRecyclarAdapter(mSourceList);
        mSourceCitiesRecycler.setAdapter(capsulsRecyclarAdapter);
        LinearLayoutManager layoutManagerhor1
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mSourceCitiesRecycler.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL));
        //mSourceCitiesRecycler.setLayoutManager(layoutManagerhor1);
        mSourceCitiesRecycler.setNestedScrollingEnabled(false);

        mDestCitiesRecycler = findViewById(R.id.rv_destination);
        mDestList = new ArrayList<>();
        mDestList.add("Loading");
        capsulsRecyclarAdapter2 = new CapsulsRecyclarAdapter(mDestList);
        mDestCitiesRecycler.setAdapter(capsulsRecyclarAdapter2);
        LinearLayoutManager layoutManagerhor2
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mDestCitiesRecycler.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL));
        //mDestCitiesRecycler.setLayoutManager(layoutManagerhor2);
        mDestCitiesRecycler.setNestedScrollingEnabled(false);

        mFleetRecycler = findViewById(R.id.fleetrecyclar);
        fleetlist = new ArrayList<>();
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        mFleetRecycler.setLayoutManager(linearLayoutManager2);
        mFleetRecycler.setNestedScrollingEnabled(false);
        fleetForViewerAdapter = new FleetForViewerAdapter(fleetlist);
        mFleetRecycler.setAdapter(fleetForViewerAdapter);

        mCompanyContacts = findViewById(R.id.list_company_contact);
        mContactPersonsList = new ArrayList<>();
        mContactPersonsAdapter = new ContactPersonsAdapterViewOnly(mContactPersonsList);
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(this);
        mCompanyContacts.setLayoutManager(linearLayoutManager3);
        mCompanyContacts.setNestedScrollingEnabled(false);
        mCompanyContacts.setAdapter(mContactPersonsAdapter);

        fabCall = findViewById(R.id.fabCall);
        toolbarLayout.setSoundEffectsEnabled(true);
        textUtils = new TextUtils();

    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        sliderLayout.stopAutoCycle();
        super.onStop();
    }
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);



        mUserDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                //get all partner data
                if (documentSnapshot.exists()) {
                    partnerInfoPojo = documentSnapshot.toObject(PartnerInfoPojo.class);
                    toolbarLayout.setTitle(textUtils.toTitleCase(partnerInfoPojo.getmCompanyName()));


                    //set images stuff

                    if (partnerInfoPojo.getmImagesUrl() != null) {

                        Logger.v("got images url");

                        for (String url : partnerInfoPojo.getmImagesUrl()) {
                            if(!url.isEmpty()){
                                DefaultSliderView defaultSliderView = new DefaultSliderView(PartnerDetailScrollingActivity.this);
                                defaultSliderView.image(url).setScaleType(BaseSliderView.ScaleType.CenterCrop);
                                sliderLayout.addSlider(defaultSliderView);

                            }else {
//                                DefaultSliderView defaultSliderView = new DefaultSliderView(PartnerDetailScrollingActivity.this);
//                                defaultSliderView.image(R.drawable.splash).setScaleType(BaseSliderView.ScaleType.CenterCrop);
//                                sliderLayout.addSlider(defaultSliderView);
//                                sliderLayout.stopAutoCycle();
                            }

                        }
                    } else {
//                        DefaultSliderView defaultSliderView = new DefaultSliderView(PartnerDetailScrollingActivity.this);
//                        defaultSliderView.image(R.drawable.company_logo).setScaleType(BaseSliderView.ScaleType.Fit);
//                        sliderLayout.addSlider(defaultSliderView);
//                        sliderLayout.stopAutoCycle();
                        mImagesUploadedInst.setVisibility(View.VISIBLE);

                    }

                    //set rating
                    mTitleRating.setText(textUtils.toTitleCase(partnerInfoPojo.getmCompanyName())+", Rated 3.7/5.0");

                    //set address
                    String addresstoset
                            = partnerInfoPojo.getmCompanyAdderss().getAddress()
                            +", "+textUtils.toTitleCase(partnerInfoPojo.getmCompanyAdderss().getCity())
                            +", "+textUtils.toTitleCase(partnerInfoPojo.getmCompanyAdderss().getState());
                    if(partnerInfoPojo.getmCompanyAdderss().getPincode()!=null){
                        addresstoset = addresstoset + ", "+partnerInfoPojo.getmCompanyAdderss().getPincode();
                    }

                    mAddress.setText(addresstoset);

                    //set address marker

                    if(partnerInfoPojo.getmCompanyAdderss().isLatLongSet()){
                        LatLng latLng = new LatLng(Double.parseDouble(partnerInfoPojo.getmCompanyAdderss().getmLatitude())
                                ,Double.parseDouble(partnerInfoPojo.getmCompanyAdderss().getmLongitude()));
                        map.addMarker(new MarkerOptions()
                                .position(latLng).title(textUtils.toTitleCase(partnerInfoPojo.getmCompanyName()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                .draggable(false).visible(true));
                        // Updates the location and zoom of the MapView
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
                        map.animateCamera(cameraUpdate);
                        Logger.v("camera should be updated");
                    }else {

                        LatLng latLng = getLocationFromAddress(getApplicationContext(),partnerInfoPojo.getmCompanyAdderss().getAddress());
                        if(latLng!=null){
                            map.addMarker(new MarkerOptions()
                                    .position(latLng).title(textUtils.toTitleCase(partnerInfoPojo.getmCompanyName()))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                    .draggable(false).visible(true));
                            // Updates the location and zoom of the MapView
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
                            map.animateCamera(cameraUpdate);
                        }else {
                            // Updates the location and zoom of the MapView
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(20,78), 20);
                            map.animateCamera(cameraUpdate);
                        }
                    }
                    //set source cities
                    mSourceList.clear();
                    if(partnerInfoPojo.getmSourceCities() != null && partnerInfoPojo.getmSourceCities().keySet() != null) {

                        mSourceList.addAll(partnerInfoPojo.getmSourceCities().keySet());
                    }
                    capsulsRecyclarAdapter.notifyDataSetChanged();


                    //set destination cities
                    mDestList.clear();
                    if(partnerInfoPojo.getmDestinationCities() != null &&  partnerInfoPojo.getmDestinationCities().keySet() != null) {
                        mDestList.addAll(partnerInfoPojo.getmDestinationCities().keySet());
                    }
                    capsulsRecyclarAdapter2.notifyDataSetChanged();


                    //set types of service
                    String servicetype = "";
                    if(partnerInfoPojo.getmTypesOfServices() != null && partnerInfoPojo.getmTypesOfServices().keySet() != null){
                        for(String s : partnerInfoPojo.getmTypesOfServices().keySet()){
                            if(partnerInfoPojo.getmTypesOfServices().get(s))
                                servicetype += "\u25CF  " + s +"\n";
                        }
                    }
                    if(!servicetype.isEmpty()){
                        mServiceTypes.setText(servicetype);
                    }

                    //set types of service
                    String natureofbusiness = "";
                    if(partnerInfoPojo.getmNatureOfBusiness()!=null){
                        for(String s : partnerInfoPojo.getmNatureOfBusiness().keySet()){
                            if( partnerInfoPojo.getmNatureOfBusiness().get(s))
                                natureofbusiness += "\u25CF  " +  s +"\n";
                        }
                    }

                    if(!natureofbusiness.isEmpty()){
                        natureofbusiness = natureofbusiness.substring(0, natureofbusiness.length() - 2);
                    }
                    mNatureOfBusiness.setText(natureofbusiness);

                    String fj = partnerInfoPojo.getFleetJson();
                    if(fj != null && fj.length() > 0 ) {
                        Gson gson = new Gson();
                        Fleet fleetWorkingWith = gson.fromJson(fj, Fleet.class);
                        String fleetList = "";
                        for (int i = 0; i < fleetWorkingWith.getTrucks().size(); i++) {
                            Truck truck = fleetWorkingWith.getTrucks().get(i);
                            if(truck.isTruckHave()) {
                                fleetList += "\u25CF "+truck.getTruckType() + "\n";
                            }
                            for (int j = 0; j < truck.getTruckProperties().size(); j++) {
                                TruckProperty truckProperty = truck.getTruckProperties().get(j);
                                    Map<String, Boolean> property = truckProperty.getProperties();
                                    for (Map.Entry<String, Boolean> entry : property.entrySet()) {
                                        if (entry.getValue()) {
                                            fleetList += "    \u25CB  " + truckProperty.getTitle() + " : " + entry.getKey() + "\n";
                                        }
                                    }
                            }

                            fleetList += "\n";
                        }

                        if(fleetList.length() > 0 ) {
                            mFleetWorkingWith.setText(fleetList);
                        }
                    }

                    //set fleet
                    if(partnerInfoPojo.getVehicles()!= null){
                        fleetlist.clear();
                        fleetlist.addAll(partnerInfoPojo.getVehicles());
                        fleetForViewerAdapter.notifyDataSetChanged();
                    }

                    if(partnerInfoPojo.getmContactPersonsList() != null){
                        mContactPersonsList.clear();
                        mContactPersonsList.addAll(partnerInfoPojo.getmContactPersonsList());
                        mContactPersonsAdapter.notifyDataSetChanged();
                    }
                } else {
                }
            }
        });
    }

    private void callNumber(String number) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + Uri.encode(number.trim())));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(callIntent);
    }

    @Override
    public void onPositiveButtonClicked(int i, String s) {

    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

//    private void showDialog() {
//        new AppRatingDialog.Builder()
//                .setPositiveButtonText("Submit")
//                .setNegativeButtonText("Cancel")
//                .setNeutralButtonText("Later")
//                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
//                .setDefaultRating(2)
//                .setTitle("Rate this application")
//                .setDescription("Please select some stars and give your feedback")
//                .setDefaultComment("This app is pretty cool !")
//                .setStarColor(R.color.colorAccent)
//                .setNoteDescriptionTextColor(R.color.colorAccent)
//                .setTitleTextColor(R.color.colorAccent)
//                .setDescriptionTextColor(R.color.colorAccent)
//                .setHint("Please write your comment here ...")
//                .setHintTextColor(R.color.clear_btn_color)
//                .setCommentTextColor(R.color.colorPrimary)
//                .setCommentBackgroundColor(R.color.colorPrimaryDark)
//                .setWindowAnimation(R.style.MyDialogFadeAnimation)
//                .create(PartnerDetailScrollingActivity.this)
//                .show();
//    }


    public LatLng getLocationFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address != null && address.size() > 0) {
                Address location = address.get(0);
                location.getLatitude();
                location.getLongitude();
                p1 = new LatLng(location.getLatitude(), location.getLongitude() );
                return p1;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return p1;
        }
        return p1;
    }
}
