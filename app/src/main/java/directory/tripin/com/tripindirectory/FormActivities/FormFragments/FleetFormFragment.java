package directory.tripin.com.tripindirectory.FormActivities.FormFragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import directory.tripin.com.tripindirectory.FormActivities.FleetViewHolder;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.model.Driver;
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

        adapterp = new FleetAdapter(mContext, mVehicles, 1);


        auth = FirebaseAuth.getInstance();
        mUserDocRef = FirebaseFirestore.getInstance()
                .collection("partners").document(auth.getUid());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        isDataFatched = false;
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
                adapterp.notifyDataSetChanged();
            }
        });

        fetchUserData();
        return rootView;

    }

    public void fetchUserData() {
        //get the updated partner pojo and set all fields if not null
        auth = FirebaseAuth.getInstance();
        mUserDocRef = FirebaseFirestore.getInstance()
                .collection("partners").document(auth.getUid());
        Logger.v("Fetching Data");
        //mLoadingDataLin.setVisibility(View.VISIBLE);
        mUserDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                isDataFatched = true;
                if (task.isSuccessful() && task.getResult().exists()) {
                    partnerInfoPojo = task.getResult().toObject(PartnerInfoPojo.class);
                    if(partnerInfoPojo.getVehicles()!=null){
                        mVehicles.clear();
                        mVehicles.addAll(partnerInfoPojo.getVehicles());
                        adapterp.setDataValues(mVehicles);
                        Logger.v("Fetch and Set Fleet");

                    }
                   Logger.v("On Data Fetch and set Company Data");
                }
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        if(partnerInfoPojo != null && isDataFatched) {
//            mVehicles.clear();
            //send the modified data to parent activity
             List<Vehicle> mVehiclesSend = new ArrayList<>();

            for(int i=0; i < mVehicles.size(); i++){

                View itemView = mVechileList.getLayoutManager().findViewByPosition(i);
                if(itemView != null) {
                    Switch turckAva = (Switch) itemView.findViewById(R.id.is_available);

                    Spinner vehicleType = (Spinner) itemView.findViewById(R.id.vehicle_type);
                    Spinner bodyType = (Spinner) itemView.findViewById(R.id.body_type);

                    TextInputEditText vechcleNumber = (TextInputEditText) itemView.findViewById(R.id.input_vechicle_number);
                    TextInputEditText payload = (TextInputEditText) itemView.findViewById(R.id.input_payload);
                    TextInputEditText length = (TextInputEditText) itemView.findViewById(R.id.input_length);
                    TextInputEditText driverName = (TextInputEditText) itemView.findViewById(R.id.input_driver_name);
                    TextInputEditText driverNumber = (TextInputEditText) itemView.findViewById(R.id.input_driver_number);

                    boolean isAvailable = turckAva.isChecked();
                    String vehicleTypeString = vehicleType.getSelectedItem().toString();
                    String bodyTypeString = bodyType.getSelectedItem().toString();

                    String vechileNo = vechcleNumber.getText().toString();
                    String truckPayload = payload.getText().toString();
                    String truckLength = length.getText().toString();
                    String driverNameString = driverName.getText().toString();
                    String driverNo = driverNumber.getText().toString();

                    Vehicle vehicle = new Vehicle();
                    vehicle.setAvailable(isAvailable);
                    vehicle.setType(vehicleTypeString);
                    vehicle.setBodyType(bodyTypeString);
                    vehicle.setNumber(vechileNo);
                    vehicle.setPayload(truckPayload);
                    vehicle.setLength(truckLength);
                    Driver driver = new Driver();
                    driver.setName(driverNameString);
                    driver.setNumber(driverNo);
                    vehicle.setDriver(driver);
                    vehicle.setNumber(vechileNo);
                    mVehiclesSend.add(vehicle);
                    Logger.v("Fetch Set Fleet");
                }
            }

            Map<String, List<Vehicle>> data = new HashMap<>();
            data.put("vehicles", mVehiclesSend);
            mUserDocRef.set(data, SetOptions.merge());

        }

    }

    public class FleetAdapter extends RecyclerView.Adapter<FleetViewHolder> {

        private List<Vehicle> mDataValues;


        private int getDataValuesSize() {
           return mDataValues.size();
        }

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
        public FleetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fleet, parent, false);
            FleetViewHolder viewHolder = new FleetViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final FleetViewHolder holder, final int position) {
            holder.setDataValue(mDataValues.get(position));
            holder.onBind(mContext, holder);


            holder.vehicleRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mVehicles.size() > 0) {
                        mVehicles.remove(position);
                        setDataValues(mVehicles);
                    }
                }
            });
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mDataValues.size();

        }
    }


}

