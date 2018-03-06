package directory.tripin.com.tripindirectory.LoadBoardActivities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import directory.tripin.com.tripindirectory.ChatingActivities.ChatRoomActivity;
import directory.tripin.com.tripindirectory.LoadBoardActivities.models.CommentPojo;
import directory.tripin.com.tripindirectory.LoadBoardActivities.models.CommentsViewHolder;
import directory.tripin.com.tripindirectory.LoadBoardActivities.models.LoadPostPojo;
import directory.tripin.com.tripindirectory.LoadBoardActivities.models.LoadPostViewHolder;
import directory.tripin.com.tripindirectory.LoadBoardActivities.models.QuotePojo;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.activity.PartnerDetailScrollingActivity;
import directory.tripin.com.tripindirectory.helper.CircleTransform;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.helper.RecyclerViewAnimator;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;
import directory.tripin.com.tripindirectory.utils.TextUtils;

public class LoadDetailsActivity extends AppCompatActivity {

    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    private String docId = "";
    public RecyclerView mLoadsList;
    public TextUtils textUtils;
    FirestoreRecyclerAdapter adapter;
    FirestoreRecyclerAdapter adapterComments;
    public RecyclerView mCommentsList;
    FirebaseAuth mAuth;
    PartnerInfoPojo partnerInfoPojo;
    NestedScrollView nestedScrollView;
    boolean isScrolledDown = false;
    private LottieAnimationView lottieAnimationViewLoading;


    EditText mComment;
    ImageButton mCommnetAction;
    LinearLayout mLinCommentsInfo;
    TextView mSeeAllComments;


