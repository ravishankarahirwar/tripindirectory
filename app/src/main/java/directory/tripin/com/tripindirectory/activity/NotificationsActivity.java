package directory.tripin.com.tripindirectory.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.adapters.UpdatesViewHolder;
import directory.tripin.com.tripindirectory.helper.RecyclerViewAnimator;
import directory.tripin.com.tripindirectory.model.UpdateInfoPojo;

public class NotificationsActivity extends AppCompatActivity {

    RecyclerView mUpdatesList;
    private DocumentReference mUserDocRef;
    private Query query;
    private RecyclerViewAnimator mAnimator;

    private FirestoreRecyclerOptions<UpdateInfoPojo> options;
    private FirestoreRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        mUpdatesList = findViewById(R.id.rv_updates);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mUpdatesList.setLayoutManager(mLayoutManager);
        mAnimator = new RecyclerViewAnimator(mUpdatesList);

//        For production only
        query = FirebaseFirestore.getInstance()
                .collection("updates").orderBy("mTimeStamp");

//        for testing purpose
//        query = FirebaseFirestore.getInstance()
//                .collection("updatestest").orderBy("mTimeStamp");

        options = new FirestoreRecyclerOptions.Builder<UpdateInfoPojo>()
                .setQuery(query, UpdateInfoPojo.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<UpdateInfoPojo, UpdatesViewHolder>(options) {


            @Override
            public UpdatesViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_notification_type_update, group, false);
                return new UpdatesViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();

            }

            @Override
            protected void onBindViewHolder(UpdatesViewHolder holder, int position, final UpdateInfoPojo model) {

                holder.title.setText(model.getmTitle());
                holder.description.setText("     "+model.getmDiscription());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(model.getmType().equals("0")){
                            final String appPackageName = getPackageName(); // package name of the app
                            Intent intentToPlayStore;
                            try {
                                intentToPlayStore = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                intentToPlayStore = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
                            }
                            startActivity(intentToPlayStore);

                        }

                        if(model.getmType().equals("1")){
                            Intent intentToURL;
                            String url = model.getmUrl();
                            intentToURL = new Intent(Intent.ACTION_VIEW);
                            intentToURL.setData(Uri.parse(url));
                            startActivity(intentToURL);
                        }

                    }
                });


            }
        };

        mUpdatesList.setAdapter(adapter);
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

}
