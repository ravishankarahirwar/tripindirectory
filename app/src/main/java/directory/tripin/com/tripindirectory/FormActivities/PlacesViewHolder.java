package directory.tripin.com.tripindirectory.FormActivities;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import directory.tripin.com.tripindirectory.R;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 21-12-2017
 */

public class PlacesViewHolder extends RecyclerView.ViewHolder{
    public ImageView mRemove;
    public TextView mCity;

    public boolean isUpvoteClicked;

    public PlacesViewHolder(View itemView) {
        super(itemView);
        mRemove = (ImageView) itemView.findViewById(R.id.place_remove);
        mCity = (TextView) itemView.findViewById(R.id.textViewcity);
    }
}
