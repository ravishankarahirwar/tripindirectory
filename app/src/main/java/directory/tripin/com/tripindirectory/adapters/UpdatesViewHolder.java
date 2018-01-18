package directory.tripin.com.tripindirectory.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import directory.tripin.com.tripindirectory.R;

/**
 * Created by Shubham on 1/10/2018.
 */

public class UpdatesViewHolder extends RecyclerView.ViewHolder {

    public ImageView icon;
    public TextView title;
    public TextView description;

    public UpdatesViewHolder(View itemView) {
        super(itemView);
        icon = itemView.findViewById(R.id.imageViewicon);
        title = itemView.findViewById(R.id.textViewtitle);
        description = itemView.findViewById(R.id.textViewDiscription);
    }
}
