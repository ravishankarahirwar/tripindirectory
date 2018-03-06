package directory.tripin.com.tripindirectory.FormActivities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.model.response.Vehicle;

/**
 * Created by Yogesh N. Tikam on 2/5/2018.
 */

public class WorkingWithHolderNew extends RecyclerView.ViewHolder {
    public CheckBox mIHave;
    private String dataValue;
    public RecyclerView propertyList;

    public WorkingWithHolderNew(View itemView) {
        super(itemView);
        mIHave = itemView.findViewById(R.id.i_have);
        propertyList = itemView.findViewById(R.id.truck_properties_list);

    }
    public void onBind(Context context, WorkingWithHolderNew placesViewHolder) {
        placesViewHolder.mIHave.setText(placesViewHolder.getDataValue());
    }
    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }
    public String getDataValue() {
        return dataValue;
    }
}
