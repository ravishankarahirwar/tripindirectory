package directory.tripin.com.tripindirectory.FormActivities.FormFragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import directory.tripin.com.tripindirectory.R;

/**
 * Created by Yogesh N. Tikam on 2/5/2018.
 */

public class TruckPropertiesViewHolder extends RecyclerView.ViewHolder {
    public TextView mPropertyTitle;
    public RecyclerView mPropertiesValues;


    public TruckPropertiesViewHolder(View itemView) {
        super(itemView);
        mPropertyTitle = itemView.findViewById(R.id.property_title);
        mPropertiesValues  = itemView.findViewById(R.id.truck_properties_values);
    }
}
