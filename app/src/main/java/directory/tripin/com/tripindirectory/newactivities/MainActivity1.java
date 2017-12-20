package directory.tripin.com.tripindirectory.newactivities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.activity.AddCompanyActivity;
import directory.tripin.com.tripindirectory.adapters.PartnersAdapter1;
import directory.tripin.com.tripindirectory.adapters.PartnersViewHolder;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.manager.PartnersManager;
import directory.tripin.com.tripindirectory.manager.PreferenceManager;
import directory.tripin.com.tripindirectory.manager.TokenManager;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;
import directory.tripin.com.tripindirectory.model.request.GetAuthToken;
import directory.tripin.com.tripindirectory.model.response.ElasticSearchResponse;
import directory.tripin.com.tripindirectory.model.response.TokenResponse;
import directory.tripin.com.tripindirectory.role.OnBottomReachedListener;
import directory.tripin.com.tripindirectory.utils.SpaceTokenizer;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

public class MainActivity1 extends AppCompatActivity implements OnBottomReachedListener {

    private static final int RC_SIGN_IN = 123;
    private static int SPLASH_SHOW_TIME = 1000;
    ArrayAdapter<String> monthAdapter = null;
    List<String> companynamesuggestions = null;
    Task<QuerySnapshot> mSuggestionsTask;
    FloatingActionButton mFloatingActionButton;
    boolean isListenerExecuted = false;
    FirebaseAuth auth;
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

        mPartnerList.setLayoutManager(new LinearLayoutManager(this));

        //get all doucuments

        query = FirebaseFirestore.getInstance()
                .collection("partners");
        options = new FirestoreRecyclerOptions.Builder<PartnerInfoPojo>()
                .setQuery(query, PartnerInfoPojo.class).build();
        adapter = new FirestoreRecyclerAdapter<PartnerInfoPojo, PartnersViewHolder>(options) {
            @Override
            public void onBindViewHolder(PartnersViewHolder holder, int position, PartnerInfoPojo model) {
//                holder.mAddress.setText(model.getmCompanyAdderss().getmAddress());
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


    }

    private void searchViewSetup() {

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final com.arlib.floatingsearchview.suggestions.model.SearchSuggestion searchSuggestion) {
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
                Toast.makeText(getApplicationContext(), query,
                        Toast.LENGTH_SHORT).show();
                setAdapter(query);
//                fetchAutoSuggestions(query);
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
    }

    private void init() {
        mContext = MainActivity1.this;
        mSearchView = (FloatingSearchView)findViewById(R.id.floating_search_view);

        mPartnersManager = new PartnersManager(mContext);
        mPreferenceManager = PreferenceManager.getInstance(mContext);
        mTokenManager = new TokenManager(mContext);
        companynamesuggestions = new ArrayList<>();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.toolbar_background));
        }

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                getDeviceId();
                getToken();
//                Toast.makeText(mContext, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
//                Toast.makeText(mContext, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }

        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_PHONE_STATE)
                .check();

//        mSearchField = this.findViewById(R.id.search_field);
//        mSearchField.setTokenizer(new SpaceTokenizer());
//
//        mSearchField.setThreshold(1);
//
//
//        mSearchField.setCursorVisible(false);

        /**
         * By default Mumbai would be search destination
         */

        if (!mPreferenceManager.isFirstTime()) {
//            mSearchField.setHint("Type company name to search");
            //performElasticSearch(mSearchField.getText().toString());
        }

        mPartnerList = findViewById(R.id.transporter_list);

