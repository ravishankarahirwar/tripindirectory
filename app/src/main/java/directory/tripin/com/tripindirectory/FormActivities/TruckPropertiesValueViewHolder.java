package directory.tripin.com.tripindirectory.FormActivities;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import directory.tripin.com.tripindirectory.R;

/**
 * Created by Yogesh N. Tikam on 2/5/2018.
 */

public class TruckPropertiesValueViewHolder extends RecyclerView.ViewHolder {
    public CheckBox mPropertyOnOff;


    public TruckPropertiesValueViewHolder(View itemView) {
        super(itemView);
        mPropertyOnOff  = itemView.findViewById(R.id.property_on_off);
    }
}
