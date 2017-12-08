package directory.tripin.com.tripindirectory.newactivities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;

import android.os.Handler;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.activity.AddCompanyActivity;
import directory.tripin.com.tripindirectory.adapters.PartnersAdapter1;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.manager.PartnersManager;
import directory.tripin.com.tripindirectory.manager.PreferenceManager;
import directory.tripin.com.tripindirectory.manager.TokenManager;
import directory.tripin.com.tripindirectory.model.request.GetAuthToken;
import directory.tripin.com.tripindirectory.model.response.ElasticSearchResponse;
import directory.tripin.com.tripindirectory.model.response.TokenResponse;
import directory.tripin.com.tripindirectory.role.OnBottomReachedListener;
import directory.tripin.com.tripindirectory.utils.SpaceTokenizer;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

public class MainActivity1 extends AppCompatActivity implements OnBottomReachedListener{

    ArrayAdapter<String> monthAdapter = null;
    String months[] = null;
    private Context mContext;
    private MultiAutoCompleteTextView mSearchField;
    private RecyclerView mPartnerList;
    private PartnersAdapter1 mPartnerAdapter1;
    private PreferenceManager mPreferenceManager;
    private TokenManager mTokenManager;

    private PartnersManager mPartnersManager;

    private ProgressDialog pd;

    FloatingActionButton mFloatingActionButton;

    private int mFromWhichEntry = 1;
    private int mPageSize = 5;
    private LinearLayoutManager mVerticalLayoutManager;

    boolean isListenerExecuted = false;

    private int mLastPosition;

    private  boolean shouldElastiSearchCall = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        init();
        setListeners();
        if (mPreferenceManager.isFirstTime()) {
            Logger.v("First Time app opened");
//            mPreferenceManager.setFirstTime(false);
            startActivity(new Intent(mContext, TutorialScreensActivity.class));
            searchBarTutorial();
        } else {
            Logger.v("Multiple times app opened");
        }
    }

    private void init() {
        mContext = MainActivity1.this;

        mPartnersManager = new PartnersManager(mContext);
        mPreferenceManager = PreferenceManager.getInstance(mContext);
        mTokenManager = new TokenManager(mContext);

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

        mSearchField = (MultiAutoCompleteTextView) this.findViewById(R.id.search_field);
        mSearchField.setTokenizer(new SpaceTokenizer());

        mSearchField.setThreshold(1);

        months = getResources().getStringArray(R.array.planets_array);
        monthAdapter = new ArrayAdapter<String>(this, R.layout.hint_completion_layout, R.id.tvHintCompletion, months);
        mSearchField.setAdapter(monthAdapter);

        mSearchField.setCursorVisible(false);

        /**
         * By default Mumbai would be search destination
         */

        if (!mPreferenceManager.isFirstTime()) {
            mSearchField.setText("Bima Complex, Kalamboli, Navi Mumbai");
            performElasticSearch(mSearchField.getText().toString());
        }

        mPartnerList = (RecyclerView) findViewById(R.id.partner_list);

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.create_company);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity1.this, AddCompanyActivity.class));
            }
        });
    }

    private void setListeners() {

        mSearchField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchField.setCursorVisible(true);
            }
        });

        mSearchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                   showProgressDialog();
                    performElasticSearch(mSearchField.getText().toString());
                    return true;
                }
                return false;
            }
        });

        //Pagination
/*
        mPartnerList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = mVerticalLayoutManager.getChildCount();
                int totalItemCount = mVerticalLayoutManager.getItemCount();
                int firstVisibleItemPosition = mVerticalLayoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = mVerticalLayoutManager.findLastVisibleItemPosition();


                Logger.v("Child Count : " + visibleItemCount);
                Logger.v("Total Item Count : " + totalItemCount);
                Logger.v("First Visible Item position : " + firstVisibleItemPosition);
                Logger.v("Last Visible Item position : " + lastVisibleItemPosition);

                       */
/*if ((totalItemCount - visibleItemCount) <= (firstVisibleItemPosition + mPageSize)) {
                           // End has been reached, Do something
                           Logger.v("Recycler view should get cALLED");
                       }*//*


//                if ( firstVisibleItemPosition + visibleItemCount >= totalItemCount) {
//                if (visibleItemCount+1 < mPageSize) {
//                if (lastVisibleItemPosition == mFromWhichEntry + mPageSize -1) {
//                if ((totalItemCount - visibleItemCount) >= (firstVisibleItemPosition + mPageSize)) {


//                if ((totalItemCount <= (lastVisibleItemPosition + visibleItemCount))) {
//                if ((totalItemCount <= (lastVisibleItemPosition + firstVisibleItemPosition))) {
//                if ((totalItemCount <= (lastVisibleItemPosition+1))) {
                if (firstVisibleItemPosition + visibleItemCount >= totalItemCount) {
                    //End of list
                    Logger.v("Recycler view should get called");
                    mFromWhichEntry = mFromWhichEntry + mPageSize;
//                    performElasticSearch(mSearchField.getText().toString());
                }
            }
        });*/
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
                } else if(elasticSearchResponse.getData().size() != 0){
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
                            performElasticSearch(mSearchField.getText().toString());
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
     *  Call back listener for Pagination
     *  @param lastPosition
     */
    @Override
    public void onBottomReached(int lastPosition) {
        if(!isListenerExecuted) {
            Logger.v("Reached the end of the list with position: " + lastPosition);
            mLastPosition = lastPosition;
            mFromWhichEntry = mFromWhichEntry + mPageSize;
            if(shouldElastiSearchCall) {
                performElasticSearch(mSearchField.getText().toString());
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
