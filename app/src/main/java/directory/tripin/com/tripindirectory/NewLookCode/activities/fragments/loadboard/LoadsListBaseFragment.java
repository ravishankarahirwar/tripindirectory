package directory.tripin.com.tripindirectory.NewLookCode.activities.fragments.loadboard;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import directory.tripin.com.tripindirectory.ChatingActivities.ChatRoomActivity;
import directory.tripin.com.tripindirectory.Messaging.Activity.ChatActivity;
import directory.tripin.com.tripindirectory.NewLookCode.activities.fragments.loadboard.models.Post;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.forum.models.User;
import directory.tripin.com.tripindirectory.forum.viewholder.PostViewHolder;
import directory.tripin.com.tripindirectory.helper.CircleTransform;
import directory.tripin.com.tripindirectory.helper.Logger;


public abstract class LoadsListBaseFragment extends Fragment {

    private static final String TAG = "PostListFragment";
    private boolean isMyPost;
    LottieAnimationView loading;
    private TextView noItems;

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<Post, LoadPostViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private FirebaseAuth mAuth;

    public LoadsListBaseFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_posts, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Initialize Database
        mAuth = FirebaseAuth.getInstance();

        // [END create_database_reference]

        mRecycler = rootView.findViewById(R.id.messages_list);
        loading = rootView.findViewById(R.id.loading);
        noItems = rootView.findViewById(R.id.noitems);
       // mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        Log.v("PostListFragment","onActivityCreated");

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Post, LoadPostViewHolder>(Post.class, R.layout.item_post,
                LoadPostViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final LoadPostViewHolder viewHolder, final Post model, final int position) {
                final DatabaseReference postRef = getRef(position);
                Log.v("PostListFragment","popolate "+position);

                if(model.getmPhotoUrl()!=null){
                    if(!model.getmPhotoUrl().isEmpty()){
                        Picasso.with(getActivity())
                                .load(model.getmPhotoUrl())
                                .placeholder(ContextCompat.getDrawable(getContext()
                                        , R.mipmap.ic_launcher_round))
                                .transform(new CircleTransform())
                                .fit()
                                .into(viewHolder.mThumbnail, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Logger.v("image set: " + position);
                                    }

                                    @Override
                                    public void onError() {
                                        Logger.v("image error: " + position);
                                    }

                                });
                    }else {
                        Logger.v("image url empty: " + position);
                    }
                }else {
                    Logger.v("image url null: " + position);
                }


                // Set click listener for the whole post view
                final String postKey = postRef.getKey();



                viewHolder.chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(model.mFuid!=null){
                            Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                            intent.putExtra("imsg", model.toString());
                            intent.putExtra("ormn", model.getmContactNo());
                            intent.putExtra("ouid", model.getmUid());
                            intent.putExtra("ofuid", model.getmFuid());
                            startActivity(intent);
                        }else {
                            Toast.makeText(getContext(),"Cant Chat",Toast.LENGTH_LONG).show();
                        }

                    }
                });

                if(model.getmUid().equalsIgnoreCase(mAuth.getUid())) {
                    viewHolder.delete.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.delete.setVisibility(View.GONE);
                }

                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        if(postRef != null) {
                                            postRef.removeValue();
                                        }
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Delete Post");
                        builder.setMessage("Are you sure? You want to delete this post.").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                    }
                });



                viewHolder.sharePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (model.mFindOrPost == 1) {
                            shareMesssages(getActivity(), "Need Truck", model.toString());

                        }else if (model.mFindOrPost == 2) {
                            shareMesssages(getActivity(), "Need LOAD", model.toString());
                        }
                    }
                });

                viewHolder.call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String phoneNO = model.getmContactNo();
                        if(phoneNO != null && phoneNO.length() > 0) {
                            callNumber(phoneNO);
                        } else {
                            Toast.makeText(getContext(), "Sorry!! Mobile no not available", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



//                // Determine if the current user has liked this post and set UI accordingly
//                if (model.stars.containsKey(getUid())) {
//                    viewHolder.starView.setImageResource(R.drawable.ic_favorite);
//                } else {
//                    viewHolder.starView.setImageResource(R.drawable.ic_favorite_border_black_24dp);
//                }

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        // Need to write to both places the post is stored
                        DatabaseReference globalPostRef = mDatabase.child("posts").child(postRef.getKey());
                        DatabaseReference userPostRef = mDatabase.child("user-posts").child(model.mUid).child(postRef.getKey());

                        // Run two transactions
                        onStarClicked(globalPostRef);
                        onStarClicked(userPostRef);
                    }
                });
            }

            @Override
            public LoadPostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.v("PostListFragment","onCreateViewholder");

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_new_load_post, parent, false);
                return new LoadPostViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                Log.v("PostListFragment","onDataChanged");
                loading.setVisibility(View.GONE);

                if(getItemCount()==0){
                    noItems.setVisibility(View.VISIBLE);
                }else {
                    noItems.setVisibility(View.GONE);
                }
                super.onDataChanged();
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    // [START post_stars_transaction]
    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    p.starCount = p.starCount - 1;
                    p.stars.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1;
                    p.stars.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }
    // [END post_stars_transaction]

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

    private void shareMesssages(Context context, String subject, String body) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            shareIntent.putExtra(Intent.EXTRA_TEXT, body);
            context.startActivity(Intent.createChooser(shareIntent, "Share via"));
        }
        catch (ActivityNotFoundException exception) {
            Toast.makeText(context, "No application found for send Email" , Toast.LENGTH_LONG).show();
        }
    }

    private void copyMessage(Context context, String content) {
        ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Hindi Message",content);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private void callNumber(String number) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + Uri.encode(number.trim())));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(callIntent);
    }
}
