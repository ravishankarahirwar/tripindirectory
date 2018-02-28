package directory.tripin.com.tripindirectory.activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.keiferstone.nonet.NoNet;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import directory.tripin.com.tripindirectory.ChatingActivities.ChatHeadsActivity;
import directory.tripin.com.tripindirectory.FormActivities.CheckBoxRecyclarAdapter;
import directory.tripin.com.tripindirectory.FormActivities.CompanyInfoActivity;
import directory.tripin.com.tripindirectory.FormActivities.FormFragments.TruckPropertiesViewHolder;
import directory.tripin.com.tripindirectory.FormActivities.TruckPropertiesValueViewHolder;
import directory.tripin.com.tripindirectory.FormActivities.WorkingWithHolderNew;
import directory.tripin.com.tripindirectory.LoadBoardActivities.LoadBoardActivity;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.adapters.FirstItemMainViewHolder;
import directory.tripin.com.tripindirectory.adapters.PartnersViewHolder;
import directory.tripin.com.tripindirectory.adapters.QueryBookmarkViewHolder;
import directory.tripin.com.tripindirectory.chat.utils.Constants;
import directory.tripin.com.tripindirectory.forum.models.User;
import directory.tripin.com.tripindirectory.helper.ListPaddingDecoration;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.helper.RecyclerViewAnimator;
import directory.tripin.com.tripindirectory.manager.PreferenceManager;
import directory.tripin.com.tripindirectory.model.ContactPersonPojo;
import directory.tripin.com.tripindirectory.model.FilterPojo;
import directory.tripin.com.tripindirectory.model.FoundHubPojo;
import directory.tripin.com.tripindirectory.model.HubFetchedCallback;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;
import directory.tripin.com.tripindirectory.model.QueryBookmarkPojo;
import directory.tripin.com.tripindirectory.model.RouteCityPojo;
import directory.tripin.com.tripindirectory.model.SuggestionCompanyName;
import directory.tripin.com.tripindirectory.model.search.Fleet;
import directory.tripin.com.tripindirectory.model.search.Truck;
import directory.tripin.com.tripindirectory.model.search.TruckProperty;
import directory.tripin.com.tripindirectory.utils.DBFields;
import directory.tripin.com.tripindirectory.utils.SearchData;
import directory.tripin.com.tripindirectory.utils.ShortingType;
import directory.tripin.com.tripindirectory.utils.TextUtils;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener ,HubFetchedCallback {

    public static final int REQUEST_INVITE = 1001;
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    private static final int SEARCHTAG_ROUTE = 0;
    private static final int SEARCHTAG_COMPANY = 1;
    private static final int SEARCHTAG_CITY = 2;

    private static final int SEARCHTAG_TRANSPORTER = 3;
    private static final int GIOQUERY_RADIUS = 50;



    private static final int SIGN_IN_FOR_CREATE_COMPANY = 123;

    private static final int SIGN_IN_FOR_FORUM = 222;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    private List<SuggestionCompanyName> companySuggestions = null;
    private DocumentReference mUserDocRef;
    private FirestoreRecyclerOptions<PartnerInfoPojo> options;
    private FirestoreRecyclerOptions<QueryBookmarkPojo> optionsbookmark;

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
    private RouteCityPojo mSourceCity;
    private RouteCityPojo mDestinationCity;
    boolean isBookmarkSaved = false;
    private int signinginfor = 0;
    private  QueryBookmarkPojo queryBookmarkPojo;


    private RadioButton mSerachByCompany;
    private RadioButton mSerachByRoute;
    private RadioButton mSerachByCity;

    private FirebaseAnalytics mFirebaseAnalytics;
    private RecyclerViewAnimator mAnimator;
    LottieAnimationView lottieAnimationView, animationBookmark;
    TextUtils textUtils;
    SlidingUpPanelLayout sliderLayout;
    private TextView mBtnApplyFilters;
    private TextView mBtnClearFilters;

    //bottom status bar
    private TextView mFilterPanelToggle;
    private TextView mSortPanelToggle;
    private TextView mNoOfFilterApply;
    private LottieAnimationView mBookmarkPanelToggle;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private HashMap<String, Boolean> mNatureofBusinessHashMap;
    private HashMap<String, Boolean> mTypesofServicesHashMap;
    private List<FilterPojo> mFiltersList;

    private CheckBoxRecyclarAdapter checkBoxRecyclarAdapter1;
    private CheckBoxRecyclarAdapter checkBoxRecyclarAdapter2;


    private RecyclerView mNatureOfBusinessRecyclarView;
    private RecyclerView mTypesOfServicesRecyclarView;
    private RecyclerView mTypesofVehiclesRecyclarView;

    private TextView mTextCount;
    private Dialog dialog;
    private boolean isApplyFilterPressed;
    private View mFilterView, mSortView, mBookmarkView;

    private RadioButton radioButtonAlphabetically;
    private RadioButton mSortAlphDecending;
    private RadioButton radioButtonCrediblity;

    private RadioGroup mSortRadioGroup;
    private Button mBtnApplySorts;
    private Button mBtnClearSorts;
    private int mSortIndex;
    boolean isApplySortPressed;

    private RecyclerView mBookmarksList;
    private FirestoreRecyclerAdapter bookmarksAdapter;
    private WorkingWithAdapter mWorkingWithAdapter;
    private AppEventsLogger logger;
    private LatLng mDestinationLatLang;
    private LatLng mSourceLatLang;
    private List<FoundHubPojo> mNearestHubsList = new ArrayList<>();
    private String mSourceHub = "";
    private int mRadiusMultiplier = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NoNet.monitor(this)
                .poll()
                .snackbar();
        //Add to Activity
        //For Production
        FacebookSdk.sdkInitialize(getApplicationContext());
        logger = AppEventsLogger.newLogger(this);
        FirebaseMessaging.getInstance().subscribeToTopic("generalUpdates");

        //For Testing
//        FirebaseMessaging.getInstance().subscribeToTopic("generalUpdatesTest");

        textUtils = new TextUtils();
        //Add to Activity
        FirebaseMessaging.getInstance().subscribeToTopic("loadboardNotification");

        setContentView(R.layout.activity_home);

        mNatureofBusinessHashMap = new HashMap<>();
        mTypesofServicesHashMap = new HashMap<>();

        initiateFiltersHashmaps();

        checkBoxRecyclarAdapter1 = new CheckBoxRecyclarAdapter(mNatureofBusinessHashMap);
        checkBoxRecyclarAdapter2 = new CheckBoxRecyclarAdapter(mTypesofServicesHashMap);

        mNatureOfBusinessRecyclarView = findViewById(R.id.rv_natureofbusiness);
        mNatureOfBusinessRecyclarView.setAdapter(checkBoxRecyclarAdapter1);
        mNatureOfBusinessRecyclarView.setLayoutManager(new GridLayoutManager(this, 2));
        mNatureOfBusinessRecyclarView.setNestedScrollingEnabled(false);

        mTypesOfServicesRecyclarView = findViewById(R.id.rv_typesofservices);
        mTypesOfServicesRecyclarView.setAdapter(checkBoxRecyclarAdapter2);
        mTypesOfServicesRecyclarView.setLayoutManager(new GridLayoutManager(this, 2));
        mTypesOfServicesRecyclarView.setNestedScrollingEnabled(false);

        InputStream raw =  getResources().openRawResource(R.raw.fleet);
        Reader rd = new BufferedReader(new InputStreamReader(raw));
        Gson gson = new Gson();
        Fleet fleet = gson.fromJson(rd, Fleet.class);
        mWorkingWithAdapter = new WorkingWithAdapter(mContext, fleet);

        mTypesofVehiclesRecyclarView = findViewById(R.id.rv_tov);
        mTypesofVehiclesRecyclarView.setLayoutManager(new LinearLayoutManager(this));
        mTypesofVehiclesRecyclarView.setNestedScrollingEnabled(false);
        mTypesofVehiclesRecyclarView.setAdapter(mWorkingWithAdapter);

        init();
        /**
         * if User coming first time in our portal
         */
        if (mPreferenceManager.isFirstTime()) {
            mPreferenceManager.setFirstTime(false);
            startActivity(new Intent(mContext, TutorialScreensActivity.class));
        }
        setAdapter("");
        setBookmarkListAdapter();
    }

    // Add to each long-lived activity
    @Override
    protected void onResume() {
        super.onResume();
        logger.logPurchase(BigDecimal.valueOf(4.32), Currency.getInstance("USD"));
        AppEventsLogger.activateApp(this,"");
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
    public void logAppstartEvent (double valToSum) {
        logger.logEvent("appstart", valToSum);
    }

    private void setBookmarkListAdapter() {
        if (mAuth.getCurrentUser() != null) {
            Logger.v("setting bookmarks adapter " + mAuth.getUid());

            Query queryBookmark = FirebaseFirestore
                    .getInstance()
                    .collection("partners")
                    .document(mAuth.getUid())
                    .collection("mQueryBookmarks").orderBy("mTimeStamp", Query.Direction.DESCENDING);

            optionsbookmark = new FirestoreRecyclerOptions.Builder<QueryBookmarkPojo>()
                    .setQuery(queryBookmark, QueryBookmarkPojo.class)
                    .build();

            bookmarksAdapter = new FirestoreRecyclerAdapter<QueryBookmarkPojo, QueryBookmarkViewHolder>(optionsbookmark) {
                @Override
                protected void onBindViewHolder(final QueryBookmarkViewHolder holder, int position, final QueryBookmarkPojo model) {

                    Logger.v("onBindBookmark: " + model.getmBookmarkName());
                    holder.title.setText(model.getmBookmarkName());
                    holder.remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            holder.remove.setText("Removing");
                            DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                            snapshot.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "Bookmark Removed", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                    holder.searchnow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            // set the book mark
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
                    });

                    if (model.getmSearchQuery().isEmpty()) {
                        holder.searchquery.setVisibility(View.GONE);
                    } else {

                        switch (model.getmSearchTag()) {
                            case SEARCHTAG_ROUTE: {
                                holder.searchquery.setCompoundDrawablesWithIntrinsicBounds(ContextCompat
                                                .getDrawable(getApplicationContext(),
                                                        R.drawable.ic_directions_grey_24dp),
                                        null,
                                        null,
                                        null);

                                break;
                            }
                            case SEARCHTAG_COMPANY: {
                                holder.searchquery.setCompoundDrawablesWithIntrinsicBounds(ContextCompat
                                                .getDrawable(getApplicationContext(),
                                                        R.drawable.ic_domain_black_24dp),
                                        null,
                                        null,
                                        null);
                                break;
                            }
                            case SEARCHTAG_CITY: {
                                holder.searchquery.setCompoundDrawablesWithIntrinsicBounds(ContextCompat
                                                .getDrawable(getApplicationContext(),
                                                        R.drawable.ic_location_on_black_24dp),
                                        null,
                                        null,
                                        null);
                                break;
                            }
                        }
                        holder.searchquery.setText(model.getmSearchQuery());
                    }

                    StringBuilder filterss = new StringBuilder();
                    for (FilterPojo f : model.getmFiltersList()) {
                        filterss.append(" (").append(f.getmFilterName()).append(") +");
                    }
                    if (!filterss.toString().isEmpty()) {
                        String s = filterss.substring(0, filterss.length() - 2);
                        holder.filters.setText(s);
                    } else {
                        holder.filters.setVisibility(View.GONE);
                    }

                    switch (model.getmSortIndex()) {
                        case 0: {
                            holder.sorting.setVisibility(View.GONE);
                            break;
                        }
                        case ShortingType.ALPHA_ASSENDING : {
                            holder.sorting.setText("Sorted Alphabetically A-Z");
                            break;
                        }
                        case ShortingType.ALPHA_DECENDING : {
                            holder.sorting.setText("Sorted Alphabetically Z-A");
                            break;
                        }
                        case  ShortingType.ACCOUNT_TYPE: {
                            holder.sorting.setText("Sorted By Crediblity");
                            break;
                        }
                    }

                }

                @Override
                public QueryBookmarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_bookmark_query, parent, false);
                    Logger.v("onCreat ViewHolder Bookmark");

                    return new QueryBookmarkViewHolder(view);
                }

                @Override
                public void onDataChanged() {
                    super.onDataChanged();
                    Logger.v("onDataChanged Bookmark");
                }

                @Override
                public void onError(FirebaseFirestoreException e) {
                    super.onError(e);
                    Logger.v("onError Bookmark");

                }
            };

            mBookmarksList.setAdapter(bookmarksAdapter);
            bookmarksAdapter.startListening();
        } else {
            //show sign in options in bookmarks layout
            Logger.v("sign in is required to access this feature");
        }


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
        mTypesofServicesHashMap.put("Packer and Movers".toUpperCase(), false);
    }

    private void init() {
        mContext = MainActivity.this;
        mSourceCity = new RouteCityPojo(mContext,1,1,this);
        mDestinationCity = new RouteCityPojo(mContext,2,1,this);
        mSearchData = new SearchData();
        mGeoDataClient = Places.getGeoDataClient(this, null);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        FirebaseApp.initializeApp(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mNearestHubsList = new ArrayList<>();


        mNoOfFilterApply = findViewById(R.id.no_of_filters);
        mSearchView = findViewById(R.id.floating_search_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mPartnerList = findViewById(R.id.transporter_list);
        mBookmarksList = findViewById(R.id.rv_bookmarks);
        mSearchTagRadioGroup = findViewById(R.id.search_tag_group);
        mPartnerList.setLayoutManager(new LinearLayoutManager(this));
        mBookmarksList.setLayoutManager(new LinearLayoutManager(this));

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

        mSerachByCompany = findViewById(R.id.search_by_company);
        mSerachByRoute = findViewById(R.id.search_by_route);
        mSerachByCity = findViewById(R.id.search_by_city);

        lottieAnimationView = findViewById(R.id.animation_view);
        animationBookmark = findViewById(R.id.animation_bookmark);

        mBtnApplyFilters = findViewById(R.id.buttonApplyFilters);
        mBtnClearFilters = findViewById(R.id.buttonClearFilters);

        mFilterView = findViewById(R.id.include_filters);
        mSortView = findViewById(R.id.include_sort);
        mBookmarkView = findViewById(R.id.include_bookmark);

        mTextCount = findViewById(R.id.textViewResCount);

        radioButtonAlphabetically = findViewById(R.id.radioButton1);
        mSortAlphDecending = findViewById(R.id.radioButton2);
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
                    mSerachByRoute.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_white));
                    //mSerachByRoute.setTypeface(Typeface.DEFAULT_BOLD);
                    mSerachByCompany.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    mSerachByCity.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    mSearchView.clearQuery();
                    mSearchView.setSearchHint("Source To Destination");
                } else if (radioButtonID == R.id.search_by_company) {
                    Bundle params = new Bundle();
                    params.putString("search_by", "ByCompanyName");
                    mFirebaseAnalytics.logEvent("SearchBy", params);
                    searchTag = SEARCHTAG_COMPANY;
                    mSerachByRoute.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    mSerachByCompany.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_white));
                    //mSerachByCompany.setTypeface(Typeface.DEFAULT_BOLD);
                    mSerachByCity.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    mSearchView.clearQuery();
                    mSearchView.setSearchHint("Search by company name");
                } else if (radioButtonID == R.id.search_by_city) {
                    Bundle params = new Bundle();
                    params.putString("search_by", "ByCity");
                    mFirebaseAnalytics.logEvent("SearchBy", params);
                    searchTag = SEARCHTAG_CITY;
                    mSerachByRoute.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    mSerachByCompany.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    mSerachByCity.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_white));
                    //mSerachByCity.setTypeface(Typeface.DEFAULT_BOLD);
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

        mFiltersList = new ArrayList<>();

        mBtnApplyFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFiltersList.clear();
                Fleet filledFleet = mWorkingWithAdapter.getDataValues();
                for (int i = 0; i < filledFleet.getTrucks().size(); i++) {
                    Truck trucks = filledFleet.getTrucks().get(i);
                    boolean ihave =  filledFleet.getTrucks().get(i).isTruckHave();
                    if(ihave) {
                        String newFilter = trucks.getTruckType();
                        FilterPojo filterPojo = new FilterPojo(newFilter, 12, 1);
                        mFiltersList.add(filterPojo);
                    }
                    for (int j = 0; j < trucks.getTruckProperties().size(); j++) {
                        TruckProperty truckProperty = trucks.getTruckProperties().get(j);
                            for (Map.Entry<String, Boolean> entry : truckProperty.getProperties().entrySet()) {
                                boolean propertyValue =  entry.getValue();
                                if(propertyValue) {
                                    String newFilter = "Fleet." + trucks.getTruckType() + "." + truckProperty.getTitle() + "." + entry.getKey();
                                    FilterPojo filterPojo = new FilterPojo(newFilter, 11, 1);
                                    mFiltersList.add(filterPojo);
                                    Log.v("FilterAdded", newFilter);
                                }
                            }
//                        }
                    }
                }


                for (String f : checkBoxRecyclarAdapter1.getmDataMap().keySet()) {
                    if (checkBoxRecyclarAdapter1.getmDataMap().get(f)) {
                        FilterPojo filterPojo = new FilterPojo(f, 1, 1);
                        mFiltersList.add(filterPojo);
                    }
                }
                for (String f : checkBoxRecyclarAdapter2.getmDataMap().keySet()) {
                    if (checkBoxRecyclarAdapter2.getmDataMap().get(f)) {
                        FilterPojo filterPojo = new FilterPojo(f, 5, 1);
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

        mBtnClearFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initiateFiltersHashmaps();
                checkBoxRecyclarAdapter1.notifyDataSetChanged();
                checkBoxRecyclarAdapter2.notifyDataSetChanged();
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
                    case R.id.radioButton1: {
                        mSortIndex = ShortingType.ALPHA_ASSENDING;
                        break;
                    }
                    case R.id.radioButton2: {
                        mSortIndex = ShortingType.ALPHA_DECENDING;
                        break;
                    }
                    case R.id.radioButton4: {
                        mSortIndex = ShortingType.ACCOUNT_TYPE;
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
                                            R.drawable.ic_bubble_chart_white_24dp),
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

                createDialog(mFiltersList, searchTag, mSearchView.getQuery(), adapter.getItemCount());
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

    private void onAuthSuccess(FirebaseUser user) {
        PreferenceManager preferenceManager =  PreferenceManager.getInstance(getApplicationContext());
       String fcmTocken = preferenceManager.getFcmToken();
       if(fcmTocken != null) {
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
        isBookmarkSaved = false;

        //base query
        query = FirebaseFirestore.getInstance()
                .collection("partners");

        //apply filters
        for (FilterPojo f : mFiltersList) {
            switch (f.getmFilterType()) {
                case 11: {
                    String filterName = f.getmFilterName();
                    query = query.whereEqualTo(filterName , true);
                    break;
                }
                case 12: {
                    query = query.whereEqualTo("fleetVehicle." + f.getmFilterName(), true);
                    break;
                }
                case 2: {
                    query = query.whereEqualTo("mFiltersVehicle." + f.getmFilterName().toUpperCase().trim(), true);
                    break;
                }
                case 3: {
                    query = query.whereEqualTo("mFiltersVehicle." + f.getmFilterName().toUpperCase().trim(), true);
                    break;
                }
                case 4: {
                    query = query.whereEqualTo("mFiltersVehicle." + f.getmFilterName().toUpperCase().trim(), true);
                    break;
                }
                case 5: {
                    query = query.whereEqualTo("mTypesOfServices." + f.getmFilterName().toUpperCase().trim(), true);
                    break;
                }
                case 6: {
                    query = query.whereEqualTo("mNatureOfBusiness." + f.getmFilterName().toUpperCase().trim(), true);

                    break;
                }

            }

        }

        //apply sorting
        switch (mSortIndex) {
            case ShortingType.ALPHA_ASSENDING : {
                query = query.orderBy(DBFields.COMPANY_NAME, Query.Direction.ASCENDING);
                break;
            }
            case ShortingType.ALPHA_DECENDING : {
                query = query.orderBy(DBFields.COMPANY_NAME, Query.Direction.DESCENDING);
                break;
            }
            case ShortingType.ACCOUNT_TYPE : {
                query = query.orderBy(DBFields.ACCOUNT_STATUS, Query.Direction.DESCENDING);
                break;
            }
            default : {
                query = query.orderBy(DBFields.COMPANY_NAME);
                break;
            }
        }

        //apply sorts

        if (!s.isEmpty()) {
            mSearchQuery = s;
            switch (searchTag) {
                case SEARCHTAG_ROUTE: {
                    if(s.equals("1")){
                        if(mSourceCity.getmNearestHub() != null && mDestinationCity.getmNearestHub() !=null){
                            Logger.v("HUBS: "+mSourceCity.getmNearestHub().getmHubName()+" , "+mDestinationCity.getmNearestHub().getmHubName());

                            query = query
                                    .whereEqualTo("mSourceHubs." + mSourceCity.getmNearestHub().getmHubName().toUpperCase(), true)
                                    .whereEqualTo("mDestinationHubs." + mDestinationCity.getmNearestHub().getmHubName().toUpperCase(), true);
                        }else {
                            Logger.v("HUBS NULL "+mSourceCity.isFetchingHub()+" , "+mDestinationCity.isFetchingHub());
                        }

                    }else {
                        if (s.contains("To") || s.contains("to")) {
                            String sourceDestination[] = s.split("(?i:to)");
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
                case SEARCHTAG_COMPANY: {

                    if (isCompanySuggestionClicked) {
                        query = query.whereEqualTo(DBFields.COMPANY_NAME, s.trim());
                    } else {
                        query = query.whereGreaterThanOrEqualTo(DBFields.COMPANY_NAME, s.trim().toUpperCase());
                    }
                    isCompanySuggestionClicked = false;
                    break;
                }
                case SEARCHTAG_CITY: {
                    query = query.whereEqualTo("mCompanyAdderss.city", s.toUpperCase());
                    break;
                }
            }
        } else {
            if (mSortIndex != ShortingType.ACCOUNT_TYPE) {
                query = query.whereGreaterThan(DBFields.COMPANY_NAME, "");
            }
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

                        String addresstoset = "";
                                if(model != null && model.getmCompanyAdderss() != null) {
                                    addresstoset = model.getmCompanyAdderss().getAddress()
                                            + ", " + textUtils.toTitleCase(model.getmCompanyAdderss().getCity())
                                            + ", " + textUtils.toTitleCase(model.getmCompanyAdderss().getState());
                                }
                        if (model.getmCompanyAdderss().getPincode() != null) {
                            addresstoset = addresstoset + ", " + model.getmCompanyAdderss().getPincode();
                        }
                        partnersViewHolder.mAddress.setText(addresstoset);

                        int fleetSize = 0;
                        if(model.getVehicles() != null) {
                            fleetSize = model.getVehicles().size();
                        }
                        partnersViewHolder.mFleetSize.setText(String.valueOf(fleetSize));

                        ((PartnersViewHolder) holder).mShareCompany.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String companyName = model.getmCompanyName();
                                //set address
                                String addresstoset
                                        = model.getmCompanyAdderss().getAddress()
                                        + ", " + textUtils.toTitleCase(model.getmCompanyAdderss().getCity())
                                        + ", " + textUtils.toTitleCase(model.getmCompanyAdderss().getState());
                                if (model.getmCompanyAdderss().getPincode() != null) {
                                    addresstoset = addresstoset + ", " + model.getmCompanyAdderss().getPincode();
                                }

                                shareMesssages(MainActivity.this, companyName, addresstoset);
                            }
                        });

                        partnersViewHolder.mCompany.setText(textUtils.toTitleCase(model.getmCompanyName()));
                        Logger.v("onBind : " + textUtils.toTitleCase(model.getmCompanyName()) + " " + model.getmAccountStatus());

                        if (model.getmAccountStatus() >= 2) {
                            partnersViewHolder.mCompany
                                    .setCompoundDrawablesWithIntrinsicBounds(ContextCompat
                                                    .getDrawable(getApplicationContext(),
                                                            R.drawable.checkmark),
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
                        mAnimator.onCreateViewHolder(view);
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


        if(bookmarksAdapter!=null)
        bookmarksAdapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
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

                mUserDocRef = FirebaseFirestore.getInstance()
                        .collection("partners").document(mAuth.getCurrentUser().getPhoneNumber());

                mUserDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            Logger.v("document exist :" + mAuth.getCurrentUser().getPhoneNumber());

                            mUserDocRef = FirebaseFirestore.getInstance()
                                    .collection("partners").document(mAuth.getUid());
                            PartnerInfoPojo partnerInfoPojo = documentSnapshot.toObject(PartnerInfoPojo.class);
                            mUserDocRef.set(partnerInfoPojo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Logger.v("data set to :" + mAuth.getUid());
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
                                                    //for loadboard
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
                                                    //for like
                                                    break;
                                                }
                                                case 0: {
                                                    //nothing
                                                }
                                            }
                                            showSnackbar(R.string.sign_in_done);
                                        }
                                    });

                                }
                            });
                        } else {
                            Logger.v("document dosent exist :" + mAuth.getCurrentUser().getPhoneNumber());
                            if (dialogWait.isShowing()) {
                                dialogWait.dismiss();
                            }
                            switch (signinginfor) {
                                case 1: {
                                    //for loadboard
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
                                    //for like
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
            if (mAuth.getCurrentUser() != null) {
                // already signed in
                startActivity(new Intent(MainActivity.this, CompanyInfoActivity.class));
            } else {
                // not signed in
                signinginfor = 1;
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


        } else if (id == R.id.nav_inbox) {
            startActivity(new Intent(MainActivity.this, ChatHeadsActivity.class));

        }else if (id == R.id.nav_logout) {
            params = new Bundle();
            params.putString("logout", "Click");
            mFirebaseAnalytics.logEvent("ClickOnLogout", params);

            mAuth.signOut();
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
                } else {

                    switch (searchTag) {
                        case SEARCHTAG_ROUTE:
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

                                        String queary = newQuery.replace(mSourceCity.getmCityName()+" To ", "").toString().trim();
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
                            mSearchView.setSearchText(mSourceCity.getmCityName()+ " To " + selectedCity);
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

                            mSourceCity.setmCityName(selectedCity.replace("To","").trim());
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

    @Override
    public void onDestinationHubFetched(String destinationhub, int o) {
        setAdapter("1");
    }

    @Override
    public void onSourceHubFetched(String sourcehub, int o) {

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
                case SEARCHTAG_ROUTE: {
                    mUpperSearchTv.setCompoundDrawablesWithIntrinsicBounds(ContextCompat
                                    .getDrawable(getApplicationContext(),
                                            R.drawable.ic_directions_grey_24dp),
                            null,
                            null,
                            null);

                    break;
                }
                case SEARCHTAG_COMPANY: {
                    mUpperSearchTv.setCompoundDrawablesWithIntrinsicBounds(ContextCompat
                                    .getDrawable(getApplicationContext(),
                                            R.drawable.ic_domain_black_24dp),
                            null,
                            null,
                            null);
                    break;
                }
                case SEARCHTAG_CITY: {
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

    private void shareMesssages(Context context, String subject, String body) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            shareIntent.putExtra(Intent.EXTRA_TEXT, subject + "\n" + body + "\n" + " Share By: Indian Logistics Network \n" + "http://bit.ly/ILNAPPS");
            context.startActivity(Intent.createChooser(shareIntent, "Share via"));
        }
        catch (ActivityNotFoundException exception) {
            Toast.makeText(context, "No application found for send Email" , Toast.LENGTH_LONG).show();
        }
    }

    //****************************Filter NEW******************
    public interface OnTruckPropertyValueChange {
        public void onPropertyChange(Map<String,Boolean> properties);
    }

    public interface OnTruckValueChange {
        public void onTruckPropertiesChange(List<TruckProperty> truckProperties);
    }
    public class WorkingWithAdapter extends RecyclerView.Adapter<WorkingWithHolderNew> {
        private  Fleet mDataValues;
        private Context mContext;
        private int getDataValuesSize() {
            return mDataValues.getTrucks().size();
        }

        // data is passed into the constructor
        public WorkingWithAdapter(Context context,Fleet fleet) {
            mContext = context;
            this.mDataValues = fleet;
        }

        private void setDataValues(Fleet dataValues) {
            mDataValues = dataValues;
            this.notifyDataSetChanged();
        }

        private Fleet getDataValues() {
            return  this.mDataValues;
        }

        // inflates the row layout from xml when needed
        @Override
        public WorkingWithHolderNew onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_working_with_vehicle, parent, false);
            WorkingWithHolderNew viewHolder = new WorkingWithHolderNew(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final WorkingWithHolderNew holder, final int position) {
            String truckType = mDataValues.getTrucks().get(position).getTruckType();
//            boolean value = mDataValues.get(key);
            PropertiesAdaptor propertiesAdaptor = new PropertiesAdaptor(MainActivity.this, mDataValues.getTrucks().get(position).getTruckProperties(), new OnTruckValueChange() {
                @Override
                public void onTruckPropertiesChange(List<TruckProperty> truckProperties) {
                    mDataValues.getTrucks().get(position).setTruckProperties(truckProperties);
                }
            });
            holder.propertyList.setLayoutManager(new LinearLayoutManager(this.mContext));
            holder.propertyList.setAdapter(propertiesAdaptor);
            holder.propertyList.addItemDecoration(new DividerItemDecoration(MainActivity.this,
                    DividerItemDecoration.VERTICAL));
            holder.mIHave.setText(truckType);
//            holder.setDataValue(key);
            holder.mIHave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mDataValues.getTrucks().get(position).setTruckHave(isChecked);
                    if(isChecked) {
                        holder.propertyList.setVisibility(View.VISIBLE);
                    } else {
                        holder.propertyList.setVisibility(View.GONE);
                    }
                }
            });
//            holder.onBind(mContext, holder);
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mDataValues.getTrucks().size();
        }
    }

    ///************************************PRoperties Adaptor
    public class PropertiesAdaptor extends RecyclerView.Adapter<TruckPropertiesViewHolder> {
        private  List<TruckProperty> truckProperties;
        private Context mContext;
        private OnTruckValueChange onTruckValueChange;
        private int getDataValuesSize() {
            return truckProperties.size();
        }

        // data is passed into the constructor
        public PropertiesAdaptor(Context context, List<TruckProperty> truckProperties, OnTruckValueChange onTruckValueChange) {
            this.truckProperties = truckProperties;
            this.onTruckValueChange = onTruckValueChange;
            mContext = context;
        }

        private void setDataValues(List<TruckProperty> truckProperties) {
            this.truckProperties = truckProperties;
            this.notifyDataSetChanged();
        }

        // inflates the row layout from xml when needed
        @Override
        public TruckPropertiesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_truck_property, parent, false);
            TruckPropertiesViewHolder viewHolder = new TruckPropertiesViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final TruckPropertiesViewHolder holder, final int position) {
            String key = truckProperties.get(position).getTitle();
            holder.mPropertyTitle.setText(key);

            PropertiesValuesAdaptor propertiesValueAdaptor = new PropertiesValuesAdaptor(mContext, truckProperties.get(position).getProperties(), new OnTruckPropertyValueChange() {
                @Override
                public void onPropertyChange(Map<String, Boolean> properties) {
                    truckProperties.get(position).setProperties(properties);
                    onTruckValueChange.onTruckPropertiesChange(truckProperties);
                }
            });
            holder.mPropertiesValues.setLayoutManager(new GridLayoutManager(this.mContext, 3));
            holder.mPropertiesValues.setAdapter(propertiesValueAdaptor);
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return truckProperties.size();

        }
    }
