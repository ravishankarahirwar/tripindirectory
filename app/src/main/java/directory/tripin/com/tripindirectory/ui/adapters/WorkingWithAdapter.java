package directory.tripin.com.tripindirectory.ui.adapters;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.List;

import directory.tripin.com.tripindirectory.FormActivities.WorkingWithHolderNew;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.callback.OnTruckValueChange;
import directory.tripin.com.tripindirectory.model.search.Fleet;
import directory.tripin.com.tripindirectory.model.search.TruckProperty;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 19-03-2018
 */

public class WorkingWithAdapter extends RecyclerView.Adapter<WorkingWithHolderNew> {
    private Fleet mDataValues;
    private Context mContext;

    private int getDataValuesSize() {
        return mDataValues.getTrucks().size();
    }

    // data is passed into the constructor
    public WorkingWithAdapter(Context context, Fleet fleet) {
        mContext = context;
        this.mDataValues = fleet;
    }

    public void setDataValues(Fleet dataValues) {
        mDataValues = dataValues;
        this.notifyDataSetChanged();
    }

    public Fleet getDataValues() {
        return this.mDataValues;
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
        String truckType = mDataValues.getTrucks().get(position).getTruckType();
//            boolean value = mDataValues.get(key);
        PropertiesAdaptor propertiesAdaptor = new PropertiesAdaptor(mContext, mDataValues.getTrucks().get(position).getTruckProperties(), new OnTruckValueChange() {
            @Override
            public void onTruckPropertiesChange(List<TruckProperty> truckProperties) {
                mDataValues.getTrucks().get(position).setTruckProperties(truckProperties);
            }
        });
        holder.propertyList.setLayoutManager(new LinearLayoutManager(this.mContext));
        holder.propertyList.setAdapter(propertiesAdaptor);
        holder.propertyList.addItemDecoration(new DividerItemDecoration(mContext,
                DividerItemDecoration.VERTICAL));
        holder.mIHave.setText(truckType);
//            holder.setDataValue(key);
        holder.mIHave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDataValues.getTrucks().get(position).setTruckHave(isChecked);
                if (isChecked) {
                    holder.propertyList.setVisibility(View.VISIBLE);
                } else {
                    holder.propertyList.setVisibility(View.GONE);
                }
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mDataValues.getTrucks().size();
    }
}

