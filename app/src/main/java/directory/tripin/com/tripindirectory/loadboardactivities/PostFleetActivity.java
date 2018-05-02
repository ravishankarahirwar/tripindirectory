package directory.tripin.com.tripindirectory.loadboardactivities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import directory.tripin.com.tripindirectory.formactivities.PlacesViewHolder;
import directory.tripin.com.tripindirectory.loadboardactivities.models.CommentPojo;
import directory.tripin.com.tripindirectory.loadboardactivities.models.FleetPostPojo;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.customviews.CustomMapView;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.interfaces.RouteFinderListner;
import directory.tripin.com.tripindirectory.manager.MapAnimator;
import directory.tripin.com.tripindirectory.manager.RouteManager;
import directory.tripin.com.tripindirectory.model.HubFetchedCallback;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;
import directory.tripin.com.tripindirectory.model.RouteCityPojo;
import directory.tripin.com.tripindirectory.utils.TextUtils;

public class PostFleetActivity extends AppCompatActivity implements HubFetchedCallback, OnMapReadyCallback {

    private TextView addPickUpCity, addDropOffCity;
    private RecyclerView mPickUpList, mDropList;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int MAP_PADDING = 50;

    int mPlaceCode = 0;
    MyRecyclerViewAdapter adapterp, adapterd;
    List<String> listpickup, listdropoff;
    List<String> listpickupHub, listdropoffHub;
    SlidingUpPanelLayout sliderLayout;
    TextView togglePanelText, togglePanelText2;
    TextView upload;
    CustomMapView mapView;
    ScrollView mScrollViewInSUP;
    GoogleMap map;
    private LatLngBounds.Builder mLatLngBounds;
    TextUtils textUtils;

    FleetPostPojo fleetPostPojo;
    EditText etMaterial;
    EditText etMaterialWeight;
    EditText etTruckLength;
    EditText etTruckPayLoad;
    EditText etPersonalNote;
    EditText etVehicleNember;
    Spinner truckType;
    Spinner bodyType;
    TextView tvSchleduledDate;
    Calendar myCalendar;

    // Item Views

    public ImageView mPublisherThumbnail;
    public TextView mPostTitle;
    public TextView mPostSubTitle;
    public ImageView mOptions;
    public TextView mScheduledDate;
    public TextView mSource;
    public TextView mDestination;
    public TextView mLoadProperties;
    public TextView mFleetProperties;
    public TextView mDistance;
    public TextView mPersonalNote;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listpickup = new ArrayList<>();
        listdropoff = new ArrayList<>();
        listpickupHub = new ArrayList<>();
        listdropoffHub = new ArrayList<>();

        adapterp = new MyRecyclerViewAdapter(listpickup, 1);
        adapterd = new MyRecyclerViewAdapter(listdropoff, 2);
        setContentView(R.layout.activity_post_fleet);
        sliderLayout = findViewById(R.id.sliding_layout);
        sliderLayout.setTouchEnabled(false);
        addPickUpCity = findViewById(R.id.add_pickupcity);
        addDropOffCity = findViewById(R.id.add_dropcity);
        mPickUpList = findViewById(R.id.pickup_list);
        mDropList = findViewById(R.id.drop_list);
        togglePanelText = findViewById(R.id.textViewtogglePanel);
        togglePanelText2 = findViewById(R.id.readytitle);
        upload = findViewById(R.id.uploadLoadPost);


        mPickUpList.setLayoutManager(new LinearLayoutManager(this));
        mDropList.setLayoutManager(new LinearLayoutManager(this));
        mPickUpList.setAdapter(adapterp);
        mDropList.setAdapter(adapterd);

        // Gets the MapView from the XML layout and creates it
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mScrollViewInSUP = findViewById(R.id.scroll_sup);

        fleetPostPojo = new FleetPostPojo();
        etMaterial = findViewById(R.id.editTextMaterial);
        etMaterialWeight = findViewById(R.id.editTextWeight);
        etPersonalNote = findViewById(R.id.editTextNote);
        etVehicleNember = findViewById(R.id.editTextNumber);
        etTruckLength = findViewById(R.id.editTextLength);
        etTruckPayLoad = findViewById(R.id.editTextPayload);
        truckType = findViewById(R.id.spinnerVehicleType);
        bodyType = findViewById(R.id.spinnerBodyType);
        tvSchleduledDate = findViewById(R.id.mDate);


