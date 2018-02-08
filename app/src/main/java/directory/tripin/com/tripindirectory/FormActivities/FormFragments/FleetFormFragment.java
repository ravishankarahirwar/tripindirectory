package directory.tripin.com.tripindirectory.FormActivities.FormFragments;


import android.content.Context;
import android.content.Intent;
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

import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import directory.tripin.com.tripindirectory.FormActivities.FleetViewHolder;
import directory.tripin.com.tripindirectory.FormActivities.WorkingWithHolderNew;
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
    private WorkingWithAdapter mWorkingWithAdapter;

    private List<Vehicle> mVehicles;
    private Set<String> mWorkingWithVehicle;
    private HashMap<String,Boolean> mWorkingWith;
    private Context mContext;
    private RecyclerView mVechileList;
    private RecyclerView mVechilWorkingWithList;
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
        mWorkingWith = new HashMap<>();

        mWorkingWithVehicle = new HashSet<>();
        adapterp = new FleetAdapter(mContext, mVehicles, 1);

//        mWorkingWithVehicle.add("LCV");
//        mWorkingWithVehicle.add("Truck");
//        mWorkingWithVehicle.add("Tusker");
//        mWorkingWithVehicle.add("Trailers");
//        mWorkingWithVehicle.add("Container");
//        mWorkingWithVehicle.add("Refrigerated");
//        mWorkingWithVehicle.add("Tankers");
//        mWorkingWithVehicle.add("Tippers");
//        mWorkingWithVehicle.add("Bulkers");
//        mWorkingWithVehicle.add("Car Carriers");
//        mWorkingWithVehicle.add("Scooter Body");
//        mWorkingWithVehicle.add("Hydraulic Axles");

        mWorkingWith.put("LCV",false);
        mWorkingWith.put("Truck",false);
        mWorkingWith.put("Trailers",false);
        mWorkingWith.put("Container",false);
        mWorkingWith.put("Refrigerated",false);
        mWorkingWith.put("Tankers",false);
        mWorkingWith.put("Tippers",false);
        mWorkingWith.put("Bulkers",false);
        mWorkingWith.put("Car Carriers",false);
        mWorkingWith.put("Scooter Body",false);
        mWorkingWith.put("Hydraulic Axles",false);

        mWorkingWithVehicle = mWorkingWith.keySet();

        mWorkingWithAdapter = new WorkingWithAdapter(mContext, mWorkingWith, 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        isDataFatched = false;
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_fleet_form, container, false);
        mAddVechile = rootView.findViewById(R.id.add_vehicle);

        mVechileList = rootView.findViewById(R.id.vehicle_list);
        mVechilWorkingWithList = rootView.findViewById(R.id.working_with_vehicle_list);
        mVechileList.setLayoutManager(new LinearLayoutManager(mContext));
        mVechilWorkingWithList.setLayoutManager(new LinearLayoutManager(mContext));

        mVechileList.setAdapter(adapterp);
        mVechilWorkingWithList.setAdapter(mWorkingWithAdapter);
        mAddVechile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Vehicle> vehicleInAdaptor = getAllItemFromAdapter();
                mVehicles.clear();
                mVehicles.addAll(vehicleInAdaptor);
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

    private  List<Vehicle> getAllItemFromAdapter() {
        int totalItemInAdaptor = mVechileList.getLayoutManager().getItemCount();
        List<Vehicle> mVehiclesSend = new ArrayList<>();
        for(int i=0; i < totalItemInAdaptor; i++){

            View itemView = mVechileList.getLayoutManager().findViewByPosition(i);
            if(itemView != null) {
                Switch turckAva = itemView.findViewById(R.id.is_available);

                Spinner vehicleType = itemView.findViewById(R.id.vehicle_type);
                Spinner bodyType = itemView.findViewById(R.id.body_type);

                TextInputEditText vechcleNumber = itemView.findViewById(R.id.input_vechicle_number);
                TextInputEditText payload = itemView.findViewById(R.id.input_payload);
                TextInputEditText length = itemView.findViewById(R.id.input_length);
                TextInputEditText driverName = itemView.findViewById(R.id.input_driver_name);
                TextInputEditText driverNumber = itemView.findViewById(R.id.input_driver_number);

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
        return mVehiclesSend;
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
                    Switch turckAva = itemView.findViewById(R.id.is_available);

                    Spinner vehicleType = itemView.findViewById(R.id.vehicle_type);
                    Spinner bodyType = itemView.findViewById(R.id.body_type);

                    TextInputEditText vechcleNumber = itemView.findViewById(R.id.input_vechicle_number);
                    TextInputEditText payload = itemView.findViewById(R.id.input_payload);
                    TextInputEditText length = itemView.findViewById(R.id.input_length);
                    TextInputEditText driverName = itemView.findViewById(R.id.input_driver_name);
                    TextInputEditText driverNumber = itemView.findViewById(R.id.input_driver_number);

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

            HashMap<String,Boolean> mFilters = new HashMap<>();
            for(int i=0; i < mWorkingWithVehicle.size(); i++){
                View itemView = mVechilWorkingWithList.getLayoutManager().findViewByPosition(i);
                if(itemView != null) {
                    CheckBox turckAva = itemView.findViewById(R.id.i_have);
                    TextView vehicleType = itemView.findViewById(R.id.truck_type);

                    if(turckAva != null && vehicleType != null) {
                        boolean value = turckAva.isChecked();
                        String key = vehicleType.getText().toString().trim();
                        mFilters.put(key, value);
                    }
                }
            }

            Map<String, List<Vehicle>> data = new HashMap<>();
            data.put("vehicles", mVehiclesSend);
            mUserDocRef.set(data, SetOptions.merge());

//            HashMap<String,Boolean> mFilters = new HashMap<>();
            for(Vehicle v : mVehiclesSend){
                mFilters.put(v.getBodyType().toUpperCase(),true);
                mFilters.put(v.getType().toUpperCase(),true);

                if(!v.getLength().isEmpty()){
                    if(Integer.parseInt(v.getLength())>=0&&Integer.parseInt(v.getLength())<10){
                        mFilters.put("Between 0-10 Ft".toUpperCase(),true);
                    }else if(Integer.parseInt(v.getLength())>=10&&Integer.parseInt(v.getLength())<20){
                        mFilters.put("Between 10-20 Ft".toUpperCase(),true);
                    }else if(Integer.parseInt(v.getLength())>=20&&Integer.parseInt(v.getLength())<30){
                        mFilters.put("Between 20-30 Ft".toUpperCase(),true);
                    }else {
                        mFilters.put("Above 30 Ft".toUpperCase(),true);
                    }
                }

                if(!v.getPayload().isEmpty()){
                    if(Integer.parseInt(v.getPayload())>=0&&Integer.parseInt(v.getPayload())<10){
                        mFilters.put("Between 0-10 MT".toUpperCase(),true);
                    }else if(Integer.parseInt(v.getPayload())>=10&&Integer.parseInt(v.getPayload())<20){
                        mFilters.put("Between 10-20 MT".toUpperCase(),true);
                    }else if(Integer.parseInt(v.getPayload())>=20&&Integer.parseInt(v.getPayload())<30){
                        mFilters.put("Between 20-30 MT".toUpperCase(),true);
                    }else {
                        mFilters.put("Above 30 MT".toUpperCase(),true);
                    }
                }
            }

            mUserDocRef.update("mFiltersVehicle",mFilters);
        }

    }

    public class WorkingWithAdapter extends RecyclerView.Adapter<WorkingWithHolderNew> {
        private  HashMap<String,Boolean> mDataValues;

        private int getDataValuesSize() {
            return mDataValues.size();
        }

        // data is passed into the constructor
        public WorkingWithAdapter(Context context, HashMap<String,Boolean> data, int type) {
            this.mDataValues = data;

        }

        private void setDataValues(HashMap<String,Boolean> dataValues) {
            mDataValues = dataValues;
            this.notifyDataSetChanged();
        }

        // inflates the row layout from xml when needed
        @Override
        public WorkingWithHolderNew onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_working_with_vehicle, parent, false);
            WorkingWithHolderNew viewHolder = new WorkingWithHolderNew(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final WorkingWithHolderNew holder, final int position) {
            String key = mDataValues.keySet().toArray()[position].toString();
            boolean value = mDataValues.get(key);
            holder.setDataValue(key);
            holder.onBind(mContext, holder);
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mDataValues.size();

        }
    }