    private RecyclerViewAnimator mAnimator;
    private FirebaseAuth firebaseAuth;
    private String mUid = "";
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_load_details);
        mLoadsList = findViewById(R.id.rv_loads);
        mCommentsList = findViewById(R.id.rv_comments);
        mComment = findViewById(R.id.et_comment);
        mCommnetAction = findViewById(R.id.imageButtonCommentAction);
        nestedScrollView = findViewById(R.id.scroll_details);
        mLinCommentsInfo = findViewById(R.id.ll_commetdiscription);
        mSeeAllComments = findViewById(R.id.textViewShowAllComeents);
        lottieAnimationViewLoading = findViewById(R.id.animation_view);

        textUtils = new TextUtils();
        firebaseAuth = FirebaseAuth.getInstance();
        mUid = firebaseAuth.getUid();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            docId = bundle.getString("docId");

            Logger.v("DocId: " + docId);
            //set details card adapter;
            getUserDetails();
            setAdapter();
            setCommentsAdapter();
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            //mLayoutManager.setStackFromEnd(true);
            mLoadsList.setLayoutManager(mLayoutManager);
            mLoadsList.setAdapter(adapter);

            LinearLayoutManager mLayoutManagerComments = new LinearLayoutManager(this);
            mLayoutManagerComments.setReverseLayout(true);
            mCommentsList.setLayoutManager(mLayoutManagerComments);
            mCommentsList.setAdapter(adapterComments);

        } else {
            finish();
        }

        mComment.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                Logger.v("on layout change: " + i + i1 + i2 + i3 + i4 + i5 + i6 + i7);
                nestedScrollView.post(new Runnable() {
                    public void run() {
                        if (isScrolledDown) {
                            nestedScrollView.fullScroll(View.FOCUS_DOWN);
                            Logger.v("scroll down lc");
                        }
                    }
                });
            }
        });


        mComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Logger.v("before");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0) {
                    mCommnetAction.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_menu_send));
                } else {
                    mCommnetAction.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_mic_black_24dp));

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mSeeAllComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start All Comments Activity
            }
        });
        mCommnetAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mComment.getText().toString().isEmpty()) {
                    //voice
                    startVoiceRecognitionActivity();
                } else {
                    //compose and send
                    if (partnerInfoPojo != null) {
                        CommentPojo commentPojo;
                        commentPojo = new CommentPojo(mComment.getText().toString().trim(),
                                partnerInfoPojo.getmCompanyName(),
                                mAuth.getCurrentUser().getPhoneNumber(),
                                partnerInfoPojo.getmFcmToken(), docId, mUid);
                        if (partnerInfoPojo.getmImagesUrl() != null) {
                            commentPojo.setmImagesUrl(partnerInfoPojo.getmImagesUrl());
                        }
                        mComment.setText("");

                        FirebaseFirestore.getInstance()
                                .collection("loads")
                                .document(docId)
                                .collection("mCommentsCollection")
                                .add(commentPojo)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        //comment added
                                        //subscribe to topic
                                        FirebaseMessaging.getInstance().subscribeToTopic(docId);
                                    }
                                });

                    } else {
                        //cant commnet
                    }
                }
            }
        });


    }

    private void hideSoftKey() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void getUserDetails() {
        FirebaseFirestore.getInstance().collection("partners").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    partnerInfoPojo = documentSnapshot.toObject(PartnerInfoPojo.class);

                }
            }
        });
    }

    private void setCommentsAdapter() {
        Query queryComments;
        queryComments = FirebaseFirestore.getInstance()
                .collection("loads").document(docId).collection("mCommentsCollection").orderBy("mTimeStamp", Query.Direction.DESCENDING).limit(10);
        FirestoreRecyclerOptions<CommentPojo> options = new FirestoreRecyclerOptions.Builder<CommentPojo>()
                .setQuery(queryComments, CommentPojo.class)
                .build();

        adapterComments = new FirestoreRecyclerAdapter<CommentPojo, CommentsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(final CommentsViewHolder holder, final int position, final CommentPojo model) {
                holder.mComment.setText(model.getmCommentText());
                if (model.getmCompanyName().isEmpty()) {
                    holder.mTitle.setText(model.getmRMN());
                } else {
                    holder.mTitle.setText(textUtils.toTitleCase(model.getmCompanyName()));
                }
                holder.mTimeAgo.setText(gettimeDiff(model.getmTimeStamp()));
                if(model.getmImagesUrl()!=null){
                    Picasso.with(getApplicationContext())
                            .load(model.getmImagesUrl().get(2))
                            .placeholder(ContextCompat.getDrawable(getApplicationContext()
                                    , R.drawable.ic_insert_comment_black_24dp))
                            .transform(new CircleTransform())
                            .fit()
                            .into(holder.mThumbNail, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Logger.v("image set: " + position);
                                }

                                @Override
                                public void onError() {
                                    Logger.v("image error: " + position);
                                    Logger.v("image error: " + position);
                                    if (model.getmUid() != null) {
                                        FirebaseFirestore.getInstance().collection("partners").document(model.getmUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    PartnerInfoPojo partnerInfoPojo = documentSnapshot.toObject(PartnerInfoPojo.class);
                                                    if (partnerInfoPojo.getmImagesUrl() != null) {
                                                        if (partnerInfoPojo.getmImagesUrl().get(2) != null) {
                                                            Picasso.with(getApplicationContext())
                                                                    .load(partnerInfoPojo.getmImagesUrl().get(2))
                                                                    .placeholder(ContextCompat.getDrawable(getApplicationContext()
                                                                            , R.drawable.ic_insert_comment_black_24dp))
                                                                    .transform(new CircleTransform())
                                                                    .fit()
                                                                    .into(holder.mThumbNail, new Callback() {
                                                                        @Override
                                                                        public void onSuccess() {
                                                                            Logger.v("Feched from original doc");
                                                                        }

                                                                        @Override
                                                                        public void onError() {
                                                                            Logger.v("user have no image");
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }
                                            }
                                        });


                                    }
                                }

                            });

                }


                holder.mCommentBox.setOnClickListener(new View.OnClickListener()

                {
                    @Override
                    public void onClick(View view) {
                        final CharSequence[] items = {
                                "Inbox", "Call", "Report", "Cancel"
                        };

                        final AlertDialog alert;
                        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        if (!model.getmCompanyName().isEmpty()) {
                            builder.setTitle(textUtils.toTitleCase(model.getmCompanyName()));
                        } else {
                            builder.setTitle(model.getmRMN());
                        }
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                // Do something with the selection
                                switch (item) {
                                    case 0: {
                                        Intent intent = new Intent(LoadDetailsActivity.this, ChatRoomActivity.class);
                                        intent.putExtra("imsg","Texting after watching your comment :\n"+
                                                ">>Comment: "+model.getmCommentText());
                                        intent.putExtra("ormn",model.getmRMN());
                                        intent.putExtra("ouid",model.getmUid());
                                        startActivity(intent);
                                        break;
                                    }
                                    case 1: {
                                        callNumber(model.getmRMN());
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

            }

            @Override
            public void onDataChanged() {
                if (adapterComments.getItemCount() == 10) {
                    mLinCommentsInfo.setVisibility(View.VISIBLE);
                }
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nestedScrollView.post(new Runnable() {
                            public void run() {
                                nestedScrollView.fullScroll(View.FOCUS_DOWN);
                                Logger.v("scroll down 3sec");
                                isScrolledDown = true;
                            }
                        });
                    }
                }, 1000);

                super.onDataChanged();
            }

            @Override
            public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_comment_loadboard, parent, false);

                return new CommentsViewHolder(view);
            }
        }

        ;

    }

    private void setAdapter() {


        Query query = FirebaseFirestore.getInstance()
                .collection("loads")
                .whereEqualTo("mDocId", docId);
        FirestoreRecyclerOptions<LoadPostPojo> options = new FirestoreRecyclerOptions.Builder<LoadPostPojo>()
                .setQuery(query, LoadPostPojo.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<LoadPostPojo, LoadPostViewHolder>(options) {
            @Override
            public void onBindViewHolder(final LoadPostViewHolder holder, final int position, final LoadPostPojo loadPostPojo) {

                DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                final String docId = snapshot.getId();


                String DisplayDate = DateFormat.getDateInstance().format(loadPostPojo.getmPickUpTimeStamp());
                String d = gettimeDiff(loadPostPojo.getmPickUpTimeStamp());
                String dd = "";
                if (d.equals("0")) {
                    dd = " (today)";
                } else if (d.equals("1")) {
                    dd = " (tomorrow)";
                } else if (d.length() > 3) {
                    dd = " (expired!)";
                } else {
                    dd = " (" + d + " days left)";
                }

                holder.mScheduledDate.setText("Scheduled Date : " + DisplayDate + dd);

                if (!loadPostPojo.getmCompanyName().isEmpty()) {
                    holder.mPostTitle.setText(textUtils.toTitleCase(loadPostPojo.getmCompanyName()));
                } else {
                    holder.mPostTitle.setText(loadPostPojo.getmRMN());
                }

                holder.mSource.setText(loadPostPojo.getmSourceCity());
                holder.mDestination.setText(loadPostPojo.getmDestinationCity());
                holder.mDistance.setText(loadPostPojo.getmEstimatedDistance() + "\nkm");

                final String loadProperties = textUtils.toTitleCase(loadPostPojo.getmLoadMaterial())
                        + ", " + textUtils.toTitleCase(loadPostPojo.getmLoadWeight()) + "MT";
                holder.mLoadProperties.setText(" " + loadProperties);
                if (loadProperties.length() > 20) {
                    holder.mLoadProperties.setSelected(true);
                }

                String fleetProperties = textUtils.toTitleCase(loadPostPojo.getmVehicleTypeRequired())
                        + ", " + textUtils.toTitleCase(loadPostPojo.getmBodyTypeRequired())
                        + ", " + textUtils.toTitleCase(loadPostPojo.getmFleetPayLoadRequired()) + "MT, "
                        + textUtils.toTitleCase(loadPostPojo.getmFleetLengthRequired()) + "Ft";
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
                        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
                                        Intent intent = new Intent(LoadDetailsActivity.this, PartnerDetailScrollingActivity.class);
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
                            holder.like.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite));
                            isIntrested = true;
                            Logger.v("heart full");

                        } else {
                            holder.like.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_border_black_24dp));
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
                    if (loadPostPojo.getmQuotedPeopleList().get(mUid)!=null) {
                        holder.quote.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_rupee_red));
                    }
                } else {
                    holder.badgeQuote.setText("0");
                }


                final boolean finalIsIntrested = isIntrested;
                holder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isScrolledDown = false;
                        if (finalIsIntrested) {
                            FirebaseFirestore.getInstance()
                                    .collection("loads")
                                    .document(docId)
                                    .update("mIntrestedPeopleList." + mUid, false)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //done
                                            isScrolledDown = true;
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
                                            isScrolledDown = true;
                                            Logger.v("like clicked");
                                        }
                                    });
                        }

                    }
                });

                holder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nestedScrollView.fullScroll(NestedScrollView.FOCUS_DOWN);
                    }
                });
                holder.badgeComment.setText("..");
                snapshot.getReference().collection("mCommentsCollection").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        Logger.v("onSuccess Comment Collection: " + position);
                        if (documentSnapshots.isEmpty()) {
                            holder.badgeComment.setText("0");
                        } else {
                            holder.badgeComment.setText(documentSnapshots.size() + "");

                        }
                    }
                });

                holder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shareMesssages(activity, "FLEET REQUIRED", loadPostPojo.getTextToShare());
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
                        if (phoneNO != null && phoneNO.length() > 0) {
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
                            Toast.makeText(getApplicationContext(), "Sorry!! Mobile no not available", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                holder.quote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final Dialog dialog = new Dialog(activity);
                        dialog.setContentView(R.layout.dialog_add_quotation);
                        final String title = textUtils.toTitleCase(loadPostPojo.getmSourceCity()) + " To " + textUtils.toTitleCase(loadPostPojo.getmDestinationCity());
                        dialog.setTitle(title + " ...");


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
                                if (documentSnapshot.exists()) {
                                    dialog.setTitle(title + ".");
                                    QuotePojo quotePojo = documentSnapshot.toObject(QuotePojo.class);
                                    amount.setText(quotePojo.getmQuoteAmount());
                                    if (!quotePojo.getmComment().isEmpty()) {
                                        comment.setText(quotePojo.getmComment());
                                    }
                                } else {
                                    dialog.setTitle(title + " :");
                                }
                            }
                        });

                        amount.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                quote.setText("Quote "+charSequence.toString().trim()+"₹");

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

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
                        Intent intent = new Intent(LoadDetailsActivity.this, ChatRoomActivity.class);
                        intent.putExtra("imsg",loadPostPojo.getTextToInitateChat());
                        intent.putExtra("ormn",loadPostPojo.getmRMN());
                        intent.putExtra("ouid",loadPostPojo.getmPostersUid());
                        startActivity(intent);
                    }
                });


            }

            @Override
            public LoadPostViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_posted_load, group, false);
                //mAnimator.onCreateViewHolder(view);
                Logger.v("onCreateViewHolder:" + i);

                return new LoadPostViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                lottieAnimationViewLoading.setVisibility(View.GONE);
                super.onDataChanged();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        adapterComments.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        adapterComments.stopListening();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_load_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_notifications: {
                final CharSequence[] items = {
                        "Turn Off Notifications", "Turn On Notifications", "Cancel"
                };

                final AlertDialog alert;
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Notification For This Post");

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        switch (item) {
                            case 0: {

                                FirebaseMessaging.getInstance().unsubscribeFromTopic(docId);
                                Toast.makeText(getApplicationContext(),"Unsubscribed!",Toast.LENGTH_LONG).show();
                                //turn off

                                break;
                            }
                            case 1: {

                                FirebaseMessaging.getInstance().subscribeToTopic(docId);
                                Toast.makeText(getApplicationContext(),"Subscribed!",Toast.LENGTH_LONG).show();
                                // turn on

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

    //-------------------- Voice -----------
    public void startVoiceRecognitionActivity() {
        String voiceSearchDialogTitle = "Comment By Voice";

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                voiceSearchDialogTitle);
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    private void onVoiceSearch(final String query) {
        mComment.setText(query);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it
            // could have heard
            ArrayList matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String enquiry = matches.get(0).toString();
            onVoiceSearch(enquiry);
        }
        super.onActivityResult(requestCode, resultCode, data);
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

}
