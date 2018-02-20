package directory.tripin.com.tripindirectory.LoadBoardActivities.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import directory.tripin.com.tripindirectory.LoadBoardActivities.models.FleetPostPojo;
import directory.tripin.com.tripindirectory.LoadBoardActivities.models.FleetPostViewHolder;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.helper.RecyclerViewAnimator;
import directory.tripin.com.tripindirectory.utils.TextUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class FleetsFragment extends Fragment {

    public RecyclerView mFleetssList;
    public TextUtils textUtils;
    FirestoreRecyclerAdapter adapter;
    private RecyclerViewAnimator mAnimator;
    public FleetsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fleets, container, false);
        mFleetssList = v.findViewById(R.id.rv_fleets);
        mAnimator = new RecyclerViewAnimator(mFleetssList);

        LinearLayoutManager mLayoutManager =  new LinearLayoutManager(getActivity());
        //mLayoutManager.setStackFromEnd(true);
        mFleetssList.setLayoutManager(mLayoutManager);

        mFleetssList.setAdapter(adapter);


        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textUtils = new TextUtils();

        Query query = FirebaseFirestore.getInstance()
                .collection("fleets")
                .orderBy("mTimeStamp", Query.Direction.DESCENDING)
                .limit(50);
        FirestoreRecyclerOptions<FleetPostPojo> options = new FirestoreRecyclerOptions.Builder<FleetPostPojo>()
                .setQuery(query, FleetPostPojo.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<FleetPostPojo, FleetPostViewHolder>(options) {
            @Override
            public void onBindViewHolder(FleetPostViewHolder holder, int position, FleetPostPojo fleetPostPojo) {

                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                String DisplayDate = sdf.format(fleetPostPojo.getmPickUpTimeStamp());
                holder.mScheduledDate.setText("Scheduled Date : "+DisplayDate+" ("+gettimeDiff(fleetPostPojo.getmPickUpTimeStamp())+" days left)");

                if(!fleetPostPojo.getmCompanyName().isEmpty()){
                    holder.mPostTitle.setText(textUtils.toTitleCase(fleetPostPojo.getmCompanyName()));
                }else {
                    holder.mPostTitle.setText(fleetPostPojo.getmRMN());
                }

                holder.mSource.setText(fleetPostPojo.getmSourceCity());
                holder.mDestination.setText(fleetPostPojo.getmDestinationCity());
                holder.mDistance.setText(fleetPostPojo.getmEstimatedDistance()+"\nkm");

                String loadProperties = fleetPostPojo.getmVehicleNumber();
                holder.mLoadProperties.setText(loadProperties);
                if(loadProperties.length()>20){
                    holder.mLoadProperties.setSelected(true);
                }

                String fleetProperties = textUtils.toTitleCase(fleetPostPojo.getmVehicleType())
                        +", "+textUtils.toTitleCase(fleetPostPojo.getmBodyType())
                        +", "+textUtils.toTitleCase(fleetPostPojo.getmFleetPayLoad())+"MT, "
                        +textUtils.toTitleCase(fleetPostPojo.getmFleetLength())+"Ft";
                holder.mFleetProperties.setText(fleetProperties);
                if(fleetProperties.length()>20){
                    holder.mFleetProperties.setSelected(true);
                }

                if(!fleetPostPojo.getmPersonalNote().isEmpty()){
                    holder.mPersonalNote.setText("\""+fleetPostPojo.getmPersonalNote()+"\"");
                }else {
                    holder.mPersonalNote.setVisibility(View.GONE);
                }

                Date postingDate = fleetPostPojo.getmTimeStamp();

                holder.mPostSubTitle.setText("Posted "+gettimeDiff(postingDate)+" . Need Load");
                mAnimator.onBindViewHolder(holder.itemView, position);

            }

            @Override
            public FleetPostViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_posted_fleet, group, false);
                mAnimator.onCreateViewHolder(view);

                return new FleetPostViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
            }
        };
    }
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public String gettimeDiff(Date startDate){

        String diff = "" ;

        if(startDate!=null){

            Date endDate = new Date();

            long duration  = endDate.getTime() - startDate.getTime();
            if(duration<0){
                return Math.abs(duration) / (1000 * 60 * 60 * 24)+"";
            }
            long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
            long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);

            if(diffInSeconds==0){
                return "Realtime!";
            }

            if(diffInSeconds<60){
                diff = ""+diffInSeconds+" sec ago";
            }else if(diffInMinutes<60){
                diff = ""+diffInMinutes+" min ago";
            }else if(diffInHours<24){
                diff = ""+diffInHours+" hrs ago";
            }else {

                long daysago = duration / (1000 * 60 * 60 * 24);
                diff = ""+daysago+" days ago";
            }

        }
        return diff;

    }

}
