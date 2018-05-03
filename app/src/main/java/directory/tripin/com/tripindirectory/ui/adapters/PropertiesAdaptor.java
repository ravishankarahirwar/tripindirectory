package directory.tripin.com.tripindirectory.ui.adapters;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

import directory.tripin.com.tripindirectory.formactivities.FormFragments.TruckPropertiesViewHolder;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.callback.OnTruckPropertyValueChange;
import directory.tripin.com.tripindirectory.callback.OnTruckValueChange;
import directory.tripin.com.tripindirectory.model.search.TruckProperty;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 19-03-2018
 */

///************************************PRoperties Adaptor
public class PropertiesAdaptor extends RecyclerView.Adapter<TruckPropertiesViewHolder> {
    private List<TruckProperty> truckProperties;
    private Context mContext;
    private OnTruckValueChange onTruckValueChange;

    private int getDataValuesSize() {
        return truckProperties.size();
    }

    // data is passed into the constructor
    public PropertiesAdaptor(Context context, List<TruckProperty> truckProperties, OnTruckValueChange onTruckValueChange) {
        this.truckProperties = truckProperties;
        this.onTruckValueChange = onTruckValueChange;
        mContext = context;
    }

    private void setDataValues(List<TruckProperty> truckProperties) {
        this.truckProperties = truckProperties;
        this.notifyDataSetChanged();
    }

    // inflates the row layout from xml when needed
    @Override
    public TruckPropertiesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_truck_property, parent, false);
        TruckPropertiesViewHolder viewHolder = new TruckPropertiesViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TruckPropertiesViewHolder holder, final int position) {
        String key = truckProperties.get(position).getTitle();
        holder.mPropertyTitle.setText(key);

        PropertiesValuesAdaptor propertiesValueAdaptor = new PropertiesValuesAdaptor(mContext, truckProperties.get(position).getProperties(), new OnTruckPropertyValueChange() {
            @Override
            public void onPropertyChange(Map<String, Boolean> properties) {
                truckProperties.get(position).setProperties(properties);
                onTruckValueChange.onTruckPropertiesChange(truckProperties);
            }
        });
        holder.mPropertiesValues.setLayoutManager(new GridLayoutManager(this.mContext, 3));
        holder.mPropertiesValues.setAdapter(propertiesValueAdaptor);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return truckProperties.size();

    }
}