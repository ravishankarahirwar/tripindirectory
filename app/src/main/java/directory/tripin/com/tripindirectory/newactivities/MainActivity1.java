package directory.tripin.com.tripindirectory.newactivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.adapters.PartnersAdapter1;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.manager.PartnersManager;
import directory.tripin.com.tripindirectory.model.response.ElasticSearchResponse;
import directory.tripin.com.tripindirectory.utils.SpaceTokenizer;

public class MainActivity1 extends AppCompatActivity {

    ArrayAdapter<String> monthAdapter = null;
    String months[] = null;
    private Context mContext;
    private MultiAutoCompleteTextView mSearchField;
    private RecyclerView mPartnerList;
    private PartnersAdapter1 mPartnerAdapter1;

    private PartnersManager mPartnersManager;

    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        init();
        setListeners();
    }

    private void init() {
        mContext = MainActivity1.this;

        mPartnersManager = new PartnersManager(mContext);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.toolbar_background));
        }

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
        mSearchField.setText("Bima Complex, Kalamboli, Navi Mumbai");
        performElasticSearch(mSearchField.getText().toString());

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
}
