package directory.tripin.com.tripindirectory.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.helper.Logger;

/**
 * Created by Yogesh N. Tikam on 11/16/2017.
 */

public class PartnersAdapter1 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SECTION_TYPE_1 = 0;
    private static final int CONTACT_TYPE_1 = 1;
    private static final int DIRECTORY_TYPE_1 = 2;
    private Context mContext;

    public PartnersAdapter1(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SECTION_TYPE_1) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.section1, parent, false);
            return new SectionViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_partner_row1, parent, false);
            return new ItemViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof SectionViewHolder) {
            SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;

            sectionViewHolder.title.setText("Directory");

        } else {

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

            itemViewHolder.mAddress.setText("Office No 301, The summit business bay (Omkar) Next to WEH Metro Station, Andheri East, Mumbai - 400093,Aao dekhayein tumhe ande ka funda, dhim tana dhinchak dhinchak");

            itemViewHolder.mCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ArrayList<String> phoneNumbers = new ArrayList<>();
                    phoneNumbers.add("809556321");
                    phoneNumbers.add("959698745");

                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Looks like there are multiple phone numbers.")
                            .setCancelable(false)
                            .setAdapter(new ArrayAdapter<String>(mContext, R.layout.dialog_multiple_no_row, R.id.dialog_number, phoneNumbers),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int item) {

                                            Logger.v("Dialog number selected :" + phoneNumbers.get(item));
                                        }
                                    });


                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

                    builder.create();
                    builder.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return SECTION_TYPE_1;
        } else {
            return DIRECTORY_TYPE_1;
        }
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public SectionViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.section_text);
        }
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
        private ExpandableTextView mAddress;
        private TextView mCall;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.partner_card_view);
            mOption = (ImageView) itemView.findViewById(R.id.option);
            mUpvote = (ImageView) itemView.findViewById(R.id.upvote);
            mDownvote = (ImageView) itemView.findViewById(R.id.downvote);
            mRanking = (TextView) itemView.findViewById(R.id.ranking);
            mRank = 0;
            mCall = (TextView) itemView.findViewById(R.id.call);
            mAddress = (ExpandableTextView) itemView.findViewById(R.id.address);
        }
    }
}
