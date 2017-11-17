package directory.tripin.com.tripindirectory.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import directory.tripin.com.tripindirectory.R;

/**
 * Created by Yogesh N. Tikam on 11/16/2017.
 */

public class PartnersAdapter1 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    public PartnersAdapter1(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_partner_row1, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

        /**
         *Logic for animating partner card
         */
      /*  itemViewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!itemViewHolder.isTranslated) {
                    itemViewHolder.mCardView.animate().translationX(150);
                    itemViewHolder.mOption.setVisibility(View.VISIBLE);
                    itemViewHolder.isTranslated = true;
                } else {
                    itemViewHolder.isTranslated = false;
                    itemViewHolder.mOption.setVisibility(View.GONE);
                    itemViewHolder.mCardView.animate().translationX(0);
                }
            }
        });*/

        itemViewHolder.mUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!itemViewHolder.isUpVoted || itemViewHolder.isDownVoted) {

                    itemViewHolder.mUpvote.setColorFilter(ContextCompat.getColor(mContext, R.color.arrow_white), PorterDuff.Mode.SRC_IN);
                    itemViewHolder.mUpvote.setBackgroundResource(R.drawable.circle_shape);
                    itemViewHolder.mDownvote.setColorFilter(ContextCompat.getColor(mContext, R.color.arrow_grey), android.graphics.PorterDuff.Mode.SRC_IN);
                    itemViewHolder.mDownvote.setBackgroundResource(0);

                    itemViewHolder.mRank++;
                    itemViewHolder.mRanking.setText(String.valueOf(itemViewHolder.mRank));

                    itemViewHolder.isUpVoted = true;
                    itemViewHolder.isDownVoted = false;
                }
            }
        });

        itemViewHolder.mDownvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!itemViewHolder.isDownVoted || itemViewHolder.isUpVoted) {

                    itemViewHolder.mDownvote.setColorFilter(ContextCompat.getColor(mContext, R.color.arrow_white), android.graphics.PorterDuff.Mode.SRC_IN);
                    itemViewHolder.mDownvote.setBackgroundResource(R.drawable.circle_shape);
                    itemViewHolder.mUpvote.setColorFilter(ContextCompat.getColor(mContext, R.color.arrow_grey), android.graphics.PorterDuff.Mode.SRC_IN);
                    itemViewHolder.mUpvote.setBackgroundResource(0);

                    itemViewHolder.mRank--;
                    itemViewHolder.mRanking.setText(String.valueOf(itemViewHolder.mRank));

                    itemViewHolder.isDownVoted = true;
                    itemViewHolder.isUpVoted = false;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private boolean isTranslated = false;
        private CardView mCardView;
        private ImageView mOption;
        private ImageView mUpvote;
        private ImageView mDownvote;
        private TextView mRanking;
        private boolean isUpVoted;
        private boolean isDownVoted;
        private int mRank;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.partner_card_view);
            mOption = (ImageView) itemView.findViewById(R.id.option);
            mUpvote = (ImageView) itemView.findViewById(R.id.upvote);
            mDownvote = (ImageView) itemView.findViewById(R.id.downvote);
            mRanking = (TextView) itemView.findViewById(R.id.ranking);
            mRank = 0;
        }
    }
}
