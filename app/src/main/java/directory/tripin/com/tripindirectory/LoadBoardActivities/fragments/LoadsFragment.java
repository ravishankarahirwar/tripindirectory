package directory.tripin.com.tripindirectory.LoadBoardActivities.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import directory.tripin.com.tripindirectory.ChatingActivities.ChatRoomActivity;
import directory.tripin.com.tripindirectory.LoadBoardActivities.LoadDetailsActivity;
import directory.tripin.com.tripindirectory.LoadBoardActivities.LoadPostsActivity;
import directory.tripin.com.tripindirectory.LoadBoardActivities.models.LoadPostPojo;
import directory.tripin.com.tripindirectory.LoadBoardActivities.models.LoadPostViewHolder;
import directory.tripin.com.tripindirectory.LoadBoardActivities.models.QuotePojo;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.activity.PartnerDetailScrollingActivity;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.helper.RecyclerViewAnimator;
import directory.tripin.com.tripindirectory.utils.TextUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoadsFragment extends Fragment {

    public RecyclerView mLoadsList;
    public TextUtils textUtils;
    private FirestoreRecyclerAdapter adapter;
    private RecyclerViewAnimator mAnimator;
    private FirebaseAuth firebaseAuth;
    private String mUid = "";
    private LottieAnimationView lottieAnimationViewLoading;
    private TextView mResultDescription;
    private TextView mSeeAll;
    private TextView mSeeAllBottom;

    public LoadsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_loads, container, false);
        mLoadsList = v.findViewById(R.id.rv_loads);
        mAnimator = new RecyclerViewAnimator(mLoadsList);
        lottieAnimationViewLoading = v.findViewById(R.id.loadingloads);
        mResultDescription = v.findViewById(R.id.textViewResultNumber);
        mSeeAll = v.findViewById(R.id.textViewShowAll);
        mSeeAllBottom = v.findViewById(R.id.textViewShowAllBottom);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        //mLayoutManager.setStackFromEnd(true);
        mLoadsList.setLayoutManager(mLayoutManager);

        mLoadsList.setAdapter(adapter);
        mSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // details Activity
                startActivity(new Intent(getActivity(), LoadPostsActivity.class));
            }
        });

        mSeeAllBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textUtils = new TextUtils();
        firebaseAuth = FirebaseAuth.getInstance();
        mUid = firebaseAuth.getUid();

        final Query query = FirebaseFirestore.getInstance()
                .collection("loads")