        mDistance = findViewById(R.id.textViewDistance);
        mFleetProperties = findViewById(R.id.textViewRequiredFleet);
        mLoadProperties = findViewById(R.id.textViewLoadProperties);
        mScheduledDate = findViewById(R.id.textViewDate);
        mPostTitle = findViewById(R.id.poster_title);
        mPostSubTitle = findViewById(R.id.textViewPostingTime);
        mSource = findViewById(R.id.textViewSourceCity);
        mDestination = findViewById(R.id.textViewDestinationCity);
        mPersonalNote = findViewById(R.id.textViewNote);

        textUtils = new TextUtils();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        setListners();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (sliderLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            sliderLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_load, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setListners() {

        addPickUpCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle params = new Bundle();
                mFirebaseAnalytics.logEvent("fleet_pickup_city", params);
                mPlaceCode = 1;
                addPickUpCity.setText("Loading...");
                starttheplacesfragment();
            }
        });

        addDropOffCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle params = new Bundle();
                mFirebaseAnalytics.logEvent("fleet_dropoff_city", params);

                mPlaceCode = 2;
                addDropOffCity.setText("Loading...");
                starttheplacesfragment();
            }
        });

        togglePanelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (truckType.getSelectedItemPosition() != 0) {
                    fleetPostPojo.setmVehicleType(truckType.getSelectedItem().toString().toUpperCase().trim());
                }
                if (bodyType.getSelectedItemPosition() != 0) {
                    fleetPostPojo.setmBodyType(bodyType.getSelectedItem().toString().toUpperCase().trim());
                }

                if (!etTruckPayLoad.getText().toString().isEmpty())
                    fleetPostPojo.setmFleetPayLoad(etTruckPayLoad.getText().toString().trim());

                if (!etTruckLength.getText().toString().isEmpty())
                    fleetPostPojo.setmFleetLength(etTruckLength.getText().toString().trim());

                if (!etPersonalNote.getText().toString().isEmpty())
                    fleetPostPojo.setmPersonalNote(etPersonalNote.getText().toString().trim());

                if (!etVehicleNember.getText().toString().isEmpty())
                    fleetPostPojo.setmVehicleNumber(etVehicleNember.getText().toString().trim());


                if (fleetPostPojo.isMinimumRequiredInputsSet()) {
                    togglePanelText.setText("Loading...");


                    //set up map
                    drawRouteOnMap(new LatLng(fleetPostPojo.getmSourceCityLatLang().getLatitude(),
                            fleetPostPojo.getmSourceCityLatLang().getLongitude()),new LatLng(fleetPostPojo.getmDestinationCityLatLang().getLatitude(),
                            fleetPostPojo.getmDestinationCityLatLang().getLongitude()));
                    //get pojo ready


                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    if (mAuth.getCurrentUser() != null) {
                        fleetPostPojo.setmRMN(mAuth.getCurrentUser().getPhoneNumber());
                        fleetPostPojo.setmPostersUid(mAuth.getCurrentUser().getUid());
                        FirebaseFirestore.getInstance().collection("partners").document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
                                    PartnerInfoPojo partnerInfoPojo = documentSnapshot.toObject(PartnerInfoPojo.class);
                                    if(partnerInfoPojo.getmCompanyName()!=null){
                                        fleetPostPojo.setmCompanyName(textUtils.toTitleCase(partnerInfoPojo.getmCompanyName()));
                                    }
                                    if(partnerInfoPojo.getmFcmToken()!=null){
                                        fleetPostPojo.setmFcmToken(partnerInfoPojo.getmFcmToken());
                                    }
                                    if(partnerInfoPojo.getmImagesUrl()!=null){
                                        fleetPostPojo.setmImagesUrl(partnerInfoPojo.getmImagesUrl());
                                    }
                                }
                                //set post item
                                String myFormat = "dd/MM/yyyy"; //In which you need put here
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                                String DisplayDate = sdf.format(fleetPostPojo.getmPickUpTimeStamp());
                                mScheduledDate.setText("Scheduled Date : "+DisplayDate+" ("+gettimeDiff(fleetPostPojo.getmPickUpTimeStamp())+" days left)");

                                if(!fleetPostPojo.getmCompanyName().isEmpty()){
                                    mPostTitle.setText(fleetPostPojo.getmCompanyName());
                                }else {
                                    mPostTitle.setText(fleetPostPojo.getmRMN());
                                }

                                mSource.setText(fleetPostPojo.getmSourceCity());
                                mDestination.setText(fleetPostPojo.getmDestinationCity());
                                mDistance.setText(fleetPostPojo.getmEstimatedDistance()+"\nkm");

                                String loadProperties = fleetPostPojo.getmVehicleNumber();
                                mLoadProperties.setText(" "+loadProperties);
                                if(loadProperties.length()>20){
                                    mLoadProperties.setSelected(true);
                                }

                                String fleetProperties = textUtils.toTitleCase(fleetPostPojo.getmVehicleType())
                                        +", "+textUtils.toTitleCase(fleetPostPojo.getmBodyType())
                                        +", "+textUtils.toTitleCase(fleetPostPojo.getmFleetPayLoad())+"MT, "
                                        +textUtils.toTitleCase(fleetPostPojo.getmFleetLength())+"Ft";
                                mFleetProperties.setText(fleetProperties);
                                if(fleetProperties.length()>20){
                                    mFleetProperties.setSelected(true);
                                }

                                if(!fleetPostPojo.getmPersonalNote().isEmpty())
                                    mPersonalNote.setText("\""+fleetPostPojo.getmPersonalNote()+"\"");

                                //caluclate ananlytics
                                sliderLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                                togglePanelText.setText("Post");
                            }
                        });
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "Fill Up Required Fields!", Toast.LENGTH_LONG).show();
                }
            }
        });
        togglePanelText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sliderLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
        tvSchleduledDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myCalendar = Calendar.getInstance();

                Logger.v("onClick Date Picker");
                DatePickerDialog datePickerDialog = new DatePickerDialog(PostFleetActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear,
                                          int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateLabel();
                    }
                }, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();

            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload.setText("Uploading...");
                final CommentPojo commentPojo;

                commentPojo = new CommentPojo(fleetPostPojo.getmPersonalNote()+". Initial Comment!",
                        fleetPostPojo.getmCompanyName(),
                        fleetPostPojo.getmRMN(),
                        fleetPostPojo.getmFcmToken(), fleetPostPojo.getmDocId(),fleetPostPojo.getmPostersUid());
                commentPojo.setmImagesUrl(fleetPostPojo.getmImagesUrl());

                DocumentReference rf = FirebaseFirestore.getInstance().collection("fleets").document();
                fleetPostPojo.setmDocId(rf.getId());
                rf.set(fleetPostPojo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseFirestore.getInstance()
                                .collection("fleets")
                                .document(fleetPostPojo.getmDocId())
                                .collection("mCommentsCollection")
                                .add(commentPojo)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        //comment added
                                        //subscribe to topic
                                        Bundle params = new Bundle();
                                        mFirebaseAnalytics.logEvent("post_fleet", params);

                                        FirebaseMessaging.getInstance().subscribeToTopic(fleetPostPojo.getmDocId());
                                        Toast.makeText(getApplicationContext(), "Fleet Posted Successfully!", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                });
                    }
                });
            }
        });
    }

    private void updateDateLabel() {
        Bundle params = new Bundle();
        mFirebaseAnalytics.logEvent("fleet_add_date", params);

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        tvSchleduledDate.setText(sdf.format(myCalendar.getTime()));
        fleetPostPojo.setmPickUpTimeStamp(myCalendar.getTime());

    }


    private void starttheplacesfragment() {
        try {

            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                    .setCountry("IN")
                    .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                final Place place = PlaceAutocomplete.getPlace(this, data);
                Logger.v("Place::: " + place.getName());
                if (mPlaceCode == 1) {
                    listpickup.add(place.getName().toString().toUpperCase());

                    Toast.makeText(this, "Pick City Added", Toast.LENGTH_SHORT).show();
                    addPickUpCity.setText("Add More");
                    addPickUpCity.setVisibility(View.GONE);
                    fleetPostPojo.setmSourceCity(place.getName().toString().toUpperCase());
                    fleetPostPojo.setmSourceCityLatLang(new GeoPoint(place.getLatLng().latitude,place.getLatLng().longitude));
                    //updateSourceHubs(place.getName().toString(), 1);

                    adapterp.notifyDataSetChanged();

                }
                if (mPlaceCode == 2) {
                    listdropoff.add(place.getName().toString().toUpperCase());

                    Toast.makeText(this, "Drop City Added", Toast.LENGTH_SHORT).show();
                    addDropOffCity.setText("Add More");
                    addDropOffCity.setVisibility(View.GONE);
                    fleetPostPojo.setmDestinationCity(place.getName().toString().toUpperCase());
                    fleetPostPojo.setmDestinationCityLatLang(new GeoPoint(place.getLatLng().latitude,place.getLatLng().longitude));
//                    fleetPostPojo.setmEstimatedDistance(getDistance(fleetPostPojo.getmDestinationCityLatLang().getLatitude(),
//                            fleetPostPojo.getmDestinationCityLatLang().getLongitude(),
//                            fleetPostPojo.getmSourceCityLatLang().getLatitude(),
//                            fleetPostPojo.getmSourceCityLatLang().getLongitude())+"");
                    // updateDestinationHubs(place.getName().toString(), 1);
                    adapterd.notifyDataSetChanged();
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                addPickUpCity.setText("Add");
                addDropOffCity.setText("Add");


            }
        }
    }

    private void updateDestinationHubs(String city, int operation) {

        RouteCityPojo routeCityPojo = new RouteCityPojo(this, 2, operation, this);
        routeCityPojo.setmCityName(city);

    }

    private void updateSourceHubs(String city, int operation) {

        RouteCityPojo routeCityPojo = new RouteCityPojo(this, 1, operation, this);
        routeCityPojo.setmCityName(city);

    }

    @Override
    public void onDestinationHubFetched(String destinationhub, int operaion) {

        switch (operaion) {
            case 1: {
                // set destination
                fleetPostPojo.setmDestinationHub(destinationhub.toUpperCase());
                break;
            }
            case 2: {
                //unset destination
                fleetPostPojo.setmDestinationHub("");
                break;
            }
        }
    }

    @Override
    public void onSourceHubFetched(String sourcehub, int operation) {
        switch (operation) {
            case 1: {
                // set destination
                fleetPostPojo.setmSourceHub(sourcehub.toUpperCase());
                break;
            }
            case 2: {
                //unset destination
                fleetPostPojo.setmSourceHub("");
                break;
            }
        }
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
    }


    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<PlacesViewHolder> {

        private List<String> mData;
        private int type = 0;

        // data is passed into the constructor
        public MyRecyclerViewAdapter(List<String> data, int type) {
            this.mData = data;
            this.type = type;
        }

        // inflates the row layout from xml when needed
        @Override
        public PlacesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city, parent, false);
            PlacesViewHolder viewHolder = new PlacesViewHolder(view);
            return viewHolder;
        }

        // binds the data to the textview in each row
        @Override
        public void onBindViewHolder(final PlacesViewHolder holder, final int position) {
            String city = mData.get(position);
            holder.mCity.setText(city);
            holder.mRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.mCity.setText("Removing...");
                    //remove city
                    if (type == 1) {
                        //remove pickup
                        listpickup.remove(position);
                        fleetPostPojo.setmSourceCity("");
                        fleetPostPojo.setmSourceHub("");
                        notifyDataSetChanged();
                        addPickUpCity.setVisibility(View.VISIBLE);
                        addPickUpCity.setText("Add");

                    }
                    if (type == 2) {
                        //remove drop off
                        updateDestinationHubs(mData.get(position), 2);
                        fleetPostPojo.setmDestinationCity("");
                        fleetPostPojo.setmDestinationHub("");
                        listdropoff.remove(position);
                        notifyDataSetChanged();
                        addDropOffCity.setVisibility(View.VISIBLE);
                        addDropOffCity.setText("Add");
                    }

                }
            });
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mData.size();
        }


    }

    private void drawRouteOnMap(LatLng source, LatLng destination) {
        if (isInternetAvailable(getApplicationContext())) {
            RouteManager routeManager = new RouteManager(source, destination, new RouteFinderListner() {
                @Override
                public void onRouteFind(ArrayList<LatLng> points) {
                    PolylineOptions lineOptions = new PolylineOptions();
                    lineOptions.addAll(points);
                    lineOptions.geodesic(true);
                    lineOptions.width(5);
                    lineOptions.color(Color.BLACK);
                    // Drawing polyline in the Google Map for the i-th route
                    if (lineOptions != null) {

                        mLatLngBounds = new LatLngBounds.Builder();
                        mLatLngBounds.include(new LatLng(fleetPostPojo.getmSourceCityLatLang().getLatitude(),
                                fleetPostPojo.getmSourceCityLatLang().getLongitude()));
                        mLatLngBounds.include(new LatLng(fleetPostPojo.getmDestinationCityLatLang().getLatitude(),
                                fleetPostPojo.getmDestinationCityLatLang().getLongitude()));
                        map.clear();

//                        Marker pickUpMarker = map.addMarker(new MarkerOptions()
//                                .position(new LatLng(fleetPostPojo.getmSourceCityLatLang().getLatitude(),
//                                        fleetPostPojo.getmSourceCityLatLang().getLongitude())));
//                        Marker dropMarker = map.addMarker(new MarkerOptions()
//                                .position(new LatLng(fleetPostPojo.getmDestinationCityLatLang().getLatitude(),
//                                        fleetPostPojo.getmDestinationCityLatLang().getLongitude())));

                        /**create the bounds from latlngBuilder to set into map camera*/
                        LatLngBounds bounds = mLatLngBounds.build();
                        /**create the camera with bounds and padding to set into map*/
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, MAP_PADDING);
                        map.animateCamera(cu);
                        startAnim(points);
                    } else {
                        Logger.v("onPostExecute : without Polylines drawn");
                    }
                }

                @Override
                public void onNoRouteFind() {
                    Toast.makeText(getApplicationContext(), "No route found", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDistanceFind(String distance) {
                    fleetPostPojo.setmEstimatedDistance(distance);
                    Logger.v("Estimated Distance: "+distance);
                    mDistance.setText(distance + "\nkm");
                }

                @Override
                public void onEstimatedTimeFind(String time) {

                }
            });
        }
    }

    private void startAnim(ArrayList<LatLng> points) {
        if (map != null) {
            if (points != null && points.size() > 1) {
                MapAnimator mapAnimator = new MapAnimator();
                mapAnimator.animateRoute(map, points);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Map not ready", Toast.LENGTH_LONG).show();
        }
    }

    public static boolean isInternetAvailable(Context context) {
        if (context == null) {
            return false;
        }
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //we are connected to a network
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
        return connected;
    }

    private int getDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (int) dist;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public String gettimeDiff(Date startDate){

        String diff = "" ;

        if(startDate!=null){

            Date endDate = new Date();

            long duration  = endDate.getTime() - startDate.getTime();
            if(duration<0){
                return Math.abs(duration) / (1000 * 60 * 60 * 24)+"";
            }
            long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
            long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);

            if(diffInSeconds==0){
                return "Realtime!";
            }

            if(diffInSeconds<60){
                diff = ""+diffInSeconds+" sec ago";
            }else if(diffInMinutes<60){
                diff = ""+diffInMinutes+" min ago";
            }else if(diffInHours<24){
                diff = ""+diffInHours+" hrs ago";
            }else {

                long daysago = duration / (1000 * 60 * 60 * 24);
                diff = ""+daysago+" days ago";
            }

        }
        return diff;

    }
}
