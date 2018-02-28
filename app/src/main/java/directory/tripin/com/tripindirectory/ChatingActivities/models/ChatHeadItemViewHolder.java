package directory.tripin.com.tripindirectory.ChatingActivities.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nex3z.notificationbadge.NotificationBadge;

import directory.tripin.com.tripindirectory.R;

/**
 * Created by Shubham on 2/26/2018.
 */

public class ChatHeadItemViewHolder extends RecyclerView.ViewHolder {

    public TextView title, lastmsg, timeago;
    public NotificationBadge badge;
    public ImageView thumbnail;
    public ChatHeadItemViewHolder(View itemView) {
        super(itemView);

        thumbnail = itemView.findViewById(R.id.imageViewThumbTyping);
        title = itemView.findViewById(R.id.textViewTitle);
        lastmsg = itemView.findViewById(R.id.textViewLastMsg);
        badge = itemView.findViewById(R.id.badge);
        timeago = itemView.findViewById(R.id.textViewTimeAgo);

    }
}
