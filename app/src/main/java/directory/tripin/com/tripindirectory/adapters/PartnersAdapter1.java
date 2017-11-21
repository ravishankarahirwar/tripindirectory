package directory.tripin.com.tripindirectory.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
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
import android.widget.Toast;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.model.response.ElasticSearchResponse;

/**
 * Created by Yogesh N. Tikam on 11/16/2017.
 */

public class PartnersAdapter1 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SECTION_TYPE_1 = 0;
    private static final int CONTACT_TYPE_1 = 1;
    private static final int DIRECTORY_TYPE_1 = 2;
    private Context mContext;
    private ElasticSearchResponse mElasticSearchResponse;

    public PartnersAdapter1(Context context, ElasticSearchResponse elasticSearchResponse) {
        mContext = context;
        mElasticSearchResponse = elasticSearchResponse;
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

            itemViewHolder.mCompany.setText(mElasticSearchResponse.getData().get(position-1).getName());
            itemViewHolder.mAddress.setText(mElasticSearchResponse.getData().get(position-1).getAddress());

            String strLike = mElasticSearchResponse.getData().get(position-1).getLike();
            String strdisLike = mElasticSearchResponse.getData().get(position-1).getDislike();

            int like = Integer.parseInt(strLike);
            int disLike = Integer.parseInt(strdisLike);

            itemViewHolder.mRanking.setText(String.valueOf(like - disLike));


            final ElasticSearchResponse.PartnerData.Mobile[] mMobileData = mElasticSearchResponse.getData().get(position-1).getMobile();

            itemViewHolder.mCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ArrayList<String> phoneNumbers = new ArrayList<>();

                    if (mMobileData.length > 1) {

                        for (int i = 0; i < mMobileData.length; i++) {
                            phoneNumbers.add(mMobileData[i].getCellNo());
                        }

                        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Looks like there are multiple phone numbers.")
                                .setCancelable(false)
                                .setAdapter(new ArrayAdapter<String>(mContext, R.layout.dialog_multiple_no_row, R.id.dialog_number, phoneNumbers),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int item) {

                                                Logger.v("Dialog number selected :" + phoneNumbers.get(item));

                                                callNumber(phoneNumbers.get(item));
                                            }
                                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });

                        builder.create();
                        builder.show();
                    } else if(mMobileData.length == 1)  {

                       callNumber(mMobileData[0].getCellNo());
                    } else {
                        Toast.makeText(mContext,"Mobile number not present for this contact",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mElasticSearchResponse.getData().size()+1;
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
        private CardView mCardView;
        private ImageView mUpvote;
        private ImageView mDownvote;
        private TextView mRanking;
        private boolean isUpVoted;
        private boolean isDownVoted;
        private int mRank;
        private ExpandableTextView mAddress;
        private TextView mCall;
        private TextView mCompany;

        public ItemViewHolder(View itemView) {
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

    private void callNumber(String number) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + Uri.encode(number.trim())));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(callIntent);
    }
}
