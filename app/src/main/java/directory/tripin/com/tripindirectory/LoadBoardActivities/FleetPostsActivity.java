package directory.tripin.com.tripindirectory.LoadBoardActivities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import directory.tripin.com.tripindirectory.ChatingActivities.ChatRoomActivity;
import directory.tripin.com.tripindirectory.LoadBoardActivities.models.FleetPostPojo;
import directory.tripin.com.tripindirectory.LoadBoardActivities.models.FleetPostViewHolder;
import directory.tripin.com.tripindirectory.LoadBoardActivities.models.LoadPostPojo;
import directory.tripin.com.tripindirectory.LoadBoardActivities.models.LoadPostViewHolder;
import directory.tripin.com.tripindirectory.LoadBoardActivities.models.QuotePojo;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.activity.PartnerDetailScrollingActivity;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.utils.TextUtils;

public class FleetPostsActivity extends AppCompatActivity {

    public RecyclerView mFleetsList;
    public TextUtils textUtils;
    FirestoreRecyclerAdapter adapter;
    FirebaseAuth mAuth;
    private LottieAnimationView lottieAnimationViewLoading;
    private Activity activity;
    private FirebaseAuth firebaseAuth;

    private String mUid = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fleet_posts);
        lottieAnimationViewLoading = findViewById(R.id.loadingloads);
        mFleetsList = findViewById(R.id.rv_fleets);
        mFleetsList.setLayoutManager(new LinearLayoutManager(this));
        activity = this;
        textUtils = new TextUtils();
        firebaseAuth = FirebaseAuth.getInstance();
        mUid = firebaseAuth.getUid();

        setAdapter();
        mFleetsList.setAdapter(adapter);

    }

    private void setAdapter() {


        Query query = FirebaseFirestore.getInstance()
                .collection("fleets")
                .orderBy("mTimeStamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<FleetPostPojo> options = new FirestoreRecyclerOptions.Builder<FleetPostPojo>()
                .setQuery(query, FleetPostPojo.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<FleetPostPojo, FleetPostViewHolder>(options) {
            @Override
            public void onBindViewHolder(final FleetPostViewHolder holder, final int position, final FleetPostPojo fleetPostPojo) {

                DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                final String docId = snapshot.getId();


                String DisplayDate = DateFormat.getDateInstance().format(fleetPostPojo.getmPickUpTimeStamp());
                String d =  gettimeDiff(fleetPostPojo.getmPickUpTimeStamp());
                String dd = "";
                if(d.equals("0")){
                    dd = " (today)";
                }else if(d.equals("1")){
                    dd = " (tomorrow)";
                }else if(d.length()>5){
                    dd = " (expired!)";
                }else {
                    dd = " ("+d+" days left)";
                }

                holder.mScheduledDate.setText("Scheduled Date : " + DisplayDate + dd);

                if (!fleetPostPojo.getmCompanyName().isEmpty()) {
                    holder.mPostTitle.setText(textUtils.toTitleCase(fleetPostPojo.getmCompanyName()));
                } else {
                    holder.mPostTitle.setText(fleetPostPojo.getmRMN());
                }

                holder.mSource.setText(fleetPostPojo.getmSourceCity());
                holder.mDestination.setText(fleetPostPojo.getmDestinationCity());
                holder.mDistance.setText(fleetPostPojo.getmEstimatedDistance() + "\nkm");

                final String loadProperties = fleetPostPojo.getmVehicleNumber();
                holder.mLoadProperties.setText(" " + loadProperties);
                if (loadProperties.length() > 20) {
                    holder.mLoadProperties.setSelected(true);
                }

                String fleetProperties = textUtils.toTitleCase(fleetPostPojo.getmVehicleType())
                        + ", " + textUtils.toTitleCase(fleetPostPojo.getmBodyType())
                        + ", " + textUtils.toTitleCase(fleetPostPojo.getmFleetPayLoad()) + "MT, "
                        + textUtils.toTitleCase(fleetPostPojo.getmFleetLength()) + "Ft";
                holder.mFleetProperties.setText(" " + fleetProperties);
                if (fleetProperties.length() > 20) {
                    holder.mFleetProperties.setSelected(true);
                }

                if (!fleetPostPojo.getmPersonalNote().isEmpty()) {
                    Logger.v("personal note visblee");
                    holder.mPersonalNote.setText("\"" + fleetPostPojo.getmPersonalNote() + "\"");
                } else {
                    holder.mPersonalNote.setVisibility(View.GONE);
                    Logger.v("personal note gone");

                }

                Date postingDate = fleetPostPojo.getmTimeStamp();

                holder.mPostSubTitle.setText("Posted " + gettimeDiff(postingDate) + " . Need Fleet");

                if (position == 0) {
                    holder.expand();
                }

                holder.mDetailsFull.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //holder.expand();
                    }
                });

                holder.mOptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final CharSequence[] items = {
                                "Visit Company Details", "See on Map", "Report This Post", "Cancel"
                        };

                        final AlertDialog alert;
                        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        if (!fleetPostPojo.getmCompanyName().isEmpty()) {
                            builder.setTitle(fleetPostPojo.getmCompanyName());
                        } else {
                            builder.setTitle(fleetPostPojo.getmRMN());
                        }
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                // Do something with the selection
                                switch (item) {
                                    case 0: {
                                        Intent intent = new Intent(FleetPostsActivity.this, PartnerDetailScrollingActivity.class);
                                        if(fleetPostPojo.getmCompanyName().isEmpty()){
                                            intent.putExtra("cname",fleetPostPojo.getmRMN());
                                        }else {
                                            intent.putExtra("cname",fleetPostPojo.getmCompanyName());
                                        }
                                        intent.putExtra("uid",fleetPostPojo.getmPostersUid());
                                        startActivity(intent);
                                        break;
                                    }
                                    case 1: {
                                        break;
                                    }
                                    case 2: {
                                        break;
                                    }

                                }
                            }
                        });
                        alert = builder.create();
                        alert.show();
                    }
                });

                //actions

                //set is intrested
                boolean isIntrested = false;
                if (fleetPostPojo.getmIntrestedPeopleList() != null) {
                    int likes = 0;
                    for (Boolean b : fleetPostPojo.getmIntrestedPeopleList().values()) {
                        if (b) {
                            likes++;
                        }
                    }

                    if (likes == 0) {
                        holder.badgeLike.setText("0");

                    } else {
                        holder.badgeLike.setNumber(likes);

                    }

                    if (fleetPostPojo.getmIntrestedPeopleList().get(mUid) != null) {
                        if (fleetPostPojo.getmIntrestedPeopleList().get(mUid)) {
                            holder.like.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_favorite));
                            isIntrested = true;
                            Logger.v("heart full");

                        }else {
                            holder.like.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_favorite_border_black_24dp));
                            isIntrested = false;
                            Logger.v("heart empty");
                        }
                        Logger.v("is intrested list not null");
                    }
                } else {
                    holder.badgeLike.setText("0");
                }
                if (fleetPostPojo.getmSharedPeopleList() != null) {
                    holder.badgeShare.setNumber(fleetPostPojo.getmSharedPeopleList().size());
                } else {
                    holder.badgeShare.setText("0");
                }

                if (fleetPostPojo.getmCalledPeopleList() != null) {
                    holder.badgeCall.setNumber(fleetPostPojo.getmCalledPeopleList().size());
                } else {
                    holder.badgeCall.setText("0");
                }
                if (fleetPostPojo.getmInboxedPeopleList() != null) {
                    holder.badgeInbox.setNumber(fleetPostPojo.getmInboxedPeopleList().size());
                } else {
                    holder.badgeInbox.setText("0");
                }

                if (fleetPostPojo.getmQuotedPeopleList() != null) {
                    holder.badgeQuote.setNumber(fleetPostPojo.getmQuotedPeopleList().size());
                    if(fleetPostPojo.getmQuotedPeopleList().get(mUid)!=null){
                        holder.quote.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_rupee_red));
                    }
                } else {
                    holder.badgeQuote.setText("0");
                }


                final boolean finalIsIntrested = isIntrested;
                holder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (finalIsIntrested) {
                            FirebaseFirestore.getInstance()
                                    .collection("fleets")
                                    .document(docId)
                                    .update("mIntrestedPeopleList." + mUid, false)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //done
                                            Logger.v("Unlike clicked");
                                        }
                                    });
                        } else {
                            FirebaseFirestore.getInstance()
                                    .collection("fleets")
                                    .document(docId)
                                    .update("mIntrestedPeopleList." + mUid, true)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //done
                                            Logger.v("like clicked");
                                        }
                                    });
                        }

                    }
                });

                holder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FleetPostsActivity.this, FleetDetailsActivity.class);
                        intent.putExtra("docId",docId);
                        startActivity(intent);
                    }
                });
                holder.badgeComment.setText("..");
                snapshot.getReference().collection("mCommentsCollection").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        Logger.v("onSuccess Comment Collection: " + position);
                        if (documentSnapshots.isEmpty()){
                            holder.badgeComment.setText("0");
                        }else {
                            holder.badgeComment.setText(documentSnapshots.size()+"");

                        }
                    }
                });

                holder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shareMesssages(activity,"LOAD REQUIRED",fleetPostPojo.getTextToShare());
                        FirebaseFirestore.getInstance()
                                .collection("fleets")
                                .document(docId)
                                .update("mSharedPeopleList." + mUid, true)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //done
                                        Logger.v("Shared Fleet");
                                    }
                                });

                    }
                });

                holder.call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String phoneNO = fleetPostPojo.getmRMN();
                        if(phoneNO != null && phoneNO.length() > 0) {
                            callNumber(phoneNO);
                            FirebaseFirestore.getInstance()
                                    .collection("fleets")
                                    .document(docId)
                                    .update("mCalledPeopleList." + mUid, true)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //done
                                            Logger.v("Called Poster");
                                        }
                                    });
                        } else {
                            Toast.makeText(activity, "Sorry!! Mobile no not available", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



                holder.quote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final Dialog dialog = new Dialog(activity);
                        dialog.setContentView(R.layout.dialog_add_quotation);
                        final String title = textUtils.toTitleCase(fleetPostPojo.getmSourceCity())+" To "+textUtils.toTitleCase(fleetPostPojo.getmDestinationCity());
                        dialog.setTitle(title+ " ...");


                        final TextView quote = dialog.findViewById(R.id.tv_quote);
                        TextView cancel = dialog.findViewById(R.id.tv_cancelquote);
                        final EditText amount = dialog.findViewById(R.id.editTextAmount);
                        final EditText comment = dialog.findViewById(R.id.editTextComment);

                        FirebaseFirestore.getInstance()
                                .collection("fleets")
                                .document(docId).collection("mQuotesCollection")
                                .document(mUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
                                    dialog.setTitle(title+".");
                                    QuotePojo quotePojo = documentSnapshot.toObject(QuotePojo.class);
                                    amount.setText(quotePojo.getmQuoteAmount());
                                    if(!quotePojo.getmComment().isEmpty()){
                                        comment.setText(quotePojo.getmComment());
                                    }
                                }else {
                                    dialog.setTitle(title+ " :");
                                }
                            }
                        });

                        quote.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(!amount.getText().toString().isEmpty()){

                                    quote.setText("Sending...");
                                    final QuotePojo quotePojo = new QuotePojo(amount.getText().toString().trim(),
                                            comment.getText().toString().trim()+"",
                                            mUid,docId,firebaseAuth.getCurrentUser().getPhoneNumber(),fleetPostPojo.getmFcmToken());
                                    FirebaseFirestore.getInstance()
                                            .collection("fleets")
                                            .document(docId)
                                            .update("mQuotedPeopleList." + mUid, true)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    //add to collection
                                                    FirebaseFirestore.getInstance()
                                                            .collection("fleets")
                                                            .document(docId).collection("mQuotesCollection")
                                                            .document(mUid).set(quotePojo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            dialog.dismiss();
                                                            Logger.v("QuoteAdded");
                                                            Toast.makeText(activity,"Quoted Successfully!",Toast.LENGTH_LONG).show();

                                                        }
                                                    });

                                                }
                                            });

                                }else {
                                    Toast.makeText(activity,"No Amount Added!",Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });


                        dialog.show();

                    }
                });

                holder.inbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseFirestore.getInstance()
                                .collection("loads")
                                .document(docId)
                                .update("mInboxedPeopleList." + mUid, true)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //done
                                        Logger.v("mInboxedPeopleList Modified");
                                    }
                                });
                        Intent intent = new Intent(FleetPostsActivity.this, ChatRoomActivity.class);
                        intent.putExtra("imsg",fleetPostPojo.getTextToInitateChat());
                        intent.putExtra("ormn",fleetPostPojo.getmRMN());
                        intent.putExtra("ouid",fleetPostPojo.getmPostersUid());
                        startActivity(intent);
                    }
                });


                Logger.v("onBind: " + position);
                //mAnimator.onBindViewHolder(holder.itemView, position);


            }

            @Override
            public FleetPostViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_posted_fleet, group, false);
                //mAnimator.onCreateViewHolder(view);
                Logger.v("onCreateViewHolder:" + i);

                return new FleetPostViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                lottieAnimationViewLoading.setVisibility(View.GONE);

            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chatheads, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_notifications: {
                break;
            }
            case R.id.action_comments: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    public String gettimeDiff(Date startDate) {

        String diff = "";

        if (startDate != null) {

            Date endDate = new Date();

            long duration = endDate.getTime() - startDate.getTime();
            if (duration < 0) {
                return Math.abs(duration) / (1000 * 60 * 60 * 24) + "";
            }
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

    private void shareMesssages(Context context, String subject, String body) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            shareIntent.putExtra(Intent.EXTRA_TEXT, body);
            context.startActivity(Intent.createChooser(shareIntent, "Share via"));
        } catch (ActivityNotFoundException exception) {
            Toast.makeText(context, "No application found for send Email", Toast.LENGTH_LONG).show();
        }
    }

    private void callNumber(String number) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + Uri.encode(number.trim())));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(callIntent);
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
