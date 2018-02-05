package directory.tripin.com.tripindirectory.FormActivities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.TextView;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.model.response.Vehicle;

/**
 * Created by Yogesh N. Tikam on 2/5/2018.
 */

public class FleetViewHolderNew extends RecyclerView.ViewHolder {
    public TextView mTruckTyoe;
    public SwitchCompat mIhave;
    private Vehicle dataValue;


    public FleetViewHolderNew(View itemView) {
        super(itemView);
        mTruckTyoe = itemView.findViewById(R.id.truck_type);
        mIhave = itemView.findViewById(R.id.i_have);
    }
    public void onBind(Context context, FleetViewHolderNew placesViewHolder) {
        mTruckTyoe.setText(placesViewHolder.getDataValue().getType());
    }
    public void setDataValue(Vehicle dataValue) {
        this.dataValue = dataValue;
    }
    public Vehicle getDataValue() {
        return dataValue;
    }


}
