package directory.tripin.com.tripindirectory.LoadBoardActivities.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import directory.tripin.com.tripindirectory.R;

/**
 * Created by Shubham on 2/22/2018.
 */

public class CommentsViewHolder extends RecyclerView.ViewHolder {

    public ImageView mThumbNail;
    public TextView mTitle;
    public TextView mComment;
    public TextView mTimeAgo;
    public LinearLayout mCommentBox;

    public CommentsViewHolder(View itemView) {
        super(itemView);
        mTitle = itemView.findViewById(R.id.tv_compname);
        mComment = itemView.findViewById(R.id.tv_comment);
        mTimeAgo = itemView.findViewById(R.id.tv_time);
        mThumbNail = itemView.findViewById(R.id.imageViewThumbTyping);
        mCommentBox = itemView.findViewById(R.id.ll_text);
    }
}
