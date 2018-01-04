package directory.tripin.com.tripindirectory.newactivities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.GeoPoint;
import com.keiferstone.nonet.NoNet;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.adapters.CapsulsRecyclarAdapter;
import directory.tripin.com.tripindirectory.adapters.FleetForViewerAdapter;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.model.ContactPersonPojo;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;
import directory.tripin.com.tripindirectory.model.response.Vehicle;
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
    TextUtils textUtils;
    List<String> mSourceList;
    List<String> mDestList;
    TextView mAddress;

    TextView mServiceTypes;
    TextView mNatureOfBusiness;
    TextView mTitleRating;
    TextView mImagesUploadedInst;
    List<Vehicle> fleetlist;
    RecyclerView mFleetRecycler;
    RecyclerView mSourceCitiesRecycler;
    RecyclerView mDestCitiesRecycler;

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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        textUtils = new TextUtils();
        toolbarLayout.setTitle(textUtils.toTitleCase(getIntent().getExtras().getString("cname") + ""));
        uid = getIntent().getExtras().getString("uid");
        mUserDocRef = FirebaseFirestore.getInstance()
                .collection("partners").document(uid);
        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) findViewById(R.id.mapView);
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
                        if (partnerInfoPojo.getmContactPersonsList().get(i) != null) {
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

        mServiceTypes = findViewById(R.id.typesofservicetv);
        mTitleRating = findViewById(R.id.titleratingtext);
        mImagesUploadedInst = findViewById(R.id.imagesinstruction);
        mNatureOfBusiness = findViewById(R.id.natureofbustv);

        mSourceCitiesRecycler = findViewById(R.id.rv_source);
        mSourceList = new ArrayList<>();
        capsulsRecyclarAdapter = new CapsulsRecyclarAdapter(mSourceList);
        mSourceCitiesRecycler.setAdapter(capsulsRecyclarAdapter);
        LinearLayoutManager layoutManagerhor1
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mSourceCitiesRecycler.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL));
        mSourceCitiesRecycler.setNestedScrollingEnabled(false);

        mDestCitiesRecycler = findViewById(R.id.rv_destination);
        mDestList = new ArrayList<>();
        capsulsRecyclarAdapter2 = new CapsulsRecyclarAdapter(mDestList);
        mDestCitiesRecycler.setAdapter(capsulsRecyclarAdapter2);
        LinearLayoutManager layoutManagerhor2
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mDestCitiesRecycler.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL));
        mDestCitiesRecycler.setNestedScrollingEnabled(false);

        mFleetRecycler = findViewById(R.id.fleetrecyclar);
        fleetlist = new ArrayList<>();
        fleetForViewerAdapter = new FleetForViewerAdapter(fleetlist);
        mFleetRecycler.setAdapter(fleetForViewerAdapter);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        mFleetRecycler.setLayoutManager(linearLayoutManager2);
        mFleetRecycler.setNestedScrollingEnabled(false);


        fabCall = findViewById(R.id.fabCall);
        toolbarLayout.setSoundEffectsEnabled(true);

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
       /*
       //in old Api Needs to call MapsInitializer before doing any CameraUpdateFactory call
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
       */


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

//                        Picasso.with(PartnerDetailScrollingActivity.this)
//                                .load(partnerInfoPojo.getmImagesUrl().get(1))
//                                .into(new Target() {
//                                    @Override
//                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//
//                                        Palette.from(bitmap)
//                                                .generate(new Palette.PaletteAsyncListener() {
//                                                    @Override
//                                                    public void onGenerated(Palette palette) {
//                                                        Palette.Swatch textSwatch = palette.getDominantSwatch();
//                                                        if (textSwatch == null) {
//                                                            Toast.makeText(PartnerDetailScrollingActivity.this, "Null swatch :(", Toast.LENGTH_SHORT).show();
//                                                            return;
//                                                        }
//                                                        Logger.v("swatch" + textSwatch.toString());
//                                                        toolbarLayout.setExpandedTitleColor(textSwatch.getTitleTextColor());
//                                                        fabCall.setBackgroundColor(textSwatch.getBodyTextColor());
//                                                        fabCall.setBackgroundTintList(ColorStateList.valueOf(textSwatch.getTitleTextColor()));
//
//
//                                                    }
//                                                });
//                                    }
//
//                                    @Override
//                                    public void onBitmapFailed(Drawable errorDrawable) {
//
//                                    }
//
//                                    @Override
//                                    public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                                    }
//                                });
//

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
                    String addresstoset = partnerInfoPojo.getmCompanyAdderss().getAddress();
                    mAddress.setText(addresstoset);

                    //set address marker
                    if(partnerInfoPojo.getmLatLng()!=null){
                        map.addMarker(new MarkerOptions()
                                .position(partnerInfoPojo.getmLatLng()).title(textUtils.toTitleCase(partnerInfoPojo.getmCompanyName()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                                .draggable(false).visible(true));
                        // Updates the location and zoom of the MapView
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(partnerInfoPojo.getmLatLng(), 10);
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
                        }
                    }
                    //set source cities
                    mSourceList.clear();
                    for(String s : partnerInfoPojo.getmSourceCities().keySet()){
                        mSourceList.add(s);
                    }
                    capsulsRecyclarAdapter.notifyDataSetChanged();

                    //set destination cities

                    mDestList.clear();
                    for(String s : partnerInfoPojo.getmDestinationCities().keySet()){
                        mDestList.add(s);
                    }
                    capsulsRecyclarAdapter2.notifyDataSetChanged();

                    //set types of service
                    String servicetype = "";
                    for(String s : partnerInfoPojo.getmTypesOfServices().keySet()){
                        if(partnerInfoPojo.getmTypesOfServices().get(s))
                            servicetype = servicetype + s +", ";
                    }
                    if(!servicetype.isEmpty()){
                        servicetype = servicetype.substring(0, servicetype.length() - 2);
                    }
                    mServiceTypes.setText(servicetype);

                    //set types of service
                    String natureofbusiness = "";
                    for(String s : partnerInfoPojo.getmNatureOfBusiness().keySet()){
                        if( partnerInfoPojo.getmNatureOfBusiness().get(s))
                            natureofbusiness = natureofbusiness + s +", ";
                    }
                    if(!natureofbusiness.isEmpty()){
                        natureofbusiness = natureofbusiness.substring(0, natureofbusiness.length() - 2);
                    }
                    mNatureOfBusiness.setText(natureofbusiness);



                    //set fleet
                    if(partnerInfoPojo.getVehicles()!=null){
                        fleetlist.clear();
                        fleetlist.addAll(partnerInfoPojo.getVehicles());
                        fleetForViewerAdapter.notifyDataSetChanged();
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
