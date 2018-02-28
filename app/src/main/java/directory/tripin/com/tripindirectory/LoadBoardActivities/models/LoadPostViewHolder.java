package directory.tripin.com.tripindirectory.LoadBoardActivities.models;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nex3z.notificationbadge.NotificationBadge;

import directory.tripin.com.tripindirectory.R;

/**
 * Created by Shubham on 2/18/2018.
 */

public class LoadPostViewHolder extends RecyclerView.ViewHolder {

    public ImageView mPublisherThumbnail;
    public TextView mPostTitle;
    public TextView mPostSubTitle;
    public ImageButton mOptions;
    public TextView mScheduledDate;
    public TextView mSource;
    public TextView mDestination;
    public TextView mLoadProperties;
    public TextView mFleetProperties;
    public TextView mDistance;
    public TextView mPersonalNote;
    public LinearLayout mActions;

    public ConstraintLayout mDetailsFull, mDetailsHidden;
    public TextView mExpand;

    public NotificationBadge badgeLike, badgeQuote, badgeShare, badgeComment, badgeInbox, badgeCall;
    public ImageView like,quote,share,comment,call,inbox;


    public LoadPostViewHolder(View itemView) {
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
        mOptions = itemView.findViewById(R.id.imageButtonMore);

        badgeCall = itemView.findViewById(R.id.badge_Call);
        badgeInbox = itemView.findViewById(R.id.badge_inbox);
        badgeComment = itemView.findViewById(R.id.badge_Comment);
        badgeShare = itemView.findViewById(R.id.badge_Share);
        badgeLike = itemView.findViewById(R.id.badge_like);
        badgeQuote = itemView.findViewById(R.id.badge_Quotes);

        like = itemView.findViewById(R.id.imageViewLike);
        share = itemView.findViewById(R.id.imageViewShare);
        comment = itemView.findViewById(R.id.imageViewComment);
        inbox = itemView.findViewById(R.id.imageViewInbox);
        call = itemView.findViewById(R.id.imageViewCall);
        quote = itemView.findViewById(R.id.imageViewQuote);

        mDetailsFull = itemView.findViewById(R.id.cl_full);
        mDetailsHidden = itemView.findViewById(R.id.cl_hidden);
        mExpand = itemView.findViewById(R.id.textViewExpand);
        mActions = itemView.findViewById(R.id.ll_actions);

    }

    public void expand(){
        mExpand.setVisibility(View.GONE);
        mDetailsHidden.setVisibility(View.VISIBLE);
        mActions.setVisibility(View.VISIBLE);
    }
}
