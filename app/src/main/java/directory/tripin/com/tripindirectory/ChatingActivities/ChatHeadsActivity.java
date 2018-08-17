package directory.tripin.com.tripindirectory.ChatingActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import directory.tripin.com.tripindirectory.ChatingActivities.models.ChatHeadItemViewHolder;
import directory.tripin.com.tripindirectory.ChatingActivities.models.ChatHeadPojo;
import directory.tripin.com.tripindirectory.NewLookCode.FacebookRequiredActivity;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.helper.CircleTransform;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.manager.PreferenceManager;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;
import directory.tripin.com.tripindirectory.utils.TextUtils;

public class ChatHeadsActivity extends AppCompatActivity {

    private RecyclerView mChatHeadsList;
    private FirebaseAuth mAuth;
    private TextView mTextNoChats;
    private TextUtils textUtils;
    private PreferenceManager preferenceManager;
    private FirestoreRecyclerAdapter<ChatHeadPojo, ChatHeadItemViewHolder> adapter;
    private LottieAnimationView lottieAnimationView;
    private FirebaseAnalytics firebaseAnalytics;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_heads);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mChatHeadsList = findViewById(R.id.rv_chatheads);
        mChatHeadsList.setLayoutManager(new LinearLayoutManager(this));
        lottieAnimationView = findViewById(R.id.loading);
        mTextNoChats = findViewById(R.id.nochats);
        mAuth = FirebaseAuth.getInstance();
        textUtils = new TextUtils();
        preferenceManager = PreferenceManager.getInstance(this);

        if(mAuth.getCurrentUser()==null
                || FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()==null
                || !preferenceManager.isFacebooked()){
            Intent i = new Intent(this, FacebookRequiredActivity.class);
            i.putExtra("backstack",true);
            startActivity(i);
            Toast.makeText(getApplicationContext(),"Login with Facebook To chat",Toast.LENGTH_LONG).show();
        }

        if(!preferenceManager.isFacebooked()){
            startActivity(new Intent(ChatHeadsActivity.this, FacebookRequiredActivity.class));
        }

        buildAdapter();

    }

    private void buildAdapter() {

        Query query = FirebaseFirestore.getInstance()
                .collection("chats")
                .document("chatheads")
                .collection(mAuth.getUid())
                .orderBy("mTimeStamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatHeadPojo> options = new FirestoreRecyclerOptions.Builder<ChatHeadPojo>()
                .setQuery(query, ChatHeadPojo.class)
                .setLifecycleOwner(this)
                .build();

        adapter = new FirestoreRecyclerAdapter<ChatHeadPojo, ChatHeadItemViewHolder>(options) {


            @Override
            protected void onBindViewHolder(final ChatHeadItemViewHolder holder, final int position, final ChatHeadPojo model) {

                if(model.getmOpponentCompanyName()!=null){
                    if (!model.getmOpponentCompanyName().isEmpty()) {
                        holder.title.setText(textUtils.toTitleCase(model.getmOpponentCompanyName()));
                    } else {
                        holder.title.setText(model.getmORMN());
                    }
                }else {
                    holder.title.setText(model.getmORMN());
                }


                holder.timeago.setText(gettimeDiff(model.getmTimeStamp()));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ChatHeadsActivity.this, ChatRoomActivity.class);
                        intent.putExtra("ormn", model.getmORMN());
                        intent.putExtra("ouid", model.getmOUID());
                        intent.putExtra("ofuid", model.getmOFUID());
                        startActivity(intent);
                        Bundle bundle = new Bundle();
                        firebaseAnalytics.logEvent("z_chathead_clicked",bundle);
                    }
                });

                holder.lastmsg.setText(model.getmLastMessage());
                if(model.getmOpponentImageUrl()!=null){
                    if(!model.getmOpponentImageUrl().isEmpty()){
                        Picasso.with(getApplicationContext())
                                .load(model.getmOpponentImageUrl())
                                .placeholder(ContextCompat.getDrawable(getApplicationContext()
                                        , R.mipmap.ic_launcher_round))
                                .transform(new CircleTransform())
                                .fit()
                                .into(holder.thumbnail, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Logger.v("image set: " + position);
                                    }

                                    @Override
                                    public void onError() {
                                    }
                                });
                    }
                }

                FirebaseFirestore.getInstance().collection("chats")
                        .document("chatrooms")
                        .collection(model.getmChatRoomId())
                        .whereEqualTo("mMessageStatus", 0)
                        .whereEqualTo("mReciversUid", mAuth.getUid())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot documentSnapshots) {
                                if (!documentSnapshots.isEmpty()) {
                                    holder.badge.setNumber(documentSnapshots.size());
                                    preferenceManager.setInbocRead(false);
                                }else {
                                    holder.badge.setNumber(0);
                                    preferenceManager.setInbocRead(true);
                                }
                            }
                        });
            }

            @Override
            public ChatHeadItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_head, parent, false);
                return new ChatHeadItemViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                lottieAnimationView.setVisibility(View.GONE);
                if(getItemCount()==0){
                    mTextNoChats.setVisibility(View.VISIBLE);
                }else {
                    mTextNoChats.setVisibility(View.GONE);
                }
            }
        };

        mChatHeadsList.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public String gettimeDiff(Date startDate) {

        String diff = "";

        if (startDate != null) {

            Date endDate = new Date();

            long duration = endDate.getTime() - startDate.getTime();

            long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
            long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);

            if (diffInSeconds <= 0) {
                return "just now!";
            }

            if (diffInSeconds < 60) {
                diff = "" + diffInSeconds + " sec";
            } else if (diffInMinutes < 60) {
                diff = "" + diffInMinutes + " min";
            } else if (diffInHours < 24) {
                diff = "" + diffInHours + " hrs";
            } else {

                long daysago = duration / (1000 * 60 * 60 * 24);
                diff = "" + daysago + " days";
            }

        }
        return diff;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chatheads, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help: {
                Intent intent = new Intent(ChatHeadsActivity.this, ChatRoomActivity.class);
                intent.putExtra("ormn", "+919284089759");
                intent.putExtra("ouid", "pKeXxKD5HjS09p4pWoUcu8Vwouo1");
                intent.putExtra("ofuid", "4zRHiYyuLMXhsiUqA7ex27VR0Xv1");
                startActivity(intent);
                Bundle bundle = new Bundle();
                firebaseAnalytics.logEvent("z_assistant", bundle);
                break;
            }
            case R.id.action_comments: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
