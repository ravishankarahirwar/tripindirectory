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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import directory.tripin.com.tripindirectory.LoadBoardActivities.models.LoadPostPojo;
import directory.tripin.com.tripindirectory.LoadBoardActivities.models.LoadPostViewHolder;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.helper.RecyclerViewAnimator;
import directory.tripin.com.tripindirectory.utils.TextUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoadsFragment extends Fragment {


    public RecyclerView mLoadsList;
    public TextUtils textUtils;
    FirestoreRecyclerAdapter adapter;
    private RecyclerViewAnimator mAnimator;


    public LoadsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_loads, container, false);
        mLoadsList = v.findViewById(R.id.rv_loads);
        mAnimator = new RecyclerViewAnimator(mLoadsList);

        LinearLayoutManager mLayoutManager =  new LinearLayoutManager(getActivity());
        //mLayoutManager.setStackFromEnd(true);
        mLoadsList.setLayoutManager(mLayoutManager);

        mLoadsList.setAdapter(adapter);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textUtils = new TextUtils();

        Query query = FirebaseFirestore.getInstance()
                .collection("loads")
                .orderBy("mTimeStamp", Query.Direction.DESCENDING)
                .limit(50);
        FirestoreRecyclerOptions<LoadPostPojo> options = new FirestoreRecyclerOptions.Builder<LoadPostPojo>()
                .setQuery(query, LoadPostPojo.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<LoadPostPojo, LoadPostViewHolder>(options) {
            @Override
            public void onBindViewHolder(LoadPostViewHolder holder, int position, LoadPostPojo loadPostPojo) {

                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                String DisplayDate = sdf.format(loadPostPojo.getmPickUpTimeStamp());
                holder.mScheduledDate.setText("Scheduled Date : "+DisplayDate+" ("+gettimeDiff(loadPostPojo.getmPickUpTimeStamp())+" days left)");

                if(!loadPostPojo.getmCompanyName().isEmpty()){
                    holder.mPostTitle.setText(textUtils.toTitleCase(loadPostPojo.getmCompanyName()));
                }else {
                    holder.mPostTitle.setText(loadPostPojo.getmRMN());
                }

                holder.mSource.setText(loadPostPojo.getmSourceCity());
                holder.mDestination.setText(loadPostPojo.getmDestinationCity());
                holder.mDistance.setText(loadPostPojo.getmEstimatedDistance()+"\nkm");

                String loadProperties = textUtils.toTitleCase(loadPostPojo.getmLoadMaterial())
                        +", "+textUtils.toTitleCase(loadPostPojo.getmLoadWeight())+ "MT";
                holder.mLoadProperties.setText(" "+loadProperties);
                if(loadProperties.length()>20){
                    holder.mLoadProperties.setSelected(true);
                }

                String fleetProperties = textUtils.toTitleCase(loadPostPojo.getmVehicleTypeRequired())
                        +", "+textUtils.toTitleCase(loadPostPojo.getmBodyTypeRequired())
                        +", "+textUtils.toTitleCase(loadPostPojo.getmFleetPayLoadRequired())+"MT, "
                        +textUtils.toTitleCase(loadPostPojo.getmFleetLengthRequired())+"Ft";
                holder.mFleetProperties.setText(" "+fleetProperties);
                if(fleetProperties.length()>20){
                    holder.mFleetProperties.setSelected(true);
                }

                if(!loadPostPojo.getmPersonalNote().isEmpty()){
                    holder.mPersonalNote.setText("\""+loadPostPojo.getmPersonalNote()+"\"");
                }else {
                    holder.mPersonalNote.setVisibility(View.GONE);
                }

                Date postingDate = loadPostPojo.getmTimeStamp();

                holder.mPostSubTitle.setText("Posted "+gettimeDiff(postingDate)+" . Need Fleet");
                mAnimator.onBindViewHolder(holder.itemView, position);

            }

            @Override
            public LoadPostViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_posted_load, group, false);
                mAnimator.onCreateViewHolder(view);

                return new LoadPostViewHolder(view);
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
