package directory.tripin.com.tripindirectory.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.model.response.Vehicle;

/**
 * Created by Yogesh N. Tikam on 1/2/2018.
 */

public class FleetForViewerAdapter extends RecyclerView.Adapter<FleetForViewerAdapter.ViewHolder> {

    List<Vehicle> datalist =new ArrayList<>();

    public FleetForViewerAdapter(List<Vehicle> datalist) {
        this.datalist = datalist;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fleetview, parent, false);
        return new FleetForViewerAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.payload.setText("Payload: "+datalist.get(position).getPayload());
        holder.length.setText("Length: "+datalist.get(position).getLength());
        holder.bodytype.setText("BodyType: "+datalist.get(position).getBodyType());
        holder.type.setText("VehicleType: "+datalist.get(position).getType());
        holder.plateno.setText(datalist.get(position).getNumber());
        holder.driver.setText("Driver: "+datalist.get(position).getDriver().getName()+"/"+datalist.get(position).getDriver().getNumber());

    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView plateno, type, bodytype, payload, length, driver;

        public ViewHolder(View itemView) {
            super(itemView);
            plateno = itemView.findViewById(R.id.plateno);
            type = itemView.findViewById(R.id.vehicletype);
            bodytype = itemView.findViewById(R.id.bodytype);
            length = itemView.findViewById(R.id.length);
            driver = itemView.findViewById(R.id.driverdetail);
            payload = itemView.findViewById(R.id.payload);
        }
    }
}
