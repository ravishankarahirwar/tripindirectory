package directory.tripin.com.tripindirectory.newactivities;

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
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import directory.tripin.com.tripindirectory.FormActivities.CompanyInfoActivity;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.adapters.PartnersViewHolder;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.helper.RecyclerViewAnimator;
import directory.tripin.com.tripindirectory.manager.PartnersManager;
import directory.tripin.com.tripindirectory.manager.PreferenceManager;
import directory.tripin.com.tripindirectory.manager.TokenManager;
import directory.tripin.com.tripindirectory.model.ContactPersonPojo;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;
import directory.tripin.com.tripindirectory.model.SuggestionCompanyName;
import directory.tripin.com.tripindirectory.utils.SearchData;

public class MainActivity1 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_INVITE = 1001;
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;
    private static final int SEARCHTAG_ROUTE = 0;
    private static final int SEARCHTAG_COMPANY = 1;
    private static final int SEARCHTAG_CITY = 2;
    private static final int SEARCHTAG_TRANSPORTER = 3;

    private static final int RC_SIGN_IN = 123;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    List<SuggestionCompanyName> companySuggestions = null;
    List<String> companynamesuggestions = null;
    DocumentReference mUserDocRef;
    FirebaseAuth auth;
    FirestoreRecyclerOptions<PartnerInfoPojo> options;
    FirestoreRecyclerAdapter adapter;
    boolean isCompanySuggestionClicked = false;
    private Context mContext;
    private RecyclerView mPartnerList;
    private PreferenceManager mPreferenceManager;
    private TokenManager mTokenManager;
    private PartnersManager mPartnersManager;
    private ProgressDialog pd;
    private int mFromWhichEntry = 1;
    private int mPageSize = 5;
    private int mLastPosition;
    private boolean shouldElastiSearchCall = true;
    private FloatingSearchView mSearchView;
    private DrawerLayout mDrawerLayout;
    private RadioGroup mSearchTagRadioGroup;
    private int searchTag = 0;
    private SearchData mSearchData;
    private Boolean mSuggestionTapped = false;
    private Query query;
    private GeoDataClient mGeoDataClient;
    private boolean isSourceSelected = false;
    private boolean isDestinationSelected = false;
    private String mSourceCity;
    private String mDestinationCity;
    private RadioButton radioButton3;
    private RadioButton radioButton2;
    private RadioButton radioButton1;
    private RadioButton radioButton4;
    private FirebaseAnalytics mFirebaseAnalytics;
    private RecyclerViewAnimator mAnimator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

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

    private void startPartnerDetailActivity(String name, String uid) {

        Intent intent = new Intent(MainActivity1.this, PartnerDetailScrollingActivity.class);
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

    private void init() {
        mContext = MainActivity1.this;
        mSearchData = new SearchData();
        mGeoDataClient = Places.getGeoDataClient(this, null);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mSearchView = findViewById(R.id.floating_search_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mPartnerList = findViewById(R.id.transporter_list);
        mSearchTagRadioGroup = findViewById(R.id.search_tag_group);
        mPartnerList.setLayoutManager(new LinearLayoutManager(this));

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mPartnersManager = new PartnersManager(mContext);
        mPreferenceManager = PreferenceManager.getInstance(mContext);
        mTokenManager = new TokenManager(mContext);
        companynamesuggestions = new ArrayList<>();
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


        mSearchTagRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int radioButtonID) {
                if (radioButtonID == R.id.search_by_route) {
                    searchTag = SEARCHTAG_ROUTE;
                    radioButton1.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_white));
                    radioButton1.setTypeface(Typeface.DEFAULT_BOLD);
                    radioButton2.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    radioButton3.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    radioButton4.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    mSearchView.clearQuery();
                    mSearchView.setSearchHint("Source To Destination");
                } else if (radioButtonID == R.id.search_by_company) {
                    searchTag = SEARCHTAG_COMPANY;
                    radioButton1.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    radioButton2.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_white));
                    radioButton2.setTypeface(Typeface.DEFAULT_BOLD);
                    radioButton3.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    radioButton4.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    mSearchView.clearQuery();
                    mSearchView.setSearchHint("Search by company name");
                } else if (radioButtonID == R.id.search_by_transporter) {
                    searchTag = SEARCHTAG_TRANSPORTER;
                    radioButton1.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    radioButton2.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    radioButton3.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_white));
                    radioButton3.setTypeface(Typeface.DEFAULT_BOLD);
                    radioButton4.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    mSearchView.clearQuery();
                    mSearchView.setSearchHint("Search by transporter name");
                } else if (radioButtonID == R.id.search_by_city) {
                    searchTag = SEARCHTAG_CITY;
                    radioButton1.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    radioButton2.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    radioButton3.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    radioButton4.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_white));
                    radioButton4.setTypeface(Typeface.DEFAULT_BOLD);
                    mSearchView.clearQuery();
                    mSearchView.setSearchHint("Search in city");
                }
            }
        });
        setupSearchBar();
    }

    private void fetchAutoSuggestions(String s) {
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
                            Set<SuggestionCompanyName> hs = new LinkedHashSet<>();
                            hs.addAll(companySuggestions);
                            companySuggestions.clear();
                            companySuggestions.addAll(hs);
                            mSearchView.swapSuggestions(companySuggestions);

                            Logger.v("adapter set!!");

                        } else {
                            Log.d("onComplete", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void setAdapter(String s) {

        query = FirebaseFirestore.getInstance()
                .collection("partners");

        if (!s.isEmpty()) {
            switch (searchTag) {
                case SEARCHTAG_ROUTE: {
                    if (s.contains("To") || s.contains("to")) {
                        String sourceDestination[] = s.split("(?i:to)");
                        String source = sourceDestination[0].trim();
                        String destination = sourceDestination[1].trim();
                        query = FirebaseFirestore.getInstance()
                                .collection("partners").whereEqualTo("mSourceCities." + source.toUpperCase(), true).whereEqualTo("mDestinationCities." + destination.toUpperCase(), true);
                    } else {
                        Toast.makeText(this, "Invalid Route Query", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
                case SEARCHTAG_COMPANY: {

                    query = FirebaseFirestore.getInstance()
                            .collection("partners").orderBy("mCompanyName").whereGreaterThanOrEqualTo("mCompanyName", s.trim());
                    Logger.v("company search query: " + s);

                    break;
                }
                case SEARCHTAG_CITY: {
                    query = FirebaseFirestore.getInstance()
                            .collection("partners").whereEqualTo("mCompanyAdderss.city", s.toUpperCase());

                    break;
                }
            }
        }


        options = new FirestoreRecyclerOptions.Builder<PartnerInfoPojo>()
                .setQuery(query, PartnerInfoPojo.class)
                .build();

        mAnimator = new RecyclerViewAnimator(mPartnerList);

        adapter = new FirestoreRecyclerAdapter<PartnerInfoPojo, PartnersViewHolder>(options) {


            @Override
            public void onBindViewHolder(final PartnersViewHolder holder, int position, final PartnerInfoPojo model) {
                holder.mAddress.setText(model.getmCompanyAdderss().getAddress());
                holder.mCompany.setText(model.getmCompanyName());


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                        startPartnerDetailActivity(model.getmCompanyName(), snapshot.getId());
                    }
                });


                holder.mCall.setOnClickListener(new View.OnClickListener() {
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
            }

            @Override
            public PartnersViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.single_partner_row1, group, false);
                mAnimator.onCreateViewHolder(view);
                return new PartnersViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                Logger.v("on Data changed");
                mSearchView.clearSuggestions();
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
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
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
                                            startActivity(new Intent(MainActivity1.this, CompanyInfoActivity.class));
                                            showSnackbar(R.string.sign_in_done);
                                        }
                                    });

                                }
                            });
                        } else {
                            Logger.v("document dosent exist :" + auth.getCurrentUser().getPhoneNumber());
                            startActivity(new Intent(MainActivity1.this, CompanyInfoActivity.class));
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
        }
    }

    void showSnackbar(int m) {
        Toast.makeText(this, getString(m), Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_business) {
            auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                // already signed in
                startActivity(new Intent(MainActivity1.this, CompanyInfoActivity.class));
            } else {
                // not signed in
                startActivityForResult(
                        // Get an instance of AuthUI based on the default app
                        AuthUI.getInstance().createSignInIntentBuilder()
                                .setAvailableProviders(
                                        Collections.singletonList(
                                                new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()))
                                .build(),
                        RC_SIGN_IN);
            }
        } else if (id == R.id.nav_notification) {

        } else if (id == R.id.nav_logout) {
            auth = FirebaseAuth.getInstance();
            auth.signOut();
            Toast.makeText(getApplicationContext(), "Signed Out", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {
            shareApp();
        } else if (id == R.id.nav_feedback) {
            sendFeedback ();
        }else if (id == R.id.nav_invite) {
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
        } catch(Exception e) {
            //e.toString();
        }
    }

    private void sendFeedback () {
        try {
            Intent Email = new Intent(Intent.ACTION_SENDTO,  Uri.fromParts(
                    "mailto","ravishankar.ahirwar@tripin.co.in", null));
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


//                            CityManager cityManager = new CityManager(mContext, newQuery, new CityManager.CitySuggestionListener() {
//                                @Override
//                                public void onSuccess(List<SuggestionCompanyName> suggestions) {
//
//                                }
//
//                                @Override
//                                public void onFaailed() {
//
//                                }
//                            });

//                            mSearchData.findSuggestions(mContext, "Mu", 5,
//                                    FIND_SUGGESTION_SIMULATED_DELAY, new SearchData.OnFindSuggestionsListener() {
//
//                                        @Override
//                                        public void onResults(List<SuggestionCompanyName> results) {
//
//                                            //this will swap the data and
//                                            //render the collapse/expand animations as necessary
//                                            mSearchView.swapSuggestions(results);
////                                    Log.d(TAG, "360" + results.get(0).getStationCode());
//
//                                            //let the users know that the background
//                                            //process has completed
////                                            mSearchView.hideProgress();
//                                        }
//                                    });
                            break;
                        case SEARCHTAG_COMPANY:
                            if(newQuery.length()-oldQuery.length()>0){
                                //foreward

                            }else {
                                //backword
                                fetchAutoSuggestions(newQuery);

                            }

                            break;

                        case SEARCHTAG_CITY:
                            new GetCityFromGoogleTask(new OnFindSuggestionsListener() {
                                @Override
                                public void onResults(List<SuggestionCompanyName> results) {
                                    mSearchView.swapSuggestions(results);
                                }
                            }).execute(newQuery, null, null);
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

                            mSearchView.setSearchText(mSourceCity + selectedCity);
                            mSearchView.clearFocus();
                            mSearchView.clearSearchFocus();
                            mSearchView.clearSuggestions();

                            setAdapter(mSourceCity + selectedCity);
                            isDestinationSelected = true;
                        } else {
                            mSearchView.setSearchText(selectedCity);
                            isSourceSelected = true;
                        }


                        mSearchView.clearSuggestions();
                        mSourceCity = selectedCity;
                        break;
                    }

                    case SEARCHTAG_COMPANY: {
                        String companyname = searchSuggestion.getBody().trim();

                        Logger.v("suggestion clicked");
                        Log.d("COMPANY", "suggestion.....");

                        mSearchView.setSearchText(companyname);
                        mSearchView.clearFocus();
                        mSearchView.clearSearchFocus();
                        mSearchView.clearSuggestions();
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
                mSuggestionTapped = false;
                Bundle params = new Bundle();
                params.putString("key_search", "Click");
                mFirebaseAnalytics.logEvent("SearchByKeyBoard", params);
                setAdapter(query);

//                startUpDownActivity(new Station("39", "Mumbai CST", "CSTM", LineIndicator.CENTER));

//                mLastQuery = query;
//
//                DataHelper.findColors(getActivity(), query,
//                        new DataHelper.OnFindColorsListener() {
//
//                            @Override
//                            public void onResults(List<ColorWrapper> results) {
//                                //show search results
//                            }
//
//                        });
//                Log.d(TAG, "onSearchAction()");
            }
        });

//        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
//            @Override
//            public void onFocus() {
//
//                //show suggestions when search bar gains focus (typically history suggestions)
//                mSearchView.swapSuggestions(DataHelper.getHistory(getActivity(), 3));
//
//                Log.d(TAG, "onFocus()");
//            }
//
//            @Override
//            public void onFocusCleared() {
//
//                //set the title of the bar so that when focus is returned a new query begins
//                mSearchView.setSearchBarTitle(mLastQuery);
//
//                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
//                //mSearchView.setSearchText(searchSuggestion.getBody());
//
//                Log.d(TAG, "onFocusCleared()");
//            }
//        });
//
//
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
//
//        //use this listener to listen to menu clicks when app:floatingSearch_leftAction="showHome"
//        mSearchView.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
//            @Override
//            public void onHomeClicked() {
//
//                Log.d(TAG, "onHomeClicked()");
//            }
//        });
//
        /*
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
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Are you at ?");
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

}
