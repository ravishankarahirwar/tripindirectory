package directory.tripin.com.tripindirectory.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.activity.PartnerDetailScrollingActivity;
import directory.tripin.com.tripindirectory.callback.OnDataLoadListner;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.helper.RecyclerViewAnimator;
import directory.tripin.com.tripindirectory.model.ContactPersonPojo;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;
import directory.tripin.com.tripindirectory.utils.Param;
import directory.tripin.com.tripindirectory.utils.TextUtils;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 18-03-2018
 */

public class PartnerAdapter extends FirestoreRecyclerAdapter<PartnerInfoPojo, RecyclerView.ViewHolder> {
    private static final int SHOW_RESULT_VIEW = 0;
    private static final int PARTNER_VIEW = 1;
    private Context mContext;
    private TextUtils mTextUtils;
    private RecyclerViewAnimator mAnimator;
    private FirebaseAnalytics mFirebaseAnalytics;
    private OnDataLoadListner mOnDataLoadListner;

    public PartnerAdapter(Context context,RecyclerViewAnimator animator, FirestoreRecyclerOptions<PartnerInfoPojo> options, OnDataLoadListner onDataLoadListner) {
        super(options);
        mContext = context;
        mTextUtils = new TextUtils();
        mAnimator = animator;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        mOnDataLoadListner = onDataLoadListner;
    }

    @Override
    protected void onBindViewHolder(final RecyclerView.ViewHolder holder, int position, final PartnerInfoPojo model) {

        switch (holder.getItemViewType()) {
            case SHOW_RESULT_VIEW: {
                FirstItemMainViewHolder firstItemMainViewHolder = (FirstItemMainViewHolder) holder;
                break;
            }
            case PARTNER_VIEW: {
                PartnersViewHolder partnersViewHolder = (PartnersViewHolder) holder;

                //set address

                String addresstoset = "";
                if(model != null && model.getmCompanyAdderss() != null) {
                    addresstoset = model.getmCompanyAdderss().getAddress()
                            + ", " + mTextUtils.toTitleCase(model.getmCompanyAdderss().getCity())
                            + ", " + mTextUtils.toTitleCase(model.getmCompanyAdderss().getState());
                }
                if (model.getmCompanyAdderss() != null && model.getmCompanyAdderss().getPincode() != null) {
                    addresstoset = addresstoset + ", " + model.getmCompanyAdderss().getPincode();
                }
                partnersViewHolder.mAddress.setText(addresstoset);

                int fleetSize = 0;
                if(model.getVehicles() != null) {
                    fleetSize = model.getVehicles().size();
                }
                partnersViewHolder.mFleetSize.setText(String.valueOf(fleetSize));

                ((PartnersViewHolder) holder).mShareCompany.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String companyName = model.getmCompanyName();
                        //set address
                        String addresstoset
                                = model.getmCompanyAdderss().getAddress()
                                + ", " + mTextUtils.toTitleCase(model.getmCompanyAdderss().getCity())
                                + ", " + mTextUtils.toTitleCase(model.getmCompanyAdderss().getState());
                        if (model.getmCompanyAdderss().getPincode() != null) {
                            addresstoset = addresstoset + ", " + model.getmCompanyAdderss().getPincode();
                        }

                        shareMesssages(mContext, companyName, addresstoset);
                    }
                });

                if(model.getmCompanyName() != null) {
                    partnersViewHolder.mCompany.setText(mTextUtils.toTitleCase(model.getmCompanyName()));
                }

