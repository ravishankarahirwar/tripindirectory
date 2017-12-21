package directory.tripin.com.tripindirectory.FormActivities;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import directory.tripin.com.tripindirectory.R;

/**
 * Created by Yogesh N. Tikam on 12/19/2017.
 */

public class PlacesViewHolder extends RecyclerView.ViewHolder {
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

    public boolean isUpvoteClicked;

    public PlacesViewHolder(View itemView) {
        super(itemView);
        isAvailable = (Switch) itemView.findViewById(R.id.is_available);

        vehicleType = (Spinner) itemView.findViewById(R.id.vehicle_type);
         bodyType = (Spinner) itemView.findViewById(R.id.body_type);

        vechcleNumber = (TextInputEditText) itemView.findViewById(R.id.input_vechicle_number);
        payload = (TextInputEditText) itemView.findViewById(R.id.input_payload);
        length = (TextInputEditText) itemView.findViewById(R.id.input_length);
        driverName = (TextInputEditText) itemView.findViewById(R.id.input_driver_name);
        driverNumber = (TextInputEditText) itemView.findViewById(R.id.input_driver_number);



    }

    public void onBind(Context context, PlacesViewHolder placesViewHolder) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.body_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        placesViewHolder.vehicleType.setAdapter(adapter);
        placesViewHolder.bodyType.setAdapter(adapter);

    }
}