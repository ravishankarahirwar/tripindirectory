package directory.tripin.com.tripindirectory.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.model.response.GetPartnersResponse;


/**
 * Created by Yogesh N. Tikam on 13/9/2017.
 */

public class PartnersAdapter extends RecyclerView.Adapter<PartnersAdapter.ViewHolder> {

    private GetPartnersResponse mPartnersList;

    public PartnersAdapter(GetPartnersResponse partnersResponse) {
        mPartnersList = partnersResponse;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_partner_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mCompanyName .setText(mPartnersList.getData().get(position).getName());
        holder.mContact .setText(mPartnersList.getData().get(position).getMobile());
        holder.mAddress .setText(mPartnersList.getData().get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return mPartnersList.getData().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mCompanyName;
        private TextView mContact;
        private TextView mAddress;

        public ViewHolder(View itemView) {
            super(itemView);
            mCompanyName = itemView.findViewById(R.id.company_name);
            mContact = itemView.findViewById(R.id.contact_no);
            mAddress = itemView.findViewById(R.id.address);
        }
    }
}
