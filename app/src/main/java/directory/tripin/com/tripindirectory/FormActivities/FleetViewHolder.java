package directory.tripin.com.tripindirectory.FormActivities;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.model.Driver;
import directory.tripin.com.tripindirectory.model.response.Vehicle;

/**
 * Created by Yogesh N. Tikam on 12/19/2017.
 */

public class FleetViewHolder extends RecyclerView.ViewHolder {
    public ImageView vehicleRemove;
    public Switch isAvailable;
    public ImageView mRemove;
    public TextView mCity;
    public Spinner vehicleType;
    public Spinner bodyType;
    public TextInputEditText vechcleNumber;
    public TextInputEditText payload;
    public TextInputEditText length;
    public TextInputEditText driverName;
    public TextInputEditText driverNumber;
    public Vehicle dataValue;

    public boolean isUpvoteClicked;

    public FleetViewHolder(View itemView) {
        super(itemView);
        isAvailable = (Switch) itemView.findViewById(R.id.is_available);

        vehicleType = (Spinner) itemView.findViewById(R.id.vehicle_type);
        bodyType = (Spinner) itemView.findViewById(R.id.body_type);

        vehicleRemove = (ImageView) itemView.findViewById(R.id.vehicle_remove);
        vechcleNumber = (TextInputEditText) itemView.findViewById(R.id.input_vechicle_number);
        payload = (TextInputEditText) itemView.findViewById(R.id.input_payload);
        length = (TextInputEditText) itemView.findViewById(R.id.input_length);
        driverName = (TextInputEditText) itemView.findViewById(R.id.input_driver_name);
        driverNumber = (TextInputEditText) itemView.findViewById(R.id.input_driver_number);
    }

    public void onBind(Context context, FleetViewHolder placesViewHolder) {
        ArrayAdapter<CharSequence> truckType = ArrayAdapter.createFromResource(context,
                R.array.truck_type, R.layout.spinner_item);

        List<String> truckTypeList = Arrays.asList(context.getResources().getStringArray(R.array.truck_type));
        List<String> bodyTypeList = Arrays.asList(context.getResources().getStringArray(R.array.body_type));

        ArrayAdapter<CharSequence> bodyTypeAdapter = ArrayAdapter.createFromResource(context,
                R.array.body_type, android.R.layout.simple_spinner_item);

        vehicleType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ((TextView) parentView.getChildAt(0)).setTextColor(Color.BLACK);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        bodyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ((TextView) parentView.getChildAt(0)).setTextColor(Color.BLACK);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        truckType.setDropDownViewResource(R.layout.spinner_item);
        bodyTypeAdapter.setDropDownViewResource(R.layout.spinner_item);

        placesViewHolder.vehicleType.setAdapter(truckType);
        placesViewHolder.bodyType.setAdapter(bodyTypeAdapter);

        String vehicleNumber = null;
        String vehiclePayload = null;
        String vehicleLength = null;
        String vehicleDriverName = null;
        String vehicleDriverNumber = null;

        if(placesViewHolder.getDataValue().getNumber() != null) {
            vehicleNumber = placesViewHolder.getDataValue().getNumber();
        }
        if(placesViewHolder.getDataValue().getPayload() != null) {
            vehiclePayload = placesViewHolder.getDataValue().getPayload();
        }

        if(placesViewHolder.getDataValue().getLength() != null) {
            vehicleLength = placesViewHolder.getDataValue().getLength();
        }

        Driver driver = placesViewHolder.getDataValue().getDriver();
        if(driver != null && driver.getName() != null) {
            vehicleDriverName = driver.getName();
        }

        if(driver != null && driver.getNumber() != null) {
            vehicleDriverNumber = driver.getNumber();
        }

        vehicleType.setSelection(truckTypeList.indexOf(placesViewHolder.getDataValue().getType().trim()));
        bodyType.setSelection(bodyTypeList.indexOf(placesViewHolder.getDataValue().getBodyType().trim()));

        isAvailable.setChecked(placesViewHolder.getDataValue().isAvailable());
        vechcleNumber.setText(vehicleNumber != null ? vehicleNumber : "");
        payload.setText(vehiclePayload != null ? vehiclePayload : "");
        length.setText(vehicleLength != null ? vehicleLength : "");
        driverName.setText(vehicleDriverName != null ? vehicleDriverName : "");
        driverNumber.setText(vehicleDriverNumber != null ? vehicleDriverNumber : "");
    }

    public Vehicle getDataValue() {
        return dataValue;
    }

    public void setDataValue(Vehicle dataValue) {
        this.dataValue = dataValue;
    }
}