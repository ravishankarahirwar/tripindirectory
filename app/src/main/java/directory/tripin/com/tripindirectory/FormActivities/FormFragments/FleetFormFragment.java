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
import android.widget.ArrayAdapter;
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
import directory.tripin.com.tripindirectory.model.response.Vehicle;

/**
 * A simple {@link Fragment} subclass.
 */
public class FleetFormFragment extends BaseFragment {

    Query query;
    FirebaseAuth auth;
    DocumentReference mUserDocRef;
    FirestoreRecyclerOptions<PartnerInfoPojo> options;
    FleetAdapter adapterp;
    List<Vehicle> mVehicles;

    private Context mContext;
    private RecyclerView mVechileList;
    private TextView mAddVechile;

    @Override
    public void onUpdate(PartnerInfoPojo partnerInfoPojo) {

    }

    public FleetFormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            this.mContext = activity.getApplicationContext();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnItemClickedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVehicles = new ArrayList<>();
        Vehicle vehicle = new Vehicle();
        Vehicle vehicle1 = new Vehicle();

        mVehicles.add(vehicle);
        mVehicles.add(vehicle1);

        adapterp = new FleetAdapter(mContext, mVehicles, 1);

        auth = FirebaseAuth.getInstance();
        mUserDocRef = FirebaseFirestore.getInstance()
                .collection("partners").document(auth.getUid());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_fleet_form, container, false);
        mAddVechile = rootView.findViewById(R.id.add_vehicle);

        mVechileList = rootView.findViewById(R.id.vehicle_list);
        mVechileList.setLayoutManager(new LinearLayoutManager(mContext));
        mVechileList.setAdapter(adapterp);

        mAddVechile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vehicle vehicle = new Vehicle();
                mVehicles.add(vehicle);
                adapterp.setDataValues(mVehicles);
            }
        });

        return rootView;
    }


    public class FleetAdapter extends RecyclerView.Adapter<PlacesViewHolder> {

        private List<Vehicle> mDataValues;
        private int type = 0;

        private void setDataValues(List<Vehicle> dataValues) {
            mDataValues = dataValues;
            notifyDataSetChanged();
        }

        // data is passed into the constructor
        public FleetAdapter(Context context, List<Vehicle> data, int type) {
            this.mDataValues = data;
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
            holder.onBind(mContext, holder);
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mDataValues.size();
        }


    }


}

