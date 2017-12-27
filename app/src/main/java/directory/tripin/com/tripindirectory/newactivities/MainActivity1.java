package directory.tripin.com.tripindirectory.newactivities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import directory.tripin.com.tripindirectory.FormActivities.CompanyInfoActivity;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.adapters.PartnersAdapter1;
import directory.tripin.com.tripindirectory.adapters.PartnersViewHolder;
import directory.tripin.com.tripindirectory.helper.Logger;
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

public class MainActivity1 extends AppCompatActivity implements OnBottomReachedListener ,NavigationView.OnNavigationItemSelectedListener {

    private static final int SEARCHTAG_ROUTE = 0;
    private static final int SEARCHTAG_TRANSPORTER = 1;
    private static final int SEARCHTAG_PEOPLE = 2;
    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;


    private static final int RC_SIGN_IN = 123;
    private static int SPLASH_SHOW_TIME = 1000;
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    ArrayAdapter<String> monthAdapter = null;
    List<SuggestionCompanyName> companySuggestions = null;
    List<String> companynamesuggestions = null;
    Task<QuerySnapshot> mSuggestionsTask;
    FloatingActionButton mFloatingActionButton;
    boolean isListenerExecuted = false;
    FirebaseAuth auth;
    DocumentReference mUserDocRef;

    Query query;
    FirestoreRecyclerOptions<PartnerInfoPojo> options;
    FirestoreRecyclerAdapter adapter;
    private Context mContext;
    private MultiAutoCompleteTextView mSearchField;
    private RecyclerView mPartnerList;
    private PartnersAdapter1 mPartnerAdapter1;
    private PreferenceManager mPreferenceManager;
    private TokenManager mTokenManager;
    private PartnersManager mPartnersManager;
    private ProgressDialog pd;
    private int mFromWhichEntry = 1;
    private int mPageSize = 5;
    private LinearLayoutManager mVerticalLayoutManager;
    private int mLastPosition;
    private boolean shouldElastiSearchCall = true;
    private FloatingSearchView mSearchView;
    private DrawerLayout mDrawerLayout;
    private RadioGroup mSearchTagRadioGroup;
    private int searchTag = 0;
    private SearchData mSearchData;
    private Boolean mSuggestionTapped = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
//        setListeners();
        if (mPreferenceManager.isFirstTime()) {
            Logger.v("First Time app opened");
//            mPreferenceManager.setFirstTime(false);
            mPreferenceManager.setFirstTime(false);
            startActivity(new Intent(mContext, TutorialScreensActivity.class));
            searchBarTutorial();
        } else {
            Logger.v("Multiple times app opened");
        }


        //get all doucuments

