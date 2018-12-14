package directory.tripin.com.tripindirectory.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import directory.tripin.com.tripindirectory.R;

/**
 * Created by Shubham on 1/4/2018.
 */

public class CapsulsRecyclarAdapter extends RecyclerView.Adapter<CapsulsRecyclarAdapter.ViewHolder> {

    List<String> datalist = new ArrayList<>();

    public CapsulsRecyclarAdapter(List<String> datalist) {
        this.datalist = datalist;
    }

    public void setDataValues(List<String> datalist){
        this.datalist = datalist;
        notifyDataSetChanged();
    }

    public CapsulsRecyclarAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_capsule_recycler, parent, false);
        return new CapsulsRecyclarAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mCity.setText(datalist.get(position));

    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mCity;
        public ViewHolder(View itemView) {
            super(itemView);
            mCity = itemView.findViewById(R.id.citytxt);
        }
    }
}