//************************************** PropertiesValueAdaptor

    public class PropertiesValuesAdaptor extends RecyclerView.Adapter<TruckPropertiesValueViewHolder> {
        private   Map<String,Boolean> properties;
        private OnTruckPropertyValueChange onTruckPropertyValueChange;
        private int getDataValuesSize() {
            return properties.size();
        }

        // data is passed into the constructor
        public PropertiesValuesAdaptor(Context context, Map<String,Boolean> properties, OnTruckPropertyValueChange onTruckPropertyValueChange) {
            this.properties = properties;
            this.onTruckPropertyValueChange = onTruckPropertyValueChange;
        }

        private void setDataValues( Map<String,Boolean> properties) {
            this.properties = properties;
            this.notifyDataSetChanged();
        }

        // inflates the row layout from xml when needed
        @Override
        public TruckPropertiesValueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_truck_property_value, parent, false);
            TruckPropertiesValueViewHolder viewHolder = new TruckPropertiesValueViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final TruckPropertiesValueViewHolder holder, final int position) {
            final String  key = new ArrayList<>( properties.keySet()).get(position);
            holder.mPropertyOnOff.setText(key);
            holder.mPropertyOnOff.setChecked(false);
            holder.mPropertyOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean value) {
                    properties.put(key.trim(), value);
                    onTruckPropertyValueChange.onPropertyChange(properties);
                }
            });
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return this.properties.size();

        }
    }

//***************************************


    private double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