                if (model.getmAccountStatus() >= 2) {
                    partnersViewHolder.mCompany
                            .setCompoundDrawablesWithIntrinsicBounds(ContextCompat
                                            .getDrawable(mContext,
                                                    R.drawable.checkmark),
                                    null,
                                    null,
                                    null);
                } else {
                    partnersViewHolder.mCompany
                            .setCompoundDrawablesWithIntrinsicBounds(ContextCompat
                                            .getDrawable(mContext,
                                                    R.drawable.ic_fiber_manual_record_black_24dp),
                                    null,
                                    null,
                                    null);
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                        startPartnerDetailActivity(model.getmCompanyName(), snapshot.getId());
                    }
                });

                if(model.getmNatureOfBusiness() != null) {
                    String natureOfBusiness = "";
                    for (Map.Entry<String, Boolean> entry : model.getmNatureOfBusiness().entrySet()) {
                        if (entry.getValue()) {
                            natureOfBusiness += mTextUtils.toTitleCase(entry.getKey()) + ", ";
                        }
                    }
                    partnersViewHolder.mNatureOfBusiness.setText(natureOfBusiness);
                }

                if(model.getmTypesOfServices() != null) {
                    String typeOfServices = "";
                    for (Map.Entry<String, Boolean> entry : model.getmTypesOfServices().entrySet()) {
                        if (entry.getValue()) {
                            typeOfServices += mTextUtils.toTitleCase(entry.getKey()) + ", ";
                        }
                    }
                    partnersViewHolder.mTypeOfServices.setText(typeOfServices);
                }

                if(model.getFleetVehicle() != null) {
                    String fleet = "";
                    for (Map.Entry<String, Boolean> entry : model.getFleetVehicle().entrySet()) {
                        if (entry.getValue()) {
                            fleet += mTextUtils.toTitleCase(entry.getKey()) + ", ";
                        }
                    }
                    partnersViewHolder.mFleetInfo.setText(fleet);
                }

                String sourceCities = "";
                String destinationCities = "";
                String route = "";
                if(model.getmSourceCities() != null) {
                    for (Map.Entry<String, Boolean> entry : model.getmSourceCities().entrySet()) {
                        if (entry.getValue()) {
                            sourceCities += mTextUtils.toTitleCase(entry.getKey());
                            break;
                        }
                    }
                }

                if(model.getmDestinationCities() != null) {
                    for (Map.Entry<String, Boolean> entry : model.getmDestinationCities().entrySet()) {
                        if (entry.getValue()) {
                            destinationCities += mTextUtils.toTitleCase(entry.getKey());
                            break;
                        }
                    }
                }
                if(sourceCities.length() > 1 && destinationCities.length() > 1) {
                    route = sourceCities + " To " + destinationCities + " ..etc";
                } else if (sourceCities.length() > 1) {
                    route = sourceCities;
                } else if (destinationCities.length() > 0) {
                    route = destinationCities;
                } else {
                    route = "Route Info Not available";
                }
                partnersViewHolder.mRouteInfo.setText(route);

                if(model.getmLastActiveTime() != null) {
                    SimpleDateFormat dt = new SimpleDateFormat("MM/dd hh:mm");
                    Date date = model.getmLastActiveTime();

                    String DisplayDate = dt.format(date);
                    partnersViewHolder.mLastActiveTime.setText(DisplayDate);
                } else {
                    partnersViewHolder.mLastActiveTime.setVisibility(View.GONE);
                }

                partnersViewHolder.mCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Bundle params = new Bundle();
                        params.putString("call", "Click");
                        mFirebaseAnalytics.logEvent("ClickOnCall", params);

                        final ArrayList<String> phoneNumbers = new ArrayList<>();
                        List<ContactPersonPojo> contactPersonPojos = model.getmContactPersonsList();

                        if (contactPersonPojos != null && contactPersonPojos.size() > 1) {
                            for (int i = 0; i < contactPersonPojos.size(); i++) {
                                if (model.getmContactPersonsList().get(i) != null) {
                                    String number = model.getmContactPersonsList().get(i).getGetmContactPersonMobile();
                                    phoneNumbers.add(number);

                                }
                            }

                            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("Looks like there are multiple phone numbers.")
                                    .setCancelable(false)
                                    .setAdapter(new ArrayAdapter<String>(mContext, R.layout.dialog_multiple_no_row, R.id.dialog_number, phoneNumbers),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int item) {

                                                    Logger.v("Dialog number selected :" + phoneNumbers.get(item));

                                                    callNumber(phoneNumbers.get(item));
                                                }
                                            });

                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                            builder.create();
                            builder.show();


                        } else {

                            String number = model.getmContactPersonsList().get(0).getGetmContactPersonMobile();
                            callNumber(number);
                        }
                    }
                });
                mAnimator.onBindViewHolder(holder.itemView, position);
                break;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        switch (viewType) {
            case SHOW_RESULT_VIEW : {
                //first item
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_first_element_main, group, false);
                mAnimator.onCreateViewHolder(view);
                return new FirstItemMainViewHolder(view);
            }
            case PARTNER_VIEW : {
                //regular item
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.single_partner_row1, group, false);
                mAnimator.onCreateViewHolder(view);
                return new PartnersViewHolder(view);
            }
            default : {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.single_partner_row1, group, false);
                mAnimator.onCreateViewHolder(view);
                return new PartnersViewHolder(view);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == -1) {
            return SHOW_RESULT_VIEW;
        } else {
            return PARTNER_VIEW;
        }
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        Logger.v("on Data changed");
        mOnDataLoadListner.onDataLoaded(getItemCount());
    }


    private void startPartnerDetailActivity(String companyName, String userId) {
        Intent intent = new Intent(mContext, PartnerDetailScrollingActivity.class);
        intent.putExtra(Param.Extra.USER_ID, userId);
        intent.putExtra(Param.Extra.COMPANY_NAME, companyName);
        mContext.startActivity(intent);
    }

    private void callNumber(String number) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + Uri.encode(number.trim())));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(callIntent);
    }

    private void shareMesssages(Context context, String subject, String body) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            shareIntent.putExtra(Intent.EXTRA_TEXT, subject + "\n" + body + "\n" + " Share By: Indian Logistics Network \n" + "http://bit.ly/ILNAPPS");
            mContext.startActivity(Intent.createChooser(shareIntent, "Share via"));
        }
        catch (ActivityNotFoundException exception) {
            Toast.makeText(mContext, "No application found for send Email" , Toast.LENGTH_LONG).show();
        }
    }
}