//                .orderBy("mPickUpTimeStamp")
//                .whereGreaterThanOrEqualTo("mPickUpTimeStamp",new Date())
                .orderBy("mTimeStamp", Query.Direction.DESCENDING)
                .limit(10);
        FirestoreRecyclerOptions<LoadPostPojo> options = new FirestoreRecyclerOptions.Builder<LoadPostPojo>()
                .setQuery(query, LoadPostPojo.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<LoadPostPojo, LoadPostViewHolder>(options) {
            @Override
            public void onBindViewHolder(final LoadPostViewHolder holder, final int position, final LoadPostPojo loadPostPojo) {

                DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                final String docId = snapshot.getId();


                String DisplayDate = DateFormat.getDateInstance().format(loadPostPojo.getmPickUpTimeStamp());
                String d =  gettimeDiff(loadPostPojo.getmPickUpTimeStamp());
                String dd = "";
                if(d.equals("0")){
                    dd = " (today)";
                }else if(d.equals("1")){
                    dd = " (tomorrow)";
                } else {
                    dd = " ("+d+" days left)";
                }

                holder.mScheduledDate.setText("Scheduled Date : " + DisplayDate );

                if (!loadPostPojo.getmCompanyName().isEmpty()) {
                    holder.mPostTitle.setText(textUtils.toTitleCase(loadPostPojo.getmCompanyName()));
                } else {
                    holder.mPostTitle.setText(loadPostPojo.getmRMN());
                }

                holder.mSource.setText(loadPostPojo.getmSourceCity());
                holder.mDestination.setText(loadPostPojo.getmDestinationCity());
                holder.mDistance.setText(loadPostPojo.getmEstimatedDistance() + "\nkm");

                final String loadProperties = textUtils.toTitleCase(loadPostPojo.getmLoadMaterial())
                        + ", " + loadPostPojo.getmLoadWeight() + "MT";
                holder.mLoadProperties.setText(" " + loadProperties);
                if (loadProperties.length() > 20) {
                    holder.mLoadProperties.setSelected(true);
                }

                final String fleetProperties = textUtils.toTitleCase(loadPostPojo.getmVehicleTypeRequired())
                        + ", " + textUtils.toTitleCase(loadPostPojo.getmBodyTypeRequired())
                        + ", " + loadPostPojo.getmFleetPayLoadRequired() + "MT, "
                        + loadPostPojo.getmFleetLengthRequired() + "Ft";
                holder.mFleetProperties.setText(" " + fleetProperties);
                if (fleetProperties.length() > 20) {
                    holder.mFleetProperties.setSelected(true);
                }

                if (!loadPostPojo.getmPersonalNote().isEmpty()) {
                    Logger.v("personal note visblee");
                    holder.mPersonalNote.setText("\"" + loadPostPojo.getmPersonalNote() + "\"");
                } else {
                    holder.mPersonalNote.setVisibility(View.GONE);
                    Logger.v("personal note gone");

                }

                Date postingDate = loadPostPojo.getmTimeStamp();

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
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        if (!loadPostPojo.getmCompanyName().isEmpty()) {
                            builder.setTitle(loadPostPojo.getmCompanyName());
                        } else {
                            builder.setTitle(loadPostPojo.getmRMN());
                        }
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                // Do something with the selection
                                switch (item) {
                                    case 0: {
                                        Intent intent = new Intent(getActivity(), PartnerDetailScrollingActivity.class);
                                        if(loadPostPojo.getmCompanyName().isEmpty()){
                                            intent.putExtra("cname",loadPostPojo.getmRMN());
                                        }else {
                                            intent.putExtra("cname",loadPostPojo.getmCompanyName());
                                        }
                                        intent.putExtra("uid",loadPostPojo.getmPostersUid());
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
                if (loadPostPojo.getmIntrestedPeopleList() != null) {
                    int likes = 0;
                    for (Boolean b : loadPostPojo.getmIntrestedPeopleList().values()) {
                        if (b) {
                            likes++;
                        }
                    }

                    if (likes == 0) {
                        holder.badgeLike.setText("0");

                    } else {
                        holder.badgeLike.setNumber(likes);

                    }

                    if (loadPostPojo.getmIntrestedPeopleList().get(mUid) != null) {
                        if (loadPostPojo.getmIntrestedPeopleList().get(mUid)) {
                            holder.like.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite));
                            isIntrested = true;
                            Logger.v("heart full");

                        }else {
                            holder.like.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite_border_black_24dp));
                            isIntrested = false;
                            Logger.v("heart empty");
                        }
                        Logger.v("is intrested list not null");
                    }
                } else {
                    holder.badgeLike.setText("0");
                }
                if (loadPostPojo.getmSharedPeopleList() != null) {
                    holder.badgeShare.setNumber(loadPostPojo.getmSharedPeopleList().size());
                } else {
                    holder.badgeShare.setText("0");
                }

                if (loadPostPojo.getmCalledPeopleList() != null) {
                    holder.badgeCall.setNumber(loadPostPojo.getmCalledPeopleList().size());
                } else {
                    holder.badgeCall.setText("0");
                }
                if (loadPostPojo.getmInboxedPeopleList() != null) {
                    holder.badgeInbox.setNumber(loadPostPojo.getmInboxedPeopleList().size());
                } else {
                    holder.badgeInbox.setText("0");
                }

                if (loadPostPojo.getmQuotedPeopleList() != null) {
                    holder.badgeQuote.setNumber(loadPostPojo.getmQuotedPeopleList().size());
                    if(loadPostPojo.getmQuotedPeopleList().get(mUid)!=null){
                        holder.quote.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_rupee_red));
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
                                    .collection("loads")
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
                                    .collection("loads")
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
                        Intent intent = new Intent(getActivity(), LoadDetailsActivity.class);
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
                        shareMesssages(getActivity(),"FLEET REQUIRED",loadPostPojo.getTextToShare());
                        FirebaseFirestore.getInstance()
                                .collection("loads")
                                .document(docId)
                                .update("mSharedPeopleList." + mUid, true)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //done
                                        Logger.v("Shared Load");
                                    }
                                });

                    }
                });

                holder.call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String phoneNO = loadPostPojo.getmRMN();
                        if(phoneNO != null && phoneNO.length() > 0) {
                            callNumber(phoneNO);
                            FirebaseFirestore.getInstance()
                                    .collection("loads")
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
                            Toast.makeText(getContext(), "Sorry!! Mobile no not available", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                holder.quote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final Dialog dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.dialog_add_quotation);
                        final String title = textUtils.toTitleCase(loadPostPojo.getmSourceCity())+" To "+textUtils.toTitleCase(loadPostPojo.getmDestinationCity());
                        dialog.setTitle(title+ " ...");


                        final TextView quote = (TextView) dialog.findViewById(R.id.tv_quote);
                        TextView cancel = (TextView) dialog.findViewById(R.id.tv_cancelquote);
                        final EditText amount = dialog.findViewById(R.id.editTextAmount);
                        final EditText comment = dialog.findViewById(R.id.editTextComment);

                        FirebaseFirestore.getInstance()
                                .collection("loads")
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
                                    Logger.v(firebaseAuth.getCurrentUser().getPhoneNumber()+" RMN");
                                    final QuotePojo quotePojo = new QuotePojo(amount.getText().toString().trim(),
                                            comment.getText().toString().trim()+"",
                                            mUid,docId,firebaseAuth.getCurrentUser().getPhoneNumber(),loadPostPojo.getmFcmToken());
                                    FirebaseFirestore.getInstance()
                                            .collection("loads")
                                            .document(docId)
                                            .update("mQuotedPeopleList." + mUid, true)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    //add to collection
                                                    FirebaseFirestore.getInstance()
                                                            .collection("loads")
                                                            .document(docId).collection("mQuotesCollection")
                                                            .document(mUid).set(quotePojo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            dialog.dismiss();
                                                            Logger.v("QuoteAdded");
                                                            Toast.makeText(getActivity(),"Quoted Successfully!",Toast.LENGTH_LONG).show();

                                                        }
                                                    });

                                                }
                                            });

                                }else {
                                    Toast.makeText(getActivity(),"No Amount Added!",Toast.LENGTH_LONG).show();
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

                if(!(loadPostPojo.getmPostersUid().equalsIgnoreCase(FirebaseAuth.getInstance().getUid()))) {

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
                            Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                            intent.putExtra("imsg", loadPostPojo.getTextToInitateChat());
                            intent.putExtra("ormn", loadPostPojo.getmRMN());
                            intent.putExtra("ouid", loadPostPojo.getmPostersUid());
                            startActivity(intent);
                        }
                    });

                }else {
                    holder.mInboxLayout.setVisibility(RelativeLayout.GONE);
                }

                Logger.v("onBind: " + position);
                //mAnimator.onBindViewHolder(holder.itemView, position);


            }

            @Override
            public LoadPostViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_posted_load, group, false);
                mAnimator.onCreateViewHolder(view);
                Logger.v("onCreateViewHolder:" + i);

                return new LoadPostViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                mSeeAllBottom.setVisibility(View.VISIBLE);
                lottieAnimationViewLoading.setVisibility(View.GONE);
                mResultDescription.setText("Showing Top "+adapter.getItemCount()+ " Recently Available Loads");

            }
        };


    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.v("onStop Loads");
        adapter.stopListening();
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

            if (diffInSeconds == 0) {
                return "Realtime!";
            }

            if (diffInSeconds < 60) {
                diff = "" + diffInSeconds + " sec ago";
            } else if (diffInMinutes < 60) {
                diff = "" + diffInMinutes + " min ago";
            } else if (diffInHours < 24) {
                diff = "" + diffInHours + " hrs ago";
            } else {

                long daysago = duration / (1000 * 60 * 60 * 24);
                diff = "" + daysago + " days ago";
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
        }
        catch (ActivityNotFoundException exception) {
            Toast.makeText(context, "No application found for send Email" , Toast.LENGTH_LONG).show();
        }
    }

    private void callNumber(String number) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + Uri.encode(number.trim())));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(callIntent);
    }
    private void hideSoftKey() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