//**************************************
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

            holder.vehicleShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(position >= 0) {
                        Vehicle dataValue = mDataValues.get(position);
                        shareTruck(dataValue);
                    } else {

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


    private void shareTruck(Vehicle dataValue) {
        try {
            if(dataValue != null) {
                String vehicleNumber = dataValue.getNumber();
                String vehiclePayload = dataValue.getPayload();
                String vehicleLength = dataValue.getLength();
                String vehicleDriverName = dataValue.getDriver().getName();
                String vehicleDriverNumber = dataValue.getDriver().getNumber();
                String vehicleTypeString  = dataValue.getType();
                String bodyTypeString = dataValue.getBodyType();

                String truckSharingInfo = "*Vehicle Information:* \n"
                        + "Vehicle Number: " + vehicleNumber +"\n"
                        + "Vehicle Type: " + vehicleTypeString +"\n"
                        + "Vehicle Body: " + bodyTypeString +"\n"
                        + "Vehicle PayLoad: " + vehiclePayload +"\n"
                        + "Vehicle Length: " + vehicleLength +"\n"
                        + "*Driver Information:*\n"
                        + "Driver Name: " + vehicleDriverName +"\n"
                        + "Driver Number: " + vehicleDriverNumber +"\n";

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Indian Logistic Network");
                i.putExtra(Intent.EXTRA_TEXT, truckSharingInfo);
                startActivity(Intent.createChooser(i, "Share by INL"));
            } else {
                Toast.makeText(mContext, "There is no data available for this vehicle", Toast.LENGTH_SHORT);
            }
        } catch(Exception e) {
            //e.toString();
        }
    }

}

