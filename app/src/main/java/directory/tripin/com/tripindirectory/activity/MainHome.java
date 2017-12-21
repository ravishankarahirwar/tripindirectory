package directory.tripin.com.tripindirectory.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.adapters.PartnersAdapter1;
import directory.tripin.com.tripindirectory.adapters.PartnersViewHolder;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.manager.PartnersManager;
import directory.tripin.com.tripindirectory.manager.PreferenceManager;
import directory.tripin.com.tripindirectory.manager.TokenManager;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;

public class MainHome extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        viewSetup();
    }

    private void init() {
        mPartnerList = findViewById(R.id.transporter_list);

    }

    private void viewSetup() {

        mPartnerList.setLayoutManager(new LinearLayoutManager(this));

        query = FirebaseFirestore.getInstance()
                .collection("partners");
        options = new FirestoreRecyclerOptions.Builder<PartnerInfoPojo>()
                .setQuery(query, PartnerInfoPojo.class).build();
        adapter = new FirestoreRecyclerAdapter<PartnerInfoPojo, PartnersViewHolder>(options) {
            @Override
            public void onBindViewHolder(PartnersViewHolder holder, int position, PartnerInfoPojo model) {
//                holder.mAddress.setText(model.getmCompanyAdderss().getmAddress());
                holder.mCompany.setText(model.getmCompanyName());
                Logger.v(model.getmCompanyName());

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

}
