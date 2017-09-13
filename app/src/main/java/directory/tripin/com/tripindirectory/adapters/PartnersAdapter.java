package directory.tripin.com.tripindirectory.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.model.response.GetPartnersResponse;


/**
 * Created by Yogesh N. Tikam on 13/9/2017.
 */

public class PartnersAdapter extends RecyclerView.Adapter<PartnersAdapter.ViewHolder> {

    private GetPartnersResponse mPartnersList;
    private Context mContext;

    public PartnersAdapter(Context context, GetPartnersResponse partnersResponse) {
        mPartnersList = partnersResponse;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_partner_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        holder.mCompanyName .setText(mPartnersList.getData().get(position).getName());
        String mobileNo = mPartnersList.getData().get(position).getMobile();
        Pattern pattern = Pattern.compile("\\d{10}");
        Matcher matcher = pattern.matcher(mobileNo);
        if (matcher.find()) {
            mobileNo = matcher.group(0);
            holder.mContact.setText(mobileNo);
        } else {
            holder.mContact.setText(mPartnersList.getData().get(position).getMobile());
        }

        holder.mCalll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobileNo = mPartnersList.getData().get(position).getMobile();
                Pattern pattern = Pattern.compile("\\d{10}");
                Matcher matcher = pattern.matcher(mobileNo);
                if (matcher.find()) {
                    mobileNo = matcher.group(0);
                }
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+ Uri.encode(mobileNo.trim())));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(callIntent);
            }
        });
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
        private ImageView mCall;
        private ImageView mAddToCommonContact;


        public ViewHolder(View itemView) {
            super(itemView);
            mCompanyName = itemView.findViewById(R.id.company_name);
            mContact = itemView.findViewById(R.id.contact_no);
            mAddress = itemView.findViewById(R.id.address);
            mCall = itemView.findViewById(R.id.call);
            mAddToCommonContact = itemView.findViewById(R.id.add);
        }
    }
}
