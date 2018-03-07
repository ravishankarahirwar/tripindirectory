package directory.tripin.com.tripindirectory.ChatingActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import directory.tripin.com.tripindirectory.ChatingActivities.models.ChatHeadPojo;
import directory.tripin.com.tripindirectory.ChatingActivities.models.ChatItemPojo;
import directory.tripin.com.tripindirectory.ChatingActivities.models.ChatItemViewHolder;
import directory.tripin.com.tripindirectory.ChatingActivities.models.UserActivityPojo;
import directory.tripin.com.tripindirectory.ChatingActivities.models.UserPresensePojo;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.helper.CircleTransform;
import directory.tripin.com.tripindirectory.helper.ListPaddingDecoration;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;
import directory.tripin.com.tripindirectory.utils.TextUtils;

public class ChatRoomActivity extends AppCompatActivity {

    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    FirestoreRecyclerAdapter<ChatItemPojo, ChatItemViewHolder> adapter;
    private RecyclerView mChatsList;
    private EditText mChatEditText;
    private ImageButton mSendAction;
    private ConstraintLayout mChatIntiatorLayout;
    private TextView mInitiatorMsg;
    LottieAnimationView loading;
    LinearLayoutManager mLayoutManagerChats;
    DatabaseReference databaseReference;
    ValueEventListener userpresencelistner;
    ValueEventListener useractivity;
    ConstraintLayout mTypingView;
    ImageView mTypingThumnail;
    ImageButton mCancelImsg;


    private String mChatRoomId;
    private String iMsg = "";


    //opponents necessary information
    private String mORMN;
    private String mOUID;

    //collected user info
    private String mOpponentCompName = "";
    private String mMyCompName = "";
    private String mOpponentImageUrl = "";
    private String mMyImageUrl = "";
    private String mOpponentFcm;
    private String mMyFcm;


    FirebaseAuth mAuth;
    TextUtils textUtils;
    ListPaddingDecoration listPaddingDecoration;


    private int mMsgType = 0;
    boolean isEtTapped = false;
    boolean isTyping = false;
    String mSubTitleText = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        mChatsList = findViewById(R.id.rv_chat);
        mLayoutManagerChats = new LinearLayoutManager(this);
        mLayoutManagerChats.setReverseLayout(true);
        //mLayoutManagerChats.setStackFromEnd(true);
        mChatsList.setLayoutManager(mLayoutManagerChats);
        listPaddingDecoration = new ListPaddingDecoration(getApplicationContext());

        mSendAction = findViewById(R.id.imageButtonSendAction);
        mChatEditText = findViewById(R.id.et_msg);
        mInitiatorMsg = findViewById(R.id.textViewimsg);
        mChatIntiatorLayout = findViewById(R.id.cl_imsg);
        loading = findViewById(R.id.loading);
        mTypingThumnail = findViewById(R.id.imageViewThumbTyping);
        mTypingView = findViewById(R.id.cl_typing);
        mCancelImsg = findViewById(R.id.imageButtonCancelImsg);
        textUtils = new TextUtils();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            finish();
            Toast.makeText(getApplicationContext(), "Sign In First!", Toast.LENGTH_LONG).show();
        }
        //get Intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            if (bundle.getString("ormn") != null && bundle.getString("ouid") != null) {
                mORMN = bundle.getString("ormn");
                mOUID = bundle.getString("ouid");

                //check if there is any i msg
                if (bundle.getString("imsg") != null) {
                    iMsg = bundle.getString("imsg");
                    if (!iMsg.isEmpty()) {
                        mInitiatorMsg.setText(iMsg);
                        mChatIntiatorLayout.setVisibility(View.VISIBLE);
                    }
                }
                setTitle(mORMN);
                //getSupportActionBar().setSubtitle("Active");
                getOpponentsDetails(mOUID);
            } else {
                Logger.v("ormn or ouid null");
                finish();
            }

        } else {
            Logger.v("bundle null");
            finish();
        }


        //set Listners;
        setListners();
