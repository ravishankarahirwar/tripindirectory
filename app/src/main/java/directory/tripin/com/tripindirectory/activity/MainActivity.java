package directory.tripin.com.tripindirectory.activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.keiferstone.nonet.NoNet;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import directory.tripin.com.tripindirectory.FormActivities.CheckBoxRecyclarAdapter;
import directory.tripin.com.tripindirectory.FormActivities.CompanyInfoActivity;
import directory.tripin.com.tripindirectory.LoadBoardActivities.LoadBoardActivity;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.adapters.FirstItemMainViewHolder;
import directory.tripin.com.tripindirectory.adapters.PartnersViewHolder;
import directory.tripin.com.tripindirectory.forum.models.User;
import directory.tripin.com.tripindirectory.helper.ListPaddingDecoration;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.helper.RecyclerViewAnimator;
import directory.tripin.com.tripindirectory.manager.PreferenceManager;
import directory.tripin.com.tripindirectory.model.ContactPersonPojo;
import directory.tripin.com.tripindirectory.model.ImageData;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;
import directory.tripin.com.tripindirectory.model.SuggestionCompanyName;
import directory.tripin.com.tripindirectory.utils.SearchData;
import directory.tripin.com.tripindirectory.utils.TextUtils;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_INVITE = 1001;
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    private static final int SEARCHTAG_ROUTE = 0;
    private static final int SEARCHTAG_COMPANY = 1;
    private static final int SEARCHTAG_CITY = 2;
    private static final int SEARCHTAG_TRANSPORTER = 3;

    private static final int SIGN_IN_FOR_CREATE_COMPANY = 123;
    private static final int SIGN_IN_FOR_FORUM = 222;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    private List<SuggestionCompanyName> companySuggestions = null;
    private List<String> companynamesuggestions = null;
    private DocumentReference mUserDocRef;
    private FirebaseAuth auth;
    private FirestoreRecyclerOptions<PartnerInfoPojo> options;
    private FirestoreRecyclerAdapter adapter;
    boolean isCompanySuggestionClicked = false;
    private Context mContext;
    private RecyclerView mPartnerList;
    private PreferenceManager mPreferenceManager;
    private FloatingSearchView mSearchView;
    private DrawerLayout mDrawerLayout;
    private RadioGroup mSearchTagRadioGroup;
    private int searchTag = 0;
    private String mSearchQuery = "";
    private SearchData mSearchData;
    private Query query;
    private GeoDataClient mGeoDataClient;
    private boolean isSourceSelected = false;
    private boolean isDestinationSelected = false;
    private String mSourceCity = "";
    private String mDestinationCity = "";

    private RadioButton radioButton3;
    private RadioButton radioButton2;
    private RadioButton radioButton1;
    private RadioButton radioButton4;
    private FirebaseAnalytics mFirebaseAnalytics;
    private RecyclerViewAnimator mAnimator;
    LottieAnimationView lottieAnimationView;
    TextUtils textUtils;
    SlidingUpPanelLayout sliderLayout;
    private Button mBtnApplyFilters;
    private Button mBtnClearFilters;

    //bottom status bar
    private TextView mFilterPanelToggle;
    private TextView mSortPanelToggle;
    private LottieAnimationView mBookmarkPanelToggle;

    private DatabaseReference mDatabase;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    private HashMap<String, Boolean> mNatureofBusinessHashMap;
    private HashMap<String, Boolean> mTypesofServicesHashMap;
    private HashMap<String, Boolean> mTypesofVehiclesHashMap;
    private HashMap<String, Boolean> mTypesofBodyHashMap;
    private HashMap<String, Boolean> mTypesofWeightsHashMap;
    private HashMap<String, Boolean> mTypesofLengthsHashMap;
    private List<String> mFiltersList;


    private CheckBoxRecyclarAdapter checkBoxRecyclarAdapter1;
    private CheckBoxRecyclarAdapter checkBoxRecyclarAdapter2;
    private CheckBoxRecyclarAdapter checkBoxRecyclarAdapter3;
    private CheckBoxRecyclarAdapter checkBoxRecyclarAdapter4;
    private CheckBoxRecyclarAdapter checkBoxRecyclarAdapter5;
    private CheckBoxRecyclarAdapter checkBoxRecyclarAdapter6;


    private RecyclerView mNatureOfBusinessRecyclarView;
    private RecyclerView mTypesOfServicesRecyclarView;
    private RecyclerView mTypesofVehiclesRecyclarView;
    private RecyclerView mTypesofBodyRecyclarView;
    private RecyclerView mTypesofWeightsRecyclarView;
    private RecyclerView mTypesofLengthsRecyclarView;

    private ImageView togglenoblist, toggletoslist, tbtov, tbtob, tbwc, tblov;
    private TextView mTextCount;
    private Dialog dialog;
    private boolean isApplyFilterPressed;
    private View mFilterView, mSortView, mBookmarkView;

    private RadioButton radioButtonAlphabetically,radioButtonRatings,radioButtonFavourite,radioButtonCrediblity;
    private RadioGroup mSortRadioGroup;
    private Button mBtnApplySorts;
    private Button mBtnClearSorts;
    private int mSortIndex;
    boolean isApplySortPressed;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NoNet.monitor(this)
                .poll()
                .snackbar();
        //Add to Activity
        //For Production
        FirebaseMessaging.getInstance().subscribeToTopic("generalUpdates");

        //For Testing
