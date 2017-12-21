package directory.tripin.com.tripindirectory.FormActivities;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

/**
 * Created by Yogesh N. Tikam on 12/21/2017.
 */

public class CheckBoxRecyclarAdapter extends RecyclerView.Adapter<CheckBoxRecyclarAdapter.ViewHolder> {

    HashMap<String,Boolean> mDataMap;

    public CheckBoxRecyclarAdapter(HashMap<String, Boolean> mDataMap) {
        this.mDataMap = mDataMap;
    }

    @Override
    public CheckBoxRecyclarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(CheckBoxRecyclarAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
