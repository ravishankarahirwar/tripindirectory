package directory.tripin.com.tripindirectory.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import directory.tripin.com.tripindirectory.R;

/**
 * Created by Yogesh N. Tikam on 12/14/2017.
 */

public class PartnersViewHolder extends RecyclerView.ViewHolder {
    public CardView mCardView;
    public TextView mAddress;
    public ImageView mCall;
    public TextView mCompany;

    public ImageView mStarView;
    public TextView mNumStarsView;
    public TextView mFleetSize;
    public ImageView mShareCompany;

    public PartnersViewHolder(View itemView) {
        super(itemView);
        mCardView = itemView.findViewById(R.id.partner_card_view);
        mCall = itemView.findViewById(R.id.call);
        mAddress =  itemView.findViewById(R.id.company_address);
        mCompany = itemView.findViewById(R.id.company_name1);

        mStarView =  itemView.findViewById(R.id.star);
        mNumStarsView = itemView.findViewById(R.id.post_num_stars);
        mFleetSize =  itemView.findViewById(R.id.fleet_size);
        mShareCompany =  itemView.findViewById(R.id.share_company);
    }
}