//        FirebaseMessaging.getInstance().subscribeToTopic("generalUpdatesTest");
        textUtils = new TextUtils();
        FirebaseMessaging.getInstance().subscribeToTopic("generalUpdatesTest");
        //Add to Activity
        FirebaseMessaging.getInstance().subscribeToTopic("loadboardNotification");

        setContentView(R.layout.activity_home);

        mNatureofBusinessHashMap = new HashMap<>();
        mTypesofServicesHashMap = new HashMap<>();
        mTypesofVehiclesHashMap = new HashMap<>();
        mTypesofBodyHashMap = new HashMap<>();
        mTypesofWeightsHashMap = new HashMap<>();
        mTypesofLengthsHashMap = new HashMap<>();

        initiateFiltersHashmaps();



        checkBoxRecyclarAdapter1 = new CheckBoxRecyclarAdapter(mNatureofBusinessHashMap);
        checkBoxRecyclarAdapter2 = new CheckBoxRecyclarAdapter(mTypesofServicesHashMap);
        checkBoxRecyclarAdapter3 = new CheckBoxRecyclarAdapter(mTypesofVehiclesHashMap);
        checkBoxRecyclarAdapter4 = new CheckBoxRecyclarAdapter(mTypesofBodyHashMap);
        checkBoxRecyclarAdapter5 = new CheckBoxRecyclarAdapter(mTypesofWeightsHashMap);
        checkBoxRecyclarAdapter6 = new CheckBoxRecyclarAdapter(mTypesofLengthsHashMap);


        mNatureOfBusinessRecyclarView = findViewById(R.id.rv_natureofbusiness);
        mNatureOfBusinessRecyclarView.setAdapter(checkBoxRecyclarAdapter1);
        mNatureOfBusinessRecyclarView.setLayoutManager(new LinearLayoutManager(this));
        mNatureOfBusinessRecyclarView.setNestedScrollingEnabled(false);
        togglenoblist = findViewById(R.id.nobup);

        mTypesOfServicesRecyclarView = findViewById(R.id.rv_typesofservices);
        mTypesOfServicesRecyclarView.setAdapter(checkBoxRecyclarAdapter2);
        mTypesOfServicesRecyclarView.setLayoutManager(new LinearLayoutManager(this));
        mTypesOfServicesRecyclarView.setNestedScrollingEnabled(false);
        toggletoslist = findViewById(R.id.nobup2);

        mTypesofVehiclesRecyclarView = findViewById(R.id.rv_tov);
        mTypesofVehiclesRecyclarView.setAdapter(checkBoxRecyclarAdapter3);
        mTypesofVehiclesRecyclarView.setLayoutManager(new LinearLayoutManager(this));
        mTypesofVehiclesRecyclarView.setNestedScrollingEnabled(false);
        tbtov = findViewById(R.id.tovup);


        mTypesofBodyRecyclarView = findViewById(R.id.rv_tob);
        mTypesofBodyRecyclarView.setAdapter(checkBoxRecyclarAdapter4);
        mTypesofBodyRecyclarView.setLayoutManager(new LinearLayoutManager(this));
        mTypesofBodyRecyclarView.setNestedScrollingEnabled(false);
        tbtob = findViewById(R.id.tobup);


        mTypesofWeightsRecyclarView = findViewById(R.id.rv_wc);
        mTypesofWeightsRecyclarView.setAdapter(checkBoxRecyclarAdapter5);
        mTypesofWeightsRecyclarView.setLayoutManager(new LinearLayoutManager(this));
        mTypesofWeightsRecyclarView.setNestedScrollingEnabled(false);
        tbwc = findViewById(R.id.wcup);


        mTypesofLengthsRecyclarView = findViewById(R.id.rv_lov);
        mTypesofLengthsRecyclarView.setAdapter(checkBoxRecyclarAdapter6);
        mTypesofLengthsRecyclarView.setLayoutManager(new LinearLayoutManager(this));
        mTypesofLengthsRecyclarView.setNestedScrollingEnabled(false);
        tblov = findViewById(R.id.lovup);


        init();

        if (mPreferenceManager.isFirstTime()) {
            Logger.v("First Time app opened");
            mPreferenceManager.setFirstTime(false);
            startActivity(new Intent(mContext, TutorialScreensActivity.class));
        } else {
            Logger.v("Multiple times app opened");
        }

        setAdapter("");
    }

    private void initiateFiltersHashmaps() {

        mNatureofBusinessHashMap.put("Fleet Owner".toUpperCase(), false);
        mNatureofBusinessHashMap.put("Transport Contractor".toUpperCase(), false);
        mNatureofBusinessHashMap.put("Commission Agent".toUpperCase(), false);

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

        mTypesofVehiclesHashMap.put("LCV".toUpperCase(), false);
        mTypesofVehiclesHashMap.put("Truck".toUpperCase(), false);
        mTypesofVehiclesHashMap.put("Tusker".toUpperCase(), false);
        mTypesofVehiclesHashMap.put("Taurus".toUpperCase(), false);
        mTypesofVehiclesHashMap.put("Trailers".toUpperCase(), false);
        mTypesofVehiclesHashMap.put("Container Body".toUpperCase(), false);
        mTypesofVehiclesHashMap.put("Refrigerated Vans".toUpperCase(), false);
        mTypesofVehiclesHashMap.put("Tankers".toUpperCase(), false);
        mTypesofVehiclesHashMap.put("Tippers".toUpperCase(), false);
        mTypesofVehiclesHashMap.put("Bulkers".toUpperCase(), false);
        mTypesofVehiclesHashMap.put("Car Carriers".toUpperCase(), false);
        mTypesofVehiclesHashMap.put("Scooter Body".toUpperCase(), false);
        mTypesofVehiclesHashMap.put("Hydraulic Axles".toUpperCase(), false);

        mTypesofBodyHashMap.put("Normal".toUpperCase(), false);
        mTypesofBodyHashMap.put("Full Body".toUpperCase(), false);
        mTypesofBodyHashMap.put("Half Body".toUpperCase(), false);
        mTypesofBodyHashMap.put("Open Body".toUpperCase(), false);
        mTypesofBodyHashMap.put("Platform".toUpperCase(), false);
        mTypesofBodyHashMap.put("Skeleton".toUpperCase(), false);
        mTypesofBodyHashMap.put("Semi-low".toUpperCase(), false);
        mTypesofBodyHashMap.put("Low".toUpperCase(), false);
        mTypesofBodyHashMap.put("Trolla/Body Trailer".toUpperCase(), false);
        mTypesofBodyHashMap.put("Refer".toUpperCase(), false);
        mTypesofBodyHashMap.put("High Cube (HC)".toUpperCase(), false);
        mTypesofBodyHashMap.put("Normal".toUpperCase(), false);

        mTypesofWeightsHashMap.put("Between 0-10 MT".toUpperCase(), false);
        mTypesofWeightsHashMap.put("Between 10-20 MT".toUpperCase(), false);
        mTypesofWeightsHashMap.put("Between 20-30 MT".toUpperCase(), false);
        mTypesofWeightsHashMap.put("Above 30 MT".toUpperCase(), false);

        mTypesofLengthsHashMap.put("Between 0-10 Ft".toUpperCase(), false);
        mTypesofLengthsHashMap.put("Between 10-20 Ft".toUpperCase(), false);
        mTypesofLengthsHashMap.put("Between 20-30 Ft".toUpperCase(), false);
        mTypesofLengthsHashMap.put("Above 30 Ft".toUpperCase(), false);
    }

    private void init() {
        mContext = MainActivity.this;
        mSearchData = new SearchData();
        mGeoDataClient = Places.getGeoDataClient(this, null);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        FirebaseApp.initializeApp(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mSearchView = findViewById(R.id.floating_search_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mPartnerList = findViewById(R.id.transporter_list);
        mSearchTagRadioGroup = findViewById(R.id.search_tag_group);
        mPartnerList.setLayoutManager(new LinearLayoutManager(this));
        ListPaddingDecoration listPaddingDecoration = new ListPaddingDecoration(getApplicationContext());
        mPartnerList.addItemDecoration(listPaddingDecoration);

        sliderLayout = findViewById(R.id.sliding_layout);
        //sliderLayout.setTouchEnabled(false);
        mFilterPanelToggle = findViewById(R.id.filter);
        mSortPanelToggle = findViewById(R.id.sort);
        mBookmarkPanelToggle = findViewById(R.id.animation_bookmark);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mPreferenceManager = PreferenceManager.getInstance(mContext);
        companySuggestions = new ArrayList<>();
        searchTag = SEARCHTAG_ROUTE;
        mSearchView.setShowSearchKey(true);
        mSearchView.attachNavigationDrawerToMenuButton(mDrawerLayout);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.toolbar_background));
        }

        radioButton3 = findViewById(R.id.search_by_transporter);
        radioButton2 = findViewById(R.id.search_by_company);
        radioButton1 = findViewById(R.id.search_by_route);
        radioButton4 = findViewById(R.id.search_by_city);

        lottieAnimationView = findViewById(R.id.animation_view);

        mBtnApplyFilters = findViewById(R.id.buttonApplyFilters);
        mBtnClearFilters = findViewById(R.id.buttonClearFilters);

        mFilterView = findViewById(R.id.include_filters);
        mSortView = findViewById(R.id.include_sort);
        mBookmarkView = findViewById(R.id.include_bookmark);

        mTextCount = findViewById(R.id.textViewResCount);

        radioButtonAlphabetically = findViewById(R.id.radioButton1);
        radioButtonRatings = findViewById(R.id.radioButton2);
        radioButtonFavourite = findViewById(R.id.radioButton3);
        radioButtonCrediblity = findViewById(R.id.radioButton4);
        mSortRadioGroup = findViewById(R.id.radioGroupSort);
        mBtnApplySorts = findViewById(R.id.buttonApplySort);
        mBtnClearSorts = findViewById(R.id.buttonClearSort);


        mSearchTagRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int radioButtonID) {
                if (radioButtonID == R.id.search_by_route) {
                    Bundle params = new Bundle();
                    params.putString("search_by", "ByRoute");
                    mFirebaseAnalytics.logEvent("SearchBy", params);

                    searchTag = SEARCHTAG_ROUTE;
                    radioButton1.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_white));
                    //radioButton1.setTypeface(Typeface.DEFAULT_BOLD);
                    radioButton2.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    radioButton3.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    radioButton4.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    mSearchView.clearQuery();
                    mSearchView.setSearchHint("Source To Destination");
                } else if (radioButtonID == R.id.search_by_company) {
                    Bundle params = new Bundle();
                    params.putString("search_by", "ByCompanyName");
                    mFirebaseAnalytics.logEvent("SearchBy", params);
                    searchTag = SEARCHTAG_COMPANY;
                    radioButton1.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    radioButton2.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_white));
                    //radioButton2.setTypeface(Typeface.DEFAULT_BOLD);
                    radioButton3.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    radioButton4.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    mSearchView.clearQuery();
                    mSearchView.setSearchHint("Search by company name");
                } else if (radioButtonID == R.id.search_by_transporter) {
                    Bundle params = new Bundle();
                    params.putString("search_by", "ByTransporter");
                    mFirebaseAnalytics.logEvent("SearchBy", params);
                    searchTag = SEARCHTAG_TRANSPORTER;
                    radioButton1.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    radioButton2.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    radioButton3.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_white));
                    radioButton3.setTypeface(Typeface.DEFAULT_BOLD);
                    radioButton4.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    mSearchView.clearQuery();
                    mSearchView.setSearchHint("Search by transporter name");
                } else if (radioButtonID == R.id.search_by_city) {
                    Bundle params = new Bundle();
                    params.putString("search_by", "ByCity");
                    mFirebaseAnalytics.logEvent("SearchBy", params);
                    searchTag = SEARCHTAG_CITY;
                    radioButton1.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    radioButton2.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    radioButton3.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    radioButton4.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_white));
                    //radioButton4.setTypeface(Typeface.DEFAULT_BOLD);
                    mSearchView.clearQuery();
                    mSearchView.setSearchHint("Search in city");
                }
            }
        });
        setupSearchBar();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle params = new Bundle();
                params.putString("gotoforum", "Click");
                mFirebaseAnalytics.logEvent("ClickGoToForum", params);

                if (mAuth.getCurrentUser() != null) {
                    onAuthSuccess(mAuth.getCurrentUser());
                } else {
                    // not signed in
                    startSignInFor(SIGN_IN_FOR_FORUM);
                }
            }
        });

        mFilterPanelToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFilterView.setVisibility(View.VISIBLE);
                sliderLayout.setScrollableView(findViewById(R.id.scroll_filters));

                sliderLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        mSortPanelToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSortView.setVisibility(View.VISIBLE);
                sliderLayout.setScrollableView(findViewById(R.id.scroll_sort));

                sliderLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        mBookmarkPanelToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBookmarkView.setVisibility(View.VISIBLE);
                sliderLayout.setScrollableView(findViewById(R.id.rv_bookmarks));

                sliderLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
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

        tblov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTypesofLengthsRecyclarView.getVisibility() == View.VISIBLE) {
                    mTypesofLengthsRecyclarView.setVisibility(View.GONE);
                    tblov.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                } else {
                    mTypesofLengthsRecyclarView.setVisibility(View.VISIBLE);
                    tblov.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                }
            }
        });
        tbtov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTypesofVehiclesRecyclarView.getVisibility() == View.VISIBLE) {
                    mTypesofVehiclesRecyclarView.setVisibility(View.GONE);
                    tbtov.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                } else {
                    mTypesofVehiclesRecyclarView.setVisibility(View.VISIBLE);
                    tbtov.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                }
            }
        });

        tbwc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTypesofWeightsRecyclarView.getVisibility() == View.VISIBLE) {
                    mTypesofWeightsRecyclarView.setVisibility(View.GONE);
                    tbwc.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                } else {
                    mTypesofWeightsRecyclarView.setVisibility(View.VISIBLE);
                    tbwc.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                }
            }
        });
        tbtob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTypesofBodyRecyclarView.getVisibility() == View.VISIBLE) {
                    mTypesofBodyRecyclarView.setVisibility(View.GONE);
                    tbtob.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                } else {
                    mTypesofBodyRecyclarView.setVisibility(View.VISIBLE);
                    tbwc.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                }
            }
        });

        mFiltersList = new ArrayList<>();

        mBtnApplyFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFiltersList.clear();
                for (String f : checkBoxRecyclarAdapter1.getmDataMap().keySet()) {
                    if (checkBoxRecyclarAdapter1.getmDataMap().get(f)) {
                        mFiltersList.add(f);
                    }
                }
                for (String f : checkBoxRecyclarAdapter2.getmDataMap().keySet()) {
                    if (checkBoxRecyclarAdapter2.getmDataMap().get(f)) {
                        mFiltersList.add(f);
                    }
                }
                for (String f : checkBoxRecyclarAdapter3.getmDataMap().keySet()) {
                    if (checkBoxRecyclarAdapter3.getmDataMap().get(f)) {
                        mFiltersList.add(f);
                    }
                }
                for (String f : checkBoxRecyclarAdapter4.getmDataMap().keySet()) {
                    if (checkBoxRecyclarAdapter4.getmDataMap().get(f)) {
                        mFiltersList.add(f);
                    }
                }
                for (String f : checkBoxRecyclarAdapter5.getmDataMap().keySet()) {
                    if (checkBoxRecyclarAdapter5.getmDataMap().get(f)) {
                        mFiltersList.add(f);
                    }
                }
                for (String f : checkBoxRecyclarAdapter6.getmDataMap().keySet()) {
                    if (checkBoxRecyclarAdapter6.getmDataMap().get(f)) {
                        mFiltersList.add(f);
                    }
                }

                if(mFiltersList.size()!=0){
                    isApplyFilterPressed = true;
                    mFilterPanelToggle.setCompoundDrawablesWithIntrinsicBounds( ContextCompat
                                    .getDrawable(getApplicationContext(),
                                            R.drawable.ic_filter_list_white_24dp),
                            null,
                            ContextCompat
                                    .getDrawable(getApplicationContext(),
                                            R.drawable.ic_bubble_chart_white_24dp),
                            null);
                }else {
                    mFilterPanelToggle.setCompoundDrawablesWithIntrinsicBounds( ContextCompat
                                    .getDrawable(getApplicationContext(),
                                            R.drawable.ic_filter_list_white_24dp),
                            null,
                            null,
                            null);
                }

                sliderLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            }
        });

        mBtnClearFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initiateFiltersHashmaps();
                checkBoxRecyclarAdapter1.notifyDataSetChanged();
                checkBoxRecyclarAdapter2.notifyDataSetChanged();
                checkBoxRecyclarAdapter3.notifyDataSetChanged();
                checkBoxRecyclarAdapter4.notifyDataSetChanged();
                checkBoxRecyclarAdapter5.notifyDataSetChanged();
                checkBoxRecyclarAdapter6.notifyDataSetChanged();
                if(mFiltersList.size()!=0){
                    mFiltersList.clear();
                    mFilterPanelToggle.setCompoundDrawablesWithIntrinsicBounds( ContextCompat
                                    .getDrawable(getApplicationContext(),
                                            R.drawable.ic_filter_list_white_24dp),
                            null,
                            null,
                            null);
                    setAdapter(mSearchView.getQuery());
                }
            }
        });

        mBtnClearSorts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mSortIndex!=0){
                    mSortRadioGroup.clearCheck();
                    mSortIndex = 0;
                    mSortPanelToggle.setCompoundDrawablesWithIntrinsicBounds( ContextCompat
                                    .getDrawable(getApplicationContext(),
                                            R.drawable.ic_sort_black_24dp),
                            null,
                            null,
                            null);
                    setAdapter(mSearchView.getQuery());
                }


            }
        });

        mBtnApplySorts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mSortRadioGroup.getCheckedRadioButtonId()){
                    case R.id.radioButton1 :{
                        mSortIndex = 1;
                        break;
                    }
                    case R.id.radioButton2 :{
                        mSortIndex = 2;
                        break;
                    }
                    case R.id.radioButton3 :{
                        mSortIndex = 3;
                        break;
                    }
                    case R.id.radioButton4 :{
                        mSortIndex = 4;
                        break;
                    }
                    default:{
                        mSortIndex=0;
                        break;
                    }
                }

                if(mSortIndex != 0){
                    isApplySortPressed = true;
                    sliderLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

                    mSortPanelToggle.setCompoundDrawablesWithIntrinsicBounds( ContextCompat
                                    .getDrawable(getApplicationContext(),
                                            R.drawable.ic_sort_black_24dp),
                            null,
                            ContextCompat
                                    .getDrawable(getApplicationContext(),
                                            R.drawable.ic_bubble_chart_white_24dp),
                            null);
                }else {
                    mSortPanelToggle.setCompoundDrawablesWithIntrinsicBounds( ContextCompat
                                    .getDrawable(getApplicationContext(),
                                            R.drawable.ic_sort_black_24dp),
                            null,
                            null,
                            null);
                }
            }
        });

        mTextCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createDialog(mFiltersList,searchTag,mSearchView.getQuery(),adapter.getItemCount());
            }
        });

        sliderLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                if(newState==SlidingUpPanelLayout.PanelState.COLLAPSED){
                    if(isApplyFilterPressed){
                        isApplyFilterPressed=false;
                        setAdapter(mSearchView.getQuery());
                    }
                    if(isApplySortPressed){
                        isApplySortPressed=false;
                        setAdapter(mSearchView.getQuery());
                    }
                    mFilterView.setVisibility(View.GONE);
                    mSortView.setVisibility(View.GONE);
                    mBookmarkView.setVisibility(View.GONE);
                }
            }
        });


    }

    private void onAuthSuccess(FirebaseUser user) {
        String userPhoneNo = user.getPhoneNumber();
        // Write new user
        writeNewUser(user.getUid(), userPhoneNo, userPhoneNo);
        // Go to MainActivity
        startActivity(new Intent(MainActivity.this, directory.tripin.com.tripindirectory.forum.MainActivity.class));
    }

    private void writeNewUser(String userId, String name, String userPhoneNo) {
        User user = new User(name, userPhoneNo);
        mDatabase.child("users").child(userId).setValue(user);
    }

    private void fetchCompanyAutoSuggestions(String s) {
        companySuggestions.clear();
        companySuggestions.add(new SuggestionCompanyName("Fetching Suggestions..."));
        mSearchView.swapSuggestions(companySuggestions);
        FirebaseFirestore.getInstance()
                .collection("partners").orderBy("mCompanyName").startAt(s).endAt(s + "\uf8ff")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        companySuggestions.clear();
                        Logger.v("on queried fetch Complete!!");
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                SuggestionCompanyName suggestionCompanyName = new SuggestionCompanyName();
                                Logger.v("suggestion: " + document.getId() + " => " + document.get("mCompanyName"));
                                suggestionCompanyName.setCompanyName(document.get("mCompanyName").toString());
                                companySuggestions.add(suggestionCompanyName);
                            }
                            mSearchView.swapSuggestions(companySuggestions);

                        } else {
                            Log.d("onComplete", "Error getting comp suggestion documents: ", task.getException());
                        }
                    }
                });
    }

    private void setAdapter(String s) {

        lottieAnimationView.setVisibility(View.VISIBLE);
        mTextCount.setVisibility(View.INVISIBLE);
        //base query
        query = FirebaseFirestore.getInstance()
                .collection("partners");

        //apply filters
        for(String f: mFiltersList){
            query = query.whereEqualTo("mFilters."+f.toUpperCase().trim(),true);
        }

        //apply sorts

        if (!s.isEmpty()) {
            mSearchQuery = s;
            switch (searchTag) {
                case SEARCHTAG_ROUTE: {
                    if (s.contains("To") || s.contains("to")) {
                        String sourceDestination[] = s.split("(?i:to)");
                        String source = sourceDestination[0].trim();
                        String destination = sourceDestination[1].trim();
                        query = query.whereEqualTo("mSourceCities." + source.toUpperCase(), true).whereEqualTo("mDestinationCities." + destination.toUpperCase(), true);
                    } else {
                        Toast.makeText(this, "Invalid Route Query", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
                case SEARCHTAG_COMPANY: {

                    if (isCompanySuggestionClicked) {
                        query = query.orderBy("mCompanyName").whereEqualTo("mCompanyName", s.trim());
                    } else {
                        query = query.orderBy("mCompanyName").whereGreaterThanOrEqualTo("mCompanyName", s.trim().toUpperCase());
                    }
                    isCompanySuggestionClicked = false;
                    break;
                }
                case SEARCHTAG_CITY: {
                    query = query.whereEqualTo("mCompanyAdderss.city", s.toUpperCase());
                    break;
                }
            }
        }else {
            query = query.orderBy("mCompanyName").whereGreaterThan("mCompanyName", "");
        }


        options = new FirestoreRecyclerOptions.Builder<PartnerInfoPojo>()
                .setQuery(query, PartnerInfoPojo.class)
                .build();

        mAnimator = new RecyclerViewAnimator(mPartnerList);

        adapter = new FirestoreRecyclerAdapter<PartnerInfoPojo, RecyclerView.ViewHolder>(options) {


            @SuppressLint("SetTextI18n")
            @Override
            public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position, final PartnerInfoPojo model) {

                switch (holder.getItemViewType()) {
                    case 0: {
                        FirstItemMainViewHolder firstItemMainViewHolder = (FirstItemMainViewHolder) holder;

                        break;
                    }
                    case 1: {
                        PartnersViewHolder partnersViewHolder = (PartnersViewHolder) holder;

                        //set address
                        String addresstoset
                                = model.getmCompanyAdderss().getAddress()
                                + ", " + textUtils.toTitleCase(model.getmCompanyAdderss().getCity())
                                + ", " + textUtils.toTitleCase(model.getmCompanyAdderss().getState());
                        if (model.getmCompanyAdderss().getPincode() != null) {
                            addresstoset = addresstoset + ", " + model.getmCompanyAdderss().getPincode();
                        }
                        partnersViewHolder.mAddress.setText(addresstoset);


                        partnersViewHolder.mCompany.setText(textUtils.toTitleCase(model.getmCompanyName()));
                        Logger.v("onBind : " + textUtils.toTitleCase(model.getmCompanyName()) + " " + model.getmAccountStatus());

                        if (model.getmAccountStatus() >= 2) {
                            partnersViewHolder.mCompany
                                    .setCompoundDrawablesWithIntrinsicBounds(ContextCompat
                                                    .getDrawable(getApplicationContext(),
                                                            R.drawable.ic_fiber_smart_record_bllue_24dp),
                                            null,
                                            null,
                                            null);
                        } else {
                            partnersViewHolder.mCompany
                                    .setCompoundDrawablesWithIntrinsicBounds(ContextCompat
                                                    .getDrawable(getApplicationContext(),
                                                            R.drawable.ic_fiber_manual_record_black_24dp),
                                            null,
                                            null,
                                            null);
                        }
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                                startPartnerDetailActivity(model.getmCompanyName(), snapshot.getId());
                            }
                        });


                        partnersViewHolder.mCall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Bundle params = new Bundle();
                                params.putString("call", "Click");
                                mFirebaseAnalytics.logEvent("ClickOnCall", params);

                                final ArrayList<String> phoneNumbers = new ArrayList<>();
                                List<ContactPersonPojo> contactPersonPojos = model.getmContactPersonsList();

                                if (contactPersonPojos != null && contactPersonPojos.size() > 1) {
                                    for (int i = 0; i < contactPersonPojos.size(); i++) {
                                        if (model.getmContactPersonsList().get(i) != null) {
                                            String number = model.getmContactPersonsList().get(i).getGetmContactPersonMobile();
                                            phoneNumbers.add(number);

                                        }
                                    }

                                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                    builder.setTitle("Looks like there are multiple phone numbers.")
                                            .setCancelable(false)
                                            .setAdapter(new ArrayAdapter<String>(mContext, R.layout.dialog_multiple_no_row, R.id.dialog_number, phoneNumbers),
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

                                    String number = model.getmContactPersonsList().get(0).getGetmContactPersonMobile();
                                    callNumber(number);
                                }
                            }
                        });
                        mAnimator.onBindViewHolder(holder.itemView, position);
                        break;
                    }
                }
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup group, int i) {

                switch (i) {
                    case 0: {
                        //first item
                        View view = LayoutInflater.from(group.getContext())
                                .inflate(R.layout.item_first_element_main, group, false);
                        // mAnimator.onCreateViewHolder(view);
                        return new FirstItemMainViewHolder(view);
                    }
                    case 1: {
                        //regular item
                        View view = LayoutInflater.from(group.getContext())
                                .inflate(R.layout.single_partner_row1, group, false);
                        mAnimator.onCreateViewHolder(view);
                        return new PartnersViewHolder(view);
                    }
                    default: {
                        View view = LayoutInflater.from(group.getContext())
                                .inflate(R.layout.single_partner_row1, group, false);
                        mAnimator.onCreateViewHolder(view);
                        return new PartnersViewHolder(view);
                    }
                }

            }

            @Override
            public int getItemViewType(int position) {
                if (position == 0) {
                    return 1; // return 0 to show first element
                } else {
                    return 1;
                }
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                Logger.v("on Data changed");
                lottieAnimationView.setVisibility(View.GONE);
                mSearchView.clearSuggestions();
                mTextCount.setVisibility(View.VISIBLE);
                startCountAnimation(adapter.getItemCount());
            }
        };

        mPartnerList.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // SIGN_IN_FOR_CREATE_COMPANY is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == SIGN_IN_FOR_CREATE_COMPANY) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                //signed in
                mUserDocRef = FirebaseFirestore.getInstance()
                        .collection("partners").document(auth.getCurrentUser().getPhoneNumber());

                mUserDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            Logger.v("document exist :" + auth.getCurrentUser().getPhoneNumber());

                            mUserDocRef = FirebaseFirestore.getInstance()
                                    .collection("partners").document(auth.getUid());
                            PartnerInfoPojo partnerInfoPojo = documentSnapshot.toObject(PartnerInfoPojo.class);
                            mUserDocRef.set(partnerInfoPojo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Logger.v("data set to :" + auth.getUid());
                                    mUserDocRef = FirebaseFirestore.getInstance()
                                            .collection("partners").document(auth.getCurrentUser().getPhoneNumber());
                                    mUserDocRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            startActivity(new Intent(MainActivity.this, CompanyInfoActivity.class));
                                            showSnackbar(R.string.sign_in_done);
                                        }
                                    });

                                }
                            });
                        } else {
                            Logger.v("document dosent exist :" + auth.getCurrentUser().getPhoneNumber());
                            startActivity(new Intent(MainActivity.this, CompanyInfoActivity.class));
                            showSnackbar(R.string.sign_in_done);
                        }
                    }
                });

                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    showSnackbar(R.string.sign_in_cancelled);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackbar(R.string.no_internet_connection);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackbar(R.string.unknown_error);
                    return;
                }
            }

            showSnackbar(R.string.unknown_sign_in_response);
        } else if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it
            // could have heard
            ArrayList matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Toast.makeText(mContext, matches.get(0).toString(), Toast.LENGTH_SHORT).show();
            String enquiry = matches.get(0).toString();
            onVoiceSearch(enquiry);
        } else if (requestCode == SIGN_IN_FOR_FORUM && resultCode == RESULT_OK) {
            if (mAuth.getCurrentUser() != null) {
                onAuthSuccess(mAuth.getCurrentUser());
            } else {
                Toast.makeText(mContext, "Unknow error", Toast.LENGTH_LONG).show();
            }
        }
    }

    void showSnackbar(int m) {
        Toast.makeText(this, getString(m), Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Bundle params = new Bundle();
        params.putString("left_navigation", "Click");
        mFirebaseAnalytics.logEvent("NavigationItemSelected", params);
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_business) {
            auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                // already signed in
                startActivity(new Intent(MainActivity.this, CompanyInfoActivity.class));
            } else {
                // not signed in
                startSignInFor(SIGN_IN_FOR_CREATE_COMPANY);
//                startActivityForResult(
//                        // Get an instance of AuthUI based on the default app
//                        AuthUI.getInstance().createSignInIntentBuilder()
//                                .setAvailableProviders(
//                                        Collections.singletonList(
//                                                new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()))
//                                .build(),
//                        SIGN_IN_FOR_CREATE_COMPANY);
            }
        } else if (id == R.id.nav_notification) {

            startActivity(new Intent(MainActivity.this, NotificationsActivity.class));

        } else if (id == R.id.nav_loadboard) {

            startActivity(new Intent(MainActivity.this, LoadBoardActivity.class));

        } else if (id == R.id.nav_logout) {
            params = new Bundle();
            params.putString("logout", "Click");
            mFirebaseAnalytics.logEvent("ClickOnLogout", params);

            auth = FirebaseAuth.getInstance();
            auth.signOut();
            Toast.makeText(getApplicationContext(), "Signed Out", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {
            params = new Bundle();
            params.putString("share", "Click");
            mFirebaseAnalytics.logEvent("ClickOnShareApp", params);
            shareApp();
        } else if (id == R.id.nav_feedback) {
            params = new Bundle();
            params.putString("feedback", "Click");
            mFirebaseAnalytics.logEvent("ClickOnFeedback", params);
            sendFeedback();
        } else if (id == R.id.nav_invite) {
            params = new Bundle();
            params.putString("invite", "Click");
            mFirebaseAnalytics.logEvent("ClickOnInvite", params);
            onInviteClicked();
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void shareApp() {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Indian Logistic Network");
            String sAux = "\nLet me recommend you this application\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=directory.tripin.com.tripindirectory \n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "Share INL"));
        } catch (Exception e) {
            //e.toString();
        }
    }

    private void sendFeedback() {
        try {
            Intent Email = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "ravishankar.ahirwar@tripin.co.in", null));
            Email.putExtra(Intent.EXTRA_SUBJECT, "Send Feedback/Feature Request/Bug Report for INL");
            Email.putExtra(Intent.EXTRA_TEXT, "Dear ..., \n Please let us know how to improve ?" + "");
            startActivity(Intent.createChooser(Email, "Send Feedback:"));
        } catch (ActivityNotFoundException e) {
            Logger.e("There is no app to send feedback for the app");
        }
    }

    private void onInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    private void setupSearchBar() {
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                    isSourceSelected = false;
                    isDestinationSelected = false;
                    mSourceCity = "";
                } else {

                    switch (searchTag) {
                        case SEARCHTAG_ROUTE:
                            if (Math.abs(newQuery.length() - oldQuery.length()) == 1) {

                                if (newQuery.length() == mDestinationCity.length() + mSourceCity.length() - 1) {
                                    isDestinationSelected = false;
                                }
                                if (newQuery.length() == mSourceCity.length() - 5) {
                                    isSourceSelected = false;
                                }
                                if (!isSourceSelected) {

                                    //set source suggestions
                                    Logger.v("source fetching......");
                                    new GetCityFromGoogleTask(new OnFindSuggestionsListener() {
                                        @Override
                                        public void onResults(List<SuggestionCompanyName> results) {
                                            mSearchView.swapSuggestions(results);
                                        }
                                    }).execute(newQuery, null, null);

                                } else {
                                    if (!isDestinationSelected) {
                                        //set destination suggestions
                                        Logger.v("destination fetching......");

                                        String queary = newQuery.replace(mSourceCity, "").toString().trim();
                                        new GetCityFromGoogleTask(new OnFindSuggestionsListener() {
                                            @Override
                                            public void onResults(List<SuggestionCompanyName> results) {
                                                mSearchView.swapSuggestions(results);
                                            }
                                        }).execute(queary, null, null);
                                    }

                                }
                            }


                            break;
                        case SEARCHTAG_COMPANY:


                            if (newQuery.length() == 1) {
                                //fetch all comps starting with firsr letter
                                fetchCompanyAutoSuggestions(newQuery.toUpperCase());

                            } else {
                                //filtermore
                                List<SuggestionCompanyName> list = new ArrayList<>();

                                for (SuggestionCompanyName s : companySuggestions) {
                                    if (s.getCompanyName().startsWith(newQuery.toUpperCase())) {
                                        list.add(s);
                                    }
                                }

                                mSearchView.swapSuggestions(list);

                            }

                            break;

                        case SEARCHTAG_CITY:
                            if (Math.abs(newQuery.length() - oldQuery.length()) == 1) {
                                new GetCityFromGoogleTask(new OnFindSuggestionsListener() {
                                    @Override
                                    public void onResults(List<SuggestionCompanyName> results) {
                                        mSearchView.swapSuggestions(results);
                                    }
                                }).execute(newQuery, null, null);
                            }
                            break;
                    }


                }

