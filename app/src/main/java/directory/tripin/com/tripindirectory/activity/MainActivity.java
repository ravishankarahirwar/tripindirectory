package directory.tripin.com.tripindirectory.activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Typeface;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.keiferstone.nonet.NoNet;
//import com.kobakei.ratethisapp.RateThisApp;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
//import com.wooplr.spotlight.SpotlightConfig;
//import com.wooplr.spotlight.SpotlightView;
//import com.wooplr.spotlight.utils.SpotlightSequence;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import directory.tripin.com.tripindirectory.chatingactivities.ChatHeadsActivity;
import directory.tripin.com.tripindirectory.formactivities.CheckBoxRecyclarAdapter;
import directory.tripin.com.tripindirectory.formactivities.CompanyInfoActivity;
import directory.tripin.com.tripindirectory.loadboardactivities.LoadBoardActivity;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.adapters.BookmarkAdapter;
import directory.tripin.com.tripindirectory.adapters.PartnerAdapter;
import directory.tripin.com.tripindirectory.callback.OnDataLoadListner;
import directory.tripin.com.tripindirectory.dataproviders.CopanyData;
import directory.tripin.com.tripindirectory.forum.models.Post;
import directory.tripin.com.tripindirectory.forum.models.User;
import directory.tripin.com.tripindirectory.helper.ListPaddingDecoration;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.helper.RecyclerViewAnimator;
import directory.tripin.com.tripindirectory.interfaces.BookmarkListner;
import directory.tripin.com.tripindirectory.manager.PreferenceManager;
import directory.tripin.com.tripindirectory.manager.QueryManager;
import directory.tripin.com.tripindirectory.model.FilterPojo;
import directory.tripin.com.tripindirectory.model.FoundHubPojo;
import directory.tripin.com.tripindirectory.model.HubFetchedCallback;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;
import directory.tripin.com.tripindirectory.model.QueryBookmarkPojo;
import directory.tripin.com.tripindirectory.model.RouteCityPojo;
import directory.tripin.com.tripindirectory.model.SuggestionCompanyName;
import directory.tripin.com.tripindirectory.model.UserQuery;
import directory.tripin.com.tripindirectory.model.search.Fleet;
import directory.tripin.com.tripindirectory.model.search.Truck;
import directory.tripin.com.tripindirectory.model.search.TruckProperty;
import directory.tripin.com.tripindirectory.ui.adapters.WorkingWithAdapter;
import directory.tripin.com.tripindirectory.Messaging.ChatList.ListChatActivity;
import directory.tripin.com.tripindirectory.utils.Analytics;
import directory.tripin.com.tripindirectory.utils.AppUtils;
import directory.tripin.com.tripindirectory.utils.Constants;
import directory.tripin.com.tripindirectory.utils.DB;
import directory.tripin.com.tripindirectory.utils.FilterType;
import directory.tripin.com.tripindirectory.utils.SearchBy;
import directory.tripin.com.tripindirectory.utils.SearchData;
import directory.tripin.com.tripindirectory.utils.ShortingType;
import directory.tripin.com.tripindirectory.utils.TextUtils;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, HubFetchedCallback, OnDataLoadListner, BookmarkListner {

    private static final String INTRO_SEARCH = "search_intro";
    private static final String INTRO_LOADBOARD = "loadboard_intro";
    private static final String INTRO_NEWLOOK = "newlook_intro";



    public static final int REQUEST_INVITE = 1001;
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private static final int NO_OF_DAY = 1;
    private static final int NO_OF_TIME_APP_OPEN = 5;

    private static final int SIGN_IN_FOR_CREATE_COMPANY = 123;
    private static final int SIGN_IN_FOR_FORUM = 222;
    private static final int SIGN_IN_FOR_CHAT = 124;


    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    //    private FirestoreRecyclerAdapter adapter;
    boolean isCompanySuggestionClicked = false;
    boolean isBookmarkSaved = false;
    boolean isApplySortPressed;
    private List<SuggestionCompanyName> companySuggestions = null;
    private DocumentReference mUserDocRef;
    private FirestoreRecyclerOptions<PartnerInfoPojo> options;
    private FirestoreRecyclerOptions<QueryBookmarkPojo> optionsbookmark;
    private DatabaseReference mPostReference;

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
    private RouteCityPojo mSourceCity;
    private RouteCityPojo mDestinationCity;
    private int signinginfor = 0;
    private QueryBookmarkPojo queryBookmarkPojo;
    private FirebaseAnalytics mFirebaseAnalytics;
    private RecyclerViewAnimator mAnimator;
    private LottieAnimationView lottieAnimationView, animationBookmark;
    private TextUtils textUtils;
    private SlidingUpPanelLayout sliderLayout;
    private TextView mApplyFilters;
    private TextView mClearFilters;
    private TextView mFilterPanelToggle;
    private TextView mSortPanelToggle;
    private TextView mNoOfFilterApply;
    private LottieAnimationView mBookmarkPanelToggle;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private HashMap<String, Boolean> mNatureofBusinessHashMap;
    private HashMap<String, Boolean> mTypesofServicesHashMap;

    private List<FilterPojo> mFiltersList;

    private CheckBoxRecyclarAdapter mNatureOfBusiness;
    private CheckBoxRecyclarAdapter mTypeOfService;

    private RecyclerView mNatureOfBusinessRecyclarView;
    private RecyclerView mTypesOfServicesRecyclarView;
    private RecyclerView mTypesofVehiclesRecyclarView;

    private TextView mTextCount;
    private TextView mTryNewLook;
//    private TextView mLoadBoardNews;

    private Dialog dialog;
    private boolean isApplyFilterPressed;
    private View mFilterView, mSortView, mBookmarkView;

    private RadioButton searchByRoute;
    private RadioButton radioButtonAlphabetically;
    private RadioButton mSortAlphDecending;
    private RadioButton radioButtonCrediblity;
    private RadioButton mShortByLastActive;
    private RadioGroup mSortRadioGroup;

    private Button mBtnApplySorts;
    private Button mBtnClearSorts;

    private int mSortIndex;
    private RecyclerView mBookmarksList;
    private FirestoreRecyclerAdapter bookmarksAdapter;
    private WorkingWithAdapter mWorkingWithAdapter;
    private AppEventsLogger logger;
    private List<FoundHubPojo> mNearestHubsList = new ArrayList<>();
    private QueryManager mQueryManager;
    private PartnerAdapter mPartnerAdapter;
    private AppUtils mAppUtils;

    private ImageView mNavFacebook;
    private ImageView mNavYouTube;
    private ImageView mNavWebsite;

    FloatingActionButton goToForum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
        viewSetup();
        FacebookSdk.sdkInitialize(getApplicationContext());
//        ratingDialogSetup();
        internetCheck();
        notificationSubscried();
        setAdapter("");
        setBookmarkListAdapter();
        setLastActiveTime();
//        showIntro();
    }


    @Override
    protected void init() {

        mContext = MainActivity.this;
        textUtils = new TextUtils();
        mAppUtils = new AppUtils(mContext);
        logger = AppEventsLogger.newLogger(this);
        mQueryManager = new QueryManager();

        FirebaseApp.initializeApp(getApplicationContext());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("posts");

        mNatureOfBusinessRecyclarView = findViewById(R.id.rv_natureofbusiness);
        mTypesOfServicesRecyclarView = findViewById(R.id.rv_typesofservices);
        mTypesofVehiclesRecyclarView = findViewById(R.id.rv_tov);

        //FilterType Initiliaze
        mNatureofBusinessHashMap = CopanyData.getNatureOfBusiness();
        mTypesofServicesHashMap = CopanyData.getTypeOfServices();

        mNatureOfBusiness = new CheckBoxRecyclarAdapter(mNatureofBusinessHashMap);
        mTypeOfService = new CheckBoxRecyclarAdapter(mTypesofServicesHashMap);

        mSourceCity = new RouteCityPojo(mContext, 1, 1, this);
        mDestinationCity = new RouteCityPojo(mContext, 2, 1, this);
        mSearchData = new SearchData();
        mGeoDataClient = Places.getGeoDataClient(this, null);

        mNearestHubsList = new ArrayList<>();
        mNavFacebook  = findViewById(R.id.nev_footer_facebook);
        mNavYouTube  = findViewById(R.id.nev_footer_youtube);
        mNavWebsite  = findViewById(R.id.nev_footer_webstie);

//        mLoadBoardNews =  findViewById(R.id.loadboard_news);
        mTryNewLook =  findViewById(R.id.trynewlook);


        mNoOfFilterApply = findViewById(R.id.no_of_filters);
        mSearchView = findViewById(R.id.floating_search_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mPartnerList = findViewById(R.id.transporter_list);
        mBookmarksList = findViewById(R.id.rv_bookmarks);
        mSearchTagRadioGroup = findViewById(R.id.search_tag_group);

        sliderLayout = findViewById(R.id.sliding_layout);
        mFilterPanelToggle = findViewById(R.id.filter);
        mSortPanelToggle = findViewById(R.id.sort);
        mBookmarkPanelToggle = findViewById(R.id.animation_bookmark);

        mPartnerList.setLayoutManager(new LinearLayoutManager(this));
        mBookmarksList.setLayoutManager(new LinearLayoutManager(this));

        ListPaddingDecoration listPaddingDecoration = new ListPaddingDecoration(getApplicationContext());
        mPartnerList.addItemDecoration(listPaddingDecoration);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mPreferenceManager = PreferenceManager.getInstance(mContext);
        companySuggestions = new ArrayList<>();
        searchTag = SearchBy.SEARCHTAG_ROUTE;
        mSearchView.setShowSearchKey(true);
        mSearchView.attachNavigationDrawerToMenuButton(mDrawerLayout);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.toolbar_background));
        }

        searchByRoute = findViewById(R.id.search_by_route);
        lottieAnimationView = findViewById(R.id.animation_view);
        animationBookmark = findViewById(R.id.animation_bookmark);

        mApplyFilters = findViewById(R.id.buttonApplyFilters);
        mClearFilters = findViewById(R.id.buttonClearFilters);

        mFilterView = findViewById(R.id.include_filters);
        mSortView = findViewById(R.id.include_sort);
        mBookmarkView = findViewById(R.id.include_bookmark);

        mTextCount = findViewById(R.id.textViewResCount);

        radioButtonAlphabetically = findViewById(R.id.radioButton1);
        mSortAlphDecending = findViewById(R.id.radioButton2);
        radioButtonCrediblity = findViewById(R.id.radioButton4);
        mShortByLastActive = findViewById(R.id.shortby_lastactive);

        mSortRadioGroup = findViewById(R.id.radioGroupSort);
        mBtnApplySorts = findViewById(R.id.buttonApplySort);
        mBtnClearSorts = findViewById(R.id.buttonClearSort);

        mFiltersList = new ArrayList<>();

        mApplyFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFiltersList.clear();
                Fleet filledFleet = mWorkingWithAdapter.getDataValues();
                for (int i = 0; i < filledFleet.getTrucks().size(); i++) {
                    Truck trucks = filledFleet.getTrucks().get(i);
                    boolean ihave = filledFleet.getTrucks().get(i).isTruckHave();
                    if (ihave) {
                        String newFilter = trucks.getTruckType();
                        FilterPojo filterPojo = new FilterPojo(newFilter, FilterType.TYPE_OF_VEHICLE, 1);
                        mFiltersList.add(filterPojo);
                    }
                    for (int j = 0; j < trucks.getTruckProperties().size(); j++) {
                        TruckProperty truckProperty = trucks.getTruckProperties().get(j);
                        for (Map.Entry<String, Boolean> entry : truckProperty.getProperties().entrySet()) {
                            boolean propertyValue = entry.getValue();
                            if (propertyValue) {
                                String newFilter = "Fleet." + trucks.getTruckType() + "." + truckProperty.getTitle() + "." + entry.getKey();
                                FilterPojo filterPojo = new FilterPojo(newFilter, FilterType.TYPE_OF_VEHICLE_PROPERTY, 1);
                                mFiltersList.add(filterPojo);
                                Log.v("FilterAdded", newFilter);
                            }
                        }
                    }
                }

                for (Map.Entry<String, Boolean> entry : mNatureOfBusiness.getmDataMap().entrySet()) {
                    if (entry.getValue()) {
                        FilterPojo filterPojo = new FilterPojo(entry.getKey(), FilterType.NATURE_OF_BUSINESS, 1);
                        mFiltersList.add(filterPojo);
                    }
                }

                for (Map.Entry<String, Boolean> entry : mTypeOfService.getmDataMap().entrySet()) {
                    if (entry.getValue()) {
                        FilterPojo filterPojo = new FilterPojo(entry.getKey(), FilterType.TYPE_OF_SERVICE, 1);
                        mFiltersList.add(filterPojo);
                    }
                }

                if (mFiltersList.size() != 0) {
                    isApplyFilterPressed = true;
                    mNoOfFilterApply.setVisibility(TextView.VISIBLE);
                    mNoOfFilterApply.setText(String.valueOf(mFiltersList.size()));
                } else {
                    mNoOfFilterApply.setVisibility(TextView.GONE);
                }
                sliderLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        mClearFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNatureOfBusiness.notifyDataSetChanged();
                mTypeOfService.notifyDataSetChanged();
                if (mFiltersList.size() != 0) {
                    mNoOfFilterApply.setVisibility(TextView.GONE);
                    mFiltersList.clear();
                    mFilterPanelToggle.setCompoundDrawablesWithIntrinsicBounds(ContextCompat
                                    .getDrawable(getApplicationContext(),
                                            R.drawable.ic_filter_list_white_24dp),
                            null,
                            null,
                            null);
                    setAdapter(mSearchView.getQuery());
                }
                sliderLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        mBtnClearSorts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mSortIndex != 0) {
                    mSortRadioGroup.clearCheck();
                    mSortIndex = 0;
                    mSortPanelToggle.setCompoundDrawablesWithIntrinsicBounds(ContextCompat
                                    .getDrawable(getApplicationContext(),
                                            R.drawable.ic_sort_black_24dp),
                            null,
                            null,
                            null);
                    setAdapter(mSearchView.getQuery());
                }
                sliderLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        mBtnApplySorts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mSortRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.radioButton1 : {
                        mSortIndex = ShortingType.ALPHA_ASSENDING;
                        break;
                    }
                    case R.id.radioButton2 : {
                        mSortIndex = ShortingType.ALPHA_DECENDING;
                        break;
                    }
                    case R.id.radioButton4 : {
                        mSortIndex = ShortingType.ACCOUNT_TYPE;
                        break;
                    }
                    case R.id.shortby_lastactive : {
                        mSortIndex = ShortingType.LAST_ACTIVE;
                        break;
                    }
                    default: {
                        mSortIndex = ShortingType.DEFAULT;
                        break;
                    }
                }

                if (mSortIndex != 0) {
                    isApplySortPressed = true;
                    sliderLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

                    mSortPanelToggle.setCompoundDrawablesWithIntrinsicBounds(ContextCompat
                                    .getDrawable(getApplicationContext(),
                                            R.drawable.ic_sort_black_24dp),
                            null,
                            ContextCompat
                                    .getDrawable(getApplicationContext(),
                                            R.drawable.ic_brightness_1_black_24dp),
                            null);
                } else {
                    mSortPanelToggle.setCompoundDrawablesWithIntrinsicBounds(ContextCompat
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
                createDialog(mFiltersList, searchTag, mSearchView.getQuery(), mPartnerAdapter.getItemCount());
            }
        });

        sliderLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    if (isApplyFilterPressed) {
                        isApplyFilterPressed = false;
                        setAdapter(mSearchView.getQuery());
                    }
                    if (isApplySortPressed) {
                        isApplySortPressed = false;
                        setAdapter(mSearchView.getQuery());
                    }
                    mFilterView.setVisibility(View.GONE);
                    mSortView.setVisibility(View.GONE);
                    mBookmarkView.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void viewSetup() {
        mNatureOfBusinessRecyclarView.setAdapter(mNatureOfBusiness);
        mNatureOfBusinessRecyclarView.setLayoutManager(new GridLayoutManager(this, 2));
        mNatureOfBusinessRecyclarView.setNestedScrollingEnabled(false);

        mTypesOfServicesRecyclarView.setAdapter(mTypeOfService);
        mTypesOfServicesRecyclarView.setLayoutManager(new GridLayoutManager(this, 2));
        mTypesOfServicesRecyclarView.setNestedScrollingEnabled(false);

        InputStream raw = getResources().openRawResource(R.raw.fleet);
        Reader rd = new BufferedReader(new InputStreamReader(raw));
        Gson gson = new Gson();
        Fleet fleet = gson.fromJson(rd, Fleet.class);
        mWorkingWithAdapter = new WorkingWithAdapter(mContext, fleet);

        mTypesofVehiclesRecyclarView.setLayoutManager(new LinearLayoutManager(this));
        mTypesofVehiclesRecyclarView.setNestedScrollingEnabled(false);
        mTypesofVehiclesRecyclarView.setAdapter(mWorkingWithAdapter);

        mTryNewLook.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(),"Launching New Look!",Toast.LENGTH_SHORT).show();
            AuthUI.getInstance().signOut(getApplicationContext()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mPreferenceManager.setisOnNewLook(true);
                    startActivity(new Intent(MainActivity.this,
                            directory.tripin.com.tripindirectory.newlookcode.activities.NewSplashActivity.class));
                    finish();
                    Bundle bundle = new Bundle();
                    mFirebaseAnalytics.logEvent("z_try_new_look",bundle);
                }
            });


        });

        mSearchTagRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int radioButtonID) {
                Bundle params = new Bundle();
                mSearchView.clearQuery();
                mSortIndex = ShortingType.DEFAULT;
                if (radioButtonID == R.id.search_by_route) {
                    params.putString("search_by", "ByRoute");
                    searchTag = SearchBy.SEARCHTAG_ROUTE;
                    mSearchView.setSearchHint("Source To Destination");
                } else if (radioButtonID == R.id.search_by_company) {
                    params.putString("search_by", "ByCompanyName");
                    searchTag = SearchBy.SEARCHTAG_COMPANY;
                    mSearchView.setSearchHint("Search by company name");
                } else if (radioButtonID == R.id.search_by_city) {
                    params.putString("search_by", "ByCity");
                    searchTag = SearchBy.SEARCHTAG_CITY;
                    mSearchView.setSearchHint("Search in city");
                }
                mFirebaseAnalytics.logEvent("SearchBy", params);
            }
        });
        setupSearchBar();


        goToForum = findViewById(R.id.fab);
        goToForum.setOnClickListener(new View.OnClickListener() {
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

        mNavFacebook.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Bundle params = new Bundle();
                mFirebaseAnalytics.logEvent(Analytics.Event.GO_TO_FACEBOOKPAGE, params);

                try {
                    Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                    String facebookUrl = getFacebookPageURL(MainActivity.this);
                    facebookIntent.setData(Uri.parse(facebookUrl));
                    startActivity(facebookIntent);
                } catch (ActivityNotFoundException exception) {
                    Toast.makeText(mContext, "Sorry: There is some issue in opening facebook", Toast.LENGTH_SHORT).show();
                } catch (Exception exception) {
                    Logger.v("Not able to open ILN facebook page");
                }
            }
        });

        mNavYouTube.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Bundle params = new Bundle();
                mFirebaseAnalytics.logEvent(Analytics.Event.GO_TO_YOUTUBE, params);

                try {
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                facebookIntent.setData(Uri.parse("http://www.youtube.com/watch?v=FOkt6F0ZAOk"));
                startActivity(facebookIntent);
                } catch (ActivityNotFoundException exception) {
                    Toast.makeText(mContext, "Sorry: There is some issue in opening youtube", Toast.LENGTH_SHORT).show();
                } catch (Exception exception) {
                    Logger.v("Not able to open ILN youtube page");
                }
            }
        });

        mNavWebsite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Bundle params = new Bundle();
                mFirebaseAnalytics.logEvent(Analytics.Event.GO_TO_WEBSITE, params);

            try {
            Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
            facebookIntent.setData(Uri.parse("http://indianlogisticsnetwork.com/#about"));
            startActivity(facebookIntent);
            } catch (ActivityNotFoundException exception) {
                Toast.makeText(mContext, "Sorry: There is some issue in opening website", Toast.LENGTH_SHORT).show();
            } catch (Exception exception) {
                Logger.v("Not able to open ILN Website page");
            }
            }
        });

