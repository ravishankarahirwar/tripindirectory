package directory.tripin.com.tripindirectory.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
    public TextView mAddress;
    public TextView mCall;
    public TextView mCompany;

    public boolean isUpvoteClicked;

    public PartnersViewHolder(View itemView) {
        super(itemView);
        mCardView = itemView.findViewById(R.id.partner_card_view);

        mCall = itemView.findViewById(R.id.call);
        mAddress =  itemView.findViewById(R.id.company_address);
        mCompany = itemView.findViewById(R.id.company_name);
    }
}