//                Log.d(TAG, "onSearchTextChanged()");
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final com.arlib.floatingsearchview.suggestions.model.SearchSuggestion searchSuggestion) {

                switch (searchTag) {
                    case SEARCHTAG_ROUTE: {
                        String selectedCity = searchSuggestion.getBody();


                        if (isSourceSelected) {

                            //destination suggestion tapped
                            mSearchView.setSearchText(mSourceCity + selectedCity);
                            mSearchView.clearFocus();
                            mSearchView.clearSearchFocus();
                            mSearchView.clearSuggestions();
                            setAdapter(mSourceCity + selectedCity);
                            isDestinationSelected = true;
                            mDestinationCity = selectedCity;

                        } else {

                            //source suggestion tapped
                            mSearchView.setSearchText(selectedCity);
                            isSourceSelected = true;
                            mSourceCity = selectedCity;

                        }


                        mSearchView.clearSuggestions();
                        break;
                    }

                    case SEARCHTAG_COMPANY: {
                        String companyname = searchSuggestion.getBody().trim();

                        Logger.v("suggestion clicked");

                        mSearchView.setSearchText(companyname);
                        mSearchView.clearFocus();
                        mSearchView.clearSearchFocus();
                        mSearchView.clearSuggestions();
                        isCompanySuggestionClicked = true;
                        setAdapter(companyname);
                        break;
                    }
                    case SEARCHTAG_CITY: {
                        Logger.v("suggestion clicked");

                        String cityname = searchSuggestion.getBody();
                        mSearchView.setSearchText(cityname);
                        mSearchView.clearFocus();
                        mSearchView.clearSearchFocus();
                        mSearchView.clearSuggestions();
                        setAdapter(cityname);
                        isCompanySuggestionClicked = true;
                        break;
                    }

                }


//                startUpDownActivity( (Station) searchSuggestion);
//
//                SearchSuggestion colorSuggestion = (SearchSuggestion) searchSuggestion;
//                DataHelper.findColors(getActivity(), colorSuggestion.getBody(),
//                        new DataHelper.OnFindColorsListener() {
//
//                            @Override
//                            public void onResults(List<ColorWrapper> results) {
//                                //show search results
//                            }
//
//                        });
//                Log.d(TAG, "onSuggestionClicked()");
//
//                mLastQuery = searchSuggestion.getBody();
            }

            @Override
            public void onSearchAction(String query) {
                Toast.makeText(mContext, "Query : " + query,
                        Toast.LENGTH_SHORT).show();
                Bundle params = new Bundle();
                params.putString("key_search", "Click");
                mFirebaseAnalytics.logEvent("SearchByKeyBoard", params);
                setAdapter(query);
            }
        });

        //handle menu clicks the same way as you would
        //in a regular activity
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {

                if (item.getItemId() == R.id.action_voice) {
                    Bundle params = new Bundle();
                    params.putString("menu_voice_recognition", "Click");
                    mFirebaseAnalytics.logEvent("VoiceRecognition", params);

                    startVoiceRecognitionActivity();
                } else {
                    //just print action
                    Toast.makeText(mContext, item.getTitle(),
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        /**
         * Here you have access to the left icon and the text of a given suggestion
         * item after as it is bound to the suggestion list. You can utilize this
         * callback to change some properties of the left icon and the text. For example, you
         * can load the left icon images using your favorite image loading library, or change text color.
         *
         *
         * Important:
         * Keep in mind that the suggestion list is a RecyclerView, so views are reused for different
         * items in the list.
         */
        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon,
                                         TextView textView, com.arlib.floatingsearchview.suggestions.model.SearchSuggestion item, int itemPosition) {
            }

        });
    }

    //-------------------- Voice -----------
    public void startVoiceRecognitionActivity() {
        String voiceSearchDialogTitle = "Search by voice";
        switch (searchTag) {
            case SEARCHTAG_ROUTE:
                voiceSearchDialogTitle = "Speak source to destination";
                break;
            case SEARCHTAG_COMPANY:
                voiceSearchDialogTitle = "Speak company name";
                break;
            case SEARCHTAG_CITY:
                voiceSearchDialogTitle = "Speak city name";
                break;
            default:
                voiceSearchDialogTitle = "Search by voice";
                break;
        }
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                voiceSearchDialogTitle);
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    private void onVoiceSearch(final String query) {
        if (query != null) {
            mSearchView.setSearchText(query);
            setAdapter(query);
        }
    }


    public interface OnFindSuggestionsListener {
        void onResults(List<SuggestionCompanyName> results);
    }

    private class GetCityFromGoogleTask extends AsyncTask<String, Void, List<SuggestionCompanyName>> {
        OnFindSuggestionsListener mOnFindSuggestionsListener;

        GetCityFromGoogleTask(OnFindSuggestionsListener onFindSuggestionsListener) {
            mOnFindSuggestionsListener = onFindSuggestionsListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<SuggestionCompanyName> suggestionCityName) {
            super.onPostExecute(suggestionCityName);
            mOnFindSuggestionsListener.onResults(suggestionCityName);
        }

        @Override
        protected List<SuggestionCompanyName> doInBackground(String... place) {
            List<SuggestionCompanyName> suggestionCompanyNames = new ArrayList<>();
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                    .setCountry("IN")
                    .build();


            Task<AutocompletePredictionBufferResponse> results =
                    mGeoDataClient.getAutocompletePredictions(place[0], BOUNDS_GREATER_SYDNEY, typeFilter);

            // This method should have been called off the main UI thread. Block and wait for at most
            // 60s for a result from the API.
            try {
                Tasks.await(results, 60, TimeUnit.SECONDS);
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                e.printStackTrace();
            }

            try {
                AutocompletePredictionBufferResponse autocompletePredictions = results.getResult();
                ArrayList<AutocompletePrediction> autocompletePredictions1 = DataBufferUtils.freezeAndClose(autocompletePredictions);
                CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);

                for (AutocompletePrediction autocompletePrediction1 : autocompletePredictions1) {
                    SuggestionCompanyName suggestionCompanyName = new SuggestionCompanyName();
                    String cityName = autocompletePrediction1.getPrimaryText(STYLE_BOLD).toString();
                    switch (searchTag) {
                        case SEARCHTAG_ROUTE: {
                            if (isSourceSelected) {
                                suggestionCompanyName.setCompanyName(cityName);
                            } else {
                                suggestionCompanyName.setCompanyName(cityName + " To ");
                            }
                            break;
                        }
                        case SEARCHTAG_CITY: {
                            suggestionCompanyName.setCompanyName(cityName);

                            break;
                        }
                    }

                    suggestionCompanyNames.add(suggestionCompanyName);
                    Log.i("Directory", "City Prediction : " + cityName);
                }

            } catch (RuntimeExecutionException e) {

            }
            return suggestionCompanyNames;
        }
    }

    private void startPartnerDetailActivity(String name, String uid) {
        Intent intent = new Intent(MainActivity.this, PartnerDetailScrollingActivity.class);
        intent.putExtra("uid", uid);
        intent.putExtra("cname", name);
        startActivity(intent);
    }

    private void callNumber(String number) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + Uri.encode(number.trim())));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(callIntent);
    }

    private void startSignInFor(int signInFor) {
        // not signed in
        startActivityForResult(
                // Get an instance of AuthUI based on the default app
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(
                                Collections.singletonList(
                                        new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()))
                        .build(),
                signInFor);
    }

    private void startCountAnimation(int n) {
        ValueAnimator animator = ValueAnimator.ofInt(0, n);
        animator.setDuration(2000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                mTextCount.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.start();
    }

    private void createDialog(List<String> filters,int tag,String StringInFSV, int numberOfResults) {
        dialog = new Dialog(MainActivity.this);

        //SET TITLE
        dialog.setTitle("Showing "+numberOfResults+ " Results...........");


        //set content
        dialog.setContentView(R.layout.dialog_resultdetails);

        TextView mUpperSearchTv, mFilters, mSorting;

        mUpperSearchTv = dialog.findViewById(R.id.textViewSearch);
        mFilters = dialog.findViewById(R.id.textViewFilters);
        mSorting = dialog.findViewById(R.id.textViewSort);

        if(mSearchQuery.isEmpty()){
            mUpperSearchTv.setVisibility(View.GONE);
        }else {

            switch (searchTag){
                case SEARCHTAG_ROUTE:{
                    mUpperSearchTv.setCompoundDrawablesWithIntrinsicBounds( ContextCompat
                                    .getDrawable(getApplicationContext(),
                                            R.drawable.ic_directions_grey_24dp),
                            null,
                            null,
                            null);

                    break;
                }
                case SEARCHTAG_COMPANY:{
                    mUpperSearchTv.setCompoundDrawablesWithIntrinsicBounds( ContextCompat
                                    .getDrawable(getApplicationContext(),
                                            R.drawable.ic_domain_black_24dp),
                            null,
                            null,
                            null);
                    break;
                }
                case SEARCHTAG_CITY:{
                    mUpperSearchTv.setCompoundDrawablesWithIntrinsicBounds( ContextCompat
                                    .getDrawable(getApplicationContext(),
                                            R.drawable.ic_location_on_black_24dp),
                            null,
                            null,
                            null);
                    break;
                }
            }
            mUpperSearchTv.setText(mSearchQuery);
        }

        StringBuilder filterss = new StringBuilder();
        for(String f: mFiltersList){
            filterss.append(" (").append(f).append(") +");
        }
        if(!filters.isEmpty()){
            String s =  filterss.substring(0, filterss.length() - 2);
            mFilters.setText(s);
        }else {
            mFilters.setVisibility(View.GONE);
        }

        switch (mSortIndex){
            case 0:{
                mSorting.setVisibility(View.GONE);
                break;
            }
            case 1:{
                mSorting.setText("Sorted Alphabetically");
                break;
            }
            case 2:{
                mSorting.setText("Sorted By User Ratings");
                break;
            }
            case 3:{
                mSorting.setText("Sorted By Favourites");
                break;
            }
            case 4:{
                mSorting.setText("Sorted By Crediblity");
                break;
            }
        }

        final EditText editTextBookamrkTitle = dialog.findViewById(R.id.editTextBookmark);
        final Button buttonBookmark = dialog.findViewById(R.id.buttonBookmark);
        Button exit = dialog.findViewById(R.id.buttonExit);


        buttonBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();


    }

    @Override
    public void onBackPressed() {

        if(sliderLayout.getPanelState()== SlidingUpPanelLayout.PanelState.EXPANDED){
            sliderLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }else {
            if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }else {
                super.onBackPressed();

            }
        }
    }
}
