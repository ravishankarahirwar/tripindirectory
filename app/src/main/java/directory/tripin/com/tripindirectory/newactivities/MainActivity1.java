package directory.tripin.com.tripindirectory.newactivities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.adapters.PartnersAdapter1;
import directory.tripin.com.tripindirectory.utils.SpaceTokenizer;

public class MainActivity1 extends AppCompatActivity {

    ArrayAdapter<String> monthAdapter = null;
    String months[] = null;
    private Context mContext;
    private MultiAutoCompleteTextView mSearchField;
    //    private CardView mCardView;
    private boolean isTranslated = false;

    private RecyclerView mPartnerList;
    private PartnersAdapter1 mPartnerAdapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        init();
        setListeners();
    }

    private void init() {
        mContext = MainActivity1.this;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.toolbar_background));
        }

        mSearchField = (MultiAutoCompleteTextView) this.findViewById(R.id.search_field);
        mSearchField.setTokenizer(new SpaceTokenizer());

        mSearchField.setThreshold(1);
      /*  mSearchField.setAdapter(ArrayAdapter.createFromResource(mContext, R.array.planets_array,
                android.R.layout.simple_dropdown_item_1line));*/


        months = getResources().getStringArray(R.array.planets_array);
        monthAdapter = new ArrayAdapter<String>(this, R.layout.hint_completion_layout, R.id.tvHintCompletion, months);
        mSearchField.setAdapter(monthAdapter);

        mSearchField.setCursorVisible(false);

//        mCardView = (CardView) findViewById(R.id.cardview);

        mPartnerList = (RecyclerView) findViewById(R.id.partner_list);
        mPartnerAdapter1 = new PartnersAdapter1(mContext);

        LinearLayoutManager verticalLayoutManager =
                new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mPartnerList.setLayoutManager(verticalLayoutManager);

        mPartnerList.setAdapter(mPartnerAdapter1);
    }

    private void setListeners() {

        /*mSearchField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mSearchField.setHint(null);
                } else {

                    if (mSearchField.getText() == null) {
                        mSearchField.setHint("Type your Enquiry");
                    }
                }
            }
        });*/

               /* mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isTranslated) {
                    mCardView.animate().translationX(150);
                    isTranslated = true;
                } else {
                    isTranslated = false;
                    mCardView.animate().translationX(0);
                }
            }
        });*/

        mSearchField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchField.setCursorVisible(true);
            }
        });


    }
}