//        mLoadBoardNews.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Bundle params = new Bundle();
//                params.putString(Analytics.Event.GO_TO_FORUM_BYNEWS, Analytics.Value.CLICK);
//                mFirebaseAnalytics.logEvent(Analytics.Event.GO_TO_FORUM_BYNEWS, params);
//
//                if (mAuth.getCurrentUser() != null) {
//                    onAuthSuccess(mAuth.getCurrentUser());
//                } else {
//                    // not signed in
//                    startSignInFor(SIGN_IN_FOR_FORUM);
//                }
//            }
//        });
    }

//    private void showIntro() {
//
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                SpotlightConfig config = new SpotlightConfig();
//                config.setDismissOnTouch(true);
//                config.setRevealAnimationEnabled(true);
//                config.setLineAndArcColor(0xFFFFFFFF);
//
//                config.setMaskColor(Color.parseColor("#dc000000"));
//
//                SpotlightSequence.getInstance(MainActivity.this,config)
//                        .addSpotlight(searchByRoute, "Search By", "Search by Route, \nCompany Name, City(Touch to Next)", INTRO_SEARCH)
//                        .addSpotlight(goToForum, "Loadboard ", "Post your requirement(Load/Truck) in Loadboard ", INTRO_LOADBOARD)
//                        .addSpotlight(mTryNewLook,"Try New Look","We have redesigned the whole app just the way you want it, give it a try!",INTRO_NEWLOOK)
//                        .startSequence();
//            }
//        }, 1000);
//    }

