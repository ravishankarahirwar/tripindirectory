package directory.tripin.com.tripindirectory.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import directory.tripin.com.tripindirectory.R;

/**
 * Created by Yogesh N. Tikam on 12/14/2017.
 */

public class PartnersViewHolder extends RecyclerView.ViewHolder {
    public CardView mCardView;
    public ImageView mUpvote;
    public ImageView mDownvote;
    public TextView mRanking;
    public boolean isUpVoted;
    public boolean isDownVoted;
    public int mRank;
    public ExpandableTextView mAddress;
    public TextView mCall;
    public TextView mCompany;

    public boolean isUpvoteClicked;

    public PartnersViewHolder(View itemView) {
        super(itemView);
        mCardView = (CardView) itemView.findViewById(R.id.partner_card_view);
        mUpvote = (ImageView) itemView.findViewById(R.id.upvote);
        mDownvote = (ImageView) itemView.findViewById(R.id.downvote);
        mRanking = (TextView) itemView.findViewById(R.id.ranking);
        mRank = 0;
        mCall = (TextView) itemView.findViewById(R.id.call);
        mAddress = (ExpandableTextView) itemView.findViewById(R.id.address);
        mCompany = (TextView) itemView.findViewById(R.id.company_name);
    }
}