package directory.tripin.com.tripindirectory.chatingactivities;

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
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.keiferstone.nonet.NoNet;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import directory.tripin.com.tripindirectory.chatingactivities.models.ChatHeadItemViewHolder;
import directory.tripin.com.tripindirectory.chatingactivities.models.ChatHeadPojo;
import directory.tripin.com.tripindirectory.helper.RecyclerViewAnimator;
import directory.tripin.com.tripindirectory.newlookcode.FacebookRequiredActivity;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.helper.CircleTransform;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.manager.PreferenceManager;
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
    private Menu mMenu;
    private RecyclerViewAnimator recyclerViewAnimator;



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
        recyclerViewAnimator = new RecyclerViewAnimator(mChatHeadsList);

        if(mAuth.getCurrentUser()==null
                || FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()==null
                || !preferenceManager.isFacebooked()){
            Intent i = new Intent(ChatHeadsActivity.this, FacebookRequiredActivity.class);
            i.putExtra("from","Chat");
            startActivityForResult(i,3);
            Toast.makeText(getApplicationContext(),"Login with Facebook To chat",Toast.LENGTH_LONG).show();
        }


        buildAdapter();
        internetCheck();
        //showIntro();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==3){
                Toast.makeText(getApplicationContext(),"Welcome",Toast.LENGTH_SHORT).show();
            }
        }else {
            if(requestCode==3){
                finish();
            }
        }
    }

    private void buildAdapter() {

        Query query = FirebaseFirestore.getInstance()
                .collection("chats")
                .document("chatheads")
                .collection(mAuth.getUid())
                .orderBy("mTimeStamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatHeadPojo> options = new FirestoreRecyclerOptions.Builder<ChatHeadPojo>()
                .setQuery(query, ChatHeadPojo.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ChatHeadPojo, ChatHeadItemViewHolder>(options) {


            @Override
            protected void onBindViewHolder(final ChatHeadItemViewHolder holder, final int position, final ChatHeadPojo model) {

                recyclerViewAnimator.onBindViewHolder(holder.itemView,position);
                if(model.getmOpponentCompanyName()!=null){
                    if (!model.getmOpponentCompanyName().isEmpty()) {
                        holder.title.setText(model.getmOpponentCompanyName());
                        if(model.getmOpponentCompanyName().equals("ILN Assistant")){
                            holder.title.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.blue_grey_700));
                        }
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
                    }else {
                        holder.thumbnail.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.ic_launcher_round));
                    }
                }else {
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
                recyclerViewAnimator.onCreateViewHolder(view);
                return new ChatHeadItemViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                mChatHeadsList.smoothScrollToPosition(0);
                lottieAnimationView.setVisibility(View.GONE);
                if(getItemCount()==0){
                    mTextNoChats.setVisibility(View.VISIBLE);
                }else {
                    mTextNoChats.setVisibility(View.GONE);
                }
            }
        };

        mChatHeadsList.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if(adapter!=null){
            adapter.stopListening();
        }
        super.onDestroy();
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
        mMenu = menu;
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

    /**
     * This method is use for checking internet connectivity
     * If there is no internet it will show an snackbar to user
     */
    private void internetCheck() {
        NoNet.monitor(this)
                .poll()
                .snackbar();
    }

    private  void  showIntro(){
        TapTargetSequence tapTargetSequence = new TapTargetSequence(this)
                .targets(
                        TapTarget
                                .forView(mMenu.findItem(R.id.action_help).getActionView(),
                                        "Your ILN Assistant here",
                                        "Any feedback, suggestion or need help? just chat with your ILN Assistant from here")
                                .transparentTarget(true)
                        .cancelable(true)
                )



                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        // Yay
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }


                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        // Boo
                    }
                });

        tapTargetSequence.start();
    }
}
