package directory.tripin.com.tripindirectory.newactivities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
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
import directory.tripin.com.tripindirectory.adapters.PartnersAdapter1;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.manager.PartnersManager;
import directory.tripin.com.tripindirectory.manager.PreferenceManager;
import directory.tripin.com.tripindirectory.manager.TokenManager;
import directory.tripin.com.tripindirectory.model.request.GetAuthToken;
import directory.tripin.com.tripindirectory.model.response.ElasticSearchResponse;
import directory.tripin.com.tripindirectory.model.response.TokenResponse;
import directory.tripin.com.tripindirectory.utils.SpaceTokenizer;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

public class MainActivity1 extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        init();
        setListeners();
        if (mPreferenceManager.isFirstTime()) {
            Logger.v("First Time app opened");
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

        if(!mPreferenceManager.isFirstTime()) {
            mSearchField.setText("Bima Complex, Kalamboli, Navi Mumbai");
            performElasticSearch(mSearchField.getText().toString());
        }
        
        mPartnerList = (RecyclerView) findViewById(R.id.partner_list);
       /* mPartnerAdapter1 = new PartnersAdapter1(mContext);

        LinearLayoutManager verticalLayoutManager =
                new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mPartnerList.setLayoutManager(verticalLayoutManager);

        mPartnerList.setAdapter(mPartnerAdapter1);*/

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
                    pd = new ProgressDialog(MainActivity1.this);
                    pd.setMessage("loading");
                    pd.show();
                    performElasticSearch(mSearchField.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void performElasticSearch(String query) {
        mPartnersManager.getElasticSearchRequest(mSearchField.getText().toString(), new PartnersManager.ElasticSearchListener() {
            @Override
            public void onSuccess(ElasticSearchResponse elasticSearchResponse) {
                Logger.v("Elastic Search success");
                if (pd != null) {
                    pd.dismiss();
                }

                mPartnerAdapter1 = new PartnersAdapter1(mContext, elasticSearchResponse);
                LinearLayoutManager verticalLayoutManager =
                        new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                mPartnerList.setLayoutManager(verticalLayoutManager);
                mPartnerList.setAdapter(mPartnerAdapter1);

                hideSoftKeyboard();
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
                    performElasticSearch(mSearchField.getText().toString());

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
//                            InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//                            if(imm.isAcceptingText()) {
//                                imm.hideSoftInputFromInputMethod(((Activity) mContext).getCurrentFocus().getWindowToken(), 0);
//                            }
                        }
                    }
                }).show();
    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

}
