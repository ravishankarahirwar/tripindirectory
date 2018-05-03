package directory.tripin.com.tripindirectory.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.Map;

import directory.tripin.com.tripindirectory.formactivities.TruckPropertiesValueViewHolder;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.callback.OnTruckPropertyValueChange;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 19-03-2018
 */

public class PropertiesValuesAdaptor extends RecyclerView.Adapter<TruckPropertiesValueViewHolder> {
    private Map<String, Boolean> properties;
    private OnTruckPropertyValueChange onTruckPropertyValueChange;

    private int getDataValuesSize() {
        return properties.size();
    }

    // data is passed into the constructor
    public PropertiesValuesAdaptor(Context context, Map<String, Boolean> properties, OnTruckPropertyValueChange onTruckPropertyValueChange) {
        this.properties = properties;
        this.onTruckPropertyValueChange = onTruckPropertyValueChange;
    }

    private void setDataValues(Map<String, Boolean> properties) {
        this.properties = properties;
        this.notifyDataSetChanged();
    }

    // inflates the row layout from xml when needed
    @Override
    public TruckPropertiesValueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_truck_property_value, parent, false);
        TruckPropertiesValueViewHolder viewHolder = new TruckPropertiesValueViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TruckPropertiesValueViewHolder holder, final int position) {
        final String key = new ArrayList<>(properties.keySet()).get(position);
        holder.mPropertyOnOff.setText(key);
        holder.mPropertyOnOff.setChecked(false);
        holder.mPropertyOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean value) {
                properties.put(key.trim(), value);
                onTruckPropertyValueChange.onPropertyChange(properties);
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return this.properties.size();

    }
}