//    /**
//     * This method is use for showing an dialog for rate app in google play store
//     */
//    private void ratingDialogSetup() {
//        RateThisApp.onCreate(this);
//        RateThisApp.showRateDialogIfNeeded(this);
//        RateThisApp.Config config = new RateThisApp.Config(NO_OF_DAY, NO_OF_TIME_APP_OPEN);
//        RateThisApp.init(config);
//    }

    /**
     * This method is use for checking internet connectivity
     * If there is no internet it will show an snackbar to user
     */
    private void internetCheck() {
        NoNet.monitor(this)
                .poll()
                .snackbar();
    }

    private void notificationSubscried() {
        FirebaseMessaging.getInstance().subscribeToTopic("generalUpdates");
        //For Testing
//        FirebaseMessaging.getInstance().subscribeToTopic("generalUpdatesTest");
        FirebaseMessaging.getInstance().subscribeToTopic("loadboardNotification");
    }



    // Add to each long-lived activity
    @Override
    protected void onResume() {
        super.onResume();
        logger.logPurchase(BigDecimal.valueOf(4.32), Currency.getInstance("USD"));
        AppEventsLogger.activateApp(this, "");
    }

    // for Android, you should also log app deactivation
    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logAppstartEvent(double valToSum) {
        logger.logEvent("appstart", valToSum);
    }

    private void setBookmarkListAdapter() {
        if (mAuth.getCurrentUser() != null) {

            optionsbookmark = mQueryManager.getBookMarkOptions(mAuth.getUid());
            BookmarkAdapter bookmarkAdapter = new BookmarkAdapter(optionsbookmark, this);
            mBookmarksList.setAdapter(bookmarkAdapter);
            bookmarkAdapter.startListening();
        } else {
            //show sign in options in bookmarks layout
            Logger.v("sign in is required to access this feature");
        }
    }


    private void onAuthSuccess(FirebaseUser user) {
        PreferenceManager preferenceManager = PreferenceManager.getInstance(getApplicationContext());
        String fcmTocken = preferenceManager.getFcmToken();
        if (fcmTocken != null) {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(Constants.ARG_USERS)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(Constants.ARG_FIREBASE_TOKEN)
                    .setValue(fcmTocken);
        }

        String userPhoneNo = user.getPhoneNumber();
        // Write new user
        writeNewUser(user.getUid(), userPhoneNo, userPhoneNo, fcmTocken);
        // Go to MainActivity
        startActivity(new Intent(MainActivity.this, directory.tripin.com.tripindirectory.forum.MainActivity.class));
    }

    private void writeNewUser(String userId, String name, String userPhoneNo, String fcmtoken) {
        User user = new User(name, userPhoneNo, fcmtoken);
        mDatabase.child("users").child(userId).child("username").setValue(name);
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

    private void setAdapter(String searchQuery) {

        lottieAnimationView.setVisibility(View.VISIBLE);
        mTextCount.setVisibility(View.INVISIBLE);
        isBookmarkSaved = false;

        query = FirebaseFirestore.getInstance()
                .collection(DB.Collection.PARTNER);

        //apply filters
        for (FilterPojo filter : mFiltersList) {
            switch (filter.getmFilterType()) {
                case FilterType.TYPE_OF_VEHICLE_PROPERTY : {
                    String filterName = filter.getmFilterName();
                    query = query.whereEqualTo(filterName, true);
                    break;
                }
                case FilterType.TYPE_OF_VEHICLE: {
                    query = query.whereEqualTo("fleetVehicle." + filter.getmFilterName(), true);
                    break;
                }
                case FilterType.TYPE_OF_SERVICE: {
                    query = query.whereEqualTo("mTypesOfServices." + filter.getmFilterName().toUpperCase().trim(), true);
                    break;
                }
                case FilterType.NATURE_OF_BUSINESS: {
                    query = query.whereEqualTo("mNatureOfBusiness." + filter.getmFilterName().toUpperCase().trim(), true);

                    break;
                }

            }

        }

        //apply sorting
        switch (mSortIndex) {
            case ShortingType.ALPHA_ASSENDING : {
                query = query.whereGreaterThan(DB.PartnerFields.COMPANY_NAME, "");
                query = query.orderBy(DB.PartnerFields.COMPANY_NAME, Query.Direction.ASCENDING);
                break;
            }
            case ShortingType.ALPHA_DECENDING : {
                query = query.whereGreaterThan(DB.PartnerFields.COMPANY_NAME, "");
                query = query.orderBy(DB.PartnerFields.COMPANY_NAME, Query.Direction.DESCENDING);
                break;
            }
            case ShortingType.ACCOUNT_TYPE : {
                searchTag = -1;
                query = query.orderBy(DB.PartnerFields.ACCOUNT_STATUS, Query.Direction.DESCENDING);
                break;
            }
            case ShortingType.LAST_ACTIVE : {
                searchTag = -1;
                query = query.orderBy(DB.PartnerFields.LASTACTIVETIME, Query.Direction.DESCENDING);
                break;
            }
            default: {
                break;
            }
        }

        //apply sorts

        if (!searchQuery.isEmpty()) {
            mSearchQuery = searchQuery;
            switch (searchTag) {
                case SearchBy.SEARCHTAG_ROUTE: {
                    if (searchQuery.equals("1")) {
                        if (mSourceCity.getmNearestHub() != null && mDestinationCity.getmNearestHub() != null) {
                            Logger.v("HUBS: " + mSourceCity.getmNearestHub().getmHubName() + " , " + mDestinationCity.getmNearestHub().getmHubName());

                            query = query
                                    .whereEqualTo("mSourceHubs." + mSourceCity.getmNearestHub().getmHubName().toUpperCase(), true)
                                    .whereEqualTo("mDestinationHubs." + mDestinationCity.getmNearestHub().getmHubName().toUpperCase(), true);
                        } else {
                            Logger.v("HUBS NULL " + mSourceCity.isFetchingHub() + " , " + mDestinationCity.isFetchingHub());
                        }

                    } else {
                        if (searchQuery.contains("To") || searchQuery.contains("to")) {
                            String sourceDestination[] = searchQuery.split("(?i:to)");
                            String source = sourceDestination[0].trim();
                            String destination = sourceDestination[1].trim();
                            query = query
                                    .whereEqualTo("mSourceCities." + source.toUpperCase(), true)
                                    .whereEqualTo("mDestinationCities." + destination.toUpperCase(), true);
                        } else {
                            Toast.makeText(this, "Invalid Route Query", Toast.LENGTH_LONG).show();
                        }
                    }

                    break;
                }
                case SearchBy.SEARCHTAG_COMPANY: {

                    if (isCompanySuggestionClicked) {
                        query = query.whereEqualTo(DB.PartnerFields.COMPANY_NAME, searchQuery.trim());
                    } else {
                        query = query.whereGreaterThanOrEqualTo(DB.PartnerFields.COMPANY_NAME, searchQuery.trim().toUpperCase());
                    }
                    isCompanySuggestionClicked = false;
                    break;
                }
                case SearchBy.SEARCHTAG_CITY: {
                    query = query.whereEqualTo("mCompanyAdderss.city", searchQuery.toUpperCase());
                    break;
                }
            }
        } else {
            query = query.orderBy(DB.PartnerFields.LASTACTIVETIME, Query.Direction.DESCENDING);

//            query = query.orderBy(DB.PartnerFields.LASTACTIVETIME, Query.Direction.DESCENDING);
//            if (mSortIndex != ShortingType.ACCOUNT_TYPE && mSortIndex != ShortingType.LAST_ACTIVE) {
//                query = query.whereGreaterThan(DB.PartnerFields.COMPANY_NAME, "");
//            }
        }

        options = new FirestoreRecyclerOptions.Builder<PartnerInfoPojo>()
                .setQuery(query, PartnerInfoPojo.class)
                .build();

        mAnimator = new RecyclerViewAnimator(mPartnerList);
        mPartnerAdapter = new PartnerAdapter(mContext, mAnimator, options, this);
        mPartnerList.setAdapter(mPartnerAdapter);
        mPartnerAdapter.startListening();
    }

    String loadBoardNews = "| ";
    @Override
    protected void onStart() {
        super.onStart();
        if (mPartnerAdapter != null) {
            mPartnerAdapter.startListening();
        }

        if (bookmarksAdapter != null)
            bookmarksAdapter.startListening();

//        mLoadBoardNews.setText("");
        mPostReference.limitToLast(10).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Log.v("LoadBoard", " onChildAdded  to read value.");

                Post post = dataSnapshot.getValue(Post.class);
                if(post!= null && post.getmSource() != null) {
                    loadBoardNews += " | ";
                    loadBoardNews += post.getmSource() + " \u25BA " .toUpperCase();
                    loadBoardNews += post.getmDestination() + " ".toUpperCase() + " \u25AA ";
                    loadBoardNews += post.getmTruckType() + " \u25AA ";
                    loadBoardNews += post.getmTruckBodyType() + " \u25AA ";
                    loadBoardNews += post.getmPayload() + " Ton ";

//                    mLoadBoardNews.append(loadBoardNews);
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.v("LoadBoard", " onChildRemoved  to read value.");

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.v("LoadBoard", " onChildChanged to read value.");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.v("LoadBoard", "onChildMoved Failed to read value.");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("LoadBoard", " onCancelledFailed to read value.");
            }
        });
        Log.v("LoadBoard", " " + loadBoardNews);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPartnerAdapter != null) {
            mPartnerAdapter.stopListening();
        }

        if (bookmarksAdapter != null) {
            bookmarksAdapter.stopListening();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // SIGN_IN_FOR_CREATE_COMPANY is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == SIGN_IN_FOR_CREATE_COMPANY) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                //signed in
                final Dialog dialogWait = new ProgressDialog(MainActivity.this);
                dialogWait.show();

                if(mAuth.getCurrentUser()!=null){
                    if(mAuth.getCurrentUser().getPhoneNumber()!=null){
                        mUserDocRef = FirebaseFirestore.getInstance()
                                .collection("partners").document(mAuth.getCurrentUser().getPhoneNumber());
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"Error, Try Again",Toast.LENGTH_LONG).show();
                }

                mUserDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            if(mAuth.getCurrentUser()!=null){
                                if(mAuth.getCurrentUser().getPhoneNumber()!=null){
                                    Logger.v("document exist :" + mAuth.getCurrentUser().getPhoneNumber());
                                }
                            }

                            mUserDocRef = FirebaseFirestore.getInstance()
                                    .collection("partners").document(mAuth.getUid());
                            PartnerInfoPojo partnerInfoPojo = documentSnapshot.toObject(PartnerInfoPojo.class);
                            mUserDocRef.set(partnerInfoPojo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Logger.v("data set to :" + mAuth.getUid());
                                    if(mAuth.getCurrentUser()!=null){
                                        if(mAuth.getCurrentUser().getPhoneNumber()!=null){
                                            mUserDocRef = FirebaseFirestore.getInstance()
                                                    .collection("partners").document(mAuth.getCurrentUser().getPhoneNumber());
                                            mUserDocRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    if (dialogWait.isShowing()) {
                                                        dialogWait.dismiss();
                                                    }
                                                    switch (signinginfor) {
                                                        case 1: {
                                                            //for comp info
                                                            startActivity(new Intent(MainActivity.this, CompanyInfoActivity.class));
                                                            break;
                                                        }
                                                        case 2: {
                                                            //for bookmark
                                                            FirebaseFirestore.getInstance()
                                                                    .collection("partners").document(mAuth.getUid()).collection("mQueryBookmarks").add(queryBookmarkPojo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                @Override
                                                                public void onSuccess(DocumentReference documentReference) {
                                                                    dialog.cancel();
                                                                    Toast.makeText(getApplicationContext(), "Bookmark Added!", Toast.LENGTH_LONG).show();
                                                                    isBookmarkSaved = true;
                                                                    setBookmarkListAdapter();
                                                                }
                                                            });
                                                            break;
                                                        }
                                                        case 3: {
                                                            //for loadboard
                                                            startActivity(new Intent(MainActivity.this, LoadBoardActivity.class));

                                                            break;
                                                        }
                                                        case 4: {
                                                            //for Inbox
                                                            startActivity(new Intent(MainActivity.this, ChatHeadsActivity.class));

                                                            break;
                                                        }
                                                        case 0: {
                                                            //nothing
                                                        }
                                                    }
                                                    showSnackbar(R.string.sign_in_done);
                                                }
                                            });
                                        }else {
                                            Toast.makeText(getApplicationContext(),"Error, Try Again!",Toast.LENGTH_LONG).show();
                                        }
                                    }else {
                                        Toast.makeText(getApplicationContext(),"Error, Try Again!",Toast.LENGTH_LONG).show();
                                    }


                                }
                            });
                        } else {

                            if(mAuth.getCurrentUser() != null){
                                if(mAuth.getCurrentUser().getPhoneNumber() != null) {
                                    Logger.v("document dosent exist :" + mAuth.getCurrentUser().getPhoneNumber());
                                }
                            }
                            if (dialogWait.isShowing()) {
                                dialogWait.dismiss();
                            }
                            switch (signinginfor) {
                                case 1: {
                                    //for comp info
                                    startActivity(new Intent(MainActivity.this, CompanyInfoActivity.class));
                                    break;
                                }
                                case 2: {
                                    //for bookmark
                                    FirebaseFirestore.getInstance()
                                            .collection("partners").document(mAuth.getUid()).collection("mQueryBookmarks").add(queryBookmarkPojo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            dialog.cancel();
                                            Toast.makeText(getApplicationContext(), "Bookmark Added!", Toast.LENGTH_LONG).show();
                                            isBookmarkSaved = true;
                                            setBookmarkListAdapter();
                                        }
                                    });
                                    break;
                                }
                                case 3: {
                                    //for loadboard
                                    startActivity(new Intent(MainActivity.this, LoadBoardActivity.class));

                                    break;
                                }
                                case 4: {
                                    //for Inbox
                                    startActivity(new Intent(MainActivity.this, ChatHeadsActivity.class));

                                    break;
                                }
                                case 0: {
                                    //nothing
                                }
                            }
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

