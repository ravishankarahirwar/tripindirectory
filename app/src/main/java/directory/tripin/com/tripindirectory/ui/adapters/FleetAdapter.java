package directory.tripin.com.tripindirectory.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import directory.tripin.com.tripindirectory.FormActivities.FleetViewHolder;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.model.response.Vehicle;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 19-03-2018
 */

public class FleetAdapter extends RecyclerView.Adapter<FleetViewHolder> {
    private List<Vehicle> mDataValues;
    private List<Vehicle> mVehicles;
    private Context mContext;

    private int getDataValuesSize() {
        return mDataValues.size();
    }

    // data is passed into the constructor
    public FleetAdapter(Context context, List<Vehicle> data,  List<Vehicle> vehicles, int type) {
        mContext = context;
        this.mDataValues = data;
        mVehicles = vehicles;
    }

    public void setDataValues(List<Vehicle> dataValues) {
        mDataValues = dataValues;
        this.notifyDataSetChanged();
    }

    // inflates the row layout from xml when needed
    @Override
    public FleetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fleet, parent, false);
        FleetViewHolder viewHolder = new FleetViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final FleetViewHolder holder, final int position) {
        holder.setDataValue(mDataValues.get(position));
        holder.onBind(mContext, holder);


        holder.vehicleRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mVehicles.size() > 0) {
                    mVehicles.remove(position);
                    setDataValues(mVehicles);
                }
            }
        });

        holder.vehicleShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position >= 0) {
                    Vehicle dataValue = mDataValues.get(position);
                    shareTruck(dataValue);
                } else {

                }

            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mDataValues.size();

    }

    private void shareTruck(Vehicle dataValue) {
        try {
            if(dataValue != null) {
                String vehicleNumber = dataValue.getNumber();
                String vehiclePayload = dataValue.getPayload();
                String vehicleLength = dataValue.getLength();
                String vehicleDriverName = dataValue.getDriver().getName();
                String vehicleDriverNumber = dataValue.getDriver().getNumber();
                String vehicleTypeString  = dataValue.getType();
                String bodyTypeString = dataValue.getBodyType();

                String truckSharingInfo = "*Vehicle Information:* \n"
                        + "Vehicle Number: " + vehicleNumber +"\n"
                        + "Vehicle Type: " + vehicleTypeString +"\n"
                        + "Vehicle Body: " + bodyTypeString +"\n"
                        + "Vehicle PayLoad: " + vehiclePayload +"\n"
                        + "Vehicle Length: " + vehicleLength +"\n"
                        + "*Driver Information:*\n"
                        + "Driver Name: " + vehicleDriverName +"\n"
                        + "Driver Number: " + vehicleDriverNumber +"\n";

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Indian Logistic Network");
                i.putExtra(Intent.EXTRA_TEXT, truckSharingInfo);
                mContext.startActivity(Intent.createChooser(i, "Share by INL"));
            } else {
                Toast.makeText(mContext, "There is no data available for this vehicle", Toast.LENGTH_SHORT);
            }
        } catch(Exception e) {
            //e.toString();
        }
    }
}
