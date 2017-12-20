package directory.tripin.com.tripindirectory.FormActivities.FormFragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import directory.tripin.com.tripindirectory.FormActivities.PlacesViewHolder;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;

/**
 * A simple {@link Fragment} subclass.
 */
public class FleetFormFragment extends BaseFragment {

    RouteFormFragment.OnPickUpPlace onPickUpPlace;
    Query query;
    FirebaseAuth auth;
    DocumentReference mUserDocRef;
    FirestoreRecyclerOptions<PartnerInfoPojo> options;
    FleetAdapter adapterp;
    List<String> listpickup, listdropoff;
    Map<String, Boolean> pickupHM;
    Map<String, Boolean> dropoffHM;
    Activity activity;
    View v;
    private TextView addPickUpCity, addDropOffCity;
    private Context mContext;
    private RecyclerView mVechileList;

    @Override
    public void onUpdate(PartnerInfoPojo partnerInfoPojo) {

        Logger.v("ONUPDATE");
        if (partnerInfoPojo.getmSourceCities() != null) {
            listpickup.clear();
            pickupHM = partnerInfoPojo.getmSourceCities();
            listpickup.addAll(pickupHM.keySet());
            mVechileList.setAdapter(adapterp);
            adapterp.notifyDataSetChanged();
            Log.e("onEvent 1",partnerInfoPojo.getmSourceCities().toString());

        }else {
            Logger.v("sourcenull");
        }
        if (partnerInfoPojo.getDestinationCities() != null) {
            listdropoff.clear();
            dropoffHM = partnerInfoPojo.getDestinationCities();
            listdropoff.addAll(dropoffHM.keySet());

        }else {
            Logger.v("detination null");

        }
    }

    public FleetFormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onPickUpPlace = (RouteFormFragment.OnPickUpPlace) activity;
            this.mContext = activity.getApplicationContext();
            this.activity = activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnItemClickedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listpickup = new ArrayList<>();
        listdropoff = new ArrayList<>();
        pickupHM = new HashMap<>();
        dropoffHM = new HashMap<>();

        adapterp = new FleetAdapter(activity, listpickup, 1);

        auth = FirebaseAuth.getInstance();
        mUserDocRef = FirebaseFirestore.getInstance()
                .collection("partners").document(auth.getUid());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_fleet_form, container, false);
        addPickUpCity = v.findViewById(R.id.add_vehicle);
        mVechileList = v.findViewById(R.id.vehicle_list);
        mVechileList.setLayoutManager(new LinearLayoutManager(activity));

        return v;
    }


    public class FleetAdapter extends RecyclerView.Adapter<PlacesViewHolder> {

        private List<String> mData;
        private int type = 0;

        // data is passed into the constructor
        public FleetAdapter(Context context, List<String> data, int type) {
            this.mData = data;
            this.type = type;
        }

        // inflates the row layout from xml when needed
        @Override
        public PlacesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fleet, parent, false);
            PlacesViewHolder viewHolder = new PlacesViewHolder(view);
            return viewHolder;
        }

        // binds the data to the textview in each row
        @Override
        public void onBindViewHolder(final PlacesViewHolder holder, final int position) {
            String city = mData.get(position);
            holder.mCity.setText(city);
            holder.mRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.mCity.setText("Removing...");
                    //remove city
                    if (type == 1) {
                        //remove pickup
                        mUserDocRef.update("mSourceCities." + mData.get(position), FieldValue.delete()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                notifyDataSetChanged();
                            }
                        });
                    }
                }
            });
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mData.size();
        }


    }


}

