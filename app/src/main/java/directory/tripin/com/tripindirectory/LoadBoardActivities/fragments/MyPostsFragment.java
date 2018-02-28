package directory.tripin.com.tripindirectory.LoadBoardActivities.fragments;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import directory.tripin.com.tripindirectory.ChatingActivities.ChatRoomActivity;
import directory.tripin.com.tripindirectory.LoadBoardActivities.FleetDetailsActivity;
import directory.tripin.com.tripindirectory.LoadBoardActivities.LoadDetailsActivity;
import directory.tripin.com.tripindirectory.LoadBoardActivities.PostLoadActivity;
import directory.tripin.com.tripindirectory.LoadBoardActivities.models.FleetPostPojo;
import directory.tripin.com.tripindirectory.LoadBoardActivities.models.FleetPostViewHolder;
import directory.tripin.com.tripindirectory.LoadBoardActivities.models.LoadPostPojo;
import directory.tripin.com.tripindirectory.LoadBoardActivities.models.LoadPostViewHolder;
import directory.tripin.com.tripindirectory.LoadBoardActivities.models.QuotePojo;
import directory.tripin.com.tripindirectory.LoadBoardActivities.models.QuoteViewHolder;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.helper.CircleTransform;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.helper.RecyclerViewAnimator;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;
import directory.tripin.com.tripindirectory.utils.TextUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyPostsFragment extends Fragment {

    private RadioGroup mSearchTagRadioGroup;
    private RadioButton radioButton2;
    private RadioButton radioButton1;
    private RecyclerViewAnimator mAnimatorFleet;
    private RecyclerViewAnimator mAnimatorLoad;

    private RecyclerView mLoadsList, mFleetsList;
    private String mUid = "";
    private FirebaseAuth firebaseAuth;


    public TextUtils textUtils;
    public FirestoreRecyclerAdapter adapterLoad, adapterFleet;

    public MyPostsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_intrested_in, container, false);

        mSearchTagRadioGroup = v.findViewById(R.id.search_tag_group);
        radioButton2 = v.findViewById(R.id.show_fleets);
        radioButton1 = v.findViewById(R.id.show_loads);
        mFleetsList = v.findViewById(R.id.rv_intrestedfleetss);
        mLoadsList = v.findViewById(R.id.rv_intrestedloads);
        mAnimatorFleet = new RecyclerViewAnimator(mFleetsList);
        mAnimatorLoad = new RecyclerViewAnimator(mLoadsList);


        mSearchTagRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int radioButtonID) {
                if (radioButtonID == R.id.show_loads) {

                    radioButton1.setTextColor(ContextCompat.getColorStateList(getActivity(), R.color.arrow_white));
                    //radioButton1.setTypeface(Typeface.DEFAULT_BOLD);
                    radioButton2.setTextColor(ContextCompat.getColorStateList(getActivity(), R.color.arrow_grey));
                    mLoadsList.setVisibility(View.VISIBLE);
                    mFleetsList.setVisibility(View.INVISIBLE);

                } else if (radioButtonID == R.id.show_fleets) {

                    radioButton1.setTextColor(ContextCompat.getColorStateList(getActivity(), R.color.arrow_grey));
                    radioButton2.setTextColor(ContextCompat.getColorStateList(getActivity(), R.color.arrow_white));

                    mFleetsList.setVisibility(View.VISIBLE);
                    mLoadsList.setVisibility(View.INVISIBLE);

                }
            }
        });

        mLoadsList.setVisibility(View.VISIBLE);
        mFleetsList.setVisibility(View.INVISIBLE);

        LinearLayoutManager mLayoutManagerLoads = new LinearLayoutManager(getActivity());
        mLoadsList.setLayoutManager(mLayoutManagerLoads);
        mLoadsList.setAdapter(adapterLoad);

        LinearLayoutManager mLayoutManagerFleets = new LinearLayoutManager(getActivity());
        mFleetsList.setLayoutManager(mLayoutManagerFleets);
        mFleetsList.setAdapter(adapterFleet);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textUtils = new TextUtils();
        firebaseAuth = FirebaseAuth.getInstance();
        mUid = firebaseAuth.getUid();
        setLoadsAdapter();
        setFleetsAdapter();


    }

    private void setLoadsAdapter() {
        Query queryLoad = FirebaseFirestore.getInstance()
                .collection("loads")
                .whereEqualTo("mPostersUid", FirebaseAuth.getInstance().getUid());
        FirestoreRecyclerOptions<LoadPostPojo> optionsLoad = new FirestoreRecyclerOptions.Builder<LoadPostPojo>()
                .setQuery(queryLoad, LoadPostPojo.class)
                .build();

        adapterLoad = new FirestoreRecyclerAdapter<LoadPostPojo, LoadPostViewHolder>(optionsLoad) {
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
                } else if (d.length() > 5) {
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
                                "Promote", "Reschedule", "Delete", "Cancel"
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
                                        break;
                                    }
                                    case 1: {
                                        final Calendar myCalendar;

                                        myCalendar = Calendar.getInstance();
                                        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear,
                                                                  int dayOfMonth) {
                                                myCalendar.set(Calendar.YEAR, year);
                                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                                FirebaseFirestore.getInstance().collection("loads").document(docId).update("mPickUpTimeStamp",myCalendar.getTime()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getActivity(),"Scheduled Date Changed",Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }, myCalendar
                                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                                myCalendar.get(Calendar.DAY_OF_MONTH));

                                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                                        datePickerDialog.show();
                                        break;
                                    }
                                    case 2: {

                                        FirebaseFirestore.getInstance().collection("loads").document(docId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getActivity(),"Post Removed",Toast.LENGTH_LONG).show();
                                            }
                                        });

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

                } else {
                    holder.badgeQuote.setText("0");
                }


                holder.badgeComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), LoadDetailsActivity.class);
                        intent.putExtra("docId", docId);
                        startActivity(intent);
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

                holder.mScheduledDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar myCalendar;

                        myCalendar = Calendar.getInstance();
                        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear,
                                                  int dayOfMonth) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                FirebaseFirestore.getInstance().collection("loads").document(docId).update("mPickUpTimeStamp",myCalendar.getTime()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getActivity(),"Scheduled Date Changed",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH));

                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                        datePickerDialog.show();
                    }
                });

                holder.badgeQuote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(getActivity());

                        dialog.setContentView(R.layout.dialog_list_items);
                        dialog.setTitle("All Quotes :");
                        RecyclerView quotesList = dialog.findViewById(R.id.recyclerView);
                        quotesList.setLayoutManager(new LinearLayoutManager(getActivity()));
                        TextView cancel = dialog.findViewById(R.id.textViewOk);
                        final FirestoreRecyclerAdapter adapter;


                        Query queryQuotes = FirebaseFirestore.getInstance()
                                .collection("loads").document(docId).collection("mQuotesCollection");

                        FirestoreRecyclerOptions<QuotePojo> optionsQuote = new FirestoreRecyclerOptions.Builder<QuotePojo>()
                                .setQuery(queryQuotes, QuotePojo.class)
                                .build();

                        adapter = new FirestoreRecyclerAdapter<QuotePojo, QuoteViewHolder>(optionsQuote) {
                            @Override
                            public QuoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.item_quote_display, parent, false);

                                return new QuoteViewHolder(view);
                            }

                            @Override
                            protected void onBindViewHolder(final QuoteViewHolder holder, final int position, final QuotePojo model) {
                                holder.inbox.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                                        intent.putExtra("imsg","Texting from your Quotation on my Load Post:\n"+model.getInitiatorMsgText());
                                        intent.putExtra("ormn",model.getmRMN());
                                        intent.putExtra("ouid",model.getmUid());
                                        startActivity(intent);
                                    }
                                });

                                holder.call.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String phoneNO = model.getmRMN();
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
                                            Toast.makeText(getActivity(), "Sorry!! Mobile no not available", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                holder.quote.setText(model.getmQuoteAmount() + "₹");

                                String comment = "";
                                if (model.getmCompanyName().isEmpty()) {
                                    comment = model.getmRMN();
                                    Logger.v(model.getmRMN() + "");
                                } else {
                                    comment = model.getmCompanyName();
                                }
                                if (!model.getmComment().isEmpty()) {
                                    comment = comment + " : " + model.getmComment();
                                }
                                holder.comment.setText(comment);

                                holder.time.setText(gettimeDiff(model.getmTimeStamp()));

                                FirebaseFirestore.getInstance().collection("partners").document(model.getmUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            PartnerInfoPojo partnerInfoPojo = documentSnapshot.toObject(PartnerInfoPojo.class);
                                            String comment = "";
                                            if (!partnerInfoPojo.getmCompanyName().isEmpty()) {

                                                comment = partnerInfoPojo.getmCompanyName();
                                            }
                                            if (!model.getmComment().isEmpty()) {
                                                comment = comment + " : " + model.getmComment();
                                            }
                                            holder.comment.setText(comment);

                                            if (partnerInfoPojo.getmImagesUrl() != null) {
                                                if (partnerInfoPojo.getmImagesUrl().get(2) != null) {
                                                    Picasso.with(getActivity())
                                                            .load(partnerInfoPojo.getmImagesUrl().get(2))
                                                            .placeholder(ContextCompat.getDrawable(getActivity()
                                                                    , R.drawable.ic_rupee_black))
                                                            .transform(new CircleTransform())
                                                            .fit()
                                                            .into(holder.thumb, new Callback() {
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


                                        }

                                    }
                                });


                            }


                        };


                        quotesList.setAdapter(adapter);
                        adapter.startListening();

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                adapter.stopListening();
                                dialog.dismiss();
                            }
                        });

                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        Window window = dialog.getWindow();
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    }
                });

                holder.badgeLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(getActivity());

                        dialog.setContentView(R.layout.dialog_list_items);
                        dialog.setTitle("Interested People :");
                        RecyclerView list = dialog.findViewById(R.id.recyclerView);
                        list.setLayoutManager(new LinearLayoutManager(getActivity()));
                        TextView cancel = dialog.findViewById(R.id.textViewOk);

                        final List<String> data = new ArrayList<>();

                        if(loadPostPojo.getmIntrestedPeopleList()!=null){
                            for (String s : loadPostPojo.getmIntrestedPeopleList().keySet()) {
                                if (loadPostPojo.getmIntrestedPeopleList().get(s)) {
                                    data.add(s);
                                }
                            }
                        }


                        final RecyclerView.Adapter<QuoteViewHolder> adapter = new RecyclerView.Adapter<QuoteViewHolder>() {
                            @Override
                            public QuoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.item_quote_display, parent, false);

                                return new QuoteViewHolder(view);
                            }

                            @Override
                            public void onBindViewHolder(final QuoteViewHolder holder, final int position) {

                                FirebaseFirestore.getInstance().collection("partners").document(data.get(position)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        final PartnerInfoPojo partnerInfoPojo = documentSnapshot.toObject(PartnerInfoPojo.class);
                                        holder.time.setText("");
                                        holder.comment.setText(partnerInfoPojo.getmRMN());
                                        holder.quote.setText(textUtils.toTitleCase(partnerInfoPojo.getmCompanyName()));

                                        holder.inbox.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                                                intent.putExtra("imsg","Texting from your Like on my load post");
                                                intent.putExtra("ormn",partnerInfoPojo.getmRMN());
                                                intent.putExtra("ouid",data.get(position));
                                                startActivity(intent);
                                            }
                                        });

                                        holder.call.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String phoneNO = partnerInfoPojo.getmRMN();
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
                                                    Toast.makeText(getActivity(), "Sorry!! Mobile no not available", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                        if (partnerInfoPojo.getmImagesUrl() != null) {
                                            if (partnerInfoPojo.getmImagesUrl().get(2) != null) {
                                                Picasso.with(getActivity())
                                                        .load(partnerInfoPojo.getmImagesUrl().get(2))
                                                        .placeholder(ContextCompat.getDrawable(getActivity()
                                                                , R.drawable.ic_favorite))
                                                        .transform(new CircleTransform())
                                                        .fit()
                                                        .into(holder.thumb, new Callback() {
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
                                    }
                                });
                            }

                            @Override
                            public int getItemCount() {
                                return data.size();
                            }
                        };

                        list.setAdapter(adapter);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });


                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        Window window = dialog.getWindow();
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                });

                holder.badgeShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(getActivity());

                        dialog.setContentView(R.layout.dialog_list_items);
                        dialog.setTitle("Shared People :");
                        RecyclerView list = dialog.findViewById(R.id.recyclerView);
                        list.setLayoutManager(new LinearLayoutManager(getActivity()));
                        TextView cancel = dialog.findViewById(R.id.textViewOk);

                        final List<String> data = new ArrayList<>();

                        if (loadPostPojo.getmSharedPeopleList() != null)
                            data.addAll(loadPostPojo.getmSharedPeopleList().keySet());

                        RecyclerView.Adapter<QuoteViewHolder> adapter = new RecyclerView.Adapter<QuoteViewHolder>() {
                            @Override
                            public QuoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.item_quote_display, parent, false);

                                return new QuoteViewHolder(view);
                            }

                            @Override
                            public void onBindViewHolder(final QuoteViewHolder holder, final int position) {

                                FirebaseFirestore.getInstance().collection("partners").document(data.get(position)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        final PartnerInfoPojo partnerInfoPojo = documentSnapshot.toObject(PartnerInfoPojo.class);
                                        holder.time.setText("");
                                        holder.comment.setText(partnerInfoPojo.getmRMN());
                                        holder.quote.setText(textUtils.toTitleCase(partnerInfoPojo.getmCompanyName()));

                                        holder.inbox.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                                                intent.putExtra("imsg","Texting from your Share on my load post");
                                                intent.putExtra("ormn",partnerInfoPojo.getmRMN());
                                                intent.putExtra("ouid",data.get(position));
                                                startActivity(intent);
                                            }
                                        });

                                        holder.call.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String phoneNO = partnerInfoPojo.getmRMN();
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
                                                    Toast.makeText(getActivity(), "Sorry!! Mobile no not available", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                        if (partnerInfoPojo.getmImagesUrl() != null) {
                                            if (partnerInfoPojo.getmImagesUrl().get(2) != null) {
                                                Picasso.with(getActivity())
                                                        .load(partnerInfoPojo.getmImagesUrl().get(2))
                                                        .placeholder(ContextCompat.getDrawable(getActivity()
                                                                , R.drawable.ic_share_blackk_24dp))
                                                        .transform(new CircleTransform())
                                                        .fit()
                                                        .into(holder.thumb, new Callback() {
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
                                    }
                                });
                            }

                            @Override
                            public int getItemCount() {
                                return data.size();
                            }
                        };

                        list.setAdapter(adapter);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });


                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        Window window = dialog.getWindow();
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                });

                holder.badgeCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(getActivity());

                        dialog.setContentView(R.layout.dialog_list_items);
                        dialog.setTitle("People who called :");
                        RecyclerView list = dialog.findViewById(R.id.recyclerView);
                        list.setLayoutManager(new LinearLayoutManager(getActivity()));
                        TextView cancel = dialog.findViewById(R.id.textViewOk);

                        final List<String> data = new ArrayList<>();

                        if (loadPostPojo.getmCalledPeopleList() != null)
                            data.addAll(loadPostPojo.getmCalledPeopleList().keySet());

                        RecyclerView.Adapter<QuoteViewHolder> adapter = new RecyclerView.Adapter<QuoteViewHolder>() {
                            @Override
                            public QuoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.item_quote_display, parent, false);

                                return new QuoteViewHolder(view);
                            }

                            @Override
                            public void onBindViewHolder(final QuoteViewHolder holder, final int position) {

                                FirebaseFirestore.getInstance().collection("partners").document(data.get(position)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        final PartnerInfoPojo partnerInfoPojo = documentSnapshot.toObject(PartnerInfoPojo.class);
                                        holder.time.setText("");
                                        holder.comment.setText(partnerInfoPojo.getmRMN());
                                        holder.quote.setText(textUtils.toTitleCase(partnerInfoPojo.getmCompanyName()));

                                        holder.inbox.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                                                intent.putExtra("imsg","Texting from your Call on my load post");
                                                intent.putExtra("ormn",partnerInfoPojo.getmRMN());
                                                intent.putExtra("ouid",data.get(position));
                                                startActivity(intent);
                                            }
                                        });

                                        holder.call.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String phoneNO = partnerInfoPojo.getmRMN();
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
                                                    Toast.makeText(getActivity(), "Sorry!! Mobile no not available", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                        if (partnerInfoPojo.getmImagesUrl() != null) {
                                            if (partnerInfoPojo.getmImagesUrl().get(2) != null) {
                                                Picasso.with(getActivity())
                                                        .load(partnerInfoPojo.getmImagesUrl().get(2))
                                                        .placeholder(ContextCompat.getDrawable(getActivity()
                                                                , R.drawable.ic_call_black))
                                                        .transform(new CircleTransform())
                                                        .fit()
                                                        .into(holder.thumb, new Callback() {
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
                                    }
                                });
                            }

                            @Override
                            public int getItemCount() {
                                return data.size();
                            }
                        };

                        list.setAdapter(adapter);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });


                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        Window window = dialog.getWindow();
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                });

                holder.badgeInbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(getActivity());

                        dialog.setContentView(R.layout.dialog_list_items);
                        dialog.setTitle("People who inboxed :");
                        RecyclerView list = dialog.findViewById(R.id.recyclerView);
                        list.setLayoutManager(new LinearLayoutManager(getActivity()));
                        TextView cancel = dialog.findViewById(R.id.textViewOk);

                        final List<String> data = new ArrayList<>();

                        if (loadPostPojo.getmInboxedPeopleList() != null)
                            data.addAll(loadPostPojo.getmInboxedPeopleList().keySet());

                        RecyclerView.Adapter<QuoteViewHolder> adapter = new RecyclerView.Adapter<QuoteViewHolder>() {
                            @Override
                            public QuoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.item_quote_display, parent, false);

                                return new QuoteViewHolder(view);
                            }

                            @Override
                            public void onBindViewHolder(final QuoteViewHolder holder, final int position) {

                                FirebaseFirestore.getInstance().collection("partners").document(data.get(position)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        final PartnerInfoPojo partnerInfoPojo = documentSnapshot.toObject(PartnerInfoPojo.class);
                                        holder.time.setText("");
                                        holder.comment.setText(partnerInfoPojo.getmRMN());
                                        holder.quote.setText(textUtils.toTitleCase(partnerInfoPojo.getmCompanyName()));

                                        holder.inbox.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                                                intent.putExtra("ormn",partnerInfoPojo.getmRMN());
                                                intent.putExtra("ouid",data.get(position));
                                                startActivity(intent);
                                            }
                                        });

                                        holder.call.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String phoneNO = partnerInfoPojo.getmRMN();
                                                if (phoneNO != null && phoneNO.length() > 0) {
                                                    callNumber(phoneNO);

                                                } else {
                                                    Toast.makeText(getActivity(), "Sorry!! Mobile no not available", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                        if (partnerInfoPojo.getmImagesUrl() != null) {
                                            if (partnerInfoPojo.getmImagesUrl().get(2) != null) {
                                                Picasso.with(getActivity())
                                                        .load(partnerInfoPojo.getmImagesUrl().get(2))
                                                        .placeholder(ContextCompat.getDrawable(getActivity()
                                                                , R.drawable.ic_email_black_24dp))
                                                        .transform(new CircleTransform())
                                                        .fit()
                                                        .into(holder.thumb, new Callback() {
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
                                    }
                                });
                            }

                            @Override
                            public int getItemCount() {
                                return data.size();
                            }
                        };

                        list.setAdapter(adapter);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });


                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        Window window = dialog.getWindow();
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                });


                Logger.v("onBind: " + position);
                //mAnimator.onBindViewHolder(holder.itemView, position);


            }

            @Override
            public LoadPostViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_my_loadpost, group, false);
                mAnimatorLoad.onCreateViewHolder(view);

                return new LoadPostViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                radioButton1.setText("Loads(" + adapterLoad.getItemCount() + ")");
            }
        };


    }

    private void setFleetsAdapter() {

        Query queryFleet = FirebaseFirestore.getInstance()
                .collection("fleets")
                .whereEqualTo("mPostersUid", FirebaseAuth.getInstance().getUid());
        FirestoreRecyclerOptions<FleetPostPojo> optionsFleet = new FirestoreRecyclerOptions.Builder<FleetPostPojo>()
                .setQuery(queryFleet, FleetPostPojo.class)
                .build();
        adapterFleet = new FirestoreRecyclerAdapter<FleetPostPojo, FleetPostViewHolder>(optionsFleet) {
            @Override
            public void onBindViewHolder(final FleetPostViewHolder holder, final int position, final FleetPostPojo fleetPostPojo) {
                DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                final String docId = snapshot.getId();


                String DisplayDate = DateFormat.getDateInstance().format(fleetPostPojo.getmPickUpTimeStamp());
                String d = gettimeDiff(fleetPostPojo.getmPickUpTimeStamp());
                String dd = "";
                if (d.equals("0")) {
                    dd = " (today)";
                } else if (d.equals("1")) {
                    dd = " (tomorrow)";
                } else if (d.length() > 5) {
                    dd = " (expired!)";
                } else {
                    dd = " (" + d + " days left)";
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
                                "Promote", "Reschedule", "Delete", "Cancel"
                        };

                        final AlertDialog alert;
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                                        break;
                                    }
                                    case 1: {
                                        final Calendar myCalendar;

                                        myCalendar = Calendar.getInstance();
                                        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear,
                                                                  int dayOfMonth) {
                                                myCalendar.set(Calendar.YEAR, year);
                                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                                FirebaseFirestore.getInstance().collection("fleets").document(docId).update("mPickUpTimeStamp",myCalendar.getTime()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getActivity(),"Scheduled Date Changed",Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }, myCalendar
                                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                                myCalendar.get(Calendar.DAY_OF_MONTH));

                                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                                        datePickerDialog.show();
                                        break;
                                    }
                                    case 2: {

                                        FirebaseFirestore.getInstance().collection("fleets").document(docId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getActivity(),"Post Removed",Toast.LENGTH_LONG).show();
                                            }
                                        });

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

                } else {
                    holder.badgeQuote.setText("0");
                }



                holder.badgeComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), FleetDetailsActivity.class);
                        intent.putExtra("docId", docId);
                        startActivity(intent);
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


                holder.mScheduledDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar myCalendar;

                        myCalendar = Calendar.getInstance();
                        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear,
                                                  int dayOfMonth) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                FirebaseFirestore.getInstance().collection("fleets").document(docId).update("mPickUpTimeStamp",myCalendar.getTime()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getActivity(),"Scheduled Date Changed",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH));

                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                        datePickerDialog.show();
                    }
                });

                holder.badgeQuote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(getActivity());

                        dialog.setContentView(R.layout.dialog_list_items);
                        dialog.setTitle("All Quotes :");
                        RecyclerView quotesList = dialog.findViewById(R.id.recyclerView);
                        quotesList.setLayoutManager(new LinearLayoutManager(getActivity()));
                        TextView cancel = dialog.findViewById(R.id.textViewOk);
                        final FirestoreRecyclerAdapter adapter;


                        Query queryQuotes = FirebaseFirestore.getInstance()
                                .collection("fleets").document(docId).collection("mQuotesCollection");

                        FirestoreRecyclerOptions<QuotePojo> optionsQuote = new FirestoreRecyclerOptions.Builder<QuotePojo>()
                                .setQuery(queryQuotes, QuotePojo.class)
                                .build();

                        adapter = new FirestoreRecyclerAdapter<QuotePojo, QuoteViewHolder>(optionsQuote) {

                            @Override
                            public int getItemViewType(int position) {
                                Logger.v("getItemViewType: "+position+getItem(position).getmQuoteAmount());
                                return super.getItemViewType(position);
                            }

                            @Override
                            public QuoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                                Logger.v("onCreateViewHolder");
                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.item_quote_display, parent, false);

                                return new QuoteViewHolder(view);
                            }

                            @Override
                            protected void onBindViewHolder(final QuoteViewHolder holder, final int position, final QuotePojo model) {
                                Logger.v("onBindViewHolder");

                                holder.inbox.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                                        intent.putExtra("imsg","Texting from your Quotation on my Fleet Post:\n"+model.getInitiatorMsgText());
                                        intent.putExtra("ormn",model.getmRMN());
                                        intent.putExtra("ouid",model.getmUid());
                                        startActivity(intent);

                                    }
                                });

                                holder.call.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String phoneNO = model.getmRMN();
                                        if (phoneNO != null && phoneNO.length() > 0) {
                                            callNumber(phoneNO);

                                        } else {
                                            Toast.makeText(getActivity(), "Sorry!! Mobile no not available", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                holder.quote.setText(model.getmQuoteAmount() + "₹");

                                String comment = "";
                                if (model.getmCompanyName().isEmpty()) {
                                    comment = model.getmRMN();
                                    Logger.v(model.getmRMN() + "");
                                } else {
                                    comment = model.getmCompanyName();
                                }
                                if (!model.getmComment().isEmpty()) {
                                    comment = comment + " : " + model.getmComment();
                                }
                                holder.comment.setText(comment);

                                holder.time.setText(gettimeDiff(model.getmTimeStamp()));

                                FirebaseFirestore.getInstance().collection("partners").document(model.getmUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            PartnerInfoPojo partnerInfoPojo = documentSnapshot.toObject(PartnerInfoPojo.class);
                                            String comment = "";
                                            if (!partnerInfoPojo.getmCompanyName().isEmpty()) {

                                                comment = partnerInfoPojo.getmCompanyName();
                                            }
                                            if (!model.getmComment().isEmpty()) {
                                                comment = comment + " : " + model.getmComment();
                                            }
                                            holder.comment.setText(comment);

                                            if (partnerInfoPojo.getmImagesUrl() != null) {
                                                if (partnerInfoPojo.getmImagesUrl().get(2) != null) {
                                                    Picasso.with(getActivity())
                                                            .load(partnerInfoPojo.getmImagesUrl().get(2))
                                                            .placeholder(ContextCompat.getDrawable(getActivity()
                                                                    , R.drawable.ic_rupee_black))
                                                            .transform(new CircleTransform())
                                                            .fit()
                                                            .into(holder.thumb, new Callback() {
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


                                        }

                                    }
                                });


                            }


                        };


                        quotesList.setAdapter(adapter);
                        adapter.startListening();

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                adapter.stopListening();
                                dialog.dismiss();
                            }
                        });

                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        Window window = dialog.getWindow();
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    }
                });

                holder.badgeLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(getActivity());

                        dialog.setContentView(R.layout.dialog_list_items);
                        dialog.setTitle("Interested People :");
                        RecyclerView list = dialog.findViewById(R.id.recyclerView);
                        list.setLayoutManager(new LinearLayoutManager(getActivity()));
                        TextView cancel = dialog.findViewById(R.id.textViewOk);

                        final List<String> data = new ArrayList<>();

                        for (String s : fleetPostPojo.getmIntrestedPeopleList().keySet()) {
                            if (fleetPostPojo.getmIntrestedPeopleList().get(s)) {
                                data.add(s);
                            }
                        }

                        RecyclerView.Adapter<QuoteViewHolder> adapter = new RecyclerView.Adapter<QuoteViewHolder>() {
                            @Override
                            public QuoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.item_quote_display, parent, false);

                                return new QuoteViewHolder(view);
                            }

                            @Override
                            public void onBindViewHolder(final QuoteViewHolder holder, final int position) {

                                FirebaseFirestore.getInstance().collection("partners").document(data.get(position)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        final PartnerInfoPojo partnerInfoPojo = documentSnapshot.toObject(PartnerInfoPojo.class);
                                        holder.time.setText("");
                                        holder.comment.setText(partnerInfoPojo.getmRMN());
                                        holder.quote.setText(textUtils.toTitleCase(partnerInfoPojo.getmCompanyName()));

                                        holder.inbox.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                                                intent.putExtra("imsg","Texting from your Like on my Fleet post");
                                                intent.putExtra("ormn",partnerInfoPojo.getmRMN());
                                                intent.putExtra("ouid",data.get(position));
                                                startActivity(intent);
                                            }
                                        });

                                        holder.call.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String phoneNO = partnerInfoPojo.getmRMN();
                                                if (phoneNO != null && phoneNO.length() > 0) {
                                                    callNumber(phoneNO);

                                                } else {
                                                    Toast.makeText(getActivity(), "Sorry!! Mobile no not available", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                        if (partnerInfoPojo.getmImagesUrl() != null) {
                                            if (partnerInfoPojo.getmImagesUrl().get(2) != null) {
                                                Picasso.with(getActivity())
                                                        .load(partnerInfoPojo.getmImagesUrl().get(2))
                                                        .placeholder(ContextCompat.getDrawable(getActivity()
                                                                , R.drawable.ic_favorite))
                                                        .transform(new CircleTransform())
                                                        .fit()
                                                        .into(holder.thumb, new Callback() {
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
                                    }
                                });
                            }

                            @Override
                            public int getItemCount() {
                                return data.size();
                            }
                        };

                        list.setAdapter(adapter);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });


                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        Window window = dialog.getWindow();
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                });

                holder.badgeShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(getActivity());

                        dialog.setContentView(R.layout.dialog_list_items);
                        dialog.setTitle("Shared People :");
                        RecyclerView list = dialog.findViewById(R.id.recyclerView);
                        list.setLayoutManager(new LinearLayoutManager(getActivity()));
                        TextView cancel = dialog.findViewById(R.id.textViewOk);

                        final List<String> data = new ArrayList<>();

                        if (fleetPostPojo.getmSharedPeopleList() != null)
                            data.addAll(fleetPostPojo.getmSharedPeopleList().keySet());

                        RecyclerView.Adapter<QuoteViewHolder> adapter = new RecyclerView.Adapter<QuoteViewHolder>() {
                            @Override
                            public QuoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.item_quote_display, parent, false);

                                return new QuoteViewHolder(view);
                            }

                            @Override
                            public void onBindViewHolder(final QuoteViewHolder holder, final int position) {

                                FirebaseFirestore.getInstance().collection("partners").document(data.get(position)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        final PartnerInfoPojo partnerInfoPojo = documentSnapshot.toObject(PartnerInfoPojo.class);
                                        holder.time.setText("");
                                        holder.comment.setText(partnerInfoPojo.getmRMN());
                                        holder.quote.setText(textUtils.toTitleCase(partnerInfoPojo.getmCompanyName()));

                                        holder.inbox.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                                                intent.putExtra("imsg","Texting from your Share on my fleet post");
                                                intent.putExtra("ormn",partnerInfoPojo.getmRMN());
                                                intent.putExtra("ouid",data.get(position));
                                                startActivity(intent);
                                            }
                                        });

                                        holder.call.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String phoneNO = partnerInfoPojo.getmRMN();
                                                if (phoneNO != null && phoneNO.length() > 0) {
                                                    callNumber(phoneNO);
                                                } else {
                                                    Toast.makeText(getActivity(), "Sorry!! Mobile no not available", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                        if (partnerInfoPojo.getmImagesUrl() != null) {
                                            if (partnerInfoPojo.getmImagesUrl().get(2) != null) {
                                                Picasso.with(getActivity())
                                                        .load(partnerInfoPojo.getmImagesUrl().get(2))
                                                        .placeholder(ContextCompat.getDrawable(getActivity()
                                                                , R.drawable.ic_share_blackk_24dp))
                                                        .transform(new CircleTransform())
                                                        .fit()
                                                        .into(holder.thumb, new Callback() {
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
                                    }
                                });
                            }

                            @Override
                            public int getItemCount() {
                                return data.size();
                            }
                        };

                        list.setAdapter(adapter);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });


                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        Window window = dialog.getWindow();
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                });

                holder.badgeCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(getActivity());

                        dialog.setContentView(R.layout.dialog_list_items);
                        dialog.setTitle("People who called :");
                        RecyclerView list = dialog.findViewById(R.id.recyclerView);
                        list.setLayoutManager(new LinearLayoutManager(getActivity()));
                        TextView cancel = dialog.findViewById(R.id.textViewOk);

                        final List<String> data = new ArrayList<>();

                        if (fleetPostPojo.getmCalledPeopleList() != null)
                            data.addAll(fleetPostPojo.getmCalledPeopleList().keySet());

                        RecyclerView.Adapter<QuoteViewHolder> adapter = new RecyclerView.Adapter<QuoteViewHolder>() {
                            @Override
                            public QuoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.item_quote_display, parent, false);

                                return new QuoteViewHolder(view);
                            }

                            @Override
                            public void onBindViewHolder(final QuoteViewHolder holder, final int position) {

                                FirebaseFirestore.getInstance().collection("partners").document(data.get(position)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        final PartnerInfoPojo partnerInfoPojo = documentSnapshot.toObject(PartnerInfoPojo.class);
                                        holder.time.setText("");
                                        holder.comment.setText(partnerInfoPojo.getmRMN());
                                        holder.quote.setText(textUtils.toTitleCase(partnerInfoPojo.getmCompanyName()));

                                        holder.inbox.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                                                intent.putExtra("imsg","Texting from your Call on my fleet post");
                                                intent.putExtra("ormn",partnerInfoPojo.getmRMN());
                                                intent.putExtra("ouid",data.get(position));
                                                startActivity(intent);
                                            }
                                        });

                                        holder.call.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String phoneNO = partnerInfoPojo.getmRMN();
                                                if (phoneNO != null && phoneNO.length() > 0) {
                                                    callNumber(phoneNO);
                                                } else {
                                                    Toast.makeText(getActivity(), "Sorry!! Mobile no not available", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                        if (partnerInfoPojo.getmImagesUrl() != null) {
                                            if (partnerInfoPojo.getmImagesUrl().get(2) != null) {
                                                Picasso.with(getActivity())
                                                        .load(partnerInfoPojo.getmImagesUrl().get(2))
                                                        .placeholder(ContextCompat.getDrawable(getActivity()
                                                                , R.drawable.ic_call_black))
                                                        .transform(new CircleTransform())
                                                        .fit()
                                                        .into(holder.thumb, new Callback() {
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
                                    }
                                });
                            }

                            @Override
                            public int getItemCount() {
                                return data.size();
                            }
                        };

                        list.setAdapter(adapter);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });


                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        Window window = dialog.getWindow();
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                });

                holder.badgeInbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(getActivity());

                        dialog.setContentView(R.layout.dialog_list_items);
                        dialog.setTitle("People who inboxed :");
                        RecyclerView list = dialog.findViewById(R.id.recyclerView);
                        list.setLayoutManager(new LinearLayoutManager(getActivity()));
                        TextView cancel = dialog.findViewById(R.id.textViewOk);

                        final List<String> data = new ArrayList<>();

                        if (fleetPostPojo.getmInboxedPeopleList() != null)
                            data.addAll(fleetPostPojo.getmInboxedPeopleList().keySet());

                        RecyclerView.Adapter<QuoteViewHolder> adapter = new RecyclerView.Adapter<QuoteViewHolder>() {
                            @Override
                            public QuoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.item_quote_display, parent, false);

                                return new QuoteViewHolder(view);
                            }

                            @Override
                            public void onBindViewHolder(final QuoteViewHolder holder, final int position) {

                                FirebaseFirestore.getInstance().collection("partners").document(data.get(position)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        final PartnerInfoPojo partnerInfoPojo = documentSnapshot.toObject(PartnerInfoPojo.class);
                                        holder.time.setText("");
                                        holder.comment.setText(partnerInfoPojo.getmRMN());
                                        holder.quote.setText(textUtils.toTitleCase(partnerInfoPojo.getmCompanyName()));

                                        holder.inbox.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                                                intent.putExtra("ormn",partnerInfoPojo.getmRMN());
                                                intent.putExtra("ouid",data.get(position));
                                                startActivity(intent);
                                            }
                                        });

                                        holder.call.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String phoneNO = partnerInfoPojo.getmRMN();
                                                if (phoneNO != null && phoneNO.length() > 0) {
                                                    callNumber(phoneNO);

                                                } else {
                                                    Toast.makeText(getActivity(), "Sorry!! Mobile no not available", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                        if (partnerInfoPojo.getmImagesUrl() != null) {
                                            if (partnerInfoPojo.getmImagesUrl().get(2) != null) {
                                                Picasso.with(getActivity())
                                                        .load(partnerInfoPojo.getmImagesUrl().get(2))
                                                        .placeholder(ContextCompat.getDrawable(getActivity()
                                                                , R.drawable.ic_email_black_24dp))
                                                        .transform(new CircleTransform())
                                                        .fit()
                                                        .into(holder.thumb, new Callback() {
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
                                    }
                                });
                            }

                            @Override
                            public int getItemCount() {
                                return data.size();
                            }
                        };

                        list.setAdapter(adapter);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });


                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        Window window = dialog.getWindow();
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
                        .inflate(R.layout.item_my_fleetpost, group, false);
                mAnimatorFleet.onCreateViewHolder(view);

                return new FleetPostViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                radioButton2.setText("Fleets(" + adapterFleet.getItemCount() + ")");
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        adapterFleet.startListening();
        adapterLoad.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterFleet.stopListening();
        adapterLoad.stopListening();

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

    private void hideSoftKey() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