        query = FirebaseFirestore.getInstance()
                .collection("partners").orderBy("mCompanyName");
        options = new FirestoreRecyclerOptions.Builder<PartnerInfoPojo>()
                .setQuery(query, PartnerInfoPojo.class).build();
        adapter = new FirestoreRecyclerAdapter<PartnerInfoPojo, PartnersViewHolder>(options) {
            @Override
            public void onBindViewHolder(PartnersViewHolder holder, int position,final PartnerInfoPojo model) {
                if(model.getmCompanyAdderss().getAddress() != null) {
                    holder.mAddress.setText(model.getmCompanyAdderss().getAddress());
                }
                if(model.getmCompanyName() != null) {
                    holder.mCompany.setText(model.getmCompanyName());
                }


                holder.mCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final ArrayList<String> phoneNumbers = new ArrayList<>();
                        List<ContactPersonPojo> contactPersonPojos =  model.getmContactPersonsList();
                        if (contactPersonPojos != null && contactPersonPojos.size() > 1) {

                            for (int i = 0; i < contactPersonPojos.size(); i++) {
                                if(model.getmContactPersonsList().get(i) != null) {
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

        mSearchView = findViewById(R.id.floating_search_view);
        mDrawerLayout =  findViewById(R.id.drawer_layout);
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

        mSearchTagRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int radioButtonID) {
                if (radioButtonID == R.id.search_by_route) {
                    searchTag = SEARCHTAG_ROUTE;
                    mSearchView.setSearchHint("Source To Destination");
                } else if (radioButtonID == R.id.search_by_transporter) {
                    searchTag = SEARCHTAG_TRANSPORTER;
                    mSearchView.setSearchHint("Search by transporter name");
                } else if (radioButtonID == R.id.search_by_people) {
                    searchTag = SEARCHTAG_PEOPLE;
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
//                                monthAdapter = new ArrayAdapter<String>(MainActivity1.this, R.layout.hint_completion_layout, R.id.tvHintCompletion, companynamesuggestions);
//                                mSearchField.setAdapter(monthAdapter);
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
        //update your query here

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
                        if(documentSnapshot.exists()){

                            Logger.v("document exist :"+auth.getCurrentUser().getPhoneNumber());

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
                        }else {
                            Logger.v("document dosent exist :"+auth.getCurrentUser().getPhoneNumber());
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
        } else  if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
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


    private void searchBarTutorial() {
        new MaterialTapTargetPrompt.Builder((Activity) mContext)
                .setPrimaryText("Search Box")
                .setSecondaryText("Enter your search text here and click the search icon on the keyboard")
                .setBackgroundColour(ContextCompat.getColor(mContext, R.color.primaryColor))
                .setPromptBackground(new RectanglePromptBackground())
                .setFocalColour(ContextCompat.getColor(mContext, R.color.primaryDarkColor))
                .setPromptFocal(new RectanglePromptFocal())
                .setAutoDismiss(false)
                .setAutoFinish(false)
                .setCaptureTouchEventOutsidePrompt(true)
                .setTarget(R.id.search_field)
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                            //Do something such as storing a value so that this prompt is never shown again
                            prompt.finish();
                            mSearchField.setText("Bima Complex, Kalamboli, Navi Mumbai");
                            //performElasticSearch(mSearchField.getText().toString());
//                            mPreferenceManager.setFirstTime(false);
                        }
                    }
                }).show();
    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

        }
    }

    /**
     * Call back listener for Pagination
     *
     * @param lastPosition
     */
    @Override
    public void onBottomReached(int lastPosition) {
        if (!isListenerExecuted) {
            Logger.v("Reached the end of the list with position: " + lastPosition);
            mLastPosition = lastPosition;
            mFromWhichEntry = mFromWhichEntry + mPageSize;
            if (shouldElastiSearchCall) {
                //performElasticSearch(mSearchField.getText().toString());
            }
//            showProgressDialog();
            isListenerExecuted = true;
        }
    }

    private void showProgressDialog() {
        pd = new ProgressDialog(MainActivity1.this);
        pd.setMessage("Loading");
        pd.show();
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
             Toast.makeText(getApplicationContext(),"Signed Out",Toast.LENGTH_SHORT).show();
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
                } else {

                    switch (searchTag) {
                        case SEARCHTAG_ROUTE :
                            mSearchData.findSuggestions(mContext, newQuery, 5,
                                    FIND_SUGGESTION_SIMULATED_DELAY, new SearchData.OnFindSuggestionsListener() {

                                        @Override
                                        public void onResults(List<SuggestionCompanyName> results) {

                                            //this will swap the data and
                                            //render the collapse/expand animations as necessary
                                            mSearchView.swapSuggestions(results);
//                                    Log.d(TAG, "360" + results.get(0).getStationCode());

                                            //let the users know that the background
                                            //process has completed
//                                            mSearchView.hideProgress();
                                        }
                                    });
                            break;
                        case SEARCHTAG_TRANSPORTER :
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
                mSearchView.setSearchText(searchSuggestion.getBody());
                mSuggestionTapped = true;
                setAdapter(searchSuggestion.getBody());
                mSearchView.clearSuggestions();

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
        if(query != null) {
            mSearchView.setSearchText(query);
            setAdapter(query);
        }
//        mSearchData.findSuggestions(mContext, query, 5,
//                FIND_SUGGESTION_SIMULATED_DELAY, new DataHelper.OnFindSuggestionsListener() {
//
//                    @Override
//                    public void onResults(final List<Station> results) {
//
//
//                    }
//                });
    }

}
