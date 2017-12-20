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
public class RouteFormFragment extends BaseFragment {

    OnPickUpPlace onPickUpPlace;
    Query query;
    FirebaseAuth auth;
    DocumentReference mUserDocRef;
    FirestoreRecyclerOptions<PartnerInfoPojo> options;
    MyRecyclerViewAdapter adapterp, adapterd;
    List<String> listpickup, listdropoff;
    Map<String, Boolean> pickupHM;
    Map<String, Boolean> dropoffHM;
    Activity activity;
    View v;
    private TextView addPickUpCity, addDropOffCity;
    private Context mContext;
    private RecyclerView mPickUpList, mDropList;

    @Override
    public void onUpdate(PartnerInfoPojo partnerInfoPojo) {

        Logger.v("ONUPDATE");
        if (partnerInfoPojo.getmSourceCities() != null) {
            listpickup.clear();
            pickupHM = partnerInfoPojo.getmSourceCities();
            listpickup.addAll(pickupHM.keySet());
            mPickUpList.setAdapter(adapterp);
            adapterp.notifyDataSetChanged();
            Log.e("onEvent 1",partnerInfoPojo.getmSourceCities().toString());

        }else {
            Logger.v("sourcenull");
        }
        if (partnerInfoPojo.getDestinationCities() != null) {
            listdropoff.clear();
            dropoffHM = partnerInfoPojo.getDestinationCities();
            listdropoff.addAll(dropoffHM.keySet());
            mDropList.setAdapter(adapterd);
            adapterd.notifyDataSetChanged();
            Log.e("onEvent",partnerInfoPojo.getDestinationCities().toString());
        }else {
            Logger.v("detination null");

        }
    }

    public RouteFormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onPickUpPlace = (OnPickUpPlace) activity;
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

        adapterp = new MyRecyclerViewAdapter(activity, listpickup, 1);
        adapterd = new MyRecyclerViewAdapter(activity, listdropoff, 2);


        auth = FirebaseAuth.getInstance();
        mUserDocRef = FirebaseFirestore.getInstance()
                .collection("partners").document(auth.getUid());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_route_form, container, false);
        addPickUpCity = v.findViewById(R.id.add_pickupcity);
        addDropOffCity = v.findViewById(R.id.add_dropcity);
        mPickUpList = v.findViewById(R.id.pickup_list);
        mDropList = v.findViewById(R.id.drop_list);
        mPickUpList.setLayoutManager(new LinearLayoutManager(activity));
        mDropList.setLayoutManager(new LinearLayoutManager(activity));

        setListners();
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void setListners() {

        addPickUpCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickUpPlace.OnPickUpClicked(1);
            }
        });

        addDropOffCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickUpPlace.OnPickUpClicked(2);
            }
        });
    }

    public void addnewplace(Place place, int mPlaceCode) {

        if (place!= null) {
            if (mPlaceCode == 1) {
                pickupHM.put(place.getName().toString(), true);
                //upload pickup city
                mUserDocRef.update("mSourceCities", pickupHM).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(mContext, "Source City Added!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (mPlaceCode == 2) {
                //upload destination city
                dropoffHM.put(place.getName().toString(), true);
                mUserDocRef.update("mDestinationCities", dropoffHM).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(mContext, "Destination City Added!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(mContext, "Try Again!", Toast.LENGTH_SHORT).show();
        }


    }

    public interface OnPickUpPlace {
        public void OnPickUpClicked(int id);
    }

    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<PlacesViewHolder> {

        private List<String> mData;
        private int type = 0;

        // data is passed into the constructor
        public MyRecyclerViewAdapter(Context context, List<String> data, int type) {
            this.mData = data;
            this.type = type;
        }

        // inflates the row layout from xml when needed
        @Override
        public PlacesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_city, parent, false);
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
                    if (type == 2) {
                        //remove drop off
                        //remove pickup
                        mUserDocRef.update("mDestinationCities." + mData.get(position), FieldValue.delete()).addOnCompleteListener(new OnCompleteListener<Void>() {
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