//
//        final int abTitleId = getResources().getIdentifier("action_bar_title", "id", "android");
//        findViewById(abTitleId).setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ChatRoomActivity.this, PartnerDetailScrollingActivity.class);
//                intent.putExtra("uid",mOUID);
//                startActivity(intent);
//            }
//        });

    }

    private void setPresenceListners() {


        userpresencelistner = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserPresensePojo userPresensePojo = dataSnapshot.getValue(UserPresensePojo.class);
                    if (userPresensePojo != null) {
                        if (userPresensePojo.getActive()) {
                            mSubTitleText = "Active Now";
                            getSupportActionBar().setSubtitle(mSubTitleText);
                        } else {
                            Date date = new Date(userPresensePojo.getmTimeStamp());
                            mSubTitleText = "Active " + gettimeDiff(date);
                            getSupportActionBar().setSubtitle(mSubTitleText);
                        }
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.child("chatpresence").child("users").child(mOUID).addValueEventListener(userpresencelistner);

        useractivity = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    UserActivityPojo userActivityPojo = dataSnapshot.getValue(UserActivityPojo.class);
                    if (userActivityPojo != null) {
                        if (userActivityPojo.getTyping()) {
                            //set typing visible
                            // mTypingView.setVisibility(View.VISIBLE);
                            getSupportActionBar().setSubtitle("Typing...");
                        } else {
                            //set typing invisible
                            //mTypingView.setVisibility(View.GONE);
                            getSupportActionBar().setSubtitle(mSubTitleText);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.child("chatpresence").child("chatrooms").child(mChatRoomId).child(mOUID).addValueEventListener(useractivity);
    }


    private void getOpponentsDetails(String ouid) {

        FirebaseFirestore.getInstance().collection("partners").document(ouid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    PartnerInfoPojo mOpppontInfo = documentSnapshot.toObject(PartnerInfoPojo.class);

                    if (!mOpppontInfo.getmCompanyName().isEmpty()) {
                        mOpponentCompName = textUtils.toTitleCase(mOpppontInfo.getmCompanyName());
                        setTitle(textUtils.toTitleCase(mOpppontInfo.getmCompanyName()));
                    }

                    if (mOpppontInfo.getmImagesUrl() != null) {
                        mOpponentImageUrl = mOpppontInfo.getmImagesUrl().get(2);
                        //setTypingThumbnail(mOpponentImageUrl);
                        //setActionbarImage(mOpponentImageUrl);
                    }

                    mOpponentFcm = mOpppontInfo.getmFcmToken();

                    FirebaseFirestore.getInstance().collection("partners").document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            PartnerInfoPojo mInfo = documentSnapshot.toObject(PartnerInfoPojo.class);

                            if (!mInfo.getmCompanyName().isEmpty()) {
                                mMyCompName = mInfo.getmCompanyName();
                            }

                            if (mInfo.getmImagesUrl() != null) {
                                mMyImageUrl = mInfo.getmImagesUrl().get(2);
                            }
                            mMyFcm = mInfo.getmFcmToken();


                            FirebaseFirestore.getInstance().collection("chats").document("chatheads").collection(mAuth.getUid()).document(mOUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        ChatHeadPojo chatHeadPojo = documentSnapshot.toObject(ChatHeadPojo.class);
                                        mChatRoomId = chatHeadPojo.getmChatRoomId();
                                    } else {
                                        mChatRoomId = mAuth.getUid() + mOUID;
                                    }

                                    buildAdapter(mChatRoomId);
                                    setPresenceListners();
                                    UserPresensePojo userPresensePojo = new UserPresensePojo(true, new Date().getTime(), mChatRoomId);
                                    databaseReference.child("chatpresence").child("users").child(mAuth.getUid()).setValue(userPresensePojo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Logger.v("onResume userpresence updated");
                                        }
                                    });

                                }
                            });
                        }
                    });


                } else {
                    Logger.v("opponents doc dont exist");
                    finish();
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListners() {

        mCancelImsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iMsg = "";
                mChatIntiatorLayout.setVisibility(View.GONE);
            }
        });
        mChatEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    updateTypingStatus(false);
                }
            }
        });

        mChatEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isEtTapped = true;

                return false;
            }
        });
        mChatEditText.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                if (isEtTapped) {
                    mLayoutManagerChats.scrollToPosition(0);
                    isEtTapped = false;
                    Logger.v("onLayout change has focus");

                }
            }
        });
        mChatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Logger.v("before");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0) {
                    mSendAction.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_send_white_24dp));

                } else {
                    mSendAction.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_mic_white_24dp));

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty() && editable.toString().trim().length() == 1) {

                    //Log.i(TAG, “typing started event…”);

                    isTyping = true;
                    updateTypingStatus(isTyping);

                    //send typing started status

                } else if (editable.toString().trim().length() == 0 && isTyping) {

                    //Log.i(TAG, “typing stopped event…”);

                    isTyping = false;
                    updateTypingStatus(isTyping);

                    //send typing stopped status

                }

            }
        });
        mSendAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mChatEditText.getText().toString().isEmpty()) {
                    //voice
                    startVoiceRecognitionActivity();
                } else {
                    //compose and send message


                    if (!iMsg.isEmpty()) {
                        //send msg with imsg
                        mChatIntiatorLayout.setVisibility(View.GONE);

                        ChatItemPojo chatItemPojo = new ChatItemPojo(mAuth.getUid(),
                                mOUID,
                                mMyFcm,
                                mOpponentFcm,
                                mAuth.getCurrentUser().getPhoneNumber(),
                                mMyCompName,
                                iMsg, mChatRoomId,
                                0, 2);

                        FirebaseFirestore.getInstance().collection("chats").document("chatrooms").collection(mChatRoomId).add(chatItemPojo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                iMsg = "";
                                mSendAction.setClickable(false);
                                ChatItemPojo chatItemPojo = new ChatItemPojo(mAuth.getUid(),
                                        mOUID,
                                        mMyFcm,
                                        mOpponentFcm,
                                        mAuth.getCurrentUser().getPhoneNumber(),
                                        mMyCompName,
                                        mChatEditText.getText().toString().trim(),
                                        mChatRoomId,
                                        0, mMsgType);

                                final String lastmsg = mChatEditText.getText().toString().trim();
                                mChatEditText.setText("");

                                FirebaseFirestore.getInstance().collection("chats").document("chatrooms").collection(mChatRoomId).add(chatItemPojo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {

                                        mSendAction.setClickable(true);
                                        ChatHeadPojo chatHeadPojo = new ChatHeadPojo(mChatRoomId,
                                                mAuth.getCurrentUser().getPhoneNumber(),
                                                mAuth.getUid(),
                                                lastmsg,
                                                mMyImageUrl
                                                , mMyCompName);
                                        FirebaseFirestore.getInstance().collection("chats").document("chatheads").collection(mOUID).document(mAuth.getUid()).set(chatHeadPojo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                ChatHeadPojo chatHeadPojo = new ChatHeadPojo(mChatRoomId,
                                                        mORMN,
                                                        mOUID,
                                                        lastmsg,
                                                        mOpponentImageUrl
                                                        , mOpponentCompName);

                                                FirebaseFirestore.getInstance().collection("chats").document("chatheads").collection(mAuth.getUid()).document(mOUID).set(chatHeadPojo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Logger.v("heads updated");
                                                    }
                                                });
                                            }
                                        });

                                    }
                                });
                            }
                        });


                    } else {
                        //only msg
                        mSendAction.setClickable(false);
                        ChatItemPojo chatItemPojo = new ChatItemPojo(mAuth.getUid(),
                                mOUID,
                                mMyFcm,
                                mOpponentFcm,
                                mAuth.getCurrentUser().getPhoneNumber(),
                                mMyCompName,
                                mChatEditText.getText().toString().trim(),
                                mChatRoomId,
                                0, mMsgType);

                        final String lastmsg = mChatEditText.getText().toString().trim();
                        mChatEditText.setText("");

                        FirebaseFirestore.getInstance().collection("chats").document("chatrooms").collection(mChatRoomId).add(chatItemPojo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {

                                mSendAction.setClickable(true);
                                ChatHeadPojo chatHeadPojo = new ChatHeadPojo(mChatRoomId,
                                        mAuth.getCurrentUser().getPhoneNumber(),
                                        mAuth.getUid(),
                                        lastmsg,
                                        mMyImageUrl
                                        , mMyCompName);
                                FirebaseFirestore.getInstance().collection("chats").document("chatheads").collection(mOUID).document(mAuth.getUid()).set(chatHeadPojo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        ChatHeadPojo chatHeadPojo = new ChatHeadPojo(mChatRoomId,
                                                mORMN,
                                                mOUID,
                                                lastmsg,
                                                mOpponentImageUrl
                                                , mOpponentCompName);

                                        FirebaseFirestore.getInstance().collection("chats").document("chatheads").collection(mAuth.getUid()).document(mOUID).set(chatHeadPojo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Logger.v("heads updated");
                                            }
                                        });
                                    }
                                });

                            }
                        });
                    }

                }
            }
        });
    }

    private void updateTypingStatus(final boolean isTyping) {
        UserActivityPojo userActivityPojo = new UserActivityPojo(isTyping);
        databaseReference.child("chatpresence").child("chatrooms").child(mChatRoomId).child(mAuth.getUid()).setValue(userActivityPojo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Logger.v("typing status updated : " + isTyping);
            }
        });

    }

    private void buildAdapter(final String mChatRoomId) {

        Query query = FirebaseFirestore.getInstance()
                .collection("chats")
                .document("chatrooms")
                .collection(mChatRoomId)
                .orderBy("mTimeStamp", Query.Direction.DESCENDING).limit(100);

        FirestoreRecyclerOptions<ChatItemPojo> options = new FirestoreRecyclerOptions.Builder<ChatItemPojo>()
                .setQuery(query, ChatItemPojo.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ChatItemPojo, ChatItemViewHolder>(options) {

            @Override
            public int getItemViewType(int position) {
                if (getItem(position).getmSendersUid().equals(mAuth.getUid())) {
                    return 1;
                } else {
                    return 2;
                }
            }

            @Override
            public ChatItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view;

                switch (viewType) {
                    case 1: {
                        view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_chat_yours, parent, false);
                        return new ChatItemViewHolder(view);
                    }
                    case 2: {
                        view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_chat_opponents, parent, false);
                        return new ChatItemViewHolder(view);
                    }
                    default: {
                        return null;
                    }
                }

            }

            @Override
            protected void onBindViewHolder(final ChatItemViewHolder holder, final int position, final ChatItemPojo model) {

                Logger.v("onBind " + position + " " + model.getmChatMesssage());

                DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                final String docId = snapshot.getId();

                holder.msg.setText(model.getmChatMesssage());

//                if(model.getmMessageType()==2){
//                    holder.msg.setTypeface(null, Typeface.BOLD);
//                }

                if (model.getmSendersUid().equals(mAuth.getUid())) {
                    //my message
                    if (model.getmMessageStatus() == 1) {
                        Logger.v(model.getmChatMesssage() + " : is seen............... ");
                        holder.seenEye
                                .setImageDrawable(ContextCompat
                                        .getDrawable(getApplicationContext(),
                                                R.drawable.ic_visibility_red_24dp));
                    }
                } else {
                    //opponents message
                    if (model.getmMessageStatus() != 1) {
                        FirebaseFirestore.getInstance()
                                .collection("chats")
                                .document("chatrooms")
                                .collection(mChatRoomId)
                                .document(docId)
                                .update("mMessageStatus", 1);
                    }

                    if (model.getmMessageType() == 1) {
                        holder.thumbnail.setVisibility(View.INVISIBLE);
                    } else {
                        if(!mOpponentImageUrl.isEmpty()){
                            Picasso.with(holder.thumbnail.getContext())
                                    .load(mOpponentImageUrl)
                                    .placeholder(ContextCompat.getDrawable(getApplicationContext()
                                            , R.drawable.ic_insert_comment_black_24dp))
                                    .transform(new CircleTransform())
                                    .fit()
                                    .into(holder.thumbnail, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            Logger.v("image set: " + position);
                                        }

                                        @Override
                                        public void onError() {
                                            Logger.v("image error: " + position);
                                            if (mOUID != null) {
                                                FirebaseFirestore.getInstance().collection("partners").document(mOUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        if (documentSnapshot.exists()) {
                                                            PartnerInfoPojo partnerInfoPojo = documentSnapshot.toObject(PartnerInfoPojo.class);
                                                            if (partnerInfoPojo.getmImagesUrl() != null) {
                                                                if (partnerInfoPojo.getmImagesUrl().get(2) != null) {
                                                                    Picasso.with(holder.thumbnail.getContext())
                                                                            .load(partnerInfoPojo.getmImagesUrl().get(2))
                                                                            .placeholder(ContextCompat.getDrawable(getApplicationContext()
                                                                                    , R.drawable.ic_insert_comment_black_24dp))
                                                                            .transform(new CircleTransform())
                                                                            .fit()
                                                                            .into(holder.thumbnail, new Callback() {
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
                        }



                }
            }

            @Override
            public void onDataChanged() {
                mLayoutManagerChats.scrollToPosition(0);
                loading.setVisibility(View.GONE);
                super.onDataChanged();

                if (adapter.getItemCount() > 0) {
                    if (getItem(0) != null) {
                        Logger.v("onDataChanged " + getItem(0).getmChatMesssage());
                        if (getItem(0).getmSendersUid().equals(mAuth.getUid())) {
                            //my msg at last
                            mTypingThumnail.setVisibility(View.VISIBLE);
                            mMsgType = 1;
                        } else {
                            mTypingThumnail.setVisibility(View.INVISIBLE);
                            //opponents msg at last
                            mMsgType = 0;
                        }
                    }

                }

            }
        };

        mChatsList.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mChatRoomId != null) {
            if (!mChatRoomId.isEmpty()) {
                UserPresensePojo userPresensePojo = new UserPresensePojo(false, new Date().getTime(), mChatRoomId);
                databaseReference.child("chatpresence").child("users").child(mAuth.getUid()).setValue(userPresensePojo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Logger.v("onPause userpresence updated");
                    }
                });
                updateTypingStatus(false);
                removeuserpresencelistners();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.startListening();
            UserPresensePojo userPresensePojo = new UserPresensePojo(true, new Date().getTime(), mChatRoomId);
            databaseReference.child("chatpresence").child("users").child(mAuth.getUid()).setValue(userPresensePojo).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Logger.v("onResume userpresence updated");
                }
            });
            setPresenceListners();
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void startVoiceRecognitionActivity() {
        String voiceSearchDialogTitle = "Message By Voice! ";

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                voiceSearchDialogTitle);
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it
            // could have heard
            ArrayList matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String enquiry = matches.get(0).toString();
            mChatEditText.setText(enquiry);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setActionbarImage(String mOpponentImageUrl) {
        final ActionBar ab = getSupportActionBar();
        Picasso.with(this)
                .load(mOpponentImageUrl)
                .transform(new CircleTransform())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Drawable d = new BitmapDrawable(getResources(), bitmap);
                        ab.setIcon(d);
                        ab.setDisplayShowHomeEnabled(true);
                        ab.setDisplayHomeAsUpEnabled(true);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chatroom, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_call: {
                callNumber(mORMN);
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
            long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
            long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);

            if (diffInSeconds <= 0) {
                return "Today";
            }

            if (diffInSeconds < 60) {
                diff = "" + diffInSeconds + "sec ago";
            } else if (diffInMinutes < 60) {
                diff = "" + diffInMinutes + "min ago";
            } else if (diffInHours < 24) {
                diff = "" + diffInHours + "hrs ago";
            } else {

                long daysago = duration / (1000 * 60 * 60 * 24);
                diff = "" + daysago + "days ago";
            }

        }
        return diff;

    }

    public void removeuserpresencelistners() {
        databaseReference.child("chatpresence").child("users").child(mOUID).removeEventListener(userpresencelistner);
        databaseReference.child("chatpresence").child("chatrooms").child(mChatRoomId).child(mOUID).removeEventListener(useractivity);

    }

    public void setTypingThumbnail(String mOpponentImageUrl) {
        Picasso.with(getApplicationContext())
                .load(mOpponentImageUrl)
                .placeholder(ContextCompat.getDrawable(getApplicationContext()
                        , R.drawable.ic_insert_comment_black_24dp))
                .transform(new CircleTransform())
                .fit()
                .into(mTypingThumnail, new Callback() {
                    @Override
                    public void onSuccess() {
                        Logger.v("image set: typiing thumb");
                    }

                    @Override
                    public void onError() {
                        Logger.v("image error: Typing thumb");
                        if (mOUID != null) {
                            FirebaseFirestore.getInstance().collection("partners").document(mOUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                                                        .into(mTypingThumnail, new Callback() {
                                                            @Override
                                                            public void onSuccess() {
                                                                Logger.v("image set: typiing thumb 2");
                                                            }

                                                            @Override
                                                            public void onError() {
                                                                Logger.v("image error: Typing thumb 2 ");
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

    private void callNumber(String number) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + Uri.encode(number.trim())));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(callIntent);
    }
}