//        mFloatingActionButton = findViewById(R.id.create_company);
//        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                auth = FirebaseAuth.getInstance();
//                if (auth.getCurrentUser() != null) {
//                    // already signed in
//                    startActivity(new Intent(MainActivity1.this, AddCompanyActivity.class));
//
//                } else {
//                    // not signed in
//                    startActivityForResult(
//                            // Get an instance of AuthUI based on the default app
//                            AuthUI.getInstance().createSignInIntentBuilder()
//                                    .setAvailableProviders(
//                                            Collections.singletonList(
//                                                    new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()))
//                                    .build(),
//                            RC_SIGN_IN);
//
//                }
//            }
//        });

        searchViewSetup();
    }

    private void setListeners() {

        mSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
                //fetch autosuggestions
                fetchAutoSuggestions(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSearchField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchField.setCursorVisible(true);
                Logger.v("on searchfield tapped!!");
            }
        });

        mSearchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    setAdapter(mSearchField.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });


    }

    private void fetchAutoSuggestions(String s) {

            FirebaseFirestore.getInstance()
                    .collection("partners").orderBy("mCompanyName").startAt(s).endAt(s + "\uf8ff")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            Logger.v("on queried fetch Complete!!");
                            if (task.isSuccessful()) {
                                companynamesuggestions.clear();
                                for (DocumentSnapshot document : task.getResult()) {
                                    Log.d("onComplete", document.getId() + " => " + document.get("mCompanyName"));
                                    companynamesuggestions.add(document.get("mCompanyName").toString());
                                }
                                Set<String> hs = new LinkedHashSet<>();
                                hs.addAll(companynamesuggestions);
                                companynamesuggestions.clear();
                                companynamesuggestions.addAll(hs);
                                monthAdapter = new ArrayAdapter<String>(MainActivity1.this, R.layout.hint_completion_layout, R.id.tvHintCompletion, companynamesuggestions);
                                mSearchField.setAdapter(monthAdapter);
                                Logger.v("adapter set!!");

                            } else {
                                Log.d("onComplete", "Error getting documents: ", task.getException());
                            }
                        }
                    });

    }


    private void setAdapter(String s) {
        adapter.stopListening();
        adapter.notifyDataSetChanged();

        //update your query here

        query = FirebaseFirestore.getInstance()
                .collection("partners");

        if (!s.equals("")) {
            if (s.contains("To")) {
                Toast.makeText(this, "Contain To", Toast.LENGTH_LONG).show();
                String sourceDestination[] = s.split("To");
                String source = sourceDestination[0].trim();
                String destination = sourceDestination[1].trim();
                query = FirebaseFirestore.getInstance()
                        .collection("partners").whereEqualTo("mSourceCities."+ source, true).whereEqualTo("destinationCities."+ destination, true);;

            } else {
                query = FirebaseFirestore.getInstance()
                        .collection("partners").whereEqualTo("mCompanyName", s);
            }
        }
        options = new FirestoreRecyclerOptions.Builder<PartnerInfoPojo>()
                .setQuery(query, PartnerInfoPojo.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<PartnerInfoPojo, PartnersViewHolder>(options) {
            @Override
            public void onBindViewHolder(PartnersViewHolder holder, int position, PartnerInfoPojo model) {
                holder.mAddress.setText(model.getmCompanyAdderss().getmAddress());
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
                showSnackbar(R.string.sign_in_done);
                startActivity(new Intent(MainActivity1.this, AddCompanyActivity.class));
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
        }
    }

    void showSnackbar(int m) {
        Toast.makeText(this, getString(m), Toast.LENGTH_LONG).show();
    }


    private void performElasticSearch(String query) {
        mPartnersManager.getElasticSearchRequest(mSearchField.getText().toString(), String.valueOf(mFromWhichEntry), String.valueOf(mPageSize), new PartnersManager.ElasticSearchListener() {
            @Override
            public void onSuccess(final ElasticSearchResponse elasticSearchResponse) {
                Logger.v("Elastic Search success");
                if (pd != null) {
                    pd.dismiss();
                }

                if (mFromWhichEntry == 1) {
                    mPartnerAdapter1 = new PartnersAdapter1(mContext, elasticSearchResponse);
                    mVerticalLayoutManager =
                            new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                    mPartnerList.setLayoutManager(mVerticalLayoutManager);
                    mPartnerList.setAdapter(mPartnerAdapter1);

                    hideSoftKeyboard();
                } else if (elasticSearchResponse.getData().size() != 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPartnerAdapter1.addNewList(elasticSearchResponse);
                        }
                    }, 2000);
                } else if (elasticSearchResponse.getData().size() == 0) {
                    mPartnerAdapter1.stopLoad();
                    shouldElastiSearchCall = false;
                }
                isListenerExecuted = false;
            }

            @Override
            public void onFailed() {
                Logger.v("Elastic Search failed");
                if (pd != null) {
                    pd.dismiss();
                }
            }
        });
    }

    private String getDeviceId() {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        mPreferenceManager.setDeviceId(telephonyManager.getDeviceId());
        return telephonyManager.getDeviceId();
    }

    private void getToken() {
        mTokenManager.getCurrentToken(new TokenManager.TokenListener() {
            @Override
            public void onSuccess(TokenResponse tokenResponse) {
                if (tokenResponse != null) {
                    Logger.v("Token: " + tokenResponse.getData().getToken());
                    String token = tokenResponse.getData().getToken();
                    String userId = tokenResponse.getData().getUserId();

                    mPreferenceManager.setToken(token);
                    mPreferenceManager.setUserId(userId);
//                    performElasticSearch(mSearchField.getText().toString());
                }
            }

            @Override
            public void onFailed(String message) {
                Logger.v(message);
            }
        }, generateRawData(mPreferenceManager.getDeviceId()));
    }

    private String generateRawData(String deviceId) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put(GetAuthToken.DEVICE_ID, deviceId);

        } catch (JSONException eJsonException) {
            eJsonException.printStackTrace();
        }
        final String requestBody = jsonBody.toString();
        Logger.v("rawData string generated from data in activity:  " + requestBody);
        return requestBody;
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
}
