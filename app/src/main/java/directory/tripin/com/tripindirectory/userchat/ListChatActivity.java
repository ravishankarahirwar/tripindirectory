package directory.tripin.com.tripindirectory.userchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.ocpsoft.prettytime.PrettyTime;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.chat.models.Chat;
import directory.tripin.com.tripindirectory.chat.models.User;
import directory.tripin.com.tripindirectory.chat.ui.viewholder.ListChatViewHolder;
import directory.tripin.com.tripindirectory.chat.utils.Constants;
import directory.tripin.com.tripindirectory.utils.ServiceUtils;

public class ListChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter<ListChatViewHolder> mAdapter;
    private String mUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chat);

        mRecyclerView =  findViewById(R.id.rv);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mUserId = user.getUid();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ListChatActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setBackgroundResource(R.color.white);

        initiate();
        mRecyclerView.setAdapter(mAdapter);
    }

    public void initiate(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;

        DatabaseReference visitores = FirebaseDatabase.getInstance().getReference(Constants.ARG_USERS).child(mUserId).child(Constants.ARG_CHAT_LIST);

        visitores.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0 ){
                    Log.v("UserCount" , "count :" + dataSnapshot.getChildrenCount());
//                    mProgressLayout.setVisibility(View.GONE);
//                    mEmptyLayout.setVisibility(View.GONE);
                } else {

//                    mProgressLayout.setVisibility(View.GONE);
//                    mEmptyLayout.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query query = visitores.orderByChild("timestamp").endAt(System.currentTimeMillis());

        mAdapter = getFirebaseRecyclerAdapter(query);


        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);


            }
        });

    }

    private FirebaseRecyclerAdapter<Chat, ListChatViewHolder> getFirebaseRecyclerAdapter(Query query) {
        return new FirebaseRecyclerAdapter<Chat, ListChatViewHolder>(Chat.class, R.layout.chat_list_item, ListChatViewHolder.class, query) {
            @Override
            public void populateViewHolder(final ListChatViewHolder visitorsViewHolder, final Chat visitors, final int position) {
                setupPost(visitorsViewHolder, visitors, position, null);
            }

            @Override
            public void onViewRecycled(ListChatViewHolder holder) {
                super.onViewRecycled(holder);
//                FirebaseUtil.getLikesRef().child(holder.mPostKey).removeEventListener(holder.mLikeListener);
            }
        };
    }

    private void setupPost(final ListChatViewHolder chatListViewHolder, final Chat chat, final int position, final String inPostKey) {

        if (chat.getSenderUid().equals(mUserId)) {

            // Text Message
            DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(chat.getReceiverUid());
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);

                    assert user != null;
                    if (user.getisOnline()) {

                        chatListViewHolder.setOnline(String.valueOf(R.drawable.ic_online_15_0_alizarin));

                    } else {

                        if (System.currentTimeMillis() - user.getTimestamp() > ServiceUtils.TIME_TO_OFFLINE) {

                            chatListViewHolder.setOffline(String.valueOf(R.drawable.ic_offline_15_0_alizarin));

                        } else if (System.currentTimeMillis() - user.getTimestamp() > ServiceUtils.TIME_TO_SOON) {

                            chatListViewHolder.setSoon(String.valueOf(R.drawable.last_min));
                        }
                    }


                    if (user.getPhotoThumb() != null) {
                        chatListViewHolder.setPhoto(user.getPhotoThumb());

                    } else {


                        chatListViewHolder.setPhoto(user.getPhotoUrl());
                    }


                    if (chat.getReceiver() != null) {

                        chatListViewHolder.setUser(chat.getReceiver(), user.getUid(), user.getFcmUserDeviceId());
                    } else {

                        chatListViewHolder.setUser("no_name", user.getUid(), user.getFcmUserDeviceId());
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });b


            if (chat.getType() == 1) {


                chatListViewHolder.setLastMessage(chat.getMessage());

            } else if (chat.getType() == 2) {


                chatListViewHolder.setLastMessage("Audio message");

            } else if (chat.getType() == 3) {

                chatListViewHolder.setLastMessage("Image message");

            }

            DateTime senttime = new DateTime(chat.getTimestamp());

            String time = new PrettyTime().format(senttime.toDate());

            chatListViewHolder.setTime(time);

            if (chat.isIsread()) {

                chatListViewHolder.setRead();
            } else {
                chatListViewHolder.setUnRead();

            }


        } else if (chat.getReceiverUid().equals(mUserId)) {

            // Text Message
            DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(chat.getSenderUid());
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);

                    assert user != null;
                    if (user.getPhotoThumb() != null) {

                        chatListViewHolder.setPhoto(user.getPhotoThumb());

                    } else {


                        chatListViewHolder.setPhoto(user.getPhotoUrl());
                    }


                    if (user.getisOnline()) {

                        chatListViewHolder.setOnline(String.valueOf(R.drawable.ic_online_15_0_alizarin));

                    } else {

                        if (System.currentTimeMillis() - user.getTimestamp() > ServiceUtils.TIME_TO_OFFLINE) {

                            chatListViewHolder.setOffline(String.valueOf(R.drawable.ic_offline_15_0_alizarin));

                        } else if (System.currentTimeMillis() - user.getTimestamp() > ServiceUtils.TIME_TO_SOON) {

                            chatListViewHolder.setSoon(String.valueOf(R.drawable.last_min));
                        }
                    }


                    if (user.getName() != null) {

                        chatListViewHolder.setUser(user.getName(), user.getUid(), user.getFcmUserDeviceId());
                    } else {

                        chatListViewHolder.setUser("no_name", user.getUid(), user.getFcmUserDeviceId());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            if (chat.getType() == 1) {

                if (chat.isIsread()) {

                    chatListViewHolder.setLastMessage(chat.getMessage());

                } else {

                    chatListViewHolder.setLastMessageBold(chat.getMessage());
                }


            } else if (chat.getType() == 2) {

                if (chat.isIsread()) {

                    chatListViewHolder.setLastMessage("Image message");

                } else {

                    chatListViewHolder.setLastMessageBold("Image message");
                }


            } else if (chat.getType() == 3) {

                if (chat.isIsread()) {

                    chatListViewHolder.setLastMessage("Audio message");

                } else {

                    chatListViewHolder.setLastMessageBold("Audio message");
                }

            }

            DateTime senttime = new DateTime(chat.getTimestamp());

            String time = new PrettyTime().format(senttime.toDate());

            chatListViewHolder.setTime(time);

            chatListViewHolder.sethideStatus();

        }


    }
}
