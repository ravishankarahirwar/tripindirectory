package directory.tripin.com.tripindirectory.newprofiles.activities;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import directory.tripin.com.tripindirectory.R;

public class RateHolder extends RecyclerView.ViewHolder{

    public TextView username;
    public TextView timestamp;
    public TextView rmn;
    public TextView ratings;
    public TextView comment;
    public ImageView thubmnail;


    public RateHolder(View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.username);
        ratings = itemView.findViewById(R.id.ratingsgiven);
        timestamp = itemView.findViewById(R.id.time);
        comment = itemView.findViewById(R.id.review);
        rmn = itemView.findViewById(R.id.rmnofrater);
        thubmnail = itemView.findViewById(R.id.thumbrateuser);
    }
}
