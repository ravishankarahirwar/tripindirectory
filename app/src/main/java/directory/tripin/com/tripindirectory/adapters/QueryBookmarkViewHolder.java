package directory.tripin.com.tripindirectory.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import directory.tripin.com.tripindirectory.R;

/**
 * Created by Shubham on 2/1/2018.
 */

public class QueryBookmarkViewHolder extends RecyclerView.ViewHolder {

    public TextView searchquery,filters,sorting, searchnow, remove, title;

    public QueryBookmarkViewHolder(View itemView) {
        super(itemView);
        searchnow = itemView.findViewById(R.id.searchnow);
        remove = itemView.findViewById(R.id.remove);
        title = itemView.findViewById(R.id.bookmark_title);
        sorting = itemView.findViewById(R.id.textViewSort);
        filters = itemView.findViewById(R.id.textViewFilters);
        searchquery = itemView.findViewById(R.id.textViewSearch);
    }
}