//                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
//                    showSnackbar(R.string.no_internet_connection);
//                    return;
//                }
//
//                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
//                    showSnackbar(R.string.unknown_error);
//                    return;
//                }
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
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                    try {
                        final ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
                        ShortcutInfo dynamicShortcut = new ShortcutInfo.Builder(this, "shortcut_web")
                                .setShortLabel("ILN-LoadBoard")
                                .setLongLabel("ILN-LoadBoard Post load and Truck")
                                .setIcon(Icon.createWithResource(this, R.drawable.delivery_truck_front))
                                .setIntents(
                                        new Intent[]{
                                                new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, directory.tripin.com.tripindirectory.forum.MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK),
                                                new Intent("directory.tripin.com.tripindirectory.forum.MainActivity.OPEN_DYNAMIC_SHORTCUT")
                                        })
                                .setRank(0)
                                .build();
                        shortcutManager.setDynamicShortcuts(Arrays.asList(dynamicShortcut));
                    }catch (Exception ex) {
                        Logger.v("Exception in creating dynamic shortcut");
                    }
                }
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
            if (mAuth.getCurrentUser() != null) {
                // already signed in
                startActivity(new Intent(MainActivity.this, CompanyInfoActivity.class));
            } else {
                // not signed in
                signinginfor = 1;
                startSignInFor(SIGN_IN_FOR_CREATE_COMPANY);
            }
        } else if (id == R.id.nav_notification) {

            startActivity(new Intent(MainActivity.this, NotificationsActivity.class));

        } else if (id == R.id.nav_inbox) {
            if (mAuth.getCurrentUser() != null) {
                startActivity(new Intent(MainActivity.this, ListChatActivity.class));
            } else {
                startSignInFor(SIGN_IN_FOR_CHAT);
            }


        }  else if (id == R.id.nav_logout) {

            if(mAuth.getUid() != null) {
                mAuth.signOut();
                Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();
                params = new Bundle();
                mFirebaseAnalytics.logEvent("ClickOnLogout", params);
            } else {
                Toast.makeText(this, "Not Sign-in", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_share) {
            params = new Bundle();
            mFirebaseAnalytics.logEvent("ClickOnShareApp", params);
            mAppUtils.shareApp();

        } else if (id == R.id.nav_feedback) {
            params = new Bundle();
            mFirebaseAnalytics.logEvent("ClickOnFeedback", params);
            mAppUtils.sendFeedback();
        } else if (id == R.id.nav_invite) {
            params = new Bundle();
            mFirebaseAnalytics.logEvent("ClickOnInvite", params);
            onInviteClicked();
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onInviteClicked() {
        startActivityForResult(mAppUtils.getInviteIntent(), REQUEST_INVITE);
    }

    private void setupSearchBar() {
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                    isSourceSelected = false;
                    isDestinationSelected = false;
                } else {

                    switch (searchTag) {
                        case SearchBy.SEARCHTAG_ROUTE:
                            if (Math.abs(newQuery.length() - oldQuery.length()) == 1) {

                                if (newQuery.length() == mDestinationCity.getmCityName().length() + mSourceCity.getmCityName().length() - 5) {
                                    isDestinationSelected = false;
                                }
                                if (newQuery.length() == mSourceCity.getmCityName().length() - 5) {
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

                                        String queary = newQuery.replace(mSourceCity.getmCityName() + " To ", "").toString().trim();
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
                        case SearchBy.SEARCHTAG_COMPANY:
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
                        case SearchBy.SEARCHTAG_CITY:
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
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final com.arlib.floatingsearchview.suggestions.model.SearchSuggestion searchSuggestion) {

                prepareUserQueary("onSuggestionClicked", searchSuggestion.getBody());

                switch (searchTag) {
                    case SearchBy.SEARCHTAG_ROUTE: {
                        String selectedCity = searchSuggestion.getBody();

                        if (isSourceSelected) {
                            //destination suggestion tapped
                            mSearchView.setSearchText(mSourceCity.getmCityName() + " To " + selectedCity);
                            mSearchView.clearFocus();
                            mSearchView.clearSearchFocus();
                            mSearchView.clearSuggestions();
                            lottieAnimationView.setVisibility(View.VISIBLE);
                            mTextCount.setVisibility(View.INVISIBLE);
                            mDestinationCity.setmCityName(selectedCity);
                            isDestinationSelected = true;
                        } else {
                            //source suggestion tapped
                            mSearchView.setSearchText(selectedCity);
                            isSourceSelected = true;

                            mSourceCity.setmCityName(selectedCity.replace("To", "").trim());
                        }
                        mSearchView.clearSuggestions();
                        break;
                    }

                    case SearchBy.SEARCHTAG_COMPANY: {
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
                    case SearchBy.SEARCHTAG_CITY: {
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
            }

            @Override
            public void onSearchAction(String query) {
                Toast.makeText(mContext, "Query : " + query,
                        Toast.LENGTH_SHORT).show();
                Bundle params = new Bundle();

                params.putString("key_search", "Click");
                mFirebaseAnalytics.logEvent("SearchByKeyBoard", params);

                Bundle paramsSearch = new Bundle();
                paramsSearch.putString("search_by_keyboard", "Yes");
                paramsSearch.putString("search_by", String.valueOf(searchTag));
                paramsSearch.putString("search_query", query);
                mFirebaseAnalytics.logEvent("SearchQueary", params);

                prepareUserQueary("KeyboardSearchKey", query);
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

    private String getSerchByText() {
        switch (searchTag) {
            case SearchBy.SEARCHTAG_ROUTE:
                return "Route";
            case SearchBy.SEARCHTAG_COMPANY:
                return "Company";
            case SearchBy.SEARCHTAG_CITY:
                return "City";
                default:
                    return "--No--";
        }
    }

    //-------------------- Voice -----------
    public void startVoiceRecognitionActivity() {
        String voiceSearchDialogTitle = "Search by voice";
        switch (searchTag) {
            case SearchBy.SEARCHTAG_ROUTE:
                voiceSearchDialogTitle = "Speak source to destination";
                break;
            case SearchBy.SEARCHTAG_COMPANY:
                voiceSearchDialogTitle = "Speak company name";
                break;
            case SearchBy.SEARCHTAG_CITY:
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
            prepareUserQueary("onVoiceSearch", query);
        }
    }

    @Override
    public void onDestinationHubFetched(String destinationhub, int o) {
        setAdapter("1");
    }

    @Override
    public void onSourceHubFetched(String sourcehub, int o) {

    }

    private void startSignInFor(int signInFor) {
        startActivityForResult(
                // Get an instance of AuthUI based on the default app
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(
                                Collections.singletonList(
                                        new AuthUI.IdpConfig.PhoneBuilder().build()))
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

    private void createDialog(List<FilterPojo> filters, int tag, String StringInFSV, int numberOfResults) {
        dialog = new Dialog(MainActivity.this);

        //SET TITLE
        dialog.setTitle("Showing " + numberOfResults + " Results...........");


        //set content
        dialog.setContentView(R.layout.dialog_resultdetails);

        TextView mUpperSearchTv, mFilters, mSorting;

        mUpperSearchTv = dialog.findViewById(R.id.textViewSearch);
        mFilters = dialog.findViewById(R.id.textViewFilters);
        mSorting = dialog.findViewById(R.id.textViewSort);

        if (mSearchQuery.isEmpty()) {
            mUpperSearchTv.setVisibility(View.GONE);
        } else {

            switch (searchTag) {
                case SearchBy.SEARCHTAG_ROUTE: {
                    mUpperSearchTv.setCompoundDrawablesWithIntrinsicBounds(ContextCompat
                                    .getDrawable(getApplicationContext(),
                                            R.drawable.ic_directions_grey_24dp),
                            null,
                            null,
                            null);

                    break;
                }
                case SearchBy.SEARCHTAG_COMPANY: {
                    mUpperSearchTv.setCompoundDrawablesWithIntrinsicBounds(ContextCompat
                                    .getDrawable(getApplicationContext(),
                                            R.drawable.ic_domain_black_24dp),
                            null,
                            null,
                            null);
                    break;
                }
                case SearchBy.SEARCHTAG_CITY: {
                    mUpperSearchTv.setCompoundDrawablesWithIntrinsicBounds(ContextCompat
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
        for (FilterPojo f : mFiltersList) {
            filterss.append(" (").append(f.getmFilterName()).append(") +");
        }
        if (!filters.isEmpty()) {
            String s = filterss.substring(0, filterss.length() - 2);
            mFilters.setText(s);
        } else {
            mFilters.setVisibility(View.GONE);
        }

        switch (mSortIndex) {
            case 0: {
                mSorting.setVisibility(View.GONE);
                break;
            }
            case 1: {
                mSorting.setText("Sorted Alphabetically");
                break;
            }
            case 2: {
                mSorting.setText("Sorted By User Ratings");
                break;
            }
            case 3: {
                mSorting.setText("Sorted By Favourites");
                break;
            }
            case 4: {
                mSorting.setText("Sorted By Crediblity");
                break;
            }
        }

        final EditText editTextBookamrkTitle = dialog.findViewById(R.id.editTextBookmark);
        final Button buttonBookmark = dialog.findViewById(R.id.buttonBookmark);
        Button exit = dialog.findViewById(R.id.buttonExit);

        if (isBookmarkSaved) {
            editTextBookamrkTitle.setVisibility(View.GONE);
            buttonBookmark.setVisibility(View.GONE);
        }
        if (mAuth.getCurrentUser() == null) {
            buttonBookmark.setText("Bookmark(Sign In Required)");
        }
        buttonBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //generate new bookmarkquerypojo object
                buttonBookmark.setText("Saving...");
                String title = editTextBookamrkTitle.getText().toString().trim();
                if (!title.isEmpty()) {

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                    String timestamp = simpleDateFormat.format(new Date());


                    queryBookmarkPojo = new QueryBookmarkPojo(mFiltersList,
                            title,
                            mSortIndex,
                            searchTag,
                            mSearchQuery, timestamp);

                    if (mAuth.getCurrentUser() != null) {
                        //add bookmark to sub collection
                        FirebaseFirestore.getInstance().collection("partners").document(mAuth.getUid()).collection("mQueryBookmarks").add(queryBookmarkPojo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                buttonBookmark.setText("Bookmark");
                                Toast.makeText(getApplicationContext(), "Bookmark Added!", Toast.LENGTH_LONG).show();
                                dialog.cancel();
                                animationBookmark.resumeAnimation();
                                isBookmarkSaved = true;
                            }
                        });
                    } else {
                        //sign in user
                        signinginfor = 2;
                        startSignInFor(SIGN_IN_FOR_CREATE_COMPANY);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Title Is Empty!", Toast.LENGTH_LONG).show();
                }
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

        if (sliderLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            sliderLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();

            }
        }
    }

    @Override
    public void onDataLoaded(int itemLoaded) {
        lottieAnimationView.setVisibility(View.GONE);
        mSearchView.clearSuggestions();
        mTextCount.setVisibility(View.VISIBLE);
        startCountAnimation(itemLoaded);
    }

    private void setLastActiveTime() {
        if (mAuth.getCurrentUser() != null) {
            mUserDocRef = FirebaseFirestore.getInstance()
                    .collection(DB.Collection.PARTNER).document(mAuth.getUid());

            Map<String, Object> updates = new HashMap<>();
            updates.put(DB.PartnerFields.LASTACTIVETIME, FieldValue.serverTimestamp());
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
                        case SearchBy.SEARCHTAG_ROUTE: {
                            if (isSourceSelected) {
                                suggestionCompanyName.setCompanyName(cityName);
                            } else {
                                suggestionCompanyName.setCompanyName(cityName + " To ");
                            }
                            break;
                        }
                        case SearchBy.SEARCHTAG_CITY: {
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
    public static String FACEBOOK_URL = "https://www.facebook.com/ILNOfficial";
    public static String FACEBOOK_PAGE_ID = "1288324944512615";
    //method to get the right URL to use in the intent
    public String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }

    @Override
    public void onBookMarkSearchClick(QueryBookmarkPojo model) {
                            searchTag = model.getmSearchTag();
                            mSortIndex = model.getmSortIndex();
                            mSearchQuery = model.getmSearchQuery();
                            mFiltersList.clear();
                            mFiltersList.addAll(model.getmFiltersList());

                            if (model.getmSortIndex() != 0) {
                                mSortPanelToggle.setCompoundDrawablesWithIntrinsicBounds(ContextCompat
                                                .getDrawable(getApplicationContext(),
                                                        R.drawable.ic_sort_black_24dp),
                                        null,
                                        ContextCompat
                                                .getDrawable(getApplicationContext(),
                                                        R.drawable.ic_bubble_chart_white_24dp),
                                        null);
                            }

                            if(model.getmFiltersList().size()!=0){
                                int noOfFilterApply = model.getmFiltersList().size();
                                mNoOfFilterApply.setVisibility(TextView.VISIBLE);
                                mNoOfFilterApply.setText(String.valueOf(noOfFilterApply));
                            } else {
                                mNoOfFilterApply.setVisibility(TextView.GONE);

                            }

                            mSearchView.setSearchText(model.getmSearchQuery());
                            setAdapter(model.getmSearchQuery());
                            sliderLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    // [START write_fan_out]
    private void writeUserQuery(String userId, UserQuery userQuery) {
        DatabaseReference queryDataBase = FirebaseDatabase.getInstance().getReference();

        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = queryDataBase.child("query").push().getKey();
        Map<String, Object> queryValues = userQuery.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/query/" + key, queryValues);
        childUpdates.put("/user-query/" + userId + "/" + key, queryValues);

        queryDataBase.updateChildren(childUpdates);
    }


    private void prepareUserQueary(String action, String query) {
        UserQuery userQuery = new UserQuery();
        userQuery.setQueryTime(System.currentTimeMillis());
        userQuery.setQuery(query);
        userQuery.setQueryBy(getSerchByText());
        userQuery.setQueryAction(action);

        String uId;
        if(mAuth != null) {
            uId = mAuth.getUid();
            if(mAuth.getCurrentUser() != null){
                if(mAuth.getCurrentUser().getPhoneNumber() != null) {
                    String phone = mAuth.getCurrentUser().getPhoneNumber();
                    userQuery.setUserMobileNo(phone);
                }
            }

        } else {
            uId = "Anonimus";
        }
        userQuery.setmUid(uId);
        writeUserQuery(uId, userQuery);
    }
}

