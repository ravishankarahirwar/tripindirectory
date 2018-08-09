package directory.tripin.com.tripindirectory.ChatingActivities.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import directory.tripin.com.tripindirectory.R;

/**
 * Created by Shubham on 2/25/2018.
 */

public class ChatItemViewHolder extends RecyclerView.ViewHolder {

    public TextView msg;
    public ImageView seenEye;
    public ImageView thumbnail;
    public TextView time;

    public ChatItemViewHolder(View itemView) {
        super(itemView);
        msg = itemView.findViewById(R.id.tv_msg);
        seenEye = itemView.findViewById(R.id.imageViewseen);
        thumbnail = itemView.findViewById(R.id.imageViewThumbTyping);
        time = itemView.findViewById(R.id.time);
    }
}
