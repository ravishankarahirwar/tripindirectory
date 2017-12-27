package directory.tripin.com.tripindirectory.newactivities;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.support.v4.app.ActivityOptionsCompat;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
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
import directory.tripin.com.tripindirectory.adapters.PartnersAdapter1;
import directory.tripin.com.tripindirectory.adapters.PartnersViewHolder;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.manager.CityManager;
import directory.tripin.com.tripindirectory.manager.PartnersManager;
import directory.tripin.com.tripindirectory.manager.PreferenceManager;
import directory.tripin.com.tripindirectory.manager.TokenManager;
import directory.tripin.com.tripindirectory.model.ContactPersonPojo;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;
import directory.tripin.com.tripindirectory.model.SuggestionCompanyName;
import directory.tripin.com.tripindirectory.role.OnBottomReachedListener;
import directory.tripin.com.tripindirectory.utils.SearchData;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

public class MainActivity1 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private static final int SEARCHTAG_ROUTE = 0;
    private static final int SEARCHTAG_TRANSPORTER = 1;
    private static final int SEARCHTAG_PEOPLE = 2;
    private static final int RC_SIGN_IN = 123;

    List<SuggestionCompanyName> companySuggestions = null;
    List<String> companynamesuggestions = null;


    DocumentReference mUserDocRef;


    FirebaseAuth auth;
    FirestoreRecyclerOptions<PartnerInfoPojo> options;
    FirestoreRecyclerAdapter adapter;

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
    private String mSourceCity;
    private String mDestinationCity;

    private RadioButton radioButton3;
    private RadioButton radioButton2;
    private RadioButton radioButton1;




    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    public interface OnFindSuggestionsListener {
        void onResults(List<SuggestionCompanyName> results);
    }

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

        query = FirebaseFirestore.getInstance()
                .collection("partners").orderBy("mCompanyName");

        options = new FirestoreRecyclerOptions.Builder<PartnerInfoPojo>()
                .setQuery(query, PartnerInfoPojo.class).build();

        adapter = new FirestoreRecyclerAdapter<PartnerInfoPojo, PartnersViewHolder>(options) {
            @Override
            public void onBindViewHolder(final PartnersViewHolder holder, int position, final PartnerInfoPojo model) {
                if (model.getmCompanyAdderss().getAddress() != null) {
                    holder.mAddress.setText(model.getmCompanyAdderss().getAddress());
                }
                if (model.getmCompanyName() != null) {
                    holder.mCompany.setText(model.getmCompanyName());
                }

                holder.mCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

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

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity1.this,PartnerDetailActivity.class));

                        Intent intent = new Intent(MainActivity1.this, PartnerDetailActivity.class);
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(getParent(), holder.mCompany, "compname");
                        startActivity(intent, options.toBundle());
                    }
                });

            }

            @Override
            public PartnersViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.single_partner_row1, group, false);
                return new PartnersViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                Logger.v("on Data changed");
            }
        };

        mPartnerList.setAdapter(adapter);


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

        radioButton3 = findViewById(R.id.search_by_people);
        radioButton2 = findViewById(R.id.search_by_transporter);
        radioButton1 = findViewById(R.id.search_by_route);


        mSearchTagRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int radioButtonID) {
                if (radioButtonID == R.id.search_by_route) {
                    searchTag = SEARCHTAG_ROUTE;
                    radioButton1.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_white));
                    radioButton2.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    radioButton3.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));

                    mSearchView.setSearchHint("Source To Destination");
                } else if (radioButtonID == R.id.search_by_transporter) {
                    searchTag = SEARCHTAG_TRANSPORTER;
                    radioButton1.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    radioButton2.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_white));
                    radioButton3.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));

                    mSearchView.setSearchHint("Search by transporter name");
                } else if (radioButtonID == R.id.search_by_people) {
                    searchTag = SEARCHTAG_PEOPLE;
                    radioButton1.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    radioButton2.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_grey));
                    radioButton3.setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.arrow_white));
                    mSearchView.setSearchHint("Search by people name");
                }
            }
        });
        setupSearchBar();
    }

    private List<SuggestionCompanyName> fetchAutoSuggestions(String s) {
        FirebaseFirestore.getInstance()
                .collection("partners").orderBy("mCompanyName").startAt(s).endAt(s + "\uf8ff")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Logger.v("on queried fetch Complete!!");
                        companySuggestions.clear();
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                SuggestionCompanyName suggestionCompanyName = new SuggestionCompanyName();
                                Log.d("suggestion", document.getId() + " => " + document.get("mCompanyName"));
                                suggestionCompanyName.setCompanyName(document.get("mCompanyName").toString());
                                companySuggestions.add(suggestionCompanyName);
                            }
                            Set<SuggestionCompanyName> hs = new LinkedHashSet<>();
                            hs.addAll(companySuggestions);
                            companySuggestions.clear();
                            companySuggestions.addAll(hs);
                            Logger.v("adapter set!!");

                        } else {
                            Log.d("onComplete", "Error getting documents: ", task.getException());
                        }
                    }
                });
        return companySuggestions;
    }


    private void setAdapter(String s) {
        adapter.stopListening();
        adapter.notifyDataSetChanged();
        query = FirebaseFirestore.getInstance()
                .collection("partners");

        if (!s.equals("")) {
            if (s.contains("To") || s.contains("to")) {
                String sourceDestination[] = s.split("(?i:to)");
                String source = sourceDestination[0].trim();
                String destination = sourceDestination[1].trim();
                query = FirebaseFirestore.getInstance()
               .collection("partners").whereEqualTo("mSourceCities."+ source.toUpperCase(), true).whereEqualTo("mDestinationCities."+ destination.toUpperCase(), true);


            } else {
                if(mSuggestionTapped){
                    query = FirebaseFirestore.getInstance()
                            .collection("partners").whereEqualTo("mCompanyName", s);
                }else {
                    query = FirebaseFirestore.getInstance()
                            .collection("partners").orderBy("mCompanyName").whereGreaterThan("mCompanyName", s);
                }

            }
        }
        options = new FirestoreRecyclerOptions.Builder<PartnerInfoPojo>()
                .setQuery(query, PartnerInfoPojo.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<PartnerInfoPojo, PartnersViewHolder>(options) {
            @Override
            public void onBindViewHolder(PartnersViewHolder holder, int position, PartnerInfoPojo model) {
                holder.mAddress.setText(model.getmCompanyAdderss().getAddress());
                holder.mCompany.setText(model.getmCompanyName());
            }

            @Override
            public PartnersViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.single_partner_row1, group, false);
                return new PartnersViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                Logger.v("on Data changed");
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
                                    Logger.v("data set to :"+auth.getUid());
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

        } else if (id == R.id.nav_send) {

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupSearchBar() {
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                    isSourceSelected = false;
                    mSourceCity = "";
                } else {

                    switch (searchTag) {
                        case SEARCHTAG_ROUTE:
                            if(isSourceSelected) {
                                String queary = newQuery.replace(mSourceCity, "").toString().trim();
                                new GetCityFromGoogleTask(new OnFindSuggestionsListener() {
                                    @Override
                                    public void onResults(List<SuggestionCompanyName> results) {
                                        mSearchView.swapSuggestions(results);
                                    }
                                }).execute(queary, null, null);
                            } else {
                                new GetCityFromGoogleTask(new OnFindSuggestionsListener() {
                                    @Override
                                    public void onResults(List<SuggestionCompanyName> results) {
                                        mSearchView.swapSuggestions(results);
                                    }
                                }).execute(newQuery, null, null);
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
                        case SEARCHTAG_TRANSPORTER:
                            List<SuggestionCompanyName> companySuggestionsForDropDown = fetchAutoSuggestions(newQuery);
                            mSearchView.swapSuggestions(companySuggestionsForDropDown);
                            break;
                    }


                }

//                Log.d(TAG, "onSearchTextChanged()");
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final com.arlib.floatingsearchview.suggestions.model.SearchSuggestion searchSuggestion) {

                String selectedCity = searchSuggestion.getBody();
                if(isSourceSelected) {
                    mSearchView.setSearchText(mSourceCity + " " + selectedCity);
                } else {
                    mSearchView.setSearchText(selectedCity);
                }
                mSearchView.clearSuggestions();
                mSourceCity = selectedCity;
                isSourceSelected = !isSourceSelected;

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

    private class GetCityFromGoogleTask extends AsyncTask<String,Void, List<SuggestionCompanyName>> {
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
                ArrayList<AutocompletePrediction>  autocompletePredictions1 =  DataBufferUtils.freezeAndClose(autocompletePredictions);
                CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);

                for(AutocompletePrediction autocompletePrediction1 : autocompletePredictions1) {
                    SuggestionCompanyName suggestionCompanyName = new SuggestionCompanyName();
                    String cityName = autocompletePrediction1.getPrimaryText(STYLE_BOLD).toString();
                    if(isSourceSelected) {
                        suggestionCompanyName.setCompanyName(cityName);
                    } else {
                        suggestionCompanyName.setCompanyName(cityName + " to ");
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
