package directory.tripin.com.tripindirectory.FormActivities;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.HashMap;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.helper.Logger;

/**
 * Created by Yogesh N. Tikam on 12/21/2017.
 */

public class CheckBoxRecyclarAdapter extends RecyclerView.Adapter<CheckBoxRecyclarAdapter.ViewHolder> {

    private HashMap<String, Boolean> mDataMap;

    public HashMap<String, Boolean> getmDataMap() {
        return mDataMap;
    }

    public void setmDataMap(HashMap<String, Boolean> mDataMap) {
        this.mDataMap = mDataMap;
    }

    public CheckBoxRecyclarAdapter(HashMap<String, Boolean> mDataMap) {
        this.mDataMap = mDataMap;
    }

    @Override
    public CheckBoxRecyclarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checkbox, parent, false);
        return new CheckBoxRecyclarAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CheckBoxRecyclarAdapter.ViewHolder holder, int position) {
        final String name = new ArrayList<>(mDataMap.keySet()).get(position);
        holder.checkBox.setText(name);
        if (mDataMap.get(name)) {
            holder.checkBox.setChecked(true);
        }else {
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDataMap.put(name,holder.checkBox.isChecked());
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataMap.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
