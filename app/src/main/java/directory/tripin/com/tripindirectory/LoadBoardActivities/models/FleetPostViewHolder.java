package directory.tripin.com.tripindirectory.LoadBoardActivities.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nex3z.notificationbadge.NotificationBadge;

import directory.tripin.com.tripindirectory.R;

/**
 * Created by Shubham on 2/18/2018.
 */

public class FleetPostViewHolder extends RecyclerView.ViewHolder {

    public ImageView mPublisherThumbnail;
    public TextView mPostTitle;
    public TextView mPostSubTitle;
    public ImageView mOptions;
    public TextView mScheduledDate;
    public TextView mSource;
    public TextView mDestination;
    public TextView mLoadProperties;
    public TextView mFleetProperties;
    public TextView mDistance;
    public TextView mPersonalNote;

    public NotificationBadge badgeLike, badgeShare, badgeComment, badgeInbox, badgeCall;
    public ImageView like,share,comment,call,inbox;


    public FleetPostViewHolder(View itemView) {
        super(itemView);

        mDistance = itemView.findViewById(R.id.textViewDistance);
        mFleetProperties = itemView.findViewById(R.id.textViewRequiredFleet);
        mLoadProperties = itemView.findViewById(R.id.textViewLoadProperties);
        mScheduledDate = itemView.findViewById(R.id.textViewDate);
        mPostTitle = itemView.findViewById(R.id.poster_title);
        mPostSubTitle = itemView.findViewById(R.id.textViewPostingTime);
        mSource = itemView.findViewById(R.id.textViewSourceCity);
        mDestination = itemView.findViewById(R.id.textViewDestinationCity);
        mPersonalNote = itemView.findViewById(R.id.textViewNote);

        badgeCall = itemView.findViewById(R.id.badge_Call);
        badgeInbox = itemView.findViewById(R.id.badge_inbox);
        badgeComment = itemView.findViewById(R.id.badge_Comment);
        badgeShare = itemView.findViewById(R.id.badge_Share);
        badgeLike = itemView.findViewById(R.id.badge_like);

        like = itemView.findViewById(R.id.imageViewLike);
        share = itemView.findViewById(R.id.imageViewShare);
        comment = itemView.findViewById(R.id.imageViewComment);
        inbox = itemView.findViewById(R.id.imageViewInbox);
        call = itemView.findViewById(R.id.imageViewCall);



    }
}
