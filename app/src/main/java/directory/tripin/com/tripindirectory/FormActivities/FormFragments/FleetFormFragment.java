package directory.tripin.com.tripindirectory.FormActivities.FormFragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import directory.tripin.com.tripindirectory.FormActivities.PlacesViewHolder1;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.model.ContactPersonPojo;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;
import directory.tripin.com.tripindirectory.model.response.Vehicle;

/**
 * A simple {@link Fragment} subclass.
 */
public class FleetFormFragment extends BaseFragment {


    private Query query;
    private FirebaseAuth auth;
    private DocumentReference mUserDocRef;
    private FirestoreRecyclerOptions<PartnerInfoPojo> options;
    private FleetAdapter adapterp;
    private List<Vehicle> mVehicles;

    private Context mContext;
    private RecyclerView mVechileList;
    private TextView mAddVechile;
    private PartnerInfoPojo partnerInfoPojo;

    public FleetFormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onUpdate(PartnerInfoPojo partnerInfoPojo) {


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
//        Vehicle vehicle = new Vehicle();
//        Vehicle vehicle1 = new Vehicle();
//
//        mVehicles.add(vehicle);
//        mVehicles.add(vehicle1);

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
        mVechileList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mVechileList.setAdapter(adapterp);

        mAddVechile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vehicle vehicle = new Vehicle();
                mVehicles.add(vehicle);
                adapterp.notifyDataSetChanged();
            }
        });

        return rootView;

    }

    public void FetchUserData() {
        //get the updated partner pojo and set all fields if not null
        auth = FirebaseAuth.getInstance();
        mUserDocRef = FirebaseFirestore.getInstance()
                .collection("partners").document(auth.getUid());
        Logger.v("Fetching Data");
        //mLoadingDataLin.setVisibility(View.VISIBLE);
        mUserDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    partnerInfoPojo = task.getResult().toObject(PartnerInfoPojo.class);

                    if(partnerInfoPojo.getVehicles()!=null){
                        mVehicles.clear();
                        mVehicles.addAll(partnerInfoPojo.getVehicles());
                    }
                   Logger.v("On Data Fetch and set Company Data");
                }
            }
        });

    }

    public class FleetAdapter extends RecyclerView.Adapter<PlacesViewHolder1> {

        private List<Vehicle> mDataValues;

        // data is passed into the constructor
        public FleetAdapter(Context context, List<Vehicle> data, int type) {
            this.mDataValues = data;

        }

        private void setDataValues(List<Vehicle> dataValues) {
            mDataValues = dataValues;
            this.notifyDataSetChanged();
        }

        // inflates the row layout from xml when needed
        @Override
        public PlacesViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fleet, parent, false);
            PlacesViewHolder1 viewHolder = new PlacesViewHolder1(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final PlacesViewHolder1 holder, final int position) {
            holder.onBind(mContext, holder);

        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mDataValues.size();

        }


    